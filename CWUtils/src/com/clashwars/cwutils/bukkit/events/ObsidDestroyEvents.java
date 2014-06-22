package com.clashwars.cwutils.bukkit.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.clashwars.cwutils.CWUtils;
import com.clashwars.cwutils.util.ItemUtils;

public class ObsidDestroyEvents implements Listener {
	private CWUtils cwu;

	public ObsidDestroyEvents(CWUtils cwu) {
		this.cwu = cwu;
	}

	// Get Blackpowder from gravel and get C4 back.
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		World world = block.getWorld();

		// Randomly give blackpowder
		if (block.getType().equals(Material.GRAVEL)) {
			Random random = new Random();
			if (random.nextInt(5) == 1) {
				world.dropItemNaturally(event.getBlock().getLocation(), blackPowderItem());
			}
			return;
		}

		// Give C4 item back if the tnt block is C4 and reset the block.
		if (block.getType().equals(Material.TNT)) {
			if (block.hasMetadata("C4")) {
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				event.getBlock().removeMetadata("C4", cwu.getPlugin());
				world.dropItemNaturally(event.getBlock().getLocation(), c4Item());
			}
			return;
		}
		return;
	}

	// Place C4
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();

		if (!block.getType().equals(Material.TNT)) {
			return;
		}

		ItemStack hand = event.getPlayer().getInventory().getItemInHand();
		if (!hand.hasItemMeta()) {
			return;
		}
		ItemMeta meta = hand.getItemMeta();
		if (!meta.getDisplayName().equals(ChatColor.DARK_RED + "C4")) {
			return;
		}
		block.setMetadata("C4", new FixedMetadataValue(cwu.getPlugin(), "C4"));
		return;
	}

	// Custom recipe for C4.
	@EventHandler
	public void onPrepareCraft(PrepareItemCraftEvent event) {
		if (event.getRecipe().getResult().getType() == Material.TNT) {
			ItemStack[] ingredients = event.getInventory().getContents();
			int blackPowder = 0;
			for (ItemStack ingredient : ingredients) {
				if (ingredient.getType() == Material.SULPHUR) {
					if (ingredient.hasItemMeta()) {
						ItemMeta meta = ingredient.getItemMeta();
						if (meta.getDisplayName().contains("Blackpowder")) {
							blackPowder++;
						}
					}
				}
			}
			if (blackPowder > 0 && blackPowder < 5) {
				event.getInventory().setResult(null);
			}
			if (blackPowder >= 5) {
				event.getInventory().setResult(c4Item());
			}
		}
		
		//Prevent ender chest creation.
		if (event.getRecipe().getResult().getType() == Material.ENDER_CHEST) {
			ItemStack[] ingredients = event.getInventory().getContents();
			for (ItemStack ingredient : ingredients) {
				if (ingredient.getType() == Material.NETHER_STAR) {
					return;
				}
			}
			event.getInventory().setResult(invalidEnderChest());
		}
	}

	// Ignite C4
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getMaterial() == Material.FLINT_AND_STEEL) {
				igniteC4(event.getClickedBlock());
			}
		}
	}

	// check if a block is C4 and if it is ignite it.
	private void igniteC4(final Block block) {
		if (!block.getType().equals(Material.TNT)) {
			return;
		}
		if (block.hasMetadata("C4")) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(cwu.getPlugin(), new Runnable() {
				public void run() {
					Chunk chunk = block.getChunk();
					for (Entity entity : chunk.getEntities()) {
						if (entity.getType() == EntityType.PRIMED_TNT) {
							if (entity.getLocation().distanceSquared(block.getLocation()) < 1) {
								entity.setMetadata("C4", new FixedMetadataValue(cwu.getPlugin(), "C4"));
							}
						}
					}
				}
			}, 10);
		}
	}

	// C4 explosion
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.getEntity().getType() != EntityType.PRIMED_TNT) {
			return;
		}
		if (!event.getEntity().hasMetadata("C4")) {
			return;
		}
		event.getLocation().getWorld().playSound(event.getLocation(), Sound.EXPLODE, 2.0f, 0.0f);
		Block block = event.getLocation().getBlock();

		List<Block> blocks = new ArrayList<Block>();
		blocks.add(block);
		blocks.add(block.getRelative(0, 1, 0));
		blocks.add(block.getRelative(0, -1, 0));
		blocks.add(block.getRelative(1, 0, 0));
		blocks.add(block.getRelative(-1, 0, 0));
		blocks.add(block.getRelative(0, 0, 1));
		blocks.add(block.getRelative(0, 0, -1));

		for (Block b : blocks) {
			if (b.getType() == Material.OBSIDIAN) {
				byte newdata = (byte) ((byte) b.getState().getRawData() + 1);
				if (newdata > 1) {
					b.breakNaturally();
					b.getWorld().playSound(b.getLocation(), Sound.ITEM_BREAK, 2.0f, 0.0f);
				} else {
					b.setData(newdata);
					b.getWorld().playSound(b.getLocation(), Sound.ITEM_BREAK, 2.0f, 1.0f);
				}
			}
		}
	}
	
	/*
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		Block ba1 = event.getBlock().getRelative(event.getFace().DOWN);
		Block ba2 = event.getBlock().getRelative(event.getFace(), 2);
		Bukkit.broadcastMessage(ChatColor.GRAY + ba1.getType().name());
		Bukkit.broadcastMessage(ChatColor.DARK_GRAY + ba2.getType().name());
		
		
		if (event.getBlock().getType() == Material.STATIONARY_WATER || event.getBlock().getType() == Material.WATER) {
			Bukkit.broadcastMessage(ChatColor.AQUA + event.getToBlock().getType().name());
			if (event.getToBlock().getType() == Material.STATIONARY_LAVA) {
				event.getToBlock().setType(Material.DIAMOND_BLOCK);
			}
		}
		
		if (event.getBlock().getType() == Material.STATIONARY_LAVA || event.getBlock().getType() == Material.LAVA) {
			Bukkit.broadcastMessage(ChatColor.YELLOW + event.getToBlock().getType().name());
			if (event.getToBlock().getType() == Material.STATIONARY_WATER) {
				event.getToBlock().setType(Material.GOLD_BLOCK);
			}
		}
	}
	*/
	

	private ItemStack blackPowderItem() {
		return ItemUtils.getItem(Material.SULPHUR, 1, (short) 0, "&8Blackpowder", new String[] { "&7Used to craft C4!", "&5&lRecipe&8&l:",
				"&8Blackpowder @ &7| &eSand #", "  &8@ &e# &8@", "  &e# &8@ &e#", "  &8@ &e# &8@" });
	}

	private ItemStack c4Item() {
		return ItemUtils.getItem(Material.TNT, 1, (short) 0, "&4C4", new String[] { "&cCan destroy obsidian!" });
	}

	private ItemStack invalidEnderChest() {
		return ItemUtils.getItem(Material.OBSIDIAN, 1, (short) 0, "&4&lDISABLED!", new String[] {"&cThis recipe is disabled", "&cUse this recipe instead!",
				"  &b$ &e& &b$  &7-  &bDiaBlock $ &7| &eNetherStar &", "  &5# &2@ &5#  &7-  &5Obsidian # &7| &2EyeOfEnder @", "  &5# &5# &5#" });
	}
}
