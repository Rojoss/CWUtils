package com.clashwars.cwutils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.clashwars.cwutils.bukkit.CWUtilsPlugin;
import com.clashwars.cwutils.bukkit.events.CombatLogEvents;
import com.clashwars.cwutils.bukkit.events.DuelEvents;
import com.clashwars.cwutils.bukkit.events.ExpEvents;
import com.clashwars.cwutils.bukkit.events.FactionEvents;
import com.clashwars.cwutils.bukkit.events.LuckEvents;
import com.clashwars.cwutils.bukkit.events.ObsidDestroyEvents;
import com.clashwars.cwutils.bukkit.events.OtherEvents;
import com.clashwars.cwutils.commands.Commands;
import com.clashwars.cwutils.config.Config;
import com.clashwars.cwutils.config.PlayerBackupConfig;
import com.clashwars.cwutils.config.PluginConfig;
import com.clashwars.cwutils.runnables.DragonRunnable;
import com.clashwars.cwutils.util.Utils;
import com.earth2me.essentials.Essentials;

public class CWUtils {
	
	private static CWUtils instance;
	private CWUtilsPlugin cwu;
	private final Logger log = Logger.getLogger("Minecraft");
	private boolean enabled;
	
	private PluginConfig pluginConfig;
	private PlayerBackupConfig pbConfig;
	private Config cfg;
	
	private Essentials essentials;
	
	private Commands cmds;
	
	private TagManager tm;
	private DuelManager dm;
	
	public Luck luck;
	
	public Set<UUID> spawnerMobs = new HashSet<UUID>();
	public HashMap<String,HashMap<UUID,Long>> cooldowns = new HashMap<String,HashMap<UUID,Long>>();

	public CWUtils(CWUtilsPlugin cwu) {
		this.cwu = cwu;
		
	}

	public void log(Object msg) {
		log.info("[CWUtils " + getPlugin().getDescription().getVersion() + "]: " + msg.toString());
	}

	public void onDisable() {
		enabled = false;
		getServer().getScheduler().cancelTasks(this.getPlugin());
		pluginConfig.save();
		if (cfg.getStatus("endReset")) {
			deleteEnd();
		}
		log("Disabled.");
	}

	public void onEnable() {
		instance = this;
		
		cfg = new Config();
		pluginConfig = new PluginConfig(cfg);
		pluginConfig.init();
		pluginConfig.load();
		
		cmds = new Commands(this);
		cmds.populateCommands();
		
		registerEvents();
		addRecipes();
		
		if (cfg.getStatus("alts")) {
			Plugin essentialsPlugin = getServer().getPluginManager().getPlugin("Essentials");
			if (essentialsPlugin.isEnabled() && (essentialsPlugin instanceof Essentials)) {
				essentials = (Essentials) essentialsPlugin;
			} 
		}
		
		if (cfg.getStatus("tagging")) {
			tm = new TagManager(this);
		}
		
		if (cfg.getStatus("duels")) {
			pbConfig = new PlayerBackupConfig(cfg);
			pbConfig.init();
			dm = new DuelManager(this);
		}
		
		if (cfg.getStatus("luck")) {
			luck = new Luck();
		}
		
		new DragonRunnable(this).runTaskTimer(this.getPlugin(), 0, 20);
		
		enabled = true;
		log("Enabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return cmds.executeCommand(sender, label, args);
	}

	private void registerEvents() {
		PluginManager pm = getPlugin().getServer().getPluginManager();
		pm.registerEvents(new ObsidDestroyEvents(this), getPlugin());
		pm.registerEvents(new CombatLogEvents(this), getPlugin());
		if (cfg.getStatus("duels")) {
			pm.registerEvents(new DuelMenu.Events(), getPlugin());
			pm.registerEvents(new DuelEvents(this), getPlugin());
		}
		pm.registerEvents(new OtherEvents(this), getPlugin());
		pm.registerEvents(new FactionEvents(), getPlugin());
		if (cfg.getStatus("exp")) {
			pm.registerEvents(new ExpEvents(this), getPlugin());
		}
		if (cfg.getStatus("luck")) {
			pm.registerEvents(new LuckEvents(this), getPlugin());
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
		//Custom Spawnwer recipe.
		if (cfg.getStatus("createSpawners")) {
			ShapedRecipe spawner = new ShapedRecipe(new ItemStack(Material.MOB_SPAWNER, 1));
			spawner.shape("^*^", "^@^", "###");
			spawner.setIngredient('^', Material.DRAGON_EGG);
			spawner.setIngredient('*', Material.NETHER_STAR);
			spawner.setIngredient('@', Material.MONSTER_EGG);
			spawner.setIngredient('#', Material.OBSIDIAN);
			getServer().addRecipe(spawner);
		}
	}
	
	private void deleteEnd() {
		World end = getServer().getWorld("world_the_end");
		if (end == null) {
			return;
		}
		//Make sure there are no players in the end.
		if (end.getPlayers().size() > 0) {
			log("Failed at resetting the end!");
			return;
		}
		
		//Unload the world.
		getServer().unloadWorld(end, false);
		
		//Delete world.
		Utils.deleteDirectory(end.getWorldFolder());
		log("The end has been deleted!");
	}

	public CWUtilsPlugin getPlugin() {
		return cwu;
	}

	public Server getServer() {
		return getPlugin().getServer();
	}
	
	public boolean isEnabled() {
		return enabled;
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
	
	public Essentials getEssentials() {
		return essentials;
	}

	public static CWUtils getInstance() {
		return instance;
	}
}
