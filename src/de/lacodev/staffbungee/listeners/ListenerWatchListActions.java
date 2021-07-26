package de.lacodev.staffbungee.listeners;

import de.lacodev.staffbungee.managers.NotificationManager;
import de.lacodev.staffbungee.managers.WatchListManager;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ListenerWatchListActions implements Listener {
	
	@EventHandler
	public void onServerChange(ServerSwitchEvent e) {
		
		String uuid = e.getPlayer().getUniqueId().toString();
		
		if(WatchListManager.isWatching(uuid)) {
			
			if(e.getFrom() != null) {
				
				NotificationManager.sendServerChangeNotify(uuid, e.getFrom().getName(), e.getPlayer().getServer().getInfo().getName());
				
			} else {
				NotificationManager.sendLoginNotify(uuid, e.getPlayer().getServer().getInfo().getName());
			}
			
		}
		
	}

}
