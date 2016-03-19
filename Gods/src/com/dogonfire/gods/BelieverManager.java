package com.dogonfire.gods;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.dogonfire.gods.LanguageManager.LANGUAGESTRING;

public class BelieverManager
{
	private Gods plugin = null;
	private Random random = new Random();
	private FileConfiguration believersConfig = null;
	private File believersConfigFile = null;
	private long lastSaveTime;

	BelieverManager(Gods p)
	{
		this.plugin = p;
	}

	public void load()
	{
		if (this.believersConfigFile == null)
		{
			this.believersConfigFile = new File(this.plugin.getDataFolder(), "believers.yml");
		}
		this.believersConfig = YamlConfiguration.loadConfiguration(this.believersConfigFile);

		this.plugin.log("Loaded " + this.believersConfig.getKeys(false).size() + " believers.");
	}

	public void save()
	{
		this.lastSaveTime = System.currentTimeMillis();
		if ((this.believersConfig == null) || (this.believersConfigFile == null))
		{
			return;
		}
		try
		{
			this.believersConfig.save(this.believersConfigFile);
		}
		catch (Exception ex)
		{
			this.plugin.log("Could not save config to " + this.believersConfigFile.getName() + ": " + ex.getMessage());
		}
	}

	public void saveTimed()
	{
		if (System.currentTimeMillis() - this.lastSaveTime < 180000L)
		{
			return;
		}
		save();
	}

	public void setReligionChat(UUID believerId, boolean enabled)
	{
		if (enabled)
		{
			this.believersConfig.set(believerId + ".ReligionChat", Boolean.valueOf(true));
		}
		else
		{
			this.believersConfig.set(believerId + ".ReligionChat", null);
		}
		saveTimed();
	}

	public boolean getReligionChat(UUID believerId)
	{
		return this.believersConfig.getBoolean(believerId + ".ReligionChat");
	}

	public String getGodForBeliever(UUID believerId)
	{
		return this.believersConfig.getString(believerId + ".God");
	}

	public Set<String> getBelievers()
	{
		Set<String> allBelievers = this.believersConfig.getKeys(false);

		return allBelievers;
	}

	public String getNearestBeliever(Location location)
	{
		Set<String> allBelievers = this.believersConfig.getKeys(false);

		double minLength = 5.0D;
		Player minPlayer = null;
		for (String believerName : allBelievers)
		{
			Player player = this.plugin.getServer().getPlayer(believerName);
			if ((player != null) && (player.getWorld() == location.getWorld()))
			{
				double length = player.getLocation().subtract(location).length();
				if (length < minLength)
				{
					minLength = length;
					minPlayer = player;
				}
			}
		}
		if (minPlayer == null)
		{
			return null;
		}
		return minPlayer.getName();
	}

	public Set<UUID> getBelieversForGod(String godName)
	{
		Set<String> allBelievers = this.believersConfig.getKeys(false);
		Set<UUID> believers = new HashSet<UUID>();
		
		for (String believer : allBelievers)
		{
			UUID believerId = UUID.fromString(believer);
			
			String believerGod = getGodForBeliever(believerId);
			
			if ((believerGod != null) && (believerGod.equals(godName)))
			{
				believers.add(believerId);
			}
		}
		
		return believers;
	}

	public Set<UUID> getOnlineBelieversForGod(String godName)
	{
		Set<String> allBelievers = this.believersConfig.getKeys(false);
		Set<UUID> believers = new HashSet();
		
		for (String believer : allBelievers)
		{
			UUID believerId = UUID.fromString(believer);
			
			if (this.plugin.getServer().getPlayer(believerId) != null)
			{
				String believerGod = getGodForBeliever(believerId);
				if ((believerGod != null) && (believerGod.equals(godName)))
				{
					believers.add(believerId);
				}
			}
		}
		
		return believers;
	}

	public boolean hasRecentPriestOffer(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();
		Date offerDate = null;

		String offerDateString = this.believersConfig.getString(believerId + ".LastPriestOffer");
		try
		{
			offerDate = formatter.parse(offerDateString);
		}
		catch (Exception ex)
		{
			offerDate = new Date();
			offerDate.setTime(0L);
		}
		long diff = thisDate.getTime() - offerDate.getTime();
		long diffMinutes = diff / 60000L;

		return diffMinutes <= 60L;
	}

	public void clearPendingPriest(UUID believerId)
	{
		this.believersConfig.set(believerId + ".LastPriestOffer", null);
		saveTimed();
	}

	public void setPendingPriest(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.believersConfig.set(believerId + ".LastPriestOffer", formatter.format(thisDate));
		saveTimed();
	}

	boolean getChangingGod(UUID believerId)
	{
		String changingGodString = this.believersConfig.getString(believerId + ".ChangingGod");

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date changingGodDate = null;
		boolean changing = false;
		Date thisDate = new Date();
		try
		{
			changingGodDate = formatter.parse(changingGodString);

			long diff = thisDate.getTime() - changingGodDate.getTime();
			long diffSeconds = diff / 1000L;

			changing = diffSeconds <= 10L;
		}
		catch (Exception ex)
		{
			changing = false;
		}
		return changing;
	}

	void clearChangingGod(UUID believerId)
	{
		this.believersConfig.set(believerId + ".ChangingGod", null);

		saveTimed();
	}

	void setChangingGod(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.believersConfig.set(believerId + ".ChangingGod", formatter.format(thisDate));
		saveTimed();
	}

	public boolean isHunting(UUID believerId)
	{
		boolean hunting = false;
		try
		{
			hunting = this.believersConfig.getBoolean(believerId + ".Hunting");
		}
		catch (Exception ex)
		{
			setHunting(believerId, false);
		}
		return hunting;
	}

	public void setHunting(UUID believerId, boolean hunting)
	{
		this.plugin.logDebug("Setting hunting string to '" + hunting + "' for " + believerId);

		this.believersConfig.set(believerId + ".Hunting", Boolean.valueOf(hunting));

		saveTimed();
	}

	public void setInvitationTime(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.believersConfig.set(believerId + ".LastInvitationTime", formatter.format(thisDate));

		saveTimed();
	}

	public int getTimeUntilCanPray(UUID believerId)
	{
		String lastPrayerString = this.believersConfig.getString(believerId + ".LastPrayer");

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date lastPrayerDate = null;
		Date thisDate = new Date();
		try
		{
			lastPrayerDate = formatter.parse(lastPrayerString);
		}
		catch (Exception ex)
		{
			lastPrayerDate = new Date();
			lastPrayerDate.setTime(0L);
		}
		long diff = this.plugin.minBelieverPrayerTime * 60 * 1000 + lastPrayerDate.getTime() - thisDate.getTime();
		long diffSeconds = diff / 1000L;
		int diffMinutes = (int) (diffSeconds / 60L);

		return diffMinutes;
	}

	public Date getLastPrayerTime(UUID believerId)
	{
		String lastPrayerString = this.believersConfig.getString(believerId + ".LastPrayer");

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date lastPrayerDate = null;
		try
		{
			lastPrayerDate = formatter.parse(lastPrayerString);
		}
		catch (Exception ex)
		{
			lastPrayerDate = new Date();
			lastPrayerDate.setTime(0L);
		}
		return lastPrayerDate;
	}

	public boolean hasRecentPrayer(UUID believerId)
	{
		String lastPrayerString = this.believersConfig.getString(believerId + ".LastPrayer");

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date lastPrayerDate = null;
		Date thisDate = new Date();
		try
		{
			lastPrayerDate = formatter.parse(lastPrayerString);
		}
		catch (Exception ex)
		{
			lastPrayerDate = new Date();
			lastPrayerDate.setTime(0L);
		}
		long diff = thisDate.getTime() - lastPrayerDate.getTime();
		long diffMinutes = diff / 60000L;

		return diffMinutes <= this.plugin.minBelieverPrayerTime;
	}

	public void setItemBlessingTime(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.believersConfig.set(believerId + ".LastItemBlessingTime", formatter.format(thisDate));

		saveTimed();
	}

	public void setHolyArtifactBlessingTime(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.believersConfig.set(believerId + ".LastHolyArtifactBlessingTime", formatter.format(thisDate));

		saveTimed();
	}

	public void setBlessingTime(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.believersConfig.set(believerId + ".LastBlessingTime", formatter.format(thisDate));

		saveTimed();
	}

	public void setCursingTime(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.believersConfig.set(believerId + ".LastCursingTime", formatter.format(thisDate));

		saveTimed();
	}

	public boolean hasRecentItemBlessing(UUID believerId)
	{
		String lastItemBlessingString = this.believersConfig.getString(believerId + ".LastItemBlessingTime");

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date lastItemBlessingDate = null;
		Date thisDate = new Date();
		try
		{
			lastItemBlessingDate = formatter.parse(lastItemBlessingString);
		}
		catch (Exception ex)
		{
			lastItemBlessingDate = new Date();
			lastItemBlessingDate.setTime(0L);
		}
		long diff = thisDate.getTime() - lastItemBlessingDate.getTime();
		long diffMinutes = diff / 60000L;

		return diffMinutes < this.plugin.minItemBlessingTime;
	}

	public boolean hasRecentHolyArtifactBlessing(UUID believerId)
	{
		String lastItemBlessingString = this.believersConfig.getString(believerId + ".LastHolyArtifactBlessingTime");

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date lastItemBlessingDate = null;
		Date thisDate = new Date();
		try
		{
			lastItemBlessingDate = formatter.parse(lastItemBlessingString);
		}
		catch (Exception ex)
		{
			lastItemBlessingDate = new Date();
			lastItemBlessingDate.setTime(0L);
		}
		long diff = thisDate.getTime() - lastItemBlessingDate.getTime();
		long diffMinutes = diff / 60000L;

		return diffMinutes < this.plugin.minHolyArtifactBlessingTime;
	}

	public boolean hasRecentBlessing(UUID believerId)
	{
		String lastItemBlessingString = this.believersConfig.getString(believerId + ".LastBlessingTime");

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date lastItemBlessingDate = null;
		Date thisDate = new Date();
		try
		{
			lastItemBlessingDate = formatter.parse(lastItemBlessingString);
		}
		catch (Exception ex)
		{
			lastItemBlessingDate = new Date();
			lastItemBlessingDate.setTime(0L);
		}
		long diff = thisDate.getTime() - lastItemBlessingDate.getTime();

		long diffSeconds = diff / 1000L;

		return diffSeconds < this.plugin.minBlessingTime;
	}

	public boolean hasRecentCursing(UUID believerId)
	{
		String lastItemBlessingString = this.believersConfig.getString(believerId + ".LastCursingTime");

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date lastItemBlessingDate = null;
		Date thisDate = new Date();
		try
		{
			lastItemBlessingDate = formatter.parse(lastItemBlessingString);
		}
		catch (Exception ex)
		{
			lastItemBlessingDate = new Date();
			lastItemBlessingDate.setTime(0L);
		}
		long diff = thisDate.getTime() - lastItemBlessingDate.getTime();
		long diffSeconds = diff / 1000L;

		return diffSeconds < this.plugin.minCursingTime;
	}

	public void setInvitation(UUID believerId, String godName)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.believersConfig.set(believerId.toString() + ".Invitation.Time", formatter.format(thisDate));
		this.believersConfig.set(believerId.toString() + ".Invitation.God", godName);

		saveTimed();
	}

	public String getInvitation(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();
		Date offerDate = null;

		String offerDateString = this.believersConfig.getString(believerId.toString() + ".Invitation.Time");
		try
		{
			offerDate = formatter.parse(offerDateString);
		}
		catch (Exception ex)
		{
			offerDate = new Date();
			offerDate.setTime(0L);
		}
		long diff = thisDate.getTime() - offerDate.getTime();
		long diffSeconds = diff / 1000L;
		if (diffSeconds > 30L)
		{
			this.believersConfig.set(believerId.toString() + ".Invitation", null);

			saveTimed();

			return null;
		}
		return this.believersConfig.getString(believerId.toString() + ".Invitation.God");
	}

	public void clearInvitation(UUID believerId)
	{
		this.believersConfig.set(believerId.toString() + ".Invitation", null);

		saveTimed();
	}

	public void removeInvitation(UUID believerId)
	{
		this.believersConfig.set(believerId.toString() + ".LastInvitationTime", null);

		saveTimed();
	}

	public int getPrayers(UUID believerId)
	{
		int prayers = this.believersConfig.getInt(believerId + ".Prayers");

		return prayers;
	}

	public void clearGodForBeliever(UUID believerId)
	{
		this.believersConfig.set(believerId.toString(), null);

		saveTimed();
	}

	public void removeBeliever(String godName, UUID believerId)
	{
		String believerGodName = this.believersConfig.getString(believerId + ".God");

		if (believerGodName != null && !believerGodName.equals(godName))
		{
			return;
		}

		clearPrayerPower(believerId);
		
		this.believersConfig.set(believerId.toString(), null);

		this.plugin.log(godName + " lost " + believerId + " as believer");

		saveTimed();
	}

	public float getBelieverPower(UUID believerId)
	{
		Date date = new Date();

		float time = 1.0F + 2.5E-008F * (float) (date.getTime() - this.plugin.getBelieverManager().getLastPrayerTime(believerId).getTime());

		return this.plugin.getBelieverManager().getPrayers(believerId) / time;
	}

	public int getPrayerPower(UUID believerId)
	{
		int prayerPower = this.believersConfig.getInt(believerId + ".PrayerPower");
		
		return prayerPower;
	}

	public void increasePrayerPower(UUID believerId, int powerChange)
	{
		int prayerPower = this.believersConfig.getInt(believerId + ".PrayerPower");

		prayerPower += powerChange;
		
		if(prayerPower < 0)
		{
			prayerPower = 0;
		}
		
		this.believersConfig.set(believerId + ".PrayerPower", prayerPower);

		saveTimed();
		
		plugin.sendInfo(believerId, LANGUAGESTRING.YourPrayerPower, ChatColor.AQUA, prayerPower, "", 10);
	}
	
	public void clearPrayerPower(UUID believerId)
	{
		this.believersConfig.set(believerId + ".PrayerPower", null);

		saveTimed();
		
		plugin.sendInfo(believerId, LANGUAGESTRING.YourPrayerPower, ChatColor.AQUA, 0, "", 10);
	}
	
	
	public void believerLeave(String godName, UUID believerId)
	{
		String believerGodName = this.believersConfig.getString(believerId + ".God");
		if (!believerGodName.equals(godName))
		{
			return;
		}
		this.believersConfig.set(believerId + ".God", null);

		saveTimed();
	}

	public void removePrayer(UUID believerId)
	{
		this.believersConfig.set(believerId + ".LastPrayer", null);

		save();
	}

	public boolean increasePrayer(UUID believerId, String godName, int incPrayers)
	{
		String lastPrayer = this.believersConfig.getString(believerId + ".LastPrayer");

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date lastPrayerDate = null;
		Date thisDate = new Date();
		try
		{
			lastPrayerDate = formatter.parse(lastPrayer);
		}
		catch (Exception ex)
		{
			lastPrayerDate = new Date();
			lastPrayerDate.setTime(0L);
		}
		int prayers = this.believersConfig.getInt(believerId + ".Prayers");
		String oldGod = this.believersConfig.getString(believerId + ".God");
		if ((oldGod != null) && (!oldGod.equals(godName)))
		{
			prayers = 0;
			lastPrayerDate.setTime(0L);
		}
		long diff = thisDate.getTime() - lastPrayerDate.getTime();

		long diffMinutes = diff / 60000L;
		if (diffMinutes < this.plugin.minBelieverPrayerTime)
		{
			return false;
		}
		prayers += incPrayers;

		this.believersConfig.set(believerId + ".LastPrayer", formatter.format(thisDate));
		this.believersConfig.set(believerId + ".God", godName);
		this.believersConfig.set(believerId + ".Prayers", Integer.valueOf(prayers));

		saveTimed();

		return true;
	}

	public boolean addPrayer(UUID believerId, String godName)
	{
		String lastPrayer = this.believersConfig.getString(believerId + ".LastPrayer");

		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date lastPrayerDate = null;
		Date thisDate = new Date();
		try
		{
			lastPrayerDate = formatter.parse(lastPrayer);
		}
		catch (Exception ex)
		{
			lastPrayerDate = new Date();
			lastPrayerDate.setTime(0L);
		}
		int prayers = this.believersConfig.getInt(believerId + ".Prayers");
		String oldGod = this.believersConfig.getString(believerId + ".God");
		if ((oldGod != null) && (!oldGod.equals(godName)))
		{
			prayers = 0;
			lastPrayerDate.setTime(0L);
		}
		long diff = thisDate.getTime() - lastPrayerDate.getTime();

		long diffMinutes = diff / 60000L;
		if (diffMinutes < this.plugin.minBelieverPrayerTime)
		{
			return false;
		}
		prayers++;

		this.believersConfig.set(believerId + ".LastPrayer", formatter.format(thisDate));
		this.believersConfig.set(believerId + ".God", godName);
		this.believersConfig.set(believerId + ".Prayers", Integer.valueOf(prayers));

		saveTimed();

		return true;
	}

	public void setLastPrayerDate(UUID believerId)
	{
		String pattern = "HH:mm:ss dd-MM-yyyy";
		DateFormat formatter = new SimpleDateFormat(pattern);
		Date thisDate = new Date();

		this.believersConfig.set(believerId + ".LastPrayer", formatter.format(thisDate));
	}

	public boolean reducePrayer(UUID believerId, int n)
	{
		int prayers = this.believersConfig.getInt(believerId + ".Prayers");

		prayers -= n;
		if (prayers < 0)
		{
			prayers = 0;
		}
		this.believersConfig.set(believerId + ".Prayers", Integer.valueOf(prayers));

		saveTimed();

		return true;
	}

	public boolean incPrayer(UUID believerId, String godName)
	{
		int prayers = this.believersConfig.getInt(believerId + ".Prayers");

		prayers++;

		this.believersConfig.set(believerId + ".God", godName);
		this.believersConfig.set(believerId + ".Prayers", Integer.valueOf(prayers));

		saveTimed();

		return true;
	}
}