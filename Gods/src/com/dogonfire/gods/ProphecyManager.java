//package com.dogonfire.gods;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.Server;
//import org.bukkit.World;
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.configuration.file.YamlConfiguration;
//import org.bukkit.entity.EntityType;
//
//public class ProphecyManager
//{
//	private Gods plugin = null;
//	private FileConfiguration prophecyConfig = null;
//	private File prophecyConfigFile = null;
//
//	static enum Prophecy
//	{
//		None, Header, BelieverWillLeaveReligion, ReligionKills100Mobs, NewPriestWillBeSelected, HolyArtifactWillBeFound, RandomBelieverWillBeKilledByUnholyMob, RandomBelieverWillBeKilledByHeathen, UnholyMobWillBeSlainByBeliever, HeathenWillBeSlain, ReligionHarvested, DragonBossWillBeSlain;
//	}
//
//	static enum ProphecyEffect
//	{
//		None, Rain, Storm, SkyWillDarken, HolyFoodRain, LongRain, LongNight, RainItems, SilverfishSwarm, DragonBoss, TitanBoss;
//	}
//
//	ProphecyManager(Gods gods)
//	{
//		this.plugin = gods;
//	}
//
//	public void load()
//	{
//		if (this.prophecyConfigFile == null)
//		{
//			this.prophecyConfigFile = new File(this.plugin.getDataFolder(), "prophecies.yml");
//		}
//		this.prophecyConfig = YamlConfiguration.loadConfiguration(this.prophecyConfigFile);
//
//		this.plugin.log("Loaded " + this.prophecyConfig.getKeys(false).size() + " prophecies.");
//	}
//
//	public void save()
//	{
//		if ((this.prophecyConfig == null) || (this.prophecyConfigFile == null))
//		{
//			return;
//		}
//		try
//		{
//			this.prophecyConfig.save(this.prophecyConfigFile);
//		}
//		catch (Exception ex)
//		{
//			this.plugin.log("Could not save config to " + this.prophecyConfigFile + ": " + ex.getMessage());
//		}
//	}
//
//	public boolean hasProphecies(String godName)
//	{
//		return (getPropheciesForGod(godName) != null) && (getPropheciesForGod(godName).size() > 0);
//	}
//
//	public List<String> getProphecyPlayersForGod(String godName)
//	{
//		return this.prophecyConfig.getStringList(godName + ".Players");
//	}
//
//	public List<String> getPropheciesForGod(String godName)
//	{
//		return this.prophecyConfig.getStringList(godName + ".Prophecies");
//	}
//
//	public List<String> getProphecyEffectsForGod(String godName)
//	{
//		return this.prophecyConfig.getStringList(godName + ".ProphecyEffects");
//	}
//
//	public void clearPropheciesForGod(String godName)
//	{
//		this.prophecyConfig.set(godName + ".Prophecies", null);
//		this.prophecyConfig.set(godName + ".ProphecyEffects", null);
//		this.prophecyConfig.set(godName + ".CurrentProphecyIndex", null);
//
//		save();
//	}
//
//	public Prophecy getCurrentProphecyForGod(String godName)
//	{
//		int currentProphecyIndex = this.prophecyConfig.getInt(godName + ".CurrentProphecyIndex");
//
//		List<String> prophecies = getPropheciesForGod(godName);
//
//		String prophecyString = (String) prophecies.get(currentProphecyIndex);
//
//		return Prophecy.valueOf(prophecyString);
//	}
//
//	public ProphecyEffect getCurrentProphecyEffectForGod(String godName)
//	{
//		int currentProphecyIndex = this.prophecyConfig.getInt(godName + ".CurrentProphecyIndex");
//
//		List<String> prophecyEffects = getProphecyEffectsForGod(godName);
//
//		String prophecyEffectString = (String) prophecyEffects.get(currentProphecyIndex);
//
//		return ProphecyEffect.valueOf(prophecyEffectString);
//	}
//
//	public void handleMobKill(String playerName, String godName, String mobType)
//	{
//		if (!hasProphecies(godName))
//		{
//			this.plugin.logDebug("Generating prophecies for " + godName);
//			generateProphecies(godName);
//		}
//		Prophecy currentProphecy = getCurrentProphecyForGod(godName);
//		if (currentProphecy == Prophecy.UnholyMobWillBeSlainByBeliever)
//		{
//			if (mobType.equals(this.plugin.getGodManager().getHolyMobTypeForGod(godName).name()))
//			{
//				fullfillCurrentProphecy(godName, playerName);
//			}
//		}
//	}
//
//	public void handleBelieverLeave(String playerName, String godName)
//	{
//		if (!hasProphecies(godName))
//		{
//			this.plugin.logDebug("Generating prophecies for " + godName);
//			generateProphecies(godName);
//		}
//		Prophecy currentProphecy = getCurrentProphecyForGod(godName);
//		if (currentProphecy == Prophecy.BelieverWillLeaveReligion)
//		{
//			fullfillCurrentProphecy(godName, playerName);
//		}
//	}
//
//	private String describeFulfilledProphecy(String godName, Prophecy prophecy, ProphecyEffect effect, String playerName)
//	{
//		String prophecyText = "MISSINGPROPHECY";
//		try
//		{
//			this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getMobTypeName(this.plugin.getGodManager().getUnholyMobTypeForGod(godName)));
//		}
//		catch (Exception ex)
//		{
//			this.plugin.logDebug(ex.getStackTrace().toString());
//		}
//		this.plugin.getLanguageManager().setPlayerName(playerName);
//		switch (prophecy)
//		{
//		case DragonBossWillBeSlain:
//			prophecyText = ChatColor.GOLD + "The Prophecies of " + godName + ChatColor.BLACK + "                                                      These are things I foreseen will come to happen in " + this.plugin.serverName + ".";
//			break;
//		case Header:
//			prophecyText = ChatColor.GOLD + "The Prophecies of " + godName + ChatColor.BLACK + "                                             These are things I foreseen will come to happen in " + this.plugin.serverName + ".";
//		case UnholyMobWillBeSlainByBeliever:
//			prophecyText = this.plugin.getLanguageManager().getLanguageStringForBook(godName, LanguageManager.LANGUAGESTRING.DragonBossWillBeSlainPastProphecyBibleText);
//			break;
//		case RandomBelieverWillBeKilledByUnholyMob:
//			prophecyText = this.plugin.getLanguageManager().getLanguageStringForBook(godName, LanguageManager.LANGUAGESTRING.UnholyMobWillBeSlainPastProphecyBibleText);
//			break;
//		case ReligionHarvested:
//			prophecyText = ChatColor.GOLD + "The Prophecies of " + godName + ChatColor.BLACK + "                                             These are things I foreseen will come to happen in " + this.plugin.serverName + ".";
//		case ReligionKills100Mobs:
//			prophecyText = "The evil " + this.plugin.getGodManager().getEatFoodTypeForGod(godName) + "was slain by the great " + playerName;
//			break;
//		case HolyArtifactWillBeFound:
//			prophecyText = playerName + "became our new leader of to guide our people";
//			break;
//		case BelieverWillLeaveReligion:
//			prophecyText = "And a champion will find a holy artifact";
//			break;
//		case RandomBelieverWillBeKilledByHeathen:
//			prophecyText = "And our believer  will be killed by the evil " + this.plugin.getGodManager().getEatFoodTypeForGod(godName) + "!";
//		case None:
//			prophecyText = "And a will be killed by a sneaky heathen!";
//		case HeathenWillBeSlain:
//			prophecyText = "The foolish " + playerName + "was slain";
//		}
//		switch (effect)
//		{
//		case DragonBoss:
//			prophecyText = prophecyText;
//			break;
//		case Storm:
//			prophecyText = prophecyText + this.plugin.getLanguageManager().getLanguageStringForBook(godName, LanguageManager.LANGUAGESTRING.DragonBossProphecyEffectPastBibleText);
//			break;
//		case Rain:
//			prophecyText = " and the sky will thunder!";
//			break;
//		case SilverfishSwarm:
//			prophecyText = " and the skies will weap rain with for 3 days!";
//			break;
//		case HolyFoodRain:
//			prophecyText = " and the skies will be filled with " + this.plugin.getGodManager().getEatFoodTypeForGod(godName) + "!";
//		case LongNight:
//			prophecyText = prophecyText + this.plugin.getLanguageManager().getLanguageStringForBook(godName, LanguageManager.LANGUAGESTRING.StormProphecyEffectPastBibleText);
//			break;
//		case SkyWillDarken:
//			prophecyText = " and silverfish will appear, roaming the lands!";
//			break;
//		case LongRain:
//			prophecyText = " and the skies will darken and sun dissapear!";
//		}
//		return ChatColor.GOLD + prophecyText;
//	}
//
//	private String describeUnfulfilledProphecy(String godName, Prophecy prophecy, ProphecyEffect effect)
//	{
//		String prophecyText = "MISSINGPROPHECY";
//		try
//		{
//			this.plugin.getLanguageManager().setType(this.plugin.getLanguageManager().getMobTypeName(this.plugin.getGodManager().getUnholyMobTypeForGod(godName)));
//		}
//		catch (Exception ex)
//		{
//			this.plugin.logDebug(ex.getStackTrace().toString());
//		}
//		this.plugin.getLanguageManager().setPlayerName(godName);
//		switch (prophecy)
//		{
//		case DragonBossWillBeSlain:
//			prophecyText = this.plugin.getLanguageManager().getLanguageStringForBook(godName, LanguageManager.LANGUAGESTRING.ProphecyHeaderBibleText);
//			break;
//		case Header:
//			prophecyText = this.plugin.getLanguageManager().getLanguageStringForBook(godName, LanguageManager.LANGUAGESTRING.BelieverWillLeaveReligionProphecyBibleText);
//			break;
//		case UnholyMobWillBeSlainByBeliever:
//			prophecyText = this.plugin.getLanguageManager().getLanguageStringForBook(godName, LanguageManager.LANGUAGESTRING.DragonBossWillBeSlainFutureProphecyBibleText);
//			break;
//		case RandomBelieverWillBeKilledByUnholyMob:
//			prophecyText = this.plugin.getLanguageManager().getLanguageStringForBook(godName, LanguageManager.LANGUAGESTRING.UnholyMobWillBeSlainFutureProphecyBibleText);
//			break;
//		case ReligionHarvested:
//			prophecyText = ChatColor.GOLD + "The Prophecies of " + godName + ChatColor.BLACK + "                                             These are things I foreseen will come to happen in " + this.plugin.serverName + ".";
//			break;
//		case ReligionKills100Mobs:
//			prophecyText = "A champion will slay the nasty " + this.plugin.getGodManager().getEatFoodTypeForGod(godName);
//			break;
//		case HolyArtifactWillBeFound:
//			prophecyText = "A new leader of " + godName + " will guide our people";
//			break;
//		case BelieverWillLeaveReligion:
//			prophecyText = "A champion will find a holy artifact";
//			break;
//		case RandomBelieverWillBeKilledByHeathen:
//			prophecyText = "And our believer  will be killed by the evil $Type!";
//			break;
//		case None:
//			prophecyText = "A brother will be killed by a sneaky heathen!";
//			break;
//		case HeathenWillBeSlain:
//			prophecyText = "A heathen will be slain";
//			break;
//		case NewPriestWillBeSelected:
//			prophecyText = "A champion will find a holy artifact";
//		}
//		switch (effect)
//		{
//		case Storm:
//			prophecyText = prophecyText + this.plugin.getLanguageManager().getLanguageStringForBook(godName, LanguageManager.LANGUAGESTRING.DragonBossProphecyEffectFutureBibleText);
//			break;
//		case Rain:
//			prophecyText = prophecyText + " and the sky will thunder!";
//			break;
//		case DragonBoss:
//			prophecyText = prophecyText;
//			break;
//		case SilverfishSwarm:
//			prophecyText = prophecyText + " and the skies will weap rain with for 3 days!";
//			break;
//		case HolyFoodRain:
//			prophecyText = prophecyText + " and the skies was be filled with $Type!";
//			break;
//		case LongNight:
//			prophecyText = prophecyText + this.plugin.getLanguageManager().getLanguageStringForBook(godName, LanguageManager.LANGUAGESTRING.StormProphecyEffectFutureBibleText);
//			break;
//		case None:
//			prophecyText = prophecyText + " and it rained with $Type";
//			break;
//		case RainItems:
//			prophecyText = prophecyText + " and day will turn into night";
//			break;
//		case SkyWillDarken:
//			prophecyText = prophecyText;
//			break;
//		case LongRain:
//			prophecyText = prophecyText;
//		}
//		return prophecyText;
//	}
//
//	public void generateProphecies(String godName)
//	{
//		List<String> prophecies = new ArrayList();
//		List<String> prophecyEffects = new ArrayList();
//		List<String> players = new ArrayList();
//
//		this.plugin.getBibleManager().getBible(godName);
//
//		players.add("NoOne");
//		prophecies.add(Prophecy.Header.name());
//		prophecyEffects.add(ProphecyEffect.None.name());
//
//		prophecies.add(Prophecy.UnholyMobWillBeSlainByBeliever.name());
//		prophecyEffects.add(ProphecyEffect.Storm.name());
//
//		prophecies.add(Prophecy.UnholyMobWillBeSlainByBeliever.name());
//		prophecyEffects.add(ProphecyEffect.DragonBoss.name());
//
//		this.prophecyConfig.set(godName + ".Prophecies", prophecies);
//		this.prophecyConfig.set(godName + ".ProphecyEffects", prophecyEffects);
//		this.prophecyConfig.set(godName + ".Players", players);
//		this.prophecyConfig.set(godName + ".CurrentProphecyIndex", Integer.valueOf(1));
//
//		save();
//
//		updatePropheciesInBible(godName);
//	}
//
//	public void fullfillCurrentProphecy(String godName, String playerName)
//	{
//		List<String> prophecies = getPropheciesForGod(godName);
//		if (this.plugin.broadcastProphecyFullfillment)
//		{
//			this.plugin.getLanguageManager().setPlayerName(playerName);
//			try
//			{
//				this.plugin.getLanguageManager().setType(godName);
//			}
//			catch (Exception ex)
//			{
//				this.plugin.logDebug(ex.getStackTrace().toString());
//			}
//			this.plugin.getServer().broadcastMessage(this.plugin.getLanguageManager().getLanguageString(godName, LanguageManager.LANGUAGESTRING.GodToBelieversProphecyFulfilled));
//		}
//		ProphecyEffect prophecyEffect = getCurrentProphecyEffectForGod(godName);
//		launchProphecyEffect(godName, prophecyEffect);
//
//		List<String> champions = getProphecyPlayersForGod(godName);
//		champions.add(playerName);
//		this.prophecyConfig.set(godName + ".Players", champions);
//
//		int currentProphecyIndex = this.prophecyConfig.getInt(godName + ".CurrentProphecyIndex");
//
//		currentProphecyIndex++;
//
//		this.prophecyConfig.set(godName + ".CurrentProphecyIndex", Integer.valueOf(currentProphecyIndex));
//		if (currentProphecyIndex >= prophecies.size())
//		{
//			this.plugin.getLanguageManager().setPlayerName(this.plugin.getBibleManager().getBibleTitle(godName));
//			this.plugin.getGodManager().godSayToBelievers(godName, LanguageManager.LANGUAGESTRING.GodToBelieversAllPropheciesFulfilled, 10);
//			clearPropheciesForGod(godName);
//			return;
//		}
//		save();
//
//		updatePropheciesInBible(godName);
//	}
//
//	public void updatePropheciesInBible(String godName)
//	{
//		int currentProphecyIndex = this.prophecyConfig.getInt(godName + ".CurrentProphecyIndex");
//
//		List<String> prophecies = getPropheciesForGod(godName);
//		List<String> players = getProphecyPlayersForGod(godName);
//		List<String> prophecyEffects = getProphecyEffectsForGod(godName);
//		List<String> prophecyPages = new ArrayList();
//		for (int p = 0; p < currentProphecyIndex; p++)
//		{
//			Prophecy fulfilledProphecy = Prophecy.valueOf((String) prophecies.get(p));
//			ProphecyEffect fulfilledProphecyEffect = ProphecyEffect.valueOf((String) prophecyEffects.get(p));
//			String fulfilledPlayerName = (String) players.get(p);
//
//			prophecyPages.add(describeFulfilledProphecy(godName, fulfilledProphecy, fulfilledProphecyEffect, fulfilledPlayerName));
//		}
//		if (currentProphecyIndex > -1)
//		{
//			for (int p = currentProphecyIndex; p < prophecies.size(); p++)
//			{
//				Prophecy unfulfilledProphecy = Prophecy.valueOf((String) prophecies.get(p));
//				ProphecyEffect unfulfilledProphecyEffect = ProphecyEffect.valueOf((String) prophecyEffects.get(p));
//
//				prophecyPages.add(describeUnfulfilledProphecy(godName, unfulfilledProphecy, unfulfilledProphecyEffect));
//			}
//		}
//		this.plugin.getBibleManager().setProphecyPages(godName, prophecyPages);
//	}
//
//	public void launchProphecyEffect(String godName, ProphecyEffect prophecyEffect)
//	{
//		switch (prophecyEffect)
//		{
//		case LongNight:
//			this.plugin.logDebug("Setting storm...");
//			for (World world : this.plugin.getServer().getWorlds())
//			{
//				world.setStorm(true);
//				world.setWeatherDuration(24000);
//			}
//			break;
//		case Storm:
//			this.plugin.logDebug("Spawning dragon boss...");
//			Location location = new Location((World) this.plugin.getServer().getWorlds().get(0), 0.0D, 0.0D, 0.0D);
//			this.plugin.getBossManager().createNewBoss(BossManager.BossType.DRAGON, godName, location);
//			break;
//		//case TitanBoss:
//		//	Location location = new Location((World) this.plugin.getServer().getWorlds().get(0), 0.0D, 0.0D, 0.0D);
//		//	this.plugin.getBossManager().createNewBoss(BossManager.BossType.TITAN, godName, location);
//		}
//	}
//}
//
///*
// * Location: C:\temp\Gods.jar
// * 
// * Qualified Name: com.dogonfire.gods.ProphecyManager
// * 
// * JD-Core Version: 0.7.0.1
// */