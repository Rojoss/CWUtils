package com.clashwars.cwutils.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.clashwars.cwutils.PlayerBackup;




public class Config {
	
	
/* ############ */
/* PluginConfig */
/* ############ */
	
	private int tagTime;
	private List<String> blockedCmds;
	private Map<UUID, List<String>> messages;
	private Map<String, Boolean> status = new HashMap<String, Boolean>();
	
	
	//Tag time
	public int getTagTime() {
		return tagTime;
	}
	
	public void setTagTime(int time) {
		this.tagTime = time;
	}

	//Blocked cmds
	public void setBlockedCmds(List<String> blockedCmds) {
		this.blockedCmds = blockedCmds;
	}
	
	public List<String> getBlockedCmds() {
		return blockedCmds;
	}

	//Queued messages
	public void setMessages(Map<UUID, List<String>> messages) {
		this.messages = messages;
	}
	
	public Map<UUID, List<String>> getMessages() {
		return messages;
	}
	
	public List<String> getMessages(UUID player) {
		return messages.get(player);
	}
	
	public void addMessage(UUID player, String message) {
		List<String> list = messages.get(player);
		if (list != null && !list.contains(message)) {
			list.add(message);
			messages.put(player, list);
		}
	}
	
	public void removeMessage(UUID player, String message) {
		List<String> list = messages.get(player);
		if (list.contains(message)) {
			list.remove(message);
			if (list.isEmpty()) {
				messages.remove(player);
			} else {
				messages.put(player, list);
			}
		}
	}

	//Enable/Disable certain plugin elements.
	public void setStatus(String element, boolean bool) {
		status.put(element, bool);
	}
	public boolean getStatus(String element) {
		return status.get(element);
	}
	
	
	
	/* ############ */
	/* PlayerBackup */
	/* ############ */
	
	private HashMap<UUID, PlayerBackup> playerBackups = new HashMap<UUID, PlayerBackup>();
	
	public void setBackups(HashMap<UUID, PlayerBackup> backups) {
		this.playerBackups = backups;
	}
	
	public void addBackup(UUID player, PlayerBackup backup) {
		playerBackups.put(player, backup);
	}
	
	public HashMap<UUID, PlayerBackup> getBackups() {
		return playerBackups;
	}
	
	public PlayerBackup getBackup(UUID player) {
		return playerBackups.get(player);
	}
	
	
	
	
	

}
