package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.BanManager;
import de.lacodev.staffbungee.managers.PlayerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDUnBan extends Command {

	public CMDUnBan(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer)sender;
			
			if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.UnBan.Use"))) {
				
				if(Main.getInstance().getConfig().getBoolean("Unban.Force-Reason")) {
					
					if(args.length == 2) {
						
						if(!args[1].equalsIgnoreCase("-s")) {
							
							String uuid = PlayerManager.getUUIDByName(args[0]); 
							
							if(uuid != null) {
								
								if(BanManager.isBanned(uuid)) {
									
									BanManager.unban(uuid, player.getUniqueId().toString(), args[1]);
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Success")));
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Not-Banned")));
								}
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Cannot-find-player")));
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Unban.Force-Reason")));
						}
						
					} else if(args.length == 3) {
						
						if(args[1].equalsIgnoreCase("-s")) {
							
							if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.UnBan.Silent"))) {
								
								String uuid = PlayerManager.getUUIDByName(args[0]); 
								
								if(uuid != null) {
									
									if(BanManager.isBanned(uuid)) {
										
										BanManager.silentunban(uuid, player.getUniqueId().toString(), args[2]);
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Success")));
										
									} else {
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Not-Banned")));
									}
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Cannot-find-player")));
								}
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.UnBan.Silent"))));
							}
							
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Usage") + " (-s) <Reason>"));
					}
					
				} else {
					if(args.length == 1) {
						
						String uuid = PlayerManager.getUUIDByName(args[0]); 
						
						if(uuid != null) {
							
							if(BanManager.isBanned(uuid)) {
								
								BanManager.unban(uuid, player.getUniqueId().toString(), "Unban");
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Success")));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Not-Banned")));
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Cannot-find-player")));
						}
						
					} else if(args.length == 2) {
						
						if(args[1].equalsIgnoreCase("-s")) {
							
							if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.UnBan.Silent"))) {
								
								String uuid = PlayerManager.getUUIDByName(args[0]); 
								
								if(uuid != null) {
									
									if(BanManager.isBanned(uuid)) {
										
										BanManager.silentunban(uuid, player.getUniqueId().toString(), "Unban");
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Success")));
										
									} else {
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Not-Banned")));
									}
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Cannot-find-player")));
								}
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.UnBan.Silent"))));
							}
							
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Usage") + " -s"));
					}
				}
				
			} else {
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.UnBan.Use"))));
			}
			
		} else {
			
			CommandSender player = sender;
			
			if(Main.getInstance().getConfig().getBoolean("Unban.Force-Reason")) {
				
				if(args.length == 2) {
					
					if(!args[1].equalsIgnoreCase("-s")) {
						
						String uuid = PlayerManager.getUUIDByName(args[0]); 
						
						if(uuid != null) {
							
							if(BanManager.isBanned(uuid)) {
								
								BanManager.unban(uuid, "Console", args[1]);
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Success")));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Not-Banned")));
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Cannot-find-player")));
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Unban.Force-Reason")));
					}
					
				} else if(args.length == 3) {
					
					if(args[1].equalsIgnoreCase("-s")) {
						
						String uuid = PlayerManager.getUUIDByName(args[0]); 
						
						if(uuid != null) {
							
							if(BanManager.isBanned(uuid)) {
								
								BanManager.silentunban(uuid, "Console", args[2]);
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Success")));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Not-Banned")));
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Cannot-find-player")));
						}
						
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Usage") + " (-s) <Reason>"));
				}
				
			} else {
				if(args.length == 1) {
					
					String uuid = PlayerManager.getUUIDByName(args[0]); 
					
					if(uuid != null) {
						
						if(BanManager.isBanned(uuid)) {
							
							BanManager.unban(uuid, "Console", "Unban");
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Success")));
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Not-Banned")));
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Cannot-find-player")));
					}
					
				} else if(args.length == 2) {
					
					if(args[1].equalsIgnoreCase("-s")) {
						
						String uuid = PlayerManager.getUUIDByName(args[0]); 
						
						if(uuid != null) {
							
							if(BanManager.isBanned(uuid)) {
								
								BanManager.silentunban(uuid, "Console", "Unban");
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Success")));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Not-Banned")));
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Cannot-find-player")));
						}
						
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.UnBan.Usage") + " -s"));
				}
			}
			
		}
		
	}

}
