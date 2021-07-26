package de.lacodev.staffbungee.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.ReportStatus;
import de.lacodev.staffbungee.enums.ReportType;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReportManager {
	
	public static HashMap<String, String> claimed = new HashMap<>();

	public static void createAutomaticReport(ReportType type, String targetuuid, String reason) {
		
		Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				
				if(Main.getMySQL().isConnected()) {
					
					Main.getMySQL().update("INSERT INTO StaffCore_reportsdb(TYPE,UUID,REASON,STATUS,CREATED_AT,UPDATED_AT) VALUES "
							+ "('"+ type.toString() +"','"+ targetuuid +"','"+ reason +"','"+ ReportStatus.CREATED.getStatus() +"',"
									+ "'"+ System.currentTimeMillis() +"','"+ System.currentTimeMillis() +"')");
					
					addReport(targetuuid);
					
					NotificationManager.sendAutoReportNotify(targetuuid, reason);
					
				}
				
			}
			
		});
		
	}
	
	public static void createPlayerReport(ReportType type, String targetuuid, String reporteruuid, String reason) {
		
		Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				
				if(Main.getMySQL().isConnected()) {
					
					Main.getMySQL().update("INSERT INTO StaffCore_reportsdb(TYPE,UUID,REPORTER_UUID,REASON,STATUS,CREATED_AT,UPDATED_AT) VALUES "
							+ "('"+ type.toString() +"','"+ targetuuid +"','"+ reporteruuid +"','"+ reason +"','"+ ReportStatus.CREATED.getStatus() +"'"
									+ ",'"+ System.currentTimeMillis() +"','"+ System.currentTimeMillis() +"')");
					
					addReport(targetuuid);
					
					NotificationManager.sendPlayerReportNotify(targetuuid, reporteruuid, reason);
					
				}
				
			}
			
		});
		
	}
	
	public static boolean hasOpenReports(String targetuuid) {
		
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT STATUS FROM StaffCore_reportsdb WHERE UUID = '"+ targetuuid +"' AND STATUS = '"+ ReportStatus.CREATED.getStatus() +"'");	
			
			try {
				return rs.next();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
		
	}
	
	public static void claimReports(String targetuuid, String teamuuid) {
		
		Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				
				if(Main.getMySQL().isConnected()) {
					
					Main.getMySQL().update("UPDATE StaffCore_reportsdb SET TEAM_UUID = '"+ teamuuid +"',STATUS = '"+ ReportStatus.CLAIMED.getStatus() +"',"
							+ "UPDATED_AT = '"+ System.currentTimeMillis() +"' WHERE UUID = '"+ targetuuid +"'");
					
					claimed.put(teamuuid, targetuuid);
					
					try {
						ArrayList<String> reporters = getReporters(targetuuid);
						
						if(!reporters.isEmpty()) {
							
							for(String uuid : reporters) {
								
								ProxiedPlayer reporter = BungeeCord.getInstance().getPlayer(UUID.fromString(uuid));
								
								if(reporter != null) {
									reporter.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Notify.User.Team-Claimed-Report")));
								}
								
							}
							
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					
				}
				
			}
			
		});
		
	}
	
	public static void confirmReport(String teamuuid) {
		Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				
				if(Main.getMySQL().isConnected()) {
					
					Main.getMySQL().update("UPDATE StaffCore_reportsdb SET STATUS = '"+ ReportStatus.CONFIRMED.getStatus() +"',"
							+ "UPDATED_AT = '"+ System.currentTimeMillis() +"' WHERE UUID = '"+ claimed.get(teamuuid) +"'");
					
					claimed.remove(teamuuid);
					
				}
				
			}
			
		});
	}
	
	public static void cancelReport(String teamuuid) {
		Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				
				if(Main.getMySQL().isConnected()) {
					
					Main.getMySQL().update("UPDATE StaffCore_reportsdb SET STATUS = '"+ ReportStatus.CANCELLED.getStatus() +"',"
							+ "UPDATED_AT = '"+ System.currentTimeMillis() +"' WHERE UUID = '"+ claimed.get(teamuuid) +"'");
					
					claimed.remove(teamuuid);
					
				}
				
			}
			
		});
	}
	
	private static ArrayList<String> getReporters(String targetuuid) throws SQLException{
		ArrayList<String> reporters = new ArrayList<>();
		
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT REPORTER_UUID FROM StaffCore_reportsdb WHERE UUID = '"+ targetuuid +"' AND TYPE = '"+ ReportType.PLAYER_REPORT.toString() +"'");	
			
			while(rs.next()) {
				if(!reporters.contains(rs.getString("REPORTER_UUID"))) {
					reporters.add(rs.getString("REPORTER_UUID"));
				}
			}
			return reporters;
		}
		return reporters;
		
	}
	
	public static ArrayList<String> getReportReasons(String uuid) throws SQLException {
		ArrayList<String> reports = new ArrayList<>();
		
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT REASON FROM StaffCore_reportsdb WHERE UUID = '"+ uuid +"'");
			
			while(rs.next()) {
				if(!reports.contains(rs.getString("REASON"))) {
					reports.add(rs.getString("REASON"));
				}
			}
			return reports;
		}
		return reports;
	}
	
	public static ArrayList<String> getOpenReports() throws SQLException {
		ArrayList<String> reports = new ArrayList<>();
		
		if(Main.getMySQL().isConnected()) {
			
			ResultSet rs = Main.getMySQL().query("SELECT UUID FROM StaffCore_reportsdb WHERE STATUS = '"+ ReportStatus.CREATED.getStatus() +"'");
			
			while(rs.next()) {
				if(!reports.contains(rs.getString("UUID"))) {
					reports.add(rs.getString("UUID"));
				}
			}
			return reports;
		}
		return reports;
	}
	
	private static void addReport(String uuid) {
		if(Main.getMySQL().isConnected()) {
			Main.getMySQL().update("UPDATE StaffCore_playerdb SET REPORTS = '"+ (PlayerManager.getReports(uuid) + 1) +"' WHERE UUID = '"+ uuid +"'");
		}
	}
	
}
