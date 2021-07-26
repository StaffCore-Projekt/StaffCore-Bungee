package de.lacodev.staffbungee.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.lacodev.staffbungee.Main;

public class ChatDetector {

	public static boolean containsAds(String message) {
		
		String[] adtest = message.split(" ");
		ArrayList<String> whitelist = (ArrayList<String>) Main.getInstance().getChatFilter().getStringList("ChatFilter.Advertisment-Whitelist");
		ArrayList<String> ccTLD = (ArrayList<String>) Main.getInstance().getChatFilter().getStringList("ChatFilter.Blocked-Domains.ccTLD");
		ArrayList<String> gTLD = (ArrayList<String>) Main.getInstance().getChatFilter().getStringList("ChatFilter.Blocked-Domains.gTLD");
		
		for(String ad : adtest) {
			
			if(!whitelist.contains(ad)) {
				
				for(String ccd : ccTLD) {
					if(ad.contains(ccd)) {
						return true;
					}
				}
				for(String gd : gTLD) {
					if(ad.contains(gd)) {
						return true;
					}
				}
				
			} else {
				return false;
			}
			
		}
		return false;
		
	}
	
	public static boolean containsSwearWord(String message) {
		
		ArrayList<String> cursed = getChatFilter();
		
		for(int i = 0; i < cursed.size(); i++) {
			int minpercent = Main.getInstance().getChatFilter().getInt("ChatFilter.Match-for-Action-in-Percentage");
			
			String[] msg = message.split(" ");
		     for (String m : msg) {
		       if (cursed.get(i).toLowerCase().contains(m.toLowerCase())) {
		         double total = cursed.get(i).length();
		         double score = m.length();
		         float percentage = (float) ((score * 100) / total);
		         if (percentage > minpercent) {
		        	 System.out.println("Blocked: " + m + " ("+ percentage +"%)");
		        	 return true;
		         }
		      }
		   }
		}
		return false;
		
	}

	public static ArrayList<String> getChatFilter() {
		ArrayList<String> list = new ArrayList<>();
		
		ResultSet rs = Main.getMySQL().query("SELECT WORD FROM StaffCore_swearingdb");
		
		try {
			while(rs.next()) {
				list.add(rs.getString("WORD"));
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
		
	}
	
	public static boolean existsSwearingWord(String word) {
		
		ResultSet rs = Main.getMySQL().query("SELECT WORD FROM StaffCore_swearingdb WHERE WORD = '"+ word +"'");
		
		try {
			if(rs.next()) {
				return rs.getString("WORD") != null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
}
