package com.clashwars.cwutils.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.clashwars.cwutils.PlayerBackup;

public class PlayerBackupConfig {
	private YamlConfiguration cfgFile;
	private ConfigUtil cu;
	private Config cfg;
	private final File dir = new File("plugins/CWUtils/Backups/");
	//private final File file = new File(dir + "/PlayerBackup.yml");

	public PlayerBackupConfig(Config cfg) {
		this.cfg = cfg;
	}

	public void init() {
		try {
			dir.mkdirs();
			//file.createNewFile();
			cfgFile = new YamlConfiguration();
			cu = new ConfigUtil(cfgFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load(UUID player) {
		File file = new File(dir, player.toString() + ".yml");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			
			cfgFile.load(file);
			
			PlayerBackup backup;
			if (!cfg.getBackups().containsKey(player)) {
				backup = new PlayerBackup(20, 20, 0, 0, 0, null, loadInv(makeUsable(cfgFile.getMapList("inventory")), new ItemStack[36]), null);
				cfg.addBackup(player, backup);
			} else {
				backup = cfg.getBackup(player);
			}
			backup.loadBackup(Bukkit.getServer().getPlayer(player));
			
			cfgFile.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void save(UUID player) {
		File file = new File(dir, player.toString() + ".yml");
		try {
			if (!cfg.getBackups().containsKey(player)) {
				PlayerBackup backup = new PlayerBackup(Bukkit.getServer().getPlayer(player));
				cfg.addBackup(player, backup);
			} else {
				PlayerBackup backup = cfg.getBackup(player);
				backup.updateFromPlayer(Bukkit.getServer().getPlayer(player));
			}
			cfgFile.set("inventory", cfg.getBackup(player).getSerializedInvContents());
			
			cfgFile.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public YamlConfiguration getCfgFile() {
		return cfgFile;
	}
	
	
	
	
	
	
	
	  private List<Map<String, Object>> makeUsable(List<Map<?, ?>> iets) {
	      List<Map<String, Object>> returnv = new ArrayList<Map<String, Object>>();
	      for (Map<?, ?> nu : iets) {
		      Set<?> keys = nu.keySet();
		      Map<String, Object> returnmap = new HashMap<String, Object>();
		      for (Iterator<?> localIterator2 = keys.iterator(); localIterator2.hasNext(); ) { Object key = localIterator2.next();
		          returnmap.put((String)key, nu.get(key));
		      }
		      returnv.add(returnmap);
	      }
	      return returnv;
	  }
	  
	  public ItemStack[] loadInv(List<Map<String, Object>> list, ItemStack[] inv) {
	      for (Map<String, Object> itemmap : list) {
	    	  ItemStack item = ItemStack.deserialize(itemmap);
	    	  inv[((Integer)itemmap.get("slot")).intValue()] = item;
	      }
	      return inv;
	  }
	
}
