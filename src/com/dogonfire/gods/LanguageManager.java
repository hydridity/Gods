package com.dogonfire.gods;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

public class LanguageManager
{
	private Gods plugin;
	private String generalLanguageFileName = null;
	private HashMap<String, FileConfiguration> languageConfigs = new HashMap();
	private Random random = new Random();
	private int amount;
	private String playerName;
	private String type;

	private void downloadLanguageFile(String fileName) throws IOException
	{
		BufferedInputStream in = new BufferedInputStream(new URL("https://raw.githubusercontent.com/DogOnFire/Gods/master/lang/" + fileName).openStream());

		FileOutputStream fos = new FileOutputStream(this.plugin.getDataFolder() + "/lang/" + fileName);

		BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

		byte[] data = new byte[1024];

		int x = 0;
		while ((x = in.read(data, 0, 1024)) >= 0)
		{
			bout.write(data, 0, x);
		}
		bout.close();

		in.close();
	}

	private boolean loadLanguageFile(String fileName)
	{
		File languageConfigFile = new File(this.plugin.getDataFolder() + "/lang/" + fileName);
		if (!languageConfigFile.exists())
		{
			this.plugin.log("Could not load " + this.plugin.getDataFolder() + "/lang/" + fileName);
			return false;
		}
		FileConfiguration languageConfig = YamlConfiguration.loadConfiguration(languageConfigFile);

		this.languageConfigs.put(fileName, languageConfig);

		this.plugin.log("Loaded " + languageConfig.getString("Version.Name") + " by " + languageConfig.getString("Version.Author") + " version " + languageConfig.getString("Version.Version"));

		return true;
	}

	public void load()
	{
		this.generalLanguageFileName = (this.plugin.languageIdentifier + "_general.yml");

		this.plugin.logDebug("generalFileName is " + this.generalLanguageFileName);
		this.plugin.logDebug("plugin.language is " + this.plugin.languageIdentifier);

		File directory = new File(this.plugin.getDataFolder() + "/lang");
		if (!directory.exists())
		{
			System.out.println("Creating language file directory '/lang'...");

			boolean result = directory.mkdir();
			if (result)
			{
				this.plugin.logDebug("Directory created");
			}
			else
			{
				this.plugin.logDebug("Directory FAILED!");
				return;
			}
		}
		if (!loadLanguageFile(this.generalLanguageFileName))
		{
			this.plugin.log("Could not load " + this.generalLanguageFileName + " from the /lang folder!");
			if (this.plugin.downloadLanguageFile)
			{
				this.plugin.log("Downloading " + this.generalLanguageFileName + " from DogOnFire...");
				try
				{
					downloadLanguageFile(this.generalLanguageFileName);
				}
				catch (Exception ex)
				{
					this.plugin.log("Could not download " + this.generalLanguageFileName + " language file from DogOnFire: " + ex.getMessage());
					return;
				}
				if (loadLanguageFile(this.generalLanguageFileName))
				{
					this.plugin.logDebug(this.generalLanguageFileName + " loaded.");
				}
			}
			else
			{
				this.plugin.log("Will NOT download from DogOnFire. Please place a valid language file in your /lang folder!");
			}
		}
		for (GodManager.GodType godType : GodManager.GodType.values())
		{
			for (GodManager.GodGender godGender : GodManager.GodGender.values())
			{
				if (godGender != GodManager.GodGender.None)
				{
					String fileName = this.plugin.languageIdentifier + "_" + godType.name().toLowerCase() + "_" + godGender.name().toLowerCase() + ".yml";
					if (!loadLanguageFile(fileName))
					{
						this.plugin.log("Could not load language file " + fileName + " from the /lang folder!");
						this.plugin.log("Downloading english files from doggycraft.dk...");
						try
						{
							downloadLanguageFile(fileName);
						}
						catch (Exception ex)
						{
							this.plugin.log("Could not download language file " + fileName + " from bukkit: " + ex.getMessage());
							continue;
						}
						loadLanguageFile(fileName);
					}
					this.plugin.log("Loaded " + fileName + ".");
				}
			}
		}
	}

	private void save()
	{
	}

	private String getLanguageFileForGod(String godName)
	{
		return this.plugin.getGodManager().getLanguageFileForGod(godName);
	}

	public String getLanguageString(String godName, LANGUAGESTRING type)
	{		
		FileConfiguration languageConfig = (FileConfiguration) this.languageConfigs.get(getLanguageFileForGod(godName));
		if (languageConfig == null)
		{
			GodManager.GodType godType = this.plugin.getGodManager().getDivineForceForGod(godName);

			this.plugin.log("No languageConfig found for " + godName + " of type " + godType.name() + "!");
			return "MISSING LANGUAGEFILE " + type.name();
		}
		
		List<String> strings = languageConfig.getStringList(type.name());
		
		if (strings == null || strings.size() == 0)
		{
			this.plugin.log("No language strings found for " + godName + "," + type.name() + "!");
			return type.name() + " MISSING in " + getLanguageFileForGod(godName);
		}
		
		String text = (String) strings.toArray()[this.random.nextInt(strings.size())];

		return parseString(text);
	}

	public String getLanguageStringForBook(String godName, LANGUAGESTRING type)
	{
		List<String> strings = ((FileConfiguration) this.languageConfigs.get(getLanguageFileForGod(godName))).getStringList(type.name());
		if (strings.size() == 0)
		{
			this.plugin.log("No language strings found for " + type.name() + "!");
			return type.name() + " MISSING in " + getLanguageFileForGod(godName);
		}
		String text = (String) strings.toArray()[this.random.nextInt(strings.size())];

		return parseStringForBook(text);
	}

	public boolean setDefault()
	{
		return true;
	}

	LanguageManager(Gods p)
	{
		this.plugin = p;
	}

	public String getPriestAssignCommand(UUID playerName)
	{
		return "";
	}

	public String getPriestRemoveCommand(UUID playerName)
	{
		return "";
	}

	public String parseString(String id)
	{
		String string = id;
		if (string.contains("$ServerName"))
		{
			string = string.replace("$ServerName", ChatColor.GOLD + this.plugin.serverName + ChatColor.WHITE + ChatColor.BOLD);
		}
		if (string.contains("$PlayerName"))
		{
			string = string.replace("$PlayerName", ChatColor.GOLD + this.playerName + ChatColor.WHITE + ChatColor.BOLD);
		}
		if (string.contains("$Amount"))
		{
			string = string.replace("$Amount", ChatColor.GOLD + String.valueOf(this.amount) + ChatColor.WHITE + ChatColor.BOLD);
		}
		if (string.contains("$Type"))
		{
			string = string.replace("$Type", ChatColor.GOLD + this.type + ChatColor.WHITE + ChatColor.BOLD);
		}
		return string;
	}

	public String parseString(String id, ChatColor defaultColor)
	{
		String string = id;
		if (string.contains("$ServerName"))
		{
			string = string.replace("$ServerName", ChatColor.GOLD + this.plugin.serverName + defaultColor);
		}
		if (string.contains("$PlayerName"))
		{
			string = string.replace("$PlayerName", ChatColor.GOLD + this.playerName + defaultColor);
		}
		if (string.contains("$Amount"))
		{
			string = string.replace("$Amount", ChatColor.GOLD + String.valueOf(this.amount) + defaultColor);
		}
		if (string.contains("$Type"))
		{
			string = string.replace("$Type", ChatColor.GOLD + this.type + defaultColor);
		}
		return string;
	}

	public String parseStringForBook(String id)
	{
		String string = id;
		if (string.contains("$ServerName"))
		{
			string = string.replace("$ServerName", this.plugin.serverName);
		}
		if (string.contains("$PlayerName"))
		{
			string = string.replace("$PlayerName", this.playerName);
		}
		if (string.contains("$Amount"))
		{
			string = string.replace("$Amount", String.valueOf(this.amount));
		}
		if (string.contains("$Type"))
		{
			string = string.replace("$Type", this.type);
		}
		return string;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

	public void setPlayerName(String name)
	{
		if (name == null)
		{
			this.plugin.logDebug("WARNING: Setting null playername");
		}
		this.playerName = name;
	}

	public int getAmount()
	{
		return this.amount;
	}

	public void setAmount(int a)
	{
		this.amount = a;
	}

	public String getType()
	{
		return this.type;
	}

	public void setType(String t) throws Exception
	{
		if (t == null)
		{
			this.plugin.logDebug("WARNING: Setting null type");
			throw new Exception("WARNING: Setting null type");
		}
		this.type = t;
	}

	public String getItemTypeName(Material material)
	{
		FileConfiguration configuration = (FileConfiguration) this.languageConfigs.get(this.generalLanguageFileName);
		if (configuration == null)
		{
			return null;
		}
		String itemTypeName = configuration.getString("Items." + material.name());
		if (itemTypeName == null)
		{
			this.plugin.logDebug("WARNING: No language string in " + this.generalLanguageFileName + " for the item '" + material.name() + "'");
			return material.name();
		}
		return itemTypeName;
	}

	public String getMobTypeName(EntityType type)
	{
		String mobTypeName = ((FileConfiguration) this.languageConfigs.get(this.generalLanguageFileName)).getString("Mobs." + type.name());
		if (mobTypeName == null)
		{
			this.plugin.logDebug("WARNING: No language string in " + this.generalLanguageFileName + " for the mob type '" + type.name() + "'");
			return type.name();
		}
		return mobTypeName;
	}

	public String getGodTypeName(GodManager.GodType type, String gender)
	{
		String typeName = ((FileConfiguration) this.languageConfigs.get(this.generalLanguageFileName)).getString("GodTypes." + type.name());
		if (typeName == null)
		{
			typeName = "$Gender of Nothing";
		}
		return typeName.replace("$Gender", gender);
	}

	public String getGodGenderName(GodManager.GodGender gender)
	{
		return ((FileConfiguration) this.languageConfigs.get(this.generalLanguageFileName)).getString("GodGender." + gender.name());
	}

	public String getGodMoodName(GodManager.GodMood mood)
	{
		return ((FileConfiguration) this.languageConfigs.get(this.generalLanguageFileName)).getString("GodMood." + mood.name());
	}

	public String getInfoString(LANGUAGESTRING languageString, ChatColor defaultColor)
	{
		String text = ((FileConfiguration) this.languageConfigs.get(this.generalLanguageFileName)).getString("Info." + languageString.name());
		
		if (text == null)
		{
			this.plugin.logDebug("WARNING: No language string in " + this.generalLanguageFileName + " for the info type '" + languageString.name() + "'");
			return languageString.name() + " MISSING in " + this.generalLanguageFileName;
		}
		return parseString(text, defaultColor);
	}

	public static enum LANGUAGESTRING
	{
		GodToBelieverPrayerRecentItemBlessing,
		GodToBelieverPrayerWhenNoItemNeed,
		GodToBelieverPrayerTooSoon,
		EnterHolyLandInfoYourGod,
		EnterHolyLandInfoOtherGod,
		EnterWildernessInfo,
		EnterNeutralLandInfo,
		EnterContestedLandInfo,
		PrivateGodNoAccess,
		DivorcedYou,
		MarrigeLovesYou,
		MarrigeYouLove,
		YouLeftReligion,
		AttackHelp,
		GodsHelp,
		AltarHelp,
		PrayForHelp,
		DescriptionHelp,
		FollowersHelp,
		InviteHelp,
		PrayAlterHelp,
		PrayToBlacklistedGodNotAllowed,
		BlessingsNotAllowed,
		CursesNotAllowed,
		TeleportIntoHolylandNotAllowed,
		AltarPrayingNotAllowed,
		YouEarnedPowerBySlayingHeathen,
		YouEarnedPowerBySlayingEnemy,
		PvPLostPower,
		ConfirmChangeToOtherReligion,
		CannotPraySoSoon,
		CannotChangeGodSoSoon,
		CannotBuildAltarToOtherGods,
		InfoHelp,
		YourPrayerPower,
		YouHealedBeings,
		RejectedJoinOffer,
		PrayedForBlessing,
		PrayedForQuest,
		PrayedForItem,
		PrayedForHealth,
		PrayedForHolyArtifact,		
		NotEnoughPrayerPower,		
		NowHunting,
		NotHunting,
		InvalidGodName,
		InvalidAltarSign,
		BlessingsHelp,
		CursesHelp,
		BuildAltarNotAllowed,
		CreateGodNotAllowed,
		SacrificeHelp,
		QuestTargetHelp,
		QuestTargetRange,
		BelieverPower,
		AttackingHolyLandsHelp,
		DefendingHolyLandsHelp,
		ContestedLandStatus,
		GodToBelieversDefendHolyLandSuccess,
		GodToBelieversDefendHolyLandFailed,
		GodToBelieversAttackHolyLandSuccess,
		GodToBelieversAttackHolyLandFailed,
		GodToBelieversAttackStarted,
		GodToBelieversAttackProgress,
		GodToBelieversAttackStatus,
		GodToBelieverQuestionHelp,
		GodToBelieverNoQuestion,
		GodToBelieversNewPlayerAccepted,
		GodToPriestPriestAccepted,
		GodToPriestBlessedPlayerSet,
		GodToPriestBlessedPlayerUnset,
		GodToPriestCursedPlayerSet,
		GodToPriestCursedPlayerUnset,
		GodToPriestEatFoodType,
		GodToPriestNotEatFoodType,
		GodToPriestSlayMobType,
		GodToPriestNotSlayMobType,
		GodToPriestUseBible,
		GodToPriestUseProphecies,
		GodToPriestBelieverKilledDeclareWarQuestion,
		GodToBelieversBlessedPlayerSet,
		GodToBelieversBlessedPlayerUnset,
		GodToBelieversCursedPlayerSet,
		GodToBelieversCursedPlayerUnset,
		GodToBelieverOfferPriest,
		GodToBelieverPraying,
		GodToBelieverMarriageProposal,
		GodToBelieverAcceptedMarriageProposal,
		GodToBelieverAcceptedYourMarriageProposal,
		GodToBelieverMarriageTokenPickup,
		GodToBelieverMarriagePartnerTokenPickup,
		GodToBelieverMarried,
		GodToBelieverMarriedCouple,
		GodToBelieversSlayDragonQuestStarted,
		GodToBelieversSlayDragonQuestProgress,
		GodToBelieversSlayDragonQuestStatus,
		GodToBelieversSlayDragonQuestCompleted,
		GodToBelieversSlayDragonQuestFailed,
		GodToBelieversSlayQuestStarted,
		GodToBelieversSlayQuestProgress,
		GodToBelieversSlayQuestStatus,
		GodToBelieversSlayQuestCompleted,
		GodToBelieversSlayQuestFailed,
		GodToBelieversConvertQuestStarted,
		GodToBelieversConvertQuestProgress,
		GodToBelieversConvertQuestStatus,
		GodToBelieversConvertQuestCompleted,
		GodToBelieversConvertQuestFailed,
		GodToBelieversBuildAltarsQuestStarted,
		GodToBelieversBuildAltarsQuestProgress,
		GodToBelieversBuildAltarsQuestStatus,
		GodToBelieversBuildAltarsQuestCompleted,
		GodToBelieversBuildAltarsQuestFailed,
		GodToBelieversBuildTowerQuestStarted,
		GodToBelieversBuildTowerQuestProgress,
		GodToBelieversBuildTowerQuestStatus,
		GodToBelieversBuildTowerQuestCompleted,
		GodToBelieversBuildTowerQuestFailed,
		GodToBelieversSacrificeQuestStarted,
		GodToBelieversSacrificeQuestProgress,
		GodToBelieversSacrificeQuestStatus,
		GodToBelieversSacrificeQuestCompleted,
		GodToBelieversSacrificeQuestFailed,
		GodToBelieversHolyFeastQuestStarted,
		GodToBelieversHolyFeastQuestProgress,
		GodToBelieversHolyFeastQuestStatus,
		GodToBelieversHolyFeastQuestCompleted,
		GodToBelieversHolyFeastQuestFailed,
		GodToBelieversHolyBattleQuestStarted,
		GodToBelieversHolyBattleQuestProgress,
		GodToBelieversHolyBattleQuestStatus,
		GodToBelieversHolyBattleQuestCompleted,
		GodToBelieversHolyBattleQuestFailed,
		GodToBelieversGiveItemsQuestStarted,
		GodToBelieversGiveBiblesQuestProgress,
		GodToBelieversGiveItemsQuestStatus,
		GodToBelieversGiveItemsQuestCompleted,
		GodToBelieversGiveItemsQuestFailed,
		GodToBelieversBurnBiblesQuestStarted,
		GodToBelieversBurnBiblesQuestProgress,
		GodToBelieversBurnBiblesQuestStatus,
		GodToBelieversBurnBiblesQuestCompleted,
		GodToBelieversBurnBiblesQuestFailed,
		GodToBelieversCrusadeQuestStarted,
		GodToBelieversCrusadeQuestProgress,
		GodToBelieversCrusadeQuestStatus,
		GodToBelieversCrusadeQuestCompleted,
		GodToBelieversCrusadeQuestFailed,
		GodToBelieversPVPRevengeQuestStarted,
		GodToBelieversPVPRevengeQuestProgress,
		GodToBelieversPVPRevengeQuestStatus,
		GodToBelieversPVPRevengeQuestCompleted,
		GodToBelieversPVPRevengeQuestFailed,
		GodToBelieversPilgrimageQuestStarted,
		GodToBelieversPilgrimageQuestProgress,
		GodToBelieversPilgrimageQuestStatus,
		GodToBelieversPilgrimageQuestCompleted,
		GodToBelieversPilgrimageQuestFailed,
		GodToBelieversClaimHolyLandQuestStarted,
		GodToBelieversClaimHolyLandQuestProgress,
		GodToBelieversClaimHolyLandQuestStatus,
		GodToBelieversClaimHolyLandQuestCompleted,
		GodToBelieversClaimHolyLandQuestFailed,
		GodToBelieversGetHolyArtifactQuestStarted,
		GodToBelieversGetHolyArtifactQuestProgress,
		GodToBelieversGetHolyArtifactQuestHelp,
		GodToBelieversGetHolyArtifactQuestRange,
		GodToBelieversGetHolyArtifactQuestStatus,
		GodToBelieversGetHolyArtifactQuestCompleted,
		GodToBelieversGetHolyArtifactOtherQuestFailed,
		GodToBelieversGetHolyArtifactQuestFailed,
		GodToBelieversHolywarQuestStarted,
		GodToBelieversHolywarQuestProgress,
		GodToBelieversHolywarQuestHelp,
		GodToBelieversHolywarQuestRange,
		GodToBelieversHolywarQuestStatus,
		GodToBelieversHolywarQuestCompleted,
		GodToBelieversHolywarOtherQuestFailed,
		GodToBelieversHolywarQuestFailed,
		GodToBelieversRemovedPriest,
		GodToBelieverPriestRejected,
		GodToBelieverAltarBuilt,
		GodToBelieversPlayerJoinedReligion,
		GodToBelieversPlayerLeftReligion,
		GodToBelieverRandomExaltedSpeech,
		GodToBelieverRandomPleasedSpeech,
		GodToBelieverRandomNeutralSpeech,
		GodToBelieverRandomDispleasedSpeech,
		GodToBelieverRandomAngrySpeech,
		GodToBelieversLostBeliever,
		GodToPlayerBlessed,
		GodToPlayerCursed,
		GodToPlayerInvite,
		GodToPlayerAcceptedInvitation,
		GodToBelieverCursedAngry,
		GodToBelieverGoodSacrifice,
		GodToBelieverMehSacrifice,
		GodToBelieverBadSacrifice,
		GodToBelieverHolyFoodSacrifice,
		GodToBelieversPlayerCursed,
		GodToBelieversPlayerBlessed,
		GodToBelieverItemBlessing,
		GodToBelieverHolyArtifactBlessing,
		GodToBelieverHealthBlessing,
		GodToBelieverSmiteBlessing,
		GodToBelieversEatFoodBlessing,
		GodToBelieversNotEatFoodCursing,
		GodToBelieversSlayMobBlessing,
		GodToBelieversNotSlayMobCursing,
		GodToBelieversPriestAccepted,
		GodToBelieversAltarDestroyed,
		GodToBelieversAltarDestroyedByPlayer,
		GodToBelieversWar,
		GodToBelieversAlliance,
		GodToBelieversWarCancelled,
		GodToBelieversAllianceCancelled,
		GodToBelieversSetHome,
		GodToBelieversSacrificeItemType,
		GodToBelieversAllPropheciesFulfilled,
		GodToBelieversProphecyFulfilled,
		GodToBelieversJustMarried,
		ProphecyHeaderBibleText,
		UnholyMobWillBeSlainFutureProphecyBibleText,
		UnholyMobWillBeSlainPastProphecyBibleText,
		StormProphecyEffectPastBibleText,
		StormProphecyEffectFutureBibleText,
		HolyFoodRainProphecyEffectBibleText,
		DarknessProphecyEffectBibleText,
		HeathenWillBeSlainProphecyEffectBibleText,
		BelieverWillLeaveReligionProphecyBibleText,
		DragonBossWillBeSlainPastProphecyBibleText,
		DragonBossWillBeSlainFutureProphecyBibleText,
		DragonBossProphecyEffectPastBibleText,
		DragonBossProphecyEffectFutureBibleText,
		DefaultBibleText1,
		DefaultBibleText2,
		DefaultBibleText3,
		DefaultBibleText4,
		DefaultBibleText5,
		DefaultBibleText6,
		DefaultBibleText7;
	}
}
