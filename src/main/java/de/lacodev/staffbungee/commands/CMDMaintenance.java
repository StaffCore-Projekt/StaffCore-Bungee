package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.Settings;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.managers.SettingsManager;
import de.lacodev.staffbungee.objects.SettingsValue;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDMaintenance extends Command {

    public CMDMaintenance(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (Main.getMySQL().isConnected()) {

                if (args.length == 0) {

                    if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                        player.hasPermission(Main.getPermissionNotice("Permissions.Maintenance.Info"))) {

                        if (Main.getMaintenanceHandler().isMaintenance()) {
                            player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Info.Message")
                                .replace("%state%", Main.getMSG("Messages.Maintenance.Info.State.Active"))));
                        } else {
                            player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Info.Message")
                                .replace("%state%", Main.getMSG("Messages.Maintenance.Info.State.InActive"))));
                        }

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                            .replace("%permission%", Main.getPermissionNotice("Permissions.Maintenance.Info"))));
                    }

                } else if (args.length == 1) {

                    if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                        player.hasPermission(Main.getPermissionNotice("Permissions.Maintenance.Use"))) {

                        if (args[0].equalsIgnoreCase("on")) {

                            if (!Main.getMaintenanceHandler().isMaintenance()) {

                                SettingsManager.updateValue(Settings.MAINTENANCE_ENABLE, new SettingsValue("TRUE"));

                                for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {

                                    if (!all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) &&
                                        !all.hasPermission(Main.getPermissionNotice("Permissions.Maintenance.Bypass"))) {

                                        all.disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&',
                                            SettingsManager.getValue(Settings.MAINTENANCE_TEXT_LINE1)) + "\n" +
                                            ChatColor.translateAlternateColorCodes('&',
                                                SettingsManager.getValue(Settings.MAINTENANCE_TEXT_LINE2))));

                                    }

                                    Main.getSessionsHandler().stopSession(all.getUniqueId().toString());
                                    PlayerManager.setPlayerToOffline(all);
                                }

                                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Activated")));

                            } else {

                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Already-Active")));

                            }

                        } else if (args[0].equalsIgnoreCase("off")) {

                            if (Main.getMaintenanceHandler().isMaintenance()) {

                                SettingsManager.updateValue(Settings.MAINTENANCE_ENABLE, new SettingsValue("FALSE"));
                                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Deactivated")));

                                for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {

                                    Main.getSessionsHandler().startSession(all.getUniqueId().toString());
                                    PlayerManager.createPlayerData(all);
                                }

                            } else {

                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Already-InActive")));

                            }

                        } else {
                            player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Usage")));
                        }

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                            .replace("%permission%", Main.getPermissionNotice("Permissions.Maintenance.Use"))));
                    }

                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Usage")));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
            }

        } else {

            if (args.length == 0) {

                if (Main.getMaintenanceHandler().isMaintenance()) {
                    sender.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Info.Message")
                        .replace("%state%", Main.getMSG("Messages.Maintenance.Info.State.Active"))));
                } else {
                    sender.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Info.Message")
                        .replace("%state%", Main.getMSG("Messages.Maintenance.Info.State.InActive"))));
                }

            } else if (args.length == 1) {

                if (args[0].equalsIgnoreCase("on")) {

                    if (!Main.getMaintenanceHandler().isMaintenance()) {

                        SettingsManager.updateValue(Settings.MAINTENANCE_ENABLE, new SettingsValue("TRUE"));
                        sender.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Activated")));

                        for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {

                            if (!all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) &&
                                !all.hasPermission(Main.getPermissionNotice("Permissions.Maintenance.Bypass"))) {

                                all.disconnect(new TextComponent(
                                    ChatColor.translateAlternateColorCodes('&', SettingsManager.getValue(Settings.MAINTENANCE_TEXT_LINE1)) +
                                        "\n" + ChatColor.translateAlternateColorCodes('&',
                                        SettingsManager.getValue(Settings.MAINTENANCE_TEXT_LINE2))));

                            }

                            Main.getSessionsHandler().stopSession(all.getUniqueId().toString());
                            PlayerManager.setPlayerToOffline(all);
                        }

                    } else {

                        sender.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Already-Active")));

                    }

                } else if (args[0].equalsIgnoreCase("off")) {

                    if (Main.getMaintenanceHandler().isMaintenance()) {

                        SettingsManager.updateValue(Settings.MAINTENANCE_ENABLE, new SettingsValue("FALSE"));
                        sender.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Deactivated")));

                        for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {

                            Main.getSessionsHandler().startSession(all.getUniqueId().toString());
                            PlayerManager.createPlayerData(all);
                        }

                    } else {

                        sender.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Already-InActive")));

                    }

                } else {
                    sender.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Usage")));
                }

            } else {
                sender.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Maintenance.Usage")));
            }

        }

    }

}
