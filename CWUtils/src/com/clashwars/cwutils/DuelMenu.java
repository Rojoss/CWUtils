package com.clashwars.cwutils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
	
	
	public void clickItem(DuelMenu menu, Player player, ItemStack item, int slot) {
		DuelRunnable dr = dm.getDuel(player);
		
		Bukkit.broadcastMessage("Clicking slot " + slot);
		
		//Coin management player 1.
		if (slot == 1) {
			dr.setPlayer1Coins(dr.getPlayer1Coins() + 100);
			dm.setCoins(dr);
		}
		if (slot == 2) {
			if (dr.getPlayer1Coins() < 100) {
				dr.setPlayer1Coins(0);
			} else {
				dr.setPlayer1Coins(dr.getPlayer1Coins() - 100);
			}
			dm.setCoins(dr);
		}
		
		//Coin management player 2.
		if (slot == 7) {
			dr.setPlayer1Coins(dr.getPlayer1Coins() + 100);
			dm.setCoins(dr);
		}
		if (slot == 6) {
			if (dr.getPlayer1Coins() < 100) {
				dr.setPlayer1Coins(0);
			} else {
				dr.setPlayer1Coins(dr.getPlayer1Coins() - 100);
			}
			dm.setCoins(dr);
		}
		
		//Ready up buttons.
		if (slot == 3) {
			if (dr.isPlayer1Ready()) {
				dr.setPlayer1Ready(false);
			} else {
				dr.setPlayer1Ready(true);
			}
			dm.setReady(dr);
		}
		if (slot == 5) {
			if (dr.isPlayer2Ready()) {
				dr.setPlayer2Ready(false);
			} else {
				dr.setPlayer2Ready(true);
			}
			dm.setReady(dr);
		}
		
		//Armor button.
		if (slot == 22) {
			switch (item.getType()) {
				case DIAMOND_CHESTPLATE:
					dm.setArmor(dr, Material.IRON_CHESTPLATE);
					break;
				case IRON_CHESTPLATE:
					dm.setArmor(dr, Material.GOLD_CHESTPLATE);
					break;
				case GOLD_CHESTPLATE:
					dm.setArmor(dr, Material.LEATHER_CHESTPLATE);
					break;
				case LEATHER_CHESTPLATE:
					dm.setArmor(dr, Material.STICK);
					break;
				default:
					dm.setArmor(dr, Material.DIAMOND_CHESTPLATE);
					break;
			}
		}
		
		//Weapon button.
		if (slot == 31) {
			switch (item.getType()) {
				case DIAMOND_SWORD:
					dm.setWeapon(dr, Material.IRON_SWORD);
					break;
				case IRON_SWORD:
					dm.setWeapon(dr, Material.STONE_SWORD);
					break;
				case STONE_SWORD:
					dm.setWeapon(dr, Material.WOOD_SWORD);
					break;
				case WOOD_SWORD:
					dm.setWeapon(dr, Material.STICK);
					break;
				default:
					dm.setWeapon(dr, Material.DIAMOND_SWORD);
					break;
			}
		}
		
		//Bow button.
		if (slot == 40) {
			if (dr.getBow()) {
				dm.setBow(dr, false);
			} else {
				dm.setBow(dr, true);
			}
		}
		
		//Potions button.
		if (slot == 49) {
			if (dr.getPotions()) {
				dm.setPotions(dr, false);
			} else {
				dm.setPotions(dr, true);
			}
		}
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
				//boolean top = raw <= menu.getSize();
				
				boolean correctSide = false;
				
				for (int y = 0; y < menu.getSize(); y += 9) {
					for (int x = 0; x < 9; x++) {
						int slot = y + x;
						if (slot == raw) {
							if (x < 5) {
								if (menu.dm.getDuel(player).getPlayer1() == player) {
									correctSide = true;
								}
							} else {
								if (menu.dm.getDuel(player).getPlayer2() == player) {
									correctSide = true;
								}
							}
							break;
						}
					}
				}
				
				
				if (!correctSide) {
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
						menu.clickItem(menu, player, item, i);
						
						event.setCancelled(true);
						event.setResult(Result.DENY);
						event.setCursor(null);
						player.updateInventory();
					}
				}
			}
		}
		
		@EventHandler(priority = EventPriority.HIGHEST)
		public void close(InventoryCloseEvent event) {
			Player player = (Player) event.getPlayer();
			Inventory inv = event.getInventory();
			
			for (DuelMenu menu : menus) {
				if (!inv.getTitle().equals(menu.getTitle()) || inv.getSize() != menu.getSize() || !inv.getHolder().equals(player)) {
					continue;
				}
				
				menu.getOpenInvs().remove(inv);
				menu.dm.cancelSetup(player, menu.dm.getDuel(player).getPlayer2());
			}
		}
	}
	
	
}
