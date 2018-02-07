package com.dogonfire.gods;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class HolyLandManager implements Listener {
	private Gods plugin = null;
	private FileConfiguration landConfig = null;
	private File landConfigFile = null;
	private HashMap<String, String> fromLocations = new HashMap<String, String>();
	private HashMap<Location, Long> hashedLocations = new HashMap<Location, Long>();
	private String pattern = "HH:mm dd-MM-yyyy";
	private DateFormat formatter = new SimpleDateFormat(this.pattern);

	HolyLandManager(Gods p) {
		this.plugin = p;
	}

	public void load() {
		if (this.landConfigFile == null) {
			this.landConfigFile = new File(this.plugin.getDataFolder(), "holyland.yml");
		}
		this.landConfig = YamlConfiguration.loadConfiguration(this.landConfigFile);

		this.plugin.log("Loaded " + this.landConfig.getKeys(false).size() + " holy land entries.");
	}

	public void save() {
		if ((this.landConfig == null) || (this.landConfigFile == null)) {
			return;
		}
		try {
			this.landConfig.save(this.landConfigFile);
		} catch (Exception ex) {
			this.plugin.log("Could not save config to " + this.landConfigFile + ": " + ex.getMessage());
		}
	}

	public boolean isSameChunk(Location one, Location two) {
		if (one.getBlockX() >> 7 != two.getBlockX() >> 7) {
			return false;
		}
		if (one.getBlockZ() >> 7 != two.getBlockZ() >> 7) {
			return false;
		}
		if (one.getWorld() != two.getWorld()) {
			return false;
		}
		return true;
	}

	public long hashLocation(Location location) {
		if (this.hashedLocations.containsKey(location)) {
			return ((Long) this.hashedLocations.get(location)).longValue();
		}
		int chunkX = location.getBlockX() >> 7;
		int chunkZ = location.getBlockZ() >> 7;

		long x = chunkX << 32;
		long z = chunkZ & 0xFFFFFFFF;

		long hash = x | z;

		this.hashedLocations.put(location, Long.valueOf(hash));

		return hash;
	}

	public long getHolyLandIdentifierFromLocation(Location location) {
		long x = location.getBlockX() << 32;
		long z = location.getBlockZ() & 0xFFFFFFFF;

		return x | z;
	}

	public String getGodForBeliever(String believerName) {
		return this.landConfig.getString(believerName + ".God");
	}

	public Set<String> getBelievers() {
		Set<String> allBelievers = this.landConfig.getKeys(false);

		return allBelievers;
	}

	public String getNearestBeliever(Location location) {
		Set<String> allBelievers = this.landConfig.getKeys(false);
		double minLength = 999999.0D;
		Player minPlayer = null;
		for (String believerName : allBelievers) {
			Player player = this.plugin.getServer().getPlayer(believerName);
			if ((player != null) && (player.getWorld() == location.getWorld())) {
				double length = player.getLocation().subtract(location).length();
				if (length < minLength) {
					minLength = length;
					minPlayer = player;
				}
			}
		}
		if (minPlayer == null) {
			return null;
		}
		return minPlayer.getName();
	}

	public void setNeutralLand(Location location) {
		Date thisDate = new Date();

		long hash = hashLocation(location);

		this.landConfig.set(hash + ".NeutralLand", this.formatter.format(thisDate));

		save();
	}

	public void clearNeutralLand(Location location) {
		long hash = hashLocation(location);

		this.landConfig.set(hash + ".NeutralLand", null);

		save();
	}

	public boolean isNeutralLandLocation(Location location) {
		return this.landConfig.getString(hashLocation(location) + ".NeutralLand") != null;
	}

	public boolean isContestedLand(Location location) {
		return this.landConfig.getString(hashLocation(location) + ".AttackingGodName") != null;
	}

	public boolean isMobTypeAllowedToSpawn(EntityType mobType) {
		if ((mobType == EntityType.BAT) || (mobType == EntityType.SQUID) || (mobType == EntityType.CHICKEN) || (mobType == EntityType.PIG) || (mobType == EntityType.COW) || (mobType == EntityType.OCELOT) || (mobType == EntityType.SHEEP)
				|| (mobType == EntityType.VILLAGER) || (mobType == EntityType.MUSHROOM_COW) || (mobType == EntityType.IRON_GOLEM)) {
			return true;
		}
		return false;
	}

	public void setPrayingHotspot(String believerName, String godName, Location location) {
		Location clampedLocation = new Location(location.getWorld(), location.getBlockX(), 0.0D, location.getBlockZ());

		long hash = hashLocation(clampedLocation);

		Date thisDate = new Date();
		if (this.landConfig.getString(hash + ".FirstPrayerTime") == null) {
			this.landConfig.set(hash + ".FirstPrayerTime", this.formatter.format(thisDate));
		}
		this.landConfig.set(hash + ".GodName", godName);
		this.landConfig.set(hash + ".LastPrayerTime", this.formatter.format(thisDate));
		this.landConfig.set(hash + ".World", clampedLocation.getWorld().getName());

		save();
	}

	public void setHolyLand(Location location, String godName) {
		long hash = hashLocation(location);

		Date thisDate = new Date();
		if (this.landConfig.getString(hash + ".FirstPrayerTime") == null) {
			this.landConfig.set(hash + ".FirstPrayerTime", this.formatter.format(thisDate));
		}
		this.landConfig.set(hash + ".GodName", godName);
		this.landConfig.set(hash + ".LastPrayerTime", this.formatter.format(thisDate));
		this.landConfig.set(hash + ".World", location.getWorld().getName());

		save();
	}

	public String getGodAtHolyLandLocationFrom(String believerName) {
		return (String) this.fromLocations.get(believerName);
	}

	public String getGodAtHolyLandLocationTo(String believerName, Location location) {
		String godName = getGodAtHolyLandLocation(location);

		this.fromLocations.put(believerName, godName);

		return godName;
	}

	public void setNeutralLandLocationFrom(String believerName) {
		this.fromLocations.put(believerName, "NeutralLand");
	}

	public boolean deleteGodAtHolyLandLocation(Location location) {
		Location clampedLocation = new Location(null, location.getBlockX(), 0.0D, location.getBlockZ());

		long holylandHash = hashLocation(clampedLocation);

		String godName = this.landConfig.getString(holylandHash + ".GodName");
		if (godName != null) {
			this.landConfig.set(holylandHash + ".GodName", null);

			save();

			return true;
		}
		return false;
	}

	public void setContestedLand(Location location, String attackingGodName) {
		this.landConfig.set(hashLocation(location) + ".AttackingGodName", attackingGodName);

		save();
	}

	public void clearContestedLand(Location location) {
		Date thisDate = new Date();

		long hash = hashLocation(location);

		this.landConfig.set(hash + ".AttackingGodName", null);

		this.landConfig.set(hash + ".ContestedTime", this.formatter.format(thisDate));

		save();
	}

	public long getContestedTimeAtHolyLand(Location location) {
		Date thisDate = new Date();
		Date contestedDate = null;
		String contestedTime = this.landConfig.getString(hashLocation(location) + ".ContestedTime");
		try {
			contestedDate = this.formatter.parse(contestedTime);
		} catch (Exception ex) {
			contestedDate = new Date();
			contestedDate.setTime(0L);
		}
		long diff = thisDate.getTime() - contestedDate.getTime();
		long diffMinutes = diff / 60000L % 60L;

		return diffMinutes;
	}

	public String getGodAtHolyLandLocation(Location location) {
		String godName = this.landConfig.getString(hashLocation(location) + ".GodName");
		if (godName != null) {
			return godName;
		}
		return null;
	}

	public void handleQuit(String playerName) {
		this.fromLocations.remove(playerName);
	}

	public void removeAbandonedLands() {
		long timeBefore = System.currentTimeMillis();

		Date thisDate = new Date();
		for (String holylandHash : this.landConfig.getKeys(false)) {
			if (this.landConfig.getString(holylandHash + ".NeutralLand") != null) {
				String lastPrayerString = this.landConfig.getString(holylandHash + ".LastPrayerTime");

				String pattern = "HH:mm dd-MM-yyyy";

				DateFormat formatter = new SimpleDateFormat(pattern);

				Date lastPrayerDate = null;
				try {
					lastPrayerDate = formatter.parse(lastPrayerString);
				} catch (Exception ex) {
					this.landConfig.set(holylandHash, null);
				}
				long diff = thisDate.getTime() - lastPrayerDate.getTime();
				long diffMinutes = diff / 60000L;
				if (diffMinutes > this.plugin.numberOfDaysForAbandonedHolyLands * 24 * 60) {
					this.landConfig.set(holylandHash, null);
				}
			}
		}
		long timeAfter = System.currentTimeMillis();

		this.plugin.logDebug("Traversed " + this.landConfig.getKeys(false).size() + " Holy lands in " + (timeAfter - timeBefore) + " ms");
	}

	private void resolveContestedLand(Location location, String godName1, String godName2, boolean firstIsWinner) {
		if (firstIsWinner) {
			setHolyLand(location, godName1);
			this.plugin.getGodManager().godSayToBelievers(godName1, LanguageManager.LANGUAGESTRING.GodToBelieversDefendHolyLandSuccess, 1);
			this.plugin.getGodManager().godSayToBelievers(godName2, LanguageManager.LANGUAGESTRING.GodToBelieversAttackHolyLandFailed, 1);
		} else {
			setHolyLand(location, godName2);
			this.plugin.getGodManager().godSayToBelievers(godName1, LanguageManager.LANGUAGESTRING.GodToBelieversDefendHolyLandFailed, 1);
			this.plugin.getGodManager().godSayToBelievers(godName2, LanguageManager.LANGUAGESTRING.GodToBelieversAttackHolyLandSuccess, 1);
		}
		clearContestedLand(location);

		this.plugin.getGodManager().clearContestedHolyLandForGod(godName1);
		this.plugin.getGodManager().clearContestedHolyLandForGod(godName2);
	}

	@EventHandler
	public void OnCreatureSpawn(CreatureSpawnEvent event) {
		if (!this.plugin.isEnabledInWorld(event.getLocation().getWorld())) {
			return;
		}
		if (this.plugin.getLandManager().isNeutralLandLocation(event.getLocation())) {
			this.plugin.logDebug("Prevented " + event.getEntityType() + " from spawning in Neutral land");
			return;
		}
		String godName = this.plugin.getLandManager().getGodAtHolyLandLocation(event.getLocation());
		if (godName != null) {
			if (this.plugin.getGodManager().getDivineForceForGod(godName) == GodManager.GodType.NATURE) {
				return;
			}
			if (event.getEntity().getType() == this.plugin.getGodManager().getHolyMobTypeForGod(godName)) {
				return;
			}
			if (this.plugin.getGodManager().getGodMobSpawning(godName)) {
				return;
			}
			if (!this.plugin.getLandManager().isMobTypeAllowedToSpawn(event.getEntityType())) {
				this.plugin.logDebug("Prevented " + event.getEntityType() + " from spawning in Holy Land of " + godName);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!this.plugin.holyLandEnabled) {
			return;
		}
		if (isSameChunk(event.getFrom(), event.getTo())) {
			return;
		}
		Player player = event.getPlayer();
		if (!this.plugin.isEnabledInWorld(player.getWorld())) {
			return;
		}
		if (!this.plugin.getPermissionsManager().hasPermission(player, "gods.holyland")) {
			return;
		}
		Location to = event.getTo();

		String godFrom = null;
		String godTo = null;

		godFrom = getGodAtHolyLandLocationFrom(player.getName());
		if (isNeutralLandLocation(to)) {
			godTo = "NeutralLand";
			setNeutralLandLocationFrom(player.getName());
			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.EnterNeutralLandInfo, ChatColor.YELLOW, "You are safe against mobs and PvP", "You are safe against mobs and PvP", 1);
			return;
		}
		if (isContestedLand(to)) {
			long time = getContestedTimeAtHolyLand(to);
			if (time <= 0L) {
				godTo = getGodAtHolyLandLocationTo(player.getName(), to);
				resolveContestedLand(to, godTo, godFrom, true);
			} else {
				int defenderKillsNeeded = 5;
				int attackerKillsNeeded = 10;

				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.EnterContestedLandInfo, ChatColor.RED, (int) time, godTo, 1);
				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.ContestedLandStatus, ChatColor.AQUA, String.valueOf(time), attackerKillsNeeded, defenderKillsNeeded, 20);
			}
		} else {
			godTo = getGodAtHolyLandLocationTo(player.getName(), to);
		}
		if ((godFrom == null) && (godTo == null)) {
			return;
		}
		if ((godTo != null) && ((godFrom == null) || (!godFrom.equals(godTo)))) {
			String playerGod = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());
			if ((playerGod != null) && (playerGod.equals(godTo))) {
				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.EnterHolyLandInfoYourGod, ChatColor.GOLD, godTo, ChatColor.AQUA + this.plugin.getGodManager().getGodDescription(godTo), 1);
			} else {
				this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.EnterHolyLandInfoOtherGod, ChatColor.GREEN, godTo, ChatColor.AQUA + this.plugin.getGodManager().getGodDescription(godTo), 1);
			}
		} else if ((godFrom != null) && (godTo == null)) {
			this.plugin.sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.EnterWildernessInfo, ChatColor.DARK_GREEN, 0, "", 1);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (!this.plugin.isEnabledInWorld(player.getWorld())) {
			return;
		}
		if (!isContestedLand(player.getLocation())) {
			return;
		}
		String godName = this.plugin.getBelieverManager().getGodForBeliever(player.getUniqueId());

		Player killer = event.getEntity().getKiller();
		String killerGodName = this.plugin.getBelieverManager().getGodForBeliever(killer.getUniqueId());
		if (this.plugin.getGodManager().increaseContestedHolyLandKillsForGod(killerGodName, 1)) {
			resolveContestedLand(player.getLocation(), godName, killerGodName, false);
		}
	}

	@EventHandler
	public void OnBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (player != null) {
			if (!this.plugin.isEnabledInWorld(player.getWorld())) {
				return;
			}
			if (player.isOp()) {
				return;
			}
			if (!this.plugin.getPermissionsManager().hasPermission(player, "gods.holyland")) {
				this.plugin.logDebug(event.getPlayer().getName() + " does not have holyland permission");
				return;
			}
		}
		if (event.getBlock() == null) {
			return;
		}
		if (this.plugin.getLandManager().isNeutralLandLocation(event.getBlock().getLocation())) {
			if (player != null) {
				player.sendMessage(ChatColor.RED + "You cannot break blocks in neutral land");
			}
			event.setCancelled(true);
			return;
		}
		String godName = this.plugin.getLandManager().getGodAtHolyLandLocation(event.getBlock().getLocation());
		Player attacker = event.getPlayer();
		String attackerGod = null;
		if (godName == null) {
			return;
		}

		if (attacker != null) {
			attackerGod = this.plugin.getBelieverManager().getGodForBeliever(attacker.getUniqueId());
		}

		if ((attackerGod == null) || (!attackerGod.equals(godName))) {
			if (attacker != null) {
				attacker.sendMessage(ChatColor.RED + "You do not have access to the holy land of " + ChatColor.YELLOW + godName);
			}
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (!this.plugin.isEnabledInWorld(event.getPlayer().getWorld())) {
			return;
		}

		String godName = this.plugin.getBelieverManager().getGodForBeliever(event.getPlayer().getUniqueId());

		String targetLandGodName = this.plugin.getLandManager().getGodAtHolyLandLocation(event.getTo());

		if (event.getPlayer().isOp()) {
			return;
		}

		if (targetLandGodName != null) {
			if ((godName == null) || (!targetLandGodName.equals(godName))) {
				this.plugin.sendInfo(event.getPlayer().getUniqueId(), LanguageManager.LANGUAGESTRING.TeleportIntoHolylandNotAllowed, ChatColor.DARK_RED, 0, targetLandGodName, 1);
				event.setCancelled(true);
			}
		}
	}
}