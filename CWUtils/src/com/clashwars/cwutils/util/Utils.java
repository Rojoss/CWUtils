package com.clashwars.cwutils.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class Utils {
	//Integrate colors in a string
	public static String integrateColor(String str) {
		for (ChatColor c : ChatColor.values()) {
			str = str.replaceAll("&" + c.getChar() + "|&" + Character.toUpperCase(c.getChar()), c.toString());
		}
		return str;
	}
	
	public static String[] integrateColor(String[] str) {
		for (int i = 0; i < str.length; i++) {
			for (ChatColor c : ChatColor.values()) {
				str[i] = str[i].replaceAll("&" + c.getChar() + "|&" + Character.toUpperCase(c.getChar()), c.toString());
			}
		}
		return str;
	}
	
	public static void resetPlayer(Player player) {
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
	
	
}
