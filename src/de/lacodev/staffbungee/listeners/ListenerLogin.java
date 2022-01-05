package de.lacodev.staffbungee.listeners;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.Settings;
import de.lacodev.staffbungee.handlers.ViolationLevelHandler;
import de.lacodev.staffbungee.managers.BanManager;
import de.lacodev.staffbungee.managers.BlackListManager;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.managers.ReportManager;
import de.lacodev.staffbungee.managers.SettingsManager;
import de.lacodev.staffbungee.managers.StaffCoreUIManager;
import de.lacodev.staffbungee.objects.LabyModInfo;
import de.lacodev.staffbungee.utils.IPLookup;
import de.lacodev.staffbungee.utils.StringGenerator;
import de.lacodev.staffbungee.utils.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ListenerLogin implements Listener {
	
	public static HashMap<String, String> brands = new HashMap<>();
	
	public static HashMap<String, LabyModInfo> labymod_info = new HashMap<>();

	@EventHandler
	public void onPreLogin(PreLoginEvent e) {
		String uuid = UUIDFetcher.getUUID(e.getConnection().getName());
		String ip = e.getConnection().getSocketAddress().toString();
		
		if(Main.getAntiMCLeaksHandler().isAccountCached(uuid)) {
			
			for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
				if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.System.Notify"))) {
					all.sendMessage(new TextComponent(Main.getMSG("Messages.System.Alerts.MCLeaks-AccountBlock").replace("%target%", e.getConnection().getName())));
				}
			}
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(Main.getMSG("Messages.System.Alerts.MCLeaks-AccountBlock").replace("%target%", e.getConnection().getName())));
			
			e.setCancelReason(new TextComponent(Main.getMSG("Messages.System.MCLeaks-Blocker.Blocked-Accounts.Kick")));
			e.setCancelled(true);
			
		} else if(BanManager.isBanned(uuid)) {
			
			if(BanManager.getRawEnd(uuid) != -1) {
				
				e.setCancelReason(new TextComponent(Main.getMSG("Messages.Layouts.Ban")
						.replace("%reason%", BanManager.getReason(uuid))
						.replace("%remaining%", BanManager.getFormattedEnd(uuid))
						.replace("%lengthvalue%", Main.getMSG("Messages.Layouts.Ban.Length-Values.Temporarly"))
						));
				e.setCancelled(true);
				
			} else {
				
				e.setCancelReason(new TextComponent(Main.getMSG("Messages.Layouts.Ban")
						.replace("%reason%", BanManager.getReason(uuid))
						.replace("%remaining%", Main.getMSG("Messages.Layouts.Ban.Length-Values.Permanently"))
						.replace("%lengthvalue%", Main.getMSG("Messages.Layouts.Ban.Length-Values.Permanently"))
						));
				e.setCancelled(true);
				
			}
			
		} else if(BanManager.isIpBanned(ip.substring(1, ip.length() - 6))) {
			
			e.setCancelReason(new TextComponent(Main.getMSG("Messages.Ban-System.IP-Ban.Kick-Screen")));
			e.setCancelled(true);
			
		} else if(BlackListManager.isBlacklisted(e.getConnection().getName())) {
			
			e.setCancelReason(new TextComponent(Main.getMSG("Messages.System.Blacklist.Kick-Screen")));
			e.setCancelled(true);
			
		}
	}
	
	@EventHandler
	public void onPostLogin(PostLoginEvent e) throws Exception {
		ProxiedPlayer player = e.getPlayer();
		
		if(Main.getMaintenanceHandler().isMaintenance()) {
			
			if(!player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) && !player.hasPermission(Main.getPermissionNotice("Permissions.Maintenance.Bypass"))) {
				
				e.getPlayer().getPendingConnection().disconnect(new TextComponent(ChatColor.translateAlternateColorCodes('&', SettingsManager.getValue(Settings.MAINTENANCE_TEXT_LINE1)) + "\n" + ChatColor.translateAlternateColorCodes('&', SettingsManager.getValue(Settings.MAINTENANCE_TEXT_LINE2))));
	
			}
			
		} else {
			
			String ip = player.getSocketAddress().toString();
			IPLookup.logIp(ip.substring(1, ip.length() - 6));
			
			PlayerManager.createPlayerData(player);
			
			ViolationLevelHandler.reset(player.getUniqueId().toString());
			
			if(IPLookup.isUsingProxy(PlayerManager.getLastKnownIp(player.getUniqueId().toString()))) {
				for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
					if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.System.Notify"))) {
						all.sendMessage(new TextComponent(Main.getMSG("Messages.System.Alerts.VPN-Detected").replace("%target%", player.getDisplayName())));
					}
				}
				Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(Main.getMSG("Messages.System.Alerts.VPN-Detected").replace("%target%", player.getDisplayName())));
				
				if(Main.getInstance().getConfig().getBoolean("VPN-Detection.Allow-VPN-Join") == false) {
					if(!(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.System.VPN-Block.Bypass")))) {
						player.disconnect(new TextComponent(Main.getMSG("Messages.System.VPN-Detection.Kick")));
					}
				}
			}
			
			if(player.isForgeUser()) {
				for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
					if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.System.Notify"))) {
						all.sendMessage(new TextComponent(Main.getMSG("Messages.System.Alerts.Forge-Detected").replace("%target%", player.getDisplayName())));
					}
				}
				Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(Main.getMSG("Messages.System.Alerts.Forge-Detected").replace("%target%", player.getDisplayName())));
			}
		}
		
		if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything"))) {
			if(!Main.getInstance().latest) {
				if(Main.getInstance().getDescription().getVersion().endsWith("Pre")) {
					player.sendMessage(new TextComponent("§cSystem §8» §7You are using an §dexperimental build§7!"));
					player.sendMessage(new TextComponent("§cSystem §8» §7Download the latest version ("+ Main.getInstance().version +") here:"));
					player.sendMessage(new TextComponent("§cSystem §8» §chttps://www.staffcore-bungee.net"));
					player.sendMessage(new TextComponent(""));
				} else {
					player.sendMessage(new TextComponent("§cSystem §8» §7You are using an §coutdated build§7!"));
					player.sendMessage(new TextComponent("§cSystem §8» §7Download the latest version ("+ Main.getInstance().version +") here:"));
					player.sendMessage(new TextComponent("§cSystem §8» §chttps://www.staffcore-bungee.net"));
					player.sendMessage(new TextComponent(""));
				}
			}
		}
		
		Main.getSessionsHandler().startSession(player.getUniqueId().toString());
		
		if(player.hasPermission(Main.getPermissionNotice("Permissions.TeamChat.AutoLogin"))) {
			Main.getTeamChat().login(player);
		} else if(player.hasPermission(Main.getPermissionNotice("Permissions.TeamChat.Ghost-AutoLogin"))) {
			Main.getTeamChat().ghostLogin(player);
		}
		
		StaffCoreUIManager.syncOnJoin(player);
	}
	
	@EventHandler
	public void onQuit(PlayerDisconnectEvent e) {
		ProxiedPlayer player = e.getPlayer();
		
		if(!Main.getMaintenanceHandler().isMaintenance()) {
			
			Main.getSessionsHandler().stopSession(player.getUniqueId().toString());
			PlayerManager.setPlayerToOffline(player);
			
		}
		
		if(ReportManager.claimed.containsKey(player.getUniqueId().toString())) {
			ReportManager.cancelReport(player.getUniqueId().toString());
		}
		
		Main.getTeamChat().logout(player);
	}
	
	  @SuppressWarnings("deprecation")
	@EventHandler
	  public void onPluginMessageReceived(PluginMessageEvent e) {
	    if (e.getSender() instanceof ProxiedPlayer) {
	    	ProxiedPlayer p = (ProxiedPlayer) e.getSender();
	    	String player = p.getName();
	      try {
	    	  if(e.getTag().contains("labymod")) {
	    		  if(brands.containsKey(player)) {
	    			  brands.replace(player, "LabyMod v3");
	    		  } else {
	    			  brands.put(player, "LabyMod v3");
	    		  }
	    	  } else if(e.getTag().contains("LMC")) {
	    		  String response = StringGenerator.readChannelMessage(new String(e.getData(), "UTF-8"));
	    		  
	    		  if(response.startsWith("INFO")) {
	    			  String stripped_response = response.substring(6);
	    			  JsonParser parser = new JsonParser();
	    			  
	    			  JsonElement element = parser.parse(stripped_response);
	    			  JsonObject obj = element.getAsJsonObject();
	    			  
	    			  String version = obj.get("version").getAsString();
	    			  ArrayList<String> mods = new ArrayList<>();
	    			  
	    			  JsonArray array = obj.get("addons").getAsJsonArray();
	    			  for(int i = 0; i < array.size(); i++) {
	    				  JsonObject child = array.get(i).getAsJsonObject();
	    				  mods.add(child.get("name").getAsString());
	    			  }
	    			  
	    			  if(labymod_info.containsKey(player)) {
	    				  labymod_info.replace(player, new LabyModInfo(version, mods));
	    			  } else {
	    				  labymod_info.put(player, new LabyModInfo(version, mods));
	    			  }
	    		  }
	    	  } else if(e.getTag().contains("the5zigmod")) {
	    		  if(brands.containsKey(player)) {
	    			  brands.replace(player, "5zig Mod");
	    		  } else {
	    			  brands.put(player, "5zig Mod");
	    		  }
	    	  } else if(e.getTag().contains("MC|Brand")) {
	    		  if(StringGenerator.readChannelMessage(new String(e.getData(), "UTF-8")).contains("lunarclient")) {
	    			  if(brands.containsKey(player)) {
		    			  brands.replace(player, "LunarClient");
		    		  } else {
		    			  brands.put(player, "LunarClient");
		    		  }
	    		  } else if(StringGenerator.readChannelMessage(new String(e.getData(), "UTF-8")).contains("vanilla")) {
	    			  if(brands.containsKey(player)) {
		    			  brands.replace(player, "Vanilla");
		    		  } else {
		    			  brands.put(player, "Vanilla");
		    		  }
	    		  } else if(StringGenerator.readChannelMessage(new String(e.getData(), "UTF-8")).contains("PLC18")) {
	    			  if(brands.containsKey(player)) {
		    			  brands.replace(player, "PvPLounge");
		    		  } else {
		    			  brands.put(player, "PvPLounge");
		    		  }
	    		  } else if(StringGenerator.readChannelMessage(new String(e.getData(), "UTF-8")).contains("LiteLoader")) {
	    			  if(brands.containsKey(player)) {
		    			  brands.replace(player, "LiteLoader");
		    		  } else {
		    			  brands.put(player, "LiteLoader");
		    		  }
	    		  } else if(StringGenerator.readChannelMessage(new String(e.getData(), "UTF-8")).contains("StaffCore-Client")) {
	    			  if(brands.containsKey(player)) {
		    			  brands.replace(player, "StaffCore");
		    		  } else {
		    			  brands.put(player, "StaffCore");
		    		  }
	    		  } else if(StringGenerator.readChannelMessage(new String(e.getData(), "UTF-8")).contains("Tecknix-Client")) {
	    			  if(brands.containsKey(player)) {
		    			  brands.replace(player, "TecknixClient");
		    		  } else {
		    			  brands.put(player, "TecknixClient");
		    		  }
	    		  }
	    	  } else if(e.getTag().contains("minecraft:brand")) {
	    		  if(StringGenerator.readChannelMessage(new String(e.getData(), "UTF-8")).contains("lunarclient")) {
	    			  if(brands.containsKey(player)) {
		    			  brands.replace(player, "LunarClient");
		    		  } else {
		    			  brands.put(player, "LunarClient");
		    		  }
	    		  } else if(StringGenerator.readChannelMessage(new String(e.getData(), "UTF-8")).contains("vanilla")) {
	    			  if(brands.containsKey(player)) {
		    			  brands.replace(player, "Vanilla");
		    		  } else {
		    			  brands.put(player, "Vanilla");
		    		  }
	    		  } else if(StringGenerator.readChannelMessage(new String(e.getData(), "UTF-8")).contains("PLC18")) {
	    			  if(brands.containsKey(player)) {
		    			  brands.replace(player, "PvPLounge");
		    		  } else {
		    			  brands.put(player, "PvPLounge");
		    		  }
	    		  } else if(StringGenerator.readChannelMessage(new String(e.getData(), "UTF-8")).contains("StaffCore-Client")) {
	    			  if(brands.containsKey(player)) {
		    			  brands.replace(player, "StaffCore");
		    		  } else {
		    			  brands.put(player, "StaffCore");
		    		  }
	    		  } else if(StringGenerator.readChannelMessage(new String(e.getData(), "UTF-8")).contains("Tecknix-Client")) {
	    			  if(brands.containsKey(player)) {
		    			  brands.replace(player, "TecknixClient");
		    		  } else {
		    			  brands.put(player, "TecknixClient");
		    		  }
	    		  }
	    	  }
	      } catch (UnsupportedEncodingException e1) {
	        e1.printStackTrace();
	      } 
	    }
	  }
}
