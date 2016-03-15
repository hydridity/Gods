package com.dogonfire.gods;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener
{
	private Gods plugin;

	ChatListener(Gods p)
	{
		this.plugin = p;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();

		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		if (this.plugin.chatFormattingEnabled)
		{
			event.setFormat(this.plugin.getChatManager().formatChat(event.getPlayer(), godName, event.getFormat()));
		}
		
		if (godName == null)
		{
			return;
		}
		if (this.plugin.getBelieverManager().getReligionChat(player.getUniqueId()))
		{
			event.setCancelled(true);
			for (Player otherPlayer : this.plugin.getServer().getOnlinePlayers())
			{
				String otherGod = this.plugin.getBelieverManager().getGodForBeliever(otherPlayer.getUniqueId());
				if ((otherGod != null) && (otherGod.equals(godName)))
				{
					if (this.plugin.getGodManager().isPriest(player.getUniqueId()))
					{
						otherPlayer.sendMessage(ChatColor.YELLOW + "[" + godName + "Chat] " + player.getName() + ": " + ChatColor.WHITE + event.getMessage());
					}
					else
					{
						otherPlayer.sendMessage(ChatColor.YELLOW + "[" + godName + "Chat] " + ChatColor.RED + player.getName() + ChatColor.YELLOW + ": " + ChatColor.WHITE + event.getMessage());
					}
				}
			}
			this.plugin.log(player.getName() + "(GODCHAT): " + event.getMessage());
		}
	}
}