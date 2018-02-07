package com.dogonfire.gods.tasks;

import com.dogonfire.gods.Gods;

public class TaskSetupGlobalQuest implements Runnable {
	private Gods plugin;

	public TaskSetupGlobalQuest(Gods instance) {
		this.plugin = instance;
	}

	public void run() {
		this.plugin.getQuestManager().GodSayNewGlobalQuest();
	}
}
