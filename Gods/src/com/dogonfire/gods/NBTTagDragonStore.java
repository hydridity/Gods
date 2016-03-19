//package com.dogonfire.gods;
//
//import java.util.List;
//import java.util.UUID;
//
//import net.minecraft.server.v1_8_R1.NBTTagCompound;
//
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.World;
//
//public class NBTTagDragonStore
//{
//	private static NBTTagCompound compound;
//
//	public static void saveToNBT(LimitedEnderDragon dragon, NBTTagCompound propertiesCompound, NBTTagCompound damageMap, NBTTagCompound targetList)
//	{
//		Location homeLocation = dragon.getHomeLocation();
//		compound.setDouble("homeLocation.x", homeLocation.getX());
//		compound.setDouble("homeLocation.y", homeLocation.getY());
//		compound.setDouble("homeLocation.z", homeLocation.getZ());
//		compound.setString("homeLocation.world", homeLocation.getWorld().getName());
//
//		Location forceGoTo = dragon.getForceLocation();
//		if (forceGoTo != null)
//		{
//			compound.setDouble("forceTarget.x", forceGoTo.getX());
//			compound.setDouble("forceTarget.y", forceGoTo.getY());
//			compound.setDouble("forceTarget.z", forceGoTo.getZ());
//			compound.setString("forceTarget.world", forceGoTo.getWorld().getName());
//		}
//		compound.setBoolean("flyingHome", dragon.isFlyingHome());
//		compound.setBoolean("isHostile", dragon.isHostile());
//
//		// compound.setFloat("currentHealth", dragon.getHealth());
//
//		compound.set("properties", propertiesCompound);
//
//		compound.setString("uuid", dragon.getUUID().toString());
//
//		compound.set("damagemap", damageMap);
//		compound.set("targetlist", targetList);
//	}
//
//	public static DragonNBTReturn loadFromNBT(LimitedEnderDragon dragon, NBTTagCompound compound)
//	{
//		String worldName = compound.getString("homeLocation.world");
//
//		double x = compound.getDouble("homeLocation.x");
//		double y = compound.getDouble("homeLocation.y");
//		double z = compound.getDouble("homeLocation.z");
//
//		Location homeLocation = new Location(Bukkit.getWorld(worldName), x, y, z);
//		boolean flyingHome = compound.getBoolean("flyingHome");
//
//		Location forceTarget = null;
//		if (compound.hasKey("foceTarget.world"))
//		{
//			double forceLocationX = compound.getDouble("forceTarget.x");
//			double forceLocationY = compound.getDouble("forceTarget.y");
//			double forceLocationZ = compound.getDouble("forceTarget.z");
//			String forceLocationWorld = compound.getString("forceTarget.world");
//
//			World forceWorld = Bukkit.getWorld(forceLocationWorld);
//			forceTarget = new Location(forceWorld, forceLocationX, forceLocationY, forceLocationZ);
//		}
//		NBTTagCompound properties = compound.getCompound("properties");
//		UUID uuid = UUID.fromString(compound.getString("uuid"));
//
//		float currentHealth = compound.getFloat("currentHealth");
//		NBTTagCompound damageList = compound.getCompound("damagemap");
//		NBTTagCompound targetList = compound.getCompound("targetlist");
//
//		DragonNBTReturn returnValue = new DragonNBTReturn(null);
//		returnValue.setHomeLocation(homeLocation).setForceTarget(forceTarget).setAgeContainer(compound.getCompound("age")).setFlyingHome(flyingHome).setProperties(properties).setUuid(uuid).setCurrentHealth(currentHealth).setDamageList(damageList).setTargetList(targetList);
//
//		return returnValue;
//	}
//
//	public static class DragonNBTReturn
//	{
//		private Location homeLocation;
//		private Location forceTarget;
//		private boolean flyingHome;
//		private NBTTagCompound properties;
//		private NBTTagCompound damageList;
//		private NBTTagCompound targetList;
//		private float currentHealth;
//		private UUID uuid;
//
//		private DragonNBTReturn()
//		{
//			this.homeLocation = new Location((World) Bukkit.getWorlds().get(0), 0.0D, 0.0D, 0.0D);
//			this.forceTarget = this.homeLocation.clone();
//			this.flyingHome = true;
//			this.properties = new NBTTagCompound();
//			this.currentHealth = 42.0F;
//		}
//
//		public DragonNBTReturn setHomeLocation(Location homeLocation)
//		{
//			this.homeLocation = homeLocation;
//			return this;
//		}
//
//		public DragonNBTReturn setForceTarget(Location forceTarget)
//		{
//			this.forceTarget = forceTarget;
//			return this;
//		}
//
//		public DragonNBTReturn setFlyingHome(boolean flyingHome)
//		{
//			this.flyingHome = flyingHome;
//			return this;
//		}
//
//		public DragonNBTReturn setProperties(NBTTagCompound properties)
//		{
//			this.properties = properties;
//			return this;
//		}
//
//		public DragonNBTReturn setAgeContainer(NBTTagCompound nbtTagCompound)
//		{
//			return this;
//		}
//
//		public DragonNBTReturn setCurrentHealth(float currentHealth)
//		{
//			this.currentHealth = currentHealth;
//			return this;
//		}
//
//		public DragonNBTReturn setUuid(UUID uuid)
//		{
//			this.uuid = uuid;
//			return this;
//		}
//
//		public DragonNBTReturn setDamageList(NBTTagCompound damageList)
//		{
//			this.damageList = damageList;
//			return this;
//		}
//
//		public DragonNBTReturn setTargetList(NBTTagCompound targetList)
//		{
//			this.targetList = targetList;
//			return this;
//		}
//
//		public Location getHomeLocation()
//		{
//			return this.homeLocation;
//		}
//
//		public Location getForceTarget()
//		{
//			return this.forceTarget;
//		}
//
//		public boolean isFlyingHome()
//		{
//			return this.flyingHome;
//		}
//
//		public NBTTagCompound getProperties()
//		{
//			return this.properties;
//		}
//
//		public UUID getUuid()
//		{
//			return this.uuid;
//		}
//
//		public float getCurrentHealth()
//		{
//			return this.currentHealth;
//		}
//
//		public NBTTagCompound getDamageList()
//		{
//			return this.damageList;
//		}
//
//		public NBTTagCompound getTargetList()
//		{
//			return this.targetList;
//		}
//	}
//}
//
///*
// * Location: C:\temp\Gods.jar
// * 
// * Qualified Name: com.dogonfire.gods.NBTTagDragonStore
// * 
// * JD-Core Version: 0.7.0.1
// */