package com.clashwars.cwutils;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.clashwars.cwutils.runnables.DuelRunnable;
import com.clashwars.cwutils.util.ItemUtils;
import com.clashwars.cwutils.util.Utils;

public class DuelManager {
	
	private CWUtils cwu;
	private Map<Player, DuelRunnable> duels;
	
	public DuelManager(CWUtils cwu) {
		this.cwu = cwu;
	}
	
	//Create a new duel between 2 players.
	public void createDuel(Player player1, Player player2) {
		DuelMenu menu = new DuelMenu(this, player1.getName() + player2.getName(), 27, "&4&lDuel: &r" + player1.getName() + "<~>" + player2.getName());
		
		DuelRunnable dr = null;
		if (!duels.containsKey(player1)) {
			dr = new DuelRunnable(cwu, menu, player1, player2);
			duels.put(player1, dr);
		} else {
			dr = getDuel(player1);
		}
		
		//TODO: set these to saved defaults.
		setArmor(dr, Material.DIAMOND_CHESTPLATE);
		setWeapon(dr, Material.DIAMOND_SWORD);
		setBow(dr, true);
		setPotions(dr, true);
		
		
		
		
		//menu.setSlot(new Entry(null, 0, null), 0);
		
		menu.show(player1);
		menu.show(player2);
	}

	public void stop(Player player1, Player player2, Player winner) {
		//Get the ending message.
		String message = "&8[&4CW Duel&8] &cThe time has ran out! Nobody wins.";
		if (winner == player1) {
			if (player2.isDead()) {
				message = "&8[&4CW Duel&8] &a&l" + player1.getName() + " &6wins by killing " + player2.getName();
			} else {
				message = "&8[&4CW Duel&8] &a&l" + player1.getName() + " &6because " + player2.getName() + " logged off.";
			}
		} else {
			if (player1.isDead()) {
				message = "&8[&4CW Duel&8] &a&l" + player2.getName() + " &6wins by killing " + player1.getName();
			} else {
				message = "&8[&4CW Duel&8] &a&l" + player2.getName() + " &6because " + player1.getName() + " logged off.";
			}
		}
		
		//Send the actual message if online otherwise send it on login.
		if (player1.isOnline()) {
			player1.sendMessage(Utils.integrateColor(message));
		} else {
			cwu.getConfig().addMessage(player1.getUniqueId(), message);
		}
		if (player2.isOnline()) {
			player2.sendMessage(Utils.integrateColor(message));
		} else {
			cwu.getConfig().addMessage(player2.getUniqueId(), message);
		}
		
	}
	
	public void start(Player player1, Player player2) {
		duels.get(player1).start();
	}
	
	public DuelRunnable getDuel(Player player) {
		DuelRunnable dr = duels.get(player);
		if (dr == null) {
			for (DuelRunnable duel : duels.values()) {
				if (duel.getPlayer2() == player) { 
					dr = duel;
					break;
				}
			}
		}
		return dr;
	}
	
	public void setDefaults() {
		//dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&a&lReady up!", null), 8);
	}
	
	public void setArmor(DuelRunnable dr, Material type) {
		switch (type) {
			case DIAMOND_CHESTPLATE:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&b&lDiamond armor", null), 0);
				break;
			case IRON_CHESTPLATE:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&f&lIron armor", null), 0);
				break;
			case GOLD_CHESTPLATE:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&6&lGold armor", null), 0);
				break;
			case LEATHER_CHESTPLATE:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&e&lLeather armor", null), 0);
				break;
			default:
				dr.getMenu().setSlot(ItemUtils.getItem(Material.STICK, 1, (short)0, "&c&lNo armor", null), 0);
				break;
			
		}
	}

	public void setWeapon(DuelRunnable dr, Material type) {
		switch (type) {
			case DIAMOND_SWORD:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&b&lDiamond sword", null), 1);
				break;
			case IRON_SWORD:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&f&lIron sword", null), 1);
				break;
			case STONE_SWORD:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&7&lStone sword", null), 1);
				break;
			case WOOD_SWORD:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&e&lWood sword", null), 1);
				break;
			default:
				dr.getMenu().setSlot(ItemUtils.getItem(Material.STICK, 1, (short)0, "&c&lNo sword", null), 1);
				break;
			
		}
	}
	
	public void setBow(DuelRunnable dr, boolean enabled) {
		if (enabled) {
			dr.getMenu().setSlot(ItemUtils.getItem(Material.BOW, 1, (short)0, "&a&lBow", null), 2);
		} else {
			dr.getMenu().setSlot(ItemUtils.getItem(Material.BOW, 1, (short)0, "&4&lNo Bow", null), 2);
		}
	}
	
	public void setPotions(DuelRunnable dr, boolean enabled) {
		if (enabled) {
			dr.getMenu().setSlot(ItemUtils.getItem(Material.POTION, 1, (short)0, "&a&lPotions", null), 3);
		} else {
			dr.getMenu().setSlot(ItemUtils.getItem(Material.GLASS_BOTTLE, 1, (short)0, "&4&lNo Potions", null), 3);
		}
	}
	
	
}
