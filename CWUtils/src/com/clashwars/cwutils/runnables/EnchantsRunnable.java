package com.clashwars.cwutils.runnables;

import java.util.Random;

import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.clashwars.cwutils.CWUtils;
import com.clashwars.cwutils.util.ParticleEffect;
import com.clashwars.cwutils.util.Utils;


public class EnchantsRunnable extends BukkitRunnable {

	private Random random = new Random();
	private Enchantment[] enchants = Enchantment.values();
	
	private CWUtils cwu;
	private Player player;
	private int slot;
	
	private int count;
	
	public EnchantsRunnable(CWUtils cwu, Player player, int slot) {
		this.cwu = cwu;
		this.player = player;
		this.slot = slot;
		count = 5;
	}
	
	@Override
	public void run() {
		int id = Utils.random(random, 0, enchants.length - 1);
		int level = Utils.random(random, 1, enchants[id].getMaxLevel());
		if (level == enchants[id].getMaxLevel()) {
			float chance = random.nextFloat();
			if (chance < 0.9f) {
				level = Utils.random(random, 1, enchants[id].getMaxLevel());
			}
		}
		cwu.getServer().dispatchCommand(cwu.getServer().getConsoleSender(), "give " + player.getName() + " enchantedbook 1 " + 
				enchants[id].getName() + ":" + level);
		
		player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0f, 1.0f);
		ParticleEffect.ENCHANTMENT_TABLE.display(player.getLocation(), 1.0f, 1.0f, 1.0f, 0.1f, 200);
		ItemStack item = player.getInventory().getItem(slot);
		if (item != null && item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName() && Utils.removeColour(meta.getDisplayName()).equalsIgnoreCase("&6&lBookshelf with &5&l" + count + " enchanted books&6&l!")) {
				if (count > 1) {
					meta.setDisplayName(Utils.integrateColor("&6&lBookshelf with &5&l" + (count - 1) + " enchanted books&6&l!"));
				} else {
					meta.setDisplayName(Utils.integrateColor("&6&lEmpty bookshelf"));
				}
				item.setItemMeta(meta);
			}
		}
		count--;
		if (count <= 0) {
			player.sendMessage(Utils.integrateColor("&8[&4CW&8] &6You emptied out the bookshelf and got 5 enchanted books!"));
			this.cancel();
		}
	}
}
