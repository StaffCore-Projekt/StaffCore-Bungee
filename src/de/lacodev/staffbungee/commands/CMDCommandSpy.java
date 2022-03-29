package de.lacodev.staffbungee.commands;

import de.lacodev.staffbungee.Main;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDCommandSpy extends Command {

	public CMDCommandSpy(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer) sender;
			
			if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.CommandSpy.Use"))) {
				
				if(args.length == 0) {
					
					if(!Main.getCommandSpy().isSpying(player)) {
						
						Main.getCommandSpy().startSpying(player);
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.CommandSpy.Spying-On-Global")));
						
					} else {
						
						Main.getCommandSpy().stopSpying(player);
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.CommandSpy.No-longer-spying")));
						
					}
					
				} else if(args.length == 1) {
					
					if(BungeeCord.getInstance().getServerInfo(args[0]) != null) {
						
						Main.getCommandSpy().startSpying(player, args[0]);
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.CommandSpy.Spying-Specific-Server").replace("%server%", args[0])));
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.CommandSpy.Server-not-found")));
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + "§7/commandspy (Optional: subserver)"));
				}
				
			} else {
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.CommandSpy.Use"))));
			}
			
		}
		
	}

}
