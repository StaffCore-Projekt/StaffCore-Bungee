package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.Violation;
import de.lacodev.staffbungee.handlers.ViolationLevelHandler;
import de.lacodev.staffbungee.managers.NotificationManager;
import de.lacodev.staffbungee.utils.StringGenerator;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDKick extends Command {

    public CMDKick(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (sender instanceof ProxiedPlayer) {

            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) ||
                player.hasPermission(Main.getPermissionNotice("Permissions.Kick.Use"))) {

                if (args.length >= 2) {

                    String reason = args[1];

                    for (int i = 2; i < args.length; i++) {
                        reason = reason + " " + args[i];
                    }

                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

                    if (target != null) {

                        if (Main.getInstance().getConfig().getBoolean("ViolationLevelSystem.Enable")) {
                            ViolationLevelHandler.addVL(target.getUniqueId().toString(), Violation.KICK);
                        }

                        target.disconnect(new TextComponent(Main.getMSG("Messages.System.Kick.Screen").replace("%reason%", reason)));

                        Main.getMySQL()
                            .update("INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,REASON,BAN_START,SUB_SERVER) VALUES "
                                + "('KICK','" + target.getUniqueId().toString() + "','" + player.getUniqueId().toString() + "','" +
                                StringGenerator.getMySQLFriendly(reason) + "','" + System.currentTimeMillis() + "','" +
                                target.getServer().getInfo().getName() + "')");

                        Main.getMySQL().update(
                            "INSERT INTO StaffCore_activitydb(type,uuid,target,message,reg_date,priority) VALUES ('PLAYER_KICKED','" +
                                player.getUniqueId().toString() + "',"
                                + "'" + target.getUniqueId().toString() + "','%player% kicked the player %target%','" +
                                System.currentTimeMillis() + "','2')");

                        NotificationManager.sendKickNotify(target.getUniqueId().toString(), player.getUniqueId().toString(), reason);


                    } else {
                        player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Target-offline")));
                    }

                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + "§7/kick <Player> <Reason>"));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission")
                    .replace("%permission%", Main.getPermissionNotice("Permissions.Kick.Use"))));
            }

        } else {
            CommandSender player = sender;

            if (args.length >= 2) {

                String reason = args[1];

                for (int i = 2; i < args.length; i++) {
                    reason = reason + " " + args[i];
                }

                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

                if (target != null) {

                    target.disconnect(new TextComponent(Main.getMSG("Messages.System.Kick.Screen").replace("%reason%", reason)));

                    Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,REASON,BAN_START) VALUES "
                        + "('KICK','" + target.getUniqueId().toString() + "','Console','" + StringGenerator.getMySQLFriendly(reason) +
                        "','" + System.currentTimeMillis() + "')");

                    NotificationManager.sendKickNotify(target.getUniqueId().toString(), "Console", reason);


                } else {
                    player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Target-offline")));
                }

            } else {
                player.sendMessage(new TextComponent(Main.getPrefix() + "§7/kick <Player> <Reason>"));
            }
        }

    }

}
