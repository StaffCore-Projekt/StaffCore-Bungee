package de.lacodev.staffbungee.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.NotificationSender;
import de.lacodev.staffbungee.enums.ReasonType;
import de.lacodev.staffbungee.enums.Violation;
import de.lacodev.staffbungee.handlers.ViolationLevelHandler;
import de.lacodev.staffbungee.objects.Reason;
import de.lacodev.staffbungee.utils.BanLengthCalculator;
import de.lacodev.staffbungee.utils.StringGenerator;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MuteManager {
	
	public static boolean isMuted(String uuid) {
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT * FROM StaffCore_mutesdb WHERE MUTED_UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					if(rs.getLong("MUTE_END") == -1) {
						return true;
					} else if(rs.getLong("MUTE_END") > System.currentTimeMillis()) {
						return true;
					} else {
						unmute(uuid, "Console", "Unmute");
					}
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
		}
		return false;
	}
	
	public static String getFormattedEnd(String uuid) {
		
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT MUTE_END FROM StaffCore_mutesdb WHERE MUTED_UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return BanLengthCalculator.calculate(rs.getLong("MUTE_END") - System.currentTimeMillis());
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
		}
		return "UNKNOWN";
		
	}
	
	public static Long getRawEnd(String uuid) {
		
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT MUTE_END FROM StaffCore_mutesdb WHERE MUTED_UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getLong("MUTE_END");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
		}
		return 0L;
		
	}
	
	public static String getReason(String uuid) {
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT REASON FROM StaffCore_mutesdb WHERE MUTED_UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getString("REASON");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
		}
		return "UNKNOWN";
	}
	
	public static String getMuteID(String uuid) {
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT MUTE_ID FROM StaffCore_mutesdb WHERE MUTED_UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getString("MUTE_ID");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
		}
		return "UNKNOWN";
	}
	
	private static void addMute(String uuid) {
		if(Main.getMySQL().isConnected()) {
			Main.getMySQL().update("UPDATE StaffCore_playerdb SET MUTES = '"+ (PlayerManager.getMutes(uuid) + 1) +"' WHERE UUID = '"+ uuid +"'");
		}
	}
	
	public static void mute(String targetuuid, String reason, String teamuuid) {
		
		if(!PlayerManager.isProtected(targetuuid)) {
			
			if(teamuuid != "Console") {
				
				ProxiedPlayer player = BungeeCord.getInstance().getPlayer(UUID.fromString(teamuuid));
				
				Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

					@Override
					public void run() {
						if(Main.getMySQL().isConnected()) {
							if(ReasonManager.existsReason(ReasonType.MUTE, reason)) {
								
								Reason muteReason = ReasonManager.getReasonByName(ReasonType.MUTE, reason);
								long muteEnd = 0;
								
								if(Main.getInstance().getConfig().getBoolean("ViolationLevelSystem.Enable")) {
									muteEnd = ViolationLevelHandler.calculateLength(targetuuid, muteReason.getLength(), ViolationLevelHandler.getVL(targetuuid));
									
									if(muteEnd != -1) {
										muteEnd = muteEnd + System.currentTimeMillis();
									}
									ViolationLevelHandler.addVL(targetuuid, Violation.MUTE);
								} else {
									if(muteReason.getLength() == -1) {
										muteEnd = muteReason.getLength();
									} else {
										muteEnd = muteReason.getLength() + System.currentTimeMillis();
									}
								}
								
								String muteid = StringGenerator.getRandomString(35);
								
								Main.getMySQL().update("INSERT INTO StaffCore_mutesdb(MUTE_ID,MUTED_UUID,TEAM_UUID,REASON,MUTE_END) VALUES ('"+ muteid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ muteEnd +"')");
								
								addMute(targetuuid);
								
								ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
								
								if(target != null) {
									
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END,SUB_SERVER) VALUES ('MUTE','"+ muteid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ muteEnd +"','"+ target.getServer().getInfo().getName() +"')");
									
									target.sendMessage(new TextComponent(Main.getMSG("Messages.Mute-System.Message-If-Player-Muted").replace("%reason%", reason)));
									
								} else {
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,MUTE_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END) VALUES ('MUTE','"+ muteid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ muteEnd +"')");
								}
								
								NotificationManager.sendNotify(NotificationSender.PLAYER_NOTIFY, "MUTE", PlayerManager.getUsernamebyUUID(teamuuid), PlayerManager.getUsernamebyUUID(targetuuid), reason);
								
							} else {
								player.sendMessage(new TextComponent(""));
								player.sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7Reason does not exist§8)"));
								player.sendMessage(new TextComponent(""));
							}
						} else {
							player.sendMessage(new TextComponent(""));
							player.sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7MySQL Connection§8)"));
							player.sendMessage(new TextComponent(""));
						}
					}
					
				});
				
			} else {
				
				Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

					@Override
					public void run() {
						if(Main.getMySQL().isConnected()) {
							if(ReasonManager.existsReason(ReasonType.MUTE, reason)) {
								
								Reason muteReason = ReasonManager.getReasonByName(ReasonType.MUTE, reason);
								long muteEnd = 0;
								
								if(Main.getInstance().getConfig().getBoolean("ViolationLevelSystem.Enable")) {
									muteEnd = ViolationLevelHandler.calculateLength(targetuuid, muteReason.getLength(), ViolationLevelHandler.getVL(targetuuid));
									
									if(muteEnd != -1) {
										muteEnd = muteEnd + System.currentTimeMillis();
									}
									ViolationLevelHandler.addVL(targetuuid, Violation.MUTE);
								} else {
									if(muteReason.getLength() == -1) {
										muteEnd = muteReason.getLength();
									} else {
										muteEnd = muteReason.getLength() + System.currentTimeMillis();
									}
								}
								
								String muteid = StringGenerator.getRandomString(35);
								
								Main.getMySQL().update("INSERT INTO StaffCore_mutesdb(MUTE_ID,MUTED_UUID,TEAM_UUID,REASON,MUTE_END) VALUES ('" + muteid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ muteEnd +"')");
								
								addMute(targetuuid);
								
								ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
								
								if(target != null) {
									
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END,SUB_SERVER) VALUES ('MUTE','"+ muteid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ muteEnd +"','"+ target.getServer().getInfo().getName() +"')");
									
									target.sendMessage(new TextComponent(Main.getMSG("Messages.Mute-System.Message-If-Player-Muted").replace("%reason%", reason)));
									
								} else {
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END) VALUES ('MUTE','"+ muteid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ muteEnd +"')");
								}
								
								NotificationManager.sendNotify(NotificationSender.CONSOLE_NOTIFY, "MUTE", PlayerManager.getUsernamebyUUID(targetuuid), reason);
								
							} else {
								BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
								BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7Reason does not exist§8)"));
								BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
							}
						} else {
							BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
							BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7MySQL Connection§8)"));
							BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
						}
					}
					
				});
				
			}
			
		} else {
			NotificationManager.sendNotify(NotificationSender.CONSOLE_NOTIFY, "PROTECT", targetuuid);
		}
		
	}
	
	public static void silentmute(String targetuuid, String reason, String teamuuid) {
		
		if(!PlayerManager.isProtected(targetuuid)) {
			if(teamuuid != "Console") {
				
				ProxiedPlayer player = BungeeCord.getInstance().getPlayer(UUID.fromString(teamuuid));
				
				Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

					@Override
					public void run() {
						if(Main.getMySQL().isConnected()) {
							if(ReasonManager.existsReason(ReasonType.MUTE, reason)) {
								
								Reason muteReason = ReasonManager.getReasonByName(ReasonType.MUTE, reason);
								long muteEnd = 0;
								
								if(Main.getInstance().getConfig().getBoolean("ViolationLevelSystem.Enable")) {
									muteEnd = ViolationLevelHandler.calculateLength(targetuuid, muteReason.getLength(), ViolationLevelHandler.getVL(targetuuid));
									
									if(muteEnd != -1) {
										muteEnd = muteEnd + System.currentTimeMillis();
									}
									ViolationLevelHandler.addVL(targetuuid, Violation.SILENT_MUTE);
								} else {
									if(muteReason.getLength() == -1) {
										muteEnd = muteReason.getLength();
									} else {
										muteEnd = muteReason.getLength() + System.currentTimeMillis();
									}
								}
								
								String muteid = StringGenerator.getRandomString(35);
								
								Main.getMySQL().update("INSERT INTO StaffCore_mutesdb(MUTE_ID,MUTED_UUID,TEAM_UUID,REASON,MUTE_END) VALUES ('"+ muteid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ muteEnd +"')");
								
								addMute(targetuuid);
								
								ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
								
								if(target != null) {
									
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END,SUB_SERVER) VALUES ('MUTE','"+ muteid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ muteEnd +"','"+ target.getServer().getInfo().getName() +"')");
									
									target.sendMessage(new TextComponent(Main.getMSG("Messages.Mute-System.Message-If-Player-Muted").replace("%reason%", reason)));
									
								} else {
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END) VALUES ('MUTE','"+ muteid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ muteEnd +"')");
								}

							} else {
								player.sendMessage(new TextComponent(""));
								player.sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7Reason does not exist§8)"));
								player.sendMessage(new TextComponent(""));
							}
						} else {
							player.sendMessage(new TextComponent(""));
							player.sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7MySQL Connection§8)"));
							player.sendMessage(new TextComponent(""));
						}
					}
					
				});
				
			} else {
				
				Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

					@Override
					public void run() {
						if(Main.getMySQL().isConnected()) {
							if(ReasonManager.existsReason(ReasonType.MUTE, reason)) {
								
								Reason muteReason = ReasonManager.getReasonByName(ReasonType.MUTE, reason);
								long muteEnd = 0;
								
								if(Main.getInstance().getConfig().getBoolean("ViolationLevelSystem.Enable")) {
									muteEnd = ViolationLevelHandler.calculateLength(targetuuid, muteReason.getLength(), ViolationLevelHandler.getVL(targetuuid));
									
									if(muteEnd != -1) {
										muteEnd = muteEnd + System.currentTimeMillis();
									}
									ViolationLevelHandler.addVL(targetuuid, Violation.SILENT_MUTE);
								} else {
									if(muteReason.getLength() == -1) {
										muteEnd = muteReason.getLength();
									} else {
										muteEnd = muteReason.getLength() + System.currentTimeMillis();
									}
								}
								
								String muteid = StringGenerator.getRandomString(35);
								
								Main.getMySQL().update("INSERT INTO StaffCore_mutesdb(MUTE_ID,MUTED_UUID,TEAM_UUID,REASON,MUTE_END) VALUES ('"+ muteid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ muteEnd +"')");
								
								addMute(targetuuid);
								
								ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
								
								if(target != null) {
									
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END,SUB_SERVER) VALUES ('MUTE','"+ muteid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ muteEnd +"','"+ target.getServer().getInfo().getName() +"')");
									
									target.sendMessage(new TextComponent(Main.getMSG("Messages.Mute-System.Message-If-Player-Muted").replace("%reason%", reason)));
									
								} else {
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END) VALUES ('MUTE','"+ muteid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ muteEnd +"')");
								}
								
							} else {
								BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
								BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7Reason does not exist§8)"));
								BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
							}
						} else {
							BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
							BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7MySQL Connection§8)"));
							BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
						}
					}
					
				});
				
			}
		} else {
			NotificationManager.sendNotify(NotificationSender.CONSOLE_NOTIFY, "PROTECT", targetuuid);
		}
		
	}
	
	public static void unmute(String targetuuid, String teamuuid, String reason) {
		
		if(teamuuid != "Console") {
			
			ProxiedPlayer team = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
			
			Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

				@Override
				public void run() {
					if(Main.getMySQL().isConnected()) {
						
						String muteid = getMuteID(targetuuid);
						
						Main.getMySQL().update("UPDATE StaffCore_punishmentsdb SET UNBANNED = '1', UNBAN_TIME = '"+ System.currentTimeMillis() +"', "
								+ "UNBAN_REASON = '"+ reason +"', UNBAN_STAFF = '"+ teamuuid +"' WHERE PUNISH_ID = '"+ muteid +"'");
						
						Main.getMySQL().update("DELETE FROM StaffCore_mutesdb WHERE MUTED_UUID = '"+ targetuuid +"'");
						
						NotificationManager.sendNotify(NotificationSender.PLAYER_NOTIFY, "UNMUTE", PlayerManager.getUsernamebyUUID(teamuuid), PlayerManager.getUsernamebyUUID(targetuuid), null);
						
					} else {
						team.sendMessage(new TextComponent(""));
						team.sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7MySQL Connection§8)"));
						team.sendMessage(new TextComponent(""));
					}
				}
				
			});
			
		} else {
			
			Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

				@Override
				public void run() {
					if(Main.getMySQL().isConnected()) {
						
						String muteid = getMuteID(targetuuid);
						
						Main.getMySQL().update("UPDATE StaffCore_punishmentsdb SET UNBANNED = '1', UNBAN_TIME = '"+ System.currentTimeMillis() +"', "
								+ "UNBAN_REASON = '"+ reason +"', UNBAN_STAFF = '"+ teamuuid +"' WHERE PUNISH_ID = '"+ muteid +"'");
						
						Main.getMySQL().update("DELETE FROM StaffCore_mutesdb WHERE MUTED_UUID = '"+ targetuuid +"'");
						
						NotificationManager.sendNotify(NotificationSender.CONSOLE_NOTIFY, "UNMUTE", PlayerManager.getUsernamebyUUID(targetuuid), null);
						
					} else {
						BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
						BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7MySQL Connection§8)"));
						BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
					}
				}
				
			});
			
		}
		
	}
	
	public static void silentunmute(String targetuuid, String teamuuid, String reason) {
		
		if(teamuuid != "Console") {
			
			ProxiedPlayer team = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
			
			Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

				@Override
				public void run() {
					if(Main.getMySQL().isConnected()) {
						
						String muteid = getMuteID(targetuuid);
						
						Main.getMySQL().update("UPDATE StaffCore_punishmentsdb SET UNBANNED = '1', UNBAN_TIME = '"+ System.currentTimeMillis() +"', "
								+ "UNBAN_REASON = '"+ reason +"', UNBAN_STAFF = '"+ teamuuid +"' WHERE PUNISH_ID = '"+ muteid +"'");
						
						Main.getMySQL().update("DELETE FROM StaffCore_mutesdb WHERE MUTED_UUID = '"+ targetuuid +"'");
						
					} else {
						team.sendMessage(new TextComponent(""));
						team.sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7MySQL Connection§8)"));
						team.sendMessage(new TextComponent(""));
					}
				}
				
			});
			
		} else {
			
			Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

				@Override
				public void run() {
					if(Main.getMySQL().isConnected()) {
						
						String muteid = getMuteID(targetuuid);
						
						Main.getMySQL().update("UPDATE StaffCore_punishmentsdb SET UNBANNED = '1', UNBAN_TIME = '"+ System.currentTimeMillis() +"', "
								+ "UNBAN_REASON = '"+ reason +"', UNBAN_STAFF = '"+ teamuuid +"' WHERE PUNISH_ID = '"+ muteid +"'");
						
						Main.getMySQL().update("DELETE FROM StaffCore_mutesdb WHERE MUTED_UUID = '"+ targetuuid +"'");
						
					} else {
						BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
						BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7MySQL Connection§8)"));
						BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
					}
				}
				
			});
			
		}
		
	}
}
