package com.dogonfire.gods;

import java.io.File;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class WhitelistManager
{
	private Gods				plugin			= null;
	private FileConfiguration	whiteList		= null;
	private File				whiteListFile	= null;
	private FileConfiguration	blackList		= null;
	private File				blackListFile	= null;

	WhitelistManager(Gods p)
	{
		this.plugin = p;
	}

	public void load()
	{
		if (this.plugin.useWhitelist)
		{
			if (this.whiteListFile == null)
			{
				this.whiteListFile = new File(this.plugin.getDataFolder(), "whitelist.yml");
			}

			this.whiteList = YamlConfiguration.loadConfiguration(this.whiteListFile);

			this.plugin.log("Loaded " + this.whiteList.getKeys(false).size() + " whitelisted Gods.");

			if (this.whiteList.getKeys(false).size() == 0)
			{
				this.whiteList.set("TheExampleGodName.MinPower", Integer.valueOf(0));

				save();
			}

			this.plugin.useBlacklist = false;

			this.plugin.log("Using whitelist");
		}

		if (this.plugin.useBlacklist)
		{
			if (this.blackListFile == null)
			{
				this.blackListFile = new File(this.plugin.getDataFolder(), "blacklist.yml");
			}
			this.blackList = YamlConfiguration.loadConfiguration(this.blackListFile);

			this.plugin.log("Loaded " + this.blackList.getKeys(false).size() + " blacklisted Gods.");
			if (this.blackList.getKeys(false).size() == 0)
			{
				this.blackList.set("TheExampleGodName", "");

				save();
			}
			this.plugin.useWhitelist = false;

			this.plugin.log("Using blacklist");
		}
	}

	public void save()
	{
		try
		{
			this.whiteList.save(this.whiteListFile);
		}
		catch (Exception ex)
		{
			this.plugin.log("Could not save whitelist to " + this.whiteListFile + ": " + ex.getMessage());
		}
		try
		{
			this.blackList.save(this.blackListFile);
		}
		catch (Exception ex)
		{
			this.plugin.log("Could not save blacklist to " + this.blackListFile + ": " + ex.getMessage());
		}
	}

	public boolean isWhitelistedGod(String godName)
	{
		String name = this.whiteList.getString(godName);

		return name != null;
	}

	public boolean isBlacklistedGod(String godName)
	{
		String name = this.blackList.getString(godName);

		return name != null;
	}

	public float getMinGodPower(String godName)
	{
		int power = this.whiteList.getInt(godName + ".MinPower");

		return power;
	}
}