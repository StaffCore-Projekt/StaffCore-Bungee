package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.PunishmentType;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.objects.Punishment;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDWarns extends Command {

    public CMDWarns(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (Main.getMySQL().isConnected()) {

                if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                    player.hasPermission(Main.getPermissionNotice("Permissions.Warns.See"))) {

                    if (args.length == 1) {

                        String uuid = PlayerManager.getUUIDByName(args[0]);

                        if (uuid != null) {

                            Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                                @Override
                                public void run() {

                                    player.sendMessage(new TextComponent(""));
                                    player.sendMessage(new TextComponent(Main.getPrefix() +
                                        Main.getMSG("Messages.Warn-System.Warns.Player-Info").replace("%target%", args[0])
                                            .replace("%warns%", "" + PlayerManager.getWarns(uuid))));
                                    player.sendMessage(new TextComponent(""));
                                    try {
                                        ArrayList<Punishment> warns = PlayerManager.getPunishments(uuid, PunishmentType.WARN);

                                        if (!warns.isEmpty()) {
                                            for (Punishment warn : warns) {

                                                Date last_login = new Date(warn.getPunishment_start());
                                                SimpleDateFormat last_login_format = new SimpleDateFormat("yyyy.MM.dd, HH:mm");
                                                String loginLastDate = last_login_format.format(last_login);

                                                TextComponent tc = new TextComponent();
                                                tc.setText("§8- §c" + warn.getType().toString() + " §8| §e" + warn.getReason() + " §8| §7" +
                                                    loginLastDate);
                                                tc.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text(
                                                    "§7#" + warn.getId() + "\n\n§7Reason §8» §c" + warn.getReason() + "\n§7Server §8» §c" +
                                                        warn.getServer()
                                                        + "\n\n§7Warned by §8» §c" + warn.getPunisher() + "\n\n§7Warned at §8» §c" +
                                                        loginLastDate)));

                                                player.sendMessage(tc);

                                            }
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });

                        } else {
                            player.sendMessage(
                                new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warns.Never-Joined")));
                        }

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warns.Usage")));
                    }

                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                        .replace("%permission%", Main.getPermissionNotice("Permissions.Warns.See"))));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
            }

        } else {

            CommandSender player = sender;

            if (Main.getMySQL().isConnected()) {

                if (args.length == 1) {

                    String uuid = PlayerManager.getUUIDByName(args[0]);

                    if (uuid != null) {

                        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                            @Override
                            public void run() {

                                player.sendMessage(new TextComponent(""));
                                player.sendMessage(new TextComponent(Main.getPrefix() +
                                    Main.getMSG("Messages.Warn-System.Warns.Player-Info").replace("%target%", args[0])
                                        .replace("%warns%", "" + PlayerManager.getWarns(uuid))));
                                player.sendMessage(new TextComponent(""));
                                try {
                                    ArrayList<Punishment> warns = PlayerManager.getPunishments(uuid, PunishmentType.WARN);

                                    if (!warns.isEmpty()) {
                                        for (Punishment warn : warns) {

                                            Date last_login = new Date(warn.getPunishment_start());
                                            SimpleDateFormat last_login_format = new SimpleDateFormat("yyyy.MM.dd, HH:mm");
                                            String loginLastDate = last_login_format.format(last_login);

                                            TextComponent tc = new TextComponent();
                                            tc.setText("§8- §c" + warn.getType().toString() + " §8| §e" + warn.getReason() + " §8| §7" +
                                                loginLastDate);

                                            player.sendMessage(tc);

                                        }
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                            }

                        });

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warns.Never-Joined")));
                    }

                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warns.Usage")));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
            }

        }

    }

}
