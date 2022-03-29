package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.BanManager;
import de.lacodev.staffbungee.managers.PlayerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDBanIp extends Command {

    public CMDBanIp(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                player.hasPermission(Main.getPermissionNotice("Permissions.IpBan.Use"))) {

                if (args.length == 1) {

                    String uuid = PlayerManager.getUUIDByName(args[0]);

                    if (uuid != null) {

                        if (!BanManager.isIpBanned(PlayerManager.getLastKnownIp(uuid))) {

                            BanManager.banIp(uuid, player.getUniqueId().toString());

                        } else {
                            player.sendMessage(new TextComponent(
                                Main.getPrefix() + Main.getMSG("Messages.Ban-System.IP-Ban.Already-banned").replace("%target%", args[0])));
                        }

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.IP-Ban.Target-Offline")));
                    }

                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.IP-Ban.Usage")));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                    .replace("%permission%", Main.getPermissionNotice("Permissions.IpBan.Use"))));
            }

        } else {
            CommandSender player = sender;

            if (args.length == 1) {

                String uuid = PlayerManager.getUUIDByName(args[0]);

                if (uuid != null) {

                    if (!BanManager.isIpBanned(PlayerManager.getLastKnownIp(uuid))) {

                        BanManager.banIp(uuid, "Console");

                    } else {
                        player.sendMessage(new TextComponent(
                            Main.getPrefix() + Main.getMSG("Messages.Ban-System.IP-Ban.Already-banned").replace("%target%", args[0])));
                    }

                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.IP-Ban.Target-Offline")));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.IP-Ban.Usage")));
            }
        }

    }

}
