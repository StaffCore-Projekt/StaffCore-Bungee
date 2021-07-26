package de.lacodev.staffbungee.managers;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.lacodev.staffbungee.Main;

public class BlackListManager {
	
	public static boolean isBlacklisted(String username) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT USERNAME FROM StaffCore_blacklistdb WHERE USERNAME = '"+ username +"'");
			
			try {
				if(rs.next()) {
					return rs.getString("USERNAME") != null;
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void add(String username, String team) {
		if(Main.getMySQL().isConnected()) {
			Main.getMySQL().update("INSERT INTO StaffCore_blacklistdb(USERNAME,ADDED_AT,ADDED_BY) VALUES ('"+ username +"','"+ System.currentTimeMillis() +"','"+ team +"')");
		}
	}
	
	public static void remove(String username) {
		if(Main.getMySQL().isConnected()) {
			Main.getMySQL().update("DELETE FROM StaffCore_blacklistdb WHERE USERNAME = '"+ username +"'");
		}
	}
	
}
