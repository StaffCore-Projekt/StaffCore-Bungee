package de.lacodev.staffbungee.managers;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.NotificationSender;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WatchListManager {

    public static void addPlayer(String targetuuid, String teamuuid) {

        if (Main.getMySQL().isConnected()) {

            Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                @Override
                public void run() {

                    Main.getMySQL().update(
                        "INSERT INTO StaffCore_watchlistdb(UUID,TEAM_UUID,INSERTED_AT) VALUES ('" + targetuuid + "','" + teamuuid + "','" +
                            System.currentTimeMillis() + "')");
                    NotificationManager.sendNotify(NotificationSender.CONSOLE_NOTIFY, "WATCHLIST_ADD", targetuuid);

                }

            });

        }

    }

    public static void removePlayer(String targetuuid) {

        if (Main.getMySQL().isConnected()) {

            Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                @Override
                public void run() {

                    Main.getMySQL().update("DELETE FROM StaffCore_watchlistdb WHERE UUID = '" + targetuuid + "'");
                    NotificationManager.sendNotify(NotificationSender.CONSOLE_NOTIFY, "WATCHLIST_REMOVE", targetuuid);

                }

            });

        }

    }

    public static boolean isWatching(String uuid) {

        if (Main.getMySQL().isConnected()) {

            ResultSet rs = Main.getMySQL().query("SELECT UUID FROM StaffCore_watchlistdb WHERE UUID = '" + uuid + "'");

            try {
                if (rs.next()) {
                    return rs.getString("UUID") != null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

}
