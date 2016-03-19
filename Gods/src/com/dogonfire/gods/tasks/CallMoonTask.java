package com.dogonfire.gods.tasks;

import com.dogonfire.gods.Gods;
import java.util.Random;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class CallMoonTask
  implements Runnable
{
  private Gods plugin;
  private long stopTime;
  private Player player;
  private Random random = new Random();
  
  public CallMoonTask(Gods instance, Player player, long stopTime)
  {
    this.plugin = instance;
    this.stopTime = stopTime;
    this.player = player;
  }
  
  public void run()
  {
    long time = this.player.getWorld().getFullTime() % 24000L;
    if ((time < 14000L) || (time > 1000L))
    {
      this.player.playSound(this.player.getLocation(), Sound.STEP_STONE, 1.0F, 0.1F);
      this.player.getWorld().setFullTime(this.player.getWorld().getFullTime() + 1000L);
      this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new CallMoonTask(this.plugin, this.player, this.stopTime), 20L);
    }
  }
}


/* Location:           C:\temp\Gods.jar
 * Qualified Name:     com.dogonfire.gods.tasks.CallMoonTask
 * JD-Core Version:    0.7.0.1
 */