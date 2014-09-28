package com.clashwars.cwutils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import com.clashwars.cwutils.config.TipConfig;
import com.clashwars.cwutils.util.ItemUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

import com.clashwars.cwutils.events.CombatLogEvents;
import com.clashwars.cwutils.events.DuelEvents;
import com.clashwars.cwutils.events.ExpEvents;
import com.clashwars.cwutils.events.FactionEvents;
import com.clashwars.cwutils.events.LuckEvents;
import com.clashwars.cwutils.events.PluginMessageEvents;
import com.clashwars.cwutils.events.ObsidDestroyEvents;
import com.clashwars.cwutils.events.OtherEvents;
import com.clashwars.cwutils.commands.Commands;
import com.clashwars.cwutils.config.Config;
import com.clashwars.cwutils.config.PlayerBackupConfig;
import com.clashwars.cwutils.config.PluginConfig;
import com.clashwars.cwutils.runnables.DragonRunnable;
import com.clashwars.cwutils.sql.MySql;
import com.clashwars.cwutils.sql.SqlInfo;
import com.clashwars.cwutils.util.Utils;
import com.earth2me.essentials.Essentials;

public class CWUtils extends JavaPlugin {
	
	private static CWUtils instance;
	private final Logger log = Logger.getLogger("Minecraft");
	private boolean enabled;
	
	private PluginConfig pluginConfig;
	private PlayerBackupConfig pbConfig;
	private Config cfg;
    private TipConfig tipCfg;
	
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

    public Set<String> eventDataRequests = new HashSet<String>();
	
	public final Map<HumanEntity, ItemStack[]> containers = new HashMap<HumanEntity, ItemStack[]>();


	public void log(Object msg) {
		log.info("[CWUtils " + getDescription().getVersion() + "]: " + msg.toString());
	}

	public void onDisable() {
		enabled = false;
		getServer().getScheduler().cancelTasks(this);
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

        tipCfg = new TipConfig("plugins/CWUtils/tips.yml");
        tipCfg.load();
		
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
		
		new DragonRunnable(this).runTaskTimer(this, 0, 20);
		
		SqlInfo sqli = cfg.getSqlInfo();
		sql = new MySql(this, sqli.getAddress(), sqli.getPort(), sqli.getDb(), sqli.getUser(), sqli.getPass());
		if (sql == null) {
			log("Can't conntact to database!");
			getPluginLoader().disablePlugin(this);
			return;
		}
		c = sql.openConnection();
		if (c == null) {
			log("Can't conntact to database!");
			getPluginLoader().disablePlugin(this);
			return;
		}
		
		enabled = true;
		log("Enabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return cmds.executeCommand(sender, label, args);
	}

	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ObsidDestroyEvents(this), this);
		pm.registerEvents(new CombatLogEvents(this), this);
		if (cfg.getStatus("duels")) {
			pm.registerEvents(new DuelMenu.Events(), this);
			pm.registerEvents(new DuelEvents(this), this);
		}
		pm.registerEvents(new OtherEvents(this), this);
		pm.registerEvents(new FactionEvents(), this);
		if (cfg.getStatus("exp")) {
			pm.registerEvents(new ExpEvents(this), this);
		}
		if (cfg.getStatus("luck")) {
			pm.registerEvents(new LuckEvents(this), this);
		}
	}
	
	private void registerChannels() {
		Messenger msg = getServer().getMessenger();

		msg.registerIncomingPluginChannel(this, "CWBungee", new PluginMessageEvents(this));
		msg.registerOutgoingPluginChannel(this, "CWBungee");
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
		//Custom Spawnwer recipes.
		if (cfg.getStatus("createSpawners")) {
            addSpawnerRecipe(50, "Creeper");
            addSpawnerRecipe(51, "Skeleton");
            addSpawnerRecipe(52, "Spider");
            addSpawnerRecipe(59, "CaveSpider");
            addSpawnerRecipe(54, "Zombie");
            addSpawnerRecipe(58, "Enderman");
            addSpawnerRecipe(90, "Pig");
            addSpawnerRecipe(92, "Cow");
            addSpawnerRecipe(91, "Sheep");
            addSpawnerRecipe(93, "Chicken");
		}
	}

    private void addSpawnerRecipe(int eggID, String entityName) {
        ShapedRecipe spawner = new ShapedRecipe(getSpawnerItem(entityName));
        spawner.shape("^*^", "^@^", "###");
        spawner.setIngredient('^', Material.DRAGON_EGG);
        spawner.setIngredient('*', Material.NETHER_STAR);
        spawner.setIngredient('@', Material.MONSTER_EGG, eggID);
        spawner.setIngredient('#', Material.OBSIDIAN);
        getServer().addRecipe(spawner);
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

	public static CWUtils inst() {
		return instance;
	}
	
	public Config getCfg() {
		return cfg;
	}
	
	public PlayerBackupConfig getPBConfig() {
		return pbConfig;
	}

    public TipConfig getTipCfg() {
        return tipCfg;
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

    public ItemStack getSpawnerItem(String entityName) {
        return ItemUtils.getItem(Material.MOB_SPAWNER, 1, (short)0, "&5&l" + entityName + " &6&lSpawner!", new String[] {
                "&e&l" + entityName, "&7When placed it will spawn &8" + entityName, "&7You can break and move the spawner for a limited time.", "&cWhen the server restarts you &4can't move &cit anymore!",
                "&7You also can't ask the staff to move the spawner.", "&7So make sure you place it in the right place!", "&7Till a restart you can pick it back up with any pickaxe."});
    }

	public void sendEventInfo(String senderStr, String event, String arena, String players, String slots, String status) {
        CommandSender sender = null;
		OfflinePlayer player = getServer().getOfflinePlayer(senderStr);
		if (player != null && player.isOnline()) {
			sender = (CommandSender)player;
		} else if (senderStr.equalsIgnoreCase("console")) {
			sender = getServer().getConsoleSender();
		}
		if (sender != null) {
            if (eventDataRequests.contains(sender.getName())) {
                eventDataRequests.remove(sender.getName());
            }
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
