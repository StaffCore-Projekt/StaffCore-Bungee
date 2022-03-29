package de.lacodev.staffbungee.handlers;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.MigrationConfig;
import de.lacodev.staffbungee.objects.Migration;
import de.lacodev.staffbungee.utils.UUIDFetcher;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class MigrationHandler {

    private MigrationConfig config;
    private HashMap<MigrationConfig, Migration> cache = new HashMap<>();

    public MigrationHandler(MigrationConfig config) {
        this.config = config;

        if (config.equals(MigrationConfig.CONFIG_LITEBANS)) {
            cache.put(config, new Migration(config,
                Main.getInstance().getConfig().getString("Migration-Helper." + config.getConfigPrefix() + ".Table-Prefix")
                , "bans", "mutes", "kicks", "warnings"));

            Main.getInstance().getProxy().getConsole()
                .sendMessage(new TextComponent("§cSystem §8» §8MigrationHandler initialised (§c" + config.toString() + "§8)"));
        } else if (config.equals(MigrationConfig.CONFIG_ADVANCEDBAN)) {
            cache.put(config, new Migration(config, "", "Punishments", "Punishments", "PunishmentHistory"));

            Main.getInstance().getProxy().getConsole()
                .sendMessage(new TextComponent("§cSystem §8» §8MigrationHandler initialised (§c" + config.toString() + "§8)"));
        }
    }

    public void migrate() {
        Migration migration = cache.get(config);

        Main.getInstance().getProxy().getConsole()
            .sendMessage(new TextComponent("§cSystem §8» §8Migration (§c" + config.toString() + "§8) is starting..."));

        ProxyServer.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {

            @Override
            public void run() {

                try {

                    if (Main.getInstance().getConfig().getBoolean("Migration-Helper.Configs." + config.toString())) {
                        if (config.equals(MigrationConfig.CONFIG_LITEBANS)) {

                            if (migration.isBansReady()) {

                                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                                    "§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Bans is now processing..."));

                                ResultSet rs =
                                    Main.getMySQL().query("SELECT * FROM " + (migration.getTablePrefix() + migration.getTableBans()));

                                int count = 0;

                                while (rs.next()) {
                                    count++;
                                    if (rs.getInt("active") == 1) {
                                        Main.getMySQL().update("INSERT INTO StaffCore_bansdb(BANNED_UUID,TEAM_UUID,REASON,BAN_END) VALUES "
                                            + "('" + rs.getString("uuid") + "','" +
                                            rs.getString("banned_by_uuid").replace("CONSOLE", "Console") + "','" + rs.getString("reason") +
                                            "','" + rs.getLong("until") + "')");
                                    }
                                    Main.getMySQL().update(
                                        "INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,REASON,BAN_START,BAN_END) VALUES ('BAN','" +
                                            rs.getString("uuid") + "',"
                                            + "'" + rs.getString("banned_by_uuid").replace("CONSOLE", "Console") + "','" +
                                            rs.getString("reason") + "','" + rs.getLong("time") + "','" + rs.getLong("until") + "')");
                                }

                                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                                    "§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Bans completed! (§7Total §8- §c" + count +
                                        "§8)"));
                            } else {
                                Main.getInstance().getProxy().getConsole().sendMessage(
                                    new TextComponent("§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Bans was skipped."));
                            }

                            if (migration.isMutesReady()) {

                                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                                    "§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Mutes is now processing..."));

                                ResultSet rs =
                                    Main.getMySQL().query("SELECT * FROM " + (migration.getTablePrefix() + migration.getTableMutes()));

                                int count = 0;

                                while (rs.next()) {
                                    count++;
                                    if (rs.getInt("active") == 1) {
                                        Main.getMySQL().update("INSERT INTO StaffCore_mutesdb(MUTED_UUID,TEAM_UUID,REASON,MUTE_END) VALUES "
                                            + "('" + rs.getString("uuid") + "','" +
                                            rs.getString("banned_by_uuid").replace("CONSOLE", "Console") + "','" + rs.getString("reason") +
                                            "','" + rs.getLong("until") + "')");
                                    }
                                    Main.getMySQL().update(
                                        "INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,REASON,BAN_START,BAN_END) VALUES ('MUTE','" +
                                            rs.getString("uuid") + "',"
                                            + "'" + rs.getString("banned_by_uuid").replace("CONSOLE", "Console") + "','" +
                                            rs.getString("reason") + "','" + rs.getLong("time") + "','" + rs.getLong("until") + "')");
                                }

                                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                                    "§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Mutes completed! (§7Total §8- §c" + count +
                                        "§8)"));
                            } else {
                                Main.getInstance().getProxy().getConsole().sendMessage(
                                    new TextComponent("§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Mutes was skipped."));
                            }

                            if (migration.isKicksReady()) {

                                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                                    "§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Kicks is now processing..."));

                                ResultSet rs =
                                    Main.getMySQL().query("SELECT * FROM " + (migration.getTablePrefix() + migration.getTableKicks()));

                                int count = 0;

                                while (rs.next()) {
                                    count++;
                                    Main.getMySQL().update(
                                        "INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,REASON,BAN_START) VALUES ('KICK','" +
                                            rs.getString("uuid") + "',"
                                            + "'" + rs.getString("banned_by_uuid").replace("CONSOLE", "Console") + "','" +
                                            rs.getString("reason") + "','" + rs.getLong("time") + "')");
                                }

                                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                                    "§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Kicks completed! (§7Total §8- §c" + count +
                                        "§8)"));
                            } else {
                                Main.getInstance().getProxy().getConsole().sendMessage(
                                    new TextComponent("§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Kicks was skipped."));
                            }

                            if (migration.isWarnsReady()) {

                                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                                    "§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Warns is now processing..."));

                                ResultSet rs =
                                    Main.getMySQL().query("SELECT * FROM " + (migration.getTablePrefix() + migration.getTableWarns()));

                                int count = 0;

                                while (rs.next()) {
                                    count++;
                                    Main.getMySQL().update("INSERT INTO StaffCore_warnsdb(WARNED_UUID,TEAM_UUID,REASON,WARNED_AT) VALUES "
                                        + "('" + rs.getString("uuid") + "','" +
                                        rs.getString("banned_by_uuid").replace("CONSOLE", "Console") + "','" + rs.getString("reason") +
                                        "','" + rs.getLong("time") + "')");
                                    Main.getMySQL().update(
                                        "INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,REASON,BAN_START) VALUES ('WARN','" +
                                            rs.getString("uuid") + "',"
                                            + "'" + rs.getString("banned_by_uuid").replace("CONSOLE", "Console") + "','" +
                                            rs.getString("reason") + "','" + rs.getLong("time") + "')");
                                }

                                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                                    "§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Warns completed! (§7Total §8- §c" + count +
                                        "§8)"));
                            } else {
                                Main.getInstance().getProxy().getConsole().sendMessage(
                                    new TextComponent("§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Warns was skipped."));
                            }
                            Main.getInstance().getProxy().getConsole().sendMessage(
                                new TextComponent("§cSystem §8» §8Migration (§c" + config.toString() + "§8) was §asuccessful"));
                            Configuration configcfg = Main.getInstance().getConfig();

                            configcfg.set("Migration-Helper.Configs." + config.toString(), false);

                            ConfigurationProvider.getProvider(YamlConfiguration.class)
                                .save(configcfg, new File(Main.getInstance().getDataFolder().getPath(), "config.yml"));
                            Main.getInstance().getProxy().getConsole().sendMessage(
                                new TextComponent("§cSystem §8» §c§lTHIS CONFIG IS NOW DISABLED UNTIL YOU MANUALLY ACTIVATE IT AGAIN!"));
                        } else if (config.equals(MigrationConfig.CONFIG_ADVANCEDBAN)) {

                            if (migration.isBansReady()) {

                                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                                    "§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Bans is now processing..."));

                                ResultSet rs = Main.getMySQL().query(
                                    "SELECT * FROM " + (migration.getTablePrefix() + migration.getTableBans()) +
                                        " WHERE punishmentType = 'BAN'");

                                int count = 0;

                                while (rs.next()) {
                                    count++;

                                    String tuuid;
                                    if (rs.getString("operator").matches("CONSOLE")) {
                                        tuuid = "Console";
                                    } else {
                                        tuuid = UUIDFetcher.getUUID(rs.getString("operator"));
                                    }

                                    Main.getMySQL().update("INSERT INTO StaffCore_bansdb(BANNED_UUID,TEAM_UUID,REASON,BAN_END) VALUES "
                                        + "('" + UUIDFetcher.getUUID(rs.getString("name")) + "','" + tuuid + "','" +
                                        rs.getString("reason") + "','" + rs.getLong("end") + "')");
                                    Main.getMySQL().update(
                                        "INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,REASON,BAN_START,BAN_END) VALUES ('BAN','" +
                                            UUIDFetcher.getUUID(rs.getString("name")) + "',"
                                            + "'" + tuuid + "','" + rs.getString("reason") + "','" + rs.getLong("start") + "','" +
                                            rs.getLong("end") + "')");
                                }

                                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                                    "§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Bans completed! (§7Total §8- §c" + count +
                                        "§8)"));
                            } else {
                                Main.getInstance().getProxy().getConsole().sendMessage(
                                    new TextComponent("§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Bans was skipped."));
                            }

                            if (migration.isMutesReady()) {

                                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                                    "§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Mutes is now processing..."));

                                ResultSet rs = Main.getMySQL().query(
                                    "SELECT * FROM " + (migration.getTablePrefix() + migration.getTableMutes()) +
                                        " WHERE punishmentType = 'MUTE'");

                                int count = 0;

                                while (rs.next()) {
                                    count++;

                                    String tuuid;
                                    if (rs.getString("operator").matches("CONSOLE")) {
                                        tuuid = "Console";
                                    } else {
                                        tuuid = UUIDFetcher.getUUID(rs.getString("operator"));
                                    }

                                    Main.getMySQL().update("INSERT INTO StaffCore_mutesdb(MUTED_UUID,TEAM_UUID,REASON,MUTE_END) VALUES "
                                        + "('" + UUIDFetcher.getUUID(rs.getString("name")) + "','" + tuuid + "','" +
                                        rs.getString("reason") + "','" + rs.getLong("end") + "')");
                                    Main.getMySQL().update(
                                        "INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,REASON,BAN_START,BAN_END) VALUES ('MUTE','" +
                                            UUIDFetcher.getUUID(rs.getString("name")) + "',"
                                            + "'" + tuuid + "','" + rs.getString("reason") + "','" + rs.getLong("start") + "','" +
                                            rs.getLong("end") + "')");
                                }

                                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                                    "§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Mutes completed! (§7Total §8- §c" + count +
                                        "§8)"));
                            } else {
                                Main.getInstance().getProxy().getConsole().sendMessage(
                                    new TextComponent("§cSystem §8» §8Migration (§c" + config.toString() + "§8) - Mutes was skipped."));
                            }

                            if (migration.isHistoryReady()) {

                                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                                    "§cSystem §8» §8Migration (§c" + config.toString() + "§8) - History is now processing..."));

                                ResultSet rs =
                                    Main.getMySQL().query("SELECT * FROM " + (migration.getTablePrefix() + migration.getTableMutes()));

                                int count = 0;

                                while (rs.next()) {
                                    count++;

                                    String tuuid;
                                    if (rs.getString("operator").matches("CONSOLE")) {
                                        tuuid = "Console";
                                    } else {
                                        tuuid = UUIDFetcher.getUUID(rs.getString("operator"));
                                    }

                                    if (rs.getString("punishmentType").matches("WARNING")) {
                                        Main.getMySQL()
                                            .update("INSERT INTO StaffCore_warnsdb(WARNED_UUID,TEAM_UUID,REASON,WARNED_AT) VALUES "
                                                + "('" + UUIDFetcher.getUUID(rs.getString("name")) + "','" + tuuid + "','" +
                                                rs.getString("reason") + "','" + rs.getLong("start") + "')");
                                        Main.getMySQL().update(
                                            "INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,REASON,BAN_START) VALUES ('WARN','" +
                                                UUIDFetcher.getUUID(rs.getString("name")) + "',"
                                                + "'" + tuuid + "','" + rs.getString("reason") + "','" + rs.getLong("start") + "')");
                                    } else {
                                        Main.getMySQL().update(
                                            "INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,REASON,BAN_START,BAN_END) VALUES ('" +
                                                rs.getString("punishmentType") + "','" + UUIDFetcher.getUUID(rs.getString("name")) + "',"
                                                + "'" + tuuid + "','" + rs.getString("reason") + "','" + rs.getLong("start") + "','" +
                                                rs.getLong("end") + "')");
                                    }
                                }

                                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                                    "§cSystem §8» §8Migration (§c" + config.toString() + "§8) - History completed! (§7Total §8- §c" +
                                        count + "§8)"));
                            } else {
                                Main.getInstance().getProxy().getConsole().sendMessage(
                                    new TextComponent("§cSystem §8» §8Migration (§c" + config.toString() + "§8) - History was skipped."));
                            }

                            Main.getInstance().getProxy().getConsole().sendMessage(
                                new TextComponent("§cSystem §8» §8Migration (§c" + config.toString() + "§8) was §asuccessful"));
                            Configuration configcfg = Main.getInstance().getConfig();

                            configcfg.set("Migration-Helper.Configs." + config.toString(), false);

                            ConfigurationProvider.getProvider(YamlConfiguration.class)
                                .save(configcfg, new File(Main.getInstance().getDataFolder().getPath(), "config.yml"));
                            Main.getInstance().getProxy().getConsole().sendMessage(
                                new TextComponent("§cSystem §8» §c§lTHIS CONFIG IS NOW DISABLED UNTIL YOU MANUALLY ACTIVATE IT AGAIN!"));
                        }
                    } else {
                        Main.getInstance().getProxy().getConsole()
                            .sendMessage(new TextComponent("§cSystem §8» §8Migration (§c" + config.toString() + "§8) is §c§lDISABLED"));
                        Main.getInstance().getProxy().getConsole()
                            .sendMessage(new TextComponent("§cSystem §8» §8You can reactivate this Migration in the config.yml"));
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    Main.getInstance().getProxy().getConsole()
                        .sendMessage(new TextComponent("§cSystem §8» §8Migration (§c" + config.toString() + "§8) failed."));
                } catch (IOException e) {
                    e.printStackTrace();
                    Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(
                        "§cSystem §8» §8Migration (§c" + config.toString() + "§8) could not be disabled! Please don't use it again!"));
                }

            }

        });
    }

}
