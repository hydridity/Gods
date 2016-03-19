package com.dogonfire.gods.tasks;

import com.dogonfire.gods.Gods;
import com.dogonfire.gods.HolyPowerManager;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class ThunderStormTask
  implements Runnable
{
  private Gods plugin;
  private long stopTime;
  private Player player;
  private Random random = new Random();
  
  public ThunderStormTask(Gods instance, Player player, long stopTime)
  {
    this.plugin = instance;
    this.stopTime = stopTime;
    this.player = player;
  }
  
  public void run()
  {
    this.player.getWorld().setStorm(true);
    




    Entity[] entities = this.plugin.getHolyPowerManager().getNearbyLivingEntities(this.player.getLocation(), 20.0D);
    
    Entity targetEntity = entities[this.random.nextInt(entities.length)];
    if (targetEntity != this.player)
    {
      Location strikeLocation = targetEntity.getLocation();
      strikeLocation = strikeLocation.getWorld().getHighestBlockAt(strikeLocation).getLocation();
      LightningStrike localLightningStrike = this.player.getWorld().strikeLightning(strikeLocation);
    }
    if (System.currentTimeMillis() < this.stopTime) {
      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new ThunderStormTask(this.plugin, this.player, this.stopTime), 20 + this.random.nextInt(100));
    } else {
      this.player.getWorld().setStorm(false);
    }
  }
}


/* Location:           C:\temp\Gods.jar
 * Qualified Name:     com.dogonfire.gods.tasks.ThunderStormTask
 * JD-Core Version:    0.7.0.1
 */