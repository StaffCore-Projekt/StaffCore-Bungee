package de.lacodev.staffbungee.listeners;

import java.util.HashMap;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.commands.CMDStaffCore;
import de.lacodev.staffbungee.enums.ReportType;
import de.lacodev.staffbungee.enums.Settings;
import de.lacodev.staffbungee.managers.MuteManager;
import de.lacodev.staffbungee.managers.ReportManager;
import de.lacodev.staffbungee.managers.SettingsManager;
import de.lacodev.staffbungee.utils.ChatDetector;
import de.lacodev.staffbungee.utils.StringGenerator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ListenerChat implements Listener {
	
	public HashMap<String, Integer> infractions = new HashMap<>();
	public static HashMap<ProxiedPlayer, Long> reportspam = new HashMap<>();

	@EventHandler
	public void onChat(ChatEvent e) {
		
		ProxiedPlayer player = (ProxiedPlayer) e.getSender();
		
		String uuid = player.getUniqueId().toString();
		
		
		if(CMDStaffCore.settings.containsKey(player)) {
			if(!e.getMessage().matches("cancel")) {
				
				if(CMDStaffCore.settings.get(player).equals(Settings.MOTD_ENABLE) || CMDStaffCore.settings.get(player).equals(Settings.MOTD_FAKEPLAYERS_ENABLE)) {
					
					SettingsManager.updateValue(CMDStaffCore.settings.get(player), e.getMessage().toUpperCase());
					CMDStaffCore.settings.remove(player);
					player.sendMessage(new TextComponent(Main.getPrefix() + "§7You §achanged §7the value of the Setting to:"));
					player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.translateAlternateColorCodes('&', e.getMessage())));
					
					e.setCancelled(true);
					
				} else {
					
					SettingsManager.updateValue(CMDStaffCore.settings.get(player), e.getMessage());
					CMDStaffCore.settings.remove(player);
					player.sendMessage(new TextComponent(Main.getPrefix() + "§7You §achanged §7the value of the Setting to:"));
					player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.translateAlternateColorCodes('&', e.getMessage())));
					
					e.setCancelled(true);
					
				}
			} else {
				player.sendMessage(new TextComponent(Main.getPrefix() + "§7You §ccancelled §7the editing of the Setting"));
				CMDStaffCore.settings.remove(player);
				e.setCancelled(true);
			}
		}
		
		if(Main.getTeamChat().isParticipant(player)) {
			
			if(e.getMessage().startsWith("@ ")) {
				
				Main.getTeamChat().sendMessage(player, e.getMessage().replace("@ ", ""));
				e.setCancelled(true);
				
			} else if(e.getMessage().startsWith("@")) {
				
				Main.getTeamChat().sendMessage(player, e.getMessage().replace("@", ""));
				e.setCancelled(true);
				
			}
		}
		
		if(!MuteManager.isMuted(uuid)) {
			
			Main.getMySQL().update("INSERT INTO StaffCore_messages(SENDER_UUID,MESSAGE) VALUES ('"+ uuid +"','"+ StringGenerator.getMySQLFriendly(e.getMessage()) +"')");
			
			if(e.getMessage().startsWith("/")) {
				
				// Command Executed on any server
				if(e.getMessage().contains("/report ")) {
					
					if(reportspam.containsKey(player)) {
						
						if(reportspam.get(player) >= System.currentTimeMillis()) {
							
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.AntiSpam")));
							e.setCancelled(true);
							
						}
						
					}
					
				}
				
			} else if(ChatDetector.containsAds(e.getMessage())) {
				
				if(!(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.ChatFilter.Bypass")))) {
					
					if(infractions.containsKey(uuid)) {
						
						infractions.replace(uuid, infractions.get(uuid) + 1);
						
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.Advertisment.Block")));
						e.setCancelled(true);
						
						if(infractions.get(uuid) >= Main.getInstance().getChatFilter().getInt("ChatFilter.Infractions-For-Action")) {
							ReportManager.createAutomaticReport(ReportType.AUTO_REPORT, uuid, "Advertising");
						}
						
					} else {
						infractions.put(uuid, 1);
						
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.Advertisment.Block")));
						e.setCancelled(true);
						
					}
					
				}
				
			} else if(ChatDetector.containsSwearWord(e.getMessage())) {
				
				if(!(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.ChatFilter.Bypass")))) {
					
					if(infractions.containsKey(uuid)) {
						
						infractions.replace(uuid, infractions.get(uuid) + 1);
						
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.Cursed-Words.Block")));
						e.setCancelled(true);
						
						if(infractions.get(uuid) >= Main.getInstance().getChatFilter().getInt("ChatFilter.Infractions-For-Action")) {
							ReportManager.createAutomaticReport(ReportType.AUTO_REPORT, uuid, "Swearing");
						}
						
					} else {
						infractions.put(uuid, 1);
						
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.Cursed-Words.Block")));
						e.setCancelled(true);
						
					}
					
				}
				
			}
			
		} else {
			
			if(!e.getMessage().startsWith("/")) {
				
				if(MuteManager.getRawEnd(uuid) != -1) {
					
					player.sendMessage(new TextComponent(Main.getMSG("Messages.Layouts.Mute").replace("%reason%", MuteManager.getReason(uuid)).replace("%remaining%", MuteManager.getFormattedEnd(uuid))));
					e.setCancelled(true);
					
				} else {
					
					player.sendMessage(new TextComponent(Main.getMSG("Messages.Layouts.Mute").replace("%reason%", MuteManager.getReason(uuid)).replace("%remaining%", Main.getMSG("Messages.Layouts.Ban.Length-Values.Permanently"))));
					e.setCancelled(true);
					
				}
				
			}
			
		}
		
	}
	
}
