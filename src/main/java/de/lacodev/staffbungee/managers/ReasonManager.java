package de.lacodev.staffbungee.managers;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.ReasonType;
import de.lacodev.staffbungee.objects.Reason;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReasonManager {

    public static void addReason(ReasonType type, String name, String timeunit, int length) {
        switch (type) {
            case BAN:

                long btime = 0;

                if (timeunit.toLowerCase().matches("d")) {
                    btime = 1000 * 60 * 60 * 24;
                } else if (timeunit.toLowerCase().matches("h")) {
                    btime = 1000 * 60 * 60;
                } else if (timeunit.toLowerCase().matches("m")) {
                    btime = 1000 * 60;
                } else if (timeunit.toLowerCase().matches("perma")) {
                    btime = 1;
                }

                Main.getMySQL().update(
                    "INSERT INTO StaffCore_reasonsdb(TYPE,NAME,BAN_LENGTH) VALUES ('BAN','" + name + "','" + (length * btime) + "')");

                break;
            case MUTE:

                long mtime = 0;

                if (timeunit.toLowerCase().matches("d")) {
                    mtime = 1000 * 60 * 60 * 24;
                } else if (timeunit.toLowerCase().matches("h")) {
                    mtime = 1000 * 60 * 60;
                } else if (timeunit.toLowerCase().matches("m")) {
                    mtime = 1000 * 60;
                } else if (timeunit.toLowerCase().matches("perma")) {
                    mtime = 1;
                }

                Main.getMySQL().update(
                    "INSERT INTO StaffCore_reasonsdb(TYPE,NAME,BAN_LENGTH) VALUES ('MUTE','" + name + "','" + (mtime * length) + "')");

                break;
            default:
                break;
        }
    }

    public static void addReason(ReasonType type, String name) {
        Main.getMySQL().update("INSERT INTO StaffCore_reportreasonsdb(TYPE,NAME) VALUES ('" + type.toString() + "','" + name + "')");
    }

    public static Reason getReasonById(ReasonType type, Integer id) {
        if (Main.getMySQL().isConnected()) {
            if (type.equals(ReasonType.REPORT)) {
                ResultSet rs = Main.getMySQL().query("SELECT * FROM StaffCore_reportreasonsdb WHERE id = '" + id + "'");

                try {
                    if (rs.next()) {
                        return new Reason(type, id, rs.getString("NAME"), 0L, false);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                ResultSet rs = Main.getMySQL().query("SELECT * FROM StaffCore_reasonsdb WHERE id = '" + id + "'");

                try {
                    if (rs.next()) {
                        return new Reason(type, id, rs.getString("NAME"), rs.getLong("BAN_LENGTH"), rs.getBoolean("ADMIN_BAN"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return null;
        }
        return null;
    }

    public static Reason getReasonByName(ReasonType type, String name) {
        if (Main.getMySQL().isConnected()) {
            if (type.equals(ReasonType.REPORT)) {
                ResultSet rs = Main.getMySQL()
                    .query("SELECT * FROM StaffCore_reportreasonsdb WHERE NAME = '" + name + "' AND TYPE = '" + type.toString() + "'");

                try {
                    if (rs.next()) {
                        return new Reason(type, rs.getInt("id"), name, 0L, false);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                ResultSet rs = Main.getMySQL()
                    .query("SELECT * FROM StaffCore_reasonsdb WHERE NAME = '" + name + "' AND TYPE = '" + type.toString() + "'");

                try {
                    if (rs.next()) {
                        return new Reason(type, rs.getInt("id"), name, rs.getLong("BAN_LENGTH"), rs.getBoolean("ADMIN_BAN"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return null;
        }
        return null;
    }

    public static boolean existsReason(ReasonType type, String name) {
        if (Main.getMySQL().isConnected()) {
            if (type.equals(ReasonType.REPORT)) {
                ResultSet rs = Main.getMySQL()
                    .query("SELECT * FROM StaffCore_reportreasonsdb WHERE TYPE = '" + type.toString() + "' AND NAME = '" + name + "'");

                try {
                    if (rs.next()) {
                        return true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                ResultSet rs = Main.getMySQL()
                    .query("SELECT * FROM StaffCore_reasonsdb WHERE TYPE = '" + type.toString() + "' AND NAME = '" + name + "'");

                try {
                    if (rs.next()) {
                        return true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static ArrayList<Reason> getReasons() {
        ArrayList<Reason> reasons = new ArrayList<>();

        if (Main.getMySQL().isConnected()) {
            ResultSet rs = Main.getMySQL().query("SELECT * FROM StaffCore_reasonsdb WHERE TYPE = 'BAN' OR TYPE = 'MUTE' ORDER BY TYPE");

            try {
                while (rs.next()) {
                    reasons.add(new Reason(ReasonType.valueOf(rs.getString("TYPE")), rs.getInt("id"), rs.getString("NAME"),
                        rs.getLong("BAN_LENGTH"), rs.getBoolean("ADMIN_BAN")));
                }
                return reasons;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            return reasons;
        }
        return reasons;
    }

    public static ArrayList<Reason> getReasons(ReasonType type) {
        if (type.equals(ReasonType.REPORT)) {
            ArrayList<Reason> reasons = new ArrayList<>();

            if (Main.getMySQL().isConnected()) {
                ResultSet rs =
                    Main.getMySQL().query("SELECT * FROM StaffCore_reportreasonsdb WHERE TYPE = '" + type.toString() + "' ORDER BY id");

                try {
                    while (rs.next()) {
                        reasons.add(new Reason(type, rs.getInt("id"), rs.getString("NAME"), 0L, false));
                    }
                    return reasons;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                return reasons;
            }
            return reasons;
        } else {
            ArrayList<Reason> reasons = new ArrayList<>();

            if (Main.getMySQL().isConnected()) {
                ResultSet rs =
                    Main.getMySQL().query("SELECT * FROM StaffCore_reasonsdb WHERE TYPE = '" + type.toString() + "' ORDER BY id");

                try {
                    while (rs.next()) {
                        reasons.add(
                            new Reason(type, rs.getInt("id"), rs.getString("NAME"), rs.getLong("BAN_LENGTH"), rs.getBoolean("ADMIN_BAN")));
                    }
                    return reasons;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                return reasons;
            }
            return reasons;
        }
    }

    public static void removeReason(ReasonType type, Integer id) {
        if (Main.getMySQL().isConnected()) {
            if (getReasonById(type, id) != null) {
                if (type.equals(ReasonType.REPORT)) {
                    Main.getMySQL().update("DELETE FROM StaffCore_reportreasonsdb WHERE id = '" + id + "'");
                } else {
                    Main.getMySQL().update("DELETE FROM StaffCore_reasonsdb WHERE id = '" + id + "'");
                }
            }
        }
    }

}
