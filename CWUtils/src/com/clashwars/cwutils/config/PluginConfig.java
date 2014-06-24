package com.clashwars.cwutils.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

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
			
			//Section enabling/disabling
			cfg.setStatus("tagging", cu.getBoolean("setEnabled.tagging", true));
			cfg.setStatus("enderchestRecipe", cu.getBoolean("setEnabled.enderchestRecipe", true));
			cfg.setStatus("enderchestBlock", cu.getBoolean("setEnabled.enderchestBlock", true));
			cfg.setStatus("blackpowder", cu.getBoolean("setEnabled.blackpowder", true));
			cfg.setStatus("c4", cu.getBoolean("setEnabled.c4", true));
			cfg.setStatus("destroyableObsidian", cu.getBoolean("setEnabled.destroyableObsidian", true));
			cfg.setStatus("duels", cu.getBoolean("setEnabled.duels", false));
			
			// Combat logging
			cfg.setTagTime(cu.getInt("combatLog.tagTime", 30));
			cfg.setBlockedCmds(cu.getStringList("combatLog.blockedCmds", new String[] { "EXAMPLE_CMD", "/EXAMPLE_CMD_2 ARG1" }));
			
			//Duel
			
			
			
			//Queued messages
			Map<UUID, List<String>> messages = new HashMap<UUID, List<String>>();
			ConfigurationSection cfgSec = cfgFile.getConfigurationSection("queuedMessages");
			if (cfgSec != null) {
				for (String playerUUID : cfgSec.getKeys(false)) {
					 messages.put(UUID.fromString(playerUUID),cu.getStringList("queuedMessages." + playerUUID, new String[] { }));
				}
			}
			cfg.setMessages(messages);
			
			cfgFile.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			cfgFile.set("queuedMessages", null);
			for (UUID playerUUID : cfg.getMessages().keySet()) {
				cfgFile.set("queuedMessages." + playerUUID.toString(), cfg.getMessages(playerUUID));
			}
			cfgFile.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
