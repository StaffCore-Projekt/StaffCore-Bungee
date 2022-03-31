package de.lacodev.staffbungee.managers;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.Settings;
import de.lacodev.staffbungee.objects.SettingsValue;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class SettingsManager {

    public static HashMap<Settings, SettingsValue> values = new HashMap<>();

    public static void generateDefaultSettings() throws SQLException {

        for (Settings setting : Settings.values()) {
            if (!existsSetting(setting)) {
                Main.getMySQL().update(
                    "INSERT INTO StaffCore_settings(SETTING,VALUE,EXPECTED_INPUT) VALUES ('" + setting.toString() + "','" +
                        setting.getStandard() + "','" + setting.getExpectedInput() + "')");
            }
        }

    }

    public static boolean existsSetting(Settings setting) throws SQLException {

        if (Main.getMySQL().isConnected()) {
            ResultSet rs = Main.getMySQL().query("SELECT SETTING FROM StaffCore_settings WHERE SETTING = '" + setting.toString() + "'");

            if (rs.next()) {
                return rs.getString("SETTING") != null;
            }
        }
        return false;

    }

    public static String getValue(Settings setting) {

        if (Main.getMySQL().isConnected()) {
            if (values.containsKey(setting)) {
                SettingsValue value = values.get(setting);
                if (value.getExpires() > System.currentTimeMillis()) {
                    return value.getValue();
                } else {
                    values.remove(setting);
                    return value.getValue();
                }
            } else {
                try {
                    if (existsSetting(setting)) {
                        ResultSet rs =
                            Main.getMySQL().query("SELECT VALUE FROM StaffCore_settings WHERE SETTING = '" + setting.toString() + "'");

                        try {
                            if (rs.next()) {
                                values.put(setting, new SettingsValue(rs.getString("VALUE")));
                                return rs.getString("VALUE");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return "§c§lUNKNOWN";

    }

    public static void updateValue(Settings setting, SettingsValue value) {

        if (Main.getMySQL().isConnected()) {
            if (values.containsKey(setting)) {

                values.remove(setting);
                values.put(setting, value);

                Main.getMySQL().update(
                    "UPDATE StaffCore_settings SET VALUE = '" + value.getValue() + "' WHERE SETTING = '" + setting.toString() + "'");

            } else {
                values.put(setting, value);

                Main.getMySQL().update(
                    "UPDATE StaffCore_settings SET VALUE = '" + value.getValue() + "' WHERE SETTING = '" + setting.toString() + "'");
            }
        }

    }

}
