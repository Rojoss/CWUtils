package com.clashwars.cwutils.bukkit.events;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.PlayerFishEvent;

import com.clashwars.cwutils.CWUtils;
import com.clashwars.cwutils.util.ExpUtils;

public class ExpEvents implements Listener {

	private CWUtils cwu;
	
	public ExpEvents(CWUtils cwu) {
		this.cwu = cwu;
	}
	
	//Fishing (x5 XP)
	@EventHandler
	public void onFish(PlayerFishEvent event) {
		int baseXP = 1;
		if (event.getExpToDrop() > 1) {
			baseXP = event.getExpToDrop();
		}
		event.setExpToDrop(baseXP * 5);
	}
	
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		//Break spawner (30 levels)
		if (event.getBlock().getType() == Material.MOB_SPAWNER) {
			event.setExpToDrop(825);
			return;
		}
		
		//Break ores etc. (x1.5 XP)
		event.setExpToDrop(Math.round(event.getExpToDrop() * 1.5f));
	}
	
	//Smelting (x1.5 XP)
	@EventHandler
	public void onSmelt(FurnaceExtractEvent event) {
		event.setExpToDrop(Math.round(event.getExpToDrop() * 1.5f));
	}
	
	
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		//Player death drop all xp.
		if (entity instanceof Player) {
			Player player = (Player) entity;
			ExpUtils xpu = new ExpUtils(player);
			event.setDroppedExp(xpu.getCurrentExp());
			xpu.setExp(0);
			return;
		}
		
		//Killing mobs (x1.5 XP (x0.5 for spawners))
		if (cwu.spawnerMobs.contains(entity.getUniqueId())) {
			cwu.spawnerMobs.remove(entity.getUniqueId());
			event.setDroppedExp(Math.round(event.getDroppedExp() * 0.5f));
		} else {
			if (entity.getType() != EntityType.ENDER_DRAGON) {
				event.setDroppedExp(Math.round(event.getDroppedExp() * 1.5f));
			} else {
				event.setDroppedExp(Math.round(event.getDroppedExp() * 0.5f));
			}
		}
	}
	
	
	//Store mobs spawned from spawners.
	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.SPAWNER) {
			cwu.spawnerMobs.add(event.getEntity().getUniqueId());
		}
	}

}
