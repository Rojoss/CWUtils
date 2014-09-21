package com.clashwars.cwutils.runnables;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.clashwars.cwutils.CWUtils;
import com.clashwars.cwutils.DuelMenu;
import com.clashwars.cwutils.PlayerBackup;
import com.clashwars.cwutils.util.Utils;

public class DuelRunnable implements Runnable {
	
	private CWUtils cwu;
	private DuelMenu menu;
	private Player player1;
	private Player player2;
	private Material armorType;
	private Material weaponType;
	private boolean bow;
	private boolean potions;
	private long startTime;
	private int taskID;
	
	private int player1Coins = 0;
	private int player2Coins = 0;
	private boolean player1Ready = false;
	private boolean player2Ready = false;
	
	public DuelRunnable(CWUtils cwu, DuelMenu menu, Player player1, Player player2) {
		this.cwu = cwu;
		this.menu = menu;
		this.player1 = player1;
		this.player2 = player2;
	}
	
	
	public int getTimeLeft() {
		return (int) ((startTime + cwu.getCfg().getTagTime()) - System.currentTimeMillis() / 1000);
	}
	
	public Player getPlayer1() {
		return player1;
	}
	
	public Player getPlayer2() {
		return player2;
	}
	
	public DuelMenu getMenu() {
		return menu;
	}
	
	
	
	public int getPlayer1Coins() {
		return player1Coins;
	}
	public int getPlayer2Coins() {
		return player2Coins;
	}
	
	public void setPlayer1Coins(int coins) {
		player1Coins = coins;
	}
	public void setPlayer2Coins(int coins) {
		player2Coins = coins;
	}
	
	public boolean isPlayer1Ready() {
		return player1Ready;
	}
	public boolean isPlayer2Ready() {
		return player2Ready;
	}
	
	public void setPlayer1Ready(boolean ready) {
		player1Ready = ready;
	}
	public void setPlayer2Ready(boolean ready) {
		player2Ready = ready;
	}
	
	
	public void setArmor(Material armorType) {
		this.armorType = armorType;
	}
	
	public void setWeapon(Material weapon) {
		this.weaponType = weapon;
	}
	
	public boolean getBow() {
		return bow;
	}
	public void setBow(boolean bow) {
		this.bow = bow;
	}
	
	public boolean getPotions() {
		return bow;
	}
	public void setPotions(boolean potions) {
		this.potions = potions;
	}
	
	
	public void start() {
		player1.closeInventory();
		player2.closeInventory();
		
		player1.sendMessage(Utils.integrateColor("&8[&4CW Duel&8] &7Making a backup of your character..."));
		player2.sendMessage(Utils.integrateColor("&8[&4CW Duel&8] &7Making a backup of your character..."));
		
		PlayerBackup backup1 = new PlayerBackup(player1);
		PlayerBackup backup2 = new PlayerBackup(player2);
		
		cwu.getCfg().addBackup(player1.getUniqueId(), backup1);
		cwu.getCfg().addBackup(player2.getUniqueId(), backup2);
		cwu.getPBConfig().save(player1.getUniqueId());
		cwu.getPBConfig().save(player2.getUniqueId());
		
		Utils.resetPlayer(player1);
		Utils.resetPlayer(player2);
		
		equipItems(player1);
		equipItems(player2);
			
		taskID = cwu.getServer().getScheduler().scheduleSyncRepeatingTask(cwu, new Runnable() {
		    int count = 3;
		    @Override
		    public void run() {
		    	if(count == 0) {
		    		Bukkit.getScheduler().cancelTask(taskID);
		    		
		    		player1.sendMessage(Utils.integrateColor("&8[&4CW Duel&8] &6The duel has started!"));
		    		player2.sendMessage(Utils.integrateColor("&8[&4CW Duel&8] &6The duel has started!"));
		    		
		    		startTime = System.currentTimeMillis();
		    		cwu.getServer().getScheduler().runTaskAsynchronously(cwu, this);
		    		return;
		        } else {
		        	player1.sendMessage(Utils.integrateColor("&8[&4CW Duel&8] &6The duel will start in &a&l" + count));
			        player2.sendMessage(Utils.integrateColor("&8[&4CW Duel&8] &6The duel will start in &a&l" + count));
			        count--;
		        }
		    }
		}, 0, 20);
	}
	
	
	private void equipItems(Player player) {
		//Equip specified armor.
		switch (armorType) {
			case DIAMOND_CHESTPLATE:
				player.getInventory().setHelmet(setDuelItem(Material.DIAMOND_HELMET, 1, 0));
				player.getInventory().setChestplate(setDuelItem(Material.DIAMOND_CHESTPLATE, 1, 0));
				player.getInventory().setLeggings(setDuelItem(Material.DIAMOND_LEGGINGS, 1, 0));
				player.getInventory().setBoots(setDuelItem(Material.DIAMOND_BOOTS, 1, 0));
				break;
			case IRON_CHESTPLATE:
				player.getInventory().setHelmet(setDuelItem(Material.IRON_HELMET, 1, 0));
				player.getInventory().setChestplate(setDuelItem(Material.IRON_CHESTPLATE, 1, 0));
				player.getInventory().setLeggings(setDuelItem(Material.IRON_LEGGINGS, 1, 0));
				player.getInventory().setBoots(setDuelItem(Material.IRON_BOOTS, 1, 0));
				break;
			case GOLD_CHESTPLATE:
				player.getInventory().setHelmet(setDuelItem(Material.GOLD_HELMET, 1, 0));
				player.getInventory().setChestplate(setDuelItem(Material.GOLD_CHESTPLATE, 1, 0));
				player.getInventory().setLeggings(setDuelItem(Material.GOLD_LEGGINGS, 1, 0));
				player.getInventory().setBoots(setDuelItem(Material.GOLD_BOOTS, 1, 0));
				break;
			case LEATHER_CHESTPLATE:
				player.getInventory().setHelmet(setDuelItem(Material.LEATHER_HELMET, 1, 0));
				player.getInventory().setChestplate(setDuelItem(Material.LEATHER_CHESTPLATE, 1, 0));
				player.getInventory().setLeggings(setDuelItem(Material.LEATHER_LEGGINGS, 1, 0));
				player.getInventory().setBoots(setDuelItem(Material.LEATHER_BOOTS, 1, 0));
				break;
			default:
				player.getInventory().setHelmet(null);
				player.getInventory().setChestplate(null);
				player.getInventory().setLeggings(null);
				player.getInventory().setBoots(null);
				break;
		}
		//Equip weapon bow and potions.
		if (weaponType != null && weaponType != Material.STICK) {
			player.getInventory().addItem(setDuelItem(weaponType, 1, 0));
		}
		if (bow) {
			player.getInventory().addItem(setDuelItem(Material.BOW, 1, 0));
			player.getInventory().addItem(setDuelItem(Material.ARROW, 10, 0));
		}
		if (potions) {
			player.getInventory().addItem(setDuelItem(Material.POTION, 1, 8193)); //Regen(0:45)
			player.getInventory().addItem(setDuelItem(Material.POTION, 3, 16385)); //Healing splash
			player.getInventory().addItem(setDuelItem(Material.POTION, 2, 16396)); //Harming splash
		}
	}
	
	private ItemStack setDuelItem(Material mat, int amt, int dur) {
		ItemStack is = new ItemStack(mat, amt, (short)dur);
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_RED + "Duel " + meta.getDisplayName());
		meta.setLore(Arrays.asList(ChatColor.DARK_RED + "This is a duel item!"));
		is.setItemMeta(meta);
		return is;
	}
	
	
	
	
	@Override
	public void run() {
		try {
			Player winner = null;
			//TODO: Change to duel time.
			while ((startTime + cwu.getCfg().getTagTime()) < System.currentTimeMillis()) {
				Thread.sleep(100);
				
				if (player1.isDead() || !player1.isOnline()) {
					winner = player2;
					break;
				}
				if (player2.isDead() || !player2.isOnline()) {
					winner = player2;
					break;
				}
				if (getTimeLeft() <= 0) {
					break;
				}
			}
			cwu.getDM().stop(player1, player2, winner);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
