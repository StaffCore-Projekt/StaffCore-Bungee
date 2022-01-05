package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.PlayerManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDStaffRollback extends Command {

	public CMDStaffRollback(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer) sender;
			
			if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.StaffRollback.Use"))) {
				
				if(args.length == 1) {
					
					String uuid = PlayerManager.getUUIDByName(args[0]);
					
					if(uuid != null) {
						
						BungeeCord.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {

							@Override
							public void run() {
								
								Main.getMySQL().update("DELETE FROM StaffCore_bansdb WHERE TEAM_UUID = '"+ uuid +"'");
								Main.getMySQL().update("DELETE FROM StaffCore_blacklistdb WHERE ADDED_BY = '"+ uuid +"'");
								Main.getMySQL().update("DELETE FROM StaffCore_ipbansdb WHERE TEAM_UUID = '"+ uuid +"'");
								Main.getMySQL().update("DELETE FROM StaffCore_mutesdb WHERE TEAM_UUID = '"+ uuid +"'");
								Main.getMySQL().update("DELETE FROM StaffCore_punishmentsdb WHERE TEAM_UUID = '"+ uuid +"'");
								Main.getMySQL().update("DELETE FROM StaffCore_reportsdb WHERE TEAM_UUID = '"+ uuid +"'");
								Main.getMySQL().update("DELETE FROM StaffCore_warnsdb WHERE TEAM_UUID = '"+ uuid +"'");
								Main.getMySQL().update("DELETE FROM StaffCore_watchlistdb WHERE TEAM_UUID = '"+ uuid +"'");
								
								Main.getMySQL().update("INSERT INTO StaffCore_activitydb(type,uuid,target,message,reg_date,priority) VALUES ('STAFF_ROLLEDBACK','"+ player.getUniqueId().toString() +"',"
										+ "'"+ PlayerManager.getUsernamebyUUID(uuid) +"','%player% rolled back all actions performed by %target%','"+ System.currentTimeMillis() +"','3')");
								
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.Staff-Rollback.Success").replace("%staff%", args[0])));
								
							}
							
						});
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Player-Not-Found")));
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + "§7/staffrollback <Player>"));
				}
				
			} else {
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.StaffRollback.Use"))));
			}
			
		} else {
			CommandSender player = sender;
			
			if(args.length == 1) {
				
				String uuid = PlayerManager.getUUIDByName(args[0]);
				
				if(uuid != null) {
					
					BungeeCord.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {

						@Override
						public void run() {
							
							Main.getMySQL().update("DELETE FROM StaffCore_bansdb WHERE TEAM_UUID = '"+ uuid +"'");
							Main.getMySQL().update("DELETE FROM StaffCore_blacklistdb WHERE ADDED_BY = '"+ uuid +"'");
							Main.getMySQL().update("DELETE FROM StaffCore_ipbansdb WHERE TEAM_UUID = '"+ uuid +"'");
							Main.getMySQL().update("DELETE FROM StaffCore_mutesdb WHERE TEAM_UUID = '"+ uuid +"'");
							Main.getMySQL().update("DELETE FROM StaffCore_punishmentsdb WHERE TEAM_UUID = '"+ uuid +"'");
							Main.getMySQL().update("DELETE FROM StaffCore_reportsdb WHERE TEAM_UUID = '"+ uuid +"'");
							Main.getMySQL().update("DELETE FROM StaffCore_warnsdb WHERE TEAM_UUID = '"+ uuid +"'");
							Main.getMySQL().update("DELETE FROM StaffCore_watchlistdb WHERE TEAM_UUID = '"+ uuid +"'");
							
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.Staff-Rollback.Success").replace("%staff%", args[0])));
							
						}
						
					});
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Player-Not-Found")));
				}
				
			} else {
				player.sendMessage(new TextComponent(Main.getPrefix() + "§7/staffrollback <Player>"));
			}
		}
		
	}

}
