package com.clashwars.cwutils;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.clashwars.cwutils.util.Utils;

public class QueueManager {
	
	private CWUtils cwu;
	
	public QueueManager(CWUtils cwu) {
		this.cwu = cwu;
	}
	
	//DB 'Queue':	ID | UUID | Name | Type | Content
	
	public boolean execute(UUID player, String type, String content, boolean forceInDatabase) {
		return execute(cwu.getServer().getOfflinePlayer(player), type, content, forceInDatabase);
	}
	
	public boolean execute(OfflinePlayer player, String type, String content, boolean forceInDatabase) {
		if (!forceInDatabase && player != null && player.hasPlayedBefore() && player.isOnline()) {
			//Execute the msg/cmd.
			
			content = content.replace("{PLAYER}", player.getName());
			content = content.replace("{DISPLAYNAME}", ((Player) player).getDisplayName());
			
			if (type.equalsIgnoreCase("cmd")) {
				cwu.getServer().dispatchCommand(cwu.getServer().getConsoleSender(), content);
			} else {
				((Player)player).sendMessage(Utils.integrateColor(content));
			}
			return true;
		} else {
			//Save it to database to be executed on login.
			if (player != null && player.hasPlayedBefore()) {
				try {
					Statement statement = cwu.getSql().createStatement();
					statement.executeUpdate("INSERT INTO Queue (UUID, Name, Type, Content) VALUES ('" 
							+ player.getUniqueId().toString() + "', '" + player.getName() + "', '" + type + "', '" + content + "');");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		return false;
	}
}
