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
}
