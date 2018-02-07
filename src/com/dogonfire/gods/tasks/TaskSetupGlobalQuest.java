package com.dogonfire.gods.tasks;

import com.dogonfire.gods.managers.QuestManager;

public class TaskSetupGlobalQuest extends Task {
	public void run() {
		QuestManager.get().GodSayNewGlobalQuest();
	}
}
