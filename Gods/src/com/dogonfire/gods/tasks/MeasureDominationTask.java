package com.dogonfire.gods.tasks;

import com.dogonfire.gods.Gods;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitScheduler;

public class MeasureDominationTask
  implements Runnable
{
  private Gods plugin;
  private Location location;
  private int points = 0;
  
  public MeasureDominationTask(Gods instance, Location location)
  {
    this.plugin = instance;
    this.location = location;
  }
  
  public void run()
  {
    Random random = new Random();
    
    this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, this, 40L);
  }
}


/* Location:           C:\temp\Gods.jar
 * Qualified Name:     com.dogonfire.gods.tasks.MeasureDominationTask
 * JD-Core Version:    0.7.0.1
 */