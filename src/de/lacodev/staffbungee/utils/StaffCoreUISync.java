package de.lacodev.staffbungee.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.NotificationSender;
import de.lacodev.staffbungee.managers.NotificationManager;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.managers.StaffCoreUIManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaffCoreUISync {
	
	public boolean isSyncing = false;
	
	public StaffCoreUISync() {
		startSync1();
		isSyncing = true;
	}

	public void startSync1() {
		BungeeCord.getInstance().getScheduler().schedule(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				
				BungeeCord.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {

					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						ResultSet rs = Main.getMySQL().query("SELECT id,TYPE,PROCESS FROM StaffCoreUI_Sync ORDER BY id");
						
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
											Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
										}
									} else {
										Main.getMySQL().update("INSERT INTO StaffCoreUI_Sync_OnJoin(USERNAME,TYPE,PROCESS) VALUES ('"+ username +"','VERIFY_ACCOUNT','"+ rs.getString("PROCESS") +"')");
										Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
									}
									break;
								case "WATCHLIST_ADD":
									String uuid = object.get("uuid").getAsString();
									
									NotificationManager.sendNotify(NotificationSender.CONSOLE_NOTIFY, "WATCHLIST_ADD", uuid);
									Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
									break;
								case "BAN_PLAYER":
									String type = object.get("type").getAsString();
									boolean silent = object.get("silent").getAsBoolean();
									String targetuuid = object.get("target_uuid").getAsString();
									String teamuuid = object.get("team_uuid").getAsString();
									String reason = object.get("reason").getAsString();
									
									ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
									
									if(target != null) {
										
										target.disconnect(new TextComponent(Main.getMSG("Messages.Ban-System.Player-Kick-Screen").replace("%reason%", reason)));
										
									}
									
									if(!silent) {
										NotificationManager.sendNotify(NotificationSender.PLAYER_NOTIFY, type, PlayerManager.getUsernamebyUUID(teamuuid), PlayerManager.getUsernamebyUUID(targetuuid), reason);
									}
									Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
									break;
								case "MUTE_PLAYER":
									String type1 = object.get("type").getAsString();
									boolean silent1 = object.get("silent").getAsBoolean();
									String targetuuid1 = object.get("target_uuid").getAsString();
									String teamuuid1 = object.get("team_uuid").getAsString();
									String reason1 = object.get("reason").getAsString();
									
									ProxiedPlayer target1 = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid1));
									
									if(target1 != null) {
										
										target1.sendMessage(new TextComponent(Main.getMSG("Messages.Mute-System.Message-If-Player-Muted").replace("%reason%", reason1)));
										
									}
									
									if(!silent1) {
										NotificationManager.sendNotify(NotificationSender.PLAYER_NOTIFY, type1, PlayerManager.getUsernamebyUUID(teamuuid1), PlayerManager.getUsernamebyUUID(targetuuid1), reason1);
									}
									Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
									break;
								case "UNBAN_PLAYER":
									String targetuuid11 = object.get("target_uuid").getAsString();
									String teamuuid11 = object.get("team_uuid").getAsString();
									String reason11 = object.get("reason").getAsString();
									
									NotificationManager.sendNotify(NotificationSender.PLAYER_NOTIFY, "UNBAN", PlayerManager.getUsernamebyUUID(teamuuid11), PlayerManager.getUsernamebyUUID(targetuuid11), reason11);
									
									Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
									break;
								case "UNMUTE_PLAYER":
									String targetuuid111 = object.get("target_uuid").getAsString();
									String teamuuid111 = object.get("team_uuid").getAsString();
									String reason111 = object.get("reason").getAsString();
									
									NotificationManager.sendNotify(NotificationSender.PLAYER_NOTIFY, "UNMUTE", PlayerManager.getUsernamebyUUID(teamuuid111), PlayerManager.getUsernamebyUUID(targetuuid111), reason111);
									
									Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
									break;
								case "JOIN_SERVER":
									String targetuuid1111 = object.get("target_uuid").getAsString();
									String teamuuid1111 = object.get("team_uuid").getAsString();
									
									ProxiedPlayer target11 = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid1111));
									
									if(target11 != null) {
										
										ProxiedPlayer player1 = BungeeCord.getInstance().getPlayer(UUID.fromString(teamuuid1111));
										
										player1.connect(target11.getServer().getInfo());
										
										player1.sendMessage(new TextComponent(""));
										player1.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Notify.Team.Claimed").replace("%target%", target11.getName())));
										
										TextComponent tc = new TextComponent(Main.getPrefix() + "§7Server §8» §c" + target11.getServer().getInfo().getName());
										tc.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text("§7Username §8» §c" + target11.getName() + "\n§7Ping §8» §c" + target11.getPing() + "ms "
												+ "\n\n§7Version §8» §c" + VersionDetector.getClientProtocol(target11.getPendingConnection()))));
										player1.sendMessage(tc);
										
										player1.sendMessage(new TextComponent(Main.getPrefix() + "§cFinish the report in the WebUI"));
										player1.sendMessage(new TextComponent(""));
										
									}
									
									Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
									break;
								}
							}
							
						} catch(NullPointerException e) {
							
						} catch (SQLException e) {
							
						}
					}
					
				});
				startSync2();
			}
			
		}, 10, TimeUnit.SECONDS);
	}
	
	public void startSync2() {
		BungeeCord.getInstance().getScheduler().schedule(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				
				BungeeCord.getInstance().getScheduler().runAsync(Main.getInstance(), new Runnable() {

					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						ResultSet rs = Main.getMySQL().query("SELECT id,TYPE,PROCESS FROM StaffCoreUI_Sync ORDER BY id");
						
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
											Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
										}
									} else {
										Main.getMySQL().update("INSERT INTO StaffCoreUI_Sync_OnJoin(USERNAME,TYPE,PROCESS) VALUES ('"+ username +"','VERIFY_ACCOUNT','"+ rs.getString("PROCESS") +"')");
										Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
									}
									break;
								case "WATCHLIST_ADD":
									String uuid = object.get("uuid").getAsString();
									
									NotificationManager.sendNotify(NotificationSender.CONSOLE_NOTIFY, "WATCHLIST_ADD", uuid);
									Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
									break;
								case "BAN_PLAYER":
									String type = object.get("type").getAsString();
									boolean silent = object.get("silent").getAsBoolean();
									String targetuuid = object.get("target_uuid").getAsString();
									String teamuuid = object.get("team_uuid").getAsString();
									String reason = object.get("reason").getAsString();
									
									ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
									
									if(target != null) {
										
										target.disconnect(new TextComponent(Main.getMSG("Messages.Ban-System.Player-Kick-Screen").replace("%reason%", reason)));
										
									}
									
									if(!silent) {
										NotificationManager.sendNotify(NotificationSender.PLAYER_NOTIFY, type, PlayerManager.getUsernamebyUUID(teamuuid), PlayerManager.getUsernamebyUUID(targetuuid), reason);
									}
									Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
									break;
								case "MUTE_PLAYER":
									String type1 = object.get("type").getAsString();
									boolean silent1 = object.get("silent").getAsBoolean();
									String targetuuid1 = object.get("target_uuid").getAsString();
									String teamuuid1 = object.get("team_uuid").getAsString();
									String reason1 = object.get("reason").getAsString();
									
									ProxiedPlayer target1 = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid1));
									
									if(target1 != null) {
										
										target1.sendMessage(new TextComponent(Main.getMSG("Messages.Mute-System.Message-If-Player-Muted").replace("%reason%", reason1)));
										
									}
									
									if(!silent1) {
										NotificationManager.sendNotify(NotificationSender.PLAYER_NOTIFY, type1, PlayerManager.getUsernamebyUUID(teamuuid1), PlayerManager.getUsernamebyUUID(targetuuid1), reason1);
									}
									Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
									break;
								case "UNBAN_PLAYER":
									String targetuuid11 = object.get("target_uuid").getAsString();
									String teamuuid11 = object.get("team_uuid").getAsString();
									String reason11 = object.get("reason").getAsString();
									
									NotificationManager.sendNotify(NotificationSender.PLAYER_NOTIFY, "UNBAN", PlayerManager.getUsernamebyUUID(teamuuid11), PlayerManager.getUsernamebyUUID(targetuuid11), reason11);
									
									Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
									break;
								case "UNMUTE_PLAYER":
									String targetuuid111 = object.get("target_uuid").getAsString();
									String teamuuid111 = object.get("team_uuid").getAsString();
									String reason111 = object.get("reason").getAsString();
									
									NotificationManager.sendNotify(NotificationSender.PLAYER_NOTIFY, "UNMUTE", PlayerManager.getUsernamebyUUID(teamuuid111), PlayerManager.getUsernamebyUUID(targetuuid111), reason111);
									
									Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
									break;
								case "JOIN_SERVER":
									String targetuuid1111 = object.get("target_uuid").getAsString();
									String teamuuid1111 = object.get("team_uuid").getAsString();
									
									ProxiedPlayer target11 = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid1111));
									
									if(target11 != null) {
										
										ProxiedPlayer player1 = BungeeCord.getInstance().getPlayer(UUID.fromString(teamuuid1111));
										
										player1.connect(target11.getServer().getInfo());
										
										player1.sendMessage(new TextComponent(""));
										player1.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Notify.Team.Claimed").replace("%target%", target11.getName())));
										
										TextComponent tc = new TextComponent(Main.getPrefix() + "§7Server §8» §c" + target11.getServer().getInfo().getName());
										tc.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text("§7Username §8» §c" + target11.getName() + "\n§7Ping §8» §c" + target11.getPing() + "ms "
												+ "\n\n§7Version §8» §c" + VersionDetector.getClientProtocol(target11.getPendingConnection()))));
										player1.sendMessage(tc);
										
										player1.sendMessage(new TextComponent(Main.getPrefix() + "§cFinish the report in the WebUI"));
										player1.sendMessage(new TextComponent(""));
										
									}
									
									Main.getMySQL().update("DELETE FROM StaffCoreUI_Sync WHERE id = '"+ rs.getInt("id") +"'");
									break;
								}
							}
							
						} catch(NullPointerException e) {
							
						} catch (SQLException e) {
							
						}
					}
					
				});
				startSync1();
			}
			
		}, 10, TimeUnit.SECONDS);
	}

}
