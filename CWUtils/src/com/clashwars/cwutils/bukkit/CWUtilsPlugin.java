package com.clashwars.cwutils.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player player = (Player)sender;
		if (commandLabel.equalsIgnoreCase("saveInv")) {
			cwu.getPBConfig().save(player.getUniqueId());
		}
		if (commandLabel.equalsIgnoreCase("loadInv")) {
			cwu.getPBConfig().load(player.getUniqueId());
		}
		
		return false;
	}
	
	
	
}
