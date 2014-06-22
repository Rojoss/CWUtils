package com.clashwars.cwutils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.clashwars.cwutils.runnables.TagRunnable;
import com.clashwars.cwutils.util.Utils;

public class PlayerTagger {
	
	private CWUtils cwu;
	Map<Player, TagRunnable> tags = new HashMap<Player, TagRunnable>();
	
	
	public PlayerTagger(CWUtils cwu) {
		this.cwu = cwu;
	}
	
	public void tag(Player tagged, Player tagger) {
		if (tags.containsKey(tagged)) {
			tags.get(tagged).resetTime(tagger);
		} else {
			TagRunnable tagR = new TagRunnable(cwu, tagged, tagger);
			tags.put(tagged, tagR);
			tagged.sendMessage(Utils.integrateColor("&8[&4CW Tag&8] &cYou have been tagged by &4" + tagger.getName()));
		}
	}

	public void removeTag(Player tagged) {
		if (tags.containsKey(tagged)) {
			tags.remove(tagged);
			tagged.sendMessage(Utils.integrateColor("&8[&4CW Tag&8] &6You are no longer tagged!"));
		}
	}
	
	public boolean isTagged(Player player) {
		return tags.containsKey(player);
	}
	
	public Player getTagger(Player tagged) {
		Player tagger = null;
		if (tags.containsKey(tagged)) {
			tagger = tags.get(tagged).getTagger();
		}
		return tagger;
	}
}
