package com.dogonfire.gods.tasks;

import com.dogonfire.gods.Gods;

public abstract class Task implements Runnable {

	public abstract void run();

	protected Gods getPlugin() {
		return Gods.get();
	}

}
