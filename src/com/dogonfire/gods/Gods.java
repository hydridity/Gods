package com.dogonfire.gods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.dogonfire.gods.GodManager.GodType;
import com.dogonfire.gods.tasks.InfoTask;

public class Gods extends JavaPlugin {
	private PrayerManager prayerManager = null;
	private MarriageManager marriageManager = null;
	private HolyPowerManager holyPowerManager = null;
	// private ProphecyManager prophecyManager = null;
	// private BossManager bossManager = null;
	private HolyArtifactManager holyArtifactManager = null;
	private ChatManager chatManager = null;
	private PermissionsManager permissionsManager = null;
	private HolyLandManager landManager = null;
	private HolyBookManager bibleManager = null;
	private WhitelistManager whitelistManager = null;
	private GodManager godManager = null;
	private QuestManager questManager = null;
	private BelieverManager believerManager = null;
	private AltarManager churchManager = null;
	private LanguageManager languageManager = null;
	private FileConfiguration config = null;
	private Commands commands = null;
	public boolean debug = false;
	public boolean downloadLanguageFile = true;
	public String languageIdentifier = "english";
	public boolean useWhitelist = false;
	public boolean useBlacklist = false;
	public boolean questsEnabled = false;
	public boolean itemBlessingEnabled = true;
	public boolean blessingEnabled = true;
	public boolean enableDetoration = false;
	public boolean commandmentsEnabled = true;
	public boolean sacrificesEnabled = true;
	public boolean holyLandEnabled = false;
	public boolean biblesEnabled = false;
	public boolean holyArtifactsEnabled = false;
	public boolean propheciesEnabled = false;
	public boolean chatFormattingEnabled = false;
	public boolean useGodTitles = true;
	public boolean marriageEnabled = false;
	public boolean marriageFireworksEnabled = false;
	public boolean cursingEnabled = true;
	public boolean lightningCurseEnabled = false;
	public boolean mobCurseEnabled = true;
	public int minCursingTime = 10;
	public int maxCursingTime = 10;
	public int minBlessingTime = 900;
	public int maxBlessingTime = 180;
	public double moodFalloff = 0.03D;
	public boolean fastDiggingBlessingEnabled = false;
	public boolean healBlessingEnabled = false;
	public boolean regenerationBlessingEnabled = false;
	public boolean speedBlessingEnabled = false;
	public boolean increaseDamageBlessingEnabled = false;
	public int minMinutesBetweenQuests = 180;
	public boolean globalQuestsEnabled = false;
	public int globalQuestsPercentChance = 10;
	public boolean slayQuestsEnabled = true;
	public boolean sacrificeQuestsEnabled = true;
	public boolean pilgrimageQuestsEnabled = false;
	public boolean holyFeastQuestsEnabled = false;
	public boolean giveItemsQuestsEnabled = false;
	public boolean burnBiblesQuestsEnabled = false;
	public boolean crusadeQuestsEnabled = false;
	public boolean convertQuestsEnabled = false;
	public boolean buildTowerQuestsEnabled = false;
	public boolean holywarQuestsEnabled = false;
	public boolean slayDragonQuestsEnabled = false;
	public String serverName = "Your Server";
	private List<String> worlds = new ArrayList<String>();
	public boolean broadcastNewGods = true;
	public boolean broadcastProphecyFullfillment = true;
	public int maxPriestsPrGod = 3;
	public int numberOfBelieversPrPriest = 3;
	public int maxInvitationTimeSeconds = 60;
	public int minItemBlessingTime = 10;
	public int minHolyArtifactBlessingTime = 1440;
	public int maxPriestPrayerTime = 8;
	public int maxBelieverPrayerTime = 154;
	public int minBelieverPrayerTime = 30;
	public int minGodPowerForItemBlessings = 3;
	public int godPowerForLevel3Items = 100;
	public int godPowerForLevel2Items = 50;
	public int godPowerForLevel1Items = 10;
	public int minBelieversForPriest = 3;
	public int minSecondsBetweenChangingGod = 5 * 60;
	public int requiredBelieversForQuests = 1;
	public boolean defaultPrivateReligions = false;
	public double godVerbosity = 1.0D;
	public boolean leaveReligionOnDeath = false;
	public boolean onlyPriestCanSetHome = false;
	public String priestAssignCommand = "";
	public String priestRemoveCommand = "";
	public boolean commandmentsBroadcastFoodEaten = true;
	public boolean commandmentsBroadcastMobSlain = true;
	public Set<Material> holylandBreakableBlockTypes = new HashSet<Material>();
	public double holyLandRadiusPrPower = 1.25D;
	public boolean holyLandDefaultPvP = false;
	public boolean holyLandDefaultMobDamage = true;
	public boolean holyLandLightning = true;
	public boolean powerLossOnDeath = false;
	public int numberOfDaysForAbandonedHolyLands = 14;
	public boolean allowMultipleGodsPrDivinePower = false;
	public boolean prayersEnabled = true;
	public boolean allowInteractionInNeutralLands = true;
	public int minHolyLandRadius = 10;
	public int maxHolyLandRadius = 1000;
	public int maxHolyArtifacts = 50;
	public boolean werewolfEnabled = false;

	public int prayerPowerForItem = 20;
	public int prayerPowerForQuest = 50;
	public int prayerPowerForBlessing = 10;
	public int prayerPowerForHolyArtifact = 100;
	public int prayerPowerForHealth = 10;

	public MarriageManager getMarriageManager() {
		return this.marriageManager;
	}

	public HolyPowerManager getHolyPowerManager() {
		return this.holyPowerManager;
	}

	// public BossManager getBossManager()
	// {
	// return this.bossManager;
	// }

	// public ProphecyManager getProphecyManager()
	// {
	// return this.prophecyManager;
	// }

	public HolyArtifactManager getHolyArtifactManager() {
		return this.holyArtifactManager;
	}

	public HolyBookManager getBibleManager() {
		return this.bibleManager;
	}

	public ChatManager getChatManager() {
		return this.chatManager;
	}

	public PermissionsManager getPermissionsManager() {
		return this.permissionsManager;
	}

	public HolyLandManager getLandManager() {
		return this.landManager;
	}

	public AltarManager getAltarManager() {
		return this.churchManager;
	}

	public QuestManager getQuestManager() {
		return this.questManager;
	}

	public GodManager getGodManager() {
		return this.godManager;
	}

	public BelieverManager getBelieverManager() {
		return this.believerManager;
	}

	public LanguageManager getLanguageManager() {
		return this.languageManager;
	}

	public WhitelistManager getWhitelistManager() {
		return this.whitelistManager;
	}

	public PrayerManager getPrayerManager() {
		return this.prayerManager;
	}

	public boolean isWhitelistedGod(String godName) {
		if (this.useWhitelist) {
			return this.whitelistManager.isWhitelistedGod(godName);
		}
		return true;
	}

	public boolean isBlacklistedGod(String godName) {
		if (this.useBlacklist) {
			return this.whitelistManager.isBlacklistedGod(godName);
		}
		return false;
	}

	public boolean isEnabledInWorld(World world) {
		return this.worlds.contains(world.getName());
	}

	public void log(String message) {
		Logger.getLogger("minecraft").info("[" + getDescription().getFullName() + "] " + message);
	}

	public void logDebug(String message) {
		if (this.debug) {
			Logger.getLogger("minecraft").info("[" + getDescription().getFullName() + "] " + message);
		}
	}

	public void sendInfo(UUID playerId, LanguageManager.LANGUAGESTRING message, ChatColor color, int amount, String name, int delay) {
		Player player = getServer().getPlayer(playerId);

		if (player == null) {
			logDebug("sendInfo can not find online player with id " + playerId);
			return;
		}

		getServer().getScheduler().runTaskLater(this, new InfoTask(this, color, playerId, message, amount, name), delay);
	}

	public void sendInfo(UUID playerId, LanguageManager.LANGUAGESTRING message, ChatColor color, String name1, String name2, int delay) {
		Player player = getServer().getPlayer(playerId);
		if (player == null) {
			logDebug("sendInfo can not find online player with id " + playerId);
			return;
		}

		getServer().getScheduler().runTaskLater(this, new InfoTask(this, color, playerId, message, name1, name2), delay);
	}

	public void sendInfo(UUID playerId, LanguageManager.LANGUAGESTRING message, ChatColor color, String name, int amount1, int amount2, int delay) {
		Player player = getServer().getPlayer(playerId);
		if (player == null) {
			logDebug("sendInfo can not find online player with id " + playerId);
			return;
		}
		getServer().getScheduler().runTaskLater(this, new InfoTask(this, color, playerId, message, name, amount1, amount2), delay);
	}

	public void reloadSettings() {
		reloadConfig();

		loadSettings();

		this.whitelistManager.load();
	}

	public void loadSettings() {
		// if (getBossManager() != null)
		// {
		// getBossManager().removeDragons();
		// }

		this.config = getConfig();

		this.debug = this.config.getBoolean("Settings.Debug", false);
		this.downloadLanguageFile = this.config.getBoolean("Settings.DownloadLanguageFile", true);
		this.languageIdentifier = this.config.getString("Settings.Language", "english");

		List<String> worldNames = this.config.getStringList("Settings.Worlds");
		if ((worldNames == null) || (worldNames.size() == 0)) {
			log("No worlds found in config file.");
			for (World world : getServer().getWorlds()) {
				this.worlds.add(world.getName());
				log("Enabed in world '" + world.getName() + "'");
			}
			this.config.set("Settings.Worlds", this.worlds);
			saveConfig();
		} else {
			for (String worldName : worldNames) {
				this.worlds.add(worldName);
				log("Enabled in '" + worldName + "'");
			}
			if (worldNames.size() == 0) {
				log("WARNING: No worlds are set in config file. Gods are disabled on this server!");
			}
		}
		this.biblesEnabled = this.config.getBoolean("Bibles.Enabled", true);
		if (this.biblesEnabled) {
			this.bibleManager = new HolyBookManager(this);
			this.bibleManager.load();
		}
		this.marriageEnabled = this.config.getBoolean("Marriage.Enabled", true);
		if (this.marriageEnabled) {
			this.marriageFireworksEnabled = this.config.getBoolean("Marriage.WeddingFireworks", true);

			this.marriageManager = new MarriageManager(this);
			this.marriageManager.load();
		}
		this.holyArtifactsEnabled = this.config.getBoolean("HolyArtifacts.Enabled", true);
		if (this.holyArtifactsEnabled) {
			this.holyPowerManager = new HolyPowerManager(this);
			this.holyArtifactManager = new HolyArtifactManager(this);
			this.holyArtifactManager.load();
		}

		this.prayersEnabled = this.config.getBoolean("Prayers.Enabled", true);

		// if (this.propheciesEnabled)
		// {
		// this.prophecyManager = new ProphecyManager(this);
		// this.prophecyManager.load();
		//
		// //this.bossManager = new BossManager(this);
		// }

		this.chatFormattingEnabled = this.config.getBoolean("ChatFormatting.Enabled", false);

		if (this.chatFormattingEnabled) {
			this.chatManager = new ChatManager(this);
			this.chatManager.load();
		}

		this.holyLandEnabled = this.config.getBoolean("HolyLand.Enabled", false);

		if (this.holyLandEnabled) {
			this.landManager = new HolyLandManager(this);
			this.landManager.load();
		}

		this.minHolyLandRadius = this.config.getInt("HolyLand.MinRadius", 10);
		this.maxHolyLandRadius = this.config.getInt("HolyLand.MaxRadius", 1000);
		this.holyLandRadiusPrPower = this.config.getDouble("HolyLand.RadiusPrPower", 1.25D);
		this.holyLandDefaultPvP = this.config.getBoolean("HolyLand.DefaultPvP", false);
		this.holyLandDefaultMobDamage = this.config.getBoolean("HolyLand.DefaultMobDamage", true);
		this.holyLandLightning = this.config.getBoolean("HolyLand.Lightning", false);
		this.allowInteractionInNeutralLands = this.config.getBoolean("HolyLand.AllowInteractionInNeutralLands", true);
		this.numberOfDaysForAbandonedHolyLands = this.config.getInt("HolyLand.DeleteAbandonedHolyLandsAfterDays", 14);

		List<String> blockList = this.config.getStringList("HolyLand.BreakableBlockTypes");
		if ((blockList != null) && (blockList.size() > 0)) {
			for (String blockType : blockList) {
				try {
					logDebug("adding breakable block type " + blockType);
					this.holylandBreakableBlockTypes.add(Material.getMaterial(blockType));
				} catch (Exception ex) {
					log("ERROR parsing HolyLand.BreakableBlockTypes blocktype '" + blockType + "' in config");
				}
			}
		} else {
			log("No HolyLand.BreakableBlockTypes section found in config.");
			log("Adding '" + Material.SMOOTH_BRICK.name() + "' to BreakableBlockTypes");
			this.holylandBreakableBlockTypes.add(Material.SMOOTH_BRICK);
		}

		this.sacrificesEnabled = this.config.getBoolean("Sacrifices.Enabled", true);

		this.commandmentsEnabled = this.config.getBoolean("Commandments.Enabled", true);
		this.commandmentsBroadcastFoodEaten = this.config.getBoolean("Commandments.BroadcastFoodEaten", true);
		this.commandmentsBroadcastMobSlain = this.config.getBoolean("Commandments.BroadcastMobSlain", true);

		this.questsEnabled = this.config.getBoolean("Quests.Enabled", false);
		this.minMinutesBetweenQuests = this.config.getInt("Quests.MinMinutesBetweenQuests", 180);

		this.globalQuestsPercentChance = this.config.getInt("Quests.GlobalQuestsPercentChance", 1);

		this.slayQuestsEnabled = this.config.getBoolean("Quests.SlayQuests", true);
		this.sacrificeQuestsEnabled = this.config.getBoolean("Quests.SacrificeQuests", true);
		this.pilgrimageQuestsEnabled = this.config.getBoolean("Quests.PilgrimageQuests", true);

		ConfigurationSection configSection = this.config.getConfigurationSection("Quests.RewardValues");
		if (configSection != null) {
			for (String rewardItem : configSection.getKeys(false)) {
				try {
					logDebug("Setting value for reward item " + rewardItem + " to " + this.config.getInt(new StringBuilder().append("Quests.RewardValues.").append(rewardItem).toString()));

					getQuestManager().setItemRewardValue(Material.getMaterial(rewardItem), this.config.getInt("Quests.RewardValues." + rewardItem));
				} catch (Exception ex) {
					log("ERROR parsing Quests.RewardValues value '" + rewardItem + "' in config");
				}
			}
		} else {
			getQuestManager().resetItemRewardValues();
		}

		this.blessingEnabled = this.config.getBoolean("Blessing.Enabled", true);
		this.speedBlessingEnabled = this.config.getBoolean("Blessing.Speed", true);
		this.regenerationBlessingEnabled = this.config.getBoolean("Blessing.Regeneration", true);
		this.healBlessingEnabled = this.config.getBoolean("Blessing.Heal", true);
		this.fastDiggingBlessingEnabled = this.config.getBoolean("Blessing.FastDigging", true);
		this.increaseDamageBlessingEnabled = this.config.getBoolean("Blessing.IncreaseDamage", true);
		this.minBlessingTime = this.config.getInt("Blessing.MinBlessingTime", 600);
		this.maxBlessingTime = this.config.getInt("Blessing.MaxBlessingTime", 180);

		this.cursingEnabled = this.config.getBoolean("Cursing.Enabled", true);
		this.lightningCurseEnabled = this.config.getBoolean("Cursing.LightningCurse", true);
		this.mobCurseEnabled = this.config.getBoolean("Cursing.MobCurse", true);
		this.maxCursingTime = this.config.getInt("Cursing.MaxCursingTime", 10);
		this.minCursingTime = this.config.getInt("Cursing.MinCursingTime", 5);

		this.itemBlessingEnabled = this.config.getBoolean("ItemBlessing.Enabled", true);
		this.minItemBlessingTime = this.config.getInt("ItemBlessing.MinItemBlessingTime", 10);
		this.minGodPowerForItemBlessings = this.config.getInt("ItemBlessing.MinGodPowerForItemBlessings", 3);
		this.godPowerForLevel1Items = this.config.getInt("ItemBlessing.GodPowerForLevel1Items", 10);
		this.godPowerForLevel2Items = this.config.getInt("ItemBlessing.GodPowerForLevel2Items", 50);
		this.godPowerForLevel3Items = this.config.getInt("ItemBlessing.GodPowerForLevel3Items", 100);
		this.onlyPriestCanSetHome = this.config.getBoolean("Settings.OnlyPriestCanSetHome", false);
		this.leaveReligionOnDeath = this.config.getBoolean("Settings.LeaveReligionOnDeath", false);
		this.maxPriestPrayerTime = this.config.getInt("Settings.MaxPriestPrayerTime", 72);
		this.maxBelieverPrayerTime = this.config.getInt("Settings.MaxBelieverPrayerTime", 154);
		this.minBelieverPrayerTime = this.config.getInt("Settings.MinBelieverPrayerTime", 30);
		this.minBelieversForPriest = this.config.getInt("Settings.MinBelieversForPriest", 3);
		this.minSecondsBetweenChangingGod = this.config.getInt("Settings.MinSecondsBetweenChangingGod", 5 * 60);
		this.maxPriestsPrGod = this.config.getInt("Settings.MaxPriestsPrGod", 1);
		this.defaultPrivateReligions = this.config.getBoolean("Settings.DefaultPrivateReligions", false);

		this.broadcastNewGods = this.config.getBoolean("Settings.BroadcastNewGods", true);
		this.useWhitelist = this.config.getBoolean("Settings.UseWhitelist", false);
		this.useBlacklist = this.config.getBoolean("Settings.UseBlacklist", false);
		this.godVerbosity = this.config.getDouble("Settings.GodVerbosity", 1.0D);
		this.serverName = this.config.getString("Settings.ServerName", "Your Server");

		this.priestAssignCommand = this.config.getString("Settings.PriestAssignCommand", "");
		this.priestRemoveCommand = this.config.getString("Settings.PriestRemoveCommand", "");
		this.allowMultipleGodsPrDivinePower = this.config.getBoolean("Settings.AllowMultipleGodsPrDivinePower", false);
		this.moodFalloff = this.config.getDouble("Settings.MoodFalloff", 0.03D);

		configSection = this.config.getConfigurationSection("Altars.BlockTypes");

		Object localObject;

		if (configSection != null) {
			for (String godType : configSection.getKeys(false)) {
				try {
					for (localObject = this.config.getStringList("Altars.BlockTypes." + godType).iterator(); ((Iterator<?>) localObject).hasNext();) {
						String blockMaterial = (String) ((Iterator<?>) localObject).next();

						log("Setting block type " + blockMaterial + " for God type " + godType);
						getAltarManager().setAltarBlockTypeForGodType(GodManager.GodType.valueOf(godType), Material.getMaterial(blockMaterial));
					}
				} catch (Exception ex) {
					log("ERROR parsing Altars.BlockType value '" + godType + "' in config");
				}
			}
		} else {
			log("No altar blocktypes found in config. Setting defaults.");
			getAltarManager().resetAltarBlockTypes();

			for (GodType godType : GodManager.GodType.values()) {
				this.config.set("Altars.BlockTypes." + godType.name(), getAltarManager().getAltarBlockTypesFromGodType(godType));
			}
			saveSettings();
		}
	}

	public void saveSettings() {
		this.config.set("Settings.Debug", Boolean.valueOf(this.debug));
		this.config.set("Settings.DownloadLanguageFile", Boolean.valueOf(this.downloadLanguageFile));
		this.config.set("Settings.Language", this.languageIdentifier);
		this.config.set("Settings.Worlds", this.worlds);
		this.config.set("Settings.UseWhitelist", Boolean.valueOf(this.useWhitelist));
		this.config.set("Settings.UseBlacklist", Boolean.valueOf(this.useBlacklist));
		this.config.set("Settings.BroadcastNewGods", Boolean.valueOf(this.broadcastNewGods));
		this.config.set("Settings.MaxPriestPrayerTime", Integer.valueOf(this.maxPriestPrayerTime));
		this.config.set("Settings.MaxBelieverPrayerTime", Integer.valueOf(this.maxBelieverPrayerTime));
		this.config.set("Settings.MinBelieverPrayerTime", Integer.valueOf(this.minBelieverPrayerTime));
		this.config.set("Settings.MinBelieversForPriest", Integer.valueOf(this.minBelieversForPriest));
		this.config.set("Settings.MaxPriestsPrGod", Integer.valueOf(this.maxPriestsPrGod));
		this.config.set("Settings.NumberOfBelieversPrPriest", Integer.valueOf(this.numberOfBelieversPrPriest));
		this.config.set("Settings.GodVerbosity", Double.valueOf(this.godVerbosity));
		this.config.set("Settings.ServerName", this.serverName);
		this.config.set("Settings.PriestAssignCommand", this.priestAssignCommand);
		this.config.set("Settings.PriestRemoveCommand", this.priestRemoveCommand);
		this.config.set("Settings.OnlyPriestCanSetHome", Boolean.valueOf(this.onlyPriestCanSetHome));
		this.config.set("Settings.LeaveReligionOnDeath", Boolean.valueOf(this.leaveReligionOnDeath));
		this.config.set("Settings.AllowMultipleGodsPrDivinePower", Boolean.valueOf(this.allowMultipleGodsPrDivinePower));
		this.config.set("Settings.MoodFalloff", Double.valueOf(this.moodFalloff));
		this.config.set("Settings.DefaultPrivateReligions", Boolean.valueOf(this.defaultPrivateReligions));
		for (GodManager.GodType godType : GodManager.GodType.values()) {
			this.config.set("Altars.BlockTypes." + godType.name(), getAltarManager().getAltarBlockTypesFromGodType(godType));
		}
		this.config.set("ItemBlessing.Enabled", Boolean.valueOf(this.itemBlessingEnabled));
		this.config.set("ItemBlessing.MinGodPowerItemBlessings", Integer.valueOf(this.minGodPowerForItemBlessings));
		this.config.set("ItemBlessing.GodPowerForLevel1Items", Integer.valueOf(this.godPowerForLevel1Items));
		this.config.set("ItemBlessing.MinItemBlessingTime", Integer.valueOf(this.minItemBlessingTime));
		this.config.set("ItemBlessing.GodPowerForLevel2Items", Integer.valueOf(this.godPowerForLevel2Items));
		this.config.set("ItemBlessing.GodPowerForLevel3Items", Integer.valueOf(this.godPowerForLevel3Items));

		this.config.set("Blessing.Enabled", Boolean.valueOf(this.blessingEnabled));
		this.config.set("Blessing.Speed", Boolean.valueOf(this.speedBlessingEnabled));
		this.config.set("Blessing.Heal", Boolean.valueOf(this.healBlessingEnabled));
		this.config.set("Blessing.Regeneration", Boolean.valueOf(this.regenerationBlessingEnabled));
		this.config.set("Blessing.IncreaseDamage", Boolean.valueOf(this.increaseDamageBlessingEnabled));
		this.config.set("Blessing.FastDigging", Boolean.valueOf(this.fastDiggingBlessingEnabled));
		this.config.set("Blessing.MaxBlessingTime", Integer.valueOf(this.maxBlessingTime));
		this.config.set("Blessing.MinBlessingTime", Integer.valueOf(this.minBlessingTime));

		this.config.set("Cursing.Enabled", Boolean.valueOf(this.cursingEnabled));
		this.config.set("Cursing.LightningCurse", Boolean.valueOf(this.lightningCurseEnabled));
		this.config.set("Cursing.MobCurse", Boolean.valueOf(this.mobCurseEnabled));
		this.config.set("Cursing.MaxCursingTime", Integer.valueOf(this.maxCursingTime));
		this.config.set("Cursing.MinCursingTime", Integer.valueOf(this.minCursingTime));

		this.config.set("Quests.Enabled", Boolean.valueOf(this.questsEnabled));
		this.config.set("Quests.MinMinutesBetweenQuests", Integer.valueOf(this.minMinutesBetweenQuests));
		this.config.set("Quests.GlobalQuestsPercentChance", Integer.valueOf(this.globalQuestsPercentChance));
		this.config.set("Quests.SlayQuests", Boolean.valueOf(this.slayQuestsEnabled));
		this.config.set("Quests.SacrificeQuests", Boolean.valueOf(this.sacrificeQuestsEnabled));
		this.config.set("Quests.ConvertQuests", Boolean.valueOf(this.convertQuestsEnabled));
		this.config.set("Quests.GiveItemsQuests", Boolean.valueOf(this.giveItemsQuestsEnabled));
		this.config.set("Quests.PilgrimageQuests", Boolean.valueOf(this.pilgrimageQuestsEnabled));
		this.config.set("Quests.HolywarQuests", Boolean.valueOf(this.holywarQuestsEnabled));

		this.config.set("Sacrifices.Enabled", Boolean.valueOf(this.sacrificesEnabled));

		this.config.set("Commandments.Enabled", Boolean.valueOf(this.commandmentsEnabled));
		this.config.set("Commandments.BroadcastFoodEaten", Boolean.valueOf(this.commandmentsBroadcastFoodEaten));
		this.config.set("Commandments.BroadcastMobSlain", Boolean.valueOf(this.commandmentsBroadcastMobSlain));

		this.config.set("HolyLand.Enabled", Boolean.valueOf(this.holyLandEnabled));
		this.config.set("HolyLand.MinRadius", Integer.valueOf(this.minHolyLandRadius));
		this.config.set("HolyLand.MaxRadius", Integer.valueOf(this.maxHolyLandRadius));
		this.config.set("HolyLand.RadiusPrPower", Double.valueOf(this.holyLandRadiusPrPower));
		this.config.set("HolyLand.DefaultPvP", Boolean.valueOf(this.holyLandDefaultPvP));
		this.config.set("HolyLand.DefaultMobDamage", Boolean.valueOf(this.holyLandDefaultMobDamage));
		this.config.set("HolyLand.Lightning", Boolean.valueOf(this.holyLandLightning));
		this.config.set("HolyLand.AllowInteractionInNeutralLands", Boolean.valueOf(this.allowInteractionInNeutralLands));
		this.config.set("HolyLand.DeleteAbandonedHolyLandsAfterDays", Integer.valueOf(this.numberOfDaysForAbandonedHolyLands));

		List<String> blockTypes = new ArrayList<String>();
		for (Material blockType : this.holylandBreakableBlockTypes) {
			blockTypes.add(blockType.name());
		}
		this.config.set("HolyLand.BreakableBlockTypes", blockTypes);

		this.config.set("ChatFormatting.Enabled", Boolean.valueOf(this.chatFormattingEnabled));

		this.config.set("Bibles.Enabled", Boolean.valueOf(this.biblesEnabled));

		this.config.set("Prophecies.Enabled", Boolean.valueOf(this.propheciesEnabled));

		this.config.set("HolyArtifacts.Enabled", Boolean.valueOf(this.holyArtifactsEnabled));

		this.config.set("Marriage.Enabled", Boolean.valueOf(this.marriageEnabled));
		this.config.set("Marriage.WeddingFireworks", Boolean.valueOf(this.marriageFireworksEnabled));

		this.config.set("Prayers.Enabled", Boolean.valueOf(this.prayersEnabled));

		saveConfig();
	}

	public void onEnable() {
		this.permissionsManager = new PermissionsManager(this);
		this.questManager = new QuestManager(this);
		this.godManager = new GodManager(this);
		this.believerManager = new BelieverManager(this);
		this.languageManager = new LanguageManager(this);
		this.churchManager = new AltarManager(this);
		this.whitelistManager = new WhitelistManager(this);
		// this.bossManager = new BossManager(this);
		this.commands = new Commands(this);

		loadSettings();
		saveSettings();

		this.permissionsManager.load();
		this.languageManager.load();
		this.godManager.load();
		this.questManager.load();
		this.believerManager.load();
		this.whitelistManager.load();
		if (this.landManager != null) {
			getServer().getPluginManager().registerEvents(this.landManager, this);
		}
		getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		getServer().getPluginManager().registerEvents(new ChatListener(this), this);

		Runnable updateTask = new Runnable() {
			public void run() {
				Gods.this.godManager.update();
			}
		};

		// Async WONT work with some calls
		// getServer().getScheduler().runTaskTimerAsynchronously(this, updateTask, 20L,
		// 200L);
		getServer().getScheduler().runTaskTimer(this, updateTask, 20L, 200L);

	}

	public void onDisable() {
		reloadSettings();

		this.godManager.save();
		this.questManager.save();
		this.believerManager.save();
		if ((this.useBlacklist) || (this.useWhitelist)) {
			this.whitelistManager.save();
		}
		if (this.holyLandEnabled) {
			this.landManager.save();
		}
		if (this.biblesEnabled) {
			this.bibleManager.save();
		}
		// this.bossManager.disable();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return this.commands.onCommand(sender, cmd, label, args);
	}
}