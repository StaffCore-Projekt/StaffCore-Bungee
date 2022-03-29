package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.PlayerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDResetPlayer extends Command {

    public CMDResetPlayer(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                player.hasPermission(Main.getPermissionNotice("Permissions.System.ResetPlayer"))) {

                if (args.length == 2) {

                    String uuid = PlayerManager.getUUIDByName(args[1]);

                    if (uuid != null) {

                        if (args[0].equalsIgnoreCase("reports")) {

                            Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                                @Override
                                public void run() {

                                    if (Main.getMySQL().isConnected()) {

                                        Main.getMySQL().update("DELETE FROM StaffCore_reportsdb WHERE UUID = '" + uuid + "'");
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.PlayerReset.Success")));

                                    }

                                }

                            });

                        } else if (args[0].equalsIgnoreCase("p-history")) {

                            Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                                @Override
                                public void run() {

                                    if (Main.getMySQL().isConnected()) {

                                        Main.getMySQL().update("DELETE FROM StaffCore_punishmentsdb WHERE UUID = '" + uuid + "'");
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.PlayerReset.Success")));

                                    }

                                }

                            });

                        } else if (args[0].equalsIgnoreCase("sessions")) {

                            Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                                @Override
                                public void run() {

                                    if (Main.getMySQL().isConnected()) {

                                        Main.getMySQL().update("DELETE FROM StaffCore_sessionsdb WHERE UUID = '" + uuid + "'");
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.PlayerReset.Success")));

                                    }

                                }

                            });

                        } else if (args[0].equalsIgnoreCase("messages")) {

                            Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                                @Override
                                public void run() {

                                    if (Main.getMySQL().isConnected()) {

                                        Main.getMySQL().update("DELETE FROM StaffCore_reportsdb WHERE UUID = '" + uuid + "'");
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.PlayerReset.Success")));

                                    }

                                }

                            });

                        } else if (args[0].equalsIgnoreCase("all")) {

                            PlayerManager.reset(uuid);
                            player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.PlayerReset.Success")));

                            if (ProxyServer.getInstance().getPlayer(args[1]) != null) {
                                ProxyServer.getInstance().getPlayer(args[1])
                                    .disconnect(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.PlayerReset.KickMessage")));
                            }

                        } else {
                            player.sendMessage(new TextComponent(
                                Main.getPrefix() + "§7/resetplayer <Reports / P-History / Sessions / §7Messages / ALL> <Player>"));
                        }

                    }

                } else {
                    player.sendMessage(new TextComponent(
                        Main.getPrefix() + "§7/resetplayer <Reports / P-History / Sessions / §7Messages / ALL> <Player>"));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                    .replace("%permission%", Main.getPermissionNotice("Permissions.System.ResetPlayer"))));
            }

        }

    }

}
