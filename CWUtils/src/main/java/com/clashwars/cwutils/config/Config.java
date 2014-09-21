package com.clashwars.cwutils.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.clashwars.cwutils.PlayerBackup;
import com.clashwars.cwutils.sql.SqlInfo;




public class Config {
	
	
/* ############ */
/* PluginConfig */
/* ############ */
	
	private SqlInfo sqlInfo;
	private int tagTime;
	private List<String> blockedCmds;
	private Map<String, Boolean> status = new HashMap<String, Boolean>();

	
	//Sql info
	public SqlInfo getSqlInfo() {
		return sqlInfo;
	}

	public void setSqlInfo(SqlInfo sqlInfo) {
		this.sqlInfo = sqlInfo;
	}
	
	
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
