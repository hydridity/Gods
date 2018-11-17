package com.dogonfire.gods.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.dogonfire.gods.Gods;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.CalculableType;
import de.bananaco.bpermissions.api.WorldManager;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsManager {

	private static PermissionsManager instance;

	public static PermissionsManager get() {
		if (instance == null)
			instance = new PermissionsManager();
		return instance;
	}

	private String pluginName = "null";

	private PluginManager pluginManager = null;
	private PermissionManager pex = null;
	private GroupManager groupManager = null;

	private PermissionsManager() {
	}

	public String getGroup(String playerName) {
		if (this.pluginName.equals("PermissionsEx")) {
			if ((this.pex.getUser(playerName).getGroups() == null) || (this.pex.getUser(playerName).getGroups().length == 0)) {
				return "";
			}
			return this.pex.getUser(playerName).getGroups()[0].getName();
		}
		if (this.pluginName.equals("GroupManager")) {
			AnjoPermissionsHandler handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
			if (handler == null) {
				Gods.get().logDebug("PermissionManager(): No handler for player " + playerName);
				return "";
			}
			return handler.getGroup(playerName);
		}
		if (this.pluginName.equals("bPermissions")) {
			de.bananaco.bpermissions.api.World w = WorldManager.getInstance().getWorld(playerName);
			if (w == null) {
				return "";
			}
			if (w.getUser(playerName).getGroupsAsString().size() == 0) {
				return "";
			}
			return (String) w.getUser(playerName).getGroupsAsString().toArray()[0];
		}
		return "";
	}

	public List<String> getGroups() {
		List<String> list = new ArrayList<String>();
		if (this.pluginName.equals("PermissionsEx")) {
			for (PermissionGroup group : this.pex.getGroupList()) {
				list.add(group.getName());
			}
			return list;
		}

		Object owh;

		if (this.pluginName.equals("GroupManager")) {
			for (org.bukkit.World world : Bukkit.getServer().getWorlds()) {
				owh = this.groupManager.getWorldsHolder().getWorldData(world.getName());
				if (owh != null) {
					Collection<Group> groups = ((OverloadedWorldHolder) owh).getGroupList();

					if (groups != null) {
						for (org.anjocaido.groupmanager.data.Group group : groups) {
							list.add(group.getName());
						}
					}
				}
			}
			return list;
		}

		return list;
	}

	public String getPermissionPluginName() {
		return this.pluginName;
	}

	public String getPrefix(String playerName) {
		if (this.pluginName.equals("PermissionsBukkit")) {
			return "";
		}
		if (this.pluginName.equals("PermissionsEx")) {
			return this.pex.getUser(this.pluginName).getOwnSuffix();
		}
		if (this.pluginName.equals("GroupManager")) {
			AnjoPermissionsHandler handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(playerName);
			if (handler == null) {
				return "";
			}
			return handler.getUserPrefix(playerName);
		}
		if (this.pluginName.equals("bPermissions")) {
			de.bananaco.bpermissions.api.World w = WorldManager.getInstance().getWorld(playerName);
			if (w == null) {
				return "";
			}
			return "";
		}
		return "";
	}

	public boolean hasPermission(Player player, String node) {
		if (this.pluginName.equals("PermissionsEx")) {
			return this.pex.has(player, node);
		}
		if (this.pluginName.equals("GroupManager")) {
			AnjoPermissionsHandler handler = this.groupManager.getWorldsHolder().getWorldPermissionsByPlayerName(player.getName());
			if (handler == null) {
				return false;
			}
			return handler.permission(player, node);
		}
		if (this.pluginName.equals("bPermissions")) {
			return ApiLayer.hasPermission(player.getWorld().getName(), CalculableType.USER, player.getName(), node);
		}
		return player.hasPermission(node);
	}

	public boolean isGroup(String groupName) {
		if (this.pluginName.equals("PermissionsEx")) {
			if (this.pex.getGroup(groupName) == null) {
				return false;
			}
			return true;
		}
		if (this.pluginName.equals("GroupManager")) {
			if (GroupManager.getGlobalGroups().getGroup(groupName) == null) {
				return false;
			}
			return true;
		}

		return false;
	}

	public void load() {
		this.pluginManager = Gods.get().getServer().getPluginManager();
		if (this.pluginManager.getPlugin("PermissionsEx") != null) {
			Gods.get().log("Using PermissionsEx.");
			this.pluginName = "PermissionsEx";
			this.pex = PermissionsEx.getPermissionManager();
		} else if (this.pluginManager.getPlugin("GroupManager") != null) {
			Gods.get().log("Using GroupManager");
			this.pluginName = "GroupManager";
			this.groupManager = ((GroupManager) this.pluginManager.getPlugin("GroupManager"));
		} else if (this.pluginManager.getPlugin("bPermissions") != null) {
			Gods.get().log("Using bPermissions.");
			this.pluginName = "bPermissions";
		} else {
			Gods.get().log("No permissions plugin detected! Defaulting to superperm");
			this.pluginName = "SuperPerm";
		}
	}

	public void setGroup(String playerName, String groupName) {
		String[] groups;
		if (this.pluginName.equals("PermissionsEx")) {
			PermissionUser user = PermissionsEx.getPermissionManager().getUser(playerName);

			groups = new String[] { groupName };
			user.setGroups(groups);
		} else if (this.pluginName.equals("bPermissions")) {
			for (org.bukkit.World world : Gods.get().getServer().getWorlds()) {
				ApiLayer.setGroup(world.getName(), CalculableType.USER, playerName, groupName);
			}
		} else if (this.pluginName.equals("GroupManager")) {
			OverloadedWorldHolder owh = this.groupManager.getWorldsHolder().getWorldDataByPlayerName(playerName);
			if (owh == null) {
				return;
			}
			org.anjocaido.groupmanager.data.User user = owh.getUser(playerName);
			if (user == null) {
				Gods.get().log("No player with the name '" + groupName + "'");
				return;
			}
			org.anjocaido.groupmanager.data.Group group = owh.getGroup(groupName);
			if (group == null) {
				Gods.get().log("No group with the name '" + groupName + "'");
				return;
			}
			user.setGroup(group);

			Player p = Bukkit.getPlayer(playerName);
			if (p != null) {
				GroupManager.BukkitPermissions.updatePermissions(p);
			}
		}

	}
}