package de.lacodev.staffbungee.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.AltaccountManager;
import de.lacodev.staffbungee.managers.BanManager;
import de.lacodev.staffbungee.managers.MuteManager;
import de.lacodev.staffbungee.managers.PlayerManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDCheckAlts extends Command {

	public CMDCheckAlts(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer)sender;
			
			if(Main.getMySQL().isConnected()) {
				if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.CheckAlts.Use"))) {
					
					if(args.length == 1) {
						
						String uuid = PlayerManager.getUUIDByName(args[0]);
						
						if(PlayerManager.existsPlayerData(uuid)) {
							
							Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

								@Override
								public void run() {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Collecting-Data").replace("%ip%", PlayerManager.getLastKnownIp(player, uuid))));
									
									ArrayList<String> alts = AltaccountManager.collectForPlayer(args[0]);
									
									if(!alts.isEmpty()) {
										
										player.sendMessage(new TextComponent(""));
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Possible-Alts-Title").replace("%target%", args[0])));
										
										TextComponent msg = new TextComponent();
										
										for(String name : alts) {
											
											String targetuuid = PlayerManager.getUUIDByName(name);
											
											Date last_login = new Date(PlayerManager.getLastOnline(targetuuid));
											SimpleDateFormat last_login_format = new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
											String loginLastDate = last_login_format.format(last_login);
											
											Date first_login = new Date(PlayerManager.getFirstOnline(targetuuid));
											SimpleDateFormat first_login_format = new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
											String loginFirstDate = first_login_format.format(first_login);
											
											TextComponent tc = new TextComponent(getColor(targetuuid) + name + "§7, ");
														
											String listString = Main.getMSG("Messages.Player-Check.Prefix.Last-IP") + PlayerManager.getLastKnownIp(player, targetuuid) + "\n"
													+ Main.getMSG("Messages.Player-Check.Prefix.Country") + PlayerManager.getCountry(targetuuid) + "\n"
													+ Main.getMSG("Messages.Player-Check.Prefix.Region") + PlayerManager.getRegion(targetuuid) + "\n \n"
													+ Main.getMSG("Messages.Player-Check.Prefix.First-Online") + loginFirstDate + "\n"
													+ Main.getMSG("Messages.Player-Check.Prefix.Last-Online") + loginLastDate + "\n"
													+ Main.getMSG("Messages.Player-Check.Prefix.OnlineTime") + PlayerManager.getOnlineTime(targetuuid) + "\n \n"
													+ Main.getMSG("Messages.Player-Check.Prefix.Reports") + PlayerManager.getReports(targetuuid) + "\n"
													+ Main.getMSG("Messages.Player-Check.Prefix.Bans") + PlayerManager.getBans(targetuuid) + "\n"
													+ Main.getMSG("Messages.Player-Check.Prefix.Mutes") + PlayerManager.getMutes(targetuuid) + "\n"
													+ Main.getMSG("Messages.Player-Check.Prefix.Warns") + PlayerManager.getWarns(targetuuid) + "\n \n"
													+ Main.getMSG("Messages.Player-Check.Prefix.Banned") + getBanningState(targetuuid) + "\n"
													+ Main.getMSG("Messages.Player-Check.Prefix.Muted") + getMutingState(targetuuid) + "\n";
										
											tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(listString)));
											tc.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/check " + name));
											
											msg.addExtra(tc);
										}
										player.sendMessage(msg);
										
									} else {
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Nothing-found")));
									}
								}
								
							});
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Player-Not-Found")));
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Usage")));
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.CheckAlts.Use"))));
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
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Collecting-Data").replace("%ip%", PlayerManager.getLastKnownIp(uuid))));
								
								ArrayList<String> alts = AltaccountManager.collectForPlayer(args[0]);
								
								if(!alts.isEmpty()) {
									
									player.sendMessage(new TextComponent(""));
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Possible-Alts-Title").replace("%target%", args[0])));
									
									for(String name : alts) {
										
										player.sendMessage(new TextComponent(""));
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Prefix.Username") + name));
									}
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Nothing-found")));
								}
							}
							
						});
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Player-Not-Found")));
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Altaccount-Check.Usage")));
				}
			} else {				
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
			}
			
		}
		
	}
	
	protected String getColor(String targetuuid) {
		
		ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(targetuuid));
		
		if(BanManager.isBanned(targetuuid)) {
			return "§c";
		} else if(MuteManager.isMuted(targetuuid)) {
			return "§6";
		} else if(target != null) {
			return "§a";
		} else {
			return "§7";
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
