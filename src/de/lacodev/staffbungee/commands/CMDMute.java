package de.lacodev.staffbungee.commands;

import java.util.ArrayList;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.ReasonType;
import de.lacodev.staffbungee.managers.MuteManager;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.managers.ReasonManager;
import de.lacodev.staffbungee.objects.Reason;
import de.lacodev.staffbungee.utils.ReasonLengthCalculator;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDMute extends Command {

	public CMDMute(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer)sender;
			
			if(Main.getMySQL().isConnected()) {
				
				if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.Mute.Use"))) {
					
					// /mute <Player> <ReasonID> [-s]
					
					if(args.length == 2) {
						
						if(PlayerManager.getUUIDByName(args[0]) != null) {
							
							String uuid = PlayerManager.getUUIDByName(args[0]);
							
							if(!MuteManager.isMuted(uuid)) {
								
								if(!player.getUniqueId().toString().matches(uuid)) {
									
									try {
										
										Integer id = Integer.parseInt(args[1]);
										
										if(ReasonManager.getReasonById(ReasonType.MUTE, id) != null) {
											
											if(Main.getInstance().getConfig().getBoolean("Per-Reason-Permission.Enable")) {
												
												if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getInstance().getConfig().getString("Per-Reason-Permission.Prefix") + id)) {
													
													MuteManager.mute(uuid, ReasonManager.getReasonById(ReasonType.MUTE, id).getName(), player.getUniqueId().toString());
													player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Created")));
													
												} else {
													player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getInstance().getConfig().getString("Per-Reason-Permission.Prefix") + id)));
												}
												
											} else {
												
												MuteManager.mute(uuid, ReasonManager.getReasonById(ReasonType.MUTE, id).getName(), player.getUniqueId().toString());
												player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Created")));
												
											}
											
										} else {
											player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Reason-Not-Exists")));
										}
										
									} catch(NumberFormatException e) {
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.NotValidID")));
									}
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Cannot-mute-self")));
								}
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Already-Muted")));
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Cannot-find-player")));
						}
						
					} else if(args.length == 3) {
						
						if(args[2].equalsIgnoreCase("-s")) {
							
							if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.Mute.Silent"))) {
								
								if(PlayerManager.getUUIDByName(args[0]) != null) {
									
									String uuid = PlayerManager.getUUIDByName(args[0]);
									
									if(!MuteManager.isMuted(uuid)) {
										
										if(!player.getUniqueId().toString().matches(uuid)) {
											
											try {
												
												Integer id = Integer.parseInt(args[1]);
												
												if(ReasonManager.getReasonById(ReasonType.MUTE, id) != null) {
													
													if(Main.getInstance().getConfig().getBoolean("Per-Reason-Permission.Enable")) {
														
														if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getInstance().getConfig().getString("Per-Reason-Permission.Prefix") + id)) {
															
															MuteManager.silentmute(uuid, ReasonManager.getReasonById(ReasonType.MUTE, id).getName(), player.getUniqueId().toString());
															player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Created")));
															
														} else {
															player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getInstance().getConfig().getString("Per-Reason-Permission.Prefix") + id)));
														}
														
													} else {
														
														MuteManager.mute(uuid, ReasonManager.getReasonById(ReasonType.MUTE, id).getName(), player.getUniqueId().toString());
														player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Created")));
														
													}
													
												} else {
													player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Reason-Not-Exists")));
												}
												
											} catch(NumberFormatException e) {
												player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.NotValidID")));
											}
											
										} else {
											player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Cannot-mute-self")));
										}
										
									} else {
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Already-Muted")));
									}
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Cannot-find-player")));
								}
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.Mute.Silent"))));
							}
							
						} else {
							
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Usage") + " -s"));
							
							ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.MUTE);
							
							if(!reasons.isEmpty()) {
								
								player.sendMessage(new TextComponent(""));
								for(Reason reason : reasons) {
									player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName() + " §8| " + ReasonLengthCalculator.calculate(reason.getLength())));
								}
								player.sendMessage(new TextComponent(""));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.No-Reasons")));
							}
							
						}
						
					} else {
						
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Usage") + " -s"));
						
						ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.MUTE);
						
						if(!reasons.isEmpty()) {
							
							player.sendMessage(new TextComponent(""));
							for(Reason reason : reasons) {
								player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName() + " §8| " + ReasonLengthCalculator.calculate(reason.getLength())));
							}
							player.sendMessage(new TextComponent(""));
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.No-Reasons")));
						}
						
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.Mute.Use"))));
				}
				
			} else {				
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
			}
			
		} else {
			
			CommandSender player = sender;
			
			if(args.length == 2) {
				
				if(PlayerManager.getUUIDByName(args[0]) != null) {
					
					String uuid = PlayerManager.getUUIDByName(args[0]);
					
					if(!MuteManager.isMuted(uuid)) {
						
						try {
							
							Integer id = Integer.parseInt(args[1]);
							
							if(ReasonManager.getReasonById(ReasonType.MUTE, id) != null) {
								
								MuteManager.mute(uuid, ReasonManager.getReasonById(ReasonType.MUTE, id).getName(), "Console");
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Created")));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Reason-Not-Exists")));
							}
							
						} catch(NumberFormatException e) {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.NotValidID")));
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Already-Muted")));
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Cannot-find-player")));
				}
				
			} else if(args.length == 3) {
				
				if(args[2].equalsIgnoreCase("-s")) {
					
					if(PlayerManager.getUUIDByName(args[0]) != null) {
						
						String uuid = PlayerManager.getUUIDByName(args[0]);
						
						if(!MuteManager.isMuted(uuid)) {
							
							try {
								
								Integer id = Integer.parseInt(args[1]);
								
								if(ReasonManager.getReasonById(ReasonType.MUTE, id) != null) {
									
									MuteManager.silentmute(uuid, ReasonManager.getReasonById(ReasonType.MUTE, id).getName(), "Console");
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Created")));
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Reason-Not-Exists")));
								}
								
							} catch(NumberFormatException e) {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.NotValidID")));
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Already-Muted")));
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Cannot-find-player")));
					}
					
				} else {
					
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Usage") + " -s"));
					
					ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.MUTE);
					
					if(!reasons.isEmpty()) {
						
						player.sendMessage(new TextComponent(""));
						for(Reason reason : reasons) {
							player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName() + " §8| " + ReasonLengthCalculator.calculate(reason.getLength())));
						}
						player.sendMessage(new TextComponent(""));
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.No-Reasons")));
					}
					
				}
				
			} else {
				
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.Usage") + " -s"));
				
				ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.MUTE);
				
				if(!reasons.isEmpty()) {
					
					player.sendMessage(new TextComponent(""));
					for(Reason reason : reasons) {
						player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName() + " §8| " + ReasonLengthCalculator.calculate(reason.getLength())));
					}
					player.sendMessage(new TextComponent(""));
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Mute-System.No-Reasons")));
				}
				
			}
			
		}
		
	}

}
