package com.clashwars.cwutils.bukkit.events;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.clashwars.cwutils.CWUtils;

public class PluginMessageEvents implements PluginMessageListener {

	private CWUtils cwu;
	
	public PluginMessageEvents(CWUtils cwu) {
		this.cwu = cwu;
	}
	
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equalsIgnoreCase("CWBungee")) {
			return;
		}
		
		final DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

		cwu.getServer().getScheduler().runTaskLater(cwu.getPlugin(), new Runnable() {
			@Override
			public void run() {
				try {
					String ch = in.readUTF();
					switch (ch.toLowerCase()) {
						case "queue":
							cwu.getQM().execute(UUID.fromString(in.readUTF()), in.readUTF(), in.readUTF(), false);
							break;
						case "eventdata":
							// sender | event | arena | players | slots | status
							cwu.sendEventInfo(in.readUTF(), in.readUTF(), in.readUTF(), in.readInt(), in.readInt(), in.readUTF());
							break;
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, 5);
	}
}
