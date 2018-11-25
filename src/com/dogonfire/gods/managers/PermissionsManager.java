package com.dogonfire.gods.managers;


import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.dogonfire.gods.Gods;

import net.milkbowl.vault.permission.Permission;

public class PermissionsManager
{
	private static PermissionsManager instance;

	public static PermissionsManager get()
	{
		if (instance == null)
			instance = new PermissionsManager();
		return instance;
	}

	private String				pluginName			= "null";
	private Gods				plugin;
	private Permission 			vaultPermission;
	
	private PermissionsManager()
	{
	}

	public PermissionsManager(Gods g)
	{
		this.plugin = g;
			
		RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		vaultPermission = permissionProvider.getProvider();
	}

	public void load()
	{
		// Nothing to see here
	}

	public Plugin getPlugin()
	{
		return plugin;
	}

	public String getPermissionPluginName()
	{
		return pluginName;
	}

	public boolean hasPermission(Player player, String node)
	{
		return vaultPermission.has(player, node);
	}

	public String getGroup(String playerName)
	{
		return vaultPermission.getPrimaryGroup(plugin.getServer().getPlayer(playerName));
	}

	public void setGroup(String playerName, String groupName)
	{
		Player player = plugin.getServer().getPlayer(playerName);
		vaultPermission.playerAddGroup(player, groupName);
	}
}