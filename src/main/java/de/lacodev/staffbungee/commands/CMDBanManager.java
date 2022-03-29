package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.ReasonType;
import de.lacodev.staffbungee.managers.ReasonManager;
import de.lacodev.staffbungee.objects.Reason;
import de.lacodev.staffbungee.utils.ReasonLengthCalculator;
import java.util.ArrayList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDBanManager extends Command {

    public CMDBanManager(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (Main.getMySQL().isConnected()) {

                if (args.length >= 4) {

                    if (args[0].equalsIgnoreCase("addreason")) {

                        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                            player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.addreason"))) {

                            String timeunit = args[2].substring(args[2].length() - 1);
                            String reason = args[3];

                            for (int i = 4; i < args.length; i++) {
                                reason = reason + " " + args[i];
                            }

                            if (timeunit.equalsIgnoreCase("d") || timeunit.equalsIgnoreCase("h") || timeunit.equalsIgnoreCase("m") ||
                                args[2].equalsIgnoreCase("perma")) {

                                if (!ReasonManager.existsReason(ReasonType.valueOf(args[1].toUpperCase()), reason)) {

                                    if (args[2].equalsIgnoreCase("perma")) {

                                        ReasonManager.addReason(ReasonType.valueOf(args[1].toUpperCase()), reason, args[2], -1);
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.Add-Reason.Success")));

                                    } else {

                                        ReasonManager.addReason(ReasonType.valueOf(args[1].toUpperCase()), reason, timeunit,
                                            Integer.valueOf(args[2].substring(0, args[2].length() - 1)));
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.Add-Reason.Success")));

                                    }

                                } else {
                                    player.sendMessage(new TextComponent(
                                        Main.getPrefix() + Main.getMSG("Messages.BanManager.Add-Reason.Already-Existing")));
                                }

                            }

                        } else {
                            player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                                .replace("%permission%", Main.getPermissionNotice("Permissions.BanManager.addreason"))));
                        }

                    } else {
                        player.sendMessage(new TextComponent(""));
                        player.sendMessage(new TextComponent(Main.getPrefix() + "§8[§cBanManager§8]"));
                        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                            player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.addreason"))) {
                            player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                                "/banmanager addreason <Ban/Mute> <Length: 30d / §7Perma> <Reason>"));
                        }
                        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                            player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.removereason"))) {
                            player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                                "/banmanager removereason <ReasonID>"));
                        }
                        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                            player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.listreasons"))) {
                            player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                                "/banmanager listreasons"));
                        }
                        player.sendMessage(new TextComponent(""));
                    }
                } else if (args.length == 2) {

                    if (args[0].equalsIgnoreCase("removereason")) {

                        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                            player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.removereason"))) {
                            try {

                                Integer id = Integer.parseInt(args[1]);
                                Reason reason = ReasonManager.getReasonById(ReasonType.BAN, id);

                                if (reason != null) {

                                    ReasonManager.removeReason(ReasonType.BAN, id);
                                    player.sendMessage(
                                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.Remove-Reason.Success")));

                                } else {
                                    player.sendMessage(
                                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.Remove-Reason.Not-Exists")));
                                }

                            } catch (NumberFormatException e) {
                                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.NotValidID")));
                            }
                        } else {
                            player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                                .replace("%permission%", Main.getPermissionNotice("Permissions.BanManager.removereason"))));
                        }

                    } else {
                        player.sendMessage(new TextComponent(""));
                        player.sendMessage(new TextComponent(Main.getPrefix() + "§8[§cBanManager§8]"));
                        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                            player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.addreason"))) {
                            player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                                "/banmanager addreason <Ban/Mute> <Length: 30d / §7Perma> <Reason>"));
                        }
                        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                            player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.removereason"))) {
                            player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                                "/banmanager removereason <ReasonID>"));
                        }
                        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                            player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.listreasons"))) {
                            player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                                "/banmanager listreasons"));
                        }
                        player.sendMessage(new TextComponent(""));
                    }

                } else if (args.length == 1) {

                    if (args[0].equalsIgnoreCase("listreasons")) {

                        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                            player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.listreasons"))) {

                            ArrayList<Reason> reasons = ReasonManager.getReasons();

                            if (!reasons.isEmpty()) {

                                player.sendMessage(new TextComponent(""));
                                for (Reason reason : reasons) {
                                    player.sendMessage(new TextComponent(
                                        "§8- §c" + reason.getType().toString() + " §8| §7#" + reason.getId() + " §e" + reason.getName() +
                                            " §8| " + ReasonLengthCalculator.calculate(reason.getLength())));
                                }
                                player.sendMessage(new TextComponent(""));

                            } else {
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.List-Reasons.No-Reasons-Found")));
                            }

                        } else {
                            player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                                .replace("%permission%", Main.getPermissionNotice("Permissions.BanManager.listreasons"))));
                        }

                    } else {
                        player.sendMessage(new TextComponent(""));
                        player.sendMessage(new TextComponent(Main.getPrefix() + "§8[§cBanManager§8]"));
                        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                            player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.addreason"))) {
                            player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                                "/banmanager addreason <Ban/Mute> <Length: 30d / §7Perma> <Reason>"));
                        }
                        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                            player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.removereason"))) {
                            player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                                "/banmanager removereason <ReasonID>"));
                        }
                        if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                            player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.listreasons"))) {
                            player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                                "/banmanager listreasons"));
                        }
                        player.sendMessage(new TextComponent(""));
                    }

                } else {
                    player.sendMessage(new TextComponent(""));
                    player.sendMessage(new TextComponent(Main.getPrefix() + "§8[§cBanManager§8]"));
                    if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                        player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.addreason"))) {
                        player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                            "/banmanager addreason <Ban/Mute> <Length: 30d / §7Perma> <Reason>"));
                    }
                    if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                        player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.removereason"))) {
                        player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                            "/banmanager removereason <ReasonID>"));
                    }
                    if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                        player.hasPermission(Main.getPermissionNotice("Permissions.BanManager.listreasons"))) {
                        player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                            "/banmanager listreasons"));
                    }
                    player.sendMessage(new TextComponent(""));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
            }

        } else {

            CommandSender player = sender;

            if (Main.getMySQL().isConnected()) {

                if (args.length >= 4) {

                    if (args[0].equalsIgnoreCase("addreason")) {

                        String timeunit = args[2].substring(args[2].length() - 1);
                        String reason = args[3];

                        for (int i = 4; i < args.length; i++) {
                            reason = reason + " " + args[i];
                        }

                        if (timeunit.equalsIgnoreCase("d") || timeunit.equalsIgnoreCase("h") || timeunit.equalsIgnoreCase("m") ||
                            args[2].equalsIgnoreCase("perma")) {

                            if (!ReasonManager.existsReason(ReasonType.valueOf(args[1].toUpperCase()), reason)) {

                                if (args[2].equalsIgnoreCase("perma")) {

                                    ReasonManager.addReason(ReasonType.valueOf(args[1].toUpperCase()), reason, args[2], -1);
                                    player.sendMessage(
                                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.Add-Reason.Success")));

                                } else {

                                    ReasonManager.addReason(ReasonType.valueOf(args[1].toUpperCase()), reason, timeunit,
                                        Integer.valueOf(args[2].substring(0, args[2].length() - 1)));
                                    player.sendMessage(
                                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.Add-Reason.Success")));

                                }

                            } else {
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.Add-Reason.Already-Existing")));
                            }

                        }

                    } else {
                        player.sendMessage(new TextComponent(""));
                        player.sendMessage(new TextComponent(Main.getPrefix() + "§8[§cBanManager§8]"));
                        player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                            "/banmanager addreason <Ban/Mute> <Length: 30d / §7Perma> <Reason>"));
                        player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                            "/banmanager removereason <ReasonID>"));
                        player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                            "/banmanager listreasons"));
                        player.sendMessage(new TextComponent(""));
                    }
                } else if (args.length == 2) {

                    if (args[0].equalsIgnoreCase("removereason")) {

                        try {

                            Integer id = Integer.parseInt(args[1]);
                            Reason reason = ReasonManager.getReasonById(ReasonType.BAN, id);

                            if (reason != null) {

                                ReasonManager.removeReason(ReasonType.BAN, id);
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.Remove-Reason.Success")));

                            } else {
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.Remove-Reason.Not-Exists")));
                            }

                        } catch (NumberFormatException e) {
                            player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.NotValidID")));
                        }

                    } else {
                        player.sendMessage(new TextComponent(""));
                        player.sendMessage(new TextComponent(Main.getPrefix() + "§8[§cBanManager§8]"));
                        player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                            "/banmanager addreason <Ban/Mute> <Length: 30d / §7Perma> <Reason>"));
                        player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                            "/banmanager removereason <ReasonID>"));
                        player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                            "/banmanager listreasons"));
                        player.sendMessage(new TextComponent(""));
                    }

                } else if (args.length == 1) {

                    if (args[0].equalsIgnoreCase("listreasons")) {

                        ArrayList<Reason> reasons = ReasonManager.getReasons();

                        if (!reasons.isEmpty()) {

                            player.sendMessage(new TextComponent(""));
                            for (Reason reason : reasons) {
                                player.sendMessage(new TextComponent(
                                    "§8- §c" + reason.getType().toString() + " §8| §7#" + reason.getId() + " §e" + reason.getName() +
                                        " §8| " + ReasonLengthCalculator.calculate(reason.getLength())));
                            }
                            player.sendMessage(new TextComponent(""));

                        } else {
                            player.sendMessage(
                                new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.List-Reasons.No-Reasons-Found")));
                        }
                    } else {
                        player.sendMessage(new TextComponent(""));
                        player.sendMessage(new TextComponent(Main.getPrefix() + "§8[§cBanManager§8]"));
                        player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                            "/banmanager addreason <Ban/Mute> <Length: 30d / §7Perma> <Reason>"));
                        player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                            "/banmanager removereason <ReasonID>"));
                        player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                            "/banmanager listreasons"));
                        player.sendMessage(new TextComponent(""));
                    }

                } else {
                    player.sendMessage(new TextComponent(""));
                    player.sendMessage(new TextComponent(Main.getPrefix() + "§8[§cBanManager§8]"));
                    player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                        "/banmanager addreason <Ban/Mute> <Length: 30d / §7Perma> <Reason>"));
                    player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                        "/banmanager removereason <ReasonID>"));
                    player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY +
                        "/banmanager listreasons"));
                    player.sendMessage(new TextComponent(""));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
            }

        }

    }

}
