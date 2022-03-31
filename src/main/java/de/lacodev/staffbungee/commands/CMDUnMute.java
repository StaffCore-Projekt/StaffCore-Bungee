package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.MuteManager;
import de.lacodev.staffbungee.managers.PlayerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDUnMute extends Command {

    public CMDUnMute(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                player.hasPermission(Main.getPermissionNotice("Permissions.UnMute.Use"))) {

                if (Main.getInstance().getConfig().getBoolean("Unmute.Force-Reason")) {

                    if (args.length == 2) {

                        if (!args[1].equalsIgnoreCase("-s")) {

                            String uuid = PlayerManager.getUUIDByName(args[0]);

                            if (uuid != null) {

                                if (MuteManager.isMuted(uuid)) {

                                    MuteManager.unmute(uuid, player.getUniqueId().toString(), args[1]);
                                    player.sendMessage(
                                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Success")));

                                } else {
                                    player.sendMessage(
                                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Not-Muted")));
                                }

                            } else {
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Cannot-find-player")));
                            }

                        } else {
                            player.sendMessage(
                                new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Unmute.Force-Reason")));
                        }

                    } else if (args.length == 3) {

                        if (args[1].equalsIgnoreCase("-s")) {

                            if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                                player.hasPermission(Main.getPermissionNotice("Permissions.UnMute.Silent"))) {

                                String uuid = PlayerManager.getUUIDByName(args[0]);

                                if (uuid != null) {

                                    if (MuteManager.isMuted(uuid)) {

                                        MuteManager.silentunmute(uuid, player.getUniqueId().toString(), args[2]);
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Success")));

                                    } else {
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Not-Muted")));
                                    }

                                } else {
                                    player.sendMessage(new TextComponent(
                                        Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Cannot-find-player")));
                                }

                            } else {
                                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                                    .replace("%permission%", Main.getPermissionNotice("Permissions.UnMute.Silent"))));
                            }

                        }

                    } else {
                        player.sendMessage(
                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Usage") + " (-s) <Reason>"));
                    }

                } else {
                    if (args.length == 1) {

                        String uuid = PlayerManager.getUUIDByName(args[0]);

                        if (uuid != null) {

                            if (MuteManager.isMuted(uuid)) {

                                MuteManager.unmute(uuid, player.getUniqueId().toString(), "Unmute");
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Success")));

                            } else {
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Not-Muted")));
                            }

                        } else {
                            player.sendMessage(
                                new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Cannot-find-player")));
                        }

                    } else if (args.length == 2) {

                        if (args[1].equalsIgnoreCase("-s")) {

                            if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                                player.hasPermission(Main.getPermissionNotice("Permissions.UnMute.Silent"))) {

                                String uuid = PlayerManager.getUUIDByName(args[0]);

                                if (uuid != null) {

                                    if (MuteManager.isMuted(uuid)) {

                                        MuteManager.silentunmute(uuid, player.getUniqueId().toString(), "Unmute");
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Success")));

                                    } else {
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Not-Muted")));
                                    }

                                } else {
                                    player.sendMessage(new TextComponent(
                                        Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Cannot-find-player")));
                                }

                            } else {
                                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                                    .replace("%permission%", Main.getPermissionNotice("Permissions.UnMute.Silent"))));
                            }

                        }

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Usage") + " -s"));
                    }
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                    .replace("%permission%", Main.getPermissionNotice("Permissions.UnMute.Use"))));
            }

        } else {

            CommandSender player = sender;

            if (Main.getInstance().getConfig().getBoolean("Unmute.Force-Reason")) {

                if (args.length == 2) {

                    if (!args[1].equalsIgnoreCase("-s")) {

                        String uuid = PlayerManager.getUUIDByName(args[0]);

                        if (uuid != null) {

                            if (MuteManager.isMuted(uuid)) {

                                MuteManager.unmute(uuid, "Console", args[1]);
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Success")));

                            } else {
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Not-Muted")));
                            }

                        } else {
                            player.sendMessage(
                                new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Cannot-find-player")));
                        }

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Unmute.Force-Reason")));
                    }

                } else if (args.length == 3) {

                    if (args[1].equalsIgnoreCase("-s")) {

                        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                            player.hasPermission(Main.getPermissionNotice("Permissions.UnMute.Silent"))) {

                            String uuid = PlayerManager.getUUIDByName(args[0]);

                            if (uuid != null) {

                                if (MuteManager.isMuted(uuid)) {

                                    MuteManager.silentunmute(uuid, "Console", args[2]);
                                    player.sendMessage(
                                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Success")));

                                } else {
                                    player.sendMessage(
                                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Not-Muted")));
                                }

                            } else {
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Cannot-find-player")));
                            }

                        } else {
                            player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                                .replace("%permission%", Main.getPermissionNotice("Permissions.UnMute.Silent"))));
                        }

                    }

                } else {
                    player.sendMessage(
                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Usage") + " (-s) <Reason>"));
                }

            } else {
                if (args.length == 1) {

                    String uuid = PlayerManager.getUUIDByName(args[0]);

                    if (uuid != null) {

                        if (MuteManager.isMuted(uuid)) {

                            MuteManager.unmute(uuid, "Console", "Unmute");
                            player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Success")));

                        } else {
                            player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Not-Muted")));
                        }

                    } else {
                        player.sendMessage(
                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Cannot-find-player")));
                    }

                } else if (args.length == 2) {

                    if (args[1].equalsIgnoreCase("-s")) {

                        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                            player.hasPermission(Main.getPermissionNotice("Permissions.UnMute.Silent"))) {

                            String uuid = PlayerManager.getUUIDByName(args[0]);

                            if (uuid != null) {

                                if (MuteManager.isMuted(uuid)) {

                                    MuteManager.silentunmute(uuid, "Console", "Unmute");
                                    player.sendMessage(
                                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Success")));

                                } else {
                                    player.sendMessage(
                                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Not-Muted")));
                                }

                            } else {
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Cannot-find-player")));
                            }

                        } else {
                            player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                                .replace("%permission%", Main.getPermissionNotice("Permissions.UnMute.Silent"))));
                        }

                    }

                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Usage") + " -s"));
                }
            }

        }

    }

}
