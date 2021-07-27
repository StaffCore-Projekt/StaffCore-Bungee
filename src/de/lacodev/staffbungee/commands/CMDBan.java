package de.lacodev.staffbungee.commands;

import java.util.ArrayList;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.ReasonType;
import de.lacodev.staffbungee.handlers.ViolationLevelHandler;
import de.lacodev.staffbungee.managers.BanManager;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.managers.ReasonManager;
import de.lacodev.staffbungee.objects.Reason;
import de.lacodev.staffbungee.utils.ReasonLengthCalculator;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CMDBan extends Command {

	public CMDBan(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if(sender instanceof ProxiedPlayer) {
			
			ProxiedPlayer player = (ProxiedPlayer)sender;
			
			if(Main.getMySQL().isConnected()) {
				
				if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.Ban.Use"))) {
					
					// /ban <Player> <ReasonID> [-s]
					
					if(args.length == 2) {
						
						if(PlayerManager.getUUIDByName(args[0]) != null) {
							
							String uuid = PlayerManager.getUUIDByName(args[0]);
							
							if(!BanManager.isBanned(uuid)) {
								
								if(!player.getUniqueId().toString().matches(uuid)) {
									
									try {
										
										Integer id = Integer.parseInt(args[1]);
										
										if(ReasonManager.getReasonById(ReasonType.BAN, id) != null) {
											
											if(Main.getInstance().getConfig().getBoolean("Per-Reason-Permission.Enable")) {

												if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getInstance().getConfig().getString("Per-Reason-Permission.Prefix") + id)) {
													
													BanManager.ban(uuid, ReasonManager.getReasonById(ReasonType.BAN, id).getName(), player.getUniqueId().toString());
													player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Created")));
													
												} else {
													player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getInstance().getConfig().getString("Per-Reason-Permission.Prefix") + id)));
												}
												
											} else {
												
												BanManager.ban(uuid, ReasonManager.getReasonById(ReasonType.BAN, id).getName(), player.getUniqueId().toString());
												player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Created")));
												
											}
											
										} else {
											player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Reason-Not-Exists")));
										}
										
									} catch(NumberFormatException e) {
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.NotValidID")));
									}
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Cannot-ban-self")));
								}
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Already-Banned")));
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Cannot-find-player")));
						}
						
					} else if(args.length == 3) {
						
						if(args[2].equalsIgnoreCase("-s")) {
							
							if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getPermissionNotice("Permissions.Ban.Silent"))) {
								if(PlayerManager.getUUIDByName(args[0]) != null) {
									
									String uuid = PlayerManager.getUUIDByName(args[0]);
									
									if(!BanManager.isBanned(uuid)) {
										
										if(!player.getUniqueId().toString().matches(uuid)) {
											
											try {
												
												Integer id = Integer.parseInt(args[1]);
												
												if(ReasonManager.getReasonById(ReasonType.BAN, id) != null) {
													
													if(Main.getInstance().getConfig().getBoolean("Per-Reason-Permission.Enable")) {

														if(player.hasPermission(Main.getPermissionNotice("Permissions.Everything")) || player.hasPermission(Main.getInstance().getConfig().getString("Per-Reason-Permission.Prefix") + id)) {
															
															BanManager.silentban(uuid, ReasonManager.getReasonById(ReasonType.BAN, id).getName(), player.getUniqueId().toString());
															player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Created")));
															
														} else {
															player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getInstance().getConfig().getString("Per-Reason-Permission.Prefix") + id)));
														}
														
													} else {
														
														BanManager.silentban(uuid, ReasonManager.getReasonById(ReasonType.BAN, id).getName(), player.getUniqueId().toString());
														player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Created")));
														
													}
													
												} else {
													player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Reason-Not-Exists")));
												}
												
											} catch(NumberFormatException e) {
												player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.NotValidID")));
											}
											
										} else {
											player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Cannot-ban-self")));
										}
										
									} else {
										player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Already-Banned")));
									}
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Cannot-find-player")));
								}
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.Ban.Silent"))));
							}
							
						} else {
							
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Usage")));
							
							ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.BAN);
							
							if(!reasons.isEmpty()) {
								
								player.sendMessage(new TextComponent(""));
								for(Reason reason : reasons) {
									player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName() + " §8| " + ReasonLengthCalculator.calculate(reason.getLength())));
								}
								player.sendMessage(new TextComponent(""));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.List-Reasons.No-Reasons-Found")));
							}
							
						}
						
					} else if(args.length == 1) {
						
						String uuid = PlayerManager.getUUIDByName(args[0]);
						
						if(uuid != null) {
							
							if(Main.getInstance().getConfig().getBoolean("ViolationLevelSystem.Enable")) {
								player.sendMessage(new TextComponent(Main.getPrefix() + "§c" + args[0] + "§8(§c"+ ViolationLevelHandler.getVL(uuid) +"§8)"));
							}
							
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Usage") + " -s"));
							
							ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.BAN);
							
							if(!reasons.isEmpty()) {
								
								player.sendMessage(new TextComponent(""));
								for(Reason reason : reasons) {
									player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName() + " §8| " + ReasonLengthCalculator.calculate(reason.getLength())));
								}
								player.sendMessage(new TextComponent(""));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.List-Reasons.No-Reasons-Found")));
							}
							
						} else {
							
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Usage") + " -s"));
							
							ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.BAN);
							
							if(!reasons.isEmpty()) {
								
								player.sendMessage(new TextComponent(""));
								for(Reason reason : reasons) {
									player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName() + " §8| " + ReasonLengthCalculator.calculate(reason.getLength())));
								}
								player.sendMessage(new TextComponent(""));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.List-Reasons.No-Reasons-Found")));
							}
							
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Usage") + " -s"));
						
						ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.BAN);
						
						if(!reasons.isEmpty()) {
							
							player.sendMessage(new TextComponent(""));
							for(Reason reason : reasons) {
								player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName() + " §8| " + ReasonLengthCalculator.calculate(reason.getLength())));
							}
							player.sendMessage(new TextComponent(""));
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.List-Reasons.No-Reasons-Found")));
						}
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Permission").replace("%permission%", Main.getPermissionNotice("Permissions.Ban.Use"))));
				}
				
			} else {				
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.System.No-Connection.Notify")));
			}
			
		} else {
			
			CommandSender player = sender;
			
			if(args.length == 2) {
				
				if(PlayerManager.getUUIDByName(args[0]) != null) {
					
					String uuid = PlayerManager.getUUIDByName(args[0]);
					
					if(!BanManager.isBanned(uuid)) {
						
						try {
							
							Integer id = Integer.parseInt(args[1]);
							
							if(ReasonManager.getReasonById(ReasonType.BAN, id) != null) {
								
								BanManager.ban(uuid, ReasonManager.getReasonById(ReasonType.BAN, id).getName(), "Console");
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Created")));
								
							} else {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Reason-Not-Exists")));
							}
							
						} catch(NumberFormatException e) {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.NotValidID")));
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Already-Banned")));
					}
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Cannot-find-player")));
				}
				
			} else if(args.length == 3) {
				
				if(args[2].equalsIgnoreCase("-s")) {
					
					if(PlayerManager.getUUIDByName(args[0]) != null) {
						
						String uuid = PlayerManager.getUUIDByName(args[0]);
						
						if(!BanManager.isBanned(uuid)) {
							
							try {
								
								Integer id = Integer.parseInt(args[1]);
								
								if(ReasonManager.getReasonById(ReasonType.BAN, id) != null) {
									
									BanManager.silentban(uuid, ReasonManager.getReasonById(ReasonType.BAN, id).getName(), "Console");
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Created")));
									
								} else {
									player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Reason-Not-Exists")));
								}
								
							} catch(NumberFormatException e) {
								player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.NotValidID")));
							}
							
						} else {
							player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Already-Banned")));
						}
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Cannot-find-player")));
					}
					
				} else {
					
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Usage") + " -s"));
					
					ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.BAN);
					
					if(!reasons.isEmpty()) {
						
						player.sendMessage(new TextComponent(""));
						for(Reason reason : reasons) {
							player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName() + " §8| " + ReasonLengthCalculator.calculate(reason.getLength())));
						}
						player.sendMessage(new TextComponent(""));
						
					} else {
						player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.List-Reasons.No-Reasons-Found")));
					}
					
				}
				
			} else {
				
				player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.Ban-System.Usage") + " -s"));
				
				ArrayList<Reason> reasons = ReasonManager.getReasons(ReasonType.BAN);
				
				if(!reasons.isEmpty()) {
					
					player.sendMessage(new TextComponent(""));
					for(Reason reason : reasons) {
						player.sendMessage(new TextComponent("§8- §c" + reason.getType().toString() + " §8| §7#"+ reason.getId() +" §e" + reason.getName() + " §8| " + ReasonLengthCalculator.calculate(reason.getLength())));
					}
					player.sendMessage(new TextComponent(""));
					
				} else {
					player.sendMessage(new TextComponent(Main.getPrefix() + Main.getMSG("Messages.BanManager.List-Reasons.No-Reasons-Found")));
				}
				
			}
			
		}
		
	}

}
