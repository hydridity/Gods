package com.dogonfire.gods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.Vector;

public class Commands
{
	private Gods plugin = null;

	Commands(Gods p)
	{
		this.plugin = p;
	}

	private boolean CommandList(CommandSender sender)
	{
		if (sender != null && !sender.isOp() && !this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.list"))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		Player player = (Player)sender;
		List<God> gods = new ArrayList();
		String playerGod = null;

		Set<String> list = this.plugin.getGodManager().getTopGods();
		for (String godName : list)
		{
			int power = (int) this.plugin.getGodManager().getGodPower(godName);

			int believers = this.plugin.getBelieverManager().getBelieversForGod(godName).size();
			if (believers > 0)
			{
				gods.add(new God(godName, power, believers));
			}
		}
		
		if (gods.size() == 0)
		{
			if (sender != null)
			{
				sender.sendMessage(ChatColor.GOLD + "There are no Gods in " + this.plugin.serverName + "!");
			}
			else
			{
				this.plugin.log("There are no Gods in " + this.plugin.serverName + "!");
			}
			return true;
		}
		
		if (sender != null)
		{
			playerGod = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
			sender.sendMessage(ChatColor.YELLOW + "--------- The Gods of " + this.plugin.serverName + " ---------");
		}
		else
		{
			this.plugin.log("--------- The Gods of " + this.plugin.serverName + " ---------");
		}
		
		Collections.sort(gods, new TopGodsComparator());

		int l = gods.size();

		List<God> topGods = gods;
		if (l > 15)
		{
			topGods = ((List) topGods).subList(0, 15);
		}
		
		int n = 1;
		boolean playerGodShown = false;
		
		for (God god : topGods)
		{
			String fullGodName = String.format("%-16s", new Object[] { god.name }) + "   " + String.format("%-16s", new Object[] { this.plugin.getGodManager().getTitleForGod(god.name) });
			if (sender != null)
			{
				if ((playerGod != null) && (god.name.equals(playerGod)))
				{
					playerGodShown = true;
					sender.sendMessage(ChatColor.GOLD +	String.format("%2d", new Object[] { Integer.valueOf(n) }) + " - " +

					fullGodName + ChatColor.GOLD + StringUtils.rightPad(new StringBuilder().append(" Power ").append(god.power).toString(), 2) + StringUtils.rightPad(new StringBuilder().append(" Believers ").append(god.believers).toString(), 2));
				}
				else
				{
					sender.sendMessage(ChatColor.YELLOW + String.format("%2d", new Object[] { Integer.valueOf(n) }) + ChatColor.AQUA + " - " + fullGodName + ChatColor.GOLD + StringUtils.rightPad(new StringBuilder().append(" Power ").append(god.power).toString(), 2) + StringUtils.rightPad(new StringBuilder().append(" Believers ").append(god.believers).toString(), 2));
				}
			}
			else
			{
				this.plugin.log(String.format("%2d", new Object[] { Integer.valueOf(n) }) + " - " + fullGodName + ChatColor.GOLD + StringUtils.rightPad(new StringBuilder().append(" Mood ").append(this.plugin.getGodManager().getExactMoodForGod(god.name)).toString(), 2) + StringUtils.rightPad(new StringBuilder().append(" Power ").append(god.power).toString(), 2) + StringUtils.rightPad(new StringBuilder().append(" Believers ").append(god.believers).toString(), 2));
			}
			n++;
		}
		
		n = 1;
		
		if ((playerGod != null) && (!playerGodShown))
		{
			for (God god : gods)
			{
				String fullGodName = String.format("%-16s", new Object[] { god.name }) + "   " + String.format("%-16s", new Object[] { this.plugin.getGodManager().getTitleForGod(god.name) });
				if ((playerGod != null) && (god.name.equals(playerGod)))
				{
					playerGodShown = true;
					sender.sendMessage("" + ChatColor.GOLD + n + " - " + fullGodName + StringUtils.rightPad(new StringBuilder().append(" Power ").append(god.power).toString(), 2) + StringUtils.rightPad(new StringBuilder().append(" Believers ").append(god.believers).toString(), 2));
				}
				n++;
			}
		}
		if (sender != null)
		{
			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.InfoHelp, ChatColor.AQUA, 0, ChatColor.WHITE + "/g info <godname>", 80);
		}
		return true;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player player = null;
		
		if ((sender instanceof Player))
		{
			player = (Player) sender;
		}
		
		if (player == null)
		{
			if ((cmd.getName().equalsIgnoreCase("gods")) || (cmd.getName().equalsIgnoreCase("g")))
			{
				if ((args.length == 1) && (args[0].equalsIgnoreCase("reload")))
				{
					this.plugin.reloadSettings();
					this.plugin.loadSettings();
					this.plugin.getQuestManager().load();
					this.plugin.getGodManager().load();
					this.plugin.getBelieverManager().load();

					return true;
				}
				CommandList(null);
			}
			return true;
		}
				
		if ((cmd.getName().equalsIgnoreCase("gods")) || (cmd.getName().equalsIgnoreCase("g")))
		{
			if (args.length == 0)
			{
				CommandGods(sender);
				this.plugin.log(sender.getName() + " /gods");
				return true;
			}
			if (args[0].equalsIgnoreCase("reload"))
			{
				if (CommandReload(sender))
				{
					this.plugin.log(sender.getName() + " /gods reload");
				}
				return true;
			}
//			if (args[0].equalsIgnoreCase("regen"))
//			{
//				EndManager endManager = new EndManager(this.plugin);
//				endManager.init();
//
//				this.plugin.log(sender.getName() + " /gods regen");
//
//				return true;
//			}
			if (args[0].equalsIgnoreCase("info"))
			{
				if (CommandInfo(sender, args))
				{
					this.plugin.log(sender.getName() + " /gods info");
				}
				return true;
			}
			if ((args[0].equalsIgnoreCase("c")) || (args[0].equalsIgnoreCase("chat")))
			{
				if (CommandChat(sender, args))
				{
					this.plugin.log(sender.getName() + " /gods chat");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("list"))
			{
				if (CommandList(sender))
				{
					this.plugin.log(sender.getName() + " /gods list");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("marriages"))
			{
				if (CommandMarriages(sender, args))
				{
					this.plugin.log(sender.getName() + " /gods marriages");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("followers"))
			{
				if (CommandFollowers(sender, args))
				{
					this.plugin.log(sender.getName() + " /gods followers");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("love"))
			{
				if (CommandLove(player, args))
				{
					this.plugin.log(sender.getName() + " /gods love");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("divorce"))
			{
				if (CommandDivorce(player, args))
				{
					this.plugin.log(sender.getName() + " /gods divorce");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("sethome"))
			{
				if (CommandSetHome(player, args))
				{
					this.plugin.log(sender.getName() + " /gods sethome");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("home"))
			{
				if (CommandHome(player, args))
				{
					this.plugin.log(sender.getName() + " /gods home");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("bible"))
			{
				if (CommandBible(sender, args))
				{
					this.plugin.log(sender.getName() + " /gods bible");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("editbible"))
			{
				if (CommandEditBible(sender, args))
				{
					this.plugin.log(sender.getName() + " /gods editbible");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("setbible"))
			{
				if (CommandSetBible(sender, args))
				{
					this.plugin.log(sender.getName() + " /gods setbible");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("pvp"))
			{
				if (CommandTogglePvP(player, args))
				{
					this.plugin.log(sender.getName() + " /gods pvp");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("hunt"))
			{
				if (CommandHunt(player, args))
				{
					this.plugin.log(sender.getName() + " /gods hunt");
				}
				return true;
			}
			
			if ((args[0].equalsIgnoreCase("open")) || (args[0].equalsIgnoreCase("close")))
			{
				CommandAccess(player, args);

				return true;
			}
			
			if (args[0].equalsIgnoreCase("leave"))
			{
				String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

				if (this.plugin.getGodManager().believerLeaveGod(player.getUniqueId()))
				{
					sender.sendMessage(ChatColor.AQUA + "You left the religion of " + ChatColor.YELLOW + godName);
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "You are not part of any religion!");
				}
				
				return true;
			}
			
			if (args[0].equalsIgnoreCase("help"))
			{
				if (args.length > 1)
				{
					if (args[1].equalsIgnoreCase("altar"))
					{
						if (CommandHelpAltar(sender, args))
						{
							this.plugin.log(sender.getName() + " /gods help altar" + args[1]);
						}
						return true;
					}
					
					if (args[1].equalsIgnoreCase("blocks"))
					{
						if (CommandHelpBlocks(sender, args))
						{
							this.plugin.log(sender.getName() + " /gods help blocks" + args[1]);
						}
						return true;
					}
				}
				else if (CommandHelp(sender))
				{
					this.plugin.log(sender.getName() + " /gods help");
				}
				
				return true;
			}
			if (args[0].equalsIgnoreCase("marry"))
			{
				if (args.length!=2)
				{
					sender.sendMessage(ChatColor.WHITE + "/g marry <playername> - " + ChatColor.AQUA + " propose to another player for marriage");
					return false;
				}

				if (CommandMarry(player, args))
				{
					this.plugin.log(sender.getName() + " /gods marry " + args[1]);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("prayfor"))
			{
				if (args.length!=2)
				{
					CommandHelpPrayFor(sender);					
					return false;
				}
				
				if (!this.plugin.getPermissionsManager().hasPermission(player, "gods.pray"))
				{
					sender.sendMessage(ChatColor.RED + "You do not have permission for that");
					return false;
				}

				if (args[1].equalsIgnoreCase("item"))
				{
					CommandPrayForItem(sender);

					return true;
				}
				if (args[1].equalsIgnoreCase("health"))
				{
					CommandPrayForHealth(sender);

					return true;
				}
				if (args[1].equalsIgnoreCase("blessing"))
				{
					CommandPrayForBlessing(sender);

					return true;
				}
				
//				if (args[1].equalsIgnoreCase("artifact"))
//				{
//					CommandPrayForArtifact(sender, args);
//
//					return true;
//				}
				
				if(plugin.questsEnabled)
				{
					if (args[1].equalsIgnoreCase("quest"))
					{
						CommandPrayForQuest(sender);
						return true;
					}
				}				
				
				CommandHelpPrayFor(sender);												
				return false;
			}
			if (args[0].equalsIgnoreCase("invite"))
			{
				if (CommandInvite(player, args))
				{
					this.plugin.log(sender.getName() + " /gods invite " + args[1]);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("war"))
			{
				if (CommandWar(player, args))
				{
					this.plugin.log(sender.getName() + " /gods war " + args[1]);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("ally"))
			{
				if (CommandAlliance(player, args))
				{
					this.plugin.log(sender.getName() + " /gods ally " + args[1]);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("info"))
			{
				if (CommandInfo(sender, args))
				{
					this.plugin.log(sender.getName() + " /gods info " + args[1]);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("followers"))
			{
				if (CommandFollowers(sender, args))
				{
					this.plugin.log(sender.getName() + " /gods followers " + args[1]);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("check"))
			{
				if (args.length!=2)
				{
					sender.sendMessage(ChatColor.WHITE + "/g check <godname>");
					return false;
				}				

				Player otherPlayer = plugin.getServer().getPlayer(args[1]);
												
				if (CommandCheck(sender, otherPlayer))
				{
					this.plugin.log(sender.getName() + " /gods check " + args[1]);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("kick"))
			{
				if (args.length!=2)
				{
					sender.sendMessage(ChatColor.WHITE + "/g kick <playername>");
					return false;
				}				

				if (CommandKick(sender, args))
				{
					this.plugin.log(sender.getName() + " /gods kick " + args[1]);
				}
				
				return true;
			}
			if (args[0].equalsIgnoreCase("startattack"))
			{
				if (CommandStartAttackHolyLand(sender, args))
				{
					this.plugin.log(sender.getName() + " /startattack");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("attack"))
			{
				if (CommandAttackHolyLand(sender, args))
				{
					this.plugin.log(sender.getName() + " /g attack");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("defend"))
			{
				if (CommandDefendHolyLand(sender, args))
				{
					this.plugin.log(sender.getName() + " /g defend");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("desc"))
			{
				if (CommandSetDescription(sender, args))
				{
					this.plugin.log(sender.getName() + " /gods desc " + args[1]);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("setsafe"))
			{
				if (CommandSetNeutralLand(sender, args))
				{
					this.plugin.log(sender.getName() + " /gods setsafe");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("holyartifact"))
			{
				if (CommandGiveHolyArtifact(sender, args))
				{
					this.plugin.log(sender.getName() + " /gods holyartifact " + args[1]);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("setpriest"))
			{
				if (CommandSetPriest(player, args))
				{
					this.plugin.log(sender.getName() + " /gods setpriest " + args[1]);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("desc"))
			{
				if (CommandSetDescription(sender, args))
				{
					this.plugin.log(sender.getName() + " /gods desc " + args[1]);
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("yes"))
			{
				if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.accept")))
				{
					sender.sendMessage(ChatColor.RED + "You do not have permission for that");
					return false;
				}
				
				this.plugin.getGodManager().believerAccept(player.getUniqueId());

				return true;
			}
			if (args[0].equalsIgnoreCase("no"))
			{
				if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.reject")))
				{
					sender.sendMessage(ChatColor.RED + "You do not have permission for that");
					return false;
				}
				
				this.plugin.getGodManager().believerReject(player.getUniqueId());
				
				return true;
			}
			
			sender.sendMessage(ChatColor.RED + "Invalid Gods command!");
			return true;
		}
		return true;
	}

	private boolean CommandInfo(CommandSender sender, String[] args)
	{
		if ((sender != null) && !sender.isOp() && !this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.info"))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		Player player = (Player)sender;
		String godName = null;
		
		if (args.length == 2)
		{
			godName = args[1];
		}
		
		if (godName == null)
		{
			godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
			if (godName == null)
			{
				sender.sendMessage(ChatColor.RED + "You do not believe in any God.");
				return true;
			}
		}
		
		godName = this.plugin.getGodManager().formatGodName(godName);
		
		if (!this.plugin.getGodManager().godExist(godName))
		{
			sender.sendMessage(ChatColor.RED + "There is no God with such name.");
			return true;
		}
		
		List<UUID> priests = this.plugin.getGodManager().getPriestsForGod(godName);
		
		if (priests == null || priests.size() == 0)
		{
			priests = new ArrayList();
		}
		
		sender.sendMessage(ChatColor.YELLOW + "--------- " + godName + " " + this.plugin.getGodManager().getColorForGod(godName) + this.plugin.getGodManager().getTitleForGod(godName) + ChatColor.YELLOW + " ---------");

		sender.sendMessage("" + ChatColor.DARK_PURPLE + ChatColor.ITALIC + this.plugin.getGodManager().getGodDescription(godName));

		ChatColor moodColor = ChatColor.AQUA;
		GodManager.GodMood godMood = this.plugin.getGodManager().getMoodForGod(godName);
		
		switch (godMood)
		{
			case EXALTED: moodColor = ChatColor.GOLD; break;
			case PLEASED: moodColor = ChatColor.DARK_GREEN; break;
			case NEUTRAL: moodColor = ChatColor.WHITE; break;
			case DISPLEASED: moodColor = ChatColor.GRAY; break;
			case ANGRY: moodColor = ChatColor.DARK_RED; break;
		}
		
		sender.sendMessage(moodColor + godName + " is " + this.plugin.getLanguageManager().getGodMoodName(godMood));

		Material neededItem = this.plugin.getGodManager().getSacrificeItemTypeForGod(godName);
		if (neededItem != null)
		{
			sender.sendMessage(ChatColor.GOLD + godName + ChatColor.AQUA + " wants more " + ChatColor.WHITE + this.plugin.getLanguageManager().getItemTypeName(neededItem));
		}
		
		if (priests.size() == 0)
		{
			sender.sendMessage(ChatColor.AQUA + "Priest: " + ChatColor.YELLOW + "None");
		}
		else if (priests.size() == 1)
		{
			sender.sendMessage(ChatColor.AQUA + "Priest: " + ChatColor.YELLOW + plugin.getServer().getOfflinePlayer(priests.get(0)).getName());
		}
		else
		{
			sender.sendMessage(ChatColor.AQUA + "Priests: ");
			for (UUID priest : priests)
			{
				sender.sendMessage(ChatColor.YELLOW + " - " + plugin.getServer().getOfflinePlayer(priest).getName());
			}
		}
		
		sender.sendMessage(ChatColor.AQUA + "Believers: " + ChatColor.YELLOW + this.plugin.getBelieverManager().getBelieversForGod(godName).size());
		sender.sendMessage(ChatColor.AQUA + "Exact power: " + ChatColor.YELLOW + this.plugin.getGodManager().getGodPower(godName));
		if (this.plugin.commandmentsEnabled)
		{
			sender.sendMessage(ChatColor.AQUA + "Holy food: " + ChatColor.YELLOW + this.plugin.getLanguageManager().getItemTypeName(this.plugin.getGodManager().getEatFoodTypeForGod(godName)));
			sender.sendMessage(ChatColor.AQUA + "Unholy food: " + ChatColor.YELLOW + this.plugin.getLanguageManager().getItemTypeName(this.plugin.getGodManager().getNotEatFoodTypeForGod(godName)));

			sender.sendMessage(ChatColor.AQUA + "Holy creature: " + ChatColor.YELLOW + this.plugin.getLanguageManager().getMobTypeName(this.plugin.getGodManager().getHolyMobTypeForGod(godName)));
			sender.sendMessage(ChatColor.AQUA + "Unholy creature: " + ChatColor.YELLOW + this.plugin.getLanguageManager().getMobTypeName(this.plugin.getGodManager().getUnholyMobTypeForGod(godName)));
		}
		
		List<String> allyRelations = this.plugin.getGodManager().getAllianceRelations(godName);
		Object warRelations = this.plugin.getGodManager().getWarRelations(godName);
		
		if ((((List) warRelations).size() > 0) || (allyRelations.size() > 0))
		{
			sender.sendMessage(ChatColor.AQUA + "Religious relations: ");
			for (String ally : this.plugin.getGodManager().getAllianceRelations(godName))
			{
				sender.sendMessage(ChatColor.GREEN + " Alliance with " + ChatColor.GOLD + ally);
			}
			List<String> enemies = this.plugin.getGodManager().getWarRelations(godName);
			for (String enemy : enemies)
			{
				sender.sendMessage(ChatColor.RED + " War with " + ChatColor.GOLD + enemy);
			}
		}
		return true;
	}

	private boolean CommandCheck(CommandSender sender, Player believer)
	{
		if ((sender != null) && (!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.check")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		if(believer==null)
		{
			sender.sendMessage(ChatColor.RED + "No such player with that name");
			return false;
		}
				
		String godName = this.plugin.getBelieverManager().getGodForBeliever(believer.getUniqueId());		
		
		if (godName == null)
		{
			sender.sendMessage(ChatColor.AQUA + believer.getDisplayName() + " does not believe in a god");
		}
		else if (this.plugin.getGodManager().isPriest(believer.getUniqueId()))
		{
			sender.sendMessage(ChatColor.AQUA + believer.getDisplayName() + " is the Priest of " + ChatColor.YELLOW + godName);
		}
		else				
		{
			sender.sendMessage(ChatColor.AQUA + believer.getDisplayName() + " believes in " + ChatColor.YELLOW + godName);
		}
		return true;
	}

	private boolean CommandGods(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "------------------ " + this.plugin.getDescription().getFullName() + " ------------------");
		sender.sendMessage(ChatColor.AQUA + "By DogOnFire");
		sender.sendMessage("" + ChatColor.AQUA);
		sender.sendMessage(ChatColor.AQUA + "There are currently " + ChatColor.WHITE + this.plugin.getGodManager().getAllGods().size() + ChatColor.AQUA + " Gods and");
		sender.sendMessage("" + ChatColor.WHITE + this.plugin.getBelieverManager().getBelievers().size() + ChatColor.AQUA + " believers in " + this.plugin.serverName);
		sender.sendMessage("" + ChatColor.AQUA);

		if (sender != null && sender instanceof Player)
		{
			Player player = (Player)sender;
			
			String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

			if (godName != null)
			{
				List<UUID> priests = this.plugin.getGodManager().getPriestsForGod(godName);
				if (priests == null || !priests.contains(player.getUniqueId()))
				{
					sender.sendMessage(ChatColor.WHITE + "You believe in " + ChatColor.GOLD + godName);
				}
				else
				{
					sender.sendMessage(ChatColor.WHITE + "You are the priest of " + ChatColor.GOLD + godName);
				}

				if(plugin.prayersEnabled)
				{
					sender.sendMessage(ChatColor.WHITE + "You have " + ChatColor.GOLD + plugin.getBelieverManager().getPrayerPower(player.getUniqueId()) + ChatColor.WHITE + " prayer power");
				}
			}
			else
			{
				sender.sendMessage(ChatColor.RED + "You do not believe in any god");
			}
			
			if (this.plugin.marriageEnabled)
			{
				String partnerName = this.plugin.getMarriageManager().getPartnerName(player.getUniqueId());
				if (partnerName != null)
				{
					sender.sendMessage(ChatColor.WHITE + "You are married to " + ChatColor.GOLD + partnerName);
				}
			}
			sender.sendMessage("" + ChatColor.AQUA);

			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.GodsHelp, ChatColor.AQUA, 0, ChatColor.WHITE + "/g help", 80);
			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.AltarHelp, ChatColor.AQUA, 0, ChatColor.WHITE + "/g help altar", 160);
			
			if(plugin.prayersEnabled)
			{
				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.PrayForHelp, ChatColor.AQUA, 0, ChatColor.WHITE + "/g prayfor", 240);
			}
		}
				
		//sender.sendMessage(ChatColor.AQUA + "Use " + ChatColor.WHITE + "/gods help" + ChatColor.AQUA + " for a list of commands");
		//sender.sendMessage(ChatColor.AQUA + "Use " + ChatColor.WHITE + "/gods help altar" + ChatColor.AQUA + " for info about how to build an altar");

		return true;
	}

	
	private void CommandHelpPrayFor(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "--------------- How to pray for things ---------------");
		sender.sendMessage(ChatColor.AQUA + "Pray for something specific from your God:");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.WHITE + "/g prayfor item - " + ChatColor.AQUA + " pray for an item");
		sender.sendMessage(ChatColor.WHITE + "/g prayfor health - " + ChatColor.AQUA + " pray for health");
		sender.sendMessage(ChatColor.WHITE + "/g prayfor blessing - " + ChatColor.AQUA + " pray for a magical blessing");
		if(plugin.questsEnabled)
		{
			sender.sendMessage(ChatColor.WHITE + "/g prayfor quest - " + ChatColor.AQUA + " pray for a quest");					
		}
		sender.sendMessage("");
		sender.sendMessage(ChatColor.AQUA + "Note that you need prayer power to perform these prayers.");		
	}
	
	private boolean CommandHelpAltar(CommandSender sender, String[] args)
	{
		sender.sendMessage(ChatColor.YELLOW + "--------------- How to build an Altar ---------------");
		sender.sendMessage(ChatColor.AQUA + "Build an altar to your God by following these simple steps:");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.WHITE + "  1  - Place a block");
		sender.sendMessage(ChatColor.WHITE + "  2a - Place a torch on top for a male god");
		sender.sendMessage(ChatColor.WHITE + "  2b - Place a redstone torch on top for a female god");
		sender.sendMessage(ChatColor.WHITE + "  3  - Place a sign on the side of the block");
		sender.sendMessage(ChatColor.WHITE + "  4  - Write the name of your God on the sign");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.AQUA + "You can now pray to your God by right-clicking the sign!");
		sender.sendMessage(ChatColor.AQUA + "Check what type of god you can make with " + ChatColor.WHITE + "/g help blocks");

		return true;
	}

	private boolean CommandHelpBlocks(CommandSender sender, String[] args)
	{
		sender.sendMessage(ChatColor.YELLOW + "--------------- God Types ---------------");
		sender.sendMessage(ChatColor.AQUA + "These are the block types you can use for making Gods:");
		sender.sendMessage("");
		for (GodManager.GodType godType : GodManager.GodType.values())
		{
			List<String> materials = this.plugin.getAltarManager().getAltarBlockTypesFromGodType(godType);
			if (materials != null)
			{
				for (String blockMaterial : materials)
				{
					sender.sendMessage(ChatColor.WHITE + blockMaterial + "  --->  " + this.plugin.getGodManager().getColorForGodType(godType) + this.plugin.getLanguageManager().getGodTypeName(godType, "God"));
				}
			}
		}
		sender.sendMessage("");
		sender.sendMessage(ChatColor.AQUA + "Check how to build an altar with " + ChatColor.WHITE + "/g help altar");

		return true;
	}

	private boolean CommandHelp(CommandSender sender)
	{
		Player player = (Player) sender;
				
		if ((sender != null) && (!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.help")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		sender.sendMessage(ChatColor.YELLOW + "------------------ " + this.plugin.getDescription().getFullName() + " ------------------");
		sender.sendMessage(ChatColor.AQUA + "/gods" + ChatColor.WHITE + " - Basic info");

		sender.sendMessage(ChatColor.AQUA + "/gods help altar" + ChatColor.WHITE + " - How to build an altar to a God");
		sender.sendMessage(ChatColor.AQUA + "/gods help blocks" + ChatColor.WHITE + " - What type of blocks are used for God altars");
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.list")))
		{
			sender.sendMessage(ChatColor.AQUA + "/gods list" + ChatColor.WHITE + " - List of all gods");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.info")))
		{
			sender.sendMessage(ChatColor.AQUA + "/gods info" + ChatColor.WHITE + " - Show info about your God");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.info")))
		{
			sender.sendMessage(ChatColor.AQUA + "/gods info <godname>" + ChatColor.WHITE + " - Show info about a specific God");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.marriages")))
		{
			sender.sendMessage(ChatColor.AQUA + "/gods marriages" + ChatColor.WHITE + " - List the most loving married couples");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.marry")))
		{
			sender.sendMessage(ChatColor.AQUA + "/gods marry" + ChatColor.WHITE + " - Ask another player to marry you");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.love")))
		{
			sender.sendMessage(ChatColor.AQUA + "/gods love" + ChatColor.WHITE + " - Love your partner");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.followers")))
		{
			sender.sendMessage(ChatColor.AQUA + "/gods followers" + ChatColor.WHITE + " - Show the followers of your God");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.followers")))
		{
			sender.sendMessage(ChatColor.AQUA + "/gods followers <godname>" + ChatColor.WHITE + " - Show followers of a God");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.check")))
		{
			sender.sendMessage(ChatColor.AQUA + "/gods check <playername>" + ChatColor.WHITE + " - Show religion for a player");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.chat")))
		{
			sender.sendMessage(ChatColor.AQUA + "/gods chat" + ChatColor.WHITE + " - Chat only with believers within your religion");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.home")))
		{
			sender.sendMessage(ChatColor.AQUA + "/gods home" + ChatColor.WHITE + " - Teleports you to your religion home");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.sethome")))
		{
			sender.sendMessage(ChatColor.AQUA + "/gods sethome" + ChatColor.WHITE + " - Sets the home of your religion");
		}
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.leave")))
		{
			sender.sendMessage(ChatColor.AQUA + "/gods leave" + ChatColor.WHITE + " - Leave your religion");
		}
		
		sender.sendMessage(ChatColor.AQUA + "/gods yes" + ChatColor.WHITE + " - Accept a proposal from your god");
		sender.sendMessage(ChatColor.AQUA + "/gods no" + ChatColor.WHITE + " - Reject a proposal from your god");
		
		if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.reload")))
		{
			sender.sendMessage(ChatColor.AQUA + "/gods reload" + ChatColor.WHITE + " - Reload config for gods system");
		}
		
		if (this.plugin.getGodManager().isPriest(player.getUniqueId()))
		{
			if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.priest.invite")))
			{
				sender.sendMessage(ChatColor.AQUA + "/gods invite <playername>" + ChatColor.WHITE + " - Invite a player to your religion");
			}
			if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.priest.kick")))
			{
				sender.sendMessage(ChatColor.AQUA + "/gods kick <playername>" + ChatColor.WHITE + " - Kick a believer from your religion");
			}
			if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.priest.bible")))
			{
				sender.sendMessage(ChatColor.AQUA + "/gods bible" + ChatColor.WHITE + " - Produces the Holy Book for your religion");
			}
			if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.priest.editbible")))
			{
				sender.sendMessage(ChatColor.AQUA + "/gods editbible" + ChatColor.WHITE + " - Edits the Holy Book for your religion");
			}
			if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.priest.setbible")))
			{
				sender.sendMessage(ChatColor.AQUA + "/gods setbible" + ChatColor.WHITE + " - Sets a book to be the Holy Book for your religion");
			}
			if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.priest.alliance")))
			{
				sender.sendMessage(ChatColor.AQUA + "/gods ally <godname>" + ChatColor.WHITE + " - Toggle alliance with another religion");
			}
			if ((sender.isOp()) || (this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.priest.war")))
			{
				sender.sendMessage(ChatColor.AQUA + "/gods war <godname>" + ChatColor.WHITE + " - Toggle war with another religion");
			}
			sender.sendMessage(ChatColor.AQUA + "/gods open" + ChatColor.WHITE + " - Set your religion as open to join for everyone");
			sender.sendMessage(ChatColor.AQUA + "/gods close" + ChatColor.WHITE + " - Set your religion as invite only");
			if (this.plugin.holyLandEnabled)
			{
				sender.sendMessage(ChatColor.AQUA + "/gods desc <text>" + ChatColor.WHITE + " - Set the description for your religion");
				sender.sendMessage(ChatColor.AQUA + "/gods pvp" + ChatColor.WHITE + " - Toggle pvp for your religon");
			}
		}
		return true;
	}

	private boolean CommandReload(CommandSender sender)
	{
		if ((!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.reload")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		this.plugin.loadSettings();

		this.plugin.getGodManager().load();
		this.plugin.getQuestManager().load();
		this.plugin.getBelieverManager().load();
		this.plugin.getWhitelistManager().load();

		sender.sendMessage(ChatColor.YELLOW + this.plugin.getDescription().getFullName() + ": " + ChatColor.WHITE + "Reloaded configuration.");
		this.plugin.log(sender.getName() + " /gods reload");

		return true;
	}

	private boolean CommandWar(Player player, String[] args)
	{
		if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.priest.war")))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		if (!this.plugin.getGodManager().isPriest(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.RED + "Only priests can declare religous wars");
			return false;
		}
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		String enemyGodName = this.plugin.getGodManager().formatGodName(args[1]);
		if (!this.plugin.getGodManager().godExist(args[1]))
		{
			player.sendMessage(ChatColor.RED + "There is no God with the name " + ChatColor.GOLD + args[1]);
			return false;
		}
		List<String> alliances = this.plugin.getGodManager().getAllianceRelations(godName);
		if (alliances.contains(enemyGodName))
		{
			player.sendMessage(ChatColor.RED + "You are ALLIED with " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
			return false;
		}
		if (this.plugin.getGodManager().toggleWarRelationForGod(godName, enemyGodName))
		{
			this.plugin.getLanguageManager().setPlayerName(godName);
			this.plugin.getGodManager().godSayToBelievers(enemyGodName, LanguageManager.LANGUAGESTRING.GodToBelieversWar, 10);

			this.plugin.getLanguageManager().setPlayerName(enemyGodName);
			this.plugin.getGodManager().godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversWar, 10);
		}
		else
		{
			this.plugin.getLanguageManager().setPlayerName(godName);
			this.plugin.getGodManager().godSayToBelievers(enemyGodName, LanguageManager.LANGUAGESTRING.GodToBelieversWarCancelled, 10);

			this.plugin.getLanguageManager().setPlayerName(enemyGodName);
			this.plugin.getGodManager().godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversWarCancelled, 10);
		}
		return true;
	}

	private boolean CommandAlliance(Player player, String[] args)
	{
		if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.priest.alliance")))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		if (!this.plugin.getGodManager().isPriest(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.RED + "Only priests can declare religous wars");
			return false;
		}
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		String allyGodName = this.plugin.getGodManager().formatGodName(args[1]);
		if (!this.plugin.getGodManager().godExist(args[1]))
		{
			player.sendMessage(ChatColor.RED + "There is no God with the name " + ChatColor.GOLD + args[1]);
			return false;
		}
		List wars = this.plugin.getGodManager().getWarRelations(godName);
		if (wars.contains(allyGodName))
		{
			player.sendMessage(ChatColor.RED + "You are in WAR with " + ChatColor.GOLD + args[1] + ChatColor.RED + "!");
			return false;
		}
		if (this.plugin.getGodManager().toggleAllianceRelationForGod(godName, allyGodName))
		{
			this.plugin.getLanguageManager().setPlayerName(godName);
			this.plugin.getGodManager().godSayToBelievers(allyGodName, LanguageManager.LANGUAGESTRING.GodToBelieversAlliance, 10);

			this.plugin.getLanguageManager().setPlayerName(allyGodName);
			this.plugin.getGodManager().godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversAlliance, 10);
		}
		else
		{
			this.plugin.getLanguageManager().setPlayerName(godName);
			this.plugin.getGodManager().godSayToBelievers(allyGodName, LanguageManager.LANGUAGESTRING.GodToBelieversAllianceCancelled, 10);

			this.plugin.getLanguageManager().setPlayerName(allyGodName);
			this.plugin.getGodManager().godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversAllianceCancelled, 10);
		}
		return true;
	}

	private boolean CommandAccess(Player player, String[] args)
	{
		if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.priest.access")))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		if (!this.plugin.getGodManager().isPriest(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.RED + "Only priests can set religion access");
			return false;
		}
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		if (!this.plugin.getGodManager().godExist(godName))
		{
			player.sendMessage(ChatColor.RED + "That God does not exist");
			return false;
		}
		String access = args[0];
		if (access.equalsIgnoreCase("open"))
		{
			this.plugin.getGodManager().setPrivateAccess(godName, false);
			this.plugin.log(player.getName() + " /gods open");
			player.sendMessage(ChatColor.AQUA + "You set the religion access to " + ChatColor.YELLOW + "open" + ChatColor.AQUA + ".");
			player.sendMessage(ChatColor.AQUA + "Players can join religion by praying at altars.");
		}
		else if (access.equalsIgnoreCase("close"))
		{
			this.plugin.getGodManager().setPrivateAccess(godName, true);
			this.plugin.log(player.getName() + " /gods close");
			player.sendMessage(ChatColor.AQUA + "You set the religion access to " + ChatColor.RED + "closed" + ChatColor.AQUA + ".");
			player.sendMessage(ChatColor.AQUA + "Players can now only pray to this religion by invitation.");
		}
		else
		{
			player.sendMessage(ChatColor.RED + "That is not a valid command");
			return false;
		}
		return true;
	}

	private boolean CommandInvite(Player player, String[] args)
	{		
		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "gods.priest.invite"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}		
		
		if (!this.plugin.getGodManager().isPriest(player.getUniqueId()))
		{
			player.sendMessage(ChatColor.RED + "Only priests can invite players");
			return false;
		}
		
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		
		if (godName==null)
		{
			player.sendMessage(ChatColor.RED + "You dont believe in a god");
			return false;
		}
		
		String playerName = args[1];
		
		Player invitedPlayer = this.plugin.getServer().getPlayer(playerName);
		if (invitedPlayer == null)
		{
			player.sendMessage(ChatColor.RED + "There is no player with the name '" + ChatColor.YELLOW + playerName + ChatColor.RED + " online.");
			return false;
		}
		
		String invitedPlayerGod = this.plugin.getBelieverManager().getGodForBeliever(invitedPlayer.getUniqueId());
		
		if ((invitedPlayerGod != null) && (invitedPlayerGod.equals(godName)))
		{
			player.sendMessage(ChatColor.YELLOW + playerName + ChatColor.RED + " already believes in '" + ChatColor.GOLD + godName + ChatColor.RED + "!");
			return false;
		}
		this.plugin.getBelieverManager().setInvitation(invitedPlayer.getUniqueId(), godName);

		this.plugin.log(godName + " invited to " + invitedPlayer.getName() + " to join the religion");
		this.plugin.getLanguageManager().setPlayerName(invitedPlayer.getName());

		this.plugin.getGodManager().GodSay(godName, invitedPlayer, LanguageManager.LANGUAGESTRING.GodToPlayerInvite, 10);

		this.plugin.sendInfo(invitedPlayer.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverQuestionHelp, ChatColor.AQUA, "/gods yes or /gods no", "/gods yes or /gods no", 40);

		player.sendMessage(ChatColor.AQUA + "You invited " + ChatColor.YELLOW + playerName + ChatColor.AQUA + " to join " + ChatColor.GOLD + godName + ChatColor.AQUA + "!");

		return true;
	}

	private boolean CommandMarry(Player player, String[] args)
	{
		if (!this.plugin.marriageEnabled)
		{
			player.sendMessage(ChatColor.RED + "Marrige is not enabled on this server");
			return false;
		}
		if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.marry")))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		String thisGodName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		if (thisGodName == null)
		{
			player.sendMessage(ChatColor.RED + "You do not believe in a God");
			return false;
		}
		
		if(args.length<2)
		{
			player.sendMessage(ChatColor.RED + "Marry who!?");
			return false;
		}
		
		String otherPlayerName = args[1];
		if (player.getName().equalsIgnoreCase(otherPlayerName))
		{
			player.sendMessage(ChatColor.RED + "Marry yourself!? Think again...");
			return false;
		}
				
		Player otherPlayer = this.plugin.getServer().getPlayer(otherPlayerName);
		if (otherPlayer == null)
		{
			player.sendMessage(ChatColor.RED + "There is no player with the name '" + ChatColor.WHITE + otherPlayerName + ChatColor.RED + " online.");
			return false;
		}

		String otherGodName = this.plugin.getBelieverManager().getGodForBeliever(otherPlayer.getUniqueId());
		
		if (otherGodName == null)
		{
			player.sendMessage(ChatColor.WHITE + otherPlayerName + ChatColor.RED + " does not believe in a God");
			return false;
		}
		
		if (!thisGodName.equals(otherGodName))
		{
			player.sendMessage(ChatColor.WHITE + otherPlayerName + ChatColor.RED + " does not believe in " + ChatColor.GOLD + thisGodName);
			return false;
		}		
		
		String partnerName = this.plugin.getMarriageManager().getPartnerName(otherPlayer.getUniqueId());
		if (partnerName != null)
		{
			player.sendMessage(ChatColor.WHITE + otherPlayerName + ChatColor.RED + " is already married to " + ChatColor.WHITE + partnerName + "!");
			return false;
		}
		partnerName = this.plugin.getMarriageManager().getPartnerName(otherPlayer.getUniqueId());
		if (partnerName != null)
		{
			player.sendMessage(ChatColor.RED + "You are already married to " + ChatColor.WHITE + partnerName + "!");
			return false;
		}
		
		this.plugin.getMarriageManager().proposeMarriage(player.getUniqueId(), otherPlayer.getUniqueId());

		this.plugin.getLanguageManager().setPlayerName(player.getName());
		this.plugin.getGodManager().GodSayWithQuestion(thisGodName, otherPlayer, LanguageManager.LANGUAGESTRING.GodToBelieverMarriageProposal, 1);

		player.sendMessage(ChatColor.AQUA + "You proposed " + ChatColor.WHITE + otherPlayerName + ChatColor.AQUA + " to marry you in the name of " + ChatColor.GOLD + thisGodName + "!");

		return true;
	}

	private boolean CommandDivorce(Player player, String[] args)
	{
		if (!this.plugin.marriageEnabled)
		{
			player.sendMessage(ChatColor.RED + "Marrige is not enabled on this server");
			return false;
		}
		
		if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.divorce")))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		String thisGodName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		
		if (thisGodName == null)
		{
			player.sendMessage(ChatColor.RED + "You do not believe in a God");
			return false;
		}
		
		String partnerName = this.plugin.getMarriageManager().getPartnerName(player.getUniqueId());
		
		if (partnerName == null)
		{
			player.sendMessage(ChatColor.RED + "You are not married, bozo!");
			return false;
		}
		
		this.plugin.getMarriageManager().divorce(player.getUniqueId());

		player.sendMessage(ChatColor.AQUA + "You divorced " + ChatColor.WHITE + partnerName + "!");

		return true;
	}

	private boolean CommandLove(Player player, String[] args)
	{
		if (!this.plugin.marriageEnabled)
		{
			player.sendMessage(ChatColor.RED + "Marriage is not enabled on this server");
			return false;
		}

		if (!player.isOp() && !this.plugin.getPermissionsManager().hasPermission(player, "gods.love"))
		{
			player.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		String thisGodName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		
		if (thisGodName == null)
		{
			player.sendMessage(ChatColor.RED + "You do not believe in a God");
			return false;
		}
		
		UUID partnerId = this.plugin.getMarriageManager().getPartnerId(player.getUniqueId());
		
		if (partnerId == null)
		{
			player.sendMessage(ChatColor.RED + "You are not married, bozo!");
			return false;
		}
		
		Player partner = this.plugin.getServer().getPlayer(partnerId);
		
		if (partner == null)
		{
			player.sendMessage(ChatColor.WHITE + plugin.getServer().getOfflinePlayer(partnerId).getName() + ChatColor.RED + " is not online!");
			return false;
		}
		
		this.plugin.getMarriageManager().love(player.getUniqueId());

		player.sendMessage(ChatColor.AQUA + "You love " + ChatColor.WHITE + plugin.getServer().getPlayer(partnerId).getDisplayName() + "!");

		return true;
	}

	private boolean CommandSetDescription(CommandSender sender, String[] args)
	{
		if ((!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.priest.description")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		Player player = (Player)sender;
		
		if (!this.plugin.getGodManager().isPriest(player.getUniqueId()))
		{
			sender.sendMessage(ChatColor.RED + "Only priests can set religion info");
			return false;
		}
		
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

		String description = "";
		for (String arg : args)
		{
			if (!arg.equals(args[0]))
			{
				description = description + " " + arg;
			}
		}
		this.plugin.getGodManager().setGodDescription(godName, description);

		sender.sendMessage(ChatColor.AQUA + "You set your religion description to " + ChatColor.YELLOW + this.plugin.getGodManager().getGodDescription(godName));

		return true;
	}

	private boolean CommandStartAttackHolyLand(CommandSender sender, String[] args)
	{
		if ((!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.priest.startattack")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		Player player = (Player)sender;

		if (!this.plugin.getGodManager().isPriest(player.getUniqueId()))
		{
			sender.sendMessage(ChatColor.RED + "Only priests can start attacks on Holy Lands!");
			return false;
		}
		
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		
		if (this.plugin.getGodManager().getContestedHolyLandForGod(godName) != null)
		{
			sender.sendMessage(ChatColor.RED + "You are already attacking a Holy Land!");
			return false;
		}
		
		String otherGodName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

		if (!this.plugin.getGodManager().hasWarRelation(godName, otherGodName))
		{
			sender.sendMessage(ChatColor.RED + "You are not in war with " + ChatColor.GOLD + otherGodName + "!");
			return false;
		}


		this.plugin.getGodManager().setContestedHolyLandForGod(godName, player.getLocation());

		sender.sendMessage(ChatColor.AQUA + "You started an attack on the Holy Land of " + ChatColor.GOLD + otherGodName);

		this.plugin.getGodManager().sendInfoToBelievers(godName, LanguageManager.LANGUAGESTRING.AttackingHolyLandsHelp, ChatColor.AQUA, otherGodName, 10, 10, 80);

		this.plugin.getLanguageManager().setPlayerName(otherGodName);
		this.plugin.getGodManager().godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversAttackStarted, 40);

		return true;
	}

	private boolean CommandAttackHolyLand(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;
		
		if ((!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.priest.attack")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

		Location attackLocation = this.plugin.getGodManager().getContestedHolyLandAttackLocationForGod(godName);

		player.teleport(attackLocation);

		String otherGodName = this.plugin.getLandManager().getGodAtHolyLandLocation(attackLocation);

		sender.sendMessage(ChatColor.AQUA + "You joined the attack on the Holy Land of " + ChatColor.GOLD + otherGodName);

		return true;
	}

	private boolean CommandDefendHolyLand(CommandSender sender, String[] args)
	{
		return false;
	}

	private boolean CommandSetDivineForce(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.priest.setforce")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		if ((!this.plugin.getGodManager().isPriest(player.getUniqueId())) && (!player.isOp()))
		{
			sender.sendMessage(ChatColor.RED + "Only priests can set the divine force");
			return false;
		}
		
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

		String divineForce = args[1].toUpperCase();

		this.plugin.getGodManager().setDivineForceForGod(godName, GodManager.GodType.valueOf(divineForce));

		sender.sendMessage(ChatColor.AQUA + "You set your religion's divine force to be " + ChatColor.YELLOW + this.plugin.getGodManager().getDivineForceForGod(godName).name());

		return true;
	}

	private boolean CommandKick(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if (!player.isOp() && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.priest.kick")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		if (!this.plugin.getGodManager().isPriest(player.getUniqueId()))
		{
			sender.sendMessage(ChatColor.RED + "Only priests can kick believers from a religion");
			return false;
		}
		
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

		String believerName = args[1];
		OfflinePlayer offlineBeliever = this.plugin.getServer().getOfflinePlayer(believerName);

		String believerGodName = this.plugin.getBelieverManager().getGodForBeliever(offlineBeliever.getUniqueId());
		
		if ((believerGodName == null) || (!believerGodName.equals(godName)))
		{
			sender.sendMessage(ChatColor.RED + "There is no such believer called '" + believerName + "' in your religion");
			return false;
		}
		if (believerGodName.equalsIgnoreCase(sender.getName()))
		{
			sender.sendMessage(ChatColor.RED + "You cannot kick yourself from your own religion, Bozo!");
			return false;
		}
		
		this.plugin.getBelieverManager().removeBeliever(godName, offlineBeliever.getUniqueId());

		sender.sendMessage(ChatColor.AQUA + "You kicked " + ChatColor.YELLOW + believerName + ChatColor.AQUA + " from your religion!");

		Player believer = this.plugin.getServer().getPlayer(believerName);
		if (believer != null)
		{
			believer.sendMessage(ChatColor.RED + "You were kicked from the religion of " + ChatColor.YELLOW + godName + ChatColor.AQUA + "!");
		}
		this.plugin.log(sender.getName() + " /gods kick " + believerName);

		return true;
	}

	private boolean CommandTogglePvP(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.priest.pvp")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		if (!this.plugin.getGodManager().isPriest(player.getUniqueId()))
		{
			sender.sendMessage(ChatColor.RED + "Only priests can toggle pvp for a religion");
			return false;
		}

		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

		boolean pvp = this.plugin.getGodManager().getGodPvP(godName);
		if (pvp)
		{
			sender.sendMessage(ChatColor.AQUA + "You set PvP for your religion to " + ChatColor.YELLOW + " disabled");
			this.plugin.getGodManager().setGodPvP(godName, false);
		}
		else
		{
			sender.sendMessage(ChatColor.AQUA + "You set PvP for your religion to " + ChatColor.YELLOW + " enabled");
			this.plugin.getGodManager().setGodPvP(godName, true);
		}
		return true;
	}

	private boolean CommandSetHome(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.sethome")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		
		if (godName == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not believe in a God");
			return false;
		}
				
		if ((this.plugin.onlyPriestCanSetHome) && (!this.plugin.getGodManager().isPriest(player.getUniqueId())))
		{
			sender.sendMessage(ChatColor.RED + "Only your priest can set the home for your religion");
			return false;
		}
		
		if (this.plugin.holyLandEnabled)
		{
			if (this.plugin.getLandManager().isNeutralLandLocation(player.getLocation()))
			{
				sender.sendMessage(ChatColor.RED + "You can only set religion home within your Holy Land");
				return false;
			}
			String locationGod = this.plugin.getLandManager().getGodAtHolyLandLocation(player.getLocation());
			if ((locationGod == null) || (!locationGod.equals(godName)))
			{
				sender.sendMessage(ChatColor.RED + "You can only set religion home within your Holy Land");
				return false;
			}
		}
		this.plugin.getGodManager().setHomeForGod(godName, player.getLocation());

		this.plugin.getLanguageManager().setPlayerName(player.getName());
		this.plugin.getGodManager().godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversSetHome, 2);

		return true;
	}

	private boolean CommandHome(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if (!player.isOp() && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.home")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		
		if (godName == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not believe in a God");
			return false;
		}
		Location location = this.plugin.getGodManager().getHomeForGod(godName);
		if (location == null)
		{
			return false;
		}

		player.teleport(location);

		return true;
	}

	private boolean CommandChat(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if ((!player.isOp()) && (!this.plugin.getPermissionsManager().hasPermission(player, "gods.chat")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		
		if (godName == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not believe in a God");
			return false;
		}
		
		if (this.plugin.getBelieverManager().getReligionChat(player.getUniqueId()))
		{
			this.plugin.getBelieverManager().setReligionChat(player.getUniqueId(), false);
			sender.sendMessage(ChatColor.AQUA + "You are now chatting public");
		}
		else
		{
			this.plugin.getBelieverManager().setReligionChat(player.getUniqueId(), true);
			sender.sendMessage(ChatColor.AQUA + "You are now only chatting with the believers of " + ChatColor.YELLOW + godName);
		}
		
		return true;
	}

	private boolean CommandHunt(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if ((!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.hunt")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		if (godName == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not believe in a God");
			return false;
		}
				
		{
			Location pilgrimageLocation = plugin.getQuestManager().getQuestLocation(godName);
						
			if(player == null || player.isFlying())
			{
				sender.sendMessage(ChatColor.RED + "No flying allowed.");
				return false;
			}
			
			if (pilgrimageLocation == null)
			{
				sender.sendMessage(ChatColor.RED + "There is no quest target to hunt for, Mr. fancy pants!");
				return false;
			}
			
			if (!pilgrimageLocation.getWorld().getName().equals(player.getWorld().getName()))
			{
				this.plugin.logDebug("PilgrimageQuest for '" + player.getDisplayName() + "' is wrong world");
				return false;
			}
			
			Vector vector = pilgrimageLocation.toVector().subtract(player.getLocation().toVector());

			this.plugin.getLanguageManager().setAmount((int) vector.length());
			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.QuestTargetRange, ChatColor.AQUA, (int) vector.length(), "", 20);						
		}
		
/*		
		if (this.plugin.getBelieverManager().isHunting(player.getUniqueId()))
		{
			this.plugin.getBelieverManager().setHunting(player.getUniqueId(), false);
			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.NotHunting, ChatColor.AQUA, 0, "", 10);
		}
		else
		{
			this.plugin.getBelieverManager().setHunting(player.getUniqueId(), true);
			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.NowHunting, ChatColor.AQUA, 0, "", 10);
		}
*/		
		return true;
	}

	private boolean CommandSetNeutralLand(CommandSender sender, String[] args)
	{
		if (!this.plugin.holyLandEnabled)
		{
			sender.sendMessage(ChatColor.RED + "Holy Land is not enabled on this server");
			return false;
		}
		
		Player player = (Player) sender;

		if ((!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.setsafe")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}

		if (this.plugin.getLandManager().isNeutralLandLocation(player.getLocation()))
		{
			this.plugin.getLandManager().clearNeutralLand(player.getLocation());
			sender.sendMessage(ChatColor.AQUA + "You set cleared the neutral land in this location.");
		}
		else
		{
			this.plugin.getLandManager().setNeutralLand(player.getLocation());
			sender.sendMessage(ChatColor.AQUA + "You set neutral land in this location.");
		}
		return true;
	}

	private boolean CommandSetPriest(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if ((!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.setpriest")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		String godName = args[1];
		if (!this.plugin.getGodManager().godExist(godName))
		{
			sender.sendMessage(ChatColor.RED + "There is no god called '" + ChatColor.GOLD + godName + ChatColor.AQUA + "'");
			return false;
		}
		
		Player otherPlayer = this.plugin.getServer().getPlayer(args[2]);
		if (player == null)
		{
			sender.sendMessage(ChatColor.RED + "There is no such player online");
			return false;
		}
		
		//this.plugin.getBelieverManager().addPrayer(player.getName(), godName);
		this.plugin.getGodManager().assignPriest(godName, otherPlayer.getUniqueId());

		sender.sendMessage(ChatColor.AQUA + "You set " + ChatColor.GOLD + otherPlayer.getName() + ChatColor.AQUA + " as priest of " + ChatColor.GOLD + godName);

		return true;
	}

	private boolean CommandPray(CommandSender sender, String[] args)
	{
		if (!this.plugin.prayersEnabled)
		{
			sender.sendMessage(ChatColor.RED + "Prayers are not enabled on this server");
			return false;
		}
		
		Player player = (Player) sender;

		if ((!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.pray")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		String prayerName = args[2];

		return true;
	}

	private boolean CommandFollowers(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if ((sender != null) && (!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.followers")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		List<Believer> believers = new ArrayList();
		String playerGod = null;

		String godName = "";
		if (args.length >= 2)
		{
			godName = args[1];
			godName = this.plugin.getGodManager().formatGodName(godName);
		}
		else
		{
			godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		}
		
		if (godName == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not believe in a God");
			return false;
		}
		
		Set<UUID> list = this.plugin.getBelieverManager().getBelieversForGod(godName);
		
		for (UUID believerId : list)
		{
			int power = (int) this.plugin.getGodManager().getGodPower(godName);
			Date lastPrayer = this.plugin.getBelieverManager().getLastPrayerTime(believerId);

			believers.add(new Believer(believerId, lastPrayer));
		}
		
		if (believers.size() == 0)
		{
			if (sender != null)
			{
				sender.sendMessage(ChatColor.GOLD + godName + ChatColor.AQUA + " has no believers!");
			}
			else
			{
				this.plugin.log("There are no Gods in " + this.plugin.serverName + "!");
			}
			return true;
		}
		
		if (sender != null)
		{
			playerGod = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
			sender.sendMessage(ChatColor.YELLOW + "--------- The Followers of " + godName + " ---------");
		}
		else
		{
			this.plugin.log("--------- The Followers of " + godName + " ---------");
		}
		Collections.sort(believers, new BelieversComparator());

		int l = believers.size();

		List<Believer> believersList = believers;
		
		if (l > 15)
		{
			believersList = ((List) believersList).subList(0, 15);
		}
		
		int n = 1;
		boolean playerShown = false;

		Date thisDate = new Date();

		for (Believer believer : believersList)
		{
			long minutes = (thisDate.getTime() - believer.lastPrayer.getTime()) / 60000L;
			long hours = (thisDate.getTime() - believer.lastPrayer.getTime()) / 3600000L;
			long days = (thisDate.getTime() - believer.lastPrayer.getTime()) / 86400000L;

			String date = "";
			if (days > 0L)
			{
				date = days + " days ago";
			}
			else if (hours > 0L)
			{
				date = hours + " hours ago";
			}
			else
			{
				date = minutes + " min ago";
			}
			
			String believerName = plugin.getServer().getOfflinePlayer(believer.believerId).getName();

			if (sender != null)
			{				
				if (playerGod != null && (believer.believerId.equals(player.getUniqueId())))
				{
					playerShown = true;
					sender.sendMessage(ChatColor.GOLD + StringUtils.rightPad(believerName, 20) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Prayed ").append(ChatColor.GOLD).append(date).toString(), 18));
				}
				else
				{
					sender.sendMessage(ChatColor.YELLOW + StringUtils.rightPad(believerName, 20) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Prayed ").append(ChatColor.GOLD).append(date).toString(), 18));
				}
			}
			else
			{
				this.plugin.log(StringUtils.rightPad(believerName, 20) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Prayed ").append(ChatColor.GOLD).append(date).toString(), 18));
			}
			n++;
		}
		
		n = 1;
		
		if ((playerGod != null) && (!playerShown))
		{
			for (Believer believer : believers)
			{
				String believerName = plugin.getServer().getOfflinePlayer(believer.believerId).getName();

				if ((playerGod != null) && (believer.believerId.equals(player.getUniqueId())))
				{
					sender.sendMessage(ChatColor.GOLD + StringUtils.rightPad(believerName, 20) + StringUtils.rightPad(new StringBuilder().append(" Prayed ").append(believer.lastPrayer).toString(), 18));
				}
				n++;
			}
		}
		return true;
	}

	private boolean CommandMarriages(CommandSender sender, String[] args)
	{
		Player player = (Player) sender;

		if ((sender != null) && (!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.marriages")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		if (!this.plugin.marriageEnabled)
		{
			sender.sendMessage(ChatColor.RED + "Marriages are not enabled on this server");
			return false;
		}
		List<MarriageManager.MarriedCouple> couples = this.plugin.getMarriageManager().getMarriedCouples();
		if (couples.size() == 0)
		{
			if (sender != null)
			{
				sender.sendMessage("There are no married couples yet!");
			}
			else
			{
				this.plugin.log("There are no married couples in " + this.plugin.serverName + "!");
			}
			return true;
		}
		if (sender != null)
		{
			sender.sendMessage(ChatColor.YELLOW + "--------- The Married Couples in " + this.plugin.serverName + " ---------");
		}
		else
		{
			this.plugin.log("--------- The Married Couples in " + this.plugin.serverName + " ---------");
		}
		int l = couples.size();

		List<MarriageManager.MarriedCouple> couplesList = couples;
		if (l > 15)
		{
			couplesList = couplesList.subList(0, 15);
		}
		
		int n = 1;
		boolean playerShown = false;
		Date thisDate = new Date();
		
		for (MarriageManager.MarriedCouple couple : couplesList)
		{
			long minutes = (thisDate.getTime() - couple.lastLove.getTime()) / 60000L;
			long hours = (thisDate.getTime() - couple.lastLove.getTime()) / 3600000L;
			long days = (thisDate.getTime() - couple.lastLove.getTime()) / 86400000L;

			String date = "";
			if (days > 0L)
			{
				date = days + " days ago";
			}
			else if (hours > 0L)
			{
				date = hours + " hours ago";
			}
			else
			{
				date = minutes + " min ago";
			}

			String player1Name = plugin.getServer().getOfflinePlayer(couple.player1Id).getName();
			String player2Name = plugin.getServer().getOfflinePlayer(couple.player2Id).getName();

			if (sender != null)
			{
				
				if ((couple.player1Id.equals(player.getUniqueId())) || (couple.player2Id.equals(player.getUniqueId())))
				{
					playerShown = true;
					sender.sendMessage("" + ChatColor.GOLD + n + " - " + StringUtils.rightPad(new StringBuilder(player1Name).append(" & ").append(player2Name).append(" (").append(couple.godName).append(")").toString(), 30) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Loved ").append(ChatColor.GOLD).append(date).toString(), 18));
				}
				else
				{
					sender.sendMessage("" + ChatColor.WHITE + n + " - " + StringUtils.rightPad(new StringBuilder(player1Name).append(" & ").append(player2Name).append(" (").append(couple.godName).append(")").toString(), 30) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Loved ").append(ChatColor.GOLD).append(date).toString(), 18));
				}
			}
			else
			{
				this.plugin.log(StringUtils.rightPad(new StringBuilder(player1Name).append(" & ").append(player2Name).append(" (").append(couple.godName).append(")").toString(), 30) + ChatColor.AQUA + StringUtils.rightPad(new StringBuilder().append(" Loved ").append(ChatColor.GOLD).append(date).toString(), 18));
			}
			n++;
		}
		
		n = 1;
		
		if (!playerShown)
		{
			for (MarriageManager.MarriedCouple couple : couples)
			{				
				String player1Name = plugin.getServer().getOfflinePlayer(couple.player1Id).getName();
				String player2Name = plugin.getServer().getOfflinePlayer(couple.player2Id).getName();

				if ((couple.player1Id.equals(player.getUniqueId())) || (couple.player2Id.equals(player.getUniqueId())))
				{
					sender.sendMessage("" + ChatColor.GOLD + n + " - " + StringUtils.rightPad(new StringBuilder(player1Name).append(" & ").append(player2Name).append(" (").append(couple.godName).append(")").toString(), 40) + StringUtils.rightPad(new StringBuilder().append(" Loved ").append(couple.lastLove).toString(), 18));
				}
				n++;
			}
		}
		return true;
	}

	private boolean CommandBible(CommandSender sender, String[] args)
	{
		if (!this.plugin.biblesEnabled)
		{
			sender.sendMessage(ChatColor.RED + "Bibles are not enabled on this server");
			return false;
		}

		Player player = (Player) sender;
		
		if (!sender.isOp() && !this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.priest.bible"))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		if (godName == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not believe in a God");
			return false;
		}

		if (!this.plugin.getGodManager().isPriest(player.getUniqueId()))
		{
			sender.sendMessage(ChatColor.RED + "Only your priest can produce the Holy Book");
			return false;
		}
		if (!this.plugin.getBibleManager().giveBible(godName, player.getName()))
		{
			sender.sendMessage(ChatColor.RED + "Could not produce a Holy Book for " + godName);
			return false;
		}
		sender.sendMessage(ChatColor.AQUA + "You produced a copy of " + ChatColor.GOLD + this.plugin.getBibleManager().getBibleTitle(godName) + ChatColor.AQUA + "!");

		return true;
	}

	private boolean CommandEditBible(CommandSender sender, String[] args)
	{
		if (!this.plugin.biblesEnabled)
		{
			sender.sendMessage(ChatColor.RED + "Bibles are not enabled on this server");
			return false;
		}

		Player player = (Player) sender;
		
		if (!sender.isOp() && !this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.priest.editbible"))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		
		if (godName == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not believe in a God");
			return false;
		}

		if (!this.plugin.getGodManager().isPriest(player.getUniqueId()))
		{
			sender.sendMessage(ChatColor.RED + "Only your priest can edit your Holy Book");
			return false;
		}
		if (!this.plugin.getBibleManager().giveEditBible(godName, player.getName()))
		{
			sender.sendMessage(ChatColor.RED + "Could not produce a editable bible for " + godName);
			return false;
		}
		sender.sendMessage(ChatColor.AQUA + "You produced a copy of " + ChatColor.GOLD + this.plugin.getBibleManager().getBibleTitle(godName) + ChatColor.AQUA + "!");

		sender.sendMessage(ChatColor.AQUA + "After you have edited it, set this as your bible with " + ChatColor.WHITE + "/g setbible" + ChatColor.AQUA + "!");

		return true;
	}

	private boolean CommandSetBible(CommandSender sender, String[] args)
	{
		if (!this.plugin.biblesEnabled)
		{
			sender.sendMessage(ChatColor.RED + "Bibles are not enabled on this server");
			return false;
		}
		
		Player player = (Player) sender;

		if ((!sender.isOp()) && (!this.plugin.getPermissionsManager().hasPermission((Player) sender, "gods.priest.setbible")))
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}
		
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		
		if (godName == null)
		{
			sender.sendMessage(ChatColor.RED + "You do not believe in a God");
			return false;
		}

		if (!this.plugin.getGodManager().isPriestForGod(player.getUniqueId(), godName))
		{
			sender.sendMessage(ChatColor.RED + "Only your priest can set the Bible");
			return false;
		}
		
		if (!this.plugin.getBibleManager().setBible(godName, player.getName()))
		{
			sender.sendMessage(ChatColor.RED + "You cannot use that as the Bible for " + ChatColor.GOLD + godName);
			return false;
		}
		
		sender.sendMessage(ChatColor.AQUA + "You set " + ChatColor.GOLD + this.plugin.getBibleManager().getBibleTitle(godName) + ChatColor.AQUA + " as your holy scripture!");

		return true;
	}

	private boolean CommandGiveHolyArtifact(CommandSender sender, String[] args)
	{
		if (!this.plugin.holyArtifactsEnabled)
		{
			sender.sendMessage(ChatColor.RED + "Holy Artifacts are not enabled on this server");
			return false;
		}

		Player player = (Player) sender;

		if (!sender.isOp())
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}

		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

		this.plugin.getGodManager().blessPlayerWithHolyArtifact(godName, player);

		sender.sendMessage(ChatColor.GOLD + godName + ChatColor.AQUA + " gave " + player.getName() + ChatColor.AQUA + " a Holy artifact!");

		return true;
	}

	private boolean CommandPrayForArtifact(CommandSender sender, String[] args)
	{
		if (!this.plugin.holyArtifactsEnabled)
		{
			sender.sendMessage(ChatColor.RED + "Holy Artifacts are not enabled on this server");
			return false;
		}
	
		if (!sender.isOp())
		{
			sender.sendMessage(ChatColor.RED + "You do not have permission for that");
			return false;
		}

		Player player = (Player) sender;
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

		int currentPrayerPower = plugin.getBelieverManager().getPrayerPower(player.getUniqueId());
		
		if(currentPrayerPower < this.plugin.prayerPowerForHolyArtifact)
		{
			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.NotEnoughPrayerPower, ChatColor.AQUA, this.plugin.prayerPowerForHolyArtifact - currentPrayerPower, godName, 1);
			return false;						
		}

		this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.PrayedForHolyArtifact, ChatColor.AQUA, this.plugin.prayerPowerForHolyArtifact - currentPrayerPower, godName, 1);

		this.plugin.getGodManager().blessPlayerWithHolyArtifact(godName, player);

		sender.sendMessage(ChatColor.GOLD + godName + ChatColor.AQUA + " gave " + player.getName() + ChatColor.AQUA + " a Holy artifact!");

		this.plugin.getBelieverManager().increasePrayerPower(player.getUniqueId(), -plugin.prayerPowerForHolyArtifact);

		return true;
	}

	private boolean CommandPrayForItem(CommandSender sender)
	{
		if (!this.plugin.itemBlessingEnabled)
		{
			sender.sendMessage(ChatColor.RED + "Item blessings are not enabled on this server");
			return false;
		}
		
		Player player = (Player) sender;
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

		int currentPrayerPower = plugin.getBelieverManager().getPrayerPower(player.getUniqueId());
		
		if(currentPrayerPower < this.plugin.prayerPowerForItem)
		{
			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.NotEnoughPrayerPower, ChatColor.AQUA, this.plugin.prayerPowerForItem - currentPrayerPower, godName, 1);
			return false;						
		}
		
		if (this.plugin.getBelieverManager().hasRecentItemBlessing(player.getUniqueId()))
		{
			//this.plugin.getGodManager().addMoodForGod(godName, this.plugin.getGodManager().getAngryModifierForGod(godName));
			this.plugin.getGodManager().godSayToBeliever(godName, player.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverPrayerRecentItemBlessing);
			return true;
		}
		
		if (this.plugin.getGodManager().blessPlayerWithItem(godName, player) == null)
		{
			this.plugin.getGodManager().godSayToBeliever(godName, player.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverPrayerWhenNoItemNeed);
		}
		else
		{
			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.PrayedForItem, ChatColor.AQUA, this.plugin.prayerPowerForItem - currentPrayerPower, godName, 1);
			this.plugin.getBelieverManager().increasePrayerPower(player.getUniqueId(), -plugin.prayerPowerForItem);			
		}

		return true;
	}

	private boolean CommandPrayForHealth(CommandSender sender)
	{
		if (!this.plugin.blessingEnabled)
		{
			sender.sendMessage(ChatColor.RED + "Blessings are not enabled on this server");
			return false;
		}
		
		Player player = (Player) sender;
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		
		int currentPrayerPower = plugin.getBelieverManager().getPrayerPower(player.getUniqueId());
		
		if(currentPrayerPower < this.plugin.prayerPowerForHealth)
		{
			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.NotEnoughPrayerPower, ChatColor.AQUA, this.plugin.prayerPowerForHealth - currentPrayerPower, godName, 1);
			return false;						
		}
				
		if (this.plugin.getBelieverManager().hasRecentItemBlessing(player.getUniqueId()))
		{
			this.plugin.getGodManager().godSayToBeliever(godName, player.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverPrayerTooSoon);
			return false;
		}
		
		this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.PrayedForHealth, ChatColor.AQUA, this.plugin.prayerPowerForBlessing - currentPrayerPower, godName, 1);

		double healing = this.plugin.getGodManager().getHealthNeed(godName, player);
		
		if (healing > 1.0D)
		{
			this.plugin.getGodManager().healPlayer(godName, player, this.plugin.getGodManager().getHealthBlessing(godName));

			this.plugin.getBelieverManager().increasePrayerPower(player.getUniqueId(), -plugin.prayerPowerForHealth);
			this.plugin.getBelieverManager().setItemBlessingTime(player.getUniqueId());

			return true;
		}
		
		return true;
	}

	private boolean CommandPrayForBlessing(CommandSender sender)
	{
		if (!this.plugin.blessingEnabled)
		{
			sender.sendMessage(ChatColor.RED + "Blessings are not enabled on this server");
			return false;
		}
		
		Player player = (Player) sender;		
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

		int currentPrayerPower = plugin.getBelieverManager().getPrayerPower(player.getUniqueId());
		
		if(currentPrayerPower < this.plugin.prayerPowerForBlessing)
		{
			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.NotEnoughPrayerPower, ChatColor.AQUA, this.plugin.prayerPowerForBlessing - currentPrayerPower, godName, 1);
			return false;						
		}
		
		this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.PrayedForBlessing, ChatColor.AQUA, this.plugin.prayerPowerForBlessing - currentPrayerPower, godName, 1);

		if (!this.plugin.getGodManager().blessPlayer(godName, player.getUniqueId(), this.plugin.getGodManager().getGodPower(godName)))
		{
			this.plugin.getGodManager().godSayToBeliever(godName, player.getUniqueId(), LanguageManager.LANGUAGESTRING.GodToBelieverPrayerTooSoon);
		}		
		else
		{		
			this.plugin.getBelieverManager().increasePrayerPower(player.getUniqueId(), -plugin.prayerPowerForBlessing);
		}
		
		return true;
	}

	private boolean CommandPrayForQuest(CommandSender sender)
	{
		if (!this.plugin.questsEnabled)
		{
			sender.sendMessage(ChatColor.RED + "Quests are not enabled on this server");
			return false;
		}
		
		Player player = (Player) sender;
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
		int currentPrayerPower = plugin.getBelieverManager().getPrayerPower(player.getUniqueId());

		if(currentPrayerPower < this.plugin.prayerPowerForQuest)
		{
			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.NotEnoughPrayerPower, ChatColor.AQUA, this.plugin.prayerPowerForQuest - currentPrayerPower, godName, 1);
			return false;						
		}
		
		if (this.plugin.getQuestManager().hasQuest(godName))
		{
			sender.sendMessage(ChatColor.GOLD + godName + ChatColor.AQUA + " already has given a quest!");
			return false;
		}
		
		this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.PrayedForQuest, ChatColor.AQUA, this.plugin.prayerPowerForQuest - currentPrayerPower, godName, 1);

		if(this.plugin.getQuestManager().generateQuest(godName))
		{		
			this.plugin.getBelieverManager().increasePrayerPower(player.getUniqueId(), -plugin.prayerPowerForQuest);
		}
		
		return true;
	}

	public class Believer
	{
		public UUID believerId;
		public Date lastPrayer;

		Believer(UUID believerId, Date lastPrayer)
		{
			this.believerId = believerId;
			this.lastPrayer = lastPrayer;
		}
	}

	public class BelieversComparator implements Comparator
	{
		public BelieversComparator()
		{
		}

		public int compare(Object object1, Object object2)
		{
			Commands.Believer b1 = (Commands.Believer) object1;
			Commands.Believer b2 = (Commands.Believer) object2;

			return (int) (b2.lastPrayer.getTime() - b1.lastPrayer.getTime());
		}
	}

	public class God
	{
		public int power;
		public String name;
		public int believers;

		God(String godName, int godPower, int godbelievers)
		{
			this.power = godPower;
			this.name = new String(godName);
			this.believers = godbelievers;
		}
	}

	public class TopGodsComparator implements Comparator
	{
		public TopGodsComparator()
		{
		}

		public int compare(Object object1, Object object2)
		{
			Commands.God g1 = (Commands.God) object1;
			Commands.God g2 = (Commands.God) object2;

			return g2.power - g1.power;
		}
	}
}