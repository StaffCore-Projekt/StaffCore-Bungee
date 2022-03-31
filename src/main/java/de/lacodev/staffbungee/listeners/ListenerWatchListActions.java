package de.lacodev.staffbungee.listeners;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.NotificationManager;
import de.lacodev.staffbungee.managers.WatchListManager;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ListenerWatchListActions implements Listener {

    @EventHandler
    public void onServerChange(ServerSwitchEvent e) {

        String uuid = e.getPlayer().getUniqueId().toString();

        if (WatchListManager.isWatching(uuid)) {

            if (e.getFrom() != null) {

                NotificationManager.sendServerChangeNotify(uuid, e.getFrom().getName(), e.getPlayer().getServer().getInfo().getName());
                Main.getMySQL().update(
                    "INSERT INTO StaffCore_activitydb(type,uuid,target,message,reg_date,priority) VALUES ('SERVER_SWITCHED','" + uuid + "',"
                        + "'" + e.getPlayer().getServer().getInfo().getName() + "','%player% switched to the Server %target%','" +
                        System.currentTimeMillis() + "','1')");

            } else {
                NotificationManager.sendLoginNotify(uuid, e.getPlayer().getServer().getInfo().getName());
                Main.getMySQL().update(
                    "INSERT INTO StaffCore_activitydb(type,uuid,target,message,reg_date,priority) VALUES ('SERVER_CONNECT','" + uuid + "',"
                        + "'" + e.getPlayer().getServer().getInfo().getName() + "','%player% connected to %target%','" +
                        System.currentTimeMillis() + "','1')");
            }

        }
    }

}
