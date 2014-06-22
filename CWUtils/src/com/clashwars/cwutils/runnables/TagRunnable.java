package com.clashwars.cwutils.runnables;

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
		return (int) ((startTime + cwu.getConfig().getTagTime()) - System.currentTimeMillis() / 1000);
	}
	
	public void resetTime(Player tagger) {
		startTime = System.currentTimeMillis();
		this.tagger = tagger;
	}
	
	@Override
	public void run() {
		try {
			while ((startTime + cwu.getConfig().getTagTime()) < System.currentTimeMillis()) {
				Thread.sleep(1000);
				
				if (tagged.isDead() || tagger.isDead() || getTimeLeft() <= 0) {
					break;
				}
			}
			cwu.getCL().removeTag(tagged);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
