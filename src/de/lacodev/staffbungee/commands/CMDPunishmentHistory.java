package de.lacodev.staffbungee.commands;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.objects.Punishment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDPunishmentHistory extends Command {

	public CMDPunishmentHistory(String name) {
		super(name, null, "history");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer)sender;
			
			if(Main.getMySQL().isConnected()) {
				if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.PunishmentHistory.See"))) {
					
					if(args.length == 1) {
						
						String uuid = PlayerManager.getUUIDByName(args[0]);
						
						if(PlayerManager.existsPlayerData(uuid)) {
							
							Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

								@Override
								public void run() {
									
									try {
										ArrayList<Punishment> punishments = PlayerManager.getPunishments(uuid);
										
										if(!punishments.isEmpty()) {
											
											for(Punishment punish : punishments) {
												
												Date last_login = new Date(punish.getPunishment_start());
												SimpleDateFormat last_login_format = new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
												String loginLastDate = last_login_format.format(last_login);
												
												TextComponent tc = new TextComponent();
												tc.setText("§8- §c" + punish.getType().toString() + " §8| §7(#"+ punish.getId() +") §e" + punish.getReason() + " §8| §7" + loginLastDate);
												
												player.sendMessage(tc);
												
											}
											
										}
										
									} catch (SQLException e) {
										e.printStackTrace();
									}
									
								}
								
							});
							
						} else {				
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Cannot-find-player")));
						}
						
					} else {
						player.sendMessage(new TextComponent(""));
						player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
								"/punishmenthistory <Player>"));
						player.sendMessage(new TextComponent(""));
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.PunishmentHistory.See"))));
				}
			} else {				
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
			}
			
		} else {
			if(Main.getMySQL().isConnected()) {
				if(args.length == 1) {
					
					String uuid = PlayerManager.getUUIDByName(args[0]);
					
					if(PlayerManager.existsPlayerData(uuid)) {
						
						Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

							@Override
							public void run() {
								
								try {
									ArrayList<Punishment> punishments = PlayerManager.getPunishments(uuid);
									
									if(!punishments.isEmpty()) {
										
										for(Punishment punish : punishments) {
											
											Date last_login = new Date(punish.getPunishment_start());
											SimpleDateFormat last_login_format = new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
											String loginLastDate = last_login_format.format(last_login);
											
											TextComponent tc = new TextComponent();
											tc.setText("§8- §c" + punish.getType().toString() + " §8| §7(#"+ punish.getId() +") §e" + punish.getReason() + " §8| §7" + loginLastDate);
											
											sender.sendMessage(tc);
											
										}
										
									}
									
								} catch (SQLException e) {
									e.printStackTrace();
								}

							}
							
						});
						
					} else {				
						sender.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Cannot-find-player")));
					}
					
				} else {
					sender.sendMessage(new TextComponent(""));
					sender.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
							"/punishmenthistory <Player>"));
					sender.sendMessage(new TextComponent(""));
				}
			} else {				
				sender.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
			}
		}
		
	}
}
