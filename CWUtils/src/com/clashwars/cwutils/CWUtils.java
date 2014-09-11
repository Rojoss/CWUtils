package com.clashwars.cwutils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.messaging.Messenger;

import com.clashwars.cwutils.bukkit.CWUtilsPlugin;
import com.clashwars.cwutils.bukkit.events.CombatLogEvents;
import com.clashwars.cwutils.bukkit.events.DuelEvents;
import com.clashwars.cwutils.bukkit.events.ExpEvents;
import com.clashwars.cwutils.bukkit.events.FactionEvents;
import com.clashwars.cwutils.bukkit.events.LuckEvents;
import com.clashwars.cwutils.bukkit.events.MessageEvents;
import com.clashwars.cwutils.bukkit.events.ObsidDestroyEvents;
import com.clashwars.cwutils.bukkit.events.OtherEvents;
import com.clashwars.cwutils.commands.Commands;
import com.clashwars.cwutils.config.Config;
import com.clashwars.cwutils.config.PlayerBackupConfig;
import com.clashwars.cwutils.config.PluginConfig;
import com.clashwars.cwutils.runnables.DragonRunnable;
import com.clashwars.cwutils.sql.MySql;
import com.clashwars.cwutils.sql.SqlInfo;
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
	
	private MySql sql = null;
	private Connection c = null;
	
	private Essentials essentials;
	
	private Commands cmds;
	
	private TagManager tm;
	private DuelManager dm;
	private QueueManager qm;
	
	public Luck luck;
	
	public Set<UUID> spawnerMobs = new HashSet<UUID>();
	public HashMap<String,HashMap<UUID,Long>> cooldowns = new HashMap<String,HashMap<UUID,Long>>();
	
	public final Map<HumanEntity, ItemStack[]> containers = new HashMap<HumanEntity, ItemStack[]>();

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
		try {
			c.close();
			sql.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		registerChannels();
		addRecipes();
		
		qm = new QueueManager(this);
		
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
		
		SqlInfo sqli = cfg.getSqlInfo();
		sql = new MySql(this, sqli.getAddress(), sqli.getPort(), sqli.getDb(), sqli.getUser(), sqli.getPass());
		if (sql == null) {
			log("Can't conntact to database!");
			getPlugin().getPluginLoader().disablePlugin(getPlugin());
			return;
		}
		c = sql.openConnection();
		if (c == null) {
			log("Can't conntact to database!");
			getPlugin().getPluginLoader().disablePlugin(getPlugin());
			return;
		}
		
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
	
	private void registerChannels() {
		Messenger msg = getPlugin().getServer().getMessenger();

		msg.registerIncomingPluginChannel(getPlugin(), "CWBungee", new MessageEvents(this));
		msg.registerOutgoingPluginChannel(getPlugin(), "CWBungee");
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
	
	public Connection getSql() {
		try {
			if (c == null || c.isClosed()) {
				c = sql.openConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return c;
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
	
	public QueueManager getQM() {
		return qm;
	}

	public static CWUtils getInstance() {
		return instance;
	}

	public void sendEventInfo(String senderStr, String event, String arena, int players, int slots, String status) {
		CommandSender sender = null;
		OfflinePlayer player = getServer().getOfflinePlayer(senderStr);
		if (player != null && player.isOnline()) {
			sender = (CommandSender)player;
		} else if (senderStr.equalsIgnoreCase("console")) {
			sender = getServer().getConsoleSender();
		}
		if (sender != null) {
			sender.sendMessage(Utils.integrateColor("&8======== &4&lEvent Information &8========"));
			if (event.equalsIgnoreCase("none") || arena.equalsIgnoreCase("none")) {
				sender.sendMessage(Utils.integrateColor("&cThere is currently no event active."));
			} else {
				sender.sendMessage(Utils.integrateColor("&6Event&8: &5" + event));
				sender.sendMessage(Utils.integrateColor("&6Arena&8: &5" + arena));
				sender.sendMessage(Utils.integrateColor("&6Slots&8: &a" + players + "&7/&2" + slots));
				sender.sendMessage(Utils.integrateColor("&6Status&8: &5" + status));
				sender.sendMessage(Utils.integrateColor("&7Use &9/event join &7to join the events server!"));
			}
		}
	}
}
