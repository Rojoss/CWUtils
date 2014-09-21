package com.clashwars.cwutils.events;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.clashwars.cwutils.util.Utils;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.event.EventFactionsChunkChange;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.massivecore.ps.PS;

public class FactionEvents implements Listener {
	
	
	@EventHandler
	public void teleport(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.ENDER_PEARL) {
			return;
		}
		if (event.getFrom().distance(event.getTo()) < 2.0f) {
			return;
		}
		
		Player player = event.getPlayer();
		Location loc = event.getTo();
		
		
		UPlayer uplayer = UPlayer.get(player);
		PS chunk = PS.valueOf(loc.getChunk());
		Faction pFaction = uplayer.getFaction();
		Faction faction = BoardColls.get().getFactionAt(chunk);
		
		Rel rel = pFaction.getRelationTo(faction);
		
		/*
		if (player.getName().equals("worstboy32")) {
			player.sendMessage(Utils.integrateColor("&aSelf: &7" + pFaction.getName() + " - Default:" + pFaction.isDefault() + " Normal:" + pFaction.isNormal() + " None:" + pFaction.isNone() + " "));
			player.sendMessage(Utils.integrateColor("&6relation: " + rel.toString()));
			player.sendMessage(Utils.integrateColor("&cTarget: &7" + faction.getName() + " - Default:" + faction.isDefault() + " Normal:" + faction.isNormal() + " None:" + faction.isNone() + " "));
		}
		*/
		
		//Allow admins to teleport anywhere
		if (uplayer.isUsingAdminMode()) {
			return;
		}
		//Allow teleporting to wilderness
		if (faction.isNone()) {
			return;
		}
		//Allow teleporting to safezone
		if (faction.getName().equalsIgnoreCase("SafeZone")) {
			return;
		}
		//Allow teleporting to warzone
		if (faction.getName().equalsIgnoreCase("WarZone")) {
			return;
		}
		//Allow teleporting to own faction
		if (faction.getName().equalsIgnoreCase(pFaction.getName())) {
			return;
		}
		//Allow teleporting to ally
		if (rel == Rel.ALLY) {
			return;
		}
		
		player.sendMessage(Utils.integrateColor("&8[&4CW&8] &cYou can't teleport to other factions."));
		event.setCancelled(true);
	}
	
	
	@EventHandler
	public void onClaim(EventFactionsChunkChange event) {
		int radius = 3;
		Faction faction = event.getNewFaction();
		PS chunk = event.getChunk();
		UPlayer sender = event.getUSender();
		
		//Always allow unclaiming.
		if (faction.isNone()) {
			return;
		}
		//Always allow safezone/warzone/wilderness claiming and allow admins to claim always.
		if (faction.isDefault() || sender.isUsingAdminMode()) {
			return;
		}
		
		//Don't allow claiming in regions.
		if (!Utils.canbuildInRegion(sender.getPlayer())) {
			sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cYou can't claim right here!"));
			event.setCancelled(true);
			return;
		}
		
		/* CHECK FOR NEARBY ENEMIES */
		if (sender != null && sender.getPlayer() != null) {
			Player player = sender.getPlayer();
			for (Entity e : player.getNearbyEntities(32, 256, 32)) {
				if (!(e instanceof Player)) {
					continue;
				}
				Player p = (Player)e;
				
				UPlayer fPlayer = UPlayer.get(p);
				Faction playerFac = fPlayer == null ? null : fPlayer.getFaction();
				
				if (playerFac == null) {
					continue;
				}
				
				if (faction.getRelationWish(playerFac) == Rel.ENEMY) {
					sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cYou can't claim this land because there is an enemy nearby!"));
					event.setCancelled(true);
					return;
				}
			}
		}
		
		
		
		/*  CHECK FOR NEARBY FACTIONS  */
		ArrayList<String> checkedFactions = new ArrayList<String>();
		
		int claimX = chunk.getChunkX();
		int claimZ = chunk.getChunkZ();
		
		//Loop through factions in xx radius.
		for (int x = claimX + -radius; x <= claimX + radius; x++) {
			for (int z = claimZ + -radius; z <= claimZ + radius; z++) {
				//Don't check chunk u claim to allow pillage.
				if (x == claimX && z == claimZ) {
					continue;
				}
				Faction fac = BoardColls.get().getFactionAt(PS.valueOf(chunk.getWorld(), x, z));
				if (fac == null) {
					continue;
				}
				
				//Allow ally's to claim next to eachother.
				Rel relation = faction.getRelationWish(fac);
				if (relation == Rel.ALLY) {
					continue;
				}
				//Allow pillaging
				if (event.getType() == EventFactionsChunkChangeType.CONQUER || event.getType() == EventFactionsChunkChangeType.PILLAGE) {
					continue;
				}
				//Ignore wilderness
				if (fac.isNone()) {
					continue;
				}
				//Ignore own faction.
				if (fac.getName() == faction.getName()) {
					continue;
				}
				//Don't check a faction twice.
				if (checkedFactions.contains(fac.getName())) {
					continue;
				}
				
				//Loop through all nearby chunks at the found chunk and count the chunks for both factions.
				//The faction with most chunks will be seen as the owner and can claim in between.
				int fClaimer = 0;
				int fTarget = 0;
				for (int xx = claimX + -6; xx <= claimX + 6; xx++) {
					for (int zz = claimZ + -6; zz <= claimZ + 6; zz++) {
						if (BoardColls.get().getFactionAt(PS.valueOf(chunk.getWorld(), xx, zz)).getName() == faction.getName()) {
							fClaimer++;
						}
					}
				}
				for (int xx = x + -6; xx <= x + 6; xx++) {
					for (int zz = z + -6; zz <= z + 6; zz++) {
						if (BoardColls.get().getFactionAt(PS.valueOf(chunk.getWorld(), xx, zz)).getName() == fac.getName()) {
							fTarget++;
						}
					}
				}
				
				//The claimer has more land then the found faction so he will be able to claim in between.
				//But there might be more factions so we continue to check.
				//We add the faction we just checked for size to a list so it doesn't get checked twice.
				if (fClaimer > fTarget) {
					checkedFactions.add(fac.getName());
					continue;
				}
				
				if (sender != null) {
					sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cYou can't claim this close to " + fac.getName() + "."));
					sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cYou have to become ally's to claim this land."));
				}
				event.setCancelled(true);
				return;
			}
		}
	}
	
}
