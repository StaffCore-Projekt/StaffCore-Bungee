package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.listeners.ListenerLogin;
import de.lacodev.staffbungee.objects.LabyModInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDLabyMod extends Command {

	public CMDLabyMod(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer)sender;
			
			if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.LabyMod.Info"))) {
				
				if(args.length == 2) {
					
					if(args[0].equalsIgnoreCase("info")) {
						
						String username = args[1];
						
						if(ListenerLogin.labymod_info.containsKey(username)) {
							
							LabyModInfo info = ListenerLogin.labymod_info.get(username);
							
							player.sendMessage(new TextComponent(""));
							player.sendMessage(new TextComponent(Main.getPrefix() + "§7LabyMod Information about §c" + username));
							player.sendMessage(new TextComponent(""));
							player.sendMessage(new TextComponent(Main.getPrefix() + "§7LabyMod Version §8» §c" + info.getVersion()));
							player.sendMessage(new TextComponent(""));
							player.sendMessage(new TextComponent(Main.getPrefix() + "§cInstalled Mods:"));
							player.sendMessage(new TextComponent(""));
							for(int i = 0; i < info.getMods().size(); i++) {
								player.sendMessage(new TextComponent(Main.getPrefix() + "§7" + info.getMods().get(i)));
							}
							player.sendMessage(new TextComponent(""));
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + "§cNo information about this player"));
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + "§7/labymod info <Player>"));
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + "§7/labymod info <Player>"));
				}
				
			} else {
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.LabyMod.Info"))));
			}
		
		} else {
			CommandSender player = sender;
			
			if(args.length == 2) {
				
				if(args[0].equalsIgnoreCase("info")) {
					
					String username = args[1];
					
					if(ListenerLogin.labymod_info.containsKey(username)) {
						
						LabyModInfo info = ListenerLogin.labymod_info.get(username);
						
						player.sendMessage(new TextComponent(""));
						player.sendMessage(new TextComponent(Main.getPrefix() + "§7LabyMod Information about §c" + username));
						player.sendMessage(new TextComponent(""));
						player.sendMessage(new TextComponent(Main.getPrefix() + "§7LabyMod Version §8» §c" + info.getVersion()));
						player.sendMessage(new TextComponent(""));
						player.sendMessage(new TextComponent(Main.getPrefix() + "§cInstalled Mods:"));
						player.sendMessage(new TextComponent(""));
						for(int i = 0; i < info.getMods().size(); i++) {
							player.sendMessage(new TextComponent(Main.getPrefix() + "§7" + info.getMods().get(i)));
						}
						player.sendMessage(new TextComponent(""));
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + "§cNo information about this player"));
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + "§7/labymod info <Player>"));
				}
				
			} else {
				player.sendMessage(new TextComponent(Main.getPrefix() + "§7/labymod info <Player>"));
			}
		}
		
	}

}
