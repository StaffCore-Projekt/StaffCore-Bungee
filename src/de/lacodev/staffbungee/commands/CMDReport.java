package de.lacodev.staffbungee.commands;

import java.util.ArrayList;
import java.util.UUID;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.ReasonType;
import de.lacodev.staffbungee.enums.ReportType;
import de.lacodev.staffbungee.enums.Settings;
import de.lacodev.staffbungee.listeners.ListenerChat;
import de.lacodev.staffbungee.managers.ReasonManager;
import de.lacodev.staffbungee.managers.ReportManager;
import de.lacodev.staffbungee.objects.Reason;
import de.lacodev.staffbungee.utils.VersionDetector;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDReport extends Command {

	public CMDReport(String name) {
		super(name);
	}
	
	public static ArrayList<ProxiedPlayer> notify = new ArrayList<>();

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer)sender;
			
			if(args.length == 2) {
				
				if(!args[0].equalsIgnoreCase("claim")) {
					
					ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[0]);
					
					if(target != null) {
						
						if(target != player) {
							
							try {
								
								Integer id = Integer.parseInt(args[1]);
								
								if(ReasonManager.getReasonById(ReasonType.REPORT, id) != null) {
									
									ReportManager.createPlayerReport(ReportType.PLAYER_REPORT, target.getUniqueId().toString(), player.getUniqueId().toString(), ReasonManager.getReasonById(ReasonType.REPORT, id).getName());
									
									if(!(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.Report.Spam.Bypass")))) {
										ListenerChat.reportspam.put(player, System.currentTimeMillis() + (1000 * Integer.valueOf(Settings.REPORT_ANTISPAM_COOLDOWN_SECONDS.getStandard())));
									}
									
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Notify.User.Report-Created")));
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.No-Reportreasons")));
								}
								
							} catch(NumberFormatException e) {
								
								if(ReasonManager.existsReason(ReasonType.REPORT, args[1])) {
									
									ReportManager.createPlayerReport(ReportType.PLAYER_REPORT, target.getUniqueId().toString(), player.getUniqueId().toString(), args[1]);
									
									if(!(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.Report.Spam.Bypass")))) {
										ListenerChat.reportspam.put(player, System.currentTimeMillis() + (1000 * Integer.valueOf(Settings.REPORT_ANTISPAM_COOLDOWN_SECONDS.getStandard())));
									}
									
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Notify.User.Report-Created")));
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.No-Reportreasons")));
								}
								
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Cannot-report-self")));
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Target-offline")));
					}
					
				} else {
					if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.Report.Claim"))) {
						
						if(!ReportManager.claimed.containsKey(player.getUniqueId().toString())) {
							ProxiedPlayer target = BungeeCord.getInstance().getPlayer(UUID.fromString(args[1]));
							
							if(target != null) {
								
								if(ReportManager.hasOpenReports(target.getUniqueId().toString())) {
									
									ReportManager.claimReports(target.getUniqueId().toString(), player.getUniqueId().toString());
									player.connect(target.getServer().getInfo());
									
									player.sendMessage(new TextComponent(""));
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Notify.Team.Claimed").replace("%target%", target.getName())));
									
									TextComponent tc = new TextComponent(Main.getPrefix() + "§7Server §8» §c" + target.getServer().getInfo().getName());
									tc.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text("§7Username §8» §c" + target.getName() + "\n§7Ping §8» §c" + target.getPing() + "ms "
											+ "\n\n§7Version §8» §c" + VersionDetector.getClientProtocol(target.getPendingConnection()))));
									player.sendMessage(tc);
									
									player.sendMessage(new TextComponent(Main.getPrefix() + "§cFinish the report with: §7/report confirm §8| §7/report cancel"));
									player.sendMessage(new TextComponent(""));
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Notify.Team.Already-Claimed")));
								}
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Target-offline")));
							}
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Currently-Working")));
						}
						
					} else {
						ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[0]);
						
						if(target != null) {
							
							if(target != player) {
								
								try {
									
									Integer id = Integer.parseInt(args[1]);
									
									if(ReasonManager.getReasonById(ReasonType.REPORT, id) != null) {
										
										ReportManager.createPlayerReport(ReportType.PLAYER_REPORT, target.getUniqueId().toString(), player.getUniqueId().toString(), ReasonManager.getReasonById(ReasonType.REPORT, id).getName());
										
										if(!(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.Report.Spam.Bypass")))) {
											ListenerChat.reportspam.put(player, System.currentTimeMillis() + (1000 * Integer.valueOf(Settings.REPORT_ANTISPAM_COOLDOWN_SECONDS.getStandard())));
										}
										
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Notify.User.Report-Created")));
										
									} else {
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.No-Reportreasons")));
									}
									
								} catch(NumberFormatException e) {
									
									if(ReasonManager.existsReason(ReasonType.REPORT, args[1])) {
										
										ReportManager.createPlayerReport(ReportType.PLAYER_REPORT, target.getUniqueId().toString(), player.getUniqueId().toString(), args[1]);
										
										if(!(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.Report.Spam.Bypass")))) {
											ListenerChat.reportspam.put(player, System.currentTimeMillis() + (1000 * Integer.valueOf(Settings.REPORT_ANTISPAM_COOLDOWN_SECONDS.getStandard())));
										}
										
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Notify.User.Report-Created")));
										
									} else {
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.No-Reportreasons")));
									}
									
								}
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Cannot-report-self")));
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Target-offline")));
						}
					}
				}
				
			} else if(args.length == 1) {
				
				if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.Report.Claim"))) {
					
					if(ReportManager.claimed.containsKey(player.getUniqueId().toString())) {
						if(args[0].equalsIgnoreCase("confirm")) {
							
							ReportManager.confirmReport(player.getUniqueId().toString());
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Report-Confirmed")));
							
						} else if(args[0].equalsIgnoreCase("cancel")) {
							
							ReportManager.cancelReport(player.getUniqueId().toString());
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Report-Cancelled")));
							
						} else {
							if(args[0].equalsIgnoreCase("templates")) {
								
								ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.REPORT);
								
								if(!reasons.isEmpty()) {
									
									player.sendMessage(new TextComponent(""));
									for(Reason reason : reasons) {
										player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName()));
									}
									player.sendMessage(new TextComponent(""));
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.No-Reportreasons")));
								}
								
							} else {
								
								player.sendMessage(new TextComponent(""));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Usage.Title")));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Usage.Command")));
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Usage.Template")));
								
								ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.REPORT);
								
								if(!reasons.isEmpty()) {
									
									player.sendMessage(new TextComponent(""));
									for(Reason reason : reasons) {
										player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName()));
									}
									player.sendMessage(new TextComponent(""));
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.No-Reportreasons")));
								}
								
							}
						}
					} else {
						if(args[0].equalsIgnoreCase("templates")) {
							
							ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.REPORT);
							
							if(!reasons.isEmpty()) {
								
								player.sendMessage(new TextComponent(""));
								for(Reason reason : reasons) {
									player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName()));
								}
								player.sendMessage(new TextComponent(""));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.No-Reportreasons")));
							}
							
						} else {
							
							player.sendMessage(new TextComponent(""));
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Usage.Title")));
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Usage.Command")));
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Usage.Template")));
							
							ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.REPORT);
							
							if(!reasons.isEmpty()) {
								
								player.sendMessage(new TextComponent(""));
								for(Reason reason : reasons) {
									player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName()));
								}
								player.sendMessage(new TextComponent(""));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.No-Reportreasons")));
							}
							
						}
					}
					
				} else {
					if(args[0].equalsIgnoreCase("templates")) {
						
						ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.REPORT);
						
						if(!reasons.isEmpty()) {
							
							player.sendMessage(new TextComponent(""));
							for(Reason reason : reasons) {
								player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName()));
							}
							player.sendMessage(new TextComponent(""));
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.No-Reportreasons")));
						}
						
					} else {
						
						player.sendMessage(new TextComponent(""));
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Usage.Title")));
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Usage.Command")));
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Usage.Template")));
						
						ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.REPORT);
						
						if(!reasons.isEmpty()) {
							
							player.sendMessage(new TextComponent(""));
							for(Reason reason : reasons) {
								player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName()));
							}
							player.sendMessage(new TextComponent(""));
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.No-Reportreasons")));
						}
						
					}
				}
				
			} else {
				
				player.sendMessage(new TextComponent(""));
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Usage.Title")));
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Usage.Command")));
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Usage.Template")));
				
				ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.REPORT);
				
				if(!reasons.isEmpty()) {
					
					player.sendMessage(new TextComponent(""));
					for(Reason reason : reasons) {
						player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName()));
					}
					player.sendMessage(new TextComponent(""));
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.No-Reportreasons")));
				}
				
			}
		}
		
	}

}
