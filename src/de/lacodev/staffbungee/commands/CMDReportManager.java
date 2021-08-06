package de.lacodev.staffbungee.commands;

import java.util.ArrayList;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.ReasonType;
import de.lacodev.staffbungee.managers.ReasonManager;
import de.lacodev.staffbungee.objects.Reason;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDReportManager extends Command {

	public CMDReportManager(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer)sender;
			
			if(Main.getMySQL().isConnected()) {
				
				if(args.length == 0) {
					
					player.sendMessage(new TextComponent(""));
					player.sendMessage(new TextComponent(Main.getPrefix() + "§8[§cReportManager§8]"));
					if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.ReportManager.addreason"))) {
						player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
								"/reportmanager addreason <Reason>"));
					}
					if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.ReportManager.removereason"))) {
						player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
								"/reportmanager removereason <ReasonID>"));	
					}
					if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.ReportManager.listreasons"))) {
						player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
								"/reportmanager listreasons"));	
					}
					player.sendMessage(new TextComponent(""));
					
				} else if(args.length == 1) {
					
					if(args[0].equalsIgnoreCase("listreasons")) {
						
						if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.ReportManager.listreasons"))) {
							
							ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.REPORT);
							
							if(!reasons.isEmpty()) {
								
								player.sendMessage(new TextComponent(""));
								for(Reason reason : reasons) {
									player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName()));
								}
								player.sendMessage(new TextComponent(""));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.List-Reasons.No-Reasons-Found")));
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.ReportManager.listreasons"))));
						}
						
					}
					
				} else if(args.length == 2) {
					
					if(args[0].equalsIgnoreCase("addreason")) {
						
						if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.ReportManager.addreason"))) {
							
							if(!ReasonManager.existsReason(ReasonType.REPORT, args[1])) {
								
								ReasonManager.addReason(ReasonType.REPORT, args[1]);
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.ReportManager.Add-Reason.Success")));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.ReportManager.Add-Reason.Already-Exists")));
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.ReportManager.addreason"))));
						}
						
					} else if(args[0].equalsIgnoreCase("removereason")) {
						
						if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.ReportManager.removereason"))) {
							
							try {
								
								Integer id = Integer.parseInt(args[1]);
								Reason reason = ReasonManager.getReasonById(ReasonType.REPORT, id);
								
								if(reason != null) {
									
									ReasonManager.removeReason(ReasonType.REPORT, id);
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.ReportManager.Remove-Reason.Success")));
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.ReportManager.Remove-Reason.Not-Exists")));
								}
								
							} catch (NumberFormatException e) {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.NotValidID")));
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.ReportManager.removereason"))));
						}
						
					}
					
				} else {
					player.sendMessage(new TextComponent(""));
					player.sendMessage(new TextComponent(Main.getPrefix() + "§8[§cReportManager§8]"));
					if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.ReportManager.addreason"))) {
						player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
								"/reportmanager addreason <Reason>"));
					}
					if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.ReportManager.removereason"))) {
						player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
								"/reportmanager removereason <ReasonID>"));	
					}
					if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.ReportManager.listreasons"))) {
						player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
								"/reportmanager listreasons"));	
					}
					player.sendMessage(new TextComponent(""));
				}
				
			} else {				
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
			}
			
		} else {
			CommandSender player = sender;
			
			if(args.length == 0) {
				
				player.sendMessage(new TextComponent(""));
				player.sendMessage(new TextComponent(Main.getPrefix() + "§8[§cReportManager§8]"));
				player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
						"/reportmanager addreason <Reason>"));
				player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
						"/reportmanager removereason <ReasonID>"));
				player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
						"/reportmanager listreasons"));
				player.sendMessage(new TextComponent(""));
				
			} else if(args.length == 1) {
				
				if(args[0].equalsIgnoreCase("listreasons")) {
					
					ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.REPORT);
					
					if(!reasons.isEmpty()) {
						
						player.sendMessage(new TextComponent(""));
						for(Reason reason : reasons) {
							player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName()));
						}
						player.sendMessage(new TextComponent(""));
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.List-Reasons.No-Reasons-Found")));
					}
					
				} else {
					player.sendMessage(new TextComponent(""));
					player.sendMessage(new TextComponent(Main.getPrefix() + "§8[§cReportManager§8]"));
					player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
							"/reportmanager addreason <Reason>"));
					player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
							"/reportmanager removereason <ReasonID>"));
					player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
							"/reportmanager listreasons"));
					player.sendMessage(new TextComponent(""));
				}
				
			} else if(args.length == 2) {
				
				if(args[0].equalsIgnoreCase("addreason")) {
					
					if(!ReasonManager.existsReason(ReasonType.REPORT, args[1])) {
						
						ReasonManager.addReason(ReasonType.REPORT, args[1]);
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.ReportManager.Add-Reason.Success")));
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.ReportManager.Add-Reason.Already-Exists")));
					}
					
				} else if(args[0].equalsIgnoreCase("removereason")) {
					
					try {
						
						Integer id = Integer.parseInt(args[1]);
						Reason reason = ReasonManager.getReasonById(ReasonType.REPORT, id);
						
						if(reason != null) {
							
							ReasonManager.removeReason(ReasonType.REPORT, id);
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.ReportManager.Remove-Reason.Success")));
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.ReportManager.Remove-Reason.Not-Exists")));
						}
						
					} catch (NumberFormatException e) {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.NotValidID")));
					}
					
				} else {
					player.sendMessage(new TextComponent(""));
					player.sendMessage(new TextComponent(Main.getPrefix() + "§8[§cReportManager§8]"));
					player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
							"/reportmanager addreason <Reason>"));
					player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
							"/reportmanager removereason <ReasonID>"));
					player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
							"/reportmanager listreasons"));
					player.sendMessage(new TextComponent(""));
				}
				
			} else {
				player.sendMessage(new TextComponent(""));
				player.sendMessage(new TextComponent(Main.getPrefix() + "§8[§cReportManager§8]"));
				player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
						"/reportmanager addreason <Reason>"));
				player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
						"/reportmanager removereason <ReasonID>"));
				player.sendMessage(new TextComponent(Main.getPrefix() + ChatColor.GRAY + 
						"/reportmanager listreasons"));
				player.sendMessage(new TextComponent(""));
			}
		}
		
	}

}
