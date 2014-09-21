package com.clashwars.cwutils.config;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

import com.clashwars.cwutils.sql.SqlInfo;

public class PluginConfig {
	private YamlConfiguration cfgFile;
	private ConfigUtil cu;
	private Config cfg;
	private final File dir = new File("plugins/CWUtils/");
	private final File file = new File(dir + "/CWUtils.yml");

	public PluginConfig(Config cfg) {
		this.cfg = cfg;
	}

	public void init() {
		try {
			dir.mkdirs();
			file.createNewFile();
			cfgFile = new YamlConfiguration();
			cu = new ConfigUtil(cfgFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load() {
		try {
			cfgFile.load(file);
			
			//Sql
			String address = cu.getString("sql.address", "localhost");
			String port = cu.getString("sql.port", "3306");
			String user = cu.getString("sql.username", "root");
			String password = cu.getString("sql.password", "");
			String database = cu.getString("sql.database", "cwclasses");
			cfg.setSqlInfo(new SqlInfo(address, port, user, password, database));
			
			//Section enabling/disabling
			cfg.setStatus("tagging", cu.getBoolean("setEnabled.tagging", true));
			cfg.setStatus("enderchestRecipe", cu.getBoolean("setEnabled.enderchestRecipe", true));
			cfg.setStatus("enderchestBlock", cu.getBoolean("setEnabled.enderchestBlock", true));
			cfg.setStatus("blackpowder", cu.getBoolean("setEnabled.blackpowder", true));
			cfg.setStatus("c4", cu.getBoolean("setEnabled.c4", true));
			cfg.setStatus("destroyableObsidian", cu.getBoolean("setEnabled.destroyableObsidian", true));
			cfg.setStatus("duels", cu.getBoolean("setEnabled.duels", false));
			cfg.setStatus("exp", cu.getBoolean("setEnabled.exp", true));
			cfg.setStatus("endReset", cu.getBoolean("setEnabled.endReset", true));
			cfg.setStatus("luck", cu.getBoolean("setEnabled.luck", true));
			cfg.setStatus("alts", cu.getBoolean("setEnabled.alts", true));
			cfg.setStatus("createSpawners", cu.getBoolean("setEnabled.createSpawners", true));
			
			// Combat logging
			cfg.setTagTime(cu.getInt("combatLog.tagTime", 30));
			cfg.setBlockedCmds(cu.getStringList("combatLog.blockedCmds", new String[] { "EXAMPLE_CMD", "/EXAMPLE_CMD_2 ARG1" }));
			
			//Duel
			
			cfgFile.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			cfgFile.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
