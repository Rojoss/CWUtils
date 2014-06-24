package com.clashwars.cwutils;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;

import com.clashwars.cwutils.bukkit.CWUtilsPlugin;
import com.clashwars.cwutils.bukkit.events.CombatLogEvents;
import com.clashwars.cwutils.bukkit.events.DuelEvents;
import com.clashwars.cwutils.bukkit.events.ObsidDestroyEvents;
import com.clashwars.cwutils.config.Config;
import com.clashwars.cwutils.config.PlayerBackupConfig;
import com.clashwars.cwutils.config.PluginConfig;

public class CWUtils {
	
	private CWUtilsPlugin cwu;
	private final Logger log = Logger.getLogger("Minecraft");
	
	private PluginConfig pluginConfig;
	private PlayerBackupConfig pbConfig;
	private Config cfg;
	private TagManager tm;
	private DuelManager dm;

	public CWUtils(CWUtilsPlugin cwu) {
		this.cwu = cwu;
	}

	public void log(Object msg) {
		log.info("[CWUtils " + getPlugin().getDescription().getVersion() + "]: " + msg.toString());
	}

	public void onDisable() {
		pluginConfig.save();
		log("Disabled.");
	}

	public void onEnable() {
		cfg = new Config();
		pluginConfig = new PluginConfig(cfg);
		pluginConfig.init();
		pluginConfig.load();
		
		registerEvents();
		addRecipes();
		
		if (cfg.getStatus("tagging")) {
			tm = new TagManager(this);
		}
		
		if (cfg.getStatus("duels")) {
			pbConfig = new PlayerBackupConfig(cfg);
			pbConfig.init();
			dm = new DuelManager(this);
		}
		
		log("Enabled.");
	}

	private void registerEvents() {
		PluginManager pm = getPlugin().getServer().getPluginManager();
		pm.registerEvents(new ObsidDestroyEvents(this), getPlugin());
		pm.registerEvents(new CombatLogEvents(this), getPlugin());
		if (cfg.getStatus("duels")) {
			pm.registerEvents(new DuelMenu.Events(), getPlugin());
			pm.registerEvents(new DuelEvents(this), getPlugin());
		}
	}
	
	private void addRecipes() {
		//Custom enderchest recipe.
		if (cfg.getStatus("enderchestRecipe")) {
			ShapedRecipe echest = new ShapedRecipe(new ItemStack(Material.ENDER_CHEST, 1));
			echest.shape("$&$", "#@#", "###");
			echest.setIngredient('$', Material.DIAMOND_BLOCK);
			echest.setIngredient('&', Material.NETHER_STAR);
			echest.setIngredient('@', Material.EYE_OF_ENDER);
			echest.setIngredient('#', Material.OBSIDIAN);
			getServer().addRecipe(echest);
		}
	}

	public CWUtilsPlugin getPlugin() {
		return cwu;
	}

	public Server getServer() {
		return getPlugin().getServer();
	}
	
	public Config getConfig() {
		return cfg;
	}
	
	public PlayerBackupConfig getPBConfig() {
		return pbConfig;
	}
	
	public TagManager getTM() {
		return tm;
	}

	public DuelManager getDM() {
		return dm;
	}
}
