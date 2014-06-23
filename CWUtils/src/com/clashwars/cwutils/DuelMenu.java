package com.clashwars.cwutils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.clashwars.cwutils.runnables.DuelRunnable;
import com.clashwars.cwutils.util.Utils;

public class DuelMenu implements Listener {

	private DuelManager dm;
	private String name;
	private int size;
	private String title;
	private ItemStack[] items;
	private Set<Inventory> openInvs = new HashSet<Inventory>();
	static Set<DuelMenu> menus = new HashSet<DuelMenu>();
	
	public DuelMenu(DuelManager dm, String name, int size, String title) {
		this.dm = dm;
		this.name = name;
		this.size = size;
		this.title = title;
		this.items = new ItemStack[size];
		menus.add(this);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public ItemStack[] getItems() {
		return items;
	}
	
	public Set<Inventory> getOpenInvs() {
		return openInvs;
	}
	
	public void setSlot(ItemStack item, int slot) {
		items[slot] = item;
		updateSlot(item, slot);
	}
	
	public void setSlot(String name, int slot) {
		if (items[slot] != null) {
			ItemStack item = items[slot];
			item.getItemMeta().setDisplayName(Utils.integrateColor(name));
			updateSlot(item, slot);
		}
	}
	
	public void setSlot(List<String> lore, int slot) {
		if (items[slot] != null) {
			ItemStack item = items[slot];
			item.getItemMeta().setLore(lore);
			updateSlot(item, slot);
		}
	}
	
	public void addEnchant(Enchantment enchant, int lvl, int slot) {
		if (items[slot] != null) {
			ItemStack item = items[slot];
			item.getItemMeta().addEnchant(enchant, lvl, true);
			updateSlot(item, slot);
		}
	}
	
	private void updateSlot(ItemStack item, int slot) {
		for (Inventory inv : openInvs) {
			inv.setItem(slot, item);
		}
	}
	
	public void show(Player player) {
		player.closeInventory();
		
		Inventory inv = Bukkit.createInventory(player, size, title);
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				inv.setItem(i, items[i]);
			}
		}
		
		player.openInventory(inv);
		openInvs.add(inv);
	}
	
	
	public void clickItem(Player player, ItemStack item, int slot) {
		if (slot < 9) {
			DuelRunnable dr = dm.getDuel(player);
			switch (ChatColor.stripColor(item.getItemMeta().getDisplayName()).toLowerCase()) {
				case "diamond armor":
					dr.setArmor(Material.IRON_CHESTPLATE);
					dm.setArmor(dr, Material.IRON_CHESTPLATE);
					break;
				case "iron armor":
					dr.setArmor(Material.GOLD_CHESTPLATE);
					dm.setArmor(dr, Material.GOLD_CHESTPLATE);
					break;
				case "gold armor":
					dr.setArmor(Material.LEATHER_CHESTPLATE);
					dm.setArmor(dr, Material.LEATHER_CHESTPLATE);
					break;
				case "leather armor":
					dr.setArmor(Material.STICK);
					dm.setArmor(dr, Material.STICK);
					break;
				case "no armor":
					dr.setArmor(Material.DIAMOND_CHESTPLATE);
					dm.setArmor(dr, Material.DIAMOND_CHESTPLATE);
					break;
				case "diamond sword":
					dr.setArmor(Material.IRON_SWORD);
					dm.setArmor(dr, Material.IRON_SWORD);
					break;
				case "iron sword":
					dr.setArmor(Material.STONE_SWORD);
					dm.setArmor(dr, Material.STONE_SWORD);
					break;
				case "stone sword":
					dr.setArmor(Material.WOOD_SWORD);
					dm.setArmor(dr, Material.WOOD_SWORD);
					break;
				case "wood sword":
					dr.setArmor(Material.STICK);
					dm.setArmor(dr, Material.STICK);
					break;
				case "no sword":
					dr.setArmor(Material.DIAMOND_SWORD);
					dm.setArmor(dr, Material.DIAMOND_SWORD);
					break;
				case "bow":
					dr.setBow(false);
					dm.setBow(dr, false);
					break;
				case "no bow":
					dr.setBow(true);
					dm.setBow(dr, true);
					break;
				case "potions":
					dr.setPotions(false);
					dm.setPotions(dr, false);
					break;
				case "no potions":
					dr.setPotions(true);
					dm.setPotions(dr, true);
					break;
				default:
					break;
			}
		}
		//TODO: Dropping items in for slot 9 and above.
	}

	public static class Events implements Listener {
		@SuppressWarnings("deprecation")
		@EventHandler(priority = EventPriority.HIGHEST)
		public void click(InventoryClickEvent event) {
			Player player = (Player) event.getWhoClicked();
			Inventory inv = event.getInventory();
			
			for (DuelMenu menu : menus) {
				if (!inv.getTitle().equals(menu.getTitle()) || inv.getSize() != menu.getSize() || !inv.getHolder().equals(player)) {
					continue;
				}
				
				int raw = event.getRawSlot();
				boolean top = raw <= menu.getSize();
				
				if (!top) {
					event.setCancelled(true);
					event.setResult(Result.DENY);
					event.setCursor(null);
					player.updateInventory();
					continue;
				}
				
				ItemStack current = event.getCurrentItem();
				
				if (current == null || current.getType() == Material.AIR) {
					continue;
				}
				
				for (int i = 0; i < menu.getItems().length; i++) {
					ItemStack item = menu.getItems()[i];
					
					if (item != null && i == raw) {
						menu.clickItem(player, item, i);
						
						event.setCancelled(true);
						event.setResult(Result.DENY);
						event.setCursor(null);
						player.updateInventory();
					}
				}
			}
		}
	}
	
	
}
