package com.dogonfire.gods.tasks;

import java.util.Random;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftOcelot;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;

public class TaskLove extends Task {
	private Player player1;
	private Player player2;
	private int cycle = 3;

	public TaskLove(Player player1, Player player2) {
		this.player1 = player1;
		this.player2 = player2;
	}

	public TaskLove(Player player1, Player player2, int cycle) {
		this.player1 = player1;
		this.player2 = player2;
		this.cycle = cycle;
	}

	public void run() {
		if ((!this.player1.isOnline()) || (!this.player2.isOnline())) {
			return;
		}

		Random random = new Random();
		boolean firework = (random.nextInt(100) == 0);

		{
			CraftPlayer player = (CraftPlayer) this.player1;
			CraftOcelot ocelot = (CraftOcelot) player.getWorld().spawn(player.getLocation(), Ocelot.class);

			player.getHandle().world.broadcastEntityEffect(ocelot.getHandle(), (byte) 7);
			ocelot.remove();

			if (firework) {
				getPlugin().getHolyPowerManager().shootFirework(player, 16);
			}
		}

		{
			CraftPlayer player = (CraftPlayer) this.player2;
			CraftOcelot ocelot = (CraftOcelot) player.getWorld().spawn(player.getLocation(), Ocelot.class);

			player.getHandle().world.broadcastEntityEffect(ocelot.getHandle(), (byte) 7);
			ocelot.remove();

			if (firework) {
				getPlugin().getHolyPowerManager().shootFirework(player, 16);
			}
		}

		int newcycle = this.cycle - 1;
		if (newcycle > 0) {
			getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(getPlugin(), new TaskLove(this.player1, this.player2, newcycle), 40L);
		}
	}
}