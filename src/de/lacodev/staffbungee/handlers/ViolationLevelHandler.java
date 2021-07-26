package de.lacodev.staffbungee.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.Violation;

public class ViolationLevelHandler {
	
	public static boolean hasVL(String uuid) {
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT UUID FROM StaffCore_violationleveldb WHERE UUID = '"+ uuid +"'");
			
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
	
	public static void addVL(String uuid, Violation vl) {
		Main.getMySQL().update("UPDATE StaffCore_violationleveldb SET VL = '"+ (getVL(uuid) + vl.getVL()) +"' WHERE UUID = '"+ uuid +"'");
	}
	
	public static void setVL(String uuid, int vl) {
		Main.getMySQL().update("UPDATE StaffCore_violationleveldb SET VL = '"+ vl +"' WHERE UUID = '"+ uuid +"'");
	}
	
	public static int getVL(String uuid) {
		
		ResultSet rs = Main.getMySQL().query("SELECT VL FROM StaffCore_violationleveldb WHERE UUID = '"+ uuid +"'");
		
		try {
			if(rs.next()) {
				return rs.getInt("VL");
			}
		} catch (SQLException e) {
			
		}
		return 0;
	}
	
	public static long calculateLength(String uuid, long length, int vl) {
		
		if(length == -1) {
			return -1;
		} else if(vl >= 120) {
			return -1;
		} else {
			long day = (1000 * 60 * 60 * 24);
			long calc = day * vl;
			return length + calc;
		}
		
	}
}
