package com.dogonfire.gods;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class HolyBook
{
	private final ItemStack	s;

	public HolyBook(ItemStack itemStack) throws Exception
	{
		if ((itemStack.getType() != Material.WRITTEN_BOOK) && (itemStack.getType() != Material.BOOK_AND_QUILL))
		{
			throw new Exception("HolyBook: CraftItemStack is not Material.WRITTEN_BOOK or Material.BOOK_AND_QUILL");
		}
		this.s = itemStack;
	}

	public ItemStack getItem()
	{
		return this.s;
	}

	public boolean hasTitle()
	{
		return this.s.getItemMeta().hasDisplayName();
	}

	public boolean hasAuthor()
	{
		return ((BookMeta) this.s.getItemMeta()).hasAuthor();
	}

	public boolean hasPages()
	{
		return ((BookMeta) this.s.getItemMeta()).hasPages();
	}

	public String getTitle()
	{
		return ((BookMeta) this.s.getItemMeta()).getTitle();
	}

	public String getAuthor()
	{
		return ((BookMeta) this.s.getItemMeta()).getAuthor();
	}

	public List<String> getPages()
	{
		return ((BookMeta) this.s.getItemMeta()).getPages();
	}

	public void setTitle(String name)
	{
		ItemMeta meta = this.s.getItemMeta();
		BookMeta bookMeta = (BookMeta) meta;
		bookMeta.setTitle(ChatColor.GOLD + name);
		this.s.setItemMeta(bookMeta);
	}

	public void setAuthor(String author)
	{
		ItemMeta meta = this.s.getItemMeta();
		BookMeta bookMeta = (BookMeta) meta;
		bookMeta.setAuthor(ChatColor.GOLD + author);
		this.s.setItemMeta(bookMeta);
	}

	public void setPages(List<String> pages)
	{
		ItemMeta meta = this.s.getItemMeta();
		BookMeta bookMeta = (BookMeta) meta;
		bookMeta.setPages(pages);
		this.s.setItemMeta(bookMeta);
	}

	public ItemStack getItemStack()
	{
		return this.s;
	}
}