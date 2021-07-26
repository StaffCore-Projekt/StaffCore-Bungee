package de.lacodev.staffbungee.managers;

import java.util.UUID;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.NotificationSender;
import de.lacodev.staffbungee.enums.Violation;
import de.lacodev.staffbungee.handlers.ViolationLevelHandler;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class WarnManager {

	public static void warn(String targetuuid, String teamuuid, String reason) {
		
		if(!PlayerManager.isProtected(targetuuid)) {
			
			Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

				@Override
				public void run() {
					
					if(teamuuid != "Console") {
						
						ProxiedPlayer player = BungeeCord.getInstance().getPlayer(UUID.fromString(teamuuid));
						
						if(Main.getMySQL().isConnected()) {
							
							if(Main.getInstance().getConfig().getBoolean("ViolationLevelSystem.Enable")) {
								ViolationLevelHandler.addVL(targetuuid, Violation.WARN);
							}
							
							addWarn(targetuuid);
							
							ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
							
							if(target != null) {
								Main.getMySQL().update("INSERT INTO StaffCore_warnsdb(WARNED_UUID,TEAM_UUID,REASON,WARNED_AT,SUB_SERVER) VALUES "
										+ "('"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ System.currentTimeMillis() +"','"+ target.getServer().getInfo().getName() +"')");
								
								Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,REASON,BAN_START,SUB_SERVER) VALUES "
										+ "('WARN','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ System.currentTimeMillis() +"','"+ target.getServer().getInfo().getName() +"')");
								
								target.sendMessage(new TextComponent(Main.getMSG("Messages.Layouts.Warn")
										.replace("%team%", PlayerManager.getUsernamebyUUID(teamuuid))
										.replace("%reason%", reason)
										.replace("%warns%", "" + PlayerManager.getWarns(targetuuid))));
								
							} else {
								Main.getMySQL().update("INSERT INTO StaffCore_warnsdb(WARNED_UUID,TEAM_UUID,REASON,WARNED_AT) VALUES "
										+ "('"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ System.currentTimeMillis() +"')");
								
								Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,REASON,BAN_START) VALUES "
										+ "('WARN','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ System.currentTimeMillis() +"')");
							}
							
							NotificationManager.sendNotify(NotificationSender.PLAYER_NOTIFY, "WARN", player.getName(), PlayerManager.getUsernamebyUUID(targetuuid), reason);
							
						} else {
							player.sendMessage(new TextComponent(""));
							player.sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7MySQL Connection§8)"));
							player.sendMessage(new TextComponent(""));
						}
						
					} else {
						
						if(Main.getMySQL().isConnected()) {
							
							if(Main.getInstance().getConfig().getBoolean("ViolationLevelSystem.Enable")) {
								ViolationLevelHandler.addVL(targetuuid, Violation.WARN);
							}
							
							addWarn(targetuuid);
							
							ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
							
							if(target != null) {
								Main.getMySQL().update("INSERT INTO StaffCore_warnsdb(WARNED_UUID,TEAM_UUID,REASON,WARNED_AT,SUB_SERVER) VALUES "
										+ "('"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ System.currentTimeMillis() +"','"+ target.getServer().getInfo().getName() +"')");
								
								Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,REASON,BAN_START,SUB_SERVER) VALUES "
										+ "('WARN','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ System.currentTimeMillis() +"','"+ target.getServer().getInfo().getName() +"')");
								
								target.sendMessage(new TextComponent(Main.getMSG("Messages.Layouts.Warn")
										.replace("%team%", PlayerManager.getUsernamebyUUID(teamuuid))
										.replace("%reason%", reason)
										.replace("%warns%", "" + PlayerManager.getWarns(targetuuid))));
								
							} else {
								Main.getMySQL().update("INSERT INTO StaffCore_warnsdb(WARNED_UUID,TEAM_UUID,REASON,WARNED_AT) VALUES "
										+ "('"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ System.currentTimeMillis() +"')");
								
								Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,REASON,BAN_START) VALUES "
										+ "('WARN','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ System.currentTimeMillis() +"')");
							}
							
							NotificationManager.sendNotify(NotificationSender.CONSOLE_NOTIFY, "WARN", PlayerManager.getUsernamebyUUID(targetuuid), reason);
							
						} else {
							BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
							BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7MySQL Connection§8)"));
							BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
						}
						
					}
					
				}
				
			});
			
		} else {
			NotificationManager.sendNotify(NotificationSender.CONSOLE_NOTIFY, "PROTECT", targetuuid);
		}
		
	}

	public static void addWarn(String uuid) {
		if(Main.getMySQL().isConnected()) {
			Main.getMySQL().update("UPDATE StaffCore_playerdb SET WARNS = '"+ (PlayerManager.getWarns(uuid) + 1) +"' WHERE UUID = '"+ uuid +"'");
		}
	}
	
}
