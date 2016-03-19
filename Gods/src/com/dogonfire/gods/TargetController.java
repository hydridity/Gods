//package com.dogonfire.gods;
//
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Queue;
//import java.util.Random;
//import net.minecraft.server.v1_7_R3.Entity;
//import net.minecraft.server.v1_7_R3.EntityLiving;
//import net.minecraft.server.v1_7_R3.EntityPlayer;
//import net.minecraft.server.v1_7_R3.NBTTagCompound;
//import org.bukkit.Bukkit;
//import org.bukkit.GameMode;
//import org.bukkit.Location;
//import org.bukkit.Server;
//import org.bukkit.World;
//import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
//import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
//import org.bukkit.entity.LivingEntity;
//import org.bukkit.entity.Player;
//import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
//import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
//import org.bukkit.plugin.PluginManager;
//
//public class TargetController
//{
//	protected LinkedList<EntityLiving> targets;
//	protected EntityLiving currentTarget;
//	protected Location targetLocation;
//	protected Location homeLocation;
//	protected boolean lockTarget;
//	protected boolean isFlyingHome;
//	protected boolean isHostile;
//	protected Gods plugin;
//	protected Random random;
//	protected LimitedEnderDragon dragon;
//	protected int unTargetTicksMax;
//	protected int unTargetTick;
//	protected Location forceGoTo;
//
//	public TargetController(Gods plugin, Location homeLocation, LimitedEnderDragon dragon, boolean isHostile)
//	{
//		this.random = new Random();
//
//		this.targets = new LinkedList();
//		this.currentTarget = null;
//		this.dragon = dragon;
//
//		this.homeLocation = homeLocation;
//		this.targetLocation = homeLocation;
//		this.isHostile = isHostile;
//
//		this.lockTarget = false;
//		this.plugin = plugin;
//
//		this.unTargetTicksMax = 60;
//		this.unTargetTick = this.unTargetTicksMax;
//		this.isFlyingHome = false;
//	}
//
//	public void addTarget(EntityLiving entity)
//	{
//		this.targets.add(entity);
//	}
//
//	public void removeTarget(EntityLiving entity)
//	{
//		this.targets.remove(entity);
//	}
//
//	public void clearTargets()
//	{
//		this.targets.clear();
//	}
//
//	protected void checkTargets()
//	{
//		Location currentLocation = getDragonLocation();
//		LinkedList<EntityLiving> newTargets = new LinkedList();
//		for (EntityLiving entity : this.targets)
//		{
//			this.plugin.logDebug("CheckTargets: " + entity.getName());
//			if (entity != null)
//			{
//				LivingEntity bukkitEntity = (LivingEntity) entity.getBukkitEntity();
//				Location targetLoc = bukkitEntity.getLocation();
//				if (targetLoc.getWorld() == currentLocation.getWorld())
//				{
//					double distanceSquared = currentLocation.distanceSquared(targetLoc);
//					double allowedDistance = 10000.0D;
//					if ((distanceSquared > 100.0D) && (distanceSquared <= allowedDistance) && (isValidTarget(bukkitEntity)))
//					{
//						newTargets.add(entity);
//					}
//				}
//			}
//		}
//		clearTargets();
//
//		this.targets = newTargets;
//	}
//
//	public boolean switchTargetsWithMode()
//	{
//		if ((this.isHostile) && (!this.isFlyingHome))
//		{
//			rescanTargetsAggressive();
//		}
//		checkTargets();
//		return switchTarget();
//	}
//
//	protected void rescanTargetsAggressive()
//	{
//		Location currentLocation = getDragonLocation();
//		List<Player> players = currentLocation.getWorld().getPlayers();
//
//		this.targets.clear();
//		for (Player player : players)
//		{
//			this.targets.add(((CraftPlayer) player).getHandle());
//		}
//	}
//
//	protected boolean switchTarget()
//	{
//		Location currentLocation = getDragonLocation();
//		EntityLiving newTarget = this.currentTarget;
//		if (this.lockTarget)
//		{
//			double distanceSquared = currentLocation.distanceSquared(this.targetLocation);
//			if (distanceSquared < 900.0D)
//			{
//				this.lockTarget = false;
//			}
//			else
//			{
//				return false;
//			}
//		}
//		boolean targetChanged = false;
//		if (this.targets.size() == 0)
//		{
//			targetChanged = (this.currentTarget != null) && (this.targetLocation != this.homeLocation);
//			newTarget = null;
//		}
//		else
//		{
//			int randomTarget = this.random.nextInt(this.targets.size());
//
//			newTarget = (EntityLiving) this.targets.get(randomTarget);
//			if ((newTarget != null) && (newTarget.getHealth() > 0.0F))
//			{
//				targetChanged = this.currentTarget != newTarget;
//			}
//		}
//		if (targetChanged)
//		{
//			this.plugin.logDebug("Setting new target to " + newTarget.getName());
//
//			this.currentTarget = fireBukkitEvent(newTarget);
//
//			this.plugin.logDebug("Setting new target to " + this.currentTarget.getName());
//
//			this.targetLocation = (this.currentTarget == null ? this.homeLocation.clone() : this.currentTarget.getBukkitEntity().getLocation());
//			return true;
//		}
//		return false;
//	}
//
//	public LinkedList<Entity> getTargetsInRange(int number, double range)
//	{
//		LinkedList randomTargets = new LinkedList();
//
//		Collections.shuffle(this.targets);
//		for (Entity entity : this.targets)
//		{
//			if (isInRange(entity.getBukkitEntity().getLocation(), range))
//			{
//				randomTargets.add(entity);
//				if (randomTargets.size() >= number)
//				{
//					break;
//				}
//			}
//		}
//		return randomTargets;
//	}
//
//	public boolean isInRange(Location entityLocation, double range)
//	{
//		if (entityLocation.getWorld() != this.dragon.getLocation().getWorld())
//		{
//			return false;
//		}
//		double doubleRange = range * range;
//		double distance = getDragonLocation().distanceSquared(entityLocation);
//
//		return distance <= doubleRange;
//	}
//
//	protected boolean isValidTarget(LivingEntity entity)
//	{
//		if ((entity == null) || (entity.isDead()))
//		{
//			return false;
//		}
//		if (!(entity instanceof Player))
//		{
//			return false;
//		}
//		Player player = (Player) entity;
//		if (player.getGameMode() == GameMode.CREATIVE)
//		{
//			return false;
//		}
//		return true;
//	}
//
//	protected EntityLiving fireBukkitEvent(EntityLiving nextTarget)
//	{
//		if (this.currentTarget != nextTarget)
//		{
//			EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(this.dragon.getBukkitEntity(), nextTarget == null ? null : (LivingEntity) nextTarget.getBukkitEntity(), EntityTargetEvent.TargetReason.RANDOM_TARGET);
//
//			Bukkit.getServer().getPluginManager().callEvent(event);
//			if (event.isCancelled())
//			{
//				return this.currentTarget;
//			}
//		}
//		return nextTarget;
//	}
//
//	protected Location getLoc(EntityLiving entity)
//	{
//		return entity == null ? this.homeLocation.clone() : entity.getBukkitEntity().getLocation();
//	}
//
//	public Location getTargetLocation()
//	{
//		return this.targetLocation;
//	}
//
//	public Location getForceGoTo()
//	{
//		return this.forceGoTo;
//	}
//
//	public EntityLiving getCurrentTarget()
//	{
//		this.unTargetTick -= 1;
//		if (this.unTargetTick <= 0)
//		{
//			this.unTargetTick = this.unTargetTicksMax;
//			return this.currentTarget;
//		}
//		return null;
//	}
//
//	public Location getDragonLocation()
//	{
//		return this.dragon.getLocation();
//	}
//
//	public LimitedEnderDragon getDragon()
//	{
//		return this.dragon;
//	}
//
//	public boolean hasTargets()
//	{
//		return this.targets.size() != 0;
//	}
//
//	public void lockTarget()
//	{
//		this.lockTarget = true;
//	}
//
//	public void unlockTarget()
//	{
//		this.lockTarget = false;
//	}
//
//	public boolean getLock()
//	{
//		return this.lockTarget;
//	}
//
//	public void forceTarget(Location loc)
//	{
//		this.currentTarget = null;
//		this.targetLocation = loc;
//	}
//
//	public void forceTarget(EntityLiving entity)
//	{
//		if (entity == null)
//		{
//			return;
//		}
//		this.currentTarget = fireBukkitEvent(entity);
//		this.targetLocation = getLoc(entity);
//	}
//
//	public boolean isHostile()
//	{
//		return this.isHostile;
//	}
//
//	public void setHostile(boolean isHostile)
//	{
//		this.isHostile = isHostile;
//	}
//
//	public void changeTarget()
//	{
//		try
//		{
//			this.dragon.bz = false;
//
//			int homeRange = 100;
//			if (getVectorDistance(this.homeLocation) > homeRange)
//			{
//				this.isFlyingHome = true;
//				this.forceGoTo = this.homeLocation;
//
//				this.currentTarget = null;
//				this.lockTarget = true;
//
//				this.targets.clear();
//			}
//			switchTargetsWithMode();
//			Location newTarget = getTargetLocation();
//
//			setNewTarget(newTarget, getLock());
//		}
//		catch (Exception e)
//		{
//			if (LimitedEnderDragon.broadcastedError != 10)
//			{
//				LimitedEnderDragon.broadcastedError += 1;
//				return;
//			}
//			LimitedEnderDragon.broadcastedError = 0;
//			this.plugin.log("An Error has Accured. Tried to access to an illigel mob (function: changeTarget). Disabling ErrorMessage for massive Spaming!");
//
//			return;
//		}
//	}
//
//	protected double getVectorDistance(Location location)
//	{
//		double x = location.getX();
//		double y = location.getY();
//		double z = location.getZ();
//
//		return getVectorDistance(x, y, z);
//	}
//
//	protected double getVectorDistance(double x, double y, double z)
//	{
//		double deltaX = this.dragon.locX - x;
//		double deltaY = this.dragon.locY - y;
//		double deltaZ = this.dragon.locZ - z;
//
//		deltaX *= deltaX;
//		deltaY *= deltaY;
//		deltaZ *= deltaZ;
//
//		return Math.sqrt(deltaX + deltaY + deltaZ);
//	}
//
//	public void setNewTarget(Location location, boolean lockTarget)
//	{
//		if (lockTarget)
//		{
//			this.forceGoTo = location;
//		}
//		if (this.forceGoTo != null)
//		{
//			location = this.forceGoTo;
//		}
//		if (this.isFlyingHome)
//		{
//			location = this.homeLocation.clone();
//		}
//		if (getVectorDistance(location) < 30.0D)
//		{
//			if ((this.isFlyingHome) && (location.equals(this.homeLocation)))
//			{
//				this.isFlyingHome = false;
//			}
//			if (this.forceGoTo != null)
//			{
//				this.forceGoTo = null;
//				location = this.homeLocation.clone();
//				return;
//			}
//		}
//		double vecDistance = 0.0D;
//		do
//		{
//			this.dragon.h = location.getX();
//			this.dragon.i = (70.0F + this.random.nextFloat() * 50.0F);
//			this.dragon.j = location.getZ();
//			if ((this.forceGoTo == null) && (!this.isFlyingHome))
//			{
//				this.dragon.h += this.random.nextFloat() * 120.0F - 60.0F;
//				this.dragon.j += this.random.nextFloat() * 120.0F - 60.0F;
//
//				double distanceX = this.dragon.locX - this.dragon.h;
//				double distanceY = this.dragon.locY - this.dragon.i;
//				double distanceZ = this.dragon.locZ - this.dragon.j;
//
//				vecDistance = distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;
//				if (vecDistance > 100.0D)
//				{
//					int minHeight = 0;
//
//					Location target = new Location(this.dragon.getLocation().getWorld(), this.dragon.h, this.dragon.i, this.dragon.j);
//					Location currentLocation = this.dragon.getLocation();
//
//					Queue line = Bresenham.line3D(currentLocation, target);
//					Iterator lineIt = line.iterator();
//					while (lineIt.hasNext())
//					{
//						Location nextLocation = (Location) lineIt.next();
//						int newMinHeight = nextLocation.getWorld().getHighestBlockYAt(nextLocation) + 7;
//						if (newMinHeight > minHeight)
//						{
//							minHeight = newMinHeight;
//						}
//					}
//					if (minHeight < 10)
//					{
//						minHeight = 20;
//					}
//					this.dragon.i = minHeight;
//				}
//			}
//			else
//			{
//				this.dragon.i = location.getY();
//				vecDistance = 101.0D;
//			}
//		}
//		while (
//
//		vecDistance < 100.0D);
//	}
//
//	public boolean isFlyingHome()
//	{
//		return this.isFlyingHome;
//	}
//
//	public void forceFlyingHome(boolean flyingHome)
//	{
//		this.isFlyingHome = flyingHome;
//	}
//
//	public Location getHomeLocation()
//	{
//		return this.homeLocation.clone();
//	}
//
//	public void setHomeLocation(Location homeLocation)
//	{
//		this.homeLocation = homeLocation.clone();
//	}
//
//	public NBTTagCompound getCurrentTagetsAsNBTList()
//	{
//		NBTTagCompound list = new NBTTagCompound();
//
//		int i = 0;
//		for (EntityLiving target : this.targets)
//		{
//			if ((target instanceof EntityPlayer))
//			{
//				String playerName = ((EntityPlayer) target).getName();
//				list.setString("target" + i, playerName);
//				i++;
//			}
//		}
//		return list;
//	}
//
//	public List<Entity> getAllCurrentTargets()
//	{
//		return new LinkedList(this.targets);
//	}
//}
//
///*
// * Location: C:\temp\Gods.jar
// * 
// * Qualified Name: com.dogonfire.gods.TargetController
// * 
// * JD-Core Version: 0.7.0.1
// */