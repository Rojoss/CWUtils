package com.clashwars.cwutils.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import com.clashwars.cwutils.CWUtils;
import com.clashwars.cwutils.util.Utils;

public class DuelEvents implements Listener {

	private CWUtils cwu;
	
	public DuelEvents(CWUtils cwu) {
		this.cwu = cwu;
	}
	
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (cwu.getDM().getDuel(player) == null) {
			return;
		}
		player.sendMessage(Utils.integrateColor("&8[&4CW Duel&8] &cYou are in a duel!"));
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (cwu.getDM().getDuel(player) == null) {
			return;
		}
		player.sendMessage(Utils.integrateColor("&8[&4CW Duel&8] &cYou are in a duel!"));
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (cwu.getDM().getDuel(player) == null) {
			return;
		}
		player.sendMessage(Utils.integrateColor("&8[&4CW Duel&8] &cYou are in a duel!"));
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		if (cwu.getDM().getDuel(player) == null) {
			return;
		}
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		if (cwu.getDM().getDuel(player) == null) {
			return;
		}
		player.sendMessage(Utils.integrateColor("&8[&4CW Duel&8] &cYou are in a duel!"));
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (cwu.getDM().getDuel(player) == null) {
			return;
		}
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent event) {
		if (!(event.getEntered() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntered();
		if (cwu.getDM().getDuel(player) == null) {
			return;
		}
		player.sendMessage(Utils.integrateColor("&8[&4CW Duel&8] &cYou are in a duel!"));
		event.setCancelled(true);
	}
}
