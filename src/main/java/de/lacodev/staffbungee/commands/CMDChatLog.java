package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.objects.Message;
import de.lacodev.staffbungee.utils.ChatDetector;
import java.sql.SQLException;
import java.util.ArrayList;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDChatLog extends Command {

    public CMDChatLog(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                player.hasPermission(Main.getPermissionNotice("Permissions.ChatLog.Use"))) {

                if (args.length == 2) {

                    if (args[0].equalsIgnoreCase("lookup")) {

                        String uuid = PlayerManager.getUUIDByName(args[1]);

                        ProxyServer.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                            @Override
                            public void run() {
                                int violations = 0;
                                if (uuid != null) {

                                    try {
                                        ArrayList<Message> messages = PlayerManager.getMessages(uuid);

                                        if (!messages.isEmpty()) {

                                            for (Message message : messages) {

                                                if (ChatDetector.containsAds(message.getMessage())) {
                                                    violations++;
                                                } else if (ChatDetector.containsSwearWord(message.getMessage())) {
                                                    violations++;
                                                }

                                            }
                                            player.sendMessage(new TextComponent(Main.getPrefix() +
                                                Main.getMSG("Messages.System.ChatLog.Lookup-Completed")
                                                    .replace("%total%", violations + "")));

                                        } else {
                                            player.sendMessage(
                                                new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.ChatLog.No-Messages")));
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.ChatLog.No-Messages")));
                                    }

                                } else {
                                    player.sendMessage(
                                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Player-Not-Found")));
                                }
                            }

                        });
                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + "§7/chatlog lookup <Player>"));
                    }

                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + "§7/chatlog lookup <Player>"));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                    .replace("%permission%", Main.getPermissionNotice("Permissions.ChatLog.Use"))));
            }

        } else {
            CommandSender player = sender;

            if (args.length == 2) {

                if (args[0].equalsIgnoreCase("lookup")) {

                    String uuid = PlayerManager.getUUIDByName(args[1]);

                    ProxyServer.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                        @Override
                        public void run() {
                            int violations = 0;
                            if (uuid != null) {

                                try {
                                    ArrayList<Message> messages = PlayerManager.getMessages(uuid);

                                    if (!messages.isEmpty()) {

                                        for (Message message : messages) {

                                            if (ChatDetector.containsAds(message.getMessage())) {
                                                violations++;
                                            } else if (ChatDetector.containsSwearWord(message.getMessage())) {
                                                violations++;
                                            }

                                        }
                                        player.sendMessage(new TextComponent(Main.getPrefix() +
                                            Main.getMSG("Messages.System.ChatLog.Lookup-Completed").replace("%total%", violations + "")));

                                    } else {
                                        player.sendMessage(
                                            new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.ChatLog.No-Messages")));
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    player.sendMessage(
                                        new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.ChatLog.No-Messages")));
                                }

                            } else {
                                player.sendMessage(
                                    new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Player-Not-Found")));
                            }
                        }

                    });
                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + "§7/chatlog lookup <Player>"));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + "§7/chatlog lookup <Player>"));
            }
        }

    }

}
