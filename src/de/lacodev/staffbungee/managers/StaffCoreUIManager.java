package de.lacodev.staffbungee.managers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.lacodev.staffbungee.Main;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaffCoreUIManager {
	
	public static void sendVerification(ProxiedPlayer player) {
	    player.sendMessage(new TextComponent(""));
	    player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.Verification.Info-MSG")));

	    TextComponent tc = new TextComponent();
	    tc.setText(Main.getPrefix() + Main.getMSG("Messages.System.Verification.Info-Button"));
	    tc.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/staffcore verify"));

	    player.sendMessage(tc);

	    player.sendMessage(new TextComponent(""));
	}
	
	public static boolean isVerified(String uuid) {
	   if(Main.getMySQL().isConnected()) {
	      ResultSet rs = Main.getMySQL().query("SELECT uuid FROM StaffCoreUI_Accounts WHERE uuid = '" + uuid + "'");
	      try {
	    	  
	        if(rs != null) {
	          if(rs.next()) {
	            return rs.getString("uuid") != null;
	          }
	        }
	        
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
	    }
	    return false;
	}
	
	public static void verify(ProxiedPlayer player) {
		Main.getMySQL().update("UPDATE StaffCoreUI_Accounts SET uuid = '"+ player.getUniqueId().toString() +"' WHERE username = '"+ player.getDisplayName() +"'");
		
		player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.Verification.Success")));
	}
	
	public static void syncOnJoin(ProxiedPlayer player) {
		BungeeCord.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				ResultSet rs = Main.getMySQL().query("SELECT id,TYPE,PROCESS FROM StaffCoreUI_Sync_OnJoin WHERE USERNAME = '"+ player.getDisplayName() +"' ORDER BY id");
				
				try {
					
					while(rs.next()) {
						JsonParser parser = new JsonParser();
						
						JsonObject object = parser.parse(rs.getString("PROCESS")).getAsJsonObject();
						
						switch(rs.getString("TYPE")) {
						case "VERIFY_ACCOUNT":
							String username = object.get("username").getAsString();
							
							ProxiedPlayer player = BungeeCord.getInstance().getPlayer(username);
							if(player != null) {
								if(!StaffCoreUIManager.isVerified(player.getUniqueId().toString())) {
									StaffCoreUIManager.sendVerification(player);
								} else {
									Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync_OnJoin WHERE id = '"+ rs.getInt("id") +"'");
								}
							}
							break;
						}
					}
					
				} catch(NullPointerException e) {
					
				} catch (SQLException e) {
					
				}
			}
			
		});
	}
}
