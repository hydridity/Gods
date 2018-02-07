package com.dogonfire.gods.tasks;

import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.dogonfire.gods.Gods;

public class SpawnHostileMobsTask implements Runnable {
	private EntityType mobType;
	private int numberOfMobs = 0;
	private Player player;

	public SpawnHostileMobsTask(Gods instance, String god, Player p, EntityType entityType, int n) {
		this.numberOfMobs = n;
		this.player = p;
		new String(god);
		this.mobType = entityType;
	}

	public void run() {
		for (int i = 0; i < this.numberOfMobs; i++) {
			Creature spawnedMob = (Creature) this.player.getWorld().spawnEntity(this.player.getLocation(), this.mobType);
			spawnedMob.setTarget(this.player);
		}
	}
}
