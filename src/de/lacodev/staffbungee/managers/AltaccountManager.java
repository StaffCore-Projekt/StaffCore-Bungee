package de.lacodev.staffbungee.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.lacodev.staffbungee.Main;

public class AltaccountManager {
	
	public static ArrayList<String> collectForPlayer(String name) {
		ArrayList<String> alts = new ArrayList<>();
		
		String last_ip = PlayerManager.getLastKnownIp(PlayerManager.getUUIDByName(name));
		
		ResultSet rs = Main.getMySQL().query("SELECT * FROM StaffCore_playerdb WHERE LAST_KNOWN_IP LIKE '%"+ last_ip +"%' AND NOT PLAYERNAME = '"+ name +"'");
		
		try {
			while(rs.next()) {
				alts.add(rs.getString("PLAYERNAME"));
			}
			return alts;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return alts;
	}

	public static ArrayList<String> collectForIp(String ip) {
		ArrayList<String> alts = new ArrayList<>();
		
		ResultSet rs = Main.getMySQL().query("SELECT * FROM StaffCore_playerdb WHERE LAST_KNOWN_IP LIKE '%"+ ip +"%'");
		
		try {
			while(rs.next()) {
				alts.add(rs.getString("PLAYERNAME"));
			}
			return alts;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return alts;
	}
}
