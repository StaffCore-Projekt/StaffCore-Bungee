package de.lacodev.staffbungee.commands;

import java.util.HashMap;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.MigrationConfig;
import de.lacodev.staffbungee.enums.Settings;
import de.lacodev.staffbungee.handlers.MigrationHandler;
import de.lacodev.staffbungee.managers.SettingsManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDStaffCore extends Command {

	public CMDStaffCore(String name) {
		super(name);
	}
	
	public static HashMap<ProxiedPlayer, Settings> settings = new HashMap<>();

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer)sender;
			
			// /staffcore alerts
			
			if(args.length == 0) {
				
				player.sendMessage(new TextComponent(""));
				player.sendMessage(new TextComponent("§cSystem §8» StaffCore-Bungee: §7v" + Main.getInstance().getDescription().getVersion()));
				player.sendMessage(new TextComponent("§cSystem §8» Plugin by: §dLacoDev"));
				player.sendMessage(new TextComponent(""));
				player.sendMessage(new TextComponent("§cSystem §8» §7All Chat displays are limited to 10 entries!"));
				
				TextComponent webui = new TextComponent("§8[§e§lWEB-UI§8]");
				webui.setClickEvent(new ClickEvent(Action.OPEN_URL,Main.getInstance().getConfig().getString("WebUI.URL")));
				webui.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click here to get to our WebUI")));
				
				TextComponent nor = new TextComponent("§cSystem §8» §7For more analytics use our: ");
				nor.addExtra(webui);
				
				player.sendMessage(nor);
				
				player.sendMessage(new TextComponent(""));
				
				TextComponent discord = new TextComponent("§8[§5§lDISCORD§8]");
				discord.setClickEvent(new ClickEvent(Action.OPEN_URL,"https://discord.gg/QwnU68V"));
				discord.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click here to join our discord :)")));
				
				if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything"))) {
					TextComponent bugreport = new TextComponent(" §8[§c§lHELP§8]");
					bugreport.setClickEvent(new ClickEvent(Action.OPEN_URL,"https://docs.staffcore-bungee.net"));
					bugreport.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click here to get help for StaffCore-Bungee")));
					
					if(SettingsManager.getValue(Settings.ADVERTISMENT_ENABLE).matches("FALSE")) {
						
						TextComponent removeads = new TextComponent(" §8[§a§lSHOW ADS§8]");
						removeads.setClickEvent(new ClickEvent(Action.RUN_COMMAND,"/staffcore ads"));
						removeads.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click here to toggle Advertisments for StaffCore")));
						bugreport.addExtra(removeads);
						
					} else {
						
						TextComponent removeads = new TextComponent(" §8[§a§lREMOVE ADS§8]");
						removeads.setClickEvent(new ClickEvent(Action.RUN_COMMAND,"/staffcore ads"));
						removeads.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click here to toggle Advertisments for StaffCore")));
						bugreport.addExtra(removeads);
						
					}
					
					discord.addExtra(bugreport);
				}
				
				player.sendMessage(discord);
				
				player.sendMessage(new TextComponent(""));
				
			} else if(args.length == 1) {
				if(args[0].equalsIgnoreCase("ads")) {
					
					if(SettingsManager.getValue(Settings.ADVERTISMENT_ENABLE).matches("FALSE")) {
						
						SettingsManager.updateValue(Settings.ADVERTISMENT_ENABLE, "TRUE");
						player.sendMessage(new TextComponent(Main.getPrefix() + "§7Advertisments got §aactivated§7! Thank you!"));
						
					} else {
						
						SettingsManager.updateValue(Settings.ADVERTISMENT_ENABLE, "FALSE");
						player.sendMessage(new TextComponent(Main.getPrefix() + "§7Advertisments got §cdeactivated§7! Enjoy!"));
						
					}
					
				}
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("settings")) {
					if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything"))) {
						
						try {
							
							Settings setting = Settings.valueOf(args[1]);
							
							if(setting != null) {
								
								settings.put(player, setting);
								player.sendMessage(new TextComponent(""));
								player.sendMessage(new TextComponent(Main.getPrefix() + "§7You are now editing the Setting: §c" + setting.toString()));
								player.sendMessage(new TextComponent(Main.getPrefix() + "§7Your Chat is disabled! Until you type in §ccancel"));
								player.sendMessage(new TextComponent(""));
								
							}
							
						} catch (IllegalArgumentException e) {
							player.sendMessage(new TextComponent(""));
							player.sendMessage(new TextComponent(Main.getPrefix() + "§cInvalid Setting!"));
							player.sendMessage(new TextComponent(""));
							for(Settings setting : Settings.values()) {
								player.sendMessage(new TextComponent(Main.getPrefix() + "§7" + setting.toString()));
							}
							player.sendMessage(new TextComponent(""));
						}
						
					}
				}
			}
		} else {
			CommandSender player = sender;
			
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase("import")) {
					
					try {
						if(MigrationConfig.valueOf(args[1]) != null) {
							
							new MigrationHandler(MigrationConfig.valueOf(args[1])).migrate();
							
						}
					} catch(IllegalArgumentException e) {
						player.sendMessage(new TextComponent(Main.getPrefix() + "§cInvalid MigrationConfig! Available Configs:"));
						for(MigrationConfig config : MigrationConfig.values()) {
							player.sendMessage(new TextComponent(Main.getPrefix() + config.toString()));
						}
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + "§7/staffcore import <MigrationConfig>"));
					player.sendMessage(new TextComponent(Main.getPrefix() + "Invalid MigrationConfig! Available Configs:"));
					for(MigrationConfig config : MigrationConfig.values()) {
						player.sendMessage(new TextComponent(Main.getPrefix() + config.toString()));
					}
				}
			} else {
				player.sendMessage(new TextComponent(Main.getPrefix() + "§7/staffcore import <MigrationConfig>"));
			}
		}
		
	}

}
