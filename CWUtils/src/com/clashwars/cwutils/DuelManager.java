package com.clashwars.cwutils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.clashwars.cwutils.runnables.DuelRunnable;
import com.clashwars.cwutils.util.ItemUtils;
import com.clashwars.cwutils.util.Utils;

public class DuelManager {
	
	/*
	TODO:
	- Save everything else in PlayerBackup not just inventory.
	- On player reset clear armor slots aswell.
	- Cancel start scheduler somehow.
	- Allow putting items in the menu.
	- Fix the money take/add buttons.
	- Take input items/money from player that lost and give it to the winner.
	- Don't drop items when a player is killed.
	- Fix players not getting reset when someone is killed.
	- Fix where it cancels like 10 times sometimes.
	- Create the /duel command and check for distance etc.
	- Save player location + world and teleport players back to original location.
	- Make sure players can't duel at places where there is no pvp.
	- Make it so players who are dueling can't be attacked.
	- Make it so dueling players can't attack other players.
	- Make it so you don't lose faction power when dueling
	- Make sure no duel items can be glitched out.
	- Make sure player can't take duel items by logging out.
	*/
	
	private CWUtils cwu;
	private Map<Player, DuelRunnable> duels = new HashMap<Player, DuelRunnable>();
	
	public DuelManager(CWUtils cwu) {
		this.cwu = cwu;
	}
	
	//Create a new duel between 2 players.
	public void createDuel(Player player1, Player player2) {
		DuelMenu menu = new DuelMenu(this, player1.getName() + player2.getName(), 54, "&4&lDuel setup");
		
		DuelRunnable dr = null;
		if (!duels.containsKey(player1)) {
			dr = new DuelRunnable(cwu, menu, player1, player2);
			duels.put(player1, dr);
		} else {
			dr = getDuel(player1);
		}
		
		//TODO: set these to saved defaults.
		setDefaults(dr);
		setReady(dr);
		setCoins(dr);
		setArmor(dr, Material.DIAMOND_CHESTPLATE);
		setWeapon(dr, Material.DIAMOND_SWORD);
		setBow(dr, true);
		setPotions(dr, true);
		
		menu.show(player1);
		menu.show(player2);
	}

	public void stop(Player player1, Player player2, Player winner) {
		//Get the ending message.
		String message = "&8[&4CW Duel&8] &cThe time has ran out! Nobody wins.";
		if (winner == player1) {
			message = "&8[&4CW Duel&8] &a&l" + player1.getName() + " &6wins by killing " + player2.getName();
		} else {
			message = "&8[&4CW Duel&8] &a&l" + player2.getName() + " &6wins by killing " + player1.getName();
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
		duels.remove(player1);
		player1.closeInventory();
		player2.closeInventory();
		
		Utils.resetPlayer(player1);
		Utils.resetPlayer(player2);

		cwu.getPBConfig().load(player1.getUniqueId());
		cwu.getPBConfig().load(player2.getUniqueId());
	}
	
	public void cancelSetup(Player player1, Player player2) {
		player1.sendMessage(Utils.integrateColor("&8[&4CW Duel&8] &cThe duel has been cancelled."));
		player2.sendMessage(Utils.integrateColor("&8[&4CW Duel&8] &cThe duel has been cancelled."));
		duels.remove(player1);
		player1.closeInventory();
		player2.closeInventory();
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
	
	public void setDefaults(DuelRunnable dr) {
		dr.getMenu().setSlot(ItemUtils.getItem(Material.STAINED_GLASS_PANE, 1, (short)15, "", null), 4);
		for (int i = 9; i < 18; i++) {
			dr.getMenu().setSlot(ItemUtils.getItem(Material.STAINED_GLASS_PANE, 1, (short)15, "", null), i);
		}
	}
	
	public void setReady(DuelRunnable dr) {
		if (dr.isPlayer1Ready()) {
			dr.getMenu().setSlot(ItemUtils.getItem(Material.INK_SACK, 1, (short)10, "&a&lReady!", new String[] {"&7Wait for the other player to ready up."}), 3);
		} else {
			dr.getMenu().setSlot(ItemUtils.getItem(Material.INK_SACK, 1, (short)8, "&c&lNot ready!", new String[] {"&7Click when you're ready!"}), 3);
		}
		if (dr.isPlayer2Ready()) {
			dr.getMenu().setSlot(ItemUtils.getItem(Material.INK_SACK, 1, (short)10, "&a&lReady!", new String[] {"&7Wait for the other player to ready up."}), 5);
		} else {
			dr.getMenu().setSlot(ItemUtils.getItem(Material.INK_SACK, 1, (short)8, "&c&lNot ready!", new String[] {"&7Click when you're ready!"}), 5);
		}
		
		if (dr.isPlayer1Ready() && dr.isPlayer2Ready()) {
			start(dr.getPlayer1(), dr.getPlayer2());
		}
	}
	
	public void setCoins(DuelRunnable dr) {
		dr.getMenu().setSlot(ItemUtils.getItem(Material.ENDER_CHEST, 1, (short)0, "&6&lCoins&7: &e&l" + dr.getPlayer1Coins(), null), 0);
		dr.getMenu().setSlot(ItemUtils.getItem(175, 1, (short)0, "&6&l+100 &e&lcoin", new String[] {"&7Add 100 coins to your input."}), 1);
		dr.getMenu().setSlot(ItemUtils.getItem(175, 1, (short)0, "&4&l-100 &c&lcoins", new String[] {"&7Subtract 100 coins from your input."}), 2);
		
		dr.getMenu().setSlot(ItemUtils.getItem(175, 1, (short)0, "&4&l-100 &c&lcoins", new String[] {"&7Subtract 100 coins from your input."}), 6);
		dr.getMenu().setSlot(ItemUtils.getItem(175, 1, (short)0, "&6&l+100 &e&lcoin", new String[] {"&7Add 100 coins to your input."}), 7);
		dr.getMenu().setSlot(ItemUtils.getItem(Material.ENDER_CHEST, 1, (short)0, "&6&lCoins&7: &e&l" + dr.getPlayer2Coins(), null), 8);
	}
	
	public void setArmor(DuelRunnable dr, Material type) {
		switch (type) {
			case DIAMOND_CHESTPLATE:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&b&lDiamond armor", null), 22);
				dr.setArmor(type);
				break;
			case IRON_CHESTPLATE:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&f&lIron armor", null), 22);
				dr.setArmor(type);
				break;
			case GOLD_CHESTPLATE:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&6&lGold armor", null), 22);
				dr.setArmor(type);
				break;
			case LEATHER_CHESTPLATE:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&e&lLeather armor", null), 22);
				dr.setArmor(type);
				break;
			default:
				dr.getMenu().setSlot(ItemUtils.getItem(Material.STICK, 1, (short)0, "&c&lNo armor", null), 22);
				dr.setArmor(Material.STICK);
				break;
			
		}
	}

	public void setWeapon(DuelRunnable dr, Material type) {
		switch (type) {
			case DIAMOND_SWORD:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&b&lDiamond sword", null), 31);
				dr.setWeapon(type);
				break;
			case IRON_SWORD:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&f&lIron sword", null), 31);
				dr.setWeapon(type);
				break;
			case STONE_SWORD:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&7&lStone sword", null), 31);
				dr.setWeapon(type);
				break;
			case WOOD_SWORD:
				dr.getMenu().setSlot(ItemUtils.getItem(type, 1, (short)0, "&e&lWood sword", null), 31);
				dr.setWeapon(type);
				break;
			default:
				dr.getMenu().setSlot(ItemUtils.getItem(Material.STICK, 1, (short)0, "&c&lNo sword", null), 31);
				dr.setWeapon(Material.STICK);
				break;
			
		}
	}
	
	public void setBow(DuelRunnable dr, boolean enabled) {
		dr.setBow(enabled);
		if (enabled) {
			dr.getMenu().setSlot(ItemUtils.getItem(Material.BOW, 1, (short)0, "&a&lBow", null), 40);
		} else {
			dr.getMenu().setSlot(ItemUtils.getItem(Material.BOW, 1, (short)0, "&4&lNo Bow", null), 40);
		}
	}
	
	public void setPotions(DuelRunnable dr, boolean enabled) {
		dr.setPotions(enabled);
		if (enabled) {
			dr.getMenu().setSlot(ItemUtils.getItem(Material.POTION, 1, (short)0, "&a&lPotions", null), 49);
		} else {
			dr.getMenu().setSlot(ItemUtils.getItem(Material.GLASS_BOTTLE, 1, (short)0, "&4&lNo Potions", null), 49);
		}
	}
	
}
