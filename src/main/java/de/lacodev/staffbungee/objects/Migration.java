package de.lacodev.staffbungee.objects;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.MigrationConfig;
import de.lacodev.staffbungee.mysql.MySQLConnect;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Migration {

    private MigrationConfig config;
    private String tableprefix;
    private String tableBans;
    private String tableMutes;
    private String tableHistory;
    private String tableKicks;
    private String tableWarns;

    public Migration(MigrationConfig config, String tableprefix, String tableBans, String tableMutes,
                     String tableHistory) {
        super();
        this.config = config;
        this.tableprefix = tableprefix;
        this.tableBans = tableBans;
        this.tableMutes = tableMutes;
        this.tableHistory = tableHistory;
    }

    public Migration(MigrationConfig config, String tableprefix, String tableBans, String tableMutes, String tableKicks,
                     String tableWarns) {
        super();
        this.config = config;
        this.tableprefix = tableprefix;
        this.tableBans = tableBans;
        this.tableMutes = tableMutes;
        this.tableKicks = tableKicks;
        this.tableWarns = tableWarns;
    }

    public MigrationConfig getConfig() {
        return config;
    }

    public String getTablePrefix() {
        return tableprefix;
    }

    public String getTableBans() {
        return tableBans;
    }

    public String getTableMutes() {
        return tableMutes;
    }

    public String getTableHistory() {
        return tableHistory;
    }

    public String getTableKicks() {
        return tableKicks;
    }

    public String getTableWarns() {
        return tableWarns;
    }

    public boolean isBansReady() throws SQLException {

        ResultSet rs = Main.getMySQL().query(
            "SELECT * FROM information_schema.tables WHERE table_schema = '" + MySQLConnect.DATABASE + "' AND table_name = '" +
                (getTablePrefix() + getTableBans()) + "' LIMIT 1");

        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isMutesReady() throws SQLException {

        ResultSet rs = Main.getMySQL().query(
            "SELECT * FROM information_schema.tables WHERE table_schema = '" + MySQLConnect.DATABASE + "' AND table_name = '" +
                (getTablePrefix() + getTableMutes()) + "' LIMIT 1");

        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isHistoryReady() throws SQLException {

        ResultSet rs = Main.getMySQL().query(
            "SELECT * FROM information_schema.tables WHERE table_schema = '" + MySQLConnect.DATABASE + "' AND table_name = '" +
                (getTablePrefix() + getTableHistory()) + "' LIMIT 1");

        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isKicksReady() throws SQLException {

        ResultSet rs = Main.getMySQL().query(
            "SELECT * FROM information_schema.tables WHERE table_schema = '" + MySQLConnect.DATABASE + "' AND table_name = '" +
                (getTablePrefix() + getTableKicks()) + "' LIMIT 1");

        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isWarnsReady() throws SQLException {

        ResultSet rs = Main.getMySQL().query(
            "SELECT * FROM information_schema.tables WHERE table_schema = '" + MySQLConnect.DATABASE + "' AND table_name = '" +
                (getTablePrefix() + getTableWarns()) + "' LIMIT 1");

        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }
}
