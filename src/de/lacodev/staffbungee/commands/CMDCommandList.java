package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.CommandListManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDCommandList extends Command {

	public CMDCommandList(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer) sender;
			
			if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.CommandList.Use"))) {
				
				if(args.length == 2) {
					
					if(args[0].equalsIgnoreCase("add")) {
						
						String command = args[1];
						
						if(!CommandListManager.isListed(command)) {
							
							CommandListManager.add(command);
														
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute.CommandList.Add-Success").replace("%command%", "/" + command)));
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute.CommandList.Already-listed")));
						}
						
					} else if(args[0].equalsIgnoreCase("remove")) {
						
						String command = args[1];
						
						if(CommandListManager.isListed(command)) {
							
							CommandListManager.remove(command);
														
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute.CommandList.Remove-Success").replace("%command%", "/" + command)));
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute.CommandList.Not-listed")));
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + "§7/commandlist <add/remove> <Command without />"));
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + "§7/commandlist <add/remove> <Command without />"));
				}
				
			} else {
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.CommandList.Use"))));
			}
			
		} else {
			CommandSender player = sender;
			
			if(args.length == 2) {
				
				
				
			} else {
				player.sendMessage(new TextComponent(Main.getPrefix() + "§7/commandlist <add/remove> <Command without />"));
			}
		}
	}

}
