package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDTeamChat extends Command {

	public CMDTeamChat(String name) {
		super(name, null, "tc");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer)sender;
			
			if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.TeamChat.Use"))) {
				
				if(args.length == 1) {
					
					if(args[0].equalsIgnoreCase("login")) {
						
						Main.getTeamChat().login(player);
						
						Main.getMySQL().update("INSERT INTO StaffCore_activitydb(type,uuid,target,message,reg_date,priority) VALUES ('TEAMCHAT_JOINED','"+ player.getUniqueId().toString() +"',"
								+ "'Teamchat','%player% joined the %target%','"+ System.currentTimeMillis() +"','1')");
						
					} else if(args[0].equalsIgnoreCase("logout")) {
						
						Main.getTeamChat().logout(player);
						
						Main.getMySQL().update("INSERT INTO StaffCore_activitydb(type,uuid,target,message,reg_date,priority) VALUES ('TEAMCHAT_LEFT','"+ player.getUniqueId().toString() +"',"
								+ "'Teamchat','%player% left the %target%','"+ System.currentTimeMillis() +"','1')");
						
					} else if(args[0].equalsIgnoreCase("ghostlogin")) {
						
						if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.TeamChat.Ghost"))) {
							Main.getTeamChat().ghostLogin(player);
						}
						
					} else if(args[0].equalsIgnoreCase("ghostlogout")) {
						
						if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.TeamChat.Ghost"))) {
							Main.getTeamChat().ghostLogout(player);
						}
						
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.TeamChat.Usage")));
					if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.TeamChat.Ghost"))) {
						player.sendMessage(new TextComponent(Main.getPrefix() + "§7/teamchat ghostlogin"));
						player.sendMessage(new TextComponent(Main.getPrefix() + "§7/teamchat ghostlogout"));
						
					}
					if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.TeamChat.List"))) {
						player.sendMessage(new TextComponent(""));
						TextComponent tc = new TextComponent();
						tc.setText(Main.getPrefix() + "§cList: §7");
						
						for(ProxiedPlayer all : Main.getTeamChat().get()) {
							if(Main.getTeamChat().isGhost(all)) {
								if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.TeamChat.Ghost"))) {
									TextComponent tc2 = new TextComponent();
									tc2.setText("§3" + all.getName() + ", ");
									tc2.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text("§7Server §8» §c" + all.getServer().getInfo().getName())));
									tc.addExtra(tc2);
								}
							} else {
								TextComponent tc2 = new TextComponent();
								tc2.setText("§7" + all.getName() + ", ");
								tc2.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text("§7Server §8» §c" + all.getServer().getInfo().getName())));
								tc.addExtra(tc2);
							}
						}
						player.sendMessage(tc);
					}
				}
				
			} else {
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.TeamChat.Use"))));
			}
			
		} else {
			CommandSender player = sender;
			
			player.sendMessage(new TextComponent(Main.getPrefix() + "§7Currently there are §c" + Main.getTeamChat().get().size() + " User(s) §7in the teamchat!"));
		}
		
	}

}
