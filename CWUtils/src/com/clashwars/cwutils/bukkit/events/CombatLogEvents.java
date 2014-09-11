package com.clashwars.cwutils.bukkit.events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.clashwars.cwutils.CWUtils;
import com.clashwars.cwutils.util.Utils;

public class CombatLogEvents implements Listener {

	private CWUtils cwu;
	
	public CombatLogEvents(CWUtils cwu) {
		this.cwu = cwu;
	}
	
	//Tag players when hit.
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerTakeDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled() || !cwu.getConfig().getStatus("tagging")) {
			return;
		}
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player damager = null;
		if (event.getDamager() instanceof Player) {
			damager = (Player) event.getDamager();
		}
		if (event.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) event.getDamager();
			ProjectileSource projSrc = proj.getShooter();
			if (projSrc instanceof Player) {
				damager = (Player) projSrc;
			}
		}
		if (damager == null) {
			return;
		}
		
		//TODO: Potions and maybe other damage sources.
		
		cwu.getTM().tag((Player)event.getEntity(), damager);
	}
	
	//Remove tag on death
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (!cwu.getConfig().getStatus("tagging")) {
			return;
		}
		cwu.getTM().removeTag((Player)event.getEntity());
	}
	
	
	//Kill player if he is tagged for quit and kick.
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onQuit(PlayerQuitEvent event) {
		if (!cwu.getConfig().getStatus("tagging")) {
			return;
		}
		
		Player player = event.getPlayer();
		if (player.isDead()) {
			return;
		}
		
		if (cwu.getTM().isTagged(player)) {
			player.setHealth(0);
			cwu.getQM().execute(player, "msg", "&8[&4CW Tag&8] &c&lYou where killed for combat logging!", true);
			cwu.getQM().execute(player, "msg", "&8[&4CW Tag&8] &4" + cwu.getTM().getTagger(player) + " &chad tagged you!", true);
			Bukkit.broadcastMessage(Utils.integrateColor("&8[&4CW Tag&8] &9&l" + player.getName() + " &3combat logged!"));
			cwu.getTM().removeTag(player);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onKick(PlayerKickEvent event) {
		if (!cwu.getConfig().getStatus("tagging")) {
			return;
		}
		
		cwu.getTM().removeTag(event.getPlayer());
	}
	
	
	//Prevent teleportation when tagged.
	@EventHandler
	public void onTeleport(EntityTeleportEvent event) {
		if (!cwu.getConfig().getStatus("tagging")) {
			return;
		}
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (cwu.getTM().isTagged(player)) {
				event.setCancelled(true);
			}
		}
	}
	
	
	//Prevent using certain commands when tagged.
	@EventHandler(priority = EventPriority.LOWEST)
	public void commandPre(PlayerCommandPreprocessEvent event) {
		if (!cwu.getConfig().getStatus("tagging")) {
			return;
		}
		Player player = event.getPlayer();
		String msg = event.getMessage();

		if (cwu.getTM().isTagged(player)) {
			List<String> cmds = cwu.getConfig().getBlockedCmds();
			for (String cmd : cmds) {
				if (msg.toLowerCase().trim().startsWith(cmd.toLowerCase().trim()) || msg.toLowerCase().trim().substring(1).startsWith(cmd.toLowerCase().trim())) {
					player.sendMessage(Utils.integrateColor("&8[&4CW Tag&8] &cYou can't use this command while tagged!"));
					event.setCancelled(true);
					break;
				}
			}
		}
	}
}
