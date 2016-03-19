package com.dogonfire.gods;

import com.dogonfire.gods.tasks.GiveHolyArtifactTask;
import com.dogonfire.gods.tasks.GiveItemTask;
import com.dogonfire.gods.tasks.GodSpeakTask;
import com.dogonfire.gods.tasks.HealPlayerTask;
import com.dogonfire.gods.tasks.SpawnGuideMobTask;
import com.dogonfire.gods.tasks.SpawnHostileMobsTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GodManager
{
	private Gods						plugin;
	private FileConfiguration			godsConfig		= null;
	private File						godsConfigFile	= null;
	private Random						random			= new Random();
	private boolean						isUpdating		= false;
	private List<String>				onlineGods		= new ArrayList();
	private long						lastSaveTime;
	private String						pattern			= "HH:mm:ss dd-MM-yyyy";
	DateFormat							formatter		= new SimpleDateFormat(this.pattern);
	private HashMap<String, GodGender>	godGenders		= new HashMap();
	private HashMap<String, GodGender>	godType			= new HashMap();
	private HashMap<String, GodGender>	godMood			= new HashMap();

	public static enum GodType
	{
		FROST,
		LOVE,
		EVIL,
		SEA,
		MOON,
		SUN,
		THUNDER,
		PARTY,
		WAR,
		WEREWOLVES,
		CREATURES,
		WISDOM,
		NATURE;
	}

	static enum GodGender
	{
		None, Male, Female;
	}

	static enum GodMood
	{
		EXALTED, PLEASED, NEUTRAL, DISPLEASED, ANGRY;
	}

	public static enum GodRelation
	{
		LOVERS, MARRIED, ENEMIES, FRIENDS, BFF, ROOMMATES;
	}

	GodManager(Gods p)
	{
		this.plugin = p;
	}

	public void load()
	{
		this.godsConfigFile = new File(this.plugin.getDataFolder(), "gods.yml");

		this.godsConfig = YamlConfiguration.loadConfiguration(this.godsConfigFile);

		this.plugin.log("Loaded " + this.godsConfig.getKeys(false).size() + " gods.");
		for (String godName : this.godsConfig.getKeys(false))
		{
			String priestName = this.godsConfig.getString(godName + ".PriestName");
			if (priestName != null)
			{
				List<String> list = new ArrayList();
				list.add(priestName);

				this.godsConfig.set("PriestName", null);
				this.godsConfig.set(godName + ".Priests", list);

				save();
			}
		}
	}

	public void save()
	{
		this.lastSaveTime = System.currentTimeMillis();
		if ((this.godsConfig == null) || (this.godsConfigFile == null))
		{
			return;
		}
		try
		{
			this.godsConfig.save(this.godsConfigFile);
		}
		catch (Exception ex)
		{
			this.plugin.log("Could not save config to " + this.godsConfigFile + ": " + ex.getMessage());
		}
	}

	public void saveTimed()
	{
		if (System.currentTimeMillis() - this.lastSaveTime < 180000L)
		{
			return;
		}
		save();
	}

	public Set<String> getAllGods()
	{
		Set<String> gods = this.godsConfig.getKeys(false);

		return gods;
	}

	public List<String> getOfflineGods()
	{
		Set<String> allGods = this.godsConfig.getKeys(false);
		List<String> offlineGods = new ArrayList();
		for (String godName : allGods)
		{
			if (!this.onlineGods.contains(godName))
			{
				offlineGods.add(godName);
			}
		}
		return offlineGods;
	}

	public Set<String> getTopGods()
	{
		Set<String> topGods = this.godsConfig.getKeys(false);

		return topGods;
	}

	public void updateOnlineGods()
	{
		this.onlineGods.clear();
		for (Player player : this.plugin.getServer().getOnlinePlayers())
		{
			String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
			if (godName != null)
			{
				if (!this.onlineGods.contains(godName))
				{
					this.onlineGods.add(godName);
				}
			}
		}
	}

	public List<String> getOnlineGods()
	{
		return this.onlineGods;
	}

	public GodGender getGenderForGod(String godName)
	{
		String genderString = this.godsConfig.getString(godName + ".Gender");
		GodGender godGender = GodGender.None;

		if (genderString != null)
		{
			try
			{
				godGender = GodGender.valueOf(genderString);
			}
			catch (Exception ex)
			{
				godGender = GodGender.None;
			}
		}

		return godGender;
	}

	public void setGenderForGod(String godName, GodGender godGender)
	{
		this.godsConfig.set(godName + ".Gender", godGender.name());

		saveTimed();
	}

	public String getLanguageFileForGod(String godName)
	{
		String languageFileName = this.godsConfig.getString(godName + ".LanguageFileName");

		if (languageFileName == null)
		{
			GodType godType = this.plugin.getGodManager().getDivineForceForGod(godName);
			if (godType == null)
			{
				godType = GodType.values()[this.random.nextInt(GodType.values().length)];
				this.plugin.getGodManager().setDivineForceForGod(godName, godType);

				this.plugin.logDebug("getLanguageFileForGod: Could not find a type for " + godName + ", so setting his type to " + godType.name());
			}
			
			GodGender godGender = this.plugin.getGodManager().getGenderForGod(godName);
						
			if(godGender==GodGender.None)
			{
				this.plugin.logDebug("getLanguageFileForGod: Could not find a gender for " + godName + ", so setting his type to " + godGender.name());

				switch(random.nextInt(2))
				{
					case 0 : godGender = GodGender.Male; break;
					case 1 : godGender = GodGender.Female; break; 
				}				
			}			
			
			languageFileName = this.plugin.languageIdentifier + "_" + godType.name().toLowerCase() + "_" + godGender.name().toLowerCase() + ".yml";

			this.plugin.log("getLanguageFileForGod: Setting language file " + languageFileName);

			this.godsConfig.set(godName + ".LanguageFileName", languageFileName);

			saveTimed();
		}

		return languageFileName;
	}

	public float getExactMoodForGod(String godName)
	{
		return (float) this.godsConfig.getDouble(godName + ".Mood");
	}

	public GodMood getMoodForGod(String godName)
	{
		float godMood = (float) this.godsConfig.getDouble(godName + ".Mood");
		if (godMood < -70.0F)
		{
			return GodMood.ANGRY;
		}
		if (godMood < -20.0F)
		{
			return GodMood.DISPLEASED;
		}
		if (godMood < 20.0F)
		{
			return GodMood.NEUTRAL;
		}
		if (godMood < 70.0F)
		{
			return GodMood.PLEASED;
		}
		return GodMood.EXALTED;
	}

	public void addMoodForGod(String godName, float mood)
	{
		float godMood = (float) this.godsConfig.getDouble(godName + ".Mood");

		godMood += mood;
		if (godMood > 100.0F)
		{
			godMood = 100.0F;
		}
		else if (godMood < -100.0F)
		{
			godMood = -100.0F;
		}
		this.godsConfig.set(godName + ".Mood", Float.valueOf(godMood));

		saveTimed();
	}

	public ChatColor getColorForGodType(GodType godType)
	{
		ChatColor color = ChatColor.WHITE;
		if (godType == null)
		{
			return ChatColor.WHITE;
		}
		switch (godType)
		{
			case THUNDER:
				color = ChatColor.DARK_GRAY;
				break;
			case EVIL:
				color = ChatColor.RED;
				break;
			case WISDOM:
				color = ChatColor.DARK_GREEN;
				break;
			case FROST:
				color = ChatColor.BLACK;
				break;
			case SUN:
				color = ChatColor.DARK_RED;
				break;
			case SEA:
				color = ChatColor.BOLD;
				break;
			case LOVE:
				color = ChatColor.BLUE;
				break;
			case MOON:
				color = ChatColor.GRAY;
				break;
			case WAR:
				color = ChatColor.GREEN;
				break;
			case NATURE:
				color = ChatColor.YELLOW;
				break;
			case CREATURES:
				color = ChatColor.DARK_BLUE;
				break;
			case WEREWOLVES:
				color = ChatColor.GRAY;
		}
		return color;
	}

	public ChatColor getColorForGod(String godName)
	{
		ChatColor color = ChatColor.WHITE;

		GodType godType = getDivineForceForGod(godName);

		return getColorForGodType(godType);
	}

	public void setColorForGod(String godName, ChatColor color)
	{
		this.godsConfig.set(godName + ".Color", color.name());

		saveTimed();
	}

	public String getTitleForGod(String godName)
	{
		if (!this.plugin.useGodTitles)
		{
			return "";
		}
		GodType godType = this.plugin.getGodManager().getDivineForceForGod(godName);
		if (godType == null)
		{
			return "";
		}
		return this.plugin.getLanguageManager().getGodTypeName(godType, this.plugin.getLanguageManager().getGodGenderName(this.plugin.getGodManager().getGenderForGod(godName)));
	}

	public void setHomeForGod(String godName, Location location)
	{
		this.godsConfig.set(godName + ".Home.X", Double.valueOf(location.getX()));
		this.godsConfig.set(godName + ".Home.Y", Double.valueOf(location.getY()));
		this.godsConfig.set(godName + ".Home.Z", Double.valueOf(location.getZ()));
		this.godsConfig.set(godName + ".Home.World", location.getWorld().getName());

		saveTimed();
	}

	public Location getHomeForGod(String godName)
	{
		Location location = new Location(null, 0.0D, 0.0D, 0.0D);

		String worldName = this.godsConfig.getString(godName + ".Home.World");
		if (worldName == null)
		{
			return null;
		}
		location.setWorld(this.plugin.getServer().getWorld(worldName));

		location.setX(this.godsConfig.getDouble(godName + ".Home.X"));
		location.setY(this.godsConfig.getDouble(godName + ".Home.Y"));
		location.setZ(this.godsConfig.getDouble(godName + ".Home.Z"));

		return location;
	}

	public long getSeedForGod(String godName)
	{
		long seed = this.godsConfig.getLong(godName + ".Seed");
		if (seed == 0L)
		{
			seed = this.random.nextLong();
			this.godsConfig.set(godName + ".Seed", Long.valueOf(seed));

			saveTimed();
		}
		return seed;
	}

	public boolean setPendingPriest(String godName, UUID believerId)
	{
		String lastPriestTime = this.godsConfig.getString(godName + ".PendingPriestTime");

		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date lastDate = null;
		Date thisDate = new Date();
		try
		{
			lastDate = formatter.parse(lastPriestTime);
		}
		catch (Exception ex)
		{
			lastDate = new Date();
			lastDate.setTime(0L);
		}
		long diff = thisDate.getTime() - lastDate.getTime();
		long diffMinutes = diff / 60000L % 60L;
		if (diffMinutes < 3L)
		{
			return false;
		}

		if (believerId == null)
		{
			return false;
		}

		this.godsConfig.set(godName + ".PendingPriest", believerId.toString());

		saveTimed();

		this.plugin.getBelieverManager().setPendingPriest(believerId);

		return true;
	}

	public List<UUID> getInvitedPlayerForGod(String godName)
	{
		List<String> players = this.godsConfig.getStringList(godName + ".InvitedPlayers");

		if (players.size() == 0)
		{
			return null;
		}

		List<UUID> invitedPlayers = new ArrayList<UUID>();

		for (String playerId : players)
		{
			invitedPlayers.add(UUID.fromString(playerId));
		}

		return invitedPlayers;
	}

	public boolean increaseContestedHolyLandKillsForGod(String godName, int n)
	{
		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date thisDate = new Date();

		Long contestedLand = getContestedHolyLandForGod(godName);

		int kills = this.godsConfig.getInt(godName + ".ContestedKills");

		this.godsConfig.set(godName + ".ContestedKills", Integer.valueOf(kills + n));

		saveTimed();

		return kills + n > 10;
	}

	public int getContestedHolyLandKillsForGod(String godName, int n)
	{
		Long contestedLand = getContestedHolyLandForGod(godName);

		int kills = this.godsConfig.getInt(godName + ".ContestedKills");

		return kills;
	}

	public void setContestedHolyLandForGod(String godName, Location contestedLand)
	{
		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date thisDate = new Date();

		this.godsConfig.set(godName + ".ContestedLand.Hash", Long.valueOf(this.plugin.getLandManager().hashLocation(contestedLand)));

		this.godsConfig.set(godName + ".ContestedLand" + ".X", Integer.valueOf(contestedLand.getBlockX()));
		this.godsConfig.set(godName + ".ContestedLand" + ".Y", Integer.valueOf(contestedLand.getBlockY()));
		this.godsConfig.set(godName + ".ContestedLand" + ".Z", Integer.valueOf(contestedLand.getBlockZ()));
		this.godsConfig.set(godName + ".ContestedLand" + ".World", contestedLand.getWorld().getName());

		this.plugin.getLandManager().setContestedLand(contestedLand, godName);

		saveTimed();
	}

	public Long getContestedHolyLandForGod(String godName)
	{
		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date thisDate = new Date();
		Date contestedDate = null;

		Long contestedLand = Long.valueOf(this.godsConfig.getLong(godName + ".ContestedLand.Hash"));
		if (contestedLand.longValue() == 0L)
		{
			return null;
		}
		return contestedLand;
	}

	public Location getContestedHolyLandAttackLocationForGod(String godName)
	{
		Long contestedLand = Long.valueOf(this.godsConfig.getLong(godName + ".ContestedLand"));

		int x = this.godsConfig.getInt(godName + ".ContestedLand" + ".X");
		int y = this.godsConfig.getInt(godName + ".ContestedLand" + ".Y");
		int z = this.godsConfig.getInt(godName + ".ContestedLand" + ".Z");
		String worldName = this.godsConfig.getString(godName + ".ContestedLand" + ".World");

		return new Location(this.plugin.getServer().getWorld(worldName), x, y, z);
	}

	public void clearContestedHolyLandForGod(String godName)
	{
		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date thisDate = new Date();

		this.godsConfig.set(godName + ".ContestedLand", null);

		saveTimed();
	}

	public void setCursedPlayerForGod(String godName, UUID believerId)
	{
		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date thisDate = new Date();

		this.godsConfig.set(godName + ".CursedPlayer", believerId);
		this.godsConfig.set(godName + ".CursedTime", formatter.format(thisDate));

		saveTimed();
	}

	public void setBlessedPlayerForGod(String godName, UUID believerId)
	{
		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date thisDate = new Date();

		this.godsConfig.set(godName + ".BlessedPlayer", believerId);
		this.godsConfig.set(godName + ".BlessedTime", formatter.format(thisDate));

		saveTimed();
	}

	public void setTimeSinceLastQuest(String godName)
	{
		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date thisDate = new Date();

		this.godsConfig.set(godName + ".LastQuestTime", formatter.format(thisDate));

		saveTimed();
	}

	public long getMinutesSinceLastQuest(String godName)
	{
		Date thisDate = new Date();
		Date questDate = null;
		this.godsConfig.set(godName + ".LastQuestTime", formatter.format(thisDate));

		String lastQuestDateString = this.godsConfig.getString(godName + ".LastQuestTime");
		try
		{
			questDate = formatter.parse(lastQuestDateString);
		}
		catch (Exception ex)
		{
			questDate = new Date();
			questDate.setTime(0L);
		}

		long diff = thisDate.getTime() - questDate.getTime();

		return diff / 60000;
	}

	public boolean toggleWarRelationForGod(String godName, String enemyGodName)
	{
		List<String> gods = this.godsConfig.getStringList(godName + ".Enemies");
		if (!gods.contains(enemyGodName))
		{
			gods.add(enemyGodName);
			this.godsConfig.set(godName + ".Enemies", gods);

			gods = this.godsConfig.getStringList(enemyGodName + ".Enemies");
			if (!gods.contains(godName))
			{
				gods.add(godName);
				this.godsConfig.set(enemyGodName + ".Enemies", gods);
			}
			if (this.godsConfig.getStringList(godName + ".Allies").contains(enemyGodName))
			{
				this.godsConfig.set(godName + ".Allies." + enemyGodName, null);
			}
			if (this.godsConfig.getStringList(enemyGodName + ".Allies").contains(godName))
			{
				this.godsConfig.set(enemyGodName + ".Allies." + godName, null);
			}
			saveTimed();

			return true;
		}
		gods.remove(enemyGodName);
		this.godsConfig.set(godName + ".Enemies", gods);

		gods = this.godsConfig.getStringList(enemyGodName + ".Enemies");
		if (gods.contains(godName))
		{
			gods.remove(godName);
			this.godsConfig.set(enemyGodName + ".Enemies", gods);
		}
		if (this.godsConfig.getStringList(godName + ".Allies").contains(enemyGodName))
		{
			this.godsConfig.set(godName + ".Allies." + enemyGodName, null);
		}
		if (this.godsConfig.getStringList(enemyGodName + ".Allies").contains(godName))
		{
			this.godsConfig.set(enemyGodName + ".Allies." + godName, null);
		}
		save();

		return false;
	}

	public boolean toggleAllianceRelationForGod(String godName, String allyGodName)
	{
		List<String> gods = this.godsConfig.getStringList(godName + ".Allies");
		if (!gods.contains(allyGodName))
		{
			gods.add(allyGodName);

			this.godsConfig.set(godName + ".Allies", gods);

			gods = this.godsConfig.getStringList(allyGodName + ".Allies");
			if (!gods.contains(godName))
			{
				gods.add(godName);
				this.godsConfig.set(allyGodName + ".Allies", gods);
			}
			if (this.godsConfig.getStringList(godName + ".Enemies").contains(allyGodName))
			{
				this.godsConfig.set(godName + ".Enemies." + allyGodName, null);
			}
			if (this.godsConfig.getStringList(allyGodName + ".Enemies").contains(godName))
			{
				this.godsConfig.set(allyGodName + ".Enemies." + godName, null);
			}
			saveTimed();

			return true;
		}
		gods.remove(allyGodName);
		this.godsConfig.set(godName + ".Allies", gods);

		gods = this.godsConfig.getStringList(allyGodName + ".Allies");
		if (gods.contains(godName))
		{
			gods.remove(godName);
			this.godsConfig.set(allyGodName + ".Allies", gods);
		}
		if (this.godsConfig.getStringList(godName + ".Enemies").contains(allyGodName))
		{
			this.godsConfig.set(godName + ".Enemies." + allyGodName, null);
		}
		if (this.godsConfig.getStringList(allyGodName + ".Enemies").contains(godName))
		{
			this.godsConfig.set(allyGodName + ".Enemies." + godName, null);
		}
		save();

		return false;
	}

	List<String> getAllianceRelations(String godName)
	{
		return this.godsConfig.getStringList(godName + ".Allies");
	}

	List<String> getWarRelations(String godName)
	{
		return this.godsConfig.getStringList(godName + ".Enemies");
	}

	public boolean hasAllianceRelation(String godName, String otherGodName)
	{
		return this.godsConfig.contains(godName + ".Allies" + otherGodName);
	}

	public boolean hasWarRelation(String godName, String otherGodName)
	{
		return this.godsConfig.contains(godName + ".Enemies" + otherGodName);
	}

	public void setPrivateAccess(String godName, boolean privateAccess)
	{
		this.godsConfig.set(godName + ".PrivateAccess", Boolean.valueOf(privateAccess));

		saveTimed();
	}

	public boolean isPrivateAccess(String godName)
	{
		Boolean access = Boolean.valueOf(this.godsConfig.getBoolean(godName + ".PrivateAccess"));
		if (access != null)
		{
			return access.booleanValue();
		}
		return false;
	}

	public List<String> getEnemyGodsForGod(String godName)
	{
		return this.godsConfig.getStringList(godName + ".War");
	}

	private int getVerbosityForGod(String godName)
	{
		int verbosity = this.godsConfig.getInt(godName + ".Verbosity");
		if (verbosity == 0)
		{
			verbosity = 1 + this.random.nextInt(50);

			this.godsConfig.set(godName + ".Verbosity", Integer.valueOf(verbosity));

			save();
		}
		Random moodRandom = new Random(getSeedForGod(godName));

		double variation = 1.0D + 1.0D * Math.sin(moodRandom.nextFloat() + (float) System.currentTimeMillis() / 3600000.0F);

		double godVerbosity = getGodPower(godName) / 100.0F + verbosity;

		return (int) (1.0D + variation * (this.plugin.godVerbosity * godVerbosity));
	}

	private String generateHolyMobTypeForGod(String godName)
	{
		EntityType mobType = EntityType.UNKNOWN;
		int r1 = this.random.nextInt(7);
		switch (r1)
		{
			case 0:
				mobType = EntityType.CHICKEN;
				break;
			case 1:
				mobType = EntityType.COW;
				break;
			case 2:
				mobType = EntityType.PIG;
				break;
			case 3:
				mobType = EntityType.SHEEP;
				break;
			case 4:
				mobType = EntityType.OCELOT;
				break;
			case 5:
				mobType = EntityType.WOLF;
				break;
			case 6:
				mobType = EntityType.MUSHROOM_COW;
		}
		return mobType.name();
	}

	private String generateUnholyMobTypeForGod(String godName)
	{
		EntityType mobType = EntityType.UNKNOWN;
		int r1 = this.random.nextInt(11);
		switch (r1)
		{
			case 0:
				mobType = EntityType.CHICKEN;
				break;
			case 1:
				mobType = EntityType.COW;
				break;
			case 2:
				mobType = EntityType.ENDERMAN;
				break;
			case 3:
				mobType = EntityType.PIG;
				break;
			case 4:
				mobType = EntityType.SHEEP;
				break;
			case 5:
				mobType = EntityType.OCELOT;
				break;
			case 6:
				mobType = EntityType.WOLF;
				break;
			case 7:
				mobType = EntityType.SQUID;
				break;
			case 8:
				mobType = EntityType.SPIDER;
				break;
			case 9:
				mobType = EntityType.SKELETON;
				break;
			case 10:
				mobType = EntityType.ZOMBIE;
		}
		return mobType.name();
	}

	public EntityType getUnholyMobTypeForGod(String godName)
	{
		String mobTypeString = this.godsConfig.getString(godName + ".SlayMobType");
		EntityType mobType = EntityType.UNKNOWN;
		if (mobTypeString == null)
		{
			mobTypeString = generateUnholyMobTypeForGod(godName);

			this.godsConfig.set(godName + ".SlayMobType", mobTypeString);

			saveTimed();
		}
		mobType = (EntityType) EntityType.valueOf(EntityType.class, mobTypeString);
		if (mobType == null)
		{
			mobTypeString = generateUnholyMobTypeForGod(godName);

			this.godsConfig.set(godName + ".SlayMobType", mobTypeString);

			save();

			mobType = EntityType.fromName(mobTypeString);
		}
		return mobType;
	}

	public EntityType getHolyMobTypeForGod(String godName)
	{
		String mobTypeString = this.godsConfig.getString(godName + ".NotSlayMobType");
		EntityType mobType = EntityType.UNKNOWN;
		if (mobTypeString == null)
		{
			do
			{
				mobTypeString = generateHolyMobTypeForGod(godName);
			}
			while (mobTypeString.equals(getUnholyMobTypeForGod(godName).name()));
			this.godsConfig.set(godName + ".NotSlayMobType", mobTypeString);

			saveTimed();
		}
		mobType = (EntityType) EntityType.valueOf(EntityType.class, mobTypeString);
		if (mobType == null)
		{
			do
			{
				mobTypeString = generateHolyMobTypeForGod(godName);
			}
			while (mobTypeString.equals(getUnholyMobTypeForGod(godName).name()));
			this.godsConfig.set(godName + ".NotSlayMobType", mobTypeString);

			save();

			mobType = EntityType.fromName(mobTypeString);
		}
		return mobType;
	}

	public Material getEatFoodTypeForGod(String godName)
	{
		String foodTypeString = this.godsConfig.getString(godName + ".EatFoodType");
		Material foodType = Material.AIR;
		if (foodTypeString == null)
		{
			int r1 = this.random.nextInt(7);
			switch (r1)
			{
				case 0:
					foodType = Material.APPLE;
					break;
				case 1:
					foodType = Material.BREAD;
					break;
				case 2:
					foodType = Material.COOKED_FISH;
					break;
				case 3:
					foodType = Material.MELON;
					break;
				case 4:
					foodType = Material.COOKED_BEEF;
					break;
				case 5:
					foodType = Material.GRILLED_PORK;
					break;
				case 6:
					foodType = Material.CARROT_ITEM;
			}
			foodTypeString = foodType.name();

			this.godsConfig.set(godName + ".EatFoodType", foodTypeString);

			saveTimed();
		}
		else
		{
			foodType = Material.getMaterial(foodTypeString);
		}
		return foodType;
	}

	public Material getNotEatFoodTypeForGod(String godName)
	{
		String foodTypeString = this.godsConfig.getString(godName + ".NotEatFoodType");
		Material foodType = Material.AIR;
		if (foodTypeString == null)
		{
			do
			{
				int r1 = this.random.nextInt(7);
				switch (r1)
				{
					case 0:
						foodType = Material.APPLE;
						break;
					case 1:
						foodType = Material.BREAD;
						break;
					case 2:
						foodType = Material.COOKED_FISH;
						break;
					case 3:
						foodType = Material.MELON;
						break;
					case 4:
						foodType = Material.COOKED_BEEF;
						break;
					case 5:
						foodType = Material.GRILLED_PORK;
						break;
					case 6:
						foodType = Material.CARROT_ITEM;
				}
				foodTypeString = foodType.name();
			}
			while (foodTypeString.equals(getEatFoodTypeForGod(godName).name()));
			this.godsConfig.set(godName + ".NotEatFoodType", foodTypeString);

			saveTimed();
		}
		else
		{
			foodType = Material.getMaterial(foodTypeString);
		}
		return foodType;
	}

	public Material getSacrificeItemTypeForGod(String godName)
	{
		String itemName = "";
		Integer value = Integer.valueOf(0);
		String sacrificeItemName = null;

		ConfigurationSection configSection = this.godsConfig.getConfigurationSection(godName + ".SacrificeValues");
		if ((configSection == null) || (configSection.getKeys(false).size() == 0))
		{
			return null;
		}
		for (int i = 0; i < configSection.getKeys(false).size(); i++)
		{
			itemName = (String) configSection.getKeys(false).toArray()[this.random.nextInt(configSection.getKeys(false).size())];

			value = Integer.valueOf(this.godsConfig.getInt(godName + ".SacrificeValues." + itemName));
			if (value.intValue() > 10)
			{
				sacrificeItemName = itemName;
			}
		}
		if (sacrificeItemName != null)
		{
			return Material.getMaterial(sacrificeItemName);
		}
		return null;
	}

	public float getFalloffModifierForGod(String godName)
	{
		Random moodRandom = new Random(getSeedForGod(godName));

		float baseFalloff = (1 + moodRandom.nextInt(40)) / 20.0F;

		double falloffValue = -this.plugin.moodFalloff * (1.0F + baseFalloff * this.plugin.getBelieverManager().getOnlineBelieversForGod(godName).size()) * (1.0D + Math.sin((float) System.currentTimeMillis() / 1500000.0F));

		this.plugin.logDebug(godName + " mood falloff is " + falloffValue);

		return (float) falloffValue;
	}

	public float getPleasedModifierForGod(String godName)
	{
		Random moodRandom = new Random(getSeedForGod(godName));

		return 5 + moodRandom.nextInt(10);
	}

	public float getAngryModifierForGod(String godName)
	{
		return -1.0F;
	}

	public void handleEat(Player player, String godName, String foodType)
	{
		Material eatFoodType = getEatFoodTypeForGod(godName);
		Material notEatFoodType = getNotEatFoodTypeForGod(godName);

		if (foodType.equals(eatFoodType.name()))
		{
			addMoodForGod(godName, getPleasedModifierForGod(godName));

			if (blessPlayer(godName, player.getUniqueId(), getGodPower(godName)))
			{
				try
				{
					this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getItemTypeName(eatFoodType));
				}
				catch (Exception ex)
				{
					this.plugin.logDebug(ex.getStackTrace().toString());
				}
				this.plugin.getLanguageManager().setPlayerName(player.getDisplayName());
				if (this.plugin.commandmentsBroadcastFoodEaten)
				{
					godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversEatFoodBlessing, 2 + this.random.nextInt(20));
				}
				else
				{
					godSayToBeliever(godName, player.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieversEatFoodBlessing);
				}
			}
		}

		if (foodType.equals(notEatFoodType.name()))
		{
			addMoodForGod(godName, getAngryModifierForGod(godName));
			if (cursePlayer(godName, player.getUniqueId(), getGodPower(godName)))
			{
				try
				{
					this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getItemTypeName(notEatFoodType));
				}
				catch (Exception ex)
				{
					this.plugin.logDebug(ex.getStackTrace().toString());
				}

				this.plugin.getLanguageManager().setPlayerName(player.getDisplayName().toUpperCase());

				if (this.plugin.commandmentsBroadcastFoodEaten)
				{
					godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversNotEatFoodCursing, 2 + this.random.nextInt(10));
				}
				else
				{
					godSayToBeliever(godName, player.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieversNotEatFoodCursing);
				}
			}
		}
	}

	public void handleKilledPlayer(UUID playerId, String godName, GodType godType)
	{
		if (godType == null)
		{
			return;
		}

		if (this.plugin.leaveReligionOnDeath)
		{
			this.plugin.getBelieverManager().believerLeave(godName, playerId);
		}

		if (this.random.nextInt(10) == 0)
		{
			// this.plugin.getGodManager().GodSayToPriest(godName,
			// LanguageManager.LANGUAGESTRING.GodToPriestBelieverKilledDeclareWarQuestion);
		}

		switch (godType)
		{
			case SUN:
				// if (!this.plugin.getQuestManager().hasQuest(godName))
				// {
				// int godPower = 1 + (int)
				// this.plugin.getGodManager().getGodPower(godName);
				// int i = godPower * this.plugin.questFrequency;
				// }
				break;
			case WAR:

				// Check for being in war, and in that case +1 belief
				// Earn 1

				break;
		}
	}

	public void handleKilled(Player player, String godName, String mobType)
	{
		if ((!this.plugin.commandmentsEnabled) || (mobType == null))
		{
			return;
		}
		EntityType holyMobType = getHolyMobTypeForGod(godName);
		EntityType unholyMobType = getUnholyMobTypeForGod(godName);
		if ((unholyMobType != null) && (mobType.equals(unholyMobType.name())))
		{
			if (blessPlayer(godName, player.getUniqueId(), getGodPower(godName)))
			{
				addMoodForGod(godName, getPleasedModifierForGod(godName));

				this.plugin.getLanguageManager().setPlayerName(player.getDisplayName());
				try
				{
					this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getMobTypeName(unholyMobType));
				}
				catch (Exception ex)
				{
					this.plugin.logDebug(ex.getStackTrace().toString());
				}
				if (this.plugin.commandmentsBroadcastMobSlain)
				{
					godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversSlayMobBlessing, 2 + this.random.nextInt(20));
				}
				else
				{
					godSayToBeliever(godName, player.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieversSlayMobBlessing);
				}
			}
		}

		if ((holyMobType != null) && (mobType.equals(holyMobType.name())))
		{
			if (cursePlayer(godName, player.getUniqueId(), getGodPower(godName)))
			{
				addMoodForGod(godName, getAngryModifierForGod(godName));

				this.plugin.getLanguageManager().setPlayerName(player.getDisplayName().toUpperCase());
				try
				{
					this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getMobTypeName(holyMobType));
				}
				catch (Exception ex)
				{
					this.plugin.logDebug(ex.getStackTrace().toString());
				}
				if (this.plugin.commandmentsBroadcastMobSlain)
				{
					godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversNotSlayMobCursing, 2 + this.random.nextInt(10));
				}
				else
				{
					godSayToBeliever(godName, player.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieversNotSlayMobCursing);
				}
			}
		}
	}

	public void handleSacrifice(String godName, Player believer, Material type)
	{
		if (believer == null)
		{
			return;
		}

		if (!this.plugin.isEnabledInWorld(believer.getWorld()))
		{
			return;
		}

		if (godName == null)
		{
			return;
		}

		int godPower = (int) this.plugin.getGodManager().getGodPower(godName);

		this.plugin.log(believer.getDisplayName() + " sacrificed " + type.name() + " to " + godName);

		Material eatFoodType = getEatFoodTypeForGod(godName);

		if (type == eatFoodType)
		{
			addMoodForGod(godName, getAngryModifierForGod(godName));
			cursePlayer(godName, believer.getUniqueId(), getGodPower(godName));

			try
			{
				this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getItemTypeName(eatFoodType));
			}
			catch (Exception ex)
			{
				this.plugin.logDebug(ex.getStackTrace().toString());
			}

			this.plugin.getLanguageManager().setPlayerName(believer.getDisplayName());

			if (this.plugin.commandmentsBroadcastFoodEaten)
			{
				godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieverHolyFoodSacrifice, 2 + this.random.nextInt(10));
			}
			else
			{
				godSayToBeliever(godName, believer.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverHolyFoodSacrifice);
			}

			strikePlayerWithLightning(believer.getUniqueId(), 1 + this.random.nextInt(3));

			return;
		}

		float value = getSacrificeValueForGod(godName, type);

		this.plugin.getLanguageManager().setPlayerName(believer.getDisplayName());

		try
		{
			this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getItemTypeName(type));
		}
		catch (Exception ex)
		{
			this.plugin.logDebug(ex.getStackTrace().toString());
		}

		if (value > 10.0F)
		{
			addMoodForGod(godName, getPleasedModifierForGod(godName));
			this.plugin.getBelieverManager().addPrayer(believer.getUniqueId(), godName);

			blessPlayer(godName, believer.getUniqueId(), godPower);
			godSayToBeliever(godName, believer.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverGoodSacrifice);

			this.plugin.getBelieverManager().increasePrayerPower(believer.getUniqueId(), 1);
		}
		else if (value >= -5.0F)
		{
			godSayToBeliever(godName, believer.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverMehSacrifice);
		}
		else
		{
			addMoodForGod(godName, getAngryModifierForGod(godName));
			strikePlayerWithLightning(believer.getUniqueId(), 1 + this.random.nextInt(3));
			godSayToBeliever(godName, believer.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverBadSacrifice);
		}

		value -= 1.0F;

		this.godsConfig.set(godName + ".SacrificeValues." + type.name(), Float.valueOf(value));

		saveTimed();
	}

	private float getSacrificeValueForGod(String godName, Material type)
	{
		return (float) this.godsConfig.getDouble(godName + ".SacrificeValues." + type.name());
	}

	private Material getSacrificeUnwantedForGod(String godName)
	{
		List<Material> unwantedItems = new ArrayList();
		ConfigurationSection configSection = this.godsConfig.getConfigurationSection(godName + ".SacrificeValues.");
		if (configSection != null)
		{
			for (String itemType : configSection.getKeys(false))
			{
				Material item = null;
				try
				{
					item = Material.valueOf(itemType);
				}
				catch (Exception ex)
				{
					continue;
				}
				if (this.godsConfig.getDouble(godName + ".SacrificeValues." + itemType) <= 0.0D)
				{
					unwantedItems.add(item);
				}
			}
		}
		else
		{
			return null;
		}
		if (unwantedItems.size() == 0)
		{
			return null;
		}
		return (Material) unwantedItems.get(this.random.nextInt(unwantedItems.size()));
	}

	public String getEnemyPlayerForGod(String godName)
	{
		List<String> enemyGods = getEnemyGodsForGod(godName);
		if (enemyGods.size() == 0)
		{
			return null;
		}
		int g = 0;
		do
		{
			String enemyGod = (String) enemyGods.get(enemyGods.size());
			if (enemyGod != null)
			{
				Set<UUID> believers = this.plugin.getBelieverManager().getBelieversForGod(enemyGod);

				int b = 0;
				while (b < 10)
				{
					int r = this.random.nextInt(believers.size());

					String believerName = (String) believers.toArray()[r];
					if (this.plugin.getServer().getPlayer(believerName) != null)
					{
						return believerName;
					}
					b++;
				}
			}
			g++;
		}
		while (

		g < 50);
		return null;
	}

	public Player getCursedPlayerForGod(String godName)
	{
		Date lastCursedDate = getLastCursingTimeForGod(godName);
		if (lastCursedDate == null)
		{
			return null;
		}
		Date thisDate = new Date();

		long diff = thisDate.getTime() - lastCursedDate.getTime();
		long diffMinutes = diff / 60000L;
		if (diffMinutes > this.plugin.maxCursingTime)
		{
			this.godsConfig.set(godName + ".CursedPlayer", null);
			this.godsConfig.set(godName + ".CursedTime", null);
			saveTimed();

			return null;
		}

		return plugin.getServer().getPlayer(this.godsConfig.getString(godName + ".CursedPlayer"));
	}

	public String getBlessedPlayerForGod(String godName)
	{
		Date lastBlessedDate = getLastBlessedTimeForGod(godName);
		if (lastBlessedDate == null)
		{
			return null;
		}
		Date thisDate = new Date();

		long diff = thisDate.getTime() - lastBlessedDate.getTime();
		long diffSeconds = diff / 1000L;
		if (diffSeconds > this.plugin.maxBlessingTime)
		{
			this.godsConfig.set(godName + ".BlessedPlayer", null);
			this.godsConfig.set(godName + ".BlessedTime", null);
			saveTimed();

			return null;
		}
		return this.godsConfig.getString(godName + ".BlessedPlayer");
	}

	public boolean godExist(String godName)
	{
		String name = this.godsConfig.getString(formatGodName(godName) + ".Created");
		if (name == null)
		{
			return false;
		}
		return true;
	}

	public String formatGodName(String godName)
	{
		return godName.substring(0, 1).toUpperCase() + godName.substring(1).toLowerCase();
	}

	public void createGod(String godName, Location location, GodGender godGender, GodType godType)
	{
		Date thisDate = new Date();

		DateFormat formatter = new SimpleDateFormat(this.pattern);

		setHomeForGod(godName, location);
		setGenderForGod(godName, godGender);
		setDivineForceForGod(godName, godType);
		setPrivateAccess(godName, this.plugin.defaultPrivateReligions);

		this.godsConfig.set(godName + ".Created", formatter.format(thisDate));

		saveTimed();
	}

	public Date getLastCursingTimeForGod(String godName)
	{
		String lastCursedString = this.godsConfig.getString(godName + ".CursedTime");
		if (lastCursedString == null)
		{
			return null;
		}
		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date lastCursedDate = null;
		try
		{
			lastCursedDate = formatter.parse(lastCursedString);
		}
		catch (Exception ex)
		{
			lastCursedDate = new Date();
			lastCursedDate.setTime(0L);
		}
		return lastCursedDate;
	}

	public Date getLastBlessedTimeForGod(String godName)
	{
		String lastBlessedString = this.godsConfig.getString(godName + ".BlessedTime");
		if (lastBlessedString == null)
		{
			return null;
		}
		DateFormat formatter = new SimpleDateFormat(this.pattern);
		Date lastBlessedDate = null;
		try
		{
			lastBlessedDate = formatter.parse(lastBlessedString);
		}
		catch (Exception ex)
		{
			lastBlessedDate = new Date();
			lastBlessedDate.setTime(0L);
		}
		return lastBlessedDate;
	}

	public float getGodPower(String godName)
	{
		float godPower = 0.0F;
		int minGodPower = 0;

		String name = this.godsConfig.getString(godName);

		if (name == null)
		{
			return 0.0F;
		}

		Set<UUID> believers = this.plugin.getBelieverManager().getBelieversForGod(godName);

		if (this.plugin.useWhitelist)
		{
			minGodPower = (int) this.plugin.getWhitelistManager().getMinGodPower(godName);
		}

		for (UUID believerId : believers)
		{
			float believerPower = this.plugin.getBelieverManager().getBelieverPower(believerId);

			godPower += believerPower;
		}
		if (godPower < minGodPower)
		{
			godPower = minGodPower;
		}
		return godPower;
	}

	public int getGodLevel(String godName)
	{
		float power = getGodPower(godName);
		if (power < 3.0F)
		{
			return 0;
		}
		if (power < 10.0F)
		{
			return 1;
		}
		return 2;
	}

	private UUID getNextBelieverForPriest(String godName)
	{
		Set<UUID> allBelievers = this.plugin.getBelieverManager().getBelieversForGod(godName);

		List<PriestCandidate> candidates = new ArrayList();

		if (allBelievers.size() == 0)
		{
			this.plugin.logDebug("Did not find any priest candidates");
			return null;
		}

		UUID pendingPriest = getPendingPriest(godName);

		for (UUID candidate : allBelievers)
		{
			Player player = this.plugin.getServer().getPlayer(candidate);
			if (player != null)
			{
				if (!isPriest(candidate))
				{
					if ((pendingPriest == null) || (!pendingPriest.equals(candidate)))
					{
						if (!this.plugin.getBelieverManager().hasRecentPriestOffer(candidate))
						{
							if (this.plugin.getPermissionsManager().hasPermission(player, "gods.priest"))
							{
								candidates.add(new PriestCandidate(candidate));
							}
						}
					}
				}
			}
		}

		if (candidates.size() == 0)
		{
			return null;
		}

		Collections.sort(candidates, new NewPriestComparator());

		PriestCandidate finalCandidate = null;
		if (candidates.size() > 2)
		{
			finalCandidate = (PriestCandidate) candidates.toArray()[this.random.nextInt(3)];
		}
		else
		{
			finalCandidate = (PriestCandidate) candidates.toArray()[0];
		}

		return finalCandidate.believerId;
	}

	public List<UUID> getPriestsForGod(String godName)
	{
		List<String> names = this.godsConfig.getStringList(godName + ".Priests");
		List<UUID> list = new ArrayList();

		if (names == null)
		{
			return null;
		}

		for (String name : names)
		{
			if (name != null && !name.equals("none"))
			{
				Date thisDate = new Date();
				Date lastPrayerDate = this.plugin.getBelieverManager().getLastPrayerTime(UUID.fromString(name));

				UUID believerId = UUID.fromString(name);

				long diff = thisDate.getTime() - lastPrayerDate.getTime();

				long diffHours = diff / 3600000L;
				if (diffHours > this.plugin.maxPriestPrayerTime)
				{
					this.plugin.getLanguageManager().setPlayerName(name);

					godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversRemovedPriest, 2 + this.random.nextInt(40));

					removePriest(godName, believerId);
				}
				else
				{
					list.add(believerId);
				}
			}
		}

		return list;
	}

	public boolean isPriest(UUID believerId)
	{
		if (believerId == null)
		{
			return false;
		}

		Set<String> gods = getAllGods();

		for (String godName : gods)
		{
			List<UUID> list = getPriestsForGod(godName);

			if (list.contains(believerId))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isPriestForGod(UUID believerId, String godName)
	{
		if (believerId == null)
		{
			return false;
		}

		List<UUID> priests = getPriestsForGod(godName);

		if (priests != null && priests.contains(believerId))
		{
			return true;
		}
		return false;
	}

	public UUID getPendingPriest(String godName)
	{
		String believer = this.godsConfig.getString(godName + ".PendingPriest");

		if ((believer == null) || (believer.equals("none")))
		{
			return null;
		}

		Player player = plugin.getServer().getPlayer(UUID.fromString(believer));

		if (player == null)
		{
			return null;
		}

		return player.getUniqueId();
	}

	public String getQuestType(String godName)
	{
		String name = this.godsConfig.getString(godName + ".QuestType");
		if ((name == null) || (name.equals("none")))
		{
			return null;
		}
		return name;
	}

	public String getGodDescription(String godName)
	{
		String description = this.godsConfig.getString(godName + ".Description");
		if (description == null)
		{
			description = new String("No description :/");
		}
		return description;
	}

	public boolean hasGodAccess(UUID believerId, String godName)
	{
		if (!isPrivateAccess(godName))
		{
			return true;
		}

		String currentGodName = this.plugin.getBelieverManager().getGodForBeliever(believerId);

		if ((currentGodName == null) || (!currentGodName.equals(godName)))
		{
			return false;
		}
		return true;
	}

	public void setGodPvP(String godName, boolean pvp)
	{
		this.godsConfig.set(godName + ".PvP", Boolean.valueOf(pvp));

		saveTimed();
	}

	public void setGodMobSpawning(String godName, boolean mobSpawning)
	{
		this.godsConfig.set(godName + ".MobSpawning", Boolean.valueOf(mobSpawning));

		saveTimed();
	}

	public boolean getGodPvP(String godName)
	{
		return (this.plugin.holyLandDefaultPvP) || (this.godsConfig.getBoolean(godName + ".PvP"));
	}

	public boolean getGodMobSpawning(String godName)
	{
		return this.godsConfig.getBoolean(godName + ".MobSpawning");
	}

	public boolean getGodMobDamage(String godName)
	{
		return (this.plugin.holyLandDefaultMobDamage) || (this.godsConfig.getBoolean(godName + ".MobDamage"));
	}

	public void setGodDescription(String godName, String description)
	{
		this.godsConfig.set(godName + ".Description", description);

		saveTimed();
	}

	public void setDivineForceForGod(String godName, GodType divineForce)
	{
		this.godsConfig.set(godName + ".DivineForce", divineForce.name().toUpperCase());

		save();
	}

	public GodType getDivineForceForGod(String godName)
	{
		GodType type = GodType.FROST;
		try
		{
			type = GodType.valueOf(this.godsConfig.getString(godName + ".DivineForce"));
		}
		catch (Exception ex)
		{
			this.plugin.log("Could not parse GodType " + this.godsConfig.getString(new StringBuilder(String.valueOf(godName)).append(".DivineForce").toString()) + " for the god '" + godName + "'. Assigning a random GodType.");
			do
			{
				type = GodType.values()[this.random.nextInt(GodType.values().length)];
			}
			while (type == GodType.WEREWOLVES);
			setDivineForceForGod(godName, type);
		}
		return type;
	}

	private Material getRewardBlessing(String godName)
	{
		if (getGodPower(godName) > this.plugin.godPowerForLevel3Items)
		{
			return Material.DIAMOND;
		}
		if (getGodPower(godName) > this.plugin.godPowerForLevel2Items)
		{
			return Material.GOLD_INGOT;
		}
		if (getGodPower(godName) > this.plugin.godPowerForLevel1Items)
		{
			return Material.CAKE;
		}
		return Material.COAL;
	}

	private Material getPickAxeBlessing(String godName)
	{
		if (getGodPower(godName) > this.plugin.godPowerForLevel3Items)
		{
			return Material.DIAMOND_PICKAXE;
		}
		if (getGodPower(godName) > this.plugin.godPowerForLevel2Items)
		{
			return Material.IRON_PICKAXE;
		}
		if (getGodPower(godName) > this.plugin.godPowerForLevel1Items)
		{
			return Material.STONE_PICKAXE;
		}
		return Material.WOOD_PICKAXE;
	}

	private Material getSpadeBlessing(String godName)
	{
		if (getGodPower(godName) > this.plugin.godPowerForLevel3Items)
		{
			return Material.DIAMOND_SPADE;
		}
		if (getGodPower(godName) > this.plugin.godPowerForLevel2Items)
		{
			return Material.IRON_SPADE;
		}
		if (getGodPower(godName) > this.plugin.godPowerForLevel1Items)
		{
			return Material.STONE_SPADE;
		}
		return Material.WOOD_SPADE;
	}

	private Material getHoeBlessing(String godName)
	{
		if (getGodPower(godName) > this.plugin.godPowerForLevel3Items)
		{
			return Material.DIAMOND_HOE;
		}
		if (getGodPower(godName) > this.plugin.godPowerForLevel2Items)
		{
			return Material.IRON_HOE;
		}
		if (getGodPower(godName) > this.plugin.godPowerForLevel1Items)
		{
			return Material.STONE_HOE;
		}
		return Material.WOOD_HOE;
	}

	private Material getAxeBlessing(String godName)
	{
		if (getGodPower(godName) > this.plugin.godPowerForLevel3Items)
		{
			return Material.DIAMOND_AXE;
		}
		if (getGodPower(godName) > this.plugin.godPowerForLevel2Items)
		{
			return Material.IRON_AXE;
		}
		if (getGodPower(godName) > this.plugin.godPowerForLevel1Items)
		{
			return Material.STONE_AXE;
		}
		return Material.WOOD_AXE;
	}

	private Material getSwordBlessing(String godName)
	{
		if (getGodPower(godName) > this.plugin.godPowerForLevel3Items)
		{
			return Material.DIAMOND_SWORD;
		}
		if (getGodPower(godName) > this.plugin.godPowerForLevel2Items)
		{
			return Material.IRON_SWORD;
		}
		if (getGodPower(godName) > this.plugin.godPowerForLevel1Items)
		{
			return Material.STONE_SWORD;
		}
		return Material.WOOD_SWORD;
	}

	private Material getFoodBlessing(String godName)
	{
		return getEatFoodTypeForGod(godName);
	}

	public int getHealthBlessing(String godName)
	{
		if (getGodPower(godName) > this.plugin.godPowerForLevel3Items)
		{
			return 3;
		}
		if (getGodPower(godName) > this.plugin.godPowerForLevel2Items)
		{
			return 2;
		}
		if (getGodPower(godName) > this.plugin.godPowerForLevel1Items)
		{
			return 1;
		}
		return 0;
	}

	private boolean hasPickAxe(Player player)
	{
		PlayerInventory inventory = player.getInventory();
		if (inventory.contains(Material.WOOD_PICKAXE))
		{
			return true;
		}
		if (inventory.contains(Material.STONE_PICKAXE))
		{
			return true;
		}
		if (inventory.contains(Material.IRON_PICKAXE))
		{
			return true;
		}
		if (inventory.contains(Material.DIAMOND_PICKAXE))
		{
			return true;
		}
		return false;
	}

	private boolean hasSpade(Player player)
	{
		PlayerInventory inventory = player.getInventory();
		if (inventory.contains(Material.WOOD_SPADE))
		{
			return true;
		}
		if (inventory.contains(Material.STONE_SPADE))
		{
			return true;
		}
		if (inventory.contains(Material.IRON_SPADE))
		{
			return true;
		}
		if (inventory.contains(Material.DIAMOND_SPADE))
		{
			return true;
		}
		return false;
	}

	private boolean hasHoe(Player player)
	{
		PlayerInventory inventory = player.getInventory();
		if (inventory.contains(Material.WOOD_HOE))
		{
			return true;
		}
		if (inventory.contains(Material.STONE_HOE))
		{
			return true;
		}
		if (inventory.contains(Material.IRON_HOE))
		{
			return true;
		}
		if (inventory.contains(Material.DIAMOND_HOE))
		{
			return true;
		}
		return false;
	}

	private boolean hasAxe(Player player)
	{
		PlayerInventory inventory = player.getInventory();
		if (inventory.contains(Material.WOOD_AXE))
		{
			return true;
		}
		if (inventory.contains(Material.STONE_AXE))
		{
			return true;
		}
		if (inventory.contains(Material.IRON_AXE))
		{
			return true;
		}
		if (inventory.contains(Material.DIAMOND_AXE))
		{
			return true;
		}
		return false;
	}

	private boolean hasSword(Player player)
	{
		PlayerInventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getSize(); i++)
		{
			ItemStack stack = inventory.getItem(i);
			if ((stack != null) && ((stack.getType().equals(Material.WOOD_SWORD)) || (stack.getType().equals(Material.STONE_SWORD)) || (stack.getType().equals(Material.IRON_SWORD)) || (stack.getType().equals(Material.DIAMOND_SWORD))) && (stack.getAmount() != 0))
			{
				return true;
			}
		}
		return false;
	}

	private boolean hasFood(Player player, String godName)
	{
		PlayerInventory inventory = player.getInventory();
		if (inventory.contains(this.plugin.getGodManager().getEatFoodTypeForGod(godName)))
		{
			return true;
		}
		return false;
	}

	public double getHealthNeed(String godName, Player player)
	{
		return player.getMaxHealth() - player.getHealth();
	}

	private ItemStack getItemNeed(String godName, Player player)
	{
		if (!hasFood(player, godName))
		{
			return new ItemStack(getFoodBlessing(godName));
		}
		if (!hasPickAxe(player))
		{
			return new ItemStack(getPickAxeBlessing(godName));
		}
		if (!hasSword(player))
		{
			return new ItemStack(getSwordBlessing(godName));
		}
		if (!hasSpade(player))
		{
			return new ItemStack(getSpadeBlessing(godName));
		}
		if (!hasAxe(player))
		{
			return new ItemStack(getAxeBlessing(godName));
		}
		if (!hasHoe(player))
		{
			return new ItemStack(getHoeBlessing(godName));
		}
		return null;
	}

	public boolean cursePlayer(String godName, UUID playerId, float godPower)
	{
		Player player = this.plugin.getServer().getPlayer(playerId);

		if (player == null)
		{
			return false;
		}

		if (this.plugin.getBelieverManager().hasRecentCursing(playerId))
		{
			return false;
		}

		int curseType = 0;
		int t = 0;

		do
		{
			curseType = this.random.nextInt(7);
			t++;
		}
		while ((t < 50) && (((curseType == 5) && (!this.plugin.lightningCurseEnabled)) || ((curseType == 6) && (!this.plugin.mobCurseEnabled))));

		float cursePower = 1.0F + godPower / 100.0F;

		switch (curseType)
		{
			case 0:
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) (200.0F * cursePower), 1));
				break;
			case 1:
				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int) (200.0F * cursePower), 1));
				break;
			case 2:
				player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (int) (200.0F * cursePower), 1));
				break;
			case 3:
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (200.0F * cursePower), 1));
				break;
			case 4:
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (int) (200.0F * cursePower), 1));
				break;
			case 5:
				strikePlayerWithLightning(playerId, 1);
				break;
			case 6:
				strikePlayerWithMobs(godName, playerId, godPower);
		}

		this.plugin.getBelieverManager().setCursingTime(player.getUniqueId());

		return true;
	}

	public boolean blessPlayer(String godName, UUID playerId, float godPower)
	{
		Player player = this.plugin.getServer().getPlayer(playerId);

		if (player == null)
		{
			return false;
		}

		if (this.plugin.getBelieverManager().hasRecentBlessing(playerId))
		{
			return false;
		}

		int blessingType = 0;
		int t = 0;

		float blessingPower = 1.0F + godPower / 100.0F;

		do
		{
			blessingType = this.random.nextInt(5);
			t++;
		}
		while ((t < 50) && (((blessingType == 0) && (!this.plugin.fastDiggingBlessingEnabled)) || ((blessingType == 1) && (!this.plugin.healBlessingEnabled)) || ((blessingType == 2) && (!this.plugin.regenerationBlessingEnabled)) || ((blessingType == 3) && (!this.plugin.speedBlessingEnabled)) || ((blessingType == 4) && (!this.plugin.increaseDamageBlessingEnabled))));

		switch (blessingType)
		{
			case 0:
				player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, (int) (300.0F * blessingPower), 1));
				break;
			case 1:
				player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, (int) (300.0F * blessingPower), 1));
				break;
			case 2:
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (int) (300.0F * blessingPower), 1));
				break;
			case 3:
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) (300.0F * blessingPower), 1));
				break;
			case 4:
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (int) (300.0F * blessingPower), 1));
		}

		this.plugin.getBelieverManager().setBlessingTime(player.getUniqueId());

		return true;
	}

	public void spawnGuidingMobs(String godName, UUID playerId, Location targetLocation)
	{
		EntityType mobType = getHolyMobTypeForGod(godName);

		Player player = this.plugin.getServer().getPlayer(playerId);
		if (player == null)
		{
			return;
		}
		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new SpawnGuideMobTask(this.plugin, player, targetLocation, mobType), 2L);
	}

	public void spawnHostileMobs(String godName, Player player, EntityType mobType, int numberOfMobs)
	{
		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new SpawnHostileMobsTask(this.plugin, godName, player, mobType, numberOfMobs), 2L);
	}

	public void giveItem(String godName, Player player, Material material, boolean speak)
	{
		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new GiveItemTask(this.plugin, godName, player, material, speak), 2L);
	}

	public void giveHolyArtifact(String godName, GodType godType, Player player, boolean speak)
	{
		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new GiveHolyArtifactTask(this.plugin, godName, godType, player, speak), 2L);
	}

	public ItemStack blessPlayerWithItem(String godName, Player player)
	{
		if (!this.plugin.isEnabledInWorld(player.getWorld()))
		{
			return null;
		}
		ItemStack item = getItemNeed(godName, player);
		if (item != null)
		{
			giveItem(godName, player, item.getType(), true);
		}
		return item;
	}

	public void blessPlayerWithHolyArtifact(String godName, Player player)
	{
		if (!this.plugin.isEnabledInWorld(player.getWorld()))
		{
			return;
		}
		giveHolyArtifact(godName, getDivineForceForGod(godName), player, true);
	}

	public boolean setPlayerOnFire(String playerName, int seconds)
	{
		for (Player matchPlayer : this.plugin.getServer().matchPlayer(playerName))
		{
			matchPlayer.setFireTicks(seconds);
		}
		return true;
	}

	public boolean strikePlayerWithMobs(String godName, UUID playerId, float godPower)
	{
		Player player = this.plugin.getServer().getPlayer(playerId);

		if (player == null)
		{
			this.plugin.logDebug("player is null");
		}

		EntityType mobType = EntityType.UNKNOWN;

		switch (this.random.nextInt(5))
		{
			case 0:
				mobType = EntityType.SKELETON;
				break;
			case 1:
				mobType = EntityType.ZOMBIE;
				break;
			case 2:
				mobType = EntityType.PIG_ZOMBIE;
				break;
			case 3:
				mobType = EntityType.SPIDER;
				break;
			case 4:
				mobType = EntityType.WOLF;
				break;
			case 5:
				mobType = EntityType.GIANT;
		}
		int numberOfMobs = 1 + (int) (godPower / 10.0F);

		spawnHostileMobs(godName, player, mobType, numberOfMobs);

		return true;
	}

	public boolean strikePlayerWithLightning(UUID playerId, int damage)
	{
		Player player = this.plugin.getServer().getPlayer(playerId);

		if (player != null)
		{
			if (damage <= 0)
			{
				player.getWorld().strikeLightningEffect(player.getLocation());
			}
			else
			{
				LightningStrike strike = player.getWorld().strikeLightning(player.getLocation());
				player.damage(damage - 1, strike);
			}
		}
		return true;
	}

	public boolean strikeCreatureWithLightning(Creature creature, int damage)
	{
		if (damage <= 0)
		{
			creature.getWorld().strikeLightningEffect(creature.getLocation());
		}
		else
		{
			LightningStrike strike = creature.getWorld().strikeLightning(creature.getLocation());
			creature.damage(damage - 1, strike);
		}
		return true;
	}

	public boolean rewardBeliever(String godName, Player believer)
	{
		ItemStack items = new ItemStack(getRewardBlessing(godName));

		giveItem(godName, believer, items.getType(), false);

		return true;
	}

	public void healPlayer(String godName, Player player, double healing)
	{
		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new HealPlayerTask(this.plugin, godName, player, LanguageManager.LANGUAGESTRING.GodToBelieverHealthBlessing), 2L);
	}

	public void believerAccept(UUID believerId)
	{
		String godName = this.plugin.getBelieverManager().getGodForBeliever(believerId);

		Player player = this.plugin.getServer().getPlayer(believerId);
		if (player == null)
		{
			this.plugin.logDebug("believerAccept(): player is null for " + believerId);
			return;
		}

		this.plugin.getLanguageManager().setPlayerName(player.getName());
		if (this.plugin.marriageEnabled)
		{
			UUID pendingMarriagePartner = this.plugin.getMarriageManager().getProposal(believerId);

			if (pendingMarriagePartner != null)
			{
				this.plugin.log(player.getName() + " accepted the proposal to marry " + pendingMarriagePartner);

				this.plugin.getMarriageManager().handleAcceptProposal(believerId, pendingMarriagePartner, godName);

				return;
			}
		}
		String pendingGodInvitation = this.plugin.getBelieverManager().getInvitation(believerId);
		if (pendingGodInvitation != null)
		{
			this.plugin.logDebug("pendingGodInvitation is " + pendingGodInvitation);
			if (addBelief(player, pendingGodInvitation, true))
			{
				this.plugin.getBelieverManager().clearInvitation(believerId);

				this.plugin.log(player.getName() + " accepted the invitation to join " + godName);

				GodSay(pendingGodInvitation, player, LanguageManager.LANGUAGESTRING.GodToPlayerAcceptedInvitation, 2 + this.random.nextInt(40));
				GodSayToBelieversExcept(godName, LanguageManager.LANGUAGESTRING.GodToBelieversNewPlayerAccepted, player.getUniqueId());
			}
			else
			{
				this.plugin.log(player.getName() + " could NOT accept the invitation to join " + godName);
			}
			return;
		}

		UUID pendingPriest = getPendingPriest(godName);

		if (pendingPriest != null)
		{
			if (pendingPriest == believerId)
			{
				assignPriest(godName, believerId);
				saveTimed();

				this.plugin.log(player.getName() + " accepted the offer from " + godName + " to be priest");

				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.InviteHelp, ChatColor.AQUA, ChatColor.WHITE + "/gods invite <playername>", ChatColor.WHITE + "/gods invite <playername>", 100);
				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.FollowersHelp, ChatColor.AQUA, ChatColor.WHITE + "/gods followers", ChatColor.WHITE + "/gods followers", 200);
				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.DescriptionHelp, ChatColor.AQUA, ChatColor.WHITE + "/gods desc", ChatColor.WHITE + "/gods desc", 300);

				if (this.plugin.holyArtifactsEnabled)
				{
					this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.AttackHelp, ChatColor.AQUA, ChatColor.WHITE + "/gods startattack", ChatColor.WHITE + "/gods startattack", 300);
				}
				try
				{
					GodSay(godName, player, LanguageManager.LANGUAGESTRING.GodToPriestPriestAccepted, 2 + this.random.nextInt(40));
					GodSayToBelieversExcept(godName, LanguageManager.LANGUAGESTRING.GodToBelieversPriestAccepted, player.getUniqueId());
				}
				catch (Exception ex)
				{
					this.plugin.log("ERROR: Could not say GodToPriestPriestAccepted text! " + ex.getMessage());
				}
				return;
			}
		}

		this.plugin.logDebug(player.getDisplayName() + " did not have anything to accepted from " + godName);
		GodSay(godName, player, LanguageManager.LANGUAGESTRING.GodToBelieverNoQuestion, 2 + this.random.nextInt(20));
	}

	public void believerReject(UUID believerId)
	{
		String godName = this.plugin.getBelieverManager().getGodForBeliever(believerId);
		Player player = this.plugin.getServer().getPlayer(believerId);

		this.plugin.getLanguageManager().setPlayerName(player.getName());

		String pendingGodInvitation = this.plugin.getBelieverManager().getInvitation(believerId);
		if (pendingGodInvitation != null)
		{
			this.plugin.getBelieverManager().clearInvitation(believerId);
			
			this.plugin.log(player.getName() + " rejected the invitation to join " + pendingGodInvitation);

			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.RejectedJoinOffer, ChatColor.RED, 0, pendingGodInvitation, 20);

			return;
		}

		UUID pendingPriest = getPendingPriest(godName);

		if (pendingPriest == null)
		{
			if (player != null)
			{
				GodSay(godName, player, LanguageManager.LANGUAGESTRING.GodToBelieverNoQuestion, 2 + this.random.nextInt(20));
			}
			return;
		}

		if (getPendingPriest(godName).equals(believerId))
		{
			this.godsConfig.set(godName + ".PendingPriest", null);

			this.plugin.getBelieverManager().clearPendingPriest(believerId);

			if (player != null)
			{
				this.plugin.log(player.getName() + " rejected the offer from " + godName + " to be priest");

				GodSay(godName, player, LanguageManager.LANGUAGESTRING.GodToBelieverPriestRejected, 2 + this.random.nextInt(20));
			}
			saveTimed();
		}
	}

	public void handleReadBible(String godName, Player player)
	{
	}

	public void handleBibleMelee(String godName, Player player)
	{
	}

	public boolean handlePray(Player player, String godName)
	{
		if (!this.plugin.isEnabledInWorld(player.getWorld()))
		{
			return false;
		}

		if (addBelief(player, godName, this.plugin.getBelieverManager().getChangingGod(player.getUniqueId())))
		{
			addMoodForGod(godName, getPleasedModifierForGod(godName));

			this.plugin.getLanguageManager().setPlayerName(player.getName());

			GodSay(godName, player, LanguageManager.LANGUAGESTRING.GodToBelieverPraying, 2 + this.random.nextInt(10));

			player.getLocation().getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 25);

			return true;
		}
		return false;
	}

	public boolean handleAltarPray(Location location, Player player, String godName)
	{
		if (!this.plugin.isEnabledInWorld(player.getWorld()))
		{
			return false;
		}

		if (addBeliefByAltar(player, godName, location, this.plugin.getBelieverManager().getChangingGod(player.getUniqueId())))
		{
			Block altarBlock = this.plugin.getAltarManager().getAltarBlockFromSign(player.getWorld().getBlockAt(location));

			if (this.plugin.getGodManager().getGenderForGod(godName) == GodGender.None)
			{
				GodGender godGender = this.plugin.getAltarManager().getGodGenderFromAltarBlock(altarBlock);

				this.plugin.logDebug("God did not have a gender, setting gender to " + godGender);

				this.plugin.getGodManager().setGenderForGod(godName, godGender);
			}
			if (this.plugin.getGodManager().getDivineForceForGod(godName) == null)
			{
				GodType godType = this.plugin.getAltarManager().getGodTypeForAltarBlockType(altarBlock.getType());

				this.plugin.logDebug("God did not have a divine force, setting divine force to " + godType);

				this.plugin.getGodManager().setDivineForceForGod(godName, godType);
			}

			addMoodForGod(godName, getPleasedModifierForGod(godName));

			if ((this.plugin.holyLandEnabled) && (this.plugin.getPermissionsManager().hasPermission(player, "gods.holyland")))
			{
				this.plugin.getLandManager().setPrayingHotspot(player.getName(), godName, altarBlock.getLocation());
			}

			this.plugin.getQuestManager().handlePrayer(godName, player.getUniqueId());

			this.plugin.getLanguageManager().setPlayerName(player.getName());

			GodSay(godName, player, LanguageManager.LANGUAGESTRING.GodToBelieverPraying, 2 + this.random.nextInt(10));
			location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 25);

			return true;
		}

		return false;
	}

	private boolean addBeliefByAltar(Player player, String godName, Location prayerLocation, boolean allowChangeGod)
	{
		if (!godExist(godName))
		{
			if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.god.create")))
			{
				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.CreateGodNotAllowed, ChatColor.RED, 0, "", 20);
				return false;
			}

			Block altarBlock = this.plugin.getAltarManager().getAltarBlockFromSign(prayerLocation.getBlock());

			GodGender godGender = this.plugin.getAltarManager().getGodGenderFromAltarBlock(altarBlock);

			this.plugin.logDebug("Altar is " + altarBlock.getType().name());

			GodType godType = this.plugin.getAltarManager().getGodTypeForAltarBlockType(altarBlock.getType());

			this.plugin.logDebug("God divine force is " + godType);

			createGod(godName, player.getLocation(), godGender, godType);

			if (this.plugin.broadcastNewGods)
			{
				this.plugin.getServer().broadcastMessage(ChatColor.WHITE + player.getName() + ChatColor.AQUA + " started to believe in the " + this.plugin.getLanguageManager().getGodGenderName(getGenderForGod(godName)) + " " + ChatColor.GOLD + godName);
			}

			this.plugin.log(player.getName() + " created new god " + godName);
		}

		return addBelief(player, godName, allowChangeGod);
	}

	private boolean addBelief(Player player, String godName, boolean allowChangeGod)
	{
		String oldGodName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

		if (godName == null)
		{
			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.InvalidGodName, ChatColor.RED, 0, "", 1);
			return false;
		}

		if (oldGodName != null && !oldGodName.equals(godName))
		{
			if (!allowChangeGod)
			{
				this.plugin.getBelieverManager().setChangingGod(player.getUniqueId());

				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.ConfirmChangeToOtherReligion, ChatColor.YELLOW, 0, oldGodName, 1);
				return false;
			}

			this.plugin.getBelieverManager().clearChangingGod(player.getUniqueId());
		}

		if (!this.plugin.getBelieverManager().addPrayer(player.getUniqueId(), godName))
		{
			int timeUntilCanPray = this.plugin.getBelieverManager().getTimeUntilCanPray(player.getUniqueId());

			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.CannotPraySoSoon, ChatColor.RED, timeUntilCanPray, "", 1);
			return false;
		}

		if (oldGodName != null && !oldGodName.equals(godName))
		{
			if (isPriestForGod(player.getUniqueId(), oldGodName))
			{
				removePriest(oldGodName, player.getUniqueId());
			}

			this.plugin.getLanguageManager().setPlayerName(player.getName());

			godSayToBelievers(oldGodName, LanguageManager.LANGUAGESTRING.GodToBelieversPlayerLeftReligion, 2 + this.random.nextInt(20));

			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.YouLeftReligion, ChatColor.RED, 0, oldGodName, 20);

			GodSayToBelieversExcept(godName, LanguageManager.LANGUAGESTRING.GodToBelieversPlayerJoinedReligion, player.getUniqueId());

			this.plugin.getBelieverManager().clearPrayerPower(player.getUniqueId());
		}
		else
		{
			Material foodType = getEatFoodTypeForGod(godName);

			try
			{
				this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getItemTypeName(foodType));
			}
			catch (Exception ex)
			{
				this.plugin.logDebug(ex.getStackTrace().toString());
			}

			giveItem(godName, player, foodType, false);

			this.plugin.getBelieverManager().increasePrayerPower(player.getUniqueId(), 1);
		}

		if (oldGodName == null || !oldGodName.equals(godName))
		{
			if (this.plugin.marriageEnabled)
			{
				this.plugin.getMarriageManager().divorce(player.getUniqueId());
			}
			this.plugin.getQuestManager().handleJoinReligion(player.getName(), godName);
		}

		return true;
	}

	public boolean addAltar(Player player, String godName, Location location)
	{
		addBeliefByAltar(player, godName, location, true);

		this.plugin.getLanguageManager().setPlayerName(player.getName());

		GodSay(godName, player, LanguageManager.LANGUAGESTRING.GodToBelieverAltarBuilt, 2 + this.random.nextInt(30));

		return true;
	}

	public static String parseBelief(String message)
	{
		return null;
	}

	public void assignPriest(String godName, UUID playerId)
	{
		this.godsConfig.set(godName + ".PendingPriest", null);
		this.plugin.getBelieverManager().clearPendingPriest(playerId);

		this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), this.plugin.getLanguageManager().getPriestAssignCommand(playerId));

		List<String> priests = this.godsConfig.getStringList(godName + ".Priests");

		priests.add(playerId.toString());

		this.godsConfig.set(godName + ".Priests", priests);

		this.godsConfig.set(godName + ".PendingPriest", null);
		this.godsConfig.set(godName + ".PendingPriestTime", null);

		this.plugin.getBelieverManager().setLastPrayerDate(playerId);

		saveTimed();
	}

	public void removePriest(String godName, UUID playerId)
	{
		this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), this.plugin.getLanguageManager().getPriestRemoveCommand(playerId));

		List<String> priests = this.godsConfig.getStringList(godName + ".Priests");

		priests.remove(playerId.toString());

		this.godsConfig.set(godName + ".Priests", priests);

		saveTimed();

		this.plugin.log(godName + " removed " + plugin.getServer().getOfflinePlayer(playerId).getName() + " as priest");
	}

	public boolean removeBeliever(UUID believerId)
	{
		String godName = this.plugin.getBelieverManager().getGodForBeliever(believerId);

		if (godName == null)
		{
			return false;
		}

		if (isPriestForGod(believerId, godName))
		{
			removePriest(godName, believerId);
		}

		this.plugin.getBelieverManager().removeBeliever(godName, believerId);

		this.plugin.getLanguageManager().setPlayerName(plugin.getServer().getOfflinePlayer(believerId).getName());
		godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversLostBeliever, 2 + this.random.nextInt(100));

		return true;
	}

	public boolean believerLeaveGod(UUID believerId)
	{
		String godName = this.plugin.getBelieverManager().getGodForBeliever(believerId);
		if (godName == null)
		{
			return false;
		}

		if (isPriestForGod(believerId, godName))
		{
			removePriest(godName, believerId);
		}
		this.plugin.getBelieverManager().believerLeave(godName, believerId);

		this.plugin.getLanguageManager().setPlayerName(plugin.getServer().getPlayer(believerId).getDisplayName());

		if (this.plugin.marriageEnabled)
		{
			this.plugin.getMarriageManager().divorce(believerId);
		}

		this.plugin.getBelieverManager().clearPrayerPower(believerId);

		godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversPlayerLeftReligion, 2 + this.random.nextInt(20));

		return true;
	}

	public void removeGod(String godName)
	{
		for (String otherGodName : getAllGods())
		{
			if (hasAllianceRelation(otherGodName, godName))
			{
				toggleAllianceRelationForGod(otherGodName, godName);
			}

			if (hasWarRelation(otherGodName, godName))
			{
				toggleWarRelationForGod(otherGodName, godName);
			}
		}

		this.godsConfig.set(godName, null);

		this.plugin.getBibleManager().clearBible(godName);

		save();
	}

	public void addBeliefAndRewardBelievers(String godName)
	{
		for (UUID playerId : this.plugin.getBelieverManager().getBelieversForGod(godName))
		{
			Player player = this.plugin.getServer().getPlayer(playerId);

			if (player == null)
			{
				continue;
			}

			this.plugin.getBelieverManager().incPrayer(player.getUniqueId(), godName);

			List<ItemStack> rewards = this.plugin.getQuestManager().getRewardsForQuestCompletion(godName);

			for (ItemStack items : rewards)
			{
				giveItem(godName, player, items.getType(), false);
			}
		}
	}

	public void GodSayToPriest(String godName, LanguageManager.LANGUAGESTRING message)
	{
		List<UUID> priests = getPriestsForGod(godName);
		if (priests == null)
		{
			return;
		}

		for (UUID priest : priests)
		{
			Player player = this.plugin.getServer().getPlayer(priest);
			if (player != null)
			{
				GodSay(godName, player, message, 2 + this.random.nextInt(30));
			}
		}
	}

	public void GodsSayToBelievers(LanguageManager.LANGUAGESTRING message, int delay)
	{
		for (String godName : getOnlineGods())
		{
			godSayToBelievers(godName, message, delay);
		}
	}

	public void godSayToBelievers(String godName, LanguageManager.LANGUAGESTRING message, int delay)
	{
		for (UUID playerId : this.plugin.getBelieverManager().getBelieversForGod(godName))
		{
			Player player = this.plugin.getServer().getPlayer(playerId);
			if (player != null)
			{
				GodSay(godName, player, message, delay);
			}
		}
	}

	public void sendInfoToBelievers(String godName, LanguageManager.LANGUAGESTRING message, ChatColor color, int delay)
	{
		for (UUID playerId : this.plugin.getBelieverManager().getBelieversForGod(godName))
		{
			Player player = this.plugin.getServer().getPlayer(playerId);

			if (player != null)
			{
				this.plugin.sendInfo(playerId, message, color, 0, "", 10);
			}
		}
	}

	public void sendInfoToBelievers(String godName, LanguageManager.LANGUAGESTRING message, ChatColor color, String name, int amount1, int amount2, int delay)
	{
		for (UUID playerId : this.plugin.getBelieverManager().getBelieversForGod(godName))
		{
			Player player = this.plugin.getServer().getPlayer(playerId);
			if (player != null)
			{
				this.plugin.sendInfo(playerId, message, color, name, amount1, amount2, 10);
			}
		}
	}

	public void OtherGodSayToBelievers(String godName, LanguageManager.LANGUAGESTRING message, int delay)
	{
		for (Player player : this.plugin.getServer().getOnlinePlayers())
		{
			String playerGod = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

			if (playerGod != null && !playerGod.equals(godName))
			{
				GodSay(godName, player, message, delay);
			}
		}
	}

	public void GodSayToBelieversExcept(String godName, LanguageManager.LANGUAGESTRING message, UUID exceptPlayer)
	{
		for (UUID playerId : this.plugin.getBelieverManager().getBelieversForGod(godName))
		{
			Player player = this.plugin.getServer().getPlayer(playerId);

			if (player != null && player.getUniqueId() != exceptPlayer)
			{
				GodSay(godName, player, message, 2 + this.random.nextInt(20));
			}
		}
	}

	public void godSayToBeliever(String godName, UUID playerId, LanguageManager.LANGUAGESTRING message)
	{
		godSayToBeliever(godName, playerId, message, 2 + this.random.nextInt(10));
	}

	public void godSayToBeliever(String godName, UUID playerId, LanguageManager.LANGUAGESTRING message, int delay)
	{
		Player player = this.plugin.getServer().getPlayer(playerId);

		if (player == null)
		{
			this.plugin.logDebug("GodSayToBeliever player for " + player.getDisplayName() + " is null");
			return;
		}
		GodSay(godName, player, message, delay);
	}

	public void GodSayWithQuestion(String godName, Player player, LanguageManager.LANGUAGESTRING message, int delay)
	{
		String playerNameString = this.plugin.getLanguageManager().getPlayerName();
		String typeNameString = this.plugin.getLanguageManager().getType();
		int amount = this.plugin.getLanguageManager().getAmount();

		if (player == null)
		{
			this.plugin.logDebug("GodSay(): Player is null!");
			return;
		}
		if (!this.plugin.isEnabledInWorld(player.getWorld()))
		{
			return;
		}
		this.plugin.logDebug(godName + " to " + player.getName() + ": " + this.plugin.getLanguageManager().getLanguageString(godName, message));
		if (!this.plugin.getPermissionsManager().hasPermission(player, "gods.listen"))
		{
			return;
		}

		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new GodSpeakTask(this.plugin, godName, player.getUniqueId(), playerNameString, typeNameString, amount, message), delay);

		this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverQuestionHelp, ChatColor.AQUA, ChatColor.WHITE + "/gods yes or /gods no", ChatColor.WHITE + "/gods yes or /gods no", delay + 80);
	}

	public void GodSay(String godName, Player player, LanguageManager.LANGUAGESTRING message, int delay)
	{
		String playerNameString = this.plugin.getLanguageManager().getPlayerName();
		String typeNameString = this.plugin.getLanguageManager().getType();
		int amount = this.plugin.getLanguageManager().getAmount();

		if (player == null)
		{
			this.plugin.logDebug("GodSay(): Player is null!");
			return;
		}

		if (!this.plugin.isEnabledInWorld(player.getWorld()))
		{
			return;
		}

		this.plugin.logDebug(godName + " to " + player.getName() + ": " + this.plugin.getLanguageManager().getLanguageString(godName, message));

		if (!this.plugin.getPermissionsManager().hasPermission(player, "gods.listen"))
		{
			return;
		}

		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new GodSpeakTask(this.plugin, godName, player.getUniqueId(), playerNameString, typeNameString, amount, message), delay);
	}

	public boolean isDeadGod(String godName)
	{
		if ((this.plugin.getBelieverManager().getBelieversForGod(godName).size() == 0) && (this.plugin.getGodManager().getGodPower(godName) < 1.0F))
		{
			removeGod(godName);

			return true;
		}
		return false;
	}

	public boolean managePriests(String godName)
	{
		int numberOfBelievers = this.plugin.getBelieverManager().getBelieversForGod(godName).size();

		List<UUID> priestNames = getPriestsForGod(godName);

		if (priestNames == null)
		{
			priestNames = new ArrayList<UUID>();
		}

		if (numberOfBelievers < this.plugin.minBelieversForPriest + 6 * priestNames.size())
		{
			return false;
		}

		if (priestNames.size() < this.plugin.maxPriestsPrGod)
		{
			if (this.random.nextInt(3) == 0)
			{
				this.plugin.logDebug(godName + " has too few priests. Finding one...");

				UUID believerId = getNextBelieverForPriest(godName);
				if (believerId == null)
				{
					this.plugin.logDebug(godName + " could not find a candidate for a priest");
					return false;
				}

				Player player = this.plugin.getServer().getPlayer(believerId);

				if (player == null)
				{
					return false;
				}

				if (setPendingPriest(godName, believerId))
				{
					this.plugin.log(godName + " offered " + player.getName() + " to be priest");
					this.plugin.getLanguageManager().setPlayerName(player.getName());

					GodSayWithQuestion(godName, player, LanguageManager.LANGUAGESTRING.GodToBelieverOfferPriest, 2);

					return true;
				}
			}
		}

		for (UUID priestId : priestNames)
		{
			if (this.random.nextInt(1 + 1000 / getVerbosityForGod(godName)) == 0)
			{
				Player player = this.plugin.getServer().getPlayer(priestId);

				if (player != null)
				{
					this.plugin.getLanguageManager().setPlayerName(player.getDisplayName());
					int r = 0;
					int t = 0;
					do
					{
						r = this.random.nextInt(3);
						t++;
					}
					while ((t < 50) && (((r == 1) && (!this.plugin.biblesEnabled)) || ((r == 2) && (!this.plugin.propheciesEnabled))));
					try
					{
						switch (r)
						{
							case 0:
								switch (this.random.nextInt(4))
								{
									case 0:
										this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getItemTypeName(getEatFoodTypeForGod(godName)));
										GodSayToPriest(godName, LanguageManager.LANGUAGESTRING.GodToPriestEatFoodType);
										break;
									case 1:
										this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getItemTypeName(getNotEatFoodTypeForGod(godName)));
										GodSayToPriest(godName, LanguageManager.LANGUAGESTRING.GodToPriestNotEatFoodType);
										break;
									case 2:
										this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getMobTypeName(getUnholyMobTypeForGod(godName)));
										GodSayToPriest(godName, LanguageManager.LANGUAGESTRING.GodToPriestSlayMobType);
										break;
									case 3:
										this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getMobTypeName(getHolyMobTypeForGod(godName)));
										GodSayToPriest(godName, LanguageManager.LANGUAGESTRING.GodToPriestNotSlayMobType);
								}
								return true;
							case 1:
								if (this.plugin.biblesEnabled)
								{
									String bibleTitle = this.plugin.getBibleManager().getBibleTitle(godName);
									this.plugin.getLanguageManager().setType(bibleTitle);
									GodSayToPriest(godName, LanguageManager.LANGUAGESTRING.GodToPriestUseBible);
									return true;
								}
								break;
							case 2:
								if (this.plugin.propheciesEnabled)
								{
									String bibleTitle = this.plugin.getBibleManager().getBibleTitle(godName);
									try
									{
										this.plugin.getLanguageManager().setType(bibleTitle);
									}
									catch (Exception ex)
									{
										this.plugin.logDebug(ex.getStackTrace().toString());
									}
									GodSayToPriest(godName, LanguageManager.LANGUAGESTRING.GodToPriestUseProphecies);
									return true;
								}
								break;
							case 3:
								if (this.plugin.holyArtifactsEnabled)
								{
									String bibleTitle = this.plugin.getBibleManager().getBibleTitle(godName);
									try
									{
										this.plugin.getLanguageManager().setType(bibleTitle);
									}
									catch (Exception ex)
									{
										this.plugin.logDebug(ex.getStackTrace().toString());
									}
									return true;
								}
								break;
							case 4:
								if (this.plugin.marriageEnabled)
								{
									String bibleTitle = this.plugin.getBibleManager().getBibleTitle(godName);
									this.plugin.getLanguageManager().setType(bibleTitle);

									return true;
								}
								break;
						}
					}
					catch (Exception ex)
					{
						this.plugin.logDebug(ex.getStackTrace().toString());
					}
				}
			}
		}
		return false;
	}

	private void manageMood(String godName)
	{
		if (this.plugin.getBelieverManager().getOnlineBelieversForGod(godName).size() == 0)
		{
			return;
		}
		this.plugin.getGodManager().addMoodForGod(godName, this.plugin.getGodManager().getFalloffModifierForGod(godName));
	}

	private boolean manageBelieverForExaltedGod(String godName, Player believer)
	{
		if (believer == null)
		{
			return false;
		}

		if (!this.plugin.isEnabledInWorld(believer.getWorld()))
		{
			return false;
		}

		if ((believer.getGameMode() != GameMode.CREATIVE) && this.plugin.getPermissionsManager().hasPermission(believer, "gods.itemblessings"))
		{
			if (!this.plugin.getBelieverManager().hasRecentItemBlessing(believer.getUniqueId()))
			{
				if (this.plugin.itemBlessingEnabled)
				{
					float power = getGodPower(godName);

					if (power >= this.plugin.minGodPowerForItemBlessings && this.random.nextInt((int) (1.0F + 50.0F / power)) == 0)
					{
						double healing = getHealthNeed(godName, believer);

						if ((healing > 1.0D) && (this.random.nextInt(3) == 0))
						{
							healPlayer(godName, believer, getHealthBlessing(godName));

							this.plugin.getBelieverManager().setItemBlessingTime(believer.getUniqueId());

							return true;
						}

						ItemStack blessedItem = blessPlayerWithItem(godName, believer);

						if (blessedItem != null)
						{
							this.plugin.getLanguageManager().setPlayerName(believer.getDisplayName());
							try
							{
								this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getItemTypeName(blessedItem.getType()));
							}
							catch (Exception ex)
							{
								this.plugin.logDebug(ex.getStackTrace().toString());
							}

							this.plugin.getBelieverManager().setItemBlessingTime(believer.getUniqueId());

							return true;
						}
					}
				}
			}

			if (this.plugin.holyArtifactsEnabled)
			{
				if (!this.plugin.getBelieverManager().hasRecentHolyArtifactBlessing(believer.getUniqueId()))
				{
					float power = getGodPower(godName);

					if ((power >= this.plugin.minGodPowerForItemBlessings) && (this.random.nextInt((int) (1.0F + 100.0F / power)) == 0))
					{
						blessPlayerWithHolyArtifact(godName, believer);

						this.plugin.getLanguageManager().setPlayerName(believer.getDisplayName());
						this.plugin.getBelieverManager().setHolyArtifactBlessingTime(believer.getUniqueId());

						return true;
					}
				}
			}
		}

		if (!this.plugin.getBelieverManager().hasRecentItemBlessing(believer.getUniqueId()))
		{
			if (blessPlayer(godName, believer.getUniqueId(), getGodPower(godName)))
			{
				this.plugin.getLanguageManager().setPlayerName(believer.getDisplayName());

				GodSay(godName, believer, LanguageManager.LANGUAGESTRING.GodToPlayerBlessed, 2 + this.random.nextInt(10));

				GodSayToBelieversExcept(godName, LanguageManager.LANGUAGESTRING.GodToBelieversPlayerBlessed, believer.getUniqueId());

				return true;
			}
		}

		if (this.plugin.marriageEnabled && this.random.nextInt(501) == 0)
		{
			List<MarriageManager.MarriedCouple> marriedCouples = this.plugin.getMarriageManager().getMarriedCouples();
			if (marriedCouples.size() > 0)
			{
				MarriageManager.MarriedCouple couple = (MarriageManager.MarriedCouple) marriedCouples.get(this.random.nextInt(marriedCouples.size()));

				this.plugin.getLanguageManager().setPlayerName(plugin.getServer().getOfflinePlayer(couple.player1Id).getName() + " and " + plugin.getServer().getOfflinePlayer(couple.player2Id).getName());
				godSayToBeliever(godName, believer.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverMarriedCouple);
				return true;
			}
		}

		if (this.random.nextInt(1 + 1000 / getVerbosityForGod(godName)) == 0)
		{
			if ((this.plugin.getBelieverManager().hasRecentPrayer(believer.getUniqueId())) && (this.random.nextInt(2) == 0))
			{
				return false;
			}
			godSayToBeliever(godName, believer.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverRandomExaltedSpeech);
			return true;
		}

		if (this.random.nextInt(1 + 600 / getVerbosityForGod(godName)) == 0)
		{
			if (godSayNeededSacrificeToBeliever(godName, believer.getUniqueId()))
			{
				return true;
			}
		}
		return false;
	}

	private boolean manageBelieverForPleasedGod(String godName, Player believer)
	{
		if (believer == null)
		{
			return false;
		}

		if (!this.plugin.isEnabledInWorld(believer.getWorld()))
		{
			return false;
		}

		if (believer.getGameMode() != GameMode.CREATIVE && this.plugin.getPermissionsManager().hasPermission(believer, "gods.itemblessings"))
		{
			if (!this.plugin.getBelieverManager().hasRecentItemBlessing(believer.getUniqueId()))
			{
				if (this.plugin.itemBlessingEnabled)
				{
					float power = getGodPower(godName);
					if ((power >= this.plugin.minGodPowerForItemBlessings) && (this.random.nextInt((int) (1.0F + 100.0F / power)) == 0))
					{
						double healing = getHealthNeed(godName, believer);
						if ((healing > 1.0D) && (this.random.nextInt(2) == 0))
						{
							healPlayer(godName, believer, getHealthBlessing(godName));

							this.plugin.getBelieverManager().setItemBlessingTime(believer.getUniqueId());

							return true;
						}

						ItemStack blessedItem = blessPlayerWithItem(godName, believer);

						if (blessedItem != null)
						{
							this.plugin.getLanguageManager().setPlayerName(believer.getDisplayName());
							try
							{
								this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getItemTypeName(blessedItem.getType()));
							}
							catch (Exception ex)
							{
								this.plugin.logDebug(ex.getStackTrace().toString());
							}
							this.plugin.getBelieverManager().setItemBlessingTime(believer.getUniqueId());

							return true;
						}
					}
				}
			}
		}

		if ((this.plugin.marriageEnabled) && (this.random.nextInt(501) == 0))
		{
			List<MarriageManager.MarriedCouple> marriedCouples = this.plugin.getMarriageManager().getMarriedCouples();

			if (marriedCouples.size() > 0)
			{
				MarriageManager.MarriedCouple couple = (MarriageManager.MarriedCouple) marriedCouples.get(this.random.nextInt(marriedCouples.size()));

				this.plugin.getLanguageManager().setPlayerName(plugin.getServer().getOfflinePlayer(couple.player1Id).getName() + " and " + plugin.getServer().getOfflinePlayer(couple.player2Id).getName());
				godSayToBeliever(godName, believer.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverMarriedCouple);
				return true;
			}
		}

		if (this.random.nextInt(1 + 1000 / getVerbosityForGod(godName)) == 0)
		{
			if ((this.plugin.getBelieverManager().hasRecentPrayer(believer.getUniqueId())) && (this.random.nextInt(2) == 0))
			{
				return false;
			}

			godSayToBeliever(godName, believer.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverRandomPleasedSpeech);

			return true;
		}

		if (this.random.nextInt(1 + 600 / getVerbosityForGod(godName)) == 0)
		{
			if (godSayNeededSacrificeToBeliever(godName, believer.getUniqueId()))
			{
				return true;
			}
		}
		return false;
	}

	private boolean manageBelieverForNeutralGod(String godName, Player believer)
	{
		if (believer == null)
		{
			return false;
		}

		if (!this.plugin.isEnabledInWorld(believer.getWorld()))
		{
			return false;
		}
		if ((this.plugin.marriageEnabled) && (this.random.nextInt(501) == 0))
		{
			List<MarriageManager.MarriedCouple> marriedCouples = this.plugin.getMarriageManager().getMarriedCouples();
			if (marriedCouples.size() > 0)
			{
				MarriageManager.MarriedCouple couple = (MarriageManager.MarriedCouple) marriedCouples.get(this.random.nextInt(marriedCouples.size()));

				this.plugin.getLanguageManager().setPlayerName(plugin.getServer().getOfflinePlayer(couple.player1Id).getName() + " and " + plugin.getServer().getOfflinePlayer(couple.player2Id).getName());
				godSayToBeliever(godName, believer.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverMarriedCouple);
				return true;
			}
		}
		if (this.random.nextInt(1 + 1000 / getVerbosityForGod(godName)) == 0)
		{
			if ((this.plugin.getBelieverManager().hasRecentPrayer(believer.getUniqueId())) && (this.random.nextInt(2) == 0))
			{
				return false;
			}
			godSayToBeliever(godName, believer.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverRandomNeutralSpeech);
			return true;
		}

		if (this.random.nextInt(1 + 600 / getVerbosityForGod(godName)) == 0)
		{
			if (godSayNeededSacrificeToBeliever(godName, believer.getUniqueId()))
			{
				return true;
			}
		}
		return false;
	}

	private boolean manageBelieverForDispleasedGod(String godName, Player believer)
	{
		if (believer == null)
		{
			return false;
		}
		if (!this.plugin.isEnabledInWorld(believer.getWorld()))
		{
			return false;
		}
		if (this.random.nextInt(1 + 1000 / getVerbosityForGod(godName)) == 0)
		{
			if ((this.plugin.getBelieverManager().hasRecentPrayer(believer.getUniqueId())) && (this.random.nextInt(2) == 0))
			{
				return false;
			}
			godSayToBeliever(godName, believer.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverRandomDispleasedSpeech);
			return true;
		}
		if (this.random.nextInt(1 + 600 / getVerbosityForGod(godName)) == 0)
		{
			if (godSayNeededSacrificeToBeliever(godName, believer.getUniqueId()))
			{
				return true;
			}
		}
		return false;
	}

	private boolean manageBelieverForAngryGod(String godName, Player believer)
	{
		if (!this.plugin.isEnabledInWorld(believer.getWorld()))
		{
			return false;
		}

		int godPower = 1 + (int) this.plugin.getGodManager().getGodPower(godName);

		if (this.random.nextInt(1 + 1000 / godPower) == 0)
		{
			if (this.plugin.getBelieverManager().hasRecentPrayer(believer.getUniqueId()))
			{
				return false;
			}

			if (cursePlayer(godName, believer.getUniqueId(), godPower))
			{
				this.plugin.getLanguageManager().setPlayerName(believer.getDisplayName());

				GodSay(godName, believer, LanguageManager.LANGUAGESTRING.GodToBelieverCursedAngry, 2 + this.random.nextInt(10));

				return true;
			}
		}

		if (this.random.nextInt(1 + 1000 / getVerbosityForGod(godName)) == 0)
		{
			if ((this.plugin.getBelieverManager().hasRecentPrayer(believer.getUniqueId())) && (this.random.nextInt(2) == 0))
			{
				return false;
			}
			godSayToBeliever(godName, believer.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverRandomAngrySpeech);
			return true;
		}

		if (this.random.nextInt(1 + 600 / getVerbosityForGod(godName)) == 0)
		{
			if (godSayNeededSacrificeToBeliever(godName, believer.getUniqueId()))
			{
				return true;
			}
		}
		return false;
	}

	private boolean godSayNeededSacrificeToBeliever(String godName, UUID believerId)
	{
		if (this.plugin.sacrificesEnabled)
		{
			Material itemType = getSacrificeItemTypeForGod(godName);
			if (itemType != null)
			{
				String itemName = this.plugin.getLanguageManager().getItemTypeName(itemType);
				try
				{
					this.plugin.getLanguageManager().setType(itemName);
				}
				catch (Exception ex)
				{
					this.plugin.logDebug(ex.getStackTrace().toString());
				}

				godSayToBeliever(godName, believerId, LanguageManager.LANGUAGESTRING.GodToBelieversSacrificeItemType);

				return true;
			}
		}
		return false;
	}

	private void manageLostBelievers(String godName)
	{
		if (this.random.nextInt(100) > 0)
		{
			return;
		}

		Set<UUID> believers = this.plugin.getBelieverManager().getBelieversForGod(godName);
		Set<UUID> managedBelievers = new HashSet();

		if (believers.size() == 0)
		{
			return;
		}

		this.plugin.logDebug("Managing lost believers for " + godName);

		for (int n = 0; n < 5; n++)
		{
			UUID believerId = (UUID) believers.toArray()[this.random.nextInt(believers.size())];
			if (!managedBelievers.contains(believerId))
			{
				Date thisDate = new Date();

				long timeDiff = thisDate.getTime() - this.plugin.getBelieverManager().getLastPrayerTime(believerId).getTime();

				if (timeDiff > 3600000 * this.plugin.maxBelieverPrayerTime)
				{
					String believerName = plugin.getServer().getOfflinePlayer(believerId).getName();
					this.plugin.getLanguageManager().setPlayerName(believerName);

					godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversLostBeliever, 2 + this.random.nextInt(100));

					this.plugin.getBelieverManager().removeBeliever(godName, believerId);
				}
			}

			managedBelievers.add(believerId);
		}
	}

	private void manageBelievers(String godName)
	{
		Set<UUID> believers = this.plugin.getBelieverManager().getOnlineBelieversForGod(godName);
		Set<UUID> managedBelievers = new HashSet();
		if (believers.size() == 0)
		{
			return;
		}

		GodMood godMood = getMoodForGod(godName);

		List<UUID> priests = getPriestsForGod(godName);
		for (int n = 0; n < 10; n++)
		{
			UUID believerId = (UUID) believers.toArray()[this.random.nextInt(believers.size())];

			if (!managedBelievers.contains(believerId))
			{
				if (priests.size() == 0)
				{
					this.plugin.getLanguageManager().setPlayerName("our priest");
				}
				else
				{
					UUID priest = priests.get(this.random.nextInt(priests.size()));

					if (priest != null)
					{
						this.plugin.getLanguageManager().setPlayerName(plugin.getServer().getOfflinePlayer(priest).getName());
					}
				}

				Player believer = plugin.getServer().getPlayer(believerId);

				switch (godMood)
				{
					case ANGRY:
						manageBelieverForExaltedGod(godName, believer);
						break;
					case DISPLEASED:
						manageBelieverForPleasedGod(godName, believer);
						break;
					case EXALTED:
						manageBelieverForNeutralGod(godName, believer);
						break;
					case NEUTRAL:
						manageBelieverForDispleasedGod(godName, believer);
						break;
					case PLEASED:
						manageBelieverForAngryGod(godName, believer);
				}

				managedBelievers.add(believerId);
			}
		}
	}

	private void manageCurses(String godName)
	{
		if (!this.plugin.cursingEnabled)
		{
			return;
		}

		Player cursedPlayer = getCursedPlayerForGod(godName);

		if (cursedPlayer == null)
		{
			return;
		}

		int godPower = 1 + (int) this.plugin.getGodManager().getGodPower(godName);

		if (this.random.nextInt(1 + 100 / godPower) == 0)
		{
			if (!this.plugin.getPermissionsManager().hasPermission(cursedPlayer, "gods.curses"))
			{
				return;
			}

			if (cursePlayer(godName, cursedPlayer.getUniqueId(), godPower))
			{
				this.plugin.getLanguageManager().setPlayerName(cursedPlayer.getDisplayName());

				GodSay(godName, cursedPlayer, LanguageManager.LANGUAGESTRING.GodToPlayerCursed, 2 + this.random.nextInt(10));

				GodSayToBelieversExcept(godName, LanguageManager.LANGUAGESTRING.GodToBelieversPlayerCursed, cursedPlayer.getUniqueId());
			}
		}
	}

	private void manageBlessings(String godName)
	{
		if (!this.plugin.blessingEnabled)
		{
			return;
		}
		String blessedPlayer = getBlessedPlayerForGod(godName);
		if (blessedPlayer == null)
		{
			return;
		}

		int godPower = 1 + (int) getGodPower(godName);

		if (this.random.nextInt(1 + 100 / godPower) == 0)
		{
			Player player = this.plugin.getServer().getPlayer(blessedPlayer);

			if ((player == null) || (!this.plugin.getPermissionsManager().hasPermission(player, "gods.blessings")))
			{
				return;
			}

			if (blessPlayer(godName, player.getUniqueId(), getGodPower(godName)))
			{
				this.plugin.getLanguageManager().setPlayerName(blessedPlayer);

				GodSay(godName, player, LanguageManager.LANGUAGESTRING.GodToPlayerBlessed, 2 + this.random.nextInt(10));

				GodSayToBelieversExcept(godName, LanguageManager.LANGUAGESTRING.GodToBelieversPlayerBlessed, player.getUniqueId());
			}
		}
	}

	private void manageQuests(String godName)
	{
		if (!this.plugin.questsEnabled)
		{
			return;
		}

		int numberOfBelievers = this.plugin.getBelieverManager().getOnlineBelieversForGod(godName).size();

		if (!this.plugin.getQuestManager().hasQuest(godName))
		{
			if (numberOfBelievers < this.plugin.requiredBelieversForQuests || this.getMinutesSinceLastQuest(godName) < this.plugin.minMinutesBetweenQuests)
			{
				return;
			}

			this.plugin.getQuestManager().generateQuest(godName);
		}
		else if (this.plugin.getQuestManager().hasExpiredQuest(godName))
		{
			addMoodForGod(godName, getAngryModifierForGod(godName));

			this.plugin.getQuestManager().godSayFailed(godName);

			this.plugin.getQuestManager().removeFailedQuestForGod(godName);
		}
		else if (random.nextInt(5) == 0)
		{
			this.plugin.getQuestManager().godSayStatus(godName);
		}
	}

	private Material getSacrificeNeedForGod(String godName)
	{
		Random materialRandom = new Random(getSeedForGod(godName));
		List<Integer> materials = new ArrayList();

		for (int n = 0; n < 5; n++)
		{
			materials.add(materialRandom.nextInt(24));
		}

		int typeIndex = 0;
		Material type = Material.AIR;

		do
		{
			typeIndex = ((Integer) materials.get(this.random.nextInt(materials.size()))).intValue();

			switch (typeIndex)
			{
				case 0:
					type = Material.RED_ROSE;
					break;
				case 1:
					type = Material.LEAVES;
					break;
				case 2:
					type = getNotEatFoodTypeForGod(godName);
					break;
				case 3:
					type = Material.RABBIT_HIDE;
					break;
				case 4:
					type = Material.RABBIT_FOOT;
					break;
				case 5:
					type = Material.CACTUS;
					break;
				case 6:
					type = Material.BREAD;
					break;
				case 7:
					type = Material.CARROT;
					break;
				case 8:
					type = Material.IRON_PICKAXE;
					break;
				case 9:
					type = Material.IRON_INGOT;
					break;
				case 10:
					type = Material.GOLD_INGOT;
					break;
				case 11:
					type = Material.APPLE;
					break;
				case 12:
					type = Material.BOOK;
					break;
				case 13:
					type = Material.CAKE;
					break;
				case 14:
					type = Material.MELON;
					break;
				case 15:
					type = Material.COOKIE;
					break;
				case 16:
					type = Material.PUMPKIN;
					break;
				case 17:
					type = Material.SUGAR_CANE;
					break;
				case 18:
					type = Material.EGG;
					break;
				case 19:
					type = Material.WHEAT;
					break;
				case 20:
					type = Material.SPIDER_EYE;
					break;
				case 21:
					type = Material.POTATO_ITEM;
					break;
				case 22:
					type = Material.BONE;
					break;
				case 23:
					type = Material.FEATHER;
			}
		}
		while (type == getEatFoodTypeForGod(godName) || type == Material.AIR);

		return type;
	}

	private void manageSacrifices(String godName)
	{
		if (!this.plugin.sacrificesEnabled)
		{
			return;
		}

		int godPower = 1 + (int) this.plugin.getGodManager().getGodPower(godName);
		if (this.random.nextInt(20 + (int) (70.0F / godPower)) > 0)
		{
			return;
		}
		Material type = getSacrificeNeedForGod(godName);

		float value = getSacrificeValueForGod(godName, type);

		value += 1 + this.random.nextInt(3);
		if (value > 64.0F)
		{
			value = 64.0F;
		}
		else if (value < -64.0F)
		{
			value = -64.0F;
		}
		this.plugin.logDebug("Increasing wanted " + type.name() + " sacrifice need for " + godName + " to " + value);

		this.godsConfig.set(godName + ".SacrificeValues." + type.name(), Float.valueOf(value));

		saveTimed();

		type = getSacrificeUnwantedForGod(godName);
		if (type != null)
		{
			value = 0.25F * getSacrificeValueForGod(godName, type);
			if (value > -0.5D)
			{
				value = 0.0F;
			}
			this.plugin.logDebug("Reducing unwanted " + type.name() + " sacrifice need for " + godName + " to " + value);
			if (value == 0.0F)
			{
				this.godsConfig.set(godName + ".SacrificeValues." + type.name(), null);
			}
			else
			{
				this.godsConfig.set(godName + ".SacrificeValues." + type.name(), Float.valueOf(value));
			}
			save();
		}
	}

	private void manageSacrifices()
	{
		if (!this.plugin.sacrificesEnabled)
		{
			return;
		}

		if (this.random.nextInt(10) > 0)
		{
			return;
		}

		this.plugin.getAltarManager().clearDroppedItems();
	}

	private void manageHolyLands()
	{
		if (!this.plugin.holyLandEnabled)
		{
			return;
		}
		if (this.random.nextInt(1000) > 0)
		{
			return;
		}
		this.plugin.getLandManager().removeAbandonedLands();
	}

	public void update()
	{
		if (this.random.nextInt(50) == 0)
		{
			this.plugin.logDebug("Processing dead offline Gods...");

			long timeBefore = System.currentTimeMillis();

			List<String> godNames = getOfflineGods();
			for (String offlineGodName : godNames)
			{
				if (isDeadGod(offlineGodName))
				{
					this.plugin.log("Removed dead offline God '" + offlineGodName + "'");
				}
			}
			long timeAfter = System.currentTimeMillis();

			this.plugin.logDebug("Processed " + godNames.size() + " offline Gods in " + (timeAfter - timeBefore) + " ms");
		}

		List<String> godNames = getOnlineGods();

		long timeBefore = System.currentTimeMillis();

		if (godNames.size() == 0)
		{
			return;
		}
		String godName = (String) godNames.toArray()[this.random.nextInt(godNames.size())];

		this.plugin.logDebug("Processing God '" + godName + "'");

		boolean godTalk = false;

		manageMood(godName);

		if (!godTalk)
		{
			godTalk = managePriests(godName);
		}

		manageLostBelievers(godName);

		if (!godTalk)
		{
			manageBelievers(godName);
		}

		if (!godTalk)
		{
			manageQuests(godName);
		}

		manageBlessings(godName);

		manageCurses(godName);

		manageSacrifices(godName);

		manageSacrifices();

		manageHolyLands();

		long timeAfter = System.currentTimeMillis();

		this.plugin.logDebug("Processed 1 Online God in " + (timeAfter - timeBefore) + " ms");
		if (this.random.nextInt(1000) == 0)
		{
			this.plugin.logDebug("Processing chests...");
		}
	}

	public class NewPriestComparator implements Comparator
	{
		public NewPriestComparator()
		{
		}

		public int compare(Object object1, Object object2)
		{
			GodManager.PriestCandidate c1 = (GodManager.PriestCandidate) object1;
			GodManager.PriestCandidate c2 = (GodManager.PriestCandidate) object2;

			float power1 = GodManager.this.plugin.getBelieverManager().getBelieverPower(c1.believerId);
			float power2 = GodManager.this.plugin.getBelieverManager().getBelieverPower(c2.believerId);

			return (int) (power2 - power1);
		}
	}

	public class PriestCandidate
	{
		public UUID	believerId;

		PriestCandidate(UUID believerId)
		{
			this.believerId = believerId;
		}
	}
}