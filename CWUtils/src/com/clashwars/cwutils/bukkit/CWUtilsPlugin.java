package com.clashwars.cwutils.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import com.clashwars.cwutils.CWUtils;

public class CWUtilsPlugin extends JavaPlugin {
	private CWUtils cwu;

	public void onDisable() {
		cwu.onDisable();
	}

	public void onEnable() {
		cwu = new CWUtils(this);
		cwu.onEnable();
	}

	public CWUtils getInstance() {
		return cwu;
	}
	
	/*
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player)sender;
		if (commandLabel.equalsIgnoreCase("duel")) {
			if (args.length >= 1) {
				if (Bukkit.getPlayer(args[0]) != null) {
					cwu.getDM().createDuel(player, Bukkit.getPlayer(args[0]));
				} else {
					player.sendMessage("Invalid player");
				}
			} else {
				player.sendMessage("Usage: /duel <player>");
			}
			//cwu.getPBConfig().save(player.getUniqueId());
		}
		if (commandLabel.equalsIgnoreCase("loadInv")) {
			cwu.getPBConfig().load(player.getUniqueId());
		}
		
		return false;
	}
	*/
}
