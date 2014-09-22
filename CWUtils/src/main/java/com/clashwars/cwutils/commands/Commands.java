package com.clashwars.cwutils.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.clashwars.cwutils.CWUtils;
import com.clashwars.cwutils.util.ExpUtils;
import com.clashwars.cwutils.util.Utils;

public class Commands {
	private CWUtils			cwu;
	private final List<Method>	commands	= new ArrayList<Method>();

	private Random random = new Random();
	
	public Commands(CWUtils cwu) {
		this.cwu = cwu;
	}

	/* Start of commands */

	@Command(permissions = {}, aliases = { "reward" })
	public boolean reward(CommandSender sender, String label, String argument, String... args) {
		if (args.length < 1) {
			sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cInvalid usage. &7/reward <player>"));
			return true;
		}
		
		if (!sender.hasPermission("cw.reward") && !sender.isOp()) {
			sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cInsufficient permissions."));
			return true;
		}
		
		@SuppressWarnings("deprecation")
		Player player = cwu.getServer().getPlayer(args[0]);
		if (player == null) {
			sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cInvalid player."));
			return true;
		}
		
		List<String> rewardsGiven = new ArrayList<String>();
		
		//Coins
		float chance = random.nextFloat();
		if (chance <= 0.20f) {
			cwu.getServer().dispatchCommand(cwu.getServer().getConsoleSender(), "eco give " + player.getName() + " 500");
			rewardsGiven.add("500 Coins");
		}
		
		//XP
		chance = random.nextFloat();
		if (chance <= 0.20f) {
			ExpUtils xpu = new ExpUtils(player);
			xpu.setExp(xpu.getCurrentExp() + 255);
			rewardsGiven.add("255 XP");
		}
		
		//Diamonds
		chance = random.nextFloat();
		if (chance <= 0.10f) {
			int amount = 1;
			chance = random.nextFloat();
			if (chance < 0.05f) {
				amount = 3;
			} else if (chance < 0.5f) {
				amount = 2;
			}
			
			if (player.getInventory().firstEmpty() >= 0) {
				player.getInventory().addItem(new ItemStack(Material.DIAMOND, amount));
			} else {
				player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.DIAMOND, amount));
			}
			rewardsGiven.add("" + amount + " Diamond");
		}
		
		//TnT
		chance = random.nextFloat();
		if (chance <= 0.15f) {
			int amount = 4;
			chance = random.nextFloat();
			if (chance < 0.05f) {
				amount = 16;
			} else if (chance < 0.5f) {
				amount = 8;
			}
			
			if (player.getInventory().firstEmpty() >= 0) {
				player.getInventory().addItem(new ItemStack(Material.TNT, amount));
			} else {
				player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.TNT, amount));
			}
			rewardsGiven.add("" + amount + " TnT");
		}
		
		//Emeralds
		chance = random.nextFloat();
		if (chance <= 0.25f) {
			int amount = 4;
			chance = random.nextFloat();
			if (chance < 0.05f) {
				amount = 16;
			} else if (chance < 0.5f) {
				amount = 8;
			}
			
			if (player.getInventory().firstEmpty() >= 0) {
				player.getInventory().addItem(new ItemStack(Material.EMERALD, amount));
			} else {
				player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.EMERALD, amount));
			}
			rewardsGiven.add("" + amount + " Emeralds");
		}
		
		//Haste
		chance = random.nextFloat();
		if (chance <= 0.5f) {
			if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING)) {
				Collection<PotionEffect> effects = player.getActivePotionEffects();
				for (PotionEffect effect : effects) {
					if (effect.getType().equals(PotionEffectType.FAST_DIGGING)) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, effect.getDuration() + 6000, 3), true);
					}
				}
			} else {
				player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 6000, 3));
			}
			rewardsGiven.add("Haste effect");
		}
		
		
		if (rewardsGiven.size() < 1) {
			player.sendMessage(Utils.integrateColor("&8[&4CW&8] &6No luck today, you didn't get any rewards."));
		} else {
			player.sendMessage(Utils.integrateColor("&8[&4CW&8] &6You received the following rewards&7: &5" + StringUtils.join(rewardsGiven, "&7, &5")));
		}
		return true;
	}
	
	
	@SuppressWarnings("deprecation")
	@Command(permissions = {"cw.alts"}, aliases = { "alts" })
	public boolean alts(CommandSender sender, String label, String argument, String... args) {
		if (cwu.getEssentials() == null) {
			sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cEssentials not found."));
			return true;
		}
		if (args.length < 1) {
			sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cInvalid usage. &7/alts <player>"));
			return true;
		}
		
		if (!sender.hasPermission("cw.alts") && !sender.isOp()) {
			sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cInsufficient permissions."));
			return true;
		}
		
		OfflinePlayer player = cwu.getServer().getOfflinePlayer(args[0]);
		if (player == null) {
			sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cInvalid player."));
			return true;
		}
		
		String ip = cwu.getEssentials().getOfflineUser(player.getName()).getLastLoginAddress();
		sender.sendMessage(Utils.integrateColor("&8== &4Alt accounts for &c" + player.getName() + "&8- &7" + ip + " &8=="));
		Set<UUID> users = cwu.getEssentials().getUserMap().getAllUniqueUsers();
		for (UUID p : users) {
			String name = cwu.getServer().getOfflinePlayer(p).getName();
			if (name != null && cwu.getEssentials().getOfflineUser(name) != null && cwu.getEssentials().getOfflineUser(name).getLastLoginAddress() != null) {
				if (cwu.getEssentials().getOfflineUser(name).getLastLoginAddress().equals(ip)) {
					sender.sendMessage(Utils.integrateColor("&4&l* &6" + name));
				}
			}
		}
		return true;
	}
	
	
	@Command(permissions = {"cw.queue"}, aliases = { "queue" })
	public boolean queue(CommandSender sender, String label, String argument, String... args) {
		if (args.length < 3) {
			sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cInvalid usage. &7/queue {player} {msg|cmd} {content}"));
			return true;
		}
		
		OfflinePlayer player = null;
		if (cwu.getServer().getOfflinePlayer(args[0]) == null || !cwu.getServer().getOfflinePlayer(args[0]).hasPlayedBefore()) {
			sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cInvalid player."));
			return true;
		}
		player = cwu.getServer().getOfflinePlayer(args[0]);
		
		String type = "msg";
		if (args[1].toLowerCase().equals("cmd")) {
			type = "cmd";
		}
		
		String content = "";
		for (int i = 2; i < args.length; i++) {
			content = content + args[i] + " ";
		}
		
		cwu.getQM().execute(player, type, content, false);
		sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &6Queue " + type + " added for &5" + player.getName() + "&6! &8'&7" + content + "&8'"));
		return true;
	}
	
	
	
	@Command(permissions = {}, aliases = { "event" })
	public boolean event(CommandSender sender, String label, String argument, String... args) {
		if (args.length < 1) {
			sender.sendMessage(Utils.integrateColor("&aGetting data from the event server..."));
			try {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);

				out.writeUTF("eventdatarequest");
				out.writeUTF(sender.getName());
				out.writeUTF("events");
				out.writeUTF("pvp");
				
				Bukkit.getOnlinePlayers().iterator().next().sendPluginMessage(cwu, "CWBungee", b.toByteArray());
			} catch (Throwable e) {
				e.printStackTrace();
			}
			return true;
		}
		if (!args[0].equalsIgnoreCase("join")) {
			sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cUse &4/event join &cto join events. Or &4/event &cfor details."));
			sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cAll your data is saved when joining events! &699.9% safe!"));
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cPlayer command only."));
			return true;
		}
		sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &6Teleporting to the events server..."));
		sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &7All your data is saved! Just like leaving the server."));
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);

			out.writeUTF("tpserver");
			out.writeUTF(((Player)sender).getName());
			out.writeUTF("events");
			
			Bukkit.getOnlinePlayers().iterator().next().sendPluginMessage(cwu, "CWBungee", b.toByteArray());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return true;
	}



    @Command(permissions = {}, aliases = { "vote" })
    public boolean vote(CommandSender sender, String label, String argument, String... args) {
        sender.sendMessage(Utils.integrateColor("&8======== &4&lVoting Sites &8========"));
        sender.sendMessage(Utils.integrateColor("&7&oYou can vote every day on the sites below."));
        sender.sendMessage(Utils.integrateColor("&7&oYou will get a random reward for every site you vote on."));
        sender.sendMessage(Utils.integrateColor("&6&lMain voting page&8&l:&9 http://clashwars.com/vote"));
        sender.sendMessage(Utils.integrateColor("&8★ &6Site 1 (PMC)&8:&9 http://goo.gl/Vmkp7q"));
        sender.sendMessage(Utils.integrateColor("&7&oplease also &4&ofavorite❤ &7&oand give a &b&odiamond✦ &7&oon pmc. &9:&c)"));
        sender.sendMessage(Utils.integrateColor("&8★ &6Site 2 (MS)&8:&9 http://goo.gl/ddmZv1"));
        sender.sendMessage(Utils.integrateColor("&8★ &6Site 3 (MCSL)&8:&9 http://goo.gl/30re1M"));
        return true;
    }
	
	
	/* End of commands */
	
	
	
	public void populateCommands() {
		commands.clear();

		for (Method method : getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class) && method.getReturnType().equals(boolean.class)) {
				commands.add(method);
			}
		}
	}

	public boolean executeCommand(CommandSender sender, String lbl, String... args) {
		try {
			for (Method method : commands) {
				Command command = method.getAnnotation(Command.class);
				String[] permissions = command.permissions();
				String[] aliases = command.aliases();
				String[] saliases = command.secondaryAliases();

				for (String alias : aliases) {
					if (alias.equalsIgnoreCase(lbl)) {
						if ((saliases == null || saliases.length <= 0)) {
							check: if (!sender.isOp() && permissions != null && permissions.length > 0) {
								for (String p : permissions) {
									if (sender.hasPermission(p)) {
										break check;
									}
								}

								sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cInsufficient permissions."));
								return true;
							}

							return (Boolean) method.invoke(this, sender, lbl, null, args);
						}

						if (args.length <= 0) {
							continue;
						}

						for (String salias : saliases) {
							if (salias.equalsIgnoreCase(args[0])) {
								check: if (!sender.isOp() && permissions != null && permissions.length > 0) {
									for (String p : permissions) {
										if (sender.hasPermission(p)) {
											break check;
										}
									}

									sender.sendMessage(Utils.integrateColor("&8[&4CW&8] &cInsufficient permissions."));
									return true;
								}

								return (Boolean) method.invoke(this, sender, lbl, args[0], Utils.trimFirst(args));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
