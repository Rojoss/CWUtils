package com.clashwars.cwutils;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.clashwars.cwutils.util.ParticleEffect;

public class Luck {
	
	private Random random = new Random();
	
	// 0.05 | 0.1
	public boolean checkChance(Player player, float minChance, float maxChance) {
		//Get amount of emeralds in ivnentory.
		int emeralds = 0;
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null) {
				continue;
			}
			if (item.getType() == Material.EMERALD) {
				emeralds += item.getAmount();
			}
		}
		
		//Get percentage of total emeralds for example 64 is 0.1% and 640 is 1.0%.
		float percentage = ((float)emeralds / 640.0f) * 100.0f;
		
		//Get chance based on percentage of emeralds and min/max chance.
		float chance = minChance + (((maxChance - minChance) / 100) * percentage);
		
		float r = random.nextFloat();
		if (r <= chance) {
			return true;
		} else {
			return false;
		}
	}


	@SuppressWarnings("deprecation")
	public boolean breakTree(Player player, Block baseLog) {
		int blocksRemoved = 0;
		Block blockAbove = baseLog.getRelative(BlockFace.UP);
		while (blockAbove.getType() == Material.LOG || blockAbove.getType() == Material.LOG_2) {
			blocksRemoved++;
			blockAbove.breakNaturally();
			if (blockAbove.getType() == Material.LOG_2) {
				ParticleEffect.displayBlockCrack(blockAbove.getLocation().add(0.5f, 0f, 0.5f), 162, blockAbove.getData(), 0.5f, 0.5f, 0.5f, 20);
			} else {
				ParticleEffect.displayBlockCrack(blockAbove.getLocation().add(0.5f, 0f, 0.5f), 17, blockAbove.getData(), 0.5f, 0.5f, 0.5f, 20);
			}
			
			blockAbove = blockAbove.getRelative(BlockFace.UP);
		}
		if (blocksRemoved < 1) {
			return false;
		} else {
			return true;
		}
		
		/*
		int distance = 0;
		Block next;
		while (true) {
			distance++;
			next = baseLog.getRelative(BlockFace.UP, distance);
			if (next.getType() == Material.LOG || next.getType() == Material.LOG_2) {
				continue;
			} else if (next.getType() != Material.LEAVES || next.getType() != Material.LEAVES_2) {
				return true;
			} else {
				Material block = baseLog.getType();
				if (block == Material.LOG || block == Material.LOG_2) {
					Location blockLocation = baseLog.getLocation();
					double y = blockLocation.getBlockY();
					double x = blockLocation.getBlockX();
					double z = blockLocation.getBlockZ();

					World currentWorld = player.getWorld();
					boolean logsLeft = true;

					while (logsLeft == true) {
						y++;
						Location blockAbove = new Location(currentWorld, x, y, z);
						Material blockAboveType = blockAbove.getBlock().getType();
						byte blockAboveData = blockAbove.getBlock().getData();
						if (blockAboveType == Material.LOG || blockAboveType == Material.LOG_2) {
							ItemStack droppedItem = new ItemStack(blockAboveType, 1, blockAboveData);
							blockAbove.getBlock().setType(Material.AIR);
							currentWorld.dropItem(blockAbove, droppedItem);
							logsLeft = true;
						} else {
							logsLeft = false;
						}
					}
				}
			}
		}
		*/
	}
	
	
	
	
}
