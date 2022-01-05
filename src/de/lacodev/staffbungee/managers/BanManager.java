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

public class BanManager {
	
	public static boolean isBanned(String uuid) {
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT BAN_END FROM StaffCore_bansdb WHERE BANNED_UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					if(rs.getLong("BAN_END") == -1) {
						return true;
					} else if(rs.getLong("BAN_END") > System.currentTimeMillis()) {
						return true;
					} else {
						unban(uuid, "Console", "Unban");
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
			
			ResultSet rs = Main.getMySQL().query("SELECT BAN_END FROM StaffCore_bansdb WHERE BANNED_UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return BanLengthCalculator.calculate(rs.getLong("BAN_END") - System.currentTimeMillis());
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
		}
		return "UNKNOWN";
		
	}
	
	public static Long getRawEnd(String uuid) {
		
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT BAN_END FROM StaffCore_bansdb WHERE BANNED_UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getLong("BAN_END");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
		}
		return 0L;
		
	}
	
	public static String getReason(String uuid) {
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT REASON FROM StaffCore_bansdb WHERE BANNED_UUID = '"+ uuid +"'");
			
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
	
	public static String getBanID(String uuid) {
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT BAN_ID FROM StaffCore_bansdb WHERE BANNED_UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getString("BAN_ID");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
		}
		return "UNKNOWN";
	}
	
	private static void addBan(String uuid) {
		if(Main.getMySQL().isConnected()) {
			Main.getMySQL().update("UPDATE StaffCore_playerdb SET BANS = '"+ (PlayerManager.getBans(uuid) + 1) +"' WHERE UUID = '"+ uuid +"'");
		}
	}
	
	public static void ban(String targetuuid, String reason, String teamuuid) {
		
		if(!PlayerManager.isProtected(targetuuid)) {
			
			if(teamuuid != "Console") {
				
				ProxiedPlayer player = BungeeCord.getInstance().getPlayer(UUID.fromString(teamuuid));
				
				Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

					@Override
					public void run() {
						if(Main.getMySQL().isConnected()) {
							if(ReasonManager.existsReason(ReasonType.BAN, reason)) {
								
								Reason banReason = ReasonManager.getReasonByName(ReasonType.BAN, reason);
								long banEnd = 0;
								
								if(Main.getInstance().getConfig().getBoolean("ViolationLevelSystem.Enable")) {
									banEnd = ViolationLevelHandler.calculateLength(targetuuid, banReason.getLength(), ViolationLevelHandler.getVL(targetuuid));
									
									if(banEnd != -1) {
										banEnd = banEnd + System.currentTimeMillis();
									}
									ViolationLevelHandler.addVL(targetuuid, Violation.BAN);
								} else {
									if(banReason.getLength() == -1) {
										banEnd = banReason.getLength();
									} else {
										banEnd = banReason.getLength() + System.currentTimeMillis();
									}
								}
								
								String banid = StringGenerator.getRandomString(35);
								
								Main.getMySQL().update("INSERT INTO StaffCore_bansdb(BAN_ID,BANNED_UUID,TEAM_UUID,REASON,BAN_END) VALUES ('"+ banid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ banEnd +"')");
								
								if(banReason.isAdmin()) {
									banIp(targetuuid, teamuuid);
								}
								
								addBan(targetuuid);
								
								ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
								
								if(target != null) {
									
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END,SUB_SERVER) VALUES ('BAN','"+ banid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ banEnd +"','"+ target.getServer().getInfo().getName() +"')");
									
									target.disconnect(new TextComponent(Main.getMSG("Messages.Ban-System.Player-Kick-Screen").replace("%reason%", reason)));
									
								} else {
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END) VALUES ('BAN','"+ banid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ banEnd +"')");
								}
								
								Main.getMySQL().update("INSERT INTO StaffCore_activitydb(type,uuid,target,message,reg_date,priority) VALUES ('PLAYER_BANNED','"+ teamuuid +"',"
										+ "'"+ PlayerManager.getUsernamebyUUID(targetuuid) +"','%player% banned %target% from the network','"+ System.currentTimeMillis() +"','3')");
								
								NotificationManager.sendNotify(NotificationSender.PLAYER_NOTIFY, "BAN", PlayerManager.getUsernamebyUUID(teamuuid), PlayerManager.getUsernamebyUUID(targetuuid), reason);
								
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
							if(ReasonManager.existsReason(ReasonType.BAN, reason)) {
								
								Reason banReason = ReasonManager.getReasonByName(ReasonType.BAN, reason);
								long banEnd = 0;
								
								if(Main.getInstance().getConfig().getBoolean("ViolationLevelSystem.Enable")) {
									banEnd = ViolationLevelHandler.calculateLength(targetuuid, banReason.getLength(), ViolationLevelHandler.getVL(targetuuid));
									
									if(banEnd != -1) {
										banEnd = banEnd + System.currentTimeMillis();
									}
									ViolationLevelHandler.addVL(targetuuid, Violation.BAN);
								} else {
									if(banReason.getLength() == -1) {
										banEnd = banReason.getLength();
									} else {
										banEnd = banReason.getLength() + System.currentTimeMillis();
									}
								}
								
								String banid = StringGenerator.getRandomString(35);
								
								Main.getMySQL().update("INSERT INTO StaffCore_bansdb(BAN_ID,BANNED_UUID,TEAM_UUID,REASON,BAN_END) VALUES ('"+ banid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ banEnd +"')");
								
								if(banReason.isAdmin()) {
									banIp(targetuuid, teamuuid);
								}
								
								addBan(targetuuid);
								
								ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
								
								if(target != null) {
									
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END,SUB_SERVER) VALUES ('BAN','"+ banid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ banEnd +"','"+ target.getServer().getInfo().getName() +"')");
									
									target.disconnect(new TextComponent(Main.getMSG("Messages.Ban-System.Player-Kick-Screen").replace("%reason%", reason)));
									
								} else {
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END) VALUES ('BAN','"+ banid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ banEnd +"')");
								}
								
								NotificationManager.sendNotify(NotificationSender.CONSOLE_NOTIFY, "BAN", PlayerManager.getUsernamebyUUID(targetuuid), reason);
								
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
	
	public static void silentban(String targetuuid, String reason, String teamuuid) {
		
		if(!PlayerManager.isProtected(targetuuid)) {
			if(teamuuid != "Console") {
				
				ProxiedPlayer player = BungeeCord.getInstance().getPlayer(UUID.fromString(teamuuid));
				
				Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

					@Override
					public void run() {
						if(Main.getMySQL().isConnected()) {
							if(ReasonManager.existsReason(ReasonType.BAN, reason)) {
								
								Reason banReason = ReasonManager.getReasonByName(ReasonType.BAN, reason);
								long banEnd = 0;
								
								if(Main.getInstance().getConfig().getBoolean("ViolationLevelSystem.Enable")) {
									banEnd = ViolationLevelHandler.calculateLength(targetuuid, banReason.getLength(), ViolationLevelHandler.getVL(targetuuid));
									
									if(banEnd != -1) {
										banEnd = banEnd + System.currentTimeMillis();
									}
									ViolationLevelHandler.addVL(targetuuid, Violation.SILENT_BAN);
								} else {
									if(banReason.getLength() == -1) {
										banEnd = banReason.getLength();
									} else {
										banEnd = banReason.getLength() + System.currentTimeMillis();
									}
								}
								
								String banid = StringGenerator.getRandomString(35);
								
								Main.getMySQL().update("INSERT INTO StaffCore_bansdb(BAN_ID,BANNED_UUID,TEAM_UUID,REASON,BAN_END) VALUES ('"+ banid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ banEnd +"')");
								
								if(banReason.isAdmin()) {
									banIp(targetuuid, teamuuid);
								}
								
								addBan(targetuuid);
								
								ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
								
								if(target != null) {
									
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END,SUB_SERVER) VALUES ('BAN','"+ banid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ banEnd +"','"+ target.getServer().getInfo().getName() +"')");
									
									target.disconnect(new TextComponent(Main.getMSG("Messages.Ban-System.Player-Kick-Screen").replace("%reason%", reason)));
									
								} else {
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END) VALUES ('BAN','"+ banid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ banEnd +"')");
								}
								
								Main.getMySQL().update("INSERT INTO StaffCore_activitydb(type,uuid,target,message,reg_date,priority) VALUES ('PLAYER_BANNED','"+ teamuuid +"',"
										+ "'"+ PlayerManager.getUsernamebyUUID(targetuuid) +"','%player% banned %target% from the network','"+ System.currentTimeMillis() +"','3')");

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
							if(ReasonManager.existsReason(ReasonType.BAN, reason)) {
								
								Reason banReason = ReasonManager.getReasonByName(ReasonType.BAN, reason);
								long banEnd = 0;
								
								if(Main.getInstance().getConfig().getBoolean("ViolationLevelSystem.Enable")) {
									banEnd = ViolationLevelHandler.calculateLength(targetuuid, banReason.getLength(), ViolationLevelHandler.getVL(targetuuid));
									
									if(banEnd != -1) {
										banEnd = banEnd + System.currentTimeMillis();
									}
									ViolationLevelHandler.addVL(targetuuid, Violation.SILENT_BAN);
								} else {
									if(banReason.getLength() == -1) {
										banEnd = banReason.getLength();
									} else {
										banEnd = banReason.getLength() + System.currentTimeMillis();
									}
								}
								
								String banid = StringGenerator.getRandomString(35);
								
								Main.getMySQL().update("INSERT INTO StaffCore_bansdb(BAN_ID,BANNED_UUID,TEAM_UUID,REASON,BAN_END) VALUES ('"+ banid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"','"+ banEnd +"')");
								
								if(banReason.isAdmin()) {
									banIp(targetuuid, teamuuid);
								}
								
								addBan(targetuuid);
								
								ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
								
								if(target != null) {
									
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END,SUB_SERVER) VALUES ('BAN','"+ banid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ banEnd +"','"+ target.getServer().getInfo().getName() +"')");
									
									target.disconnect(new TextComponent(Main.getMSG("Messages.Ban-System.Player-Kick-Screen").replace("%reason%", reason)));
									
								} else {
									Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,PUNISH_ID,UUID,TEAM_UUID,REASON,BAN_START,BAN_END) VALUES ('BAN','"+ banid +"','"+ targetuuid +"','"+ teamuuid +"','"+ reason +"'"
											+ ",'"+ System.currentTimeMillis() +"','"+ banEnd +"')");
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
	
	public static void unban(String targetuuid, String teamuuid, String reason) {
		
		if(teamuuid != "Console") {
			
			ProxiedPlayer team = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
			
			Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

				@Override
				public void run() {
					if(Main.getMySQL().isConnected()) {
						
						String banid = getBanID(targetuuid);
						
						Main.getMySQL().update("UPDATE StaffCore_punishmentsdb SET UNBANNED = '1', UNBAN_TIME = '"+ System.currentTimeMillis() +"', "
								+ "UNBAN_REASON = '"+ reason +"', UNBAN_STAFF = '"+ teamuuid +"' WHERE PUNISH_ID = '"+ banid +"'");
						
						Main.getMySQL().update("DELETE FROM StaffCore_bansdb WHERE BANNED_UUID = '"+ targetuuid +"'");
						
						NotificationManager.sendNotify(NotificationSender.PLAYER_NOTIFY, "UNBAN", PlayerManager.getUsernamebyUUID(teamuuid), PlayerManager.getUsernamebyUUID(targetuuid), reason);
						
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
						
						String banid = getBanID(targetuuid);
						
						Main.getMySQL().update("UPDATE StaffCore_punishmentsdb SET UNBANNED = '1', UNBAN_TIME = '"+ System.currentTimeMillis() +"', "
								+ "UNBAN_REASON = '"+ reason +"', UNBAN_STAFF = '"+ teamuuid +"' WHERE PUNISH_ID = '"+ banid +"'");
						
						Main.getMySQL().update("DELETE FROM StaffCore_bansdb WHERE BANNED_UUID = '"+ targetuuid +"'");
						
						NotificationManager.sendNotify(NotificationSender.CONSOLE_NOTIFY, "UNBAN", PlayerManager.getUsernamebyUUID(targetuuid), reason);
						
					} else {
						BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
						BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7MySQL Connection§8)"));
						BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
					}
				}
				
			});
			
		}
		
	}
	
	public static void silentunban(String targetuuid, String teamuuid, String reason) {
		
		if(teamuuid != "Console") {
			
			ProxiedPlayer team = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
			
			Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

				@Override
				public void run() {
					if(Main.getMySQL().isConnected()) {
						
						String banid = getBanID(targetuuid);
						
						Main.getMySQL().update("UPDATE StaffCore_punishmentsdb SET UNBANNED = '1', UNBAN_TIME = '"+ System.currentTimeMillis() +"', "
								+ "UNBAN_REASON = '"+ reason +"', UNBAN_STAFF = '"+ teamuuid +"' WHERE PUNISH_ID = '"+ banid +"'");
						
						Main.getMySQL().update("DELETE FROM StaffCore_bansdb WHERE BANNED_UUID = '"+ targetuuid +"'");
						
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
						
						String banid = getBanID(targetuuid);
						
						Main.getMySQL().update("UPDATE StaffCore_punishmentsdb SET UNBANNED = '1', UNBAN_TIME = '"+ System.currentTimeMillis() +"', "
								+ "UNBAN_REASON = '"+ reason +"', UNBAN_STAFF = '"+ teamuuid +"' WHERE PUNISH_ID = '"+ banid +"'");
						
						Main.getMySQL().update("DELETE FROM StaffCore_bansdb WHERE BANNED_UUID = '"+ targetuuid +"'");
						
					} else {
						BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
						BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§7MySQL Connection§8)"));
						BungeeCord.getInstance().getConsole().sendMessage(new TextComponent(""));
					}
				}
				
			});
			
		}
		
	}
	
	public static boolean isIpBanned(String ip) {
		
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT BAN_END FROM StaffCore_ipbansdb WHERE IP_ADDRESS = '"+ ip +"'");
			
			try {
				if(rs.next()) {
					if(rs.getLong("BAN_END") > System.currentTimeMillis()) {
						return true;
					} else {
						return false;
					}
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
		}
		return false;
		
	}
	
	public static void banIp(String targetuuid, String teamuuid) {
		
		if(!PlayerManager.isProtected(targetuuid)) {
			
			String banned_ip = PlayerManager.getLastKnownIp(targetuuid);
			long banEnd = (Main.getInstance().getConfig().getLong("IPBan.Duration-In-Hours") * 1000 * 60 * 60) + System.currentTimeMillis();
			
			if(Main.getInstance().getConfig().getBoolean("ViolationLevelSystem.Enable")) {
				ViolationLevelHandler.addVL(targetuuid, Violation.IP_BAN);
			}
			
			Main.getMySQL().update("INSERT INTO StaffCore_ipbansdb(BANNED_UUID,IP_ADDRESS,TEAM_UUID,BAN_END) VALUES ('"+ targetuuid +"','"+ banned_ip +"','"+ teamuuid +"','"+ banEnd +"')");
			
			ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
			
			if(target != null) {
				
				Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,BAN_START,BAN_END,SUB_SERVER) VALUES ('IPBAN','"+ targetuuid +"','"+ teamuuid +"'"
						+ ",'"+ System.currentTimeMillis() +"','"+ banEnd +"','"+ target.getServer().getInfo().getName() +"')");
				
			} else {
				Main.getMySQL().update("INSERT INTO StaffCore_punishmentsdb(TYPE,UUID,TEAM_UUID,BAN_START,BAN_END) VALUES ('IPBAN','"+ targetuuid +"','"+ teamuuid +"'"
						+ ",'"+ System.currentTimeMillis() +"','"+ banEnd +"')");
			}	
			
			for(ProxiedPlayer all : BungeeCord.getInstance().getPlayers()) {
				
				String rawip = all.getPendingConnection().getSocketAddress().toString();
				String ip = rawip.substring(1, rawip.length() - 6);
				
				if(ip.matches(banned_ip)) {
					all.disconnect(new TextComponent(Main.getMSG("Messages.Ban-System.IP-Ban.Kick-Screen")));
				}
				
			}
			
			NotificationManager.sendIpBanNotify(targetuuid, teamuuid, (Main.getInstance().getConfig().getLong("IPBan.Duration-In-Hours") * 1000 * 60 * 60));
			
		} else {
			NotificationManager.sendNotify(NotificationSender.CONSOLE_NOTIFY, "PROTECT", targetuuid);
		}
		
	}
	
	public static void unbanIp(String targetuuid, String teamuuid) {
		
		Main.getMySQL().update("DELETE FROM StaffCore_ipbansdb WHERE BANNED_UUID = '"+ targetuuid +"'");
		NotificationManager.sendIpUnBanNotify(targetuuid, teamuuid);
		
	}
}
