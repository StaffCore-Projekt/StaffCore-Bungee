package de.lacodev.staffbungee.managers;

import de.lacodev.staffbungee.Main;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommandListManager {

    public static void add(String command) {
        if (Main.getMySQL().isConnected()) {

            Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                @Override
                public void run() {

                    Main.getMySQL().update("INSERT INTO StaffCore_commandlistdb(COMMAND) VALUES ('" + command + "')");

                }

            });

        }
    }

    public static void remove(String command) {
        if (Main.getMySQL().isConnected()) {

            Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                @Override
                public void run() {

                    Main.getMySQL().update("DELETE FROM StaffCore_commandlistdb WHERE COMMAND = '" + command + "'");

                }

            });

        }
    }

    public static boolean isListed(String command) {
        if (Main.getMySQL().isConnected()) {

            ResultSet rs = Main.getMySQL().query("SELECT COMMAND FROM StaffCore_commandlistdb WHERE COMMAND = '" + command + "'");

            try {
                if (rs.next()) {
                    return rs.getString("COMMAND") != null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

}
