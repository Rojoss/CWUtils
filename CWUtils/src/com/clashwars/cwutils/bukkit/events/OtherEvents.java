package com.clashwars.cwutils.bukkit.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.clashwars.cwutils.CWUtils;
import com.clashwars.cwutils.runnables.EnchantsRunnable;
import com.clashwars.cwutils.util.ExpUtils;
import com.clashwars.cwutils.util.ParticleEffect;
import com.clashwars.cwutils.util.Utils;

public class OtherEvents implements Listener {
	
	private CWUtils cwu;
	Random random = new Random();
	
	public OtherEvents(CWUtils cwu) {
		this.cwu = cwu;
	}
	
	//Get queued messages and commands on login and execute them
	@EventHandler
	public void login(final PlayerLoginEvent event) {
		cwu.getServer().getScheduler().scheduleSyncDelayedTask(cwu.getPlugin(), new Runnable() {
		    public void run() {
				Player player = event.getPlayer();
				try {
					Statement statement = cwu.getSql().createStatement();
					ResultSet res = statement.executeQuery("SELECT * FROM Queue WHERE UUID='" + player.getUniqueId().toString() + "';");
					while (res.next()) {
						if (cwu.getQM().execute(player, res.getString("Type"), res.getString("content"), false)) {
							try {
								Statement statement2 = cwu.getSql().createStatement();
								if (statement2.executeUpdate("DELETE FROM Queue WHERE ID='" + res.getInt("ID") + "';") < 1) {
									cwu.log("Error deleting queue entry! ID: " + res.getInt("ID") + " Player: " + player.getName() + " UUID: " + player.getUniqueId());
									return;
								}
							} catch (SQLException e) {
								cwu.log("Error deleting queue entry! ID: " + res.getInt("ID") + " Player: " + player.getName() + " UUID: " + player.getUniqueId());
								e.printStackTrace();
								return;
							}
						}
					}
				} catch (SQLException e) {
					player.sendMessage(Utils.integrateColor("&8[&4CW&8] &cError connecting to the databse. Can't load queue."));
					e.printStackTrace();
				}
		    }
		}, 20L);
	}
	
	//Only allow player entities through portals
	@EventHandler
	public void portalEnter(EntityPortalEnterEvent event) {
		if (event.getLocation().getWorld().getEnvironment() == Environment.THE_END) {
			Entity entity = event.getEntity();
			if (entity.getType() != EntityType.PLAYER) {
				entity.remove();
			}
		}
	}

	//Nerf golden apples by changing regen 5 to regen 3.
	@EventHandler
	public void Consume(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		final Player player = event.getPlayer();
		if (item.getType() == Material.GOLDEN_APPLE) {
			cwu.getServer().getScheduler().scheduleSyncDelayedTask(cwu.getPlugin(), new Runnable() {
				public void run() {
					if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE) && player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE) && player.hasPotionEffect(PotionEffectType.REGENERATION)) {
						player.removePotionEffect(PotionEffectType.REGENERATION);
						player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 600, 2));
					}
				}
			}, 10L);
		}
	}
	
	//Prevent wither creation in overworld && Spawn mobs inside border only.
	@EventHandler
	public void WitherSpawnEvent(CreatureSpawnEvent event) {
		//Prevent wither creation
		Entity e = event.getEntity();
		if (e.getType() == EntityType.WITHER && e.getWorld().getEnvironment() == Environment.NORMAL) {
			event.setCancelled(true);
			return;
		}
		
		//Only spawn mobs inside border
		Location loc = e.getLocation();
		int x = loc.getBlockX();
		int z = loc.getBlockZ();
		if (x > 5000 || x < -5000 || z > 5000 || z < -5000) {
			e.remove();
			return;
		}
	}
	
	
	//Nerf gold dropped from pigman.
	@EventHandler
	public void EntityDeath(EntityDeathEvent event) {
		//Nerf pigman
		if (event.getEntity().getType() != EntityType.PIG_ZOMBIE) {
			return;
		}
		for (ItemStack drop : event.getDrops()) {
			if (drop.getType() == Material.GOLD_NUGGET) {
				float chance = random.nextFloat();
				if (chance <= 0.33f) {
					drop.setAmount(1);
				} else {
					drop.setAmount(0);
				}
			}
			if (drop.getType() == Material.GOLD_INGOT) {
				float chance = random.nextFloat();
				if (chance <= 0.1f) {
					drop.setAmount(1);
				} else {
					drop.setAmount(0);
				}
			}
		}
	}
	
	//Sign interaction && Enchanted books
	@EventHandler
	public void Interact(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		
		
		if (player.getItemInHand().getType() == Material.BOOKSHELF) {
			ItemStack item = player.getItemInHand();
			if (!item.hasItemMeta()) {
				return;
			}
			ItemMeta meta = item.getItemMeta();
			if (!meta.hasDisplayName()) {
				return;
			}
			if (Utils.removeColour(meta.getDisplayName()).equalsIgnoreCase("&6&lBookshelf with &5&l5 enchanted books&6&l!")) {
				event.setCancelled(true);
				player.sendMessage(Utils.integrateColor("&8[&4CW&8] &6Emptying out the bookshelf..."));
				new EnchantsRunnable(cwu, player, player.getInventory().getHeldItemSlot()).runTaskTimer(cwu.getPlugin(), 0, 20);
				return;
			}
			return;
		}
		
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Block block = event.getClickedBlock();
		if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
			Sign sign = (Sign) block.getState();
			
			String locationString = sign.getLocation().getBlockX() + "," + sign.getLocation().getBlockY() + "," + sign.getLocation().getBlockZ();
			
			//Reward sign.
			if (Utils.removeColour(sign.getLine(0)).equalsIgnoreCase("&8[&4REWARD&8]")) {
				//Create map for the sign if it doesn't exist yet.
				HashMap<UUID,Long> cdPlayers = cwu.cooldowns.get(locationString);
				if (cdPlayers == null) {
					cdPlayers = new HashMap<UUID, Long>();
				}
				
				//Check if player is in the map.
				if (cdPlayers.containsKey(player.getUniqueId())) {
					Long cdTime = cdPlayers.get(player.getUniqueId());
					//Check if cooldown ran out or not.
					if (cdTime - System.currentTimeMillis() > 0) {
						player.sendMessage(Utils.integrateColor("&8[&4CW&8] &cThis sign is on cooldown for another " + ((cdTime - System.currentTimeMillis()) / 1000) + " seconds."));
						return;
					} else {
						cdPlayers.put(player.getUniqueId(), System.currentTimeMillis() + ((12 * 60) * 60) * 1000);
					}
				} else {
					//Create new cooldown for the player.
					cdPlayers.put(player.getUniqueId(), System.currentTimeMillis() + ((12 * 60) * 60) * 1000);
				}
				cwu.cooldowns.put(locationString, cdPlayers);
				
				
				
				String[] split = Utils.stripAllColour(sign.getLine(1)).trim().split(" ");
				if (split.length <= 1) {
					player.sendMessage(Utils.integrateColor("&8[&4CW&8] &cThis reward sign is not setup properly."));
					return;
				}
				//Get amount and type from the splited string at line2.
				int amount = Integer.parseInt(split[0]);
				String type = split[1];
				switch (type) {
					//Give experience reward.
					case "xp":
						ExpUtils xpu = new ExpUtils(player);
						xpu.setExp(xpu.getCurrentExp() + amount);
						player.sendMessage(Utils.integrateColor("&8[&4CW&8] &6You have been rewarded &5" + amount + " experience&6!"));
						break;
					default:
						player.sendMessage(Utils.integrateColor("&8[&4CW&8] &cThis reward sign is not setup properly."));
						break;
				}
			}
		}
	}
	
	
	
	//Particles when hit for VIP
	@EventHandler
	public void PlayerHitEvent(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof LargeFireball) {
			if (event.getEntity() instanceof EnderDragon) {
				event.setDamage(0);
				event.setCancelled(true);
				return;
			}
		}
		
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		if (player.isOp()) {
			ParticleEffect.displayBlockDust(player.getLocation().add(0, 0.5f, 0), 152, (byte) 0, 0.1f, 1.0f, 0.1f, 0.05f, 15);
    		return;
    	}
		if (player.hasPermission("vip.particle.diamond")) {
			ParticleEffect.displayBlockDust(player.getLocation().add(0, 0.5f, 0), 57, (byte) 0, 0.1f, 1.0f, 0.1f, 0.05f, 15);
		}
		if (player.hasPermission("vip.particle.gold")) {
			ParticleEffect.displayBlockDust(player.getLocation().add(0, 0.5f, 0), 41, (byte) 0, 0.1f, 1.0f, 0.1f, 0.05f, 15);
		}
		if (player.hasPermission("vip.particle.iron")) {
			ParticleEffect.displayBlockDust(player.getLocation().add(0, 0.5f, 0), 42, (byte) 0, 0.1f, 1.0f, 0.1f, 0.05f, 15);
		}
	}
	
	//Particle when respawn for VIP
	@EventHandler
	public void Respawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		cwu.getServer().getScheduler().scheduleSyncDelayedTask(cwu.getPlugin(), new Runnable() {
		    public void run() {
		    	if (player.isOp()) {
					ParticleEffect.displayBlockDust(player.getLocation(), 152, (byte) 0, 0.8f, 1.5f, 0.8f, 0.05f, 75);
		    		return;
		    	}
		    	if (player.hasPermission("vip.particle.diamond")) {
					ParticleEffect.displayBlockDust(player.getLocation(), 57, (byte) 0, 0.8f, 1.5f, 0.8f, 0.05f, 75);
				}
				if (player.hasPermission("vip.particle.gold")) {
					ParticleEffect.displayBlockDust(player.getLocation(), 41, (byte) 0, 0.8f, 1.5f, 0.8f, 0.05f, 75);
				}
				if (player.hasPermission("vip.particle.iron")) {
					ParticleEffect.displayBlockDust(player.getLocation(), 42, (byte) 0, 0.8f, 1.5f, 0.8f, 0.05f, 75);
				}
		    }
		}, 40L);
	}
	
	//Particle on death for VIP && Head collection for VIP
	@EventHandler
	public void Death(PlayerDeathEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		final Player player = (Player)event.getEntity();
		
		//Head collection.
		if (player.getKiller() != null) {
			Player killer = player.getKiller(); 
			boolean giveHead = false;
			if (killer.hasPermission("vip.headcollect.diamond")) {
				giveHead = true;
			}
			if (killer.hasPermission("vip.headcollect.gold")) {
				float chance = random.nextFloat();
				if (chance <= 0.05f) {
					killer.sendMessage(Utils.integrateColor("&8[&4CW&8] &6You got lucky and got &5" + player.getDisplayName() + " &6his head!"));
					giveHead = true;
				}
			}
			
			if (giveHead == true) {
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
	            ItemMeta itemMeta = item.getItemMeta();
	            ((SkullMeta) itemMeta).setOwner(player.getName());
	            item.setItemMeta(itemMeta);
	            killer.getInventory().addItem(item);
			}
		}
		
		//Particles.
		if (player.hasPermission("vip.particle.diamond")) {
			cwu.getServer().getScheduler().scheduleSyncDelayedTask(cwu.getPlugin(), new Runnable() {
			    public void run() {
			    	if (player.isOp()) {
			    		ParticleEffect.displayBlockDust(player.getLocation(), 152, (byte) 0, 1f, 1.5f, 1f, 0.08f, 100);
			    		return;
			    	}
			    	ParticleEffect.displayBlockDust(player.getLocation(), 57, (byte) 0, 1f, 1.5f, 1f, 0.08f, 100);
			    }
			}, 10L);
			cwu.getServer().getScheduler().scheduleSyncDelayedTask(cwu.getPlugin(), new Runnable() {
			    public void run() {
			    	if (player.isOp()) {
			    		ParticleEffect.displayBlockDust(player.getLocation(), 152, (byte) 0, 1f, 1.5f, 1f, 0.08f, 100);
			    		return;
			    	}
			    	ParticleEffect.displayBlockDust(player.getLocation(), 57, (byte) 0, 1f, 1.5f, 1f, 0.08f, 100);
			    }
			}, 12L);
	    }
	}
}
