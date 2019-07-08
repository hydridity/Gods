package com.dogonfire.gods.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.dogonfire.gods.Gods;
import com.dogonfire.gods.managers.BelieverManager;
import com.dogonfire.gods.managers.GodManager;

public class CommandFollowers extends GodsCommand {

	protected CommandFollowers() {
		super("followers");
		this.permission = "gods.followers";
		this.parameters = "<god>";
		this.description = "Show the followers of your/a God";
	}

	@Override
	public void onCommand(CommandSender sender, String command, String... args) {
		if (!hasPermission(sender)) {
			sender.sendMessage(stringNoPermission);
			return;
		}
		if (sender instanceof Player == false) {
			sender.sendMessage(stringPlayerOnly);
			return;
		}
		Player player = (Player) sender;
		List<Believer> believers = new ArrayList<Believer>();
		String playerGod = null;

		String godName = "";
		if (args.length >= 2) {
			godName = GodManager.get().formatGodName(args[1]);
		} else {
			godName = BelieverManager.get().getGodForBeliever(player.getUniqueId());
		}

		if (godName == null) {
			sender.sendMessage(ChatColor.RED + "You do not believe in a God");
			return;
		}

		Set<UUID> list = BelieverManager.get().getBelieversForGod(godName);

		for (UUID believerId : list) {
			Date lastPrayer = BelieverManager.get().getLastPrayerTime(believerId);
			believers.add(new Believer(believerId, lastPrayer));
		}

		if (believers.size() == 0) {
			sender.sendMessage(ChatColor.GOLD + godName + ChatColor.AQUA + " has no believers!");
			return;
		}

		playerGod = BelieverManager.get().getGodForBeliever(player.getUniqueId());
		sender.sendMessage(ChatColor.YELLOW + "--------- The Followers of " + godName + " ---------");
		Collections.sort(believers, new BelieversComparator());

		int l = believers.size();

		List<Believer> believersList = believers;

		if (l > 15) {
			believersList = believersList.subList(0, 15);
		}

		boolean playerShown = false;

		Date thisDate = new Date();

		for (Believer believer : believersList) {
			long minutes = (thisDate.getTime() - believer.lastPrayer.getTime()) / 60000L;
			long hours = (thisDate.getTime() - believer.lastPrayer.getTime()) / 3600000L;
			long days = (thisDate.getTime() - believer.lastPrayer.getTime()) / 86400000L;

			String date = "";
			if (days > 0L) {
				date = days + " days ago";
			} else if (hours > 0L) {
				date = hours + " hours ago";
			} else {
				date = minutes + " min ago";
			}

			String believerName = Gods.get().getServer().getOfflinePlayer(believer.believerId).getName();

			if (sender != null) {
				if (playerGod != null && (believer.believerId.equals(player.getUniqueId()))) {
					playerShown = true;
					sender.sendMessage(ChatColor.GOLD + StringUtils.rightPad(believerName, 20) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Prayed ").append(ChatColor.GOLD).append(date).toString(), 18));
				} else {
					sender.sendMessage(ChatColor.YELLOW + StringUtils.rightPad(believerName, 20) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Prayed ").append(ChatColor.GOLD).append(date).toString(), 18));
				}
			} else {
				Gods.get().log(StringUtils.rightPad(believerName, 20) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Prayed ").append(ChatColor.GOLD).append(date).toString(), 18));
			}
		}

		if ((playerGod != null) && (!playerShown)) {
			for (Believer believer : believers) {
				String believerName = Gods.get().getServer().getOfflinePlayer(believer.believerId).getName();

				if ((playerGod != null) && (believer.believerId.equals(player.getUniqueId()))) {
					sender.sendMessage(ChatColor.GOLD + StringUtils.rightPad(believerName, 20) + StringUtils.rightPad(new StringBuilder().append(" Prayed ").append(believer.lastPrayer).toString(), 18));
				}
			}
		}
	}
}

class Believer {
	public UUID believerId;
	public Date lastPrayer;

	Believer(UUID believerId, Date lastPrayer) {
		this.believerId = believerId;
		this.lastPrayer = lastPrayer;
	}
}

class BelieversComparator implements Comparator<Object> {
	public BelieversComparator() {
	}

	@Override
	public int compare(Object object1, Object object2) {
		Believer b1 = (Believer) object1;
		Believer b2 = (Believer) object2;

		return (int) (b2.lastPrayer.getTime() - b1.lastPrayer.getTime());
	}
}
