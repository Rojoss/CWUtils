package com.clashwars.cwutils.util;

import java.io.File;
import java.util.Random;

import net.minecraft.server.v1_7_R4.EntityFireball;
import net.minecraft.server.v1_7_R4.EntityLargeFireball;
import net.minecraft.server.v1_7_R4.EntityLiving;
import net.minecraft.server.v1_7_R4.EntitySmallFireball;
import net.minecraft.server.v1_7_R4.MathHelper;
import net.minecraft.server.v1_7_R4.WorldServer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftProjectile;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;

public class Utils {
	//Integrate colors in a string
	public static String integrateColor(String str) {
		for (ChatColor c : ChatColor.values()) {
			str = str.replaceAll("&" + c.getChar() + "|&" + Character.toUpperCase(c.getChar()), c.toString());
		}
		return str;
	}
	
	public static String[] integrateColor(String[] str) {
		for (int i = 0; i < str.length; i++) {
			for (ChatColor c : ChatColor.values()) {
				str[i] = str[i].replaceAll("&" + c.getChar() + "|&" + Character.toUpperCase(c.getChar()), c.toString());
			}
		}
		return str;
	}
	
	public static String stripAllColour(String str) {
		return ChatColor.stripColor(integrateColor(str));
	}
	
	public static String removeColour(String str) {
		for (ChatColor c : ChatColor.values()) {
			str = str.replace(c.toString(), "&" + c.getChar());
		}

		return str;
	}
	
	//Trim first string from string array
	public static String[] trimFirst(String[] arr) {
		String[] ret = new String[arr.length - 1];

		for (int i = 1; i < arr.length; i++) {
			ret[i - 1] = arr[i];
		}

		return ret;
	}
	
	public static void resetPlayer(Player player) {
		player.getInventory().clear();
		player.setHealth((double) 20);
		player.setFoodLevel(20);
		player.setExp(0f);
		player.setLevel(0);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.setFireTicks(0);
	}
	
	
	public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File files[] = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                	deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return(path.delete());
	}
	
	public static boolean canbuildInRegion (Player player) {
		ApplicableRegionSet set = WGBukkit.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());
		LocalPlayer localPlayer = WGBukkit.getPlugin().wrapPlayer(player);
		return set.canBuild(localPlayer);
    }
	
	public static int random(Random random, int start, int end) {
		return start + random.nextInt(end - start + 1);
	}
	
	public static void shootAt(org.bukkit.entity.LivingEntity entity, org.bukkit.entity.EntityType type, Location loc, float force) {
		EntityLiving target = ((CraftLivingEntity) entity).getHandle();
		WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();

		switch (type) {
		case FIREBALL:
			shootAtWithFireballProperties(new EntityLargeFireball(world), target, loc, force);
			return;
		case SMALL_FIREBALL:
			shootAtWithFireballProperties(new EntitySmallFireball(world), target, loc, force);
			return;
		default:
			return;
		}
	}

	// Used for large and small fireballs
	private static void shootAtWithFireballProperties(EntityFireball fireball, EntityLiving target, Location loc, float force) {
		fireball.isIncendiary = false;
		Random random = new Random();

		double d0 = target.locX - loc.getX();
		double d1 = target.boundingBox.b + target.length / 2.0F - (loc.getY() + target.length / 2.0F);
		double d2 = target.locZ - loc.getZ();
		double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);

		float f2 = (float) (Math.atan2(d2, d0) * 180.0D / 3.1415927410125732D) - 90.0F;
		float f3 = (float) (-(Math.atan2(d1, d3) * 180.0D / 3.1415927410125732D));

		double d4 = d0 / d3;
		double d5 = d2 / d3;

		if (fireball instanceof EntitySmallFireball) {
			float f1 = MathHelper.c(force) * 0.5F;
			d0 += random.nextGaussian() * (double) f1;
			d2 += random.nextGaussian() * (double) f1;
		}

		fireball.setPositionRotation(loc.getX() + d4, loc.getY(), loc.getZ() + d5, f2, f3);
		fireball.setPosition(fireball.locX, fireball.locY, fireball.locZ);
		fireball.height = 0.0F;
		fireball.motX = fireball.motY = fireball.motZ = 0.0D;
		// CraftBukkit start - Added setDirection method
		fireball.setDirection(d0, d1, d2);

		if (fireball instanceof EntitySmallFireball) {
			fireball.locY = loc.getY() + (double) (target.length / 2.0F) + 0.5D;
		}

		CraftEntity fb = fireball.getBukkitEntity();

		if (fb instanceof CraftProjectile) {
			((CraftProjectile) fb).setBounce(false);
		}

		target.world.addEntity(fireball);
	}

	
	
}
