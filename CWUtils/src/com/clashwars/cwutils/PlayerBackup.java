package com.clashwars.cwutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerBackup {
	
	double health;
	int hunger;
	int xpLvl;
	float xp;
	int fireTicks;
	Collection<PotionEffect> effects;
	ItemStack[] inv;
	ItemStack[] armor;
	
	public PlayerBackup(double health, int hunger, int xpLvL, float xp, int fireTicks, Collection<PotionEffect> effects, ItemStack[] inv, ItemStack[] armor) {
		this.health = health;
		this.hunger = hunger;
		this.xpLvl = xpLvL;
		this.xp = xp;
		this.fireTicks = fireTicks;
		this.effects = effects;
		this.inv = inv;
		this.armor = armor;
	}
	
	public PlayerBackup(Player player) {
		updateFromPlayer(player);
	}
	
	public void updateFromPlayer(Player player) {
		this.health = player.getHealth();
		this.hunger = player.getFoodLevel();
		this.xpLvl = player.getLevel();
		this.xp = player.getExp();
		this.fireTicks = player.getFireTicks();
		this.effects = player.getActivePotionEffects();
		
		ItemStack cursor = player.getItemOnCursor();
		if (cursor != null) {
			player.getInventory().addItem(cursor);
			player.setItemOnCursor(null);
		}
		
		this.inv = player.getInventory().getContents();
		this.armor = player.getInventory().getArmorContents();
	}
	
	public void loadBackup(Player player) {
		player.setHealth(health);
		player.setFoodLevel(hunger);
		player.setExp(xp);
		player.setLevel(xpLvl);
		player.setFireTicks(fireTicks);
		if (effects != null) {
			for (PotionEffect effect : effects) {
				player.addPotionEffect(effect);
			}
		}
		if (inv != null) {
			player.getInventory().setContents(inv);
		}
		if (armor != null) {
			player.getInventory().setArmorContents(armor);
		}
	}
	
	public void resetPlayer(Player player) {
		player.getInventory().clear();
		player.setHealth((double) 20);
		player.setFoodLevel(20);
		player.setExp(0f);
		player.setLevel(0);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.setFireTicks(0);
	}
	
	
	
	
	public double getHealth() {
		return health;
	}
	
	public int getFoodLevel() {
		return hunger;
	}
	
	public int getLevel() {
		return xpLvl;
	}
	
	public float getExp() {
		return xp;
	}
	
	public int getFireTicks() {
		return fireTicks;
	}
	
	public Collection<PotionEffect> getActivePotionEffects() {
		return effects;
	}
	
	public ItemStack[] getInvContents() {
		return inv;
	}
	
	public ItemStack[] getArmorContents() {
		return armor;
	}
	
	public List<Map<String, Object>> getSerializedInvContents() {
		return serializeItemStacks(inv);
	}
	
	public List<Map<String, Object>> getSerializedArmorContents() {
		return serializeItemStacks(armor);
	}
	
	
	
	
	private  List<Map<String, Object>> serializeItemStacks(ItemStack[] items) {
		List<Map<String, Object>> serializedItems = new ArrayList<Map<String, Object>>();
	    for (int i = 0; i < items.length; i++) {
	        ItemStack item = items[i];
			if (item != null) {
			    Map<String, Object> serializedItem = item.serialize();
			    serializedItem.put("slot", Integer.valueOf(i));
			    serializedItems.add(serializedItem);
			}
	    }
	    return serializedItems;
	}
}
