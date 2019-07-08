package com.dogonfire.gods.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

import com.dogonfire.gods.Gods;
import com.dogonfire.gods.config.GodsConfiguration;
import com.dogonfire.gods.managers.GodManager.GodType;

public class AltarManager
{
	private static AltarManager instance;

	public static AltarManager get()
	{
		if (instance == null)
			instance = new AltarManager();
		return instance;
	}

	private Random							random			= new Random();
	private Map<Integer, String>			droppedItems	= new HashMap<Integer, String>();
	private Map<Material, List<GodType>>	altarBlockTypes	= new HashMap<Material, List<GodType>>();

	private AltarManager()
	{
	}

	public void addDroppedItem(int entityID, String playerName)
	{
		this.droppedItems.put(Integer.valueOf(entityID), playerName);
	}

	public void clearDroppedItems()
	{
		Gods.get().logDebug("Cleared " + this.droppedItems.size() + " dropped items");
		this.droppedItems.clear();
	}

	public Block getAltarBlockFromSign(Block block)
	{
		if ((block == null) || (block.getType() != Material.OAK_WALL_SIGN))
		{
			return null;
		}
		BlockData m = block.getBlockData();
		
		Directional face = (Directional)m;
		
		Block altarBlock = block.getRelative(face.getFacing().getOppositeFace());

		Gods.get().logDebug("getAltarBlockFromSign(): AltarBlock block is " + altarBlock.getType().name());
		if (getGodTypeForAltarBlockType(altarBlock.getType()) == null)
		{
			return null;
		}
		if ((!altarBlock.getRelative(BlockFace.UP).getType().equals(Material.TORCH)) && (!altarBlock.getRelative(BlockFace.UP).getType().equals(Material.REDSTONE_TORCH)))
		{
			return null;
		}
		return altarBlock;
	}

	public List<String> getAltarBlockTypesFromGodType(GodManager.GodType godType)
	{
		List<String> list = new ArrayList<String>();
		for (Material blockMaterial : this.altarBlockTypes.keySet())
		{
			if ((this.altarBlockTypes.get(blockMaterial) != null) && (((List<?>) this.altarBlockTypes.get(blockMaterial)).contains(godType)))
			{
				list.add(blockMaterial.name());
			}
		}
		return list;
	}

	public Player getBlessedPlayerFromAltarSign(Block block, String[] lines)
	{
		if ((block == null) || (block.getType() != Material.OAK_WALL_SIGN))
		{
			return null;
		}
		String cursesName = lines[0].trim();
		if (!cursesName.equalsIgnoreCase("blessings"))
		{
			return null;
		}

		String playerName = lines[2];

		if ((playerName == null) || (playerName.length() < 1))
		{
			return null;
		}

		return Gods.get().getServer().getPlayer(playerName);
	}

	public Player getCursedPlayerFromAltar(Block block, String[] lines)
	{
		if ((block == null) || (block.getType() != Material.OAK_WALL_SIGN))
		{
			return null;
		}

		String cursesName = lines[0].trim();
		if (!cursesName.equalsIgnoreCase("curses"))
		{
			return null;
		}

		String playerName = lines[2];
		if ((playerName == null) || (playerName.length() < 1))
		{
			return null;
		}

		return Gods.get().getServer().getPlayer(playerName);
	}

	public String getDroppedItemPlayer(int entityID)
	{
		return this.droppedItems.get(entityID);
	}

	public GodManager.GodGender getGodGenderFromAltarBlock(Block block)
	{
		if (block.getRelative(BlockFace.UP).getType().equals(Material.REDSTONE_TORCH))
		{
			return GodManager.GodGender.Female;
		}
		return GodManager.GodGender.Male;
	}

	public GodManager.GodType getGodTypeForAltarBlockType(Material altarBlockType)
	{
		List<GodManager.GodType> godTypes = this.altarBlockTypes.get(altarBlockType);
		if ((godTypes == null) || (godTypes.size() == 0))
		{
			Gods.get().logDebug("No god types available for block type " + altarBlockType.name() + "!");
			return null;
		}
		return godTypes.get(this.random.nextInt(godTypes.size()));
	}

	public boolean handleNewBlessingAltar(SignChangeEvent event)
	{
		Player player = event.getPlayer();

		if (!player.isOp() && !PermissionsManager.get().hasPermission(player, "gods.altar.build"))
		{
			return false;
		}
		event.setLine(0, "Blessings");
		event.setLine(1, "On");

		event.setLine(3, "");

		Gods.get().sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.BlessingsHelp, ChatColor.AQUA, 0, "", 10);

		return true;
	}

	public boolean handleNewCursingAltar(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		if ((!player.isOp()) && (!PermissionsManager.get().hasPermission(player, "gods.altar.build")))
		{
			Gods.get().sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.BuildAltarNotAllowed, ChatColor.DARK_RED, 0, "", 10);
			return false;
		}
		event.setLine(0, "Curses");
		event.setLine(1, "On");

		event.setLine(3, "");

		Gods.get().sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.CursesHelp, ChatColor.AQUA, 0, "", 20);

		return true;
	}

	public boolean handleNewPrayingAltar(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		if ((!player.isOp()) && (!PermissionsManager.get().hasPermission(player, "gods.altar.build")))
		{
			Gods.get().sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.BuildAltarNotAllowed, ChatColor.DARK_RED, 0, "", 10);
			return false;
		}

		String godName = "";
		int line = 0;
		int otherline = 0;
		while ((godName.isEmpty()) && (line < 4))
		{
			godName = event.getLine(line++);
		}

		line--;

		while (otherline < 4)
		{
			if (otherline == line)
			{
				otherline++;
			}
			else
			{
				String text = event.getLine(otherline);

				if ((text != null) && (text.length() > 0))
				{
					Gods.get().sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.InvalidAltarSign, ChatColor.DARK_RED, 0, "", 10);
					return false;
				}
				otherline++;
			}
		}

		if (godName.length() <= 1)
		{
			Gods.get().sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.InvalidGodName, ChatColor.DARK_RED, 0, "", 20);
			return false;
		}

		godName = godName.trim();
		godName = GodManager.get().formatGodName(godName);

		if ((godName.length() <= 1) || (godName.contains(" ")))
		{
			Gods.get().sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.InvalidGodName, ChatColor.DARK_RED, 0, "", 20);
			return false;
		}

		if ((Gods.get().isBlacklistedGod(godName)) || (!Gods.get().isWhitelistedGod(godName)))
		{
			Gods.get().sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.PrayToBlacklistedGodNotAllowed, ChatColor.DARK_RED, 0, "", 1);
			return false;
		}

		if (!GodManager.get().hasGodAccess(player.getUniqueId(), godName))
		{
			Gods.get().sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.PrivateGodNoAccess, ChatColor.DARK_RED, 0, "", 1);
			return false;
		}

		String currentGodName = BelieverManager.get().getGodForBeliever(player.getUniqueId());
		if (currentGodName != null && !currentGodName.equals(godName))
		{
			Gods.get().sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.CannotBuildAltarToOtherGods, ChatColor.DARK_RED, 0, godName, 1);
			return false;
		}

		Block altarBlock = getAltarBlockFromSign(player.getWorld().getBlockAt(event.getBlock().getLocation()));

		if (altarBlock == null)
		{
			return false;
		}
		if (GodManager.get().addAltar(event.getPlayer(), godName, event.getBlock().getLocation()))
		{
			if ((GodsConfiguration.get().isHolyLandEnabled()) && (PermissionsManager.get().hasPermission(player, "gods.holyland")))
			{
				HolyLandManager.get().setPrayingHotspot(player.getName(), godName, altarBlock.getLocation());
			}
			QuestManager.get().handleBuiltPrayingAltar(godName);

			event.setLine(0, "Altar");
			event.setLine(1, "of");
			event.setLine(2, godName);
			event.setLine(3, "");

			Gods.get().sendInfo(event.getPlayer().getUniqueId(), LanguageManager.LANGUAGESTRING.PrayAlterHelp, ChatColor.AQUA, 0, godName, 80);
		}
		else
		{
			event.setLine(0, "");
			event.setLine(1, "");
			event.setLine(2, "");
			event.setLine(3, "");

			return false;
		}

		return true;
	}

	public boolean handleNewSacrificingAltar(SignChangeEvent event)
	{
		Player player = event.getPlayer();
		if ((!player.isOp()) && (!PermissionsManager.get().hasPermission(player, "gods.altar.build")))
		{
			Gods.get().sendInfo(player.getUniqueId(), LanguageManager.LANGUAGESTRING.BuildAltarNotAllowed, ChatColor.DARK_RED, 0, "", 10);
			return false;
		}
		return false;
	}

	public boolean isAltarBlock(Block block)
	{
		if ((block == null) || (getGodTypeForAltarBlockType(block.getType()) == null))
		{
			return false;
		}
		if ((block.getRelative(BlockFace.UP).getType() != Material.TORCH) && (block.getRelative(BlockFace.UP).getType() != Material.REDSTONE_TORCH))
		{
			return false;
		}
		
		for (BlockFace face : new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST })
		{
			if (block.getRelative(face).getType() == Material.OAK_WALL_SIGN)
			{
				return true;
			}
		}
		
		return false;
	}

	public boolean isAltarSign(Block block)
	{
		if ((block == null) || (block.getType() != Material.OAK_WALL_SIGN))
		{
			return false;
		}
		BlockData m = block.getBlockData();
		
		Directional face = (Directional)m;
		
		Block altarBlock = block.getRelative(face.getFacing().getOppositeFace());

		Gods.get().logDebug("isAltarSign(): AltarBlock block is " + altarBlock.getType().name());
		if (getGodTypeForAltarBlockType(altarBlock.getType()) == null)
		{
			return false;
		}
		
		if ((!altarBlock.getRelative(BlockFace.UP).getType().equals(Material.TORCH)) && (!altarBlock.getRelative(BlockFace.UP).getType().equals(Material.REDSTONE_TORCH)))
		{
			return false;
		}
		return true;
	}

	public boolean isAltarTorch(Block block)
	{
		if (block == null)
		{
			return false;
		}
		if ((block.getType() != Material.TORCH) && (block.getType() != Material.REDSTONE_TORCH))
		{
			return false;
		}
		Block altarBlock = block.getRelative(BlockFace.DOWN);

		Gods.get().logDebug("isAltarTorch(): AltarBlock block is " + altarBlock.getType().name());
		if (getGodTypeForAltarBlockType(altarBlock.getType()) == null)
		{
			return false;
		}
		if (altarBlock.getRelative(BlockFace.EAST).getType() == Material.OAK_WALL_SIGN)
		{
			return true;
		}
		if (altarBlock.getRelative(BlockFace.WEST).getType() == Material.OAK_WALL_SIGN)
		{
			return true;
		}
		if (altarBlock.getRelative(BlockFace.NORTH).getType() == Material.OAK_WALL_SIGN)
		{
			return true;
		}
		if (altarBlock.getRelative(BlockFace.SOUTH).getType() == Material.OAK_WALL_SIGN)
		{
			return true;
		}
		return false;
	}

	public boolean isBlessingAltar(Block block, String[] lines)
	{
		if (!isAltarSign(block))
		{
			return false;
		}
		return getBlessedPlayerFromAltarSign(block, lines) != null;
	}

	public boolean isCursingAltar(Block block, String[] lines)
	{
		if (!isAltarSign(block))
		{
			return false;
		}
		return getCursedPlayerFromAltar(block, lines) != null;
	}

	public boolean isPrayingAltar(Block block)
	{
		return isAltarSign(block);
	}

	public void resetAltarBlockTypes()
	{
		this.altarBlockTypes.clear();

		{
			ArrayList<GodManager.GodType> list = new ArrayList<GodType>();
			list.add(GodManager.GodType.MOON);
			this.altarBlockTypes.put(Material.END_STONE, list);
		}

		{
			ArrayList<GodType> list = new ArrayList<GodType>();
			list.add(GodManager.GodType.EVIL);
			this.altarBlockTypes.put(Material.OBSIDIAN, list);
		}

		{
			ArrayList<GodManager.GodType> list = new ArrayList<GodType>();
			list.add(GodManager.GodType.FROST);
			this.altarBlockTypes.put(Material.SNOW_BLOCK, list);
		}

		{
			ArrayList<GodManager.GodType> list = new ArrayList<GodType>();
			list.add(GodManager.GodType.CREATURES);
			this.altarBlockTypes.put(Material.OAK_LOG, list);
		}

		{
			ArrayList<GodManager.GodType> list = new ArrayList<GodType>();
			list.add(GodManager.GodType.NATURE);
			this.altarBlockTypes.put(Material.MELON, list);
		}

		{
			ArrayList<GodManager.GodType> list = new ArrayList<GodType>();
			list.add(GodManager.GodType.WISDOM);
			this.altarBlockTypes.put(Material.BOOKSHELF, list);
		}

		{
			ArrayList<GodManager.GodType> list = new ArrayList<GodType>();
			list.add(GodManager.GodType.LOVE);
			this.altarBlockTypes.put(Material.DIAMOND_BLOCK, list);
		}

		{
			ArrayList<GodManager.GodType> list = new ArrayList<GodType>();
			list.add(GodManager.GodType.THUNDER);
			this.altarBlockTypes.put(Material.QUARTZ_BLOCK, list);
		}

		{
			ArrayList<GodManager.GodType> list = new ArrayList<GodType>();
			list.add(GodManager.GodType.SUN);
			this.altarBlockTypes.put(Material.SANDSTONE, list);
		}

		{
			ArrayList<GodManager.GodType> list = new ArrayList<GodType>();
			list.add(GodManager.GodType.PARTY);
			this.altarBlockTypes.put(Material.EMERALD_BLOCK, list);
		}

		{
			ArrayList<GodManager.GodType> list = new ArrayList<GodType>();
			list.add(GodManager.GodType.WAR);
			this.altarBlockTypes.put(Material.NETHERRACK, list);
		}

		{
			ArrayList<GodManager.GodType> list = new ArrayList<GodType>();
			list.add(GodManager.GodType.SEA);
			this.altarBlockTypes.put(Material.LAPIS_BLOCK, list);
		}

		if (GodsConfiguration.get().isWerewolfEnabled())
		{
			ArrayList<GodManager.GodType> list = new ArrayList<GodType>();
			list.add(GodManager.GodType.WEREWOLVES);
			this.altarBlockTypes.put(Material.OAK_WOOD, list);
		}
	}

	public void setAltarBlockTypeForGodType(GodManager.GodType godType, Material blockMaterial)
	{
		List<GodManager.GodType> godTypes = new ArrayList<GodType>();
		if (this.altarBlockTypes.containsKey(blockMaterial))
		{
			godTypes = this.altarBlockTypes.get(blockMaterial);
		}
		if (!godTypes.contains(godType))
		{
			godTypes.add(godType);
		}
		this.altarBlockTypes.put(blockMaterial, godTypes);
	}
}