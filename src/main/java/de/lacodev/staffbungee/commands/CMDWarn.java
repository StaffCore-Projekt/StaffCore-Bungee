package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.managers.WarnManager;
import de.lacodev.staffbungee.utils.StringGenerator;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDWarn extends Command {

    public CMDWarn(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (Main.getMySQL().isConnected()) {

                if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                    player.hasPermission(Main.getPermissionNotice("Permissions.Warn.Use"))) {

                    if (args.length >= 2) {

                        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

                        if (target != null) {

                            if (target != player) {

                                String reason = "";
                                for (int i = 1; i < args.length; i++) {
                                    reason += args[i] + " ";
                                }

                                WarnManager.warn(target.getUniqueId().toString(), player.getUniqueId().toString(),
                                    StringGenerator.getMySQLFriendly(reason));

                                Main.getMySQL().update(
                                    "INSERT INTO StaffCore_activitydb(type,uuid,target,message,reg_date,priority) VALUES ('PLAYER_WARNED','" +
                                        player.getUniqueId().toString() + "',"
                                        + "'" + PlayerManager.getUsernamebyUUID(target.getUniqueId().toString()) +
                                        "','%player% warned the player %target%','" + System.currentTimeMillis() + "','2')");

                                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warn.Created")));

                            } else {
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warn.Cant-Warn-Self")));
                            }

                        } else {
                            player.sendMessage(
                                new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warn.Target-Offline")));
                        }

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warn.Usage")));
                    }

                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                        .replace("%permission%", Main.getPermissionNotice("Permissions.Warn.Use"))));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
            }

        } else {

            CommandSender player = sender;

            if (Main.getMySQL().isConnected()) {

                if (args.length >= 2) {

                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

                    if (target != null) {

                        if (target != player) {

                            String reason = "";
                            for (int i = 1; i < args.length; i++) {
                                reason += args[i] + " ";
                            }

                            WarnManager.warn(target.getUniqueId().toString(), "Console", reason);
                            player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warn.Created")));

                        } else {
                            player.sendMessage(
                                new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warn.Cant-Warn-Self")));
                        }

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warn.Target-Offline")));
                    }

                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warn.Usage")));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
            }

        }

    }

}
