//package com.dogonfire.gods;
//
//import java.util.HashMap;
//import java.util.Set;
//import java.util.UUID;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.scheduler.BukkitScheduler;
//
//public class DragonLogicTicker implements Runnable
//{
//	private Gods plugin;
//	private HashMap<UUID, Location> locs;
//
//	public DragonLogicTicker(Gods plugin)
//	{
//		this.plugin = plugin;
//		Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, this, 5L, 5L);
//		this.locs = new HashMap();
//	}
//
//	public void run()
//	{
//		int limit = 25;
//		limit /= 5;
//		if (limit > 0)
//		{
//			Set<UUID> ids = this.plugin.getBossManager().getDragonContainer().getAllIDs();
//			for (UUID id : ids)
//			{
//				LimitedEnderDragon dragon = this.plugin.getBossManager().getDragonContainer().getDragonById(id);
//				if ((dragon == null) /*|| !dragon.isAlive()*/)
//				{
//					this.locs.remove(id);
//				}
//				else
//				{
//					Location lastDragonLoc = (Location) this.locs.get(id);
//					if (lastDragonLoc == null)
//					{
//						this.locs.put(id, dragon.getLocation().clone());
//					}
//					else
//					{
//						double flyDistance = lastDragonLoc.distance(dragon.getLocation());
//						if (flyDistance < 0.01D)
//						{
//							for (int i = 0; i < limit; i++)
//							{
//								try
//								{
//									if (dragon == null)
//									{
//										break;
//									}
//									dragon.e();
//								}
//								catch (Exception localException)
//								{
//								}
//							}
//						}
//						this.locs.put(id, dragon.getLocation());
//
//						this.plugin.logDebug("dragon Logic Calls: " + dragon.getLogicCalls());
//					}
//				}
//			}
//		}
//	}
//}
//
///*
// * Location: C:\temp\Gods.jar
// * 
// * Qualified Name: com.dogonfire.gods.DragonLogicTicker
// * 
// * JD-Core Version: 0.7.0.1
// */