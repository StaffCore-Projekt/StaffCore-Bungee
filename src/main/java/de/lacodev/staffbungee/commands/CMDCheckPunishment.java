package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.PunishmentType;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.objects.Punishment;
import de.lacodev.staffbungee.utils.BanLengthCalculator;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDCheckPunishment extends Command {

    public CMDCheckPunishment(String name) {
        super(name, null, "checkp");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                player.hasPermission(Main.getPermissionNotice("Permissions.Punishment.Check"))) {

                if (args.length == 2) {

                    if (args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("mute") || args[0].equalsIgnoreCase("warn") ||
                        args[0].equalsIgnoreCase("kick")) {

                        ProxyServer.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                            @Override
                            public void run() {
                                PunishmentType type = PunishmentType.valueOf(args[0].toUpperCase());

                                String uuid = PlayerManager.getUUIDByName(args[1]);

                                if (uuid != null) {
                                    ArrayList<Punishment> punishments;
                                    try {
                                        punishments = PlayerManager.getPunishments(uuid, type);

                                        if (!punishments.isEmpty()) {

                                            for (Punishment punish : punishments) {

                                                Date last_login = new Date(punish.getPunishment_start());
                                                SimpleDateFormat last_login_format =
                                                    new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
                                                String loginLastDate = last_login_format.format(last_login);

                                                TextComponent tc = new TextComponent();
                                                tc.setText("�8- �c" + punish.getType().toString() + " �8| �7#" + punish.getId() + "�e" +
                                                    punish.getReason() + " �8| �7" + loginLastDate);

                                                player.sendMessage(tc);

                                            }

                                        }
                                    } catch (SQLException e1) {
                                        e1.printStackTrace();
                                    }
                                } else {
                                    player.sendMessage(
                                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Cannot-find-player")));
                                }
                            }

                        });

                    } else {
                        player.sendMessage(new TextComponent(""));
                        player.sendMessage(new TextComponent(Main.getPrefix() + "�7/checkpunishment <ID>"));
                        player.sendMessage(new TextComponent(Main.getPrefix() + "�7/checkpunishment <Ban/Mute/Kick/Warn> <Player>"));
                        player.sendMessage(new TextComponent(""));
                    }

                } else if (args.length == 1) {
                    ProxyServer.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                        @Override
                        public void run() {

                            try {

                                Integer id = Integer.parseInt(args[0]);

                                try {

                                    if (PlayerManager.getPunishment(id) != null) {
                                        Punishment punish = PlayerManager.getPunishment(id);

                                        Date last_login = new Date(punish.getPunishment_start());
                                        SimpleDateFormat last_login_format =
                                            new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
                                        String loginLastDate = last_login_format.format(last_login);


                                        player.sendMessage(new TextComponent(""));
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + "�7Username �8� �c" + punish.getPunished()));
                                        player.sendMessage(new TextComponent(
                                            Main.getPrefix() + Main.getMSG("Messages.System.Punishment.Punished-By") +
                                                punish.getPunisher()));
                                        player.sendMessage(new TextComponent(""));
                                        player.sendMessage(new TextComponent(
                                            Main.getPrefix() + Main.getMSG("Messages.System.Punishment.Reason") + punish.getReason() +
                                                "�8(�7" + punish.getType().toString() + "�8)"));
                                        player.sendMessage(new TextComponent(""));
                                        player.sendMessage(new TextComponent(
                                            Main.getPrefix() + Main.getMSG("Messages.System.Punishment.Date") + loginLastDate));
                                        if (!punish.isUnbanned()) {
                                            if (punish.getPunishment_end() != -1) {
                                                player.sendMessage(new TextComponent(
                                                    Main.getPrefix() + Main.getMSG("Messages.System.Punishment.Remaining") +
                                                        BanLengthCalculator.calculate(
                                                            punish.getPunishment_end() - System.currentTimeMillis())));
                                            } else {
                                                player.sendMessage(new TextComponent(
                                                    Main.getPrefix() + Main.getMSG("Messages.System.Punishment.Remaining") +
                                                        BanLengthCalculator.calculate(punish.getPunishment_end())));
                                            }
                                        } else {

                                            Date last_login1 = new Date(punish.getUnbantime());
                                            SimpleDateFormat last_login_format1 =
                                                new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
                                            String loginLastDate1 = last_login_format1.format(last_login1);

                                            player.sendMessage(new TextComponent(""));
                                            if (punish.getType().equals(PunishmentType.BAN)) {
                                                player.sendMessage(new TextComponent(Main.getPrefix() + "�7Status �8� �cUNBANNED"));
                                                player.sendMessage(
                                                    new TextComponent(Main.getPrefix() + "�7Date of Unban �8� �c" + loginLastDate1));
                                                player.sendMessage(
                                                    new TextComponent(Main.getPrefix() + "�7Unbanned by �8� �c" + punish.getUnbanstaff()));
                                                player.sendMessage(new TextComponent(
                                                    Main.getPrefix() + "�7Unban-Reason �8� �c" + punish.getUnbanreason()));
                                            } else if (punish.getType().equals(PunishmentType.MUTE)) {
                                                player.sendMessage(new TextComponent(Main.getPrefix() + "�7Status �8� �cUNMUTED"));
                                                player.sendMessage(
                                                    new TextComponent(Main.getPrefix() + "�7Date of Unmute �8� �c" + loginLastDate1));
                                                player.sendMessage(
                                                    new TextComponent(Main.getPrefix() + "�7Unmuted by �8� �c" + punish.getUnbanstaff()));
                                                player.sendMessage(new TextComponent(
                                                    Main.getPrefix() + "�7Unmute-Reason �8� �c" + punish.getUnbanreason()));
                                            }

                                        }
                                        player.sendMessage(new TextComponent(""));
                                        player.sendMessage(new TextComponent(Main.getPrefix() + "�7Server �8� �c" + punish.getServer()));
                                        player.sendMessage(new TextComponent(""));
                                    }

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                            } catch (NumberFormatException e) {
                                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.NotValidID")));
                            }
                        }
                    });
                } else {
                    player.sendMessage(new TextComponent(""));
                    player.sendMessage(new TextComponent(Main.getPrefix() + "�7/checkpunishment <ID>"));
                    player.sendMessage(new TextComponent(Main.getPrefix() + "�7/checkpunishment <Ban/Mute/Kick/Warn> <Player>"));
                    player.sendMessage(new TextComponent(""));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                    .replace("%permission%", Main.getPermissionNotice("Permissions.Punishment.Check"))));
            }

        } else {
            CommandSender player = sender;

            if (args.length == 2) {

                if (args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("mute") || args[0].equalsIgnoreCase("warn") ||
                    args[0].equalsIgnoreCase("kick")) {

                    ProxyServer.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                        @Override
                        public void run() {
                            PunishmentType type = PunishmentType.valueOf(args[0].toUpperCase());

                            String uuid = PlayerManager.getUUIDByName(args[1]);

                            if (uuid != null) {
                                ArrayList<Punishment> punishments;
                                try {
                                    punishments = PlayerManager.getPunishments(uuid, type);

                                    if (!punishments.isEmpty()) {

                                        for (Punishment punish : punishments) {

                                            Date last_login = new Date(punish.getPunishment_start());
                                            SimpleDateFormat last_login_format =
                                                new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
                                            String loginLastDate = last_login_format.format(last_login);

                                            TextComponent tc = new TextComponent();
                                            tc.setText("�8- �c" + punish.getType().toString() + " �8| �7#" + punish.getId() + "�e" +
                                                punish.getReason() + " �8| �7" + loginLastDate);

                                            player.sendMessage(tc);

                                        }

                                    }
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                }
                            } else {
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Cannot-find-player")));
                            }
                        }

                    });

                } else {
                    player.sendMessage(new TextComponent(""));
                    player.sendMessage(new TextComponent(Main.getPrefix() + "�7/checkpunishment <ID>"));
                    player.sendMessage(new TextComponent(Main.getPrefix() + "�7/checkpunishment <Ban/Mute/Kick/Warn> <Player>"));
                    player.sendMessage(new TextComponent(""));
                }

            } else if (args.length == 1) {
                ProxyServer.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                    @Override
                    public void run() {

                        try {

                            Integer id = Integer.parseInt(args[0]);

                            try {

                                if (PlayerManager.getPunishment(id) != null) {
                                    Punishment punish = PlayerManager.getPunishment(id);

                                    Date last_login = new Date(punish.getPunishment_start());
                                    SimpleDateFormat last_login_format =
                                        new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
                                    String loginLastDate = last_login_format.format(last_login);


                                    player.sendMessage(new TextComponent(""));
                                    player.sendMessage(new TextComponent(Main.getPrefix() + "�7Username �8� �c" + punish.getPunished()));
                                    player.sendMessage(new TextComponent(
                                        Main.getPrefix() + Main.getMSG("Messages.System.Punishment.Punished-By") + punish.getPunisher()));
                                    player.sendMessage(new TextComponent(""));
                                    player.sendMessage(new TextComponent(
                                        Main.getPrefix() + Main.getMSG("Messages.System.Punishment.Reason") + punish.getReason() + "�8(�7" +
                                            punish.getType().toString() + "�8)"));
                                    player.sendMessage(new TextComponent(""));
                                    player.sendMessage(new TextComponent(
                                        Main.getPrefix() + Main.getMSG("Messages.System.Punishment.Date") + loginLastDate));
                                    if (!punish.isUnbanned()) {
                                        if (punish.getPunishment_end() != -1) {
                                            player.sendMessage(new TextComponent(
                                                Main.getPrefix() + Main.getMSG("Messages.System.Punishment.Remaining") +
                                                    BanLengthCalculator.calculate(
                                                        punish.getPunishment_end() - System.currentTimeMillis())));
                                        } else {
                                            player.sendMessage(new TextComponent(
                                                Main.getPrefix() + Main.getMSG("Messages.System.Punishment.Remaining") +
                                                    BanLengthCalculator.calculate(punish.getPunishment_end())));
                                        }
                                    } else {

                                        Date last_login1 = new Date(punish.getUnbantime());
                                        SimpleDateFormat last_login_format1 =
                                            new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
                                        String loginLastDate1 = last_login_format1.format(last_login1);

                                        player.sendMessage(new TextComponent(""));
                                        if (punish.getType().equals(PunishmentType.BAN)) {
                                            player.sendMessage(new TextComponent(Main.getPrefix() + "�7Status �8� �cUNBANNED"));
                                            player.sendMessage(
                                                new TextComponent(Main.getPrefix() + "�7Date of Unban �8� �c" + loginLastDate1));
                                            player.sendMessage(
                                                new TextComponent(Main.getPrefix() + "�7Unbanned by �8� �c" + punish.getUnbanstaff()));
                                            player.sendMessage(
                                                new TextComponent(Main.getPrefix() + "�7Unban-Reason �8� �c" + punish.getUnbanreason()));
                                        } else if (punish.getType().equals(PunishmentType.MUTE)) {
                                            player.sendMessage(new TextComponent(Main.getPrefix() + "�7Status �8� �cUNMUTED"));
                                            player.sendMessage(
                                                new TextComponent(Main.getPrefix() + "�7Date of Unmute �8� �c" + loginLastDate1));
                                            player.sendMessage(
                                                new TextComponent(Main.getPrefix() + "�7Unmuted by �8� �c" + punish.getUnbanstaff()));
                                            player.sendMessage(
                                                new TextComponent(Main.getPrefix() + "�7Unmute-Reason �8� �c" + punish.getUnbanreason()));
                                        }

                                    }
                                    player.sendMessage(new TextComponent(""));
                                    player.sendMessage(new TextComponent(Main.getPrefix() + "�7Server �8� �c" + punish.getServer()));
                                    player.sendMessage(new TextComponent(""));
                                }

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        } catch (NumberFormatException e) {
                            player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.NotValidID")));
                        }
                    }
                });
            } else {
                player.sendMessage(new TextComponent(""));
                player.sendMessage(new TextComponent(Main.getPrefix() + "�7/checkpunishment <ID>"));
                player.sendMessage(new TextComponent(Main.getPrefix() + "�7/checkpunishment <Ban/Mute/Kick/Warn> <Player>"));
                player.sendMessage(new TextComponent(""));
            }
        }

    }

}
