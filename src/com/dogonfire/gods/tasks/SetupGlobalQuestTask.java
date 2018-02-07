package com.dogonfire.gods.tasks;

import com.dogonfire.gods.Gods;

public class SetupGlobalQuestTask implements Runnable {
	private Gods plugin;

	public SetupGlobalQuestTask(Gods instance) {
		this.plugin = instance;
	}

	public void run() {
		this.plugin.getQuestManager().GodSayNewGlobalQuest();
	}
}
