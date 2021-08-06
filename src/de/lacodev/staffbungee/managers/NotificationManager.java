package de.lacodev.staffbungee.managers;

import java.util.UUID;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.DiscordType;
import de.lacodev.staffbungee.enums.NotificationSender;
import de.lacodev.staffbungee.handlers.DiscordIntegrationHandler;
import de.lacodev.staffbungee.utils.ReasonLengthCalculator;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class NotificationManager {

	public static void sendNotify(NotificationSender sender, String type, String targetuuid) {
		if(sender.equals(NotificationSender.CONSOLE_NOTIFY)) {
			switch(type) {
				case "PROTECT":
					for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
						if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.System.Notify"))) {
							all.sendMessage(new TextComponent(""));
							all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.Player-Protected.Notify").replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))));
							all.sendMessage(new TextComponent(""));
						}
					}
					String notify = Main.getPrefix() + Main.getMSG("Messages.System.Player-Protected.Notify").replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid));
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify));
					break;
				case "WATCHLIST_ADD":
					for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
						if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.WatchList.Notify"))) {
							all.sendMessage(new TextComponent(""));
							all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.WatchList.Add-Player.Notify").replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))));
							all.sendMessage(new TextComponent(""));
						}
					}
					String notify1 = Main.getPrefix() + Main.getMSG("Messages.WatchList.Add-Player.Notify").replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid));
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify1));
					break;
				case "WATCHLIST_REMOVE":
					for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
						if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.WatchList.Notify"))) {
							all.sendMessage(new TextComponent(""));
							all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.WatchList.Remove-Player.Notify").replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))));
							all.sendMessage(new TextComponent(""));
						}
					}
					String notify11 = Main.getPrefix() + Main.getMSG("Messages.WatchList.Remove-Player.Notify").replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid));
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify11));
					break;
			}
		}
	}
	
	public static void sendServerChangeNotify(String targetuuid, String serverfrom, String serverto) {
		
		for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
			if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.WatchList.Notify"))) {
				all.sendMessage(new TextComponent(""));
				all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Watchlist.Server-Change.Notify")
				.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
				.replace("%server_from%", serverfrom)
				.replace("%server_to%", serverto)));
				all.sendMessage(new TextComponent(""));
			}
		}
		String notify = Main.getPrefix() + Main.getMSG("Messages.Watchlist.Server-Change.Notify")
		.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
		.replace("%server_from%", serverfrom)
		.replace("%server_to%", serverto);
		Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify));
		
		if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.WatchList-Alerts.Enable")) {
			
			try {
				new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.WatchList-Alerts.WebHook-URL")).sendMessageToWebHook(DiscordType.WATCHLIST_ALERT, ChatColor.stripColor(notify.replace("»", ">")), targetuuid);
			} catch (Exception e) {
				
			}
		}
	}
	
	public static void sendAutoReportNotify(String targetuuid, String reason) {
		
		for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
			if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.Report.Notify"))) {
				
				all.sendMessage(new TextComponent(""));
				
				TextComponent n1 = new TextComponent(Main.getPrefix() + Main.getMSG("Messages.ReportSystem.AutoReport.Notify").replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid)));
				n1.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new Text(Main.getPrefix() + Main.getMSG("Messages.ReportSystem.Notify.Prefix.Reason") + reason + "\n"
				+ Main.getPrefix() + "§7Server §8» §c" + BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid)).getServer().getInfo().getName() + "\n\n" + "§8» " + Main.getMSG("Messages.Report-System.Notify.Team.Teleport-Button"))));
				n1.setClickEvent(new ClickEvent(Action.RUN_COMMAND,"/report claim " + targetuuid));
				
				all.sendMessage(n1);
				
				all.sendMessage(new TextComponent(""));
				
			}
		}
		String notify = Main.getPrefix() + Main.getMSG("Messages.ReportSystem.AutoReport.Notify")
		.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid));
		Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify));
		Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(Main.getPrefix() + reason));
		
		if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Auto-Report.Enable")) {
			
			try {
				new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Player-Report.WebHook-URL")).sendMessageToWebHook(DiscordType.AUTO_REPORT, ChatColor.stripColor(notify.replace("»", ">") + "\nReason > " + reason + "\nServer > " + BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid)).getServer().getInfo().getName()), targetuuid);
			} catch (Exception e) {
				
			}
		}
	}
	
	public static void sendPlayerReportNotify(String targetuuid, String reporteruuid, String reason) {
		
		for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
			if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.Report.Notify"))) {
				all.sendMessage(new TextComponent(""));
				
				TextComponent n1 = new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Notify.Team.Reported")
				.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
				.replace("%player%", PlayerManager.getUsernamebyUUID(reporteruuid)));
				n1.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new Text(Main.getPrefix() + Main.getMSG("Messages.ReportSystem.Notify.Prefix.Reason") + reason + "\n"
				+ Main.getPrefix() + "§7Server §8» §c" + BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid)).getServer().getInfo().getName() + "\n\n" + "§8» " + Main.getMSG("Messages.Report-System.Notify.Team.Teleport-Button"))));
				n1.setClickEvent(new ClickEvent(Action.RUN_COMMAND,"/report claim " + targetuuid));
				
				all.sendMessage(n1);
				
				all.sendMessage(new TextComponent(""));
			}
		}
		String notify = Main.getPrefix() + Main.getMSG("Messages.Report-System.Notify.Team.Reported")
		.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
		.replace("%player%", PlayerManager.getUsernamebyUUID(reporteruuid));
		Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify));
		Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(Main.getPrefix() + reason));
		Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(Main.getPrefix() + BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid)).getServer().getInfo().getName()));
		
		if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Player-Report.Enable")) {
			
			try {
				new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Player-Report.WebHook-URL")).sendMessageToWebHook(DiscordType.PLAYER_REPORT, ChatColor.stripColor(notify.replace("»", ">") + "\nReason > " + reason + "\nServer > " + BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid)).getServer().getInfo().getName()), targetuuid);
			} catch (Exception e) {
				
			}
		}
		
	}
	
	public static void sendLoginNotify(String targetuuid, String server) {
		
		for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
			if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.WatchList.Notify"))) {
				all.sendMessage(new TextComponent(""));
				all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.WatchList.Login.Notify")
				.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
				.replace("%server%", server)));
				all.sendMessage(new TextComponent(""));
			}
		}
		String notify = Main.getPrefix() + Main.getMSG("Messages.WatchList.Login.Notify")
		.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
		.replace("%server%", server);
		Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify));
		
		if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.WatchList-Alerts.Enable")) {
			
			try {
				new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.WatchList-Alerts.WebHook-URL")).sendMessageToWebHook(DiscordType.WATCHLIST_ALERT, ChatColor.stripColor(notify.replace("»", ">")), targetuuid);
			} catch (Exception e) {
				
			}
		}
		
	}
	
	public static void sendKickNotify(String targetuuid, String teamuuid, String reason) {
		
		if(teamuuid != "Console") {
			
			for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
				if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.Kick.Notify"))) {
					all.sendMessage(new TextComponent(""));
					all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.Kick.Notify")
					.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
					.replace("%player%", PlayerManager.getUsernamebyUUID(teamuuid))));
					all.sendMessage(new TextComponent(Main.getPrefix() + "§e" + reason));
					all.sendMessage(new TextComponent(""));
				}
			}
			String notify = Main.getPrefix() + Main.getMSG("Messages.System.Kick.Notify")
			.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
			.replace("%player%", PlayerManager.getUsernamebyUUID(teamuuid));
			Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify));
			Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(Main.getPrefix() + reason));
			
			if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Network-Kick.Enable")) {
				
				try {
					new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Network-Kick.WebHook-URL")).sendMessageToWebHook(DiscordType.NETWORK_KICK, ChatColor.stripColor(notify.replace("»", ">") + "\nReason > " + reason), targetuuid);
				} catch (Exception e) {
					
				}
			}
			
		} else {
			
			for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
				if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.Kick.Notify"))) {
					all.sendMessage(new TextComponent(""));
					all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.Kick.Notify")
					.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
					.replace("%player%", "Console")));
					all.sendMessage(new TextComponent(Main.getPrefix() + "§e" + reason));
					all.sendMessage(new TextComponent(""));
				}
			}
			String notify = Main.getPrefix() + Main.getMSG("Messages.System.Kick.Notify")
			.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
			.replace("%player%", "Console");
			Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify));
			Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(Main.getPrefix() + reason));
			
			if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Network-Kick.Enable")) {
				
				try {
					new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Network-Kick.WebHook-URL")).sendMessageToWebHook(DiscordType.NETWORK_KICK, ChatColor.stripColor(notify.replace("»", ">") + "\nReason > " + reason), targetuuid);
				} catch (Exception e) {
					
				}
			}
			
		}
		
	}
	
	public static void sendIpBanNotify(String targetuuid, String teamuuid, long duration) {
		
		if(teamuuid != "Console") {
			
			for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
				if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.IpBan.Notify"))) {
					all.sendMessage(new TextComponent(""));
					all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.IP-Ban.Notify")
					.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
					.replace("%player%", PlayerManager.getUsernamebyUUID(teamuuid))
					.replace("%duration%", ReasonLengthCalculator.calculate(duration))));
					all.sendMessage(new TextComponent(""));
				}
			}
			String notify = Main.getPrefix() + Main.getMSG("Messages.Ban-System.IP-Ban.Notify")
			.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
			.replace("%player%", PlayerManager.getUsernamebyUUID(teamuuid))
			.replace("%duration%", ReasonLengthCalculator.calculate(duration));
			Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify));
			
			if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Network-IPBan.Enable")) {
				
				try {
					new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Network-IPBan.WebHook-URL")).sendMessageToWebHook(DiscordType.NETWORK_IPBAN, ChatColor.stripColor(notify.replace("»", ">")), targetuuid);
				} catch (Exception e) {
					
				}
			}
			
		} else {
			
			for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
				if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.IpBan.Notify"))) {
					all.sendMessage(new TextComponent(""));
					all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.IP-Ban.Notify")
					.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
					.replace("%player%", "Console")
					.replace("%duration%", ReasonLengthCalculator.calculate(duration))));
					all.sendMessage(new TextComponent(""));
				}
			}
			String notify = Main.getPrefix() + Main.getMSG("Messages.Ban-System.IP-Ban.Notify")
			.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
			.replace("%player%", "Console")
			.replace("%duration%", ReasonLengthCalculator.calculate(duration));
			Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify));
			
			if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Network-IPBan.Enable")) {
				
				try {
					new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Network-IPBan.WebHook-URL")).sendMessageToWebHook(DiscordType.NETWORK_IPBAN, ChatColor.stripColor(notify.replace("»", ">")), targetuuid);
				} catch (Exception e) {
					
				}
			}
			
		}
		
	}
	
	public static void sendIpUnBanNotify(String targetuuid, String teamuuid) {
		
		if(teamuuid != "Console") {
			
			for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
				if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.IpUnBan.Notify"))) {
					all.sendMessage(new TextComponent(""));
					all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.IP-UnBan.Notify")
					.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
					.replace("%player%", PlayerManager.getUsernamebyUUID(teamuuid))));
					all.sendMessage(new TextComponent(""));
				}
			}
			String notify = Main.getPrefix() + Main.getMSG("Messages.Ban-System.IP-UnBan.Notify")
			.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
			.replace("%player%", PlayerManager.getUsernamebyUUID(teamuuid));
			Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify));
			
		} else {
			
			for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
				if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.IpBan.Notify"))) {
					all.sendMessage(new TextComponent(""));
					all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.IP-UnBan.Notify")
					.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
					.replace("%player%", "Console")));
					all.sendMessage(new TextComponent(""));
				}
			}
			String notify = Main.getPrefix() + Main.getMSG("Messages.Ban-System.IP-UnBan.Notify")
			.replace("%target%", PlayerManager.getUsernamebyUUID(targetuuid))
			.replace("%player%", "Console");
			Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify));
			
		}
		
	}
	
	public static void sendNotify(NotificationSender sender, String type, String targetname, String reason) {
		if(sender.equals(NotificationSender.CONSOLE_NOTIFY)) {
			
			switch(type) {
				case "BAN":
					for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
						if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.Ban.Notify"))) {
							all.sendMessage(new TextComponent(""));
							all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Notify.Team.Banned").replace("%target%", targetname).replace("%player%", "Console")));
							all.sendMessage(new TextComponent(Main.getPrefix() + "§e" + reason));
							all.sendMessage(new TextComponent(""));
						}
					}
					String notify = Main.getPrefix() + Main.getMSG("Messages.Ban-System.Notify.Team.Banned").replace("%target%", targetname).replace("%player%", "Console");
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify));
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(Main.getPrefix() + reason));
					
					if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Network-Ban.Enable")) {
						
						try {
							new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Network-Ban.WebHook-URL")).sendMessageToWebHook(DiscordType.NETWORK_BAN, ChatColor.stripColor(notify.replace("»", ">") + "\nReason > " + reason), PlayerManager.getUUIDByName(targetname));
						} catch (Exception e) {
							
						}
					}
					break;
				case "MUTE":
					for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
						if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.Mute.Notify"))) {
							all.sendMessage(new TextComponent(""));
							all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Notify.Team.Muted").replace("%target%", targetname).replace("%player%", "Console")));
							all.sendMessage(new TextComponent(Main.getPrefix() + "§e" + reason));
							all.sendMessage(new TextComponent(""));
						}
					}
					String notify1 = Main.getPrefix() + Main.getMSG("Messages.Mute-System.Notify.Team.Muted").replace("%target%", targetname).replace("%player%", "Console");
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify1));
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(Main.getPrefix() + reason));
					
					if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Network-Mute.Enable")) {
						
						try {
							new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Network-Mute.WebHook-URL")).sendMessageToWebHook(DiscordType.NETWORK_MUTE, ChatColor.stripColor(notify1.replace("»", ">") + "\nReason > " + reason), PlayerManager.getUUIDByName(targetname));
						} catch (Exception e) {
							
						}
					}
					break;
				case "UNBAN":
					for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
						if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.UnBan.Notify"))) {
							all.sendMessage(new TextComponent(""));
							all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Notify.Team.Unban").replace("%target%", targetname).replace("%player%", "Console")));
							all.sendMessage(new TextComponent(Main.getPrefix() + "§e" + reason));
							all.sendMessage(new TextComponent(""));
						}
					}
					String notify11 = Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Notify.Team.Unban").replace("%target%", targetname).replace("%player%", "Console");
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify11));
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(Main.getPrefix() + reason));
					
					if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Network-Unban.Enable")) {
						
						try {
							new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Network-Unban.WebHook-URL")).sendMessageToWebHook(DiscordType.NETWORK_UNBAN, ChatColor.stripColor(notify11.replace("»", ">") + "\nReason > " + reason), PlayerManager.getUUIDByName(targetname));
						} catch (Exception e) {
							
						}
					}
					
					break;
				case "UNMUTE":
					for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
						if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.UnMute.Notify"))) {
							all.sendMessage(new TextComponent(""));
							all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Notify.Team.Unmute").replace("%target%", targetname).replace("%player%", "Console")));
							all.sendMessage(new TextComponent(Main.getPrefix() + "§e" + reason));
							all.sendMessage(new TextComponent(""));
						}
					}
					String notify111 = Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Notify.Team.Unmute").replace("%target%", targetname).replace("%player%", "Console");
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify111));
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(Main.getPrefix() + reason));
					
					if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Network-Unmute.Enable")) {
						
						try {
							new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Network-Unmute.WebHook-URL")).sendMessageToWebHook(DiscordType.NETWORK_UNMUTE, ChatColor.stripColor(notify111.replace("»", ">") + "\nReason > " + reason), PlayerManager.getUUIDByName(targetname));
						} catch (Exception e) {
							
						}
					}
					
					break;
				case "WARN":
					for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
						if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.Warn.Notify"))) {
							all.sendMessage(new TextComponent(""));
							all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warn.Notify").replace("%target%", targetname).replace("%player%", "Console")));
							all.sendMessage(new TextComponent(Main.getPrefix() + "§e" + reason));
							all.sendMessage(new TextComponent(""));
						}
					}
					String notify1111 = Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warn.Notify").replace("%target%", targetname).replace("%player%", "Console");
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify1111));
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(Main.getPrefix() + reason));
					
					if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Network-Warn.Enable")) {
						
						try {
							new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Network-Warn.WebHook-URL")).sendMessageToWebHook(DiscordType.NETWORK_WARN, ChatColor.stripColor(notify1111.replace("»", ">") + "\nReason > " + reason), PlayerManager.getUUIDByName(targetname));
						} catch (Exception e) {
							
						}
					}
					break;
			}
			
		}
	}
	
	public static void sendNotify(NotificationSender sender, String type, String username, String targetname, String reason) {
		if(sender.equals(NotificationSender.PLAYER_NOTIFY)) {
			
			switch(type) {
				case "BAN":
					for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
						if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.Ban.Notify"))) {
							all.sendMessage(new TextComponent(""));
							all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Notify.Team.Banned").replace("%target%", targetname).replace("%player%", username)));
							all.sendMessage(new TextComponent(Main.getPrefix() + "§e" + reason));
							all.sendMessage(new TextComponent(""));
						}
					}
					String notify = Main.getPrefix() + Main.getMSG("Messages.Ban-System.Notify.Team.Banned").replace("%target%", targetname).replace("%player%", username);
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify));
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(Main.getPrefix() + reason));
					
					if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Network-Ban.Enable")) {
						
						try {
							new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Network-Ban.WebHook-URL")).sendMessageToWebHook(DiscordType.NETWORK_BAN, ChatColor.stripColor(notify.replace("»", ">") + "\nReason > " + reason), PlayerManager.getUUIDByName(targetname));
						} catch (Exception e) {
							
						}
					}
					break;
				case "MUTE":
					for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
						if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.Mute.Notify"))) {
							all.sendMessage(new TextComponent(""));
							all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Notify.Team.Muted").replace("%target%", targetname).replace("%player%", username)));
							all.sendMessage(new TextComponent(Main.getPrefix() + "§e" + reason));
							all.sendMessage(new TextComponent(""));
						}
					}
					String notify1 = Main.getPrefix() + Main.getMSG("Messages.Mute-System.Notify.Team.Muted").replace("%target%", targetname).replace("%player%", username);
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify1));
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(Main.getPrefix() + reason));
					
					if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Network-Mute.Enable")) {
						
						try {
							new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Network-Mute.WebHook-URL")).sendMessageToWebHook(DiscordType.NETWORK_MUTE, ChatColor.stripColor(notify1.replace("»", ">") + "\nReason > " + reason), PlayerManager.getUUIDByName(targetname));
						} catch (Exception e) {
							
						}
					}
					break;
				case "UNBAN":
					for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
						if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.UnBan.Notify"))) {
							all.sendMessage(new TextComponent(""));
							all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Notify.Team.Unban").replace("%target%", targetname).replace("%player%", username)));
							all.sendMessage(new TextComponent(Main.getPrefix() + "§e" + reason));
							all.sendMessage(new TextComponent(""));
						}
					}
					String notify11 = Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Notify.Team.Unban").replace("%target%", targetname).replace("%player%", username);
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify11));
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(Main.getPrefix() + reason));
					
					if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Network-Unban.Enable")) {
						
						try {
							new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Network-Unban.WebHook-URL")).sendMessageToWebHook(DiscordType.NETWORK_UNBAN, ChatColor.stripColor(notify11.replace("»", ">") + "\nReason > " + reason), PlayerManager.getUUIDByName(targetname));
						} catch (Exception e) {
							
						}
					}
					
					break;
				case "UNMUTE":
					for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
						if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.UnMute.Notify"))) {
							all.sendMessage(new TextComponent(""));
							all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Notify.Team.Unmute").replace("%target%", targetname).replace("%player%", username)));
							all.sendMessage(new TextComponent(Main.getPrefix() + "§e" + reason));
							all.sendMessage(new TextComponent(""));
						}
					}
					String notify111 = Main.getPrefix() + Main.getMSG("Messages.Mute-System.UnMute.Notify.Team.Unmute").replace("%target%", targetname).replace("%player%", username);
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify111));
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(Main.getPrefix() + reason));
					
					if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Network-Unmute.Enable")) {
						
						try {
							new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Network-Unmute.WebHook-URL")).sendMessageToWebHook(DiscordType.NETWORK_UNMUTE, ChatColor.stripColor(notify111.replace("»", ">") + "\nReason > " + reason), PlayerManager.getUUIDByName(targetname));
						} catch (Exception e) {
							
						}
					}
					
					break;
				case "WARN":
					for(ProxiedPlayer all : Main.getInstance().getProxy().getPlayers()) {
						if(all.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || all.hasPermission(Main.getPermissionNotice("Permissions.Warn.Notify"))) {
							all.sendMessage(new TextComponent(""));
							all.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warn.Notify").replace("%target%", targetname).replace("%player%", username)));
							all.sendMessage(new TextComponent(""));
						}
					}
					String notify1111 = Main.getPrefix() + Main.getMSG("Messages.Warn-System.Warn.Notify").replace("%target%", targetname).replace("%player%", username);
					Main.getInstance().getProxy().getLogger().info(ChatColor.stripColor(notify1111));
					
					if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Network-Warn.Enable")) {
						
						try {
							new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Network-Warn.WebHook-URL")).sendMessageToWebHook(DiscordType.NETWORK_WARN, ChatColor.stripColor(notify1111.replace("»", ">") + "\nReason > " + reason), PlayerManager.getUUIDByName(targetname));
						} catch (Exception e) {
							
						}
					}
					break;
			}
			
		}
	}
	
}
