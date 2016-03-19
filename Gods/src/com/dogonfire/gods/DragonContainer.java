//package com.dogonfire.gods;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//import org.bukkit.Bukkit;
//import org.bukkit.Chunk;
//import org.bukkit.Location;
//import org.bukkit.scheduler.BukkitScheduler;
//
//public class DragonContainer
//{
//	private HashMap<UUID, LimitedEnderDragon> dragonList;
//	private Gods plugin;
//
//	public class CleanRunner implements Runnable
//	{
//		private DragonContainer container;
//
//		public CleanRunner(Gods plugin, DragonContainer container)
//		{
//			this.container = container;
//			Bukkit.getScheduler().runTaskTimer(plugin, this, 10L, 10L);
//		}
//
//		public void run()
//		{
//			this.container.cleanRun();
//		}
//	}
//
//	public DragonContainer(Gods plugin)
//	{
//		this.plugin = plugin;
//		this.dragonList = new HashMap();
//		new CleanRunner(plugin, this);
//	}
//
//	public int cleanRun()
//	{
//		List<UUID> toDelete = new ArrayList();
//		for (UUID id : this.dragonList.keySet())
//		{
//			LimitedEnderDragon dragon = (LimitedEnderDragon) this.dragonList.get(id);
//			//if ((dragon == null) || (!dragon.isAlive()))
//			//{
//			//	toDelete.add(id);
//			//}
//		}
//		if (toDelete.size() == 0)
//		{
//			return 0;
//		}
//		for (UUID dragon : toDelete)
//		{
//			this.dragonList.remove(dragon);
//
//			this.plugin.logDebug("removed Dragon!");
//		}
//		return toDelete.size();
//	}
//
//	public int killEnderDragons(Location location, int range, boolean instantRemove)
//	{
//		ArrayList<UUID> toRemove = new ArrayList();
//		for (UUID dragonID : this.dragonList.keySet())
//		{
//			LimitedEnderDragon dragon = (LimitedEnderDragon) this.dragonList.get(dragonID);
//			if ((dragon != null) && ((range == 0) || (dragon.isInRange(location, range))))
//			{
//				toRemove.add(dragonID);
//				if (instantRemove)
//				{
//					dragon.remove();
//				}
//				else
//				{
//					//dragon.dealDamage(DamageSource.MAGIC, 1000.0F);
//				}
//			}
//		}
//		if (toRemove.size() == 0)
//		{
//			return 0;
//		}
//		for (UUID dragonID : toRemove)
//		{
//			this.dragonList.remove(dragonID);
//		}
//		return toRemove.size();
//	}
//
//	public int sendAllDragonsHome()
//	{
//		int i = 0;
//		for (UUID dragonID : this.dragonList.keySet())
//		{
//			LimitedEnderDragon dragon = (LimitedEnderDragon) this.dragonList.get(dragonID);
//			if (dragon != null)
//			{
//				dragon.forceFlyHome(true);
//				i++;
//			}
//		}
//		return i;
//	}
//
//	public boolean containsID(UUID id)
//	{
//		return this.dragonList.containsKey(id);
//	}
//
//	public Set<UUID> getAllIDs()
//	{
//		return this.dragonList.keySet();
//	}
//
//	public int loaded()
//	{
//		int i = 0;
//		for (UUID id : this.dragonList.keySet())
//		{
//			if (this.dragonList.get(id) != null)
//			{
//				i++;
//			}
//		}
//		return i;
//	}
//
//	public int count()
//	{
//		return this.dragonList.size();
//	}
//
//	public LimitedEnderDragon getDragonById(UUID id)
//	{
//		return (LimitedEnderDragon) this.dragonList.get(id);
//	}
//
//	public Location getPositionByID(UUID id)
//	{
//		LimitedEnderDragon dragon = (LimitedEnderDragon) this.dragonList.get(id);
//		if (dragon == null)
//		{
//			return null;
//		}
//		return dragon.getLocation();
//	}
//
//	public boolean isLoaded(UUID id)
//	{
//		LimitedEnderDragon dragon = (LimitedEnderDragon) this.dragonList.get(id);
//
//		boolean isNotDeleted = dragon != null;
//		boolean isLoaded = false;
//		try
//		{
//			isLoaded = dragon.getLocation().getChunk().isLoaded();
//		}
//		catch (Exception localException)
//		{
//		}
//		return (isNotDeleted) && (isLoaded);
//	}
//
//	public void registerDragon(LimitedEnderDragon dragon)
//	{
//		this.dragonList.put(dragon.getUUID(), dragon);
//	}
//
//	public void unregisterDragon(UUID dragonId)
//	{
//		this.dragonList.remove(dragonId);
//	}
//
//	public List<LimitedEnderDragon> getAllDragons()
//	{
//		return new LinkedList(this.dragonList.values());
//	}
//}
