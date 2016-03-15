package com.dogonfire.gods.tasks;

import java.util.Random;

import com.dogonfire.gods.GodManager;
import com.dogonfire.gods.Gods;
import com.dogonfire.gods.LanguageManager;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftOcelot;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class LoveTask implements Runnable
{
	private Gods	plugin;
	private Player	player1;
	private Player	player2;
	private int		cycle	= 3;

	public LoveTask(Gods instance, Player player1, Player player2)
	{
		this.plugin = instance;
		this.player1 = player1;
		this.player2 = player2;
	}

	public LoveTask(Gods instance, Player player1, Player player2, int cycle)
	{
		this.plugin = instance;
		this.player1 = player1;
		this.player2 = player2;
		this.cycle = cycle;
	}

	public void run()
	{
		if ((!this.player1.isOnline()) || (!this.player2.isOnline()))
		{
			return;
		}

		Random random = new Random();
		boolean firework = (random.nextInt(100) == 0);

		{
			CraftPlayer player = (CraftPlayer) this.player1;
			CraftOcelot ocelot = (CraftOcelot) player.getWorld().spawn(player.getLocation(), Ocelot.class);

			player.getHandle().world.broadcastEntityEffect(ocelot.getHandle(), (byte) 7);
			ocelot.remove();

			if (firework)
			{
				this.plugin.getHolyPowerManager().shootFirework(player, 16);
			}
		}

		{
			CraftPlayer player = (CraftPlayer) this.player2;
			CraftOcelot ocelot = (CraftOcelot) player.getWorld().spawn(player.getLocation(), Ocelot.class);

			player.getHandle().world.broadcastEntityEffect(ocelot.getHandle(), (byte) 7);
			ocelot.remove();

			if (firework)
			{
				this.plugin.getHolyPowerManager().shootFirework(player, 16);
			}
		}

		int newcycle = this.cycle - 1;
		if (newcycle > 0)
		{
			this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new LoveTask(this.plugin, this.player1, this.player2, newcycle), 40L);
		}
	}

	/*
	 * private void onLove()
	 * 
	 * { if (this.plugin.getGodManager().getDivineForceForGod(godName) ==
	 * GodManager.GodType.LOVE) { powerBefore =
	 * this.plugin.getBelieverManager().getBelieverPower(player.getUniqueId());
	 * this.plugin.getBelieverManager().increasePrayer(killer.getUniqueId(),
	 * killerGodName, 2);
	 * this.plugin.getBelieverManager().increasePrayerPower(killer
	 * .getUniqueId(), 2); powerAfter =
	 * this.plugin.getBelieverManager().getBelieverPower(player.getUniqueId());
	 * 
	 * this.plugin.sendInfo(killer.getUniqueId(),
	 * LanguageManager.LANGUAGESTRING.YouEarnedPowerBySlayingHeathen,
	 * ChatColor.AQUA, (int) (powerAfter - powerBefore), killerGodName, 20); } }
	 */
}