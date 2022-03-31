package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.utils.ChatDetector;
import java.util.ArrayList;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDChatFilter extends Command {

    public CMDChatFilter(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                player.hasPermission(Main.getPermissionNotice("Permissions.ChatFilter.Change"))) {

                if (args.length == 2) {

                    if (args[0].equalsIgnoreCase("add")) {

                        if (!ChatDetector.existsSwearingWord(args[1])) {

                            Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                                @Override
                                public void run() {
                                    Main.getMySQL().update("INSERT INTO StaffCore_swearingdb(WORD,ADDED_AT) VALUES ('" + args[1] + "','" +
                                        System.currentTimeMillis() + "')");
                                    player.sendMessage(new TextComponent(Main.getPrefix() + "§7Word §aadded §7to the Swearing-Words"));
                                }

                            });

                        }

                    } else if (args[0].equalsIgnoreCase("remove")) {

                        if (ChatDetector.existsSwearingWord(args[1])) {

                            Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                                @Override
                                public void run() {
                                    Main.getMySQL().update("DELETE FROM StaffCore_swearingdb WHERE WORD = '" + args[1] + "'");
                                    player.sendMessage(new TextComponent(Main.getPrefix() + "§7Word §aremoved §7from the Swearing-Words"));
                                }

                            });

                        }

                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + "§7/chatfilter <add/remove> <Word>"));
                    }

                } else if (args.length == 1) {

                    if (args[0].equalsIgnoreCase("list")) {

                        Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

                            @Override
                            public void run() {
                                ArrayList<String> list = ChatDetector.getChatFilter();

                                for (String l : list) {
                                    player.sendMessage(new TextComponent(Main.getPrefix() + "§7- §c" + l));
                                }
                            }

                        });

                    }

                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + "§7/chatfilter <add/remove> <Word>"));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                    .replace("%permission%", Main.getPermissionNotice("Permissions.ChatFilter.Change"))));
            }
        }

    }

}
