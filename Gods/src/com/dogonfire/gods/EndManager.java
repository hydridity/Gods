package com.dogonfire.gods;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitScheduler;

public class EndManager
{
	private final String directory = "plugins" + File.separator + "Gods";
	File f_config = new File(this.directory + File.separator + "end_config.yml");
	File f_endChunks = new File(this.directory + File.separator + "endChunks.yml");
	EndWorldConfig mainEndConfig;
	private World mainEndWorld;
	private EndChunks endChunks;
	private HashMap<UUID, HashMap<String, Double>> data;
	private HashMap<UUID, Integer> edHealth;
	private Gods plugin;

	EndManager(Gods plugin)
	{
		this.plugin = plugin;
	}

	public void onDisable()
	{
		if (this.mainEndWorld == null)
		{
			this.plugin.log("No End world found ! Nothing will be down.");
		}
		else
		{
			if (this.mainEndConfig.regenOnStop())
			{
				hardRegen(this.mainEndWorld);
			}
			else
			{
				this.plugin.log("Regen on Stop disabled, nothing to do.");
			}
			this.endChunks.save(this.f_endChunks);
		}
		this.plugin.log("TheEndAgain successfully disabled.");
	}

	public void init()
	{
		this.data = new HashMap();
		this.edHealth = new HashMap();
		this.mainEndConfig = new EndWorldConfig(this.plugin);

		checkConfig();

		loadConfig(this.mainEndWorld);

		this.mainEndConfig.setRespawnTimerTask(-42);
		for (World w : this.plugin.getServer().getWorlds())
		{
			if (w.getEnvironment().equals(World.Environment.THE_END))
			{
				this.mainEndWorld = w;
				break;
			}
		}
		if (this.mainEndWorld == null)
		{
			this.plugin.log("No End world found ! Nothing will be done.");
		}
		else
		{
			this.endChunks = new EndChunks(this.plugin, this.mainEndWorld);

			Bukkit.getScheduler().scheduleAsyncDelayedTask(this.plugin, new Runnable()
			{
				public void run()
				{
					synchronized (EndManager.this.endChunks)
					{
						EndManager.this.endChunks.load(EndManager.this.f_endChunks);
					}
				}
			});
			this.mainEndWorld.setKeepSpawnInMemory(false);
			if (this.mainEndConfig.getRespawnTimer() == 0)
			{
				spawnEnderDragonsToActualNumber(this.mainEndWorld);
			}
			else
			{
				launchRespawnTask(this.mainEndWorld);
			}
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable()
			{
				public void run()
				{
					EndManager.this.updateNbAliveED(EndManager.this.mainEndWorld);
					if (EndManager.this.mainEndConfig.getNbEd() > EndManager.this.mainEndConfig.getActualNbMaxEnderDragon())
					{
						EndManager.this.removeEnderDragons(EndManager.this.mainEndWorld, EndManager.this.mainEndConfig.getNbEd() - EndManager.this.mainEndConfig.getNbMaxEnderDragon());
					}
				}
			}, 100L, 100L);
			for (Entity e : this.mainEndWorld.getEntities())
			{
				if ((e instanceof EnderDragon))
				{
					EnderDragon ed = (EnderDragon) e;
					ed.setHealth(200.0D);
					this.edHealth.put(ed.getUniqueId(), Integer.valueOf(this.mainEndConfig.getEnderDragonHealth()));
				}
			}
		}
		this.plugin.log("EndManager successfully enabled.");
	}

	public void hardRegen(World w)
	{
		EndWorldConfig config = this.mainEndConfig;

		this.plugin.log("Hard Regen - Taking out the players...");
		for (Player p : this.mainEndWorld.getPlayers())
		{
			if (config.getActionOnRegen() == 0)
			{
				p.kickPlayer(ChatColor.GREEN + toColor(config.getRegenMessage()));
			}
			else
			{
				p.sendMessage(ChatColor.GREEN + toColor(config.getRegenMessage()));
				//p.teleport(((World) this.plugin.getServer().getWorlds().get(0)).getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
			}
		}
		this.plugin.log("Hard Regen - Removing entities...");
		for (Entity e : this.mainEndWorld.getEntities())
		{
			e.remove();
		}
		this.plugin.log("Hard Regen - Regenerating...");
		for (ExtendedChunk chunk : this.endChunks.getIterableChunks())
		{
			Chunk c = this.mainEndWorld.getChunkAt(chunk.getX(), chunk.getZ());
			this.mainEndWorld.regenerateChunk(c.getX(), c.getZ());
		}
		this.plugin.log("Hard Regen - Saving...");

		this.mainEndWorld.save();
		this.plugin.log("Hard Regen - Done !");
	}

	public void softRegen(World w)
	{
		EndWorldConfig config = this.mainEndConfig;

		config.setNbEd(0);

		this.plugin.log("Soft Regen - Removing players...");
		for (Player p : this.mainEndWorld.getPlayers())
		{
			if (config.getActionOnRegen() == 0)
			{
				p.kickPlayer(ChatColor.GREEN + toColor(config.getRegenMessage()));
			}
			else
			{
				//p.teleport(((World) this.plugin.getServer().getWorlds().get(0)).getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
				p.sendMessage(ChatColor.GREEN + toColor(config.getRegenMessage()));
			}
		}
		this.plugin.log("Soft Regen - Removing entities...");
		for (Entity e : this.mainEndWorld.getEntities())
		{
			e.remove();
		}
		this.plugin.log("Soft Regen - Unloading chunks...");
		for (Chunk c : this.mainEndWorld.getLoadedChunks())
		{
			c.unload();
		}
		this.plugin.log("Soft Regen - Flag chunks as to-be-regen-on-reload...");

		this.endChunks.regen(this.mainEndWorld.getName());

		this.plugin.log("Soft Regen - Chunks flagged. Waiting...");

		Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
		{
			public void run()
			{
				EndManager.this.plugin.log("Soft Regen - Respawning EDs...");
				EndManager.this.spawnEnderDragonsToActualNumber(EndManager.this.mainEndWorld);
				EndManager.this.plugin.log("Soft Regen - EDs respawned !");
			}
		}, 60L);
	}

	public int spawnEnderDragonsToActualNumber(World w)
	{
		EndWorldConfig config = this.mainEndConfig;
		for (int x = -3; x <= 3; x++)
		{
			for (int z = -3; z <= 3; z++)
			{
				this.mainEndWorld.loadChunk(x, z);
			}
		}
		int dragonNumber = 0;
		int spawned = 0;
		if (this.mainEndWorld != null)
		{
			updateNbAliveED(w);
			dragonNumber = config.getNbEd();
			Random rand = new Random();
			while (dragonNumber < config.getActualNbMaxEnderDragon())
			{
				final Location loc = new Location(this.mainEndWorld, rand.nextInt(20) - 10, rand.nextInt(20) + 70, rand.nextInt(20) - 10);
				loc.getChunk().load();
				if (this.mainEndWorld.spawnEntity(loc, EntityType.ENDER_DRAGON) == null)
				{
					Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
					{
						public void run()
						{
							EndManager.this.mainEndWorld.spawnEntity(loc, EntityType.ENDER_DRAGON);
						}
					});
				}
				dragonNumber++;

				spawned++;
			}
		}
		if (spawned > 0)
		{
			broadcastSpawned(this.mainEndWorld);
		}
		return spawned;
	}

	protected void removeEnderDragons(World w, int quantity)
	{
		World endWorld = this.mainEndWorld;

		SortedMap dragons = new TreeMap();
		for (Entity e : endWorld.getEntities())
		{
			if ((e.getType() == EntityType.ENDER_DRAGON) && (((EnderDragon) e).getHealth() > 0.0D))
			{
				dragons.put(Double.valueOf(e.getLocation().lengthSquared()), (EnderDragon) e);
			}
		}
		int deletedEDs = 0;
		while ((deletedEDs < quantity) && (dragons.size() > 0))
		{
			EnderDragon e = (EnderDragon) dragons.get(dragons.lastKey());
			this.data.remove(e.getUniqueId());
			e.remove();
			dragons.remove(dragons.lastKey());
			deletedEDs++;
		}
	}

	public void launchRespawnTask(final World w)
	{
		final EndWorldConfig config = this.mainEndConfig;
		if (config.getRespawnTimer() != 0)
		{
			if (config.getRespawnTimerTask() == -42)
			{
				config.setRespawnTimerTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable()
				{
					public void run()
					{
						config.newActualNumber();
						if (config.regenOnRespawn())
						{
							EndManager.this.softRegen(w);
						}
						else
						{
							EndManager.this.spawnEnderDragonsToActualNumber(w);
						}
					}
				}, 200L, config.getRespawnTimer() * 60 * 20));
			}
			else
			{
				this.plugin.getServer().getScheduler().cancelTask(config.getRespawnTimerTask());
				config.setRespawnTimerTask(-42);
				launchRespawnTask(w);
			}
		}
	}

	public void checkConfig()
	{
		new File(this.directory).mkdir();
		YamlConfiguration yamlConfig = new YamlConfiguration();
		if (!this.f_config.exists())
		{
			newConfig(this.mainEndWorld);
		}
		else
		{
			try
			{
				yamlConfig.load(this.f_config);
			}
			catch (FileNotFoundException e1)
			{
				e1.printStackTrace();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			catch (InvalidConfigurationException e1)
			{
				e1.printStackTrace();
			}
			if ((!yamlConfig.isSet("pluginVersion")) || (!yamlConfig.getString("pluginVersion").equals(this.plugin.getDescription().getVersion())))
			{
				newConfig(this.mainEndWorld);
			}
		}
	}

	public void loadConfig(World w)
	{
		EndWorldConfig config = this.mainEndConfig;

		YamlConfiguration yamlConfig = new YamlConfiguration();
		try
		{
			yamlConfig.load(this.f_config);
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		catch (InvalidConfigurationException e1)
		{
			e1.printStackTrace();
		}
		yamlConfig.getBoolean("useTEAPrefix", true);

		config.setRegenOnStop(yamlConfig.getBoolean("regenOnStop", true));
		config.setRegenOnRespawn(yamlConfig.getBoolean("regenOnRespawn", false));
		config.setActionOnRegen(yamlConfig.getInt("actionOnRegen", 0));
		if ((config.getActionOnRegen() != 0) && (config.getActionOnRegen() != 1))
		{
			this.plugin.log("actionOnRegen should be 0 or 1. Check config. Value set to 0 !");
			config.setActionOnRegen(0);
		}
		config.setRespawnTimer(yamlConfig.getInt("respawnTimer", 180));
		config.setNbMaxEnderDragon(yamlConfig.getInt("nbMaxEnderDragon", 1));
		if (config.getNbMaxEnderDragon() < 1)
		{
			this.plugin.log("nbMaxEnderDragon is lesser than 1 ! Value reset to 1 !");
		}
		else if (config.getNbMaxEnderDragon() > 10)
		{
			this.plugin.log("nbMaxEnderDragon is greater than 10 ! This could be dangerous !");
		}
		config.setNbMinEnderDragon(yamlConfig.getInt("nbMinEnderDragon", 1));
		if (config.getNbMinEnderDragon() < 1)
		{
			this.plugin.log("nbMinEnderDragon is lesser than 1 ! Value reset to 1 !");
		}
		else if (config.getNbMinEnderDragon() > 10)
		{
			this.plugin.log("nbMinEnderDragon is greater than 10 ! This could be dangerous !");
		}
		if (!config.newActualNumber())
		{
			this.plugin.log("nbMinEnderDragon and nbMaxEnderDragon have bad values. They have been setted to 1 for now.");
			this.plugin.log("Please check config");
		}
		config.setXpRewardingType(yamlConfig.getInt("xpRewardingType", 0));
		if ((config.getXpRewardingType() != 0) && (config.getXpRewardingType() != 1))
		{
			config.setXpRewardingType(0);
			this.plugin.log("xpRewardingType should be 0 or 1. Check config. Value set to 0 !");
		}
		config.setXpReward(yamlConfig.getInt("xpReward", 20000));
		if (config.getXpReward() < 0)
		{
			config.setXpReward(0);
			this.plugin.log("xpReward should greater than 0. Check config. Value set to 0 !");
		}
		config.setRegenMessage(yamlConfig.getString("regenMessage"));

		String respawnMessagesTmp = yamlConfig.getString("respawnMessages", "");
		config.setRespawnMessages(respawnMessagesTmp.length() > 0 ? respawnMessagesTmp.split(";") : new String[0]);
		config.setExpMessage1(yamlConfig.getString("expMessage1", "The EnderDragon died ! You won ").split(";"));
		config.setExpMessage2(yamlConfig.getString("expMessage2", " exp !").split(";"));

		config.setPreventPortals(yamlConfig.getInt("preventPortals", 0));
		config.setEnderDragonHealth(yamlConfig.getInt("enderDragonHealth", 200));
		if (config.getEnderDragonHealth() < 1)
		{
			config.setEnderDragonHealth(200);
			this.plugin.log("enderDragonHealth should greater than 1. Check config. Value set to 200 !");
		}
		config.setEnderDragonDamageMultiplier(yamlConfig.getDouble("enderDragonDamageMultiplier", 1.0D));
		if (config.getEnderDragonDamageMultiplier() < 0.0D)
		{
			config.setEnderDragonDamageMultiplier(1.0D);
			this.plugin.log("enderDragonDamageMultiplier should greater than 0.0. Check config. Value set to 1.0 !");
		}
		config.setCustomEggHandling(yamlConfig.getInt("customEggHandling", 0));
		if ((config.getCustomEggHandling() != 1) && (config.getCustomEggHandling() != 0))
		{
			this.plugin.log("customEggHandling should be 0 or 1. Check config. Value set to 0 !");
			config.setCustomEggHandling(0);
		}
		config.setEggMessage(yamlConfig.getString("eggMessage", "You earn the &cDragon Egg &a!"));
	}

	public void newConfig(World w)
	{
		try
		{
			boolean update = false;
			YamlConfiguration yamlConfig = new YamlConfiguration();

			try
			{
				if (this.f_config.exists())
				{
					yamlConfig.load(this.f_config);
					update = true;
				}
				else
				{
					update = false;
				}
			}
			catch (Exception e)
			{
				update = false;
			}
			
			this.f_config.createNewFile();

			FileWriter fstream = new FileWriter(this.f_config);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write("#Version of the plugin, DO NOT CHANGE THIS VALUE !\n");
			out.write("pluginVersion: " + this.plugin.getDescription().getVersion() + "\n\n");

			out.write("#Should we use the [End] prefix in messages ? Yes=true, No=false\n");
			out.write("useTEAPrefix: " + yamlConfig.getBoolean("useTEAPrefix", true) + "\n\n");

			out.write("#Should we regen the End world at server stop ? Yes=true, No=false\n");
			out.write("regenOnStop: " + yamlConfig.getBoolean("regenOnStop", false) + "\n\n");

			out.write("#Should we regen the End world when respawning EnderDragons ? Yes=true, No=false\n");
			out.write("regenOnRespawn: " + yamlConfig.getBoolean("regenOnRespawn", false) + "\n\n");

			out.write("#What should we do if there are players in the End world on regen ?\n");
			out.write("#\t* 0 = All players in the End world get kicked, so they can rejoin directly in the End after restart <= Default Value\n");
			out.write("#\t* 1 = All players in the End world get teleported to first world's spawn\n");
			out.write("actionOnRegen: " + yamlConfig.getInt("actionOnRegen", 0) + "\n\n");

			out.write("#Messages to send when the End regen. Used for broadcast and kick message (actionOnRegen value above)\n");
			out.write("regenMessage: '" + yamlConfig.getString("regenMessage", "The &cEnd &ais regenerating !") + "'\n\n");

			out.write("#The end will be regenerated and ED will respawn every X minutes. Here are some examples :\n");
			out.write("#\t* 0    = Disabled\n");
			out.write("#\t* 10   = 10 minutes\n");
			out.write("#\t* 60   = 1 hour\n");
			out.write("#\t* 240  = 4 hours (6 times per day)\n");
			out.write("#\t* 360  = 6 hours (4 times per day)\n");
			out.write("#\t* 480  = 8 hours (3 times per day)\n");
			out.write("#\t* 720  = 12 hours (2 times per day)\n");
			out.write("#\t* 1440 = 24 hours (1 time per day) <= Default Value\n");
			out.write("respawnTimer: " + yamlConfig.getInt("respawnTimer", 0) + "\n\n");

			out.write("#Maximum number of EnderDragon to be respawned in the End world ? Should not be greater than 3 or 4\n");
			out.write("nbMaxEnderDragon: " + yamlConfig.getInt("nbMaxEnderDragon", 1) + "\n\n");

			out.write("#Minimum number of EnderDragon to be respawned in the End world ? Should not be greater than 3 or 4\n");
			out.write("nbMinEnderDragon: " + yamlConfig.getInt("nbMinEnderDragon", 1) + "\n\n");

			out.write("#Use custom XP rewarding system ? Yes=1, No=0\n");
			out.write("xpRewardingType: " + yamlConfig.getInt("xpRewardingType", 0) + "\n\n");

			out.write("#How many XP points does the ED drop/give ?\n");
			out.write("xpReward: " + yamlConfig.getInt("xpReward", 20000) + "\n\n");

			out.write("#Messages to send when the ED respawn. Set to '' for no messages. Seperate different lines with ;\n");
			out.write("respawnMessages: '" + yamlConfig.getString("respawnMessages", "The &cEnderDragon &arespawned !;Will you try to &ckill him &a?") + "'\n\n");

			out.write("#Messages to send when the ED die and players receive exp with custom system.\n");
			out.write("#\tMessage format : expMessage1 <expQuantity> expMessage2\n");
			out.write("#\tExample with 100 as quantity : The EnderDragon died ! You won 100 exp !\n");
			out.write("expMessage1: '" + yamlConfig.getString("expMessage1", "The &cED &adied !;You won &c") + "'\n");
			out.write("expMessage2: '" + yamlConfig.getString("expMessage2", " &aexp !") + "'\n\n");

			out.write("#Change the health value of the EnderDragon. Default = 200\n");
			out.write("enderDragonHealth: " + yamlConfig.getInt("enderDragonHealth", 200) + "\n\n");

			out.write("#Change the damage done by EnderDragons. Default absolute value depends on which difficulty you play (3 to 7.5 hearts), so it's a multiplier\n");
			out.write("enderDragonDamageMultiplier: " + yamlConfig.getDouble("enderDragonDamageMultiplier", 1.0D) + "\n\n");

			out.write("#Prevent EnderDragon from creating portals on Death ?\n");
			out.write("#    * 0    = Disabled - portal will spawn normally. Removes any obsidian tower who could block it.\n");
			out.write("#    * 1    = Egg      - portal will be removed but DragonEgg still spawn. Also removes obsi tower.\n");
			out.write("#    * 2    = Enabled  - portal will not spawn. No more cuted obsidian towers. No Egg.\n");
			out.write("preventPortals: " + yamlConfig.getInt("preventPortals", 0) + "\n\n");

			out.write("#Directly give the egg to one of the principal killer of the EnderDragon ?\n");
			out.write("# WARNING - If preventPortals has a value of 2, this does nothing !\n");
			out.write("#    * 0    = Disabled - The egg will spawn normally\n");
			out.write("#    * 1    = Enabled  - The egg will be semi-randomly given to one of the best fighter against this EnderDragon\n");
			out.write("customEggHandling: " + yamlConfig.getInt("customEggHandling", 0) + "\n\n");

			out.write("#Messages to send when the the player obtains an Egg\n");
			out.write("eggMessage: '" + yamlConfig.getString("eggMessage", "You earn the &cDragon Egg &a!") + "'\n\n");
			out.close();
			this.plugin.log("config.yml " + (update ? "updated" : "generated") + ", please see plugins/TheEndAgain/config.yml !");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void updateNbAliveED(World w)
	{
		EndWorldConfig config = this.mainEndConfig;
		int nbED = 0;
		if (this.mainEndWorld != null)
		{
			for (Entity e : this.mainEndWorld.getEntities())
			{
				if ((e.getType() == EntityType.ENDER_DRAGON) && (((EnderDragon) e).getHealth() > 0.0D))
				{
					nbED++;
				}
			}
		}
		config.setNbEd(nbED);
	}

	public void broadcastSpawned(World w)
	{
		EndWorldConfig config = this.mainEndConfig;
		for (String s : config.getRespawnMessages())
		{
			this.plugin.getServer().broadcastMessage(ChatColor.GREEN + toColor(s));
		}
	}

	public String toColor(String input)
	{
		String output = input.replaceAll("&([0-9a-fA-F])", "§$1");
		return output;
	}
}

/*
 * Location: C:\temp\Gods.jar
 * 
 * Qualified Name: com.dogonfire.gods.EndManager
 * 
 * JD-Core Version: 0.7.0.1
 */