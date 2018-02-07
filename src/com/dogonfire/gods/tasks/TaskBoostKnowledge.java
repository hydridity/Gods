package com.dogonfire.gods.tasks;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.dogonfire.gods.Gods;

public class TaskBoostKnowledge implements Runnable {
	private Player player;
	private long amount;

	public TaskBoostKnowledge(Gods instance, Player player, long amount) {
		this.amount = amount;
		this.player = player;
	}

	public void run() {
		this.player.playSound(this.player.getLocation(), Sound.AMBIENT_CAVE, 1.0F, 0.1F);

		this.player.setExp(this.player.getExp() + (float) this.amount);
	}
}
