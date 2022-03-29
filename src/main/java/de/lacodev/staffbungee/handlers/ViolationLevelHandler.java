package de.lacodev.staffbungee.handlers;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.Violation;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViolationLevelHandler {

    public static boolean hasVL(String uuid) {
        if (Main.getMySQL().isConnected()) {
            ResultSet rs = Main.getMySQL().query("SELECT UUID FROM StaffCore_violationleveldb WHERE UUID = '" + uuid + "'");

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

    public static boolean hasLastReset(String uuid) {
        if (Main.getMySQL().isConnected()) {
            ResultSet rs = Main.getMySQL().query("SELECT UUID FROM StaffCore_violationresetdb WHERE UUID = '" + uuid + "'");

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

    public static long getNextReset(String uuid) {
        if (Main.getMySQL().isConnected()) {
            ResultSet rs = Main.getMySQL().query("SELECT NEXT_RESET FROM StaffCore_violationresetdb WHERE UUID = '" + uuid + "'");

            try {
                if (rs.next()) {
                    return rs.getLong("NEXT_RESET");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static void reset(String uuid) {
        if (System.currentTimeMillis() >= getNextReset(uuid)) {

            long next = System.currentTimeMillis() + (1000 * 60 * 60 * 24);

            if ((getVL(uuid) - Main.getInstance().getConfig().getInt("ViolationLevelSystem.Remove-Points.Daily")) < 0) {
                setVL(uuid, 0);
            } else {
                setVL(uuid, getVL(uuid) - Main.getInstance().getConfig().getInt("ViolationLevelSystem.Remove-Points.Daily"));
            }

            Main.getMySQL().update("UPDATE StaffCore_violationresetdb SET NEXT_RESET = '" + next + "' WHERE UUID = '" + uuid + "'");
        }
    }

    public static void addVL(String uuid, Violation vl) {
        Main.getMySQL()
            .update("UPDATE StaffCore_violationleveldb SET VL = '" + (getVL(uuid) + vl.getVL(vl)) + "' WHERE UUID = '" + uuid + "'");
    }

    public static void setVL(String uuid, int vl) {
        Main.getMySQL().update("UPDATE StaffCore_violationleveldb SET VL = '" + vl + "' WHERE UUID = '" + uuid + "'");
    }

    public static int getVL(String uuid) {

        ResultSet rs = Main.getMySQL().query("SELECT VL FROM StaffCore_violationleveldb WHERE UUID = '" + uuid + "'");

        try {
            if (rs.next()) {
                return rs.getInt("VL");
            }
        } catch (SQLException e) {

        }
        return 0;
    }

    public static long calculateLength(String uuid, long length, int vl) {

        if (length == -1) {
            return -1;
        } else if (vl >= 120) {
            return -1;
        } else {
            long day = (1000 * 60 * 60 * 24);
            long calc = day * vl;
            return length + calc;
        }

    }
}
