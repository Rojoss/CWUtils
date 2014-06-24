package com.clashwars.cwutils.runnables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.clashwars.cwutils.CWUtils;

public class TagRunnable implements Runnable {
	
	private CWUtils cwu;
	
	private long startTime;
	private Player tagged;
	private Player tagger;
	
	public TagRunnable(CWUtils cwu, Player tagged, Player tagger) {
		this.cwu = cwu;
		this.tagged = tagged;
		this.tagger = tagger;
		startTime = System.currentTimeMillis();
		cwu.getServer().getScheduler().runTaskAsynchronously(cwu.getPlugin(), this);
	}
	
	public Player getTagger() {
		return tagger;
	}
	
	public int getTimeLeft() {
		return (int) (((startTime + (cwu.getConfig().getTagTime() * 1000)) - System.currentTimeMillis()) / 1000);
	}
	
	public void resetTime(Player tagger) {
		startTime = System.currentTimeMillis();
		this.tagger = tagger;
	}
	
	@Override
	public void run() {
		try {
			while ((startTime + (cwu.getConfig().getTagTime() * 1000)) > System.currentTimeMillis() && cwu.isEnabled()) {
				Thread.sleep(100);
				
				if (getTimeLeft() <= 0) {
					break;
				}
			}
			cwu.getTM().removeTag(tagged);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
