//package com.dogonfire.gods;
//
//import java.io.File;
//import java.io.PrintStream;
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Random;
//import java.util.Set;
//import java.util.UUID;
//
//import net.minecraft.server.v1_8_R1.EntityTypes;
//
//import org.bukkit.Chunk;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.Server;
//import org.bukkit.block.Block;
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.configuration.file.YamlConfiguration;
//import org.bukkit.entity.EnderDragon;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.LivingEntity;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
//import org.bukkit.event.world.ChunkUnloadEvent;
//
//public class BossManager implements Listener
//{
//	private Gods plugin;
//	private HashMap<String, Entity> bossEntityID = new HashMap();
//	private DragonContainer dragonContainer;
//	private DragonLogicTicker dragonLogicTicker;
//	private FileConfiguration bossConfig = null;
//	private File bossConfigFile = null;
//	private Random random = new Random();
//	private final int edInt = 63;
//	private final String edName = "LimitedEnderDragon";
//	private final Class<?> edClass = LimitedEnderDragon.class;
//	private HashMap<UUID, LimitedEnderDragon> dragonList = new HashMap();
//
//	public void registerDragon(LimitedEnderDragon dragon)
//	{
//		this.dragonContainer.registerDragon(dragon);
//	}
//
//	public void unregisterDragon(UUID dragonId)
//	{
//		this.dragonContainer.unregisterDragon(dragonId);
//	}
//
//	public List<LimitedEnderDragon> getAllDragons()
//	{
//		return new LinkedList(this.dragonList.values());
//	}
//
//	static enum BossType
//	{
//		TITAN, DRAGON;
//	}
//
//	private String generateDragonBossName()
//	{
//		String name = "";
//
//		return name;
//	}
//
//	public BossManager(Gods plugin)
//	{
//		this.plugin = plugin;
//
//		this.dragonContainer = new DragonContainer(plugin);
//		this.dragonLogicTicker = new DragonLogicTicker(plugin);
//		try
//		{
//			Class entityTypeClass = EntityTypes.class;
//
//			Field c = entityTypeClass.getDeclaredField("c");
//			c.setAccessible(true);
//			HashMap c_map = (HashMap) c.get(null);
//			c_map.put("LimitedEnderDragon", this.edClass);
//
//			Field d = entityTypeClass.getDeclaredField("d");
//			d.setAccessible(true);
//			HashMap d_map = (HashMap) d.get(null);
//			d_map.put(this.edClass, "LimitedEnderDragon");
//
//			Field e = entityTypeClass.getDeclaredField("e");
//			e.setAccessible(true);
//			HashMap e_map = (HashMap) e.get(null);
//			e_map.put(Integer.valueOf(63), this.edClass);
//
//			Field f = entityTypeClass.getDeclaredField("f");
//			f.setAccessible(true);
//			HashMap f_map = (HashMap) f.get(null);
//			f_map.put(this.edClass, Integer.valueOf(63));
//
//			Field g = entityTypeClass.getDeclaredField("g");
//			g.setAccessible(true);
//			HashMap g_map = (HashMap) g.get(null);
//			g_map.put("LimitedEnderDragon", Integer.valueOf(63));
//		}
//		catch (NoClassDefFoundError exp)
//		{
//			plugin.log("Could not inject LimitedEnderDragon.");
//
//			exp.printStackTrace();
//		}
//		catch (Exception exp)
//		{
//			plugin.log("Something has gone wrong while injekting! Plugin will be disabled!");
//		}
//	}
//
//	public void disable()
//	{
//		for (LimitedEnderDragon dragon : this.dragonContainer.getAllDragons())
//		{
//			//LivingEntity a = (LivingEntity) ((Object) dragon).getBukkitEntity();
//			//a.remove();
//		}
//	}
//
//	public void load()
//	{
//		if (this.bossConfigFile == null)
//		{
//			this.bossConfigFile = new File(this.plugin.getDataFolder(), "bosses.yml");
//		}
//		this.bossConfig = YamlConfiguration.loadConfiguration(this.bossConfigFile);
//
//		this.plugin.log("Loaded " + this.bossConfig.getKeys(false).size() + " bosses.");
//	}
//
//	public void save()
//	{
//		if ((this.bossConfig == null) || (this.bossConfigFile == null))
//		{
//			return;
//		}
//		try
//		{
//			this.bossConfig.save(this.bossConfigFile);
//		}
//		catch (Exception ex)
//		{
//			this.plugin.log("Could not save config to " + this.bossConfigFile + ": " + ex.getMessage());
//		}
//	}
//
//	public DragonContainer getDragonContainer()
//	{
//		return this.dragonContainer;
//	}
//
//	public int getNumberOfDragons()
//	{
//		return this.dragonContainer.getAllDragons().size();
//	}
//
//	public void spawnDragon(Player player)
//	{
//		//LimitedEnderDragon dragon = new LimitedEnderDragon(this.plugin, player.getLocation(), ((CraftWorld) player.getLocation().getWorld()).getHandle(), "Ssdfs");
//		//dragon.spawn();
//		//dragon.addEnemy(player);
//	}
//
//	public int createNewBoss(BossType type, String godName, Location location)
//	{
//		/*
//		String bossName = generateDragonBossName();
//
//		//net.minecraft.server.v1_8_R1.World notchWorld = ((CraftWorld) location.getWorld()).getHandle();
//		//DragonBoss dragonBoss = new DragonBoss(this.plugin, bossName, location, notchWorld);
//		//notchWorld.addEntity(dragonBoss, CreatureSpawnEvent.SpawnReason.CUSTOM);
//		//LivingEntity dragon = (LivingEntity) dragonBoss.getBukkitEntity();
//
//		save();
//
//		this.bossEntityID.put(bossName, dragon);
//
//		return dragon.getEntityId();
//		*/
//		return 0;
//	}
//
//	public String getBossName()
//	{
//		return "Calgor The Destroyer";
//	}
//
//	private Location getDragonBossCaveLocation(org.bukkit.World world, int minDist, int maxDist, Location center)
//	{
//		int run = 0;
//
//		Block target = null;
//
//		List defaultspawnblocks = new ArrayList();
//		defaultspawnblocks.add(Material.STONE);
//		defaultspawnblocks.add(Material.SMOOTH_BRICK);
//		defaultspawnblocks.add(Material.MOSSY_COBBLESTONE);
//		defaultspawnblocks.add(Material.OBSIDIAN);
//		do
//		{
//			run++;
//
//			int minLevel = 4;
//			int maxLevel = 50;
//			int maxLight = 4;
//			int minLight = 0;
//			int x;
//			int z;
//			do
//			{
//				x = this.random.nextInt(maxDist * 2) - maxDist + center.getBlockX();
//				z = this.random.nextInt(maxDist * 2) - maxDist + center.getBlockZ();
//			}
//			while ((Math.abs(x - center.getBlockX()) < minDist) || (Math.abs(z - center.getBlockZ()) < minDist));
//			int y;
//			do
//			{
//				y = this.random.nextInt(maxLevel);
//			}
//			while (
//
//			y < minLevel);
//			target = world.getBlockAt(x, y, z);
//			if ((target.getType() == Material.AIR) && (world.getBlockAt(x, y + 1, z).getType() == Material.AIR) && (world.getBlockAt(x, y + 2, z).getType() == Material.AIR) && (world.getBlockAt(x, y + 3, z).getType() == Material.AIR))
//			{
//				target = world.getBlockAt(x, y + 2, z);
//				if ((defaultspawnblocks.contains(target.getType())) && (world.getHighestBlockAt(target.getLocation()).getType() != Material.WATER))
//				{
//					return target.getLocation();
//				}
//			}
//		}
//		while (
//
//		run < 100);
//		this.plugin.log("Could not find a Dragon Boss cave location " + world.getName());
//		return null;
//	}
//
//	public void removeDragons()
//	{
//		for (org.bukkit.World world : this.plugin.getServer().getWorlds())
//		{
//			removeDragons(world);
//		}
//	}
//
//	public void removeDragons(org.bukkit.World world)
//	{
//		if (world == null)
//		{
//			return;
//		}
//		int passed = 0;
//		for (Entity entity : world.getEntities())
//		{
//			if ((entity instanceof EnderDragon))
//			{
//				entity.remove();
//				passed++;
//			}
//		}
//		world.save();
//
//		this.plugin.logDebug("Removed " + passed + " boss dragon(s)");
//	}
//
//	void detectPlayer(DragonBoss dragon)
//	{
//	}
//
//	public void setNewTargetForDragon(DragonBoss dragon)
//	{
//		/*
//		dragon.startX = dragon.locX;
//		dragon.startY = dragon.locY;
//		dragon.startZ = dragon.locZ;
//
//		dragon.toX = (this.random.nextInt(2001) - 1000);
//		dragon.toZ = (this.random.nextInt(2001) - 1000);
//		if (this.random.nextInt(3) == 0)
//		{
//			org.bukkit.World world = dragon.world.getWorld();
//
//			Location location = getDragonBossCaveLocation(world, 200, 1000, new Location(world, 0.0D, 0.0D, 0.0D));
//			if (location != null)
//			{
//				dragon.toY = location.getY();
//			}
//		}
//		else
//		{
//			dragon.toY = 100.0D;
//		}
//		System.out.println("Dragon NewTarget " + dragon.toX + "," + dragon.toY + "," + dragon.toZ);
//
//		double yaw = dragon.getCorrectYaw(dragon.toX, dragon.toZ);
//
//		dragon.setupMove();
//
//		dragon.finalmove = false;
//
//		this.bossConfig.set(dragon.getName() + "TargetPosition.X", Double.valueOf(dragon.toX));
//		*/
//	}
//
//	public void dragonUpdate(DragonBoss dragon)
//	{
//		/*
//		Entity entity = dragon.getBukkitEntity();
//
//		entity.getPassenger();
//
//		double myX = dragon.locX;
//		double myY = dragon.locY;
//		double myZ = dragon.locZ;
//		if (dragon.finalmove)
//		{
//			if ((int) dragon.locY > (int) dragon.toY)
//			{
//				myY -= dragon.speed;
//			}
//			else if ((int) dragon.locY < (int) dragon.toY)
//			{
//				myY += dragon.speed;
//			}
//			else if (this.random.nextInt(100) == 0)
//			{
//				setNewTargetForDragon(dragon);
//			}
//			else
//			{
//				detectPlayer(dragon);
//			}
//			dragon.setPosition(myX, myY, myZ);
//			return;
//		}
//		if ((int) dragon.locY < dragon.maxY)
//		{
//			myY += dragon.speed;
//		}
//		if (myX < dragon.toX)
//		{
//			myX += dragon.XTick;
//		}
//		else
//		{
//			myX -= dragon.XTick;
//		}
//		if (myZ < dragon.toZ)
//		{
//			myZ += dragon.ZTick;
//		}
//		else
//		{
//			myZ -= dragon.ZTick;
//		}
//		if ((Math.abs(myZ - dragon.toZ) < 50.0D) && (Math.abs(myX - dragon.toX) < 50.0D))
//		{
//			dragon.finalmove = true;
//		}
//		System.out.println("Normal move : " + myX + " " + myY + " " + myZ);
//
//		dragon.setPosition(myX, myY, myZ);
//		*/
//	}
//
//	@EventHandler
//	public void chunkUnload(ChunkUnloadEvent event)
//	{
//		Chunk chunk = event.getChunk();
//		Entity[] arrayOfEntity;
//		if ((arrayOfEntity = chunk.getEntities()).length != 0)
//		{
//			Entity entity = arrayOfEntity[0];
//			if (this.dragonContainer.containsID(entity.getUniqueId()))
//			{
//			}
//			event.setCancelled(true);
//			return;
//		}
//	}
//}
//
///*
// * Location: C:\temp\Gods.jar
// * 
// * Qualified Name: com.dogonfire.gods.BossManager
// * 
// * JD-Core Version: 0.7.0.1
// */