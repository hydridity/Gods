package com.dogonfire.gods.tasks;

public class TaskSetupGlobalQuest extends Task {
	public void run() {
		getPlugin().getQuestManager().GodSayNewGlobalQuest();
	}
}
