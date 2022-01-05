package de.lacodev.staffbungee.enums;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.lacodev.staffbungee.Main;

public enum Extension {
	
	HOOK(""),PLUGIN_MANAGER("SC-EX-PluginManager"),TWOFA_AUTHENTICATION("SC-EX-2FA-Authenticator"),ANTICHEAT_HOOK("SC-EX-AntiCheatHook"),GUIS("SC-EX-GUI"),REWARDS("SC-EX-Rewards"),TASKS("SC-EX-Tasks");
	
	String name;
	
	Extension(String name) {
		this.name = name;
	}
	
	public boolean isActive() {
		ResultSet rs = Main.getMySQL().query("SELECT STATUS FROM StaffCore_extensionsdb WHERE TYPE = '"+ this.toString() +"'");
		
		try {
			if(rs.next()) {
				return rs.getBoolean("STATUS");
			}
		} catch (SQLException e) {
			return false;
		}
		return false;
	}
	
	public boolean exists() {
		ResultSet rs = Main.getMySQL().query("SELECT TYPE FROM StaffCore_extensionsdb WHERE TYPE = '"+ this.toString() +"'");
		
		try {
			if(rs.next()) {
				return rs.getString("TYPE") != null;
			}
		} catch (SQLException e) {
			return false;
		}
		return false;
	}
	

}
