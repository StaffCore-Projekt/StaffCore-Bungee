package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.BlackListManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDBlackList extends Command {

    public CMDBlackList(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                player.hasPermission(Main.getPermissionNotice("Permissions.Blacklist.Change"))) {

                if (args.length == 2) {

                    if (args[0].equalsIgnoreCase("check")) {

                        if (BlackListManager.isBlacklisted(args[1])) {
                            player.sendMessage(new TextComponent(Main.getPrefix() +
                                Main.getMSG("Messages.System.Blacklist.Check-Status.Message").replace("%username%", args[1])
                                    .replace("%state%", Main.getMSG("Messages.System.Blacklist.Check-Status.Blacklisted"))));
                        } else {
                            player.sendMessage(new TextComponent(Main.getPrefix() +
                                Main.getMSG("Messages.System.Blacklist.Check-Status.Message").replace("%username%", args[1])
                                    .replace("%state%", Main.getMSG("Messages.System.Blacklist.Check-Status.Not-Blacklisted"))));
                        }

                    } else if (args[0].equalsIgnoreCase("add")) {

                        if (!BlackListManager.isBlacklisted(args[1])) {

                            BlackListManager.add(args[1], player.getUniqueId().toString());
                            player.sendMessage(new TextComponent(
                                Main.getPrefix() + Main.getMSG("Messages.System.Blacklist.Add.Success").replace("%username%", args[1])));

                        } else {
                            player.sendMessage(new TextComponent(Main.getPrefix() +
                                Main.getMSG("Messages.System.Blacklist.Add.Already-Blacklisted").replace("%username%", args[1])));
                        }

                    } else if (args[0].equalsIgnoreCase("remove")) {

                        if (BlackListManager.isBlacklisted(args[1])) {

                            BlackListManager.remove(args[1]);
                            player.sendMessage(new TextComponent(
                                Main.getPrefix() + Main.getMSG("Messages.System.Blacklist.Remove.Success").replace("%username%", args[1])));

                        } else {
                            player.sendMessage(new TextComponent(Main.getPrefix() +
                                Main.getMSG("Messages.System.Blacklist.Remove.Not-Blacklisted").replace("%username%", args[1])));
                        }

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + "§7/blacklist <check/add/remove> <Username>"));
                    }

                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + "§7/blacklist <check/add/remove> <Username>"));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                    .replace("%permission%", Main.getPermissionNotice("Permissions.Blacklist.Change"))));
            }

        } else {
            CommandSender player = sender;

            if (args.length == 2) {

                if (args[0].equalsIgnoreCase("check")) {

                    if (BlackListManager.isBlacklisted(args[1])) {
                        player.sendMessage(new TextComponent(
                            Main.getPrefix() + Main.getMSG("Messages.System.Blacklist.Check-Status.Message").replace("%username%", args[1])
                                .replace("%state%", Main.getMSG("Messages.System.Blacklist.Check-Status.Blacklisted"))));
                    } else {
                        player.sendMessage(new TextComponent(
                            Main.getPrefix() + Main.getMSG("Messages.System.Blacklist.Check-Status.Message").replace("%username%", args[1])
                                .replace("%state%", Main.getMSG("Messages.System.Blacklist.Check-Status.Not-Blacklisted"))));
                    }

                } else if (args[0].equalsIgnoreCase("add")) {

                    if (!BlackListManager.isBlacklisted(args[1])) {

                        BlackListManager.add(args[1], "Console");
                        player.sendMessage(new TextComponent(
                            Main.getPrefix() + Main.getMSG("Messages.System.Blacklist.Add.Success").replace("%username%", args[1])));

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() +
                            Main.getMSG("Messages.System.Blacklist.Add.Already-Blacklisted").replace("%username%", args[1])));
                    }

                } else if (args[0].equalsIgnoreCase("remove")) {

                    if (BlackListManager.isBlacklisted(args[1])) {

                        BlackListManager.remove(args[1]);
                        player.sendMessage(new TextComponent(
                            Main.getPrefix() + Main.getMSG("Messages.System.Blacklist.Remove.Success").replace("%username%", args[1])));

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() +
                            Main.getMSG("Messages.System.Blacklist.Remove.Not-Blacklisted").replace("%username%", args[1])));
                    }

                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + "§7/blacklist <check/add/remove> <Username>"));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + "§7/blacklist <check/add/remove> <Username>"));
            }
        }

    }

}
