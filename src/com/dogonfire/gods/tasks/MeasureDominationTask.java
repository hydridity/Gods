package com.dogonfire.gods.tasks;

import java.util.Random;

import org.bukkit.Location;

import com.dogonfire.gods.Gods;

public class MeasureDominationTask implements Runnable {
	private Gods plugin;

	public MeasureDominationTask(Gods instance, Location location) {
		this.plugin = instance;
	}

	public void run() {
		new Random();

		this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, this, 40L);
	}
}
