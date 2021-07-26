package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.managers.WatchListManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDWatchList extends Command {

	public CMDWatchList(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer)sender;
			
			if(Main.getMySQL().isConnected()) {
				
				if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.WatchList.Change"))) {
					
					if(args.length == 2) {
						
						String uuid = PlayerManager.getUUIDByName(args[1]);
						
						if(uuid != null) {
							
							if(args[0].equalsIgnoreCase("add")) {
								
								if(!WatchListManager.isWatching(uuid)) {
									
									WatchListManager.addPlayer(uuid, player.getUniqueId().toString());
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.WatchList.Add-Player.Success")));
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Watchlist.Already-Watching")));
								}
								
							} else if(args[0].equalsIgnoreCase("remove")) {
								
								if(WatchListManager.isWatching(uuid)) {
									
									WatchListManager.removePlayer(uuid);
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.WatchList.Remove-Player.Success")));
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Watchlist.Not-Watching")));
								}
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.WatchList.Usage")));
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.WatchList.Never-Joined")));
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.WatchList.Usage")));
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.WatchList.Change"))));
				}
				
			} else {				
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
			}
			
		} else {
			
			CommandSender player = sender;
			
			if(args.length == 2) {
				
				String uuid = PlayerManager.getUUIDByName(args[1]);
				
				if(uuid != null) {
					
					if(args[0].equalsIgnoreCase("add")) {
						
						if(!WatchListManager.isWatching(uuid)) {
							
							WatchListManager.addPlayer(uuid, "Console");
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.WatchList.Add-Player.Success")));
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Watchlist.Already-Watching")));
						}
						
					} else if(args[0].equalsIgnoreCase("remove")) {
						
						if(WatchListManager.isWatching(uuid)) {
							
							WatchListManager.removePlayer(uuid);
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.WatchList.Remove-Player.Success")));
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Watchlist.Not-Watching")));
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.WatchList.Usage")));
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.WatchList.Never-Joined")));
				}
				
			} else {
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.WatchList.Usage")));
			}
			
		}
		
	}

}
