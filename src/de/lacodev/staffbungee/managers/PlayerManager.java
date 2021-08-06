package de.lacodev.staffbungee.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.PunishmentType;
import de.lacodev.staffbungee.handlers.ViolationLevelHandler;
import de.lacodev.staffbungee.listeners.ListenerLogin;
import de.lacodev.staffbungee.objects.Message;
import de.lacodev.staffbungee.objects.Punishment;
import de.lacodev.staffbungee.objects.Session;
import de.lacodev.staffbungee.utils.IPLookup;
import de.lacodev.staffbungee.utils.VersionDetector;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerManager {
	
	public static void createPlayerData(ProxiedPlayer player) {
		Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				if(Main.getMySQL().isConnected()) {
					if(!existsPlayerData(player.getUniqueId().toString())) {
						
						String ip = player.getSocketAddress().toString();
						
						Main.getMySQL().update("INSERT INTO StaffCore_playerdb(UUID,PLAYERNAME,BANS,MUTES,REPORTS,WARNS,LAST_KNOWN_IP,FIRST_LOGIN,LAST_LOGIN,PROTECTED,COUNTRY,REGION,ONLINE) "
								+ "VALUES ('"+ player.getUniqueId().toString() +"','"+ player.getDisplayName() +"','0','0','0','0',"
										+ "'"+ ip.substring(1, ip.length() - 6) +"','"+ System.currentTimeMillis() +"','"+ System.currentTimeMillis() +"','0',"
												+ "'"+ IPLookup.getCountry(ip.substring(1, ip.length() - 6)) +"','"+ IPLookup.getRegion(ip.substring(1, ip.length() - 6)) +"','1')");
						
						Main.getMySQL().update("INSERT INTO StaffCore_violationleveldb(UUID,VL) VALUES ('"+ player.getUniqueId().toString() +"','0')");
						Main.getMySQL().update("INSERT INTO StaffCore_violationresetdb(UUID,NEXT_RESET) VALUES ('"+ player.getUniqueId().toString() +"','0')");
						
					} else {
						updatePlayerData(player);
					}
				}
			}
			
		});
	}

	private static void updatePlayerData(ProxiedPlayer player) {
		String ip = player.getSocketAddress().toString();
		
		Main.getMySQL().update("UPDATE StaffCore_playerdb SET PLAYERNAME = '"+ player.getDisplayName() +"',"
				+ "LAST_KNOWN_IP = '"+ ip.substring(1, ip.length() - 6) +"',LAST_LOGIN = '"+ System.currentTimeMillis() +"',"
						+ "COUNTRY = '"+ IPLookup.getCountry(ip.substring(1, ip.length() - 6)) +"',"
								+ "REGION = '"+ IPLookup.getRegion(ip.substring(1, ip.length() - 6)) +"',"
										+ "ONLINE = '1' WHERE UUID = '"+ player.getUniqueId().toString() +"'");
		
		if(!ViolationLevelHandler.hasVL(player.getUniqueId().toString())) {
			Main.getMySQL().update("INSERT INTO StaffCore_violationleveldb(UUID,VL) VALUES ('"+ player.getUniqueId().toString() +"','0')");
		}
		if(!ViolationLevelHandler.hasLastReset(player.getUniqueId().toString())) {
			Main.getMySQL().update("INSERT INTO StaffCore_violationresetdb(UUID,NEXT_RESET) VALUES ('"+ player.getUniqueId().toString() +"','0')");
		}
		
	}
	
	public static void setPlayerToOffline(ProxiedPlayer player) {
		
		Main.getMySQL().update("UPDATE StaffCore_playerdb SET ONLINE = '0' WHERE UUID = '"+ player.getUniqueId().toString() +"'");
		
	}

	public static boolean existsPlayerData(String uuid) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT UUID FROM StaffCore_playerdb WHERE UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getString("UUID") != null;
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static String getUUIDByName(String name) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT UUID FROM StaffCore_playerdb WHERE PLAYERNAME = '"+ name +"'");
			
			try {
				if(rs.next()) {
					return rs.getString("UUID");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} else {
			return null;
		}
		return null;
	}
	
	public static String getStatus(String uuid) {
		ProxiedPlayer player = BungeeCord.getInstance().getPlayer(UUID.fromString(uuid));
		
		if(player != null) {
			if(player.isForgeUser()) {
				return "§aOnline §8| §cFORGE §8| §7" + VersionDetector.getClientProtocol(player.getPendingConnection());
			} else {
				return "§aOnline §8| §c"+ getBrand(player.getName()) +" §8| §7" + VersionDetector.getClientProtocol(player.getPendingConnection());
			}
		} else {
			return "§cOffline";
		}
	}
	
	public static String getBrand(String name) {
		return ListenerLogin.brands.get(name);
	}

	public static String getUsernamebyUUID(String uuid) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT PLAYERNAME FROM StaffCore_playerdb WHERE UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getString("PLAYERNAME");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} else {
			return null;
		}
		return null;
	}
	
	public static ArrayList<Message> getMessages(String uuid) throws SQLException{
		ArrayList<Message> messages = new ArrayList<>();
		
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT * FROM StaffCore_messages WHERE SENDER_UUID = '"+ uuid +"' ORDER BY id ASC");
			
			while(rs.next()) {
				messages.add(new Message(rs.getInt("id"),rs.getString("SENDER_UUID"),rs.getString("MESSAGE"),rs.getString("reg_date")));
			}
			return messages;
			
		} else {
			return messages;
		}
	}
	
	public static String getCountry(String uuid) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT COUNTRY FROM StaffCore_playerdb WHERE UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getString("COUNTRY");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} else {
			return "UNKNOWN";
		}
		return "UNKNOWN";
	}
	
	public static String getRegion(String uuid) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT REGION FROM StaffCore_playerdb WHERE UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getString("REGION");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} else {
			return "UNKNOWN";
		}
		return "UNKNOWN";
	}
	
	public static String getLastKnownIp(ProxiedPlayer player, String uuid) {
		if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.System.Show-IP"))) {
			if(Main.getMySQL().isConnected()) {
				ResultSet rs = Main.getMySQL().query("SELECT LAST_KNOWN_IP FROM StaffCore_playerdb WHERE UUID = '"+ uuid +"'");
				
				try {
					if(rs.next()) {
						return rs.getString("LAST_KNOWN_IP");
					}
				} catch(SQLException e) {
					e.printStackTrace();
				}
			} else {
				return "UNKNOWN";
			}
			return "UNKNOWN";
		} else {
			return "§kUNKNOWN";
		}
	}
	
	public static String getLastKnownIp(String uuid) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT LAST_KNOWN_IP FROM StaffCore_playerdb WHERE UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getString("LAST_KNOWN_IP");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} else {
			return "UNKNOWN";
		}
		return "UNKNOWN";
	}
	
	public static Long getLastOnline(String uuid) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT LAST_LOGIN FROM StaffCore_playerdb WHERE UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getLong("LAST_LOGIN");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} else {
			return (long) 0;
		}
		return (long) 0;
	}
	
	public static Long getFirstOnline(String uuid) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT FIRST_LOGIN FROM StaffCore_playerdb WHERE UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getLong("FIRST_LOGIN");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} else {
			return (long) 0;
		}
		return (long) 0;
	}
	
	public static Integer getBans(String uuid) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT BANS FROM StaffCore_playerdb WHERE UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getInt("BANS");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} else {
			return 0;
		}
		return 0;
	}
	
	public static Integer getMutes(String uuid) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT MUTES FROM StaffCore_playerdb WHERE UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getInt("MUTES");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} else {
			return 0;
		}
		return 0;
	}
	
	public static Integer getReports(String uuid) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT REPORTS FROM StaffCore_playerdb WHERE UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getInt("REPORTS");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} else {
			return 0;
		}
		return 0;
	}
	
	public static Integer getWarns(String uuid) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT WARNS FROM StaffCore_playerdb WHERE UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getInt("WARNS");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} else {
			return 0;
		}
		return 0;
	}
	
	public static boolean isProtected(String uuid) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT PROTECTED FROM StaffCore_playerdb WHERE UUID = '"+ uuid +"'");
			
			try {
				if(rs.next()) {
					return rs.getBoolean("PROTECTED");
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} else {
			return false;
		}
		return false;
	}

	public static String getOnlineTime(String uuid) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT SUM(SESSION_END - SESSION_START) as playtime FROM StaffCore_sessionsdb WHERE UUID = '"+ uuid +"' AND FINISHED = '1'");
			
			try {
				if(rs.next()) {
					long millis = rs.getLong("playtime");
					
					long sekunden = 0L;
					long minuten = 0L;
					long stunden = 0L;
					long tage = 0L;
					
					while(millis >= 1000L) {
						millis -= 1000L;
						sekunden += 1L;
					}
					while(sekunden >= 60L) {
						sekunden -= 60L;
						minuten += 1L;
					}
					while(minuten >= 60L) {
						minuten -= 60L;
						stunden += 1L;
					}
					while(stunden >= 24L) {
						stunden -= 24L;
						tage += 1L;
					}
					
					String strtage = String.valueOf(tage);
					String strstunden = String.valueOf(stunden);
					String strminuten = String.valueOf(minuten);
					String strsekunden = String.valueOf(sekunden);
					
					if(tage < 10) {
						strtage = "0" + String.valueOf(tage);
					}
					
					if(stunden < 10) {
						strstunden = "0" + String.valueOf(stunden);
					}
					
					if(minuten < 10) {
						strminuten = "0" + String.valueOf(minuten);
					}
					
					if(sekunden < 10) {
						strsekunden = "0" + String.valueOf(sekunden);
					}
					
					if (tage != 0L) {
						return "§a" + strtage + "§7 d, §a" + strstunden + "§7 h, §a" + strminuten + "§7 min";
					}
					if ((tage == 0L) && (stunden != 0L)) {
						return "§a" + strstunden + "§7 h, §a" + strminuten + "§7 min, §a" + strsekunden + "§7 sec";
					}
					if ((tage == 0L) && (stunden == 0L) && (minuten != 0L)) {
						return "§a" + strminuten + "§7 min, §a" + strsekunden + " §7 sec";
					}
					if ((tage == 0L) && (stunden == 0L) && (minuten == 0L) && (sekunden != 0L)) {
						return "§a" + strsekunden + " §7sec";
					}
					return "§4Error in calculation!";
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return "§4Error in calculation!";
	}
	
	public static ArrayList<Session> getSessions(String uuid) throws SQLException {
		ArrayList<Session> sessions = new ArrayList<>();
		
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT * FROM StaffCore_sessionsdb WHERE UUID = '"+ uuid +"' AND FINISHED = '1' ORDER BY id DESC LIMIT 10");
			
			while(rs.next()) {
				sessions.add(new Session(rs.getString("SESSION_ID"), rs.getLong("SESSION_START"), rs.getLong("SESSION_END")));
			}
			return sessions;
			
		} else {
			return sessions;
		}
	}
	
	public static ArrayList<Punishment> getPunishments(String uuid) throws SQLException {
		ArrayList<Punishment> punishments = new ArrayList<>();
		
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT * FROM StaffCore_punishmentsdb WHERE UUID = '"+ uuid +"' ORDER BY id DESC LIMIT 10");
			
			while(rs.next()) {
				punishments.add(new Punishment(rs.getInt("id"), PunishmentType.valueOf(rs.getString("TYPE")), rs.getString("REASON"), rs.getString("UUID"), 
						rs.getString("TEAM_UUID"), rs.getLong("BAN_START"), rs.getLong("BAN_END"), rs.getString("SUB_SERVER"), rs.getBoolean("UNBANNED"), 
						rs.getLong("UNBAN_TIME"), rs.getString("UNBAN_REASON"), rs.getString("UNBAN_STAFF")));
			}
			return punishments;
			
		} else {
			return punishments;
		}
	}
	
	public static ArrayList<Punishment> getPunishments(String uuid, PunishmentType type) throws SQLException {
		ArrayList<Punishment> punishments = new ArrayList<>();
		
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT * FROM StaffCore_punishmentsdb WHERE UUID = '"+ uuid +"' AND TYPE = '"+ type.toString() +"' ORDER BY id DESC LIMIT 10");
			
			while(rs.next()) {
				punishments.add(new Punishment(rs.getInt("id"), type, rs.getString("REASON"), rs.getString("UUID"), 
						rs.getString("TEAM_UUID"), rs.getLong("BAN_START"), rs.getLong("BAN_END"), rs.getString("SUB_SERVER"), rs.getBoolean("UNBANNED"), 
						rs.getLong("UNBAN_TIME"), rs.getString("UNBAN_REASON"), rs.getString("UNBAN_STAFF")));
			}
			return punishments;
			
		} else {
			return punishments;
		}
	}
	
	public static Punishment getPunishment(int id) throws SQLException {
		
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT * FROM StaffCore_punishmentsdb WHERE id = '"+ id +"'");
			
			Punishment punishments = null;
			
			while(rs.next()) {
			  punishments = new Punishment(rs.getInt("id"), PunishmentType.valueOf(rs.getString("TYPE")), rs.getString("REASON"), rs.getString("UUID"), 
					  rs.getString("TEAM_UUID"), rs.getLong("BAN_START"), rs.getLong("BAN_END"), rs.getString("SUB_SERVER"), rs.getBoolean("UNBANNED"), 
						rs.getLong("UNBAN_TIME"), rs.getString("UNBAN_REASON"), rs.getString("UNBAN_STAFF"));
			}
			return punishments;
			
		}
		return null;
	}
	
	public static void reset(String uuid) {
		
		if(Main.getMySQL().isConnected()) {
			
			Main.getMySQL().update("DELETE FROM StaffCore_playerdb WHERE UUID = '"+ uuid +"'");
			Main.getMySQL().update("DELETE FROM StaffCore_sessionsdb WHERE UUID = '"+ uuid +"'");
			Main.getMySQL().update("DELETE FROM StaffCore_messages WHERE SENDER_UUID = '"+ uuid +"'");
			Main.getMySQL().update("DELETE FROM StaffCore_bansdb WHERE BANNED_UUID = '"+ uuid +"'");
			Main.getMySQL().update("DELETE FROM StaffCore_mutesdb WHERE MUTED_UUID = '"+ uuid +"'");
			Main.getMySQL().update("DELETE FROM StaffCore_warnsdb WHERE WARNED_UUID = '"+ uuid +"'");
			Main.getMySQL().update("DELETE FROM StaffCore_punishmentsdb WHERE UUID = '"+ uuid +"'");
			
		}
		
	}
}
