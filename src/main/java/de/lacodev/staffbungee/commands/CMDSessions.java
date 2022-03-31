package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.objects.Session;
import de.lacodev.staffbungee.utils.SessionLengthCalculator;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDSessions extends Command {

    public CMDSessions(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (Main.getMySQL().isConnected()) {

                if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                    player.hasPermission(Main.getPermissionNotice("Permissions.SessionsLookup.Use"))) {

                    if (args.length == 1) {

                        if (args[0].startsWith("#SC21")) {
                            // Further information about a specific session

                            Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                                @Override
                                public void run() {

                                    try {

                                        Session session = Main.getSessionsHandler().get(args[0]);

                                        Date session_start = new Date(session.getStart());
                                        SimpleDateFormat session_start_format =
                                            new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
                                        String sessionstartDate = session_start_format.format(session_start);

                                        Date session_end = new Date(session.getEnd());
                                        SimpleDateFormat session_end_format =
                                            new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
                                        String sessionendDate = session_end_format.format(session_end);

                                        player.sendMessage(new TextComponent(""));
                                        player.sendMessage(new TextComponent(Main.getPrefix() + "§7SessionID §8» §e" + args[0]));
                                        player.sendMessage(new TextComponent(""));
                                        player.sendMessage(new TextComponent(
                                            Main.getPrefix() + "§7Username §8» §e" + PlayerManager.getUsernamebyUUID(session.getUuid())));
                                        player.sendMessage(new TextComponent(
                                            Main.getPrefix() + Main.getMSG("Messages.Sessions.Prefix.MC-Version") +
                                                session.getMcversion()));
                                        player.sendMessage(new TextComponent(""));
                                        player.sendMessage(new TextComponent(Main.getPrefix() + "§7IP §8» §e" + session.getIp(player)));
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + "§7Virtual-Host §8» §e" + session.getVirtualHost()));
                                        player.sendMessage(new TextComponent(""));
                                        player.sendMessage(new TextComponent(
                                            Main.getPrefix() + Main.getMSG("Messages.Sessions.Prefix.Start") + sessionstartDate));
                                        player.sendMessage(new TextComponent(
                                            Main.getPrefix() + Main.getMSG("Messages.Sessions.Prefix.End") + sessionendDate));
                                        player.sendMessage(new TextComponent(""));
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
                        } else {
                            // Overview of the latest 10 sessions
                            // ID | Length

                            String uuid = PlayerManager.getUUIDByName(args[0]);

                            if (PlayerManager.existsPlayerData(uuid)) {

                                player.sendMessage(new TextComponent(
                                    Main.getPrefix() + Main.getMSG("Messages.Sessions.Title").replace("%player%", args[0])));
                                player.sendMessage(new TextComponent(""));

                                Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                                    @Override
                                    public void run() {

                                        try {
                                            ArrayList<Session> sessions = PlayerManager.getSessions(uuid);

                                            if (!sessions.isEmpty()) {

                                                for (Session session : sessions) {

                                                    TextComponent clickaction1 = new TextComponent();
                                                    clickaction1.setText(Main.getPrefix() + "§7" + session.getUniqueid() + "§8 | " +
                                                        SessionLengthCalculator.calculate(session.getStart(), session.getEnd()));
                                                    clickaction1.setClickEvent(
                                                        new ClickEvent(Action.SUGGEST_COMMAND, "/sessions " + session.getUniqueid()));
                                                    clickaction1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                        new Text(Main.getMSG("Messages.Sessions.Hover-Text"))));

                                                    player.sendMessage(clickaction1);

                                                }
                                                player.sendMessage(new TextComponent(""));

                                            } else {
                                                player.sendMessage(new TextComponent(
                                                    Main.getPrefix() + Main.getMSG("Messages.Sessions.No-Sessions-Found")));
                                            }

                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            player.sendMessage(
                                                new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Sessions.No-Sessions-Found")));
                                        }

                                    }

                                });

                            } else {
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.No-Player-Found")));
                            }
                        }

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Sessions.Usage")));
                    }

                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                        .replace("%permission%", Main.getPermissionNotice("Permissions.SessionsLookup.Use"))));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
            }

        } else {

            CommandSender player = sender;

            if (args.length == 1) {

                if (args[0].startsWith("#SC21")) {
                    // Further information about a specific session

                    Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                        @Override
                        public void run() {

                            try {

                                Session session = Main.getSessionsHandler().get(args[0]);

                                Date session_start = new Date(session.getStart());
                                SimpleDateFormat session_start_format =
                                    new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
                                String sessionstartDate = session_start_format.format(session_start);

                                Date session_end = new Date(session.getEnd());
                                SimpleDateFormat session_end_format =
                                    new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
                                String sessionendDate = session_end_format.format(session_end);

                                player.sendMessage(new TextComponent(""));
                                player.sendMessage(new TextComponent(Main.getPrefix() + "§7SessionID §8» §e" + args[0]));
                                player.sendMessage(new TextComponent(""));
                                player.sendMessage(new TextComponent(
                                    Main.getPrefix() + "§7Username §8» §e" + PlayerManager.getUsernamebyUUID(session.getUuid())));
                                player.sendMessage(new TextComponent(
                                    Main.getPrefix() + Main.getMSG("Messages.Sessions.Prefix.MC-Version") + session.getMcversion()));
                                player.sendMessage(new TextComponent(""));
                                player.sendMessage(new TextComponent(Main.getPrefix() + "§7IP §8» §e" + session.getIp()));
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + "§7Virtual-Host §8» §e" + session.getVirtualHost()));
                                player.sendMessage(new TextComponent(""));
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Sessions.Prefix.Start") + sessionstartDate));
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Sessions.Prefix.End") + sessionendDate));
                                player.sendMessage(new TextComponent(""));
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        }

                    });
                } else {
                    // Overview of the latest 10 sessions
                    // ID | Length

                    String uuid = PlayerManager.getUUIDByName(args[0]);

                    if (PlayerManager.existsPlayerData(uuid)) {

                        player.sendMessage(
                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Sessions.Title").replace("%player%", args[0])));
                        player.sendMessage(new TextComponent(""));

                        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                            @Override
                            public void run() {

                                try {
                                    ArrayList<Session> sessions = PlayerManager.getSessions(uuid);

                                    if (!sessions.isEmpty()) {

                                        for (Session session : sessions) {

                                            TextComponent clickaction1 = new TextComponent();
                                            clickaction1.setText(Main.getPrefix() + "§7" + session.getUniqueid() + "§8 | " +
                                                SessionLengthCalculator.calculate(session.getStart(), session.getEnd()));
                                            clickaction1.setClickEvent(
                                                new ClickEvent(Action.SUGGEST_COMMAND, "/sessions " + session.getUniqueid()));
                                            clickaction1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                new Text(Main.getMSG("Messages.Sessions.Hover-Text"))));

                                            player.sendMessage(clickaction1);

                                        }
                                        player.sendMessage(new TextComponent(""));

                                    } else {
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Sessions.No-Sessions-Found")));
                                    }

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    player.sendMessage(
                                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Sessions.No-Sessions-Found")));
                                }

                            }

                        });

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.No-Player-Found")));
                    }
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Sessions.Usage")));
            }

        }

    }

}
