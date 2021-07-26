package de.lacodev.staffbungee.commands;

import java.sql.SQLException;
import java.util.ArrayList;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.managers.ReportManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDReports extends Command {

	public CMDReports(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer)sender;
			
			if(Main.getMySQL().isConnected()) {
				
				if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.Reports.See"))) {
					
					if(args.length == 0) {
						
						try {
							ArrayList<String> openReports = ReportManager.getOpenReports();
							
							if(!openReports.isEmpty()) {
								
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Reports.Title")));
								player.sendMessage(new TextComponent(""));
								for(String uuid : openReports) {
									
									TextComponent tc = new TextComponent();
									tc.setText(Main.getPrefix() + Main.getMSG("Messages.Report-System.Reports.Layout")
									.replace("%target%", PlayerManager.getUsernamebyUUID(uuid))
									.replace("%status%", PlayerManager.getStatus(uuid)));
									tc.setClickEvent(new ClickEvent(Action.RUN_COMMAND,"/report claim " + uuid));
									
									String hovertext = Main.getMSG("Messages.Report-System.Reports.Layout-Hover-Title") + "\n\n";
									
									for(String reason : ReportManager.getReportReasons(uuid)) {
										hovertext += "§8- §c" + reason + "\n";
									}
									tc.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new Text(hovertext)));
									
									player.sendMessage(tc);
								}
								player.sendMessage(new TextComponent(""));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Reports.No-Open")));
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Report-System.Reports.Usage")));
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.Reports.See"))));
				}
				
			} else {
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
			}
			
		} else {
			
			try {
				sender.sendMessage(new TextComponent(Main.getPrefix() + "§7Currently there are §c" + ReportManager.getOpenReports().size() + " Reports §7open!"));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
