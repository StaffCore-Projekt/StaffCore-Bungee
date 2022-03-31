package de.lacodev.staffbungee.listeners;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.Settings;
import de.lacodev.staffbungee.managers.SettingsManager;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ListenerServerPing implements Listener {

    @EventHandler
    public void onPing(ProxyPingEvent e) {

        if (Main.getMaintenanceHandler().isMaintenance()) {
            ServerPing ping = e.getResponse();
            ServerPing.Players players = ping.getPlayers();
            ServerPing.Protocol version = ping.getVersion();
            version.setName(ChatColor.translateAlternateColorCodes('&', SettingsManager.getValue(Settings.MAINTENANCE_VERSIONTEXT)));
            version.setProtocol(2);
            players.setOnline(1);
            players.setMax(1);
            if (Boolean.valueOf(SettingsManager.getValue(Settings.ADVERTISMENT_ENABLE)).equals(Boolean.TRUE)) {
                players.setSample(new ServerPing.PlayerInfo[] {new ServerPing.PlayerInfo("§a", UUID.randomUUID()),
                    new ServerPing.PlayerInfo("§7This Server is operated by §cStaffCore-Bungee", UUID.randomUUID()),
                    new ServerPing.PlayerInfo("§a", UUID.randomUUID())});
            }
            ping.setPlayers(players);
            ping.setVersion(version);
            ping.setDescriptionComponent(new TextComponent(
                ChatColor.translateAlternateColorCodes('&', SettingsManager.getValue(Settings.MAINTENANCE_TEXT_LINE1)) + "\n" +
                    ChatColor.translateAlternateColorCodes('&', SettingsManager.getValue(Settings.MAINTENANCE_TEXT_LINE2))));
        } else if (Boolean.valueOf(SettingsManager.getValue(Settings.MOTD_ENABLE)).equals(Boolean.TRUE)) {
            ServerPing ping = e.getResponse();
            ServerPing.Players players = ping.getPlayers();
            ServerPing.Protocol version = ping.getVersion();
            version.setProtocol(version.getProtocol());

            if (Boolean.valueOf(SettingsManager.getValue(Settings.MOTD_FAKEPLAYERS_ENABLE)).equals(Boolean.TRUE)) {
                players.setOnline(Integer.valueOf(SettingsManager.getValue(Settings.MOTD_FAKEPLAYERS_COUNT)));
            }
            players.setMax(Integer.valueOf(SettingsManager.getValue(Settings.MOTD_MAXPLAYER_COUNT)));
            if (Boolean.valueOf(SettingsManager.getValue(Settings.ADVERTISMENT_ENABLE)).equals(Boolean.TRUE)) {
                players.setSample(new ServerPing.PlayerInfo[] {new ServerPing.PlayerInfo("§a", UUID.randomUUID()),
                    new ServerPing.PlayerInfo("§7This Server is operated by §cStaffCore-Bungee", UUID.randomUUID()),
                    new ServerPing.PlayerInfo("§a", UUID.randomUUID())});
            }
            ping.setPlayers(players);
            ping.setVersion(version);
            ping.setDescriptionComponent(new TextComponent(
                ChatColor.translateAlternateColorCodes('&', SettingsManager.getValue(Settings.MOTD_TEXT_LINE1)) + "\n" +
                    ChatColor.translateAlternateColorCodes('&', SettingsManager.getValue(Settings.MOTD_TEXT_LINE2))));
        }
    }

}
