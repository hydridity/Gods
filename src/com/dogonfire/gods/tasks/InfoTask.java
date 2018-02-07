package com.dogonfire.gods.tasks;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.dogonfire.gods.Gods;
import com.dogonfire.gods.LanguageManager;

public class InfoTask implements Runnable {
	private Gods plugin;
	private UUID playerId = null;
	private String name1 = null;
	private String name2 = null;
	private LanguageManager.LANGUAGESTRING message = null;
	private int amount = 0;
	private ChatColor color;

	public InfoTask(Gods instance, ChatColor color, UUID playerId, LanguageManager.LANGUAGESTRING m, int amount, String name1) {
		this.plugin = instance;
		this.playerId = playerId;
		this.message = m;
		this.name1 = name1;
		this.name2 = this.name2;
		this.amount = amount;
		this.color = color;
	}

	public InfoTask(Gods instance, ChatColor color, UUID playerId, LanguageManager.LANGUAGESTRING m, String name1, String name2) {
		this.plugin = instance;
		this.playerId = playerId;
		this.name1 = name1;
		this.name2 = name2;
		this.message = m;
		this.amount = this.amount;
		this.color = color;
	}

	public InfoTask(Gods instance, ChatColor color, UUID playerId, LanguageManager.LANGUAGESTRING m, String name, int amount1, int amount2) {
		this.plugin = instance;
		this.playerId = playerId;
		this.name1 = this.name1;
		this.name2 = String.valueOf(amount1);
		this.message = m;
		this.amount = amount2;
		this.color = color;
	}

	public void run() {
		Player player = this.plugin.getServer().getPlayer(this.playerId);

		if (player == null) {
			return;
		}

		this.plugin.getLanguageManager().setPlayerName(this.name1);

		try {
			this.plugin.getLanguageManager().setType(this.name2);
		} catch (Exception ex) {
			this.plugin.logDebug(ex.getStackTrace().toString());
		}

		this.plugin.getLanguageManager().setAmount(this.amount);

		String questionMessage = this.plugin.getLanguageManager().getInfoString(this.message, this.color);

		player.sendMessage(

				this.color + questionMessage);
	}
}