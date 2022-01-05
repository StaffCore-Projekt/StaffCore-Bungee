package de.lacodev.staffbungee.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.DiscordType;
import de.lacodev.staffbungee.handlers.DiscordIntegrationHandler;
import de.lacodev.staffbungee.managers.BanManager;
import de.lacodev.staffbungee.managers.MuteManager;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.utils.IPLookup;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDCheckPlayer extends Command {

	public CMDCheckPlayer(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer)sender;
			
			if(Main.getMySQL().isConnected()) {
				if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.Check.Use"))) {
					
					if(args.length == 1) {
						
						String uuid = PlayerManager.getUUIDByName(args[0]);
						
						if(PlayerManager.existsPlayerData(uuid)) {
							
							Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

								@Override
								public void run() {
									
									Date last_login = new Date(PlayerManager.getLastOnline(uuid));
									SimpleDateFormat last_login_format = new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
									String loginLastDate = last_login_format.format(last_login);
									
									Date first_login = new Date(PlayerManager.getFirstOnline(uuid));
									SimpleDateFormat first_login_format = new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
									String loginFirstDate = first_login_format.format(first_login);
									
									player.sendMessage(new TextComponent(""));
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Title")));
									player.sendMessage(new TextComponent(""));
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Name") + args[0] + " §8( " + PlayerManager.getStatus(uuid) + " §8)"));
									player.sendMessage(new TextComponent(Main.getPrefix() + "§7UniqueId §8» §e" + uuid));
									player.sendMessage(new TextComponent(""));
									if(IPLookup.isUsingProxy(PlayerManager.getLastKnownIp(uuid))) {
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Last-IP") + PlayerManager.getLastKnownIp(player, uuid) + " §8(§cVPN§8)"));
									} else {
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Last-IP") + PlayerManager.getLastKnownIp(player, uuid)));
									}
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Country") + PlayerManager.getCountry(uuid)));
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Region") + PlayerManager.getRegion(uuid)));
									player.sendMessage(new TextComponent(""));
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.First-Online") + loginFirstDate));
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Last-Online") + loginLastDate));
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.OnlineTime") + PlayerManager.getOnlineTime(uuid)));
									player.sendMessage(new TextComponent(""));
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Reports") + PlayerManager.getReports(uuid)));
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Bans") + PlayerManager.getBans(uuid)));
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Mutes") + PlayerManager.getMutes(uuid)));
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Warns") + PlayerManager.getWarns(uuid)));
									player.sendMessage(new TextComponent(""));
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Banned") + getBanningState(uuid)));
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Muted") + getMutingState(uuid)));
									player.sendMessage(new TextComponent(""));
									
									TextComponent clickaction1 = new TextComponent();
									clickaction1.setText(" " + ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + "Sessions" + ChatColor.DARK_GRAY + "]");
									clickaction1.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/sessions " + args[0]));
									
									TextComponent clickaction0 = new TextComponent();
									clickaction0.setText(ChatColor.DARK_GRAY + "» [" + ChatColor.YELLOW + "Punishments" + ChatColor.DARK_GRAY + "]");
									clickaction0.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/punishmenthistory " + args[0]));
									clickaction0.addExtra(clickaction1);
									
									if(PlayerManager.getBrand(args[0]) != null) {
										if(PlayerManager.getBrand(args[0]).matches("LabyMod v3")) {
											TextComponent clickaction2 = new TextComponent();
											clickaction2.setText(" " + ChatColor.DARK_GRAY + "[" + ChatColor.RED + "LabyMod" + ChatColor.DARK_GRAY + "]");
											clickaction2.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/labymod info " + args[0]));
											clickaction0.addExtra(clickaction2);
										}
									}
									
									player.sendMessage(clickaction0);
									player.sendMessage(new TextComponent(""));
									
									if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Playerdata-Request.Enable")) {
										
										String listString = Main.getMSG("Messages.Player-Check.Prefix.Name").replace("»", ">") + args[0] + " §8( " + PlayerManager.getStatus(uuid) + " §8) \n"
												+ Main.getMSG("Messages.Player-Check.Prefix.Last-IP").replace("»", ">") + PlayerManager.getLastKnownIp(player, uuid) + "\n"
												+ Main.getMSG("Messages.Player-Check.Prefix.Country").replace("»", ">") + PlayerManager.getCountry(uuid) + "\n"
												+ Main.getMSG("Messages.Player-Check.Prefix.Region").replace("»", ">") + PlayerManager.getRegion(uuid) + "\n"
												+ Main.getMSG("Messages.Player-Check.Prefix.First-Online").replace("»", ">") + loginFirstDate + "\n"
												+ Main.getMSG("Messages.Player-Check.Prefix.Last-Online").replace("»", ">") + loginLastDate + "\n"
												+ Main.getMSG("Messages.Player-Check.Prefix.OnlineTime").replace("»", ">") + PlayerManager.getOnlineTime(uuid) + "\n"
												+ Main.getMSG("Messages.Player-Check.Prefix.Reports").replace("»", ">") + PlayerManager.getReports(uuid) + "\n"
												+ Main.getMSG("Messages.Player-Check.Prefix.Bans").replace("»", ">") + PlayerManager.getBans(uuid) + "\n"
												+ Main.getMSG("Messages.Player-Check.Prefix.Mutes").replace("»", ">") + PlayerManager.getMutes(uuid) + "\n"
												+ Main.getMSG("Messages.Player-Check.Prefix.Warns").replace("»", ">") + PlayerManager.getWarns(uuid) + "\n"
												+ Main.getMSG("Messages.Player-Check.Prefix.Banned").replace("»", ">") + getBanningState(uuid) + "\n"
												+ Main.getMSG("Messages.Player-Check.Prefix.Muted").replace("»", ">") + getMutingState(uuid) + "\n";
										
										try {
											new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Playerdata-Request.WebHook-URL")).sendMessageToWebHook(DiscordType.REQUEST_PLAYERDATA, ChatColor.stripColor(listString), uuid);
										} catch (Exception e) {
											
										}
									}
									
									Main.getMySQL().update("INSERT INTO StaffCore_activitydb(type,uuid,target,message,reg_date,priority) VALUES ('PLAYER_CHECKED','"+ player.getUniqueId().toString() +"',"
											+ "'"+ PlayerManager.getUsernamebyUUID(uuid) +"','%player% checked the information of %target%','"+ System.currentTimeMillis() +"','2')");
									
								}
								
							});
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.No-Player-Found")));
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Usage")));
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.Check.Use"))));
				}
			} else {				
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
			}
			
		} else {
			
			CommandSender player = sender;
			
			if(Main.getMySQL().isConnected()) {
				if(args.length == 1) {
					
					String uuid = PlayerManager.getUUIDByName(args[0]);
					
					if(PlayerManager.existsPlayerData(uuid)) {
						
						Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

							@Override
							public void run() {
								
								Date last_login = new Date(PlayerManager.getLastOnline(uuid));
								SimpleDateFormat last_login_format = new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
								String loginLastDate = last_login_format.format(last_login);
								
								Date first_login = new Date(PlayerManager.getFirstOnline(uuid));
								SimpleDateFormat first_login_format = new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
								String loginFirstDate = first_login_format.format(first_login);
								
								player.sendMessage(new TextComponent(""));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Title")));
								player.sendMessage(new TextComponent(""));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Name") + args[0] + " §8( " + PlayerManager.getStatus(uuid) + " §8)"));
								player.sendMessage(new TextComponent(Main.getPrefix() + "§7UniqueId §8» §e" + uuid));
								player.sendMessage(new TextComponent(""));
								if(IPLookup.isUsingProxy(PlayerManager.getLastKnownIp(uuid))) {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Last-IP") + PlayerManager.getLastKnownIp(uuid) + " §8(§cVPN§8)"));
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Last-IP") + PlayerManager.getLastKnownIp(uuid)));
								}
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Country") + PlayerManager.getCountry(uuid)));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Region") + PlayerManager.getRegion(uuid)));
								player.sendMessage(new TextComponent(""));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.First-Online") + loginFirstDate));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Last-Online") + loginLastDate));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.OnlineTime") + PlayerManager.getOnlineTime(uuid)));
								player.sendMessage(new TextComponent(""));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Reports") + PlayerManager.getReports(uuid)));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Bans") + PlayerManager.getBans(uuid)));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Mutes") + PlayerManager.getMutes(uuid)));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Warns") + PlayerManager.getWarns(uuid)));
								player.sendMessage(new TextComponent(""));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Banned") + getBanningState(uuid)));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Prefix.Muted") + getMutingState(uuid)));
								player.sendMessage(new TextComponent(""));
								
								if(Main.getInstance().getDiscord().getBoolean("Discord-Integration.Events.Playerdata-Request.Enable")) {
									
									String listString = Main.getMSG("Messages.Player-Check.Prefix.Name").replace("»", ">") + args[0] + " §8( " + PlayerManager.getStatus(uuid) + " §8) \n"
											+ Main.getMSG("Messages.Player-Check.Prefix.Last-IP").replace("»", ">") + PlayerManager.getLastKnownIp(uuid) + "\n"
											+ Main.getMSG("Messages.Player-Check.Prefix.Country").replace("»", ">") + PlayerManager.getCountry(uuid) + "\n"
											+ Main.getMSG("Messages.Player-Check.Prefix.Region").replace("»", ">") + PlayerManager.getRegion(uuid) + "\n"
											+ Main.getMSG("Messages.Player-Check.Prefix.First-Online").replace("»", ">") + loginFirstDate + "\n"
											+ Main.getMSG("Messages.Player-Check.Prefix.Last-Online").replace("»", ">") + loginLastDate + "\n"
											+ Main.getMSG("Messages.Player-Check.Prefix.OnlineTime").replace("»", ">") + PlayerManager.getOnlineTime(uuid) + "\n"
											+ Main.getMSG("Messages.Player-Check.Prefix.Reports").replace("»", ">") + PlayerManager.getReports(uuid) + "\n"
											+ Main.getMSG("Messages.Player-Check.Prefix.Bans").replace("»", ">") + PlayerManager.getBans(uuid) + "\n"
											+ Main.getMSG("Messages.Player-Check.Prefix.Mutes").replace("»", ">") + PlayerManager.getMutes(uuid) + "\n"
											+ Main.getMSG("Messages.Player-Check.Prefix.Warns").replace("»", ">") + PlayerManager.getWarns(uuid) + "\n"
											+ Main.getMSG("Messages.Player-Check.Prefix.Banned").replace("»", ">") + getBanningState(uuid) + "\n"
											+ Main.getMSG("Messages.Player-Check.Prefix.Muted").replace("»", ">") + getMutingState(uuid) + "\n";
									
									try {
										new DiscordIntegrationHandler(Main.getInstance().getDiscord().getString("Discord-Integration.Events.Playerdata-Request.WebHook-URL")).sendMessageToWebHook(DiscordType.REQUEST_PLAYERDATA, ChatColor.stripColor(listString), uuid);
									} catch (Exception e) {
										
									}
								}
							}
							
						});
						
					} else {
						sender.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.No-Player-Found")));
					}
					
				} else {
					sender.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Player-Check.Usage")));
				}
			} else {				
				sender.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
			}
		}
		
	}

	private String getMutingState(String uuid) {
		if(MuteManager.isMuted(uuid)) {
			return Main.getMSG("Messages.Player-Check.State.Muted") + MuteManager.getReason(uuid);
		} else {
			return Main.getMSG("Messages.Player-Check.State.No-Entry");
		}
	}

	private String getBanningState(String uuid) {
		if(BanManager.isBanned(uuid)) {
			return Main.getMSG("Messages.Player-Check.State.Banned") + BanManager.getReason(uuid);
		} else {
			return Main.getMSG("Messages.Player-Check.State.No-Entry");
		}
	}

}
