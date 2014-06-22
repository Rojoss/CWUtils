package com.clashwars.cwutils.config;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class Config {
	
	private int tagTime;
	private List<String> blockedCmds;
	private Map<UUID, List<String>> messages;
	
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
}
