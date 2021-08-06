package de.lacodev.staffbungee;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import de.lacodev.staffbungee.commands.CMDBan;
import de.lacodev.staffbungee.commands.CMDBanIp;
import de.lacodev.staffbungee.commands.CMDBanManager;
import de.lacodev.staffbungee.commands.CMDBlackList;
import de.lacodev.staffbungee.commands.CMDChatFilter;
import de.lacodev.staffbungee.commands.CMDChatLog;
import de.lacodev.staffbungee.commands.CMDCheckAlts;
import de.lacodev.staffbungee.commands.CMDCheckPlayer;
import de.lacodev.staffbungee.commands.CMDCheckPunishment;
import de.lacodev.staffbungee.commands.CMDKick;
import de.lacodev.staffbungee.commands.CMDLabyMod;
import de.lacodev.staffbungee.commands.CMDMaintenance;
import de.lacodev.staffbungee.commands.CMDMute;
import de.lacodev.staffbungee.commands.CMDPunishmentHistory;
import de.lacodev.staffbungee.commands.CMDReport;
import de.lacodev.staffbungee.commands.CMDReportManager;
import de.lacodev.staffbungee.commands.CMDReports;
import de.lacodev.staffbungee.commands.CMDResetPlayer;
import de.lacodev.staffbungee.commands.CMDSessions;
import de.lacodev.staffbungee.commands.CMDStaffCore;
import de.lacodev.staffbungee.commands.CMDStaffRollback;
import de.lacodev.staffbungee.commands.CMDTeamChat;
import de.lacodev.staffbungee.commands.CMDUnBan;
import de.lacodev.staffbungee.commands.CMDUnBanIp;
import de.lacodev.staffbungee.commands.CMDUnMute;
import de.lacodev.staffbungee.commands.CMDWarn;
import de.lacodev.staffbungee.commands.CMDWarns;
import de.lacodev.staffbungee.commands.CMDWatchList;
import de.lacodev.staffbungee.enums.MigrationConfig;
import de.lacodev.staffbungee.handlers.AntiMCLeaksHandler;
import de.lacodev.staffbungee.handlers.MaintenanceHandler;
import de.lacodev.staffbungee.handlers.SessionsHandler;
import de.lacodev.staffbungee.handlers.TeamChatHandler;
import de.lacodev.staffbungee.handlers.TranslationHandler;
import de.lacodev.staffbungee.listeners.ListenerChat;
import de.lacodev.staffbungee.listeners.ListenerLogin;
import de.lacodev.staffbungee.listeners.ListenerServerPing;
import de.lacodev.staffbungee.listeners.ListenerWatchListActions;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.managers.SettingsManager;
import de.lacodev.staffbungee.mysql.MySQLConnect;
import de.lacodev.staffbungee.utils.UpdateChecker;
import de.lacodev.staffbungee.utils.VersionDetector;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Main extends Plugin {
	
	public static Main instance;
	
	public static MySQLConnect mysql;
	
	public static String prefix;
	
	public boolean latest = false;
	public String version;
	
	public static AntiMCLeaksHandler antimcleakshandler;
	public static TranslationHandler translator;
	public static SessionsHandler sessions;
	public static MaintenanceHandler maintenance;
	public static TeamChatHandler teamchat;
	
	public void onEnable() {
		
		instance = this;
		
		antimcleakshandler = new AntiMCLeaksHandler();
		translator = new TranslationHandler();
		sessions = new SessionsHandler();
		maintenance = new MaintenanceHandler();
		teamchat = new TeamChatHandler();
		
		loadConfigs();
		applyPrefix();
		registerCommands();
		registerEvents();
		
		startAntiMcLeaksServices();
		
		setupMySQL();
		checkForUpdates();
		if(getMySQL().isConnected()) {
			VersionDetector.setup();
			try {
				SettingsManager.generateDefaultSettings();
				Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §8Settings §asuccessfully §8loaded!"));
			} catch (SQLException e) {
				Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §8Settings §cfailed §8to load!"));
			}
			startWebMcSync();
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» Environment: §7" + BungeeCord.getInstance().getVersion()));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» StaffCore: §7v" + this.getDescription().getVersion() + getPluginUpdate()));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §a§lSUCCESS §8(§aPLUGIN STARTED§8)"));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
		} else {
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» Environment: §7" + BungeeCord.getInstance().getVersion()));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» StaffCore: §7v" + this.getDescription().getVersion()));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §c§lFAILED §8(§cNO CONNECTION§8)"));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
		}
	}

	private String getPluginUpdate() {
		if(latest) {
			return " §8(§a§lLATEST§8)";
		} else {
			if(this.getDescription().getVersion().endsWith("Pre")) {
				return " §8(§d§lEXPERIMENTAL§8)";
			} else {
				return " §8(§c§lOUTDATED§8)";
			}
		}
	}

	private void checkForUpdates() {
		Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §8Checking for updates..."));
		Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
		
        new UpdateChecker(this, 93533).getLatestVersion(version -> {
            if(this.getDescription().getVersion().equalsIgnoreCase(version)) {
                latest = true;
                this.version = version;
                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §7You are using the §alatest build§7!"));
                Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
            } else {
            	if(this.getDescription().getVersion().endsWith("Pre")) {
            		this.version = version;
                	Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §7You are using an §dexperimental build§7!"));
                	Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §7Download the latest version ("+ version +") here:"));
                	Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §chttps://www.staffcore-bungee.net"));
                	Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
            	} else {
            		this.version = version;
                	Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §7You are using an §coutdated build§7!"));
                	Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §7Download the latest version ("+ version +") here:"));
                	Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §chttps://www.staffcore-bungee.net"));
                	Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
            	}
            }

        });
		
	}

	public void onDisable() {
		
		for(ProxiedPlayer all : BungeeCord.getInstance().getPlayers()) {
			getSessionsHandler().stopSession(all.getUniqueId().toString());
			PlayerManager.setPlayerToOffline(all);
		}
		
	}
	
	private void startWebMcSync() {
		getProxy().getScheduler().schedule(this, new Runnable() {

			@Override
			public void run() {
				SettingsManager.values.clear();
			}
			
		}, 10, 1440, TimeUnit.SECONDS);
	}

	private void startAntiMcLeaksServices() {
		if(getConfig().getBoolean("MCLeaks-Blocker.Enable")) {
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §8Activating MCLeaks-Blocker..."));
			
			if(getConfig().getBoolean("MCLeaks-Blocker.Cache-Updater.Enable")) {
				getAntiMCLeaksHandler().cacheAccounts();
				getProxy().getScheduler().schedule(this, new Runnable() {

					@Override
					public void run() {
						getAntiMCLeaksHandler().cacheAccounts();
					}
					
				}, getConfig().getInt("MCLeaks-Blocker.Cache-Updater.Period-In-Minutes"), 1440, TimeUnit.MINUTES);
			} else {
				getAntiMCLeaksHandler().cacheAccounts();
			}
		}
	}

	private void setupMySQL() {
		
		try {
			
			File file = new File(getDataFolder().getPath(), "mysql.yml");
			
			Configuration mysqlcfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			
		    MySQLConnect.HOST = mysqlcfg.getString("MySQL.HOST");
		    MySQLConnect.PORT = mysqlcfg.getString("MySQL.PORT");
		    MySQLConnect.DATABASE = mysqlcfg.getString("MySQL.DATABASE");
		    MySQLConnect.USER = mysqlcfg.getString("MySQL.USERNAME");
		    MySQLConnect.PASSWORD = mysqlcfg.getString("MySQL.PASSWORD");
		    
		    mysql = new MySQLConnect(MySQLConnect.HOST, MySQLConnect.DATABASE, MySQLConnect.USER, MySQLConnect.PASSWORD);
		    
		    // Basic Playerdata and Geolocationtracking
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_playerdb(id INT(6) AUTO_INCREMENT UNIQUE, UUID VARCHAR(255), PLAYERNAME VARCHAR(255), "
		    		+ "BANS INT(6), MUTES INT(6), REPORTS INT(6), WARNS INT(6), LAST_KNOWN_IP VARCHAR(255), FIRST_LOGIN LONG, LAST_LOGIN LONG, PROTECTED INT(6), "
		    		+ "COUNTRY VARCHAR(255), REGION VARCHAR (255), ONLINE INT(6))");
		    
		    // SessionsLog, Monitoring and Onlinetime Calculation
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_sessionsdb(id INT(6) AUTO_INCREMENT UNIQUE, SESSION_ID VARCHAR(255), UUID VARCHAR(255), SESSION_START LONG, "
		    		+ "SESSION_END LONG, IP_ADDRESS VARCHAR(255), MC_VERSION VARCHAR(255), VIRTUAL_HOST VARCHAR(255), FINISHED BOOLEAN)");
		    
		    // ChatLog and Chat Monitoring
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_messages(id INT(6) AUTO_INCREMENT UNIQUE, SENDER_UUID VARCHAR(255), "
		    		+ "MESSAGE VARCHAR(256), reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)");
		    
		    // General Settings and Maintenance System
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_settings(id INT(6) AUTO_INCREMENT UNIQUE, SETTING VARCHAR(255), VALUE VARCHAR(500))");
		    
		    // PunishmentSystem Reason Management
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_reasonsdb(id INT(6) AUTO_INCREMENT UNIQUE, TYPE VARCHAR(255), NAME VARCHAR(500), BAN_LENGTH LONG, ADMIN_BAN BOOLEAN)");
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_reportreasonsdb(id INT(6) AUTO_INCREMENT UNIQUE, TYPE VARCHAR(255), NAME VARCHAR(500))");
		    
		    // BanSystem Banregister
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_bansdb(id INT(6) AUTO_INCREMENT UNIQUE, BAN_ID VARCHAR(35), BANNED_UUID VARCHAR(255), TEAM_UUID VARCHAR(255), REASON VARCHAR(255), BAN_END LONG)");
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_ipbansdb(id INT(6) AUTO_INCREMENT UNIQUE, BANNED_UUID VARCHAR(255), IP_ADDRESS VARCHAR(255), TEAM_UUID VARCHAR(255), BAN_END LONG)");
		    
		    // MuteSystem Muteregister
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_mutesdb(id INT(6) AUTO_INCREMENT UNIQUE, MUTE_ID VARCHAR(35), MUTED_UUID VARCHAR(255), TEAM_UUID VARCHAR(255), REASON VARCHAR(255), MUTE_END LONG)");
		    
		    // WarnSystem Warnregister
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_warnsdb(id INT(6) AUTO_INCREMENT UNIQUE, WARNED_UUID VARCHAR(255), TEAM_UUID VARCHAR(255), REASON VARCHAR(255), WARNED_AT LONG, SUB_SERVER VARCHAR(255) NOT NULL DEFAULT 'Offline')");
		    
		    // Punishment-History
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_punishmentsdb(id INT(6) AUTO_INCREMENT UNIQUE, TYPE VARCHAR(255), PUNISH_ID VARCHAR(35), UUID VARCHAR(255), TEAM_UUID VARCHAR(255), REASON VARCHAR(255), "
		    		+ "BAN_START LONG, BAN_END LONG, SUB_SERVER VARCHAR(255) NOT NULL DEFAULT 'Offline', UNBANNED BOOLEAN, UNBAN_TIME LONG, UNBAN_REASON VARCHAR(255), UNBAN_STAFF VARCHAR(255))");
		    
		    // WatchList
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_watchlistdb(id INT(6) AUTO_INCREMENT UNIQUE, UUID VARCHAR(255), TEAM_UUID VARCHAR(255), INSERTED_AT LONG)");
		    
		    // ReportSystem
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_reportsdb(id INT(6) AUTO_INCREMENT UNIQUE, TYPE VARCHAR(255), UUID VARCHAR(255), REPORTER_UUID VARCHAR(255), REASON VARCHAR(255), "
		    		+ "TEAM_UUID VARCHAR(255), STATUS INT(6), CREATED_AT LONG, UPDATED_AT LONG)");
		    
		    // Swearing ChatFilter
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_swearingdb(id INT(6) AUTO_INCREMENT UNIQUE, WORD VARCHAR(255), ADDED_AT LONG)");
		    
		    // Violationlevel System
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_violationleveldb(id INT(6) AUTO_INCREMENT UNIQUE, UUID VARCHAR(255), VL INT(6))");
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_violationresetdb(id INT(6) AUTO_INCREMENT UNIQUE, UUID VARCHAR(255), NEXT_RESET LONG)");
		    
		    // Blacklist System
		    mysql.update("CREATE TABLE IF NOT EXISTS StaffCore_blacklistdb(id INT(6) AUTO_INCREMENT UNIQUE, USERNAME VARCHAR(255), ADDED_AT LONG, ADDED_BY VARCHAR(255))");
		    
		    mysql.updateTables("ALTER TABLE StaffCore_punishmentsdb ADD SUB_SERVER VARCHAR(255) NOT NULL DEFAULT 'Offline' AFTER BAN_END");
		    mysql.updateTables("ALTER TABLE StaffCore_punishmentsdb ADD PUNISH_ID VARCHAR(35) AFTER TYPE");
		    mysql.updateTables("ALTER TABLE StaffCore_punishmentsdb ADD UNBANNED BOOLEAN AFTER SUB_SERVER");
		    mysql.updateTables("ALTER TABLE StaffCore_punishmentsdb ADD UNBAN_TIME LONG AFTER UNBANNED");
		    mysql.updateTables("ALTER TABLE StaffCore_punishmentsdb ADD UNBAN_REASON VARCHAR(255) AFTER UNBAN_TIME");
		    mysql.updateTables("ALTER TABLE StaffCore_punishmentsdb ADD UNBAN_STAFF VARCHAR(255) AFTER UNBAN_REASON");
		    mysql.updateTables("ALTER TABLE StaffCore_bansdb ADD BAN_ID VARCHAR(35) AFTER id");
		    mysql.updateTables("ALTER TABLE StaffCore_mutesdb ADD MUTE_ID VARCHAR(35) AFTER id");
		    mysql.updateTables("ALTER TABLE StaffCore_sessionsdb ADD VIRTUAL_HOST VARCHAR(255) AFTER MC_VERSION");
		    mysql.updateTables("ALTER TABLE StaffCore_reasonsdb ADD ADMIN_BAN BOOLEAN AFTER BAN_LENGTH");
		    mysql.updateTables("ALTER TABLE StaffCore_warnsdb ADD SUB_SERVER VARCHAR(255) NOT NULL DEFAULT 'Offline' AFTER WARNED_AT");
		    
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8(§7MySQL§8) §8- §aSuccessfully §7created the database §atables"));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			
		} catch(IOException e) {
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8(§7MySQL§8) §8- §cFailed to §7create the database §ctables"));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
		}
		
	}

	private void loadConfigs() {
		
	    if (!getDataFolder().exists()) {
	        getDataFolder().mkdir();
	    }
		
	    File permission = new File(getDataFolder().getPath(), "permissions.yml");
	    
	    if(!permission.exists()) {
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» Generating... (§epermissions.yml§8)"));
	    	try {
				permission.createNewFile();
				
				Configuration configcfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(permission);
				
				configcfg.set("Permissions.Everything", "staffcore.*");
				configcfg.set("Permissions.System.Notify", "staffcore.notify");
				configcfg.set("Permissions.System.Show-IP", "staffcore.show.ip");
				configcfg.set("Permissions.System.ResetPlayer", "staffcore.system.resetplayer");
				configcfg.set("Permissions.System.VPN-Block.Bypass", "staffcore.vpn.bypass");
				configcfg.set("Permissions.IpBan.Use", "staffcore.ipban.use");
				configcfg.set("Permissions.IpBan.Notify", "staffcore.ipban.notify");
				configcfg.set("Permissions.IpUnBan.Use", "staffcore.ipunban.use");
				configcfg.set("Permissions.IpUnBan.Notify", "staffcore.ipunban.notify");
				configcfg.set("Permissions.Report.Notify", "staffcore.report.notify");
				configcfg.set("Permissions.Report.Claim", "staffcore.report.claim");
				configcfg.set("Permissions.Report.Spam.Bypass", "staffcore.report.spam.bypass");
				configcfg.set("Permissions.Reports.See", "staffcore.reports.see");
				configcfg.set("Permissions.Check.Use", "staffcore.check.use");
				configcfg.set("Permissions.BanManager.addreason", "staffcore.banmanager.addreason");
				configcfg.set("Permissions.BanManager.removereason", "staffcore.banmanager.removereason");
				configcfg.set("Permissions.BanManager.listreasons", "staffcore.banmanager.listreasons");
				configcfg.set("Permissions.ReportManager.addreason", "staffcore.reportmanager.addreason");
				configcfg.set("Permissions.ReportManager.removereason", "staffcore.reportmanager.removereason");
				configcfg.set("Permissions.ReportManager.listreasons", "staffcore.reportmanager.listreasons");
				configcfg.set("Permissions.Ban.Use", "staffcore.ban.use");
				configcfg.set("Permissions.Ban.Notify", "staffcore.ban.notify");
				configcfg.set("Permissions.Ban.Silent", "staffcore.ban.silent");
				configcfg.set("Permissions.Mute.Use", "staffcore.mute.use");
				configcfg.set("Permissions.Mute.Notify", "staffcore.mute.notify");
				configcfg.set("Permissions.Mute.Silent", "staffcore.mute.silent");
				configcfg.set("Permissions.Kick.Use", "staffcore.kick.use");
				configcfg.set("Permissions.Kick.Notify", "staffcore.kick.notify");
				configcfg.set("Permissions.UnBan.Use", "staffcore.unban.use");
				configcfg.set("Permissions.UnBan.Notify", "staffcore.unban.notify");
				configcfg.set("Permissions.UnBan.Silent", "staffcore.unban.silent");
				configcfg.set("Permissions.UnMute.Use", "staffcore.unmute.use");
				configcfg.set("Permissions.UnMute.Notify", "staffcore.unmute.notify");
				configcfg.set("Permissions.UnMute.Silent", "staffcore.unmute.silent");
				configcfg.set("Permissions.PunishmentHistory.See", "staffcore.punishmenthistory.see");
				configcfg.set("Permissions.CheckAlts.Use", "staffcore.checkalts.use");
				configcfg.set("Permissions.SessionsLookup.Use", "staffcore.sessionslookup.use");
				configcfg.set("Permissions.Warn.Use", "staffcore.warn.use");
				configcfg.set("Permissions.Warn.Notify", "staffcore.warn.notify");
				configcfg.set("Permissions.Warns.See", "staffcore.warns.see");
				configcfg.set("Permissions.Maintenance.Info", "staffcore.maintenance.info");
				configcfg.set("Permissions.Maintenance.Use", "staffcore.maintenance.use");
				configcfg.set("Permissions.Maintenance.Bypass", "staffcore.maintenance.bypass");
				configcfg.set("Permissions.WatchList.Change", "staffcore.watchlist.change");
				configcfg.set("Permissions.WatchList.Notify", "staffcore.watchlist.notify");
				configcfg.set("Permissions.ChatFilter.Change", "staffcore.chatfilter.change");
				configcfg.set("Permissions.ChatFilter.Bypass", "staffcore.chatfilter.bypass");
				configcfg.set("Permissions.ChatLog.Use", "staffcore.chatlog.use");
				configcfg.set("Permissions.TeamChat.Use", "staffcore.teamchat.use");
				configcfg.set("Permissions.TeamChat.Ghost", "staffcore.teamchat.ghost");
				configcfg.set("Permissions.TeamChat.List", "staffcore.teamchat.list");
				configcfg.set("Permissions.TeamChat.AutoLogin", "staffcore.teamchat.autologin");
				configcfg.set("Permissions.TeamChat.Ghost-AutoLogin", "staffcore.teamchat.ghost.autologin");
				configcfg.set("Permissions.LabyMod.Info", "staffcore.labymod.info");
				configcfg.set("Permissions.Punishment.Check", "staffcore.punishment.check");
				configcfg.set("Permissions.Blacklist.Change", "staffcore.blacklist.change");
				configcfg.set("Permissions.StaffRollback.Use", "staffcore.staffrollback.use");
				
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(configcfg, permission);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    File discord = new File(getDataFolder().getPath(), "discord.yml");
	    
	    if(!discord.exists()) {
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» Generating... (§ediscord.yml§8)"));
	    	try {
	    		discord.createNewFile();
	    		
	    		Configuration configcfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(discord);
	    		
				configcfg.set("Discord-Integration.Events.Playerdata-Request.Enable", false);
				configcfg.set("Discord-Integration.Events.Playerdata-Request.WebHook-URL", "{Your Discord-WebHook URL}");
				configcfg.set("Discord-Integration.Events.Network-Ban.Enable", false);
				configcfg.set("Discord-Integration.Events.Network-Ban.WebHook-URL", "{Your Discord-WebHook URL}");
				configcfg.set("Discord-Integration.Events.Network-Mute.Enable", false);
				configcfg.set("Discord-Integration.Events.Network-Mute.WebHook-URL", "{Your Discord-WebHook URL}");
				configcfg.set("Discord-Integration.Events.Network-Kick.Enable", false);
				configcfg.set("Discord-Integration.Events.Network-Kick.WebHook-URL", "{Your Discord-WebHook URL}");
				configcfg.set("Discord-Integration.Events.Network-Warn.Enable", false);
				configcfg.set("Discord-Integration.Events.Network-Warn.WebHook-URL", "{Your Discord-WebHook URL}");
				configcfg.set("Discord-Integration.Events.Network-Unban.Enable", false);
				configcfg.set("Discord-Integration.Events.Network-Unban.WebHook-URL", "{Your Discord-WebHook URL}");
				configcfg.set("Discord-Integration.Events.Network-Unmute.Enable", false);
				configcfg.set("Discord-Integration.Events.Network-Unmute.WebHook-URL", "{Your Discord-WebHook URL}");
				configcfg.set("Discord-Integration.Events.Player-Report.Enable", false);
				configcfg.set("Discord-Integration.Events.Player-Report.WebHook-URL", "{Your Discord-WebHook URL}");
				configcfg.set("Discord-Integration.Events.Auto-Report.Enable", false);
				configcfg.set("Discord-Integration.Events.Auto-Report.WebHook-URL", "{Your Discord-WebHook URL}");
				configcfg.set("Discord-Integration.Events.Network-IPBan.Enable", false);
				configcfg.set("Discord-Integration.Events.Network-IPBan.WebHook-URL", "{Your Discord-WebHook URL}");
				configcfg.set("Discord-Integration.Events.WatchList-Alerts.Enable", false);
				configcfg.set("Discord-Integration.Events.WatchList-Alerts.WebHook-URL", "{Your Discord-WebHook URL}");
				
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(configcfg, discord);
	    		
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    }
	    
	    File chatfilter = new File(getDataFolder().getPath(), "chatfilter.yml");
	    
	    if(!chatfilter.exists()) {
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» Generating... (§echatfilter.yml§8)"));
	    	try {
	    		chatfilter.createNewFile();
	    		
	    		Configuration configcfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(chatfilter);
	    		
				configcfg.set("ChatFilter.Blocks-For-Report", 5);
				configcfg.set("ChatFilter.Match-for-Action-in-Percentage", 75);
				configcfg.set("ChatFilter.Advertisement-Whitelist", Arrays.asList("yourserver.com","ts.yourserver.com","discord.yourserver.com","forum.yourserver.com","www.staffcore-bungee.net"));
				configcfg.set("ChatFilter.Blocked-Domains.ccTLD", 
						Arrays.asList(
						".ac",".ad",".ae",".af",".ag",".ai",".al",".am",".an",".ao",".aq",".ar",".as",".asia",".at",".au",".aw",".ax",
						".az",".ba",".bb",".bd",".be",".bf",".bg",".bh",".bi",".bj",".bm",".bn",".bo",".br",".bs",".bt",".bv",".bw",
						".by",".bz",".ca",".cc",".cd",".cf",".cg",".ch",".ci",".ck",".cl",".cm",".cn",".co",".cr",".cs",".cu",".cv",
						".cx",".cy",".cz",".de",".dj",".dk",".dm",".do",".dz",".ec",".ee",".eg",".eh",".er",".es",".et",".eu",".fi",
						".fj",".fk",".fm",".fo",".fr",".ga",".gb",".gd",".ge",".gf",".gg",".gh",".gi",".gl",".gm",".gn",".gp",".gq",
						".gr",".gs",".gt",".gu",".gw",".gy",".hk",".hm",".hn",".hr",".ht",".hu",".id",".ie",".il",".im",".in",".io",
						".iq",".ir",".is",".it",".je",".jm",".jo",".jp",".ke",".kg",".kh",".ki",".km",".kn",".ko",".kp",".kr",".kw",
						".ky",".kz",".la",".lb",".lc",".li",".lk",".lr",".ls",".lt",".lu",".lv",".ly",".ma",".mc",".md",".mg",".mh",
						".mk",".ml",".mm",".mn",".mo",".mp",".mq",".mr",".ms",".mt",".mu",".mv",".mw",".mx",".my",".na",".nc",".ne",
						".nf",".ng",".ni",".nl",".no",".np",".nr",".nu",".nz",".om",".pa",".pe",".pf",".pg",".ph",".pk",".pl",".pm",
						".pn",".pr",".ps",".pt",".pw",".py",".qa",".re",".ro",".ru",".rw",".sa",".sb",".sc",".sd",".se",".sg",".sh",
						".si",".sj",".sk",".sl",".sm",".sn",".so",".sr",".st",".su",".sv",".sy",".sz",".tc",".td",".tf",".tg",".th",
						".tj",".tk",".tm",".tn",".to",".tp",".tr",".tt",".tv",".tw",".tz",".ua",".ug",".uk",".um",".us",".uy",".uz",
						".va",".vc",".ve",".vg",".vi",".vn",".vu",".wf",".ws",".ye",".yt",".yu",".za",".zm",".zw",
						"(.)ac","(.)ad","(.)ae","(.)af","(.)ag","(.)ai","(.)al","(.)am","(.)an","(.)ao","(.)aq","(.)ar","(.)as","(.)asia","(.)at","(.)au","(.)aw","(.)ax",
						"(.)az","(.)ba","(.)bb","(.)bd","(.)be","(.)bf","(.)bg","(.)bh","(.)bi","(.)bj","(.)bm","(.)bn","(.)bo","(.)br","(.)bs","(.)bt","(.)bv","(.)bw",
						"(.)by","(.)bz","(.)ca","(.)cc","(.)cd","(.)cf","(.)cg","(.)ch","(.)ci","(.)ck","(.)cl","(.)cm","(.)cn","(.)co","(.)cr","(.)cs","(.)cu","(.)cv",
						"(.)cx","(.)cy","(.)cz","(.)de","(.)dj","(.)dk","(.)dm","(.)do","(.)dz","(.)ec","(.)ee","(.)eg","(.)eh","(.)er","(.)es","(.)et","(.)eu","(.)fi",
						"(.)fj","(.)fk","(.)fm","(.)fo","(.)fr","(.)ga","(.)gb","(.)gd","(.)ge","(.)gf","(.)gg","(.)gh","(.)gi","(.)gl","(.)gm","(.)gn","(.)gp","(.)gq",
						"(.)gr","(.)gs","(.)gt","(.)gu","(.)gw","(.)gy","(.)hk","(.)hm","(.)hn","(.)hr","(.)ht","(.)hu","(.)id","(.)ie","(.)il","(.)im","(.)in","(.)io",
						"(.)iq","(.)ir","(.)is","(.)it","(.)je","(.)jm","(.)jo","(.)jp","(.)ke","(.)kg","(.)kh","(.)ki","(.)km","(.)kn","(.)ko","(.)kp","(.)kr","(.)kw",
						"(.)ky","(.)kz","(.)la","(.)lb","(.)lc","(.)li","(.)lk","(.)lr","(.)ls","(.)lt","(.)lu","(.)lv","(.)ly","(.)ma","(.)mc","(.)md","(.)mg","(.)mh",
						"(.)mk","(.)ml","(.)mm","(.)mn","(.)mo","(.)mp","(.)mq","(.)mr","(.)ms","(.)mt","(.)mu","(.)mv","(.)mw","(.)mx","(.)my","(.)na","(.)nc","(.)ne",
						"(.)nf","(.)ng","(.)ni","(.)nl","(.)no","(.)np","(.)nr","(.)nu","(.)nz","(.)om","(.)pa","(.)pe","(.)pf","(.)pg","(.)ph","(.)pk","(.)pl","(.)pm",
						"(.)pn","(.)pr","(.)ps","(.)pt","(.)pw","(.)py","(.)qa","(.)re","(.)ro","(.)ru","(.)rw","(.)sa","(.)sb","(.)sc","(.)sd","(.)se","(.)sg","(.)sh",
						"(.)si","(.)sj","(.)sk","(.)sl","(.)sm","(.)sn","(.)so","(.)sr","(.)st","(.)su","(.)sv","(.)sy","(.)sz","(.)tc","(.)td","(.)tf","(.)tg","(.)th",
						"(.)tj","(.)tk","(.)tm","(.)tn","(.)to","(.)tp","(.)tr","(.)tt","(.)tv","(.)tw","(.)tz","(.)ua","(.)ug","(.)uk","(.)um","(.)us","(.)uy","(.)uz",
						"(.)va","(.)vc","(.)ve","(.)vg","(.)vi","(.)vn","(.)vu","(.)wf","(.)ws","(.)ye","(.)yt","(.)yu","(.)za","(.)zm","(.)zw",
						"[.]ac","[.]ad","[.]ae","[.]af","[.]ag","[.]ai","[.]al","[.]am","[.]an","[.]ao","[.]aq","[.]ar","[.]as","[.]asia","[.]at","[.]au","[.]aw","[.]ax",
						"[.]az","[.]ba","[.]bb","[.]bd","[.]be","[.]bf","[.]bg","[.]bh","[.]bi","[.]bj","[.]bm","[.]bn","[.]bo","[.]br","[.]bs","[.]bt","[.]bv","[.]bw",
						"[.]by","[.]bz","[.]ca","[.]cc","[.]cd","[.]cf","[.]cg","[.]ch","[.]ci","[.]ck","[.]cl","[.]cm","[.]cn","[.]co","[.]cr","[.]cs","[.]cu","[.]cv",
						"[.]cx","[.]cy","[.]cz","[.]de","[.]dj","[.]dk","[.]dm","[.]do","[.]dz","[.]ec","[.]ee","[.]eg","[.]eh","[.]er","[.]es","[.]et","[.]eu","[.]fi",
						"[.]fj","[.]fk","[.]fm","[.]fo","[.]fr","[.]ga","[.]gb","[.]gd","[.]ge","[.]gf","[.]gg","[.]gh","[.]gi","[.]gl","[.]gm","[.]gn","[.]gp","[.]gq",
						"[.]gr","[.]gs","[.]gt","[.]gu","[.]gw","[.]gy","[.]hk","[.]hm","[.]hn","[.]hr","[.]ht","[.]hu","[.]id","[.]ie","[.]il","[.]im","[.]in","[.]io",
						"[.]iq","[.]ir","[.]is","[.]it","[.]je","[.]jm","[.]jo","[.]jp","[.]ke","[.]kg","[.]kh","[.]ki","[.]km","[.]kn","[.]ko","[.]kp","[.]kr","[.]kw",
						"[.]ky","[.]kz","[.]la","[.]lb","[.]lc","[.]li","[.]lk","[.]lr","[.]ls","[.]lt","[.]lu","[.]lv","[.]ly","[.]ma","[.]mc","[.]md","[.]mg","[.]mh",
						"[.]mk","[.]ml","[.]mm","[.]mn","[.]mo","[.]mp","[.]mq","[.]mr","[.]ms","[.]mt","[.]mu","[.]mv","[.]mw","[.]mx","[.]my","[.]na","[.]nc","[.]ne",
						"[.]nf","[.]ng","[.]ni","[.]nl","[.]no","[.]np","[.]nr","[.]nu","[.]nz","[.]om","[.]pa","[.]pe","[.]pf","[.]pg","[.]ph","[.]pk","[.]pl","[.]pm",
						"[.]pn","[.]pr","[.]ps","[.]pt","[.]pw","[.]py","[.]qa","[.]re","[.]ro","[.]ru","[.]rw","[.]sa","[.]sb","[.]sc","[.]sd","[.]se","[.]sg","[.]sh",
						"[.]si","[.]sj","[.]sk","[.]sl","[.]sm","[.]sn","[.]so","[.]sr","[.]st","[.]su","[.]sv","[.]sy","[.]sz","[.]tc","[.]td","[.]tf","[.]tg","[.]th",
						"[.]tj","[.]tk","[.]tm","[.]tn","[.]to","[.]tp","[.]tr","[.]tt","[.]tv","[.]tw","[.]tz","[.]ua","[.]ug","[.]uk","[.]um","[.]us","[.]uy","[.]uz",
						"[.]va","[.]vc","[.]ve","[.]vg","[.]vi","[.]vn","[.]vu","[.]wf","[.]ws","[.]ye","[.]yt","[.]yu","[.]za","[.]zm","[.]zw"
						));
				configcfg.set("ChatFilter.Blocked-Domains.gTLD", 
						Arrays.asList(
								".edu",".mil",".com",".jobs",".org",".name",".travel",".info",".biz",".mobi",".int",".gov",".coop",".museum",".pro",".aero",".post",
								"(.)edu","(.)mil","(.)com","(.)jobs","(.)org","(.)name","(.)travel","(.)info","(.)biz","(.)mobi","(.)int","(.)gov","(.)coop","(.)museum","(.)pro","(.)aero","(.)post",
								"[.]edu","[.]mil","[.]com","[.]jobs","[.]org","[.]name","[.]travel","[.]info","[.]biz","[.]mobi","[.]int","[.]gov","[.]coop","[.]museum","[.]pro","[.]aero","[.]post"
								));
				
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(configcfg, chatfilter);
				
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}	    	
	    }
	    
		// Creation of config.yml
		File config = new File(getDataFolder().getPath(), "config.yml");
		
		if(!config.exists()) {
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» Generating... (§econfig.yml§8)"));
			try {
				config.createNewFile();
				
				Configuration configcfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
				
				configcfg.set("General.System-Prefix", "&cSystem &8%double_arrow% ");
				configcfg.set("General.Custom-Messages.Enable", false);
				configcfg.set("General.Custom-Messages.Rest-API-Key", "{Your API-Key from the Dashboard}");
				configcfg.set("General.Language", "us");
				configcfg.set("General.Time-Format", "yyyy.MM.dd, HH:mm");
				configcfg.set("ViolationLevelSystem.Enable", false);
				configcfg.set("ViolationLevelSystem.Adding-Points.CONFIRMED_REPORT", 3);
				configcfg.set("ViolationLevelSystem.Adding-Points.WARN", 5);
				configcfg.set("ViolationLevelSystem.Adding-Points.KICK", 7);
				configcfg.set("ViolationLevelSystem.Adding-Points.MUTE", 14);
				configcfg.set("ViolationLevelSystem.Adding-Points.BAN", 21);
				configcfg.set("ViolationLevelSystem.Adding-Points.SILENT_MUTE", 28);
				configcfg.set("ViolationLevelSystem.Adding-Points.SILENT_BAN", 35);
				configcfg.set("ViolationLevelSystem.Adding-Points.IP_BAN", 60);
				configcfg.set("ViolationLevelSystem.Remove-Points.Daily", 2);
				configcfg.set("VPN-Detection.Allow-VPN-Join", true);
				configcfg.set("Per-Reason-Permission.Enable", false);
				configcfg.set("Per-Reason-Permission.Prefix", "staffcore.reason.");
				configcfg.set("Unban.Force-Reason", false);
				configcfg.set("Unmute.Force-Reason", false);
				configcfg.set("WebUI.URL", "https://ui.staffcore-bungee.net");
				
				for(MigrationConfig configs : MigrationConfig.values()) {
					configcfg.set("Migration-Helper.Configs." + configs.toString(), true);
				}
				configcfg.set("Migration-Helper.LiteBans-Config.Table-Prefix", "litebans_");
				
				configcfg.set("MCLeaks-Blocker.Enable", true);
				configcfg.set("MCLeaks-Blocker.Cache-Updater.Enable", true);
				configcfg.set("MCLeaks-Blocker.Cache-Updater.Period-In-Minutes", 10);
				
				configcfg.set("IPBan.Duration-In-Hours", 36);
				
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(configcfg, config);
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
		//Creation of mysql.yml
		File mysql = new File(getDataFolder().getPath(), "mysql.yml");
		
		if(!mysql.exists()) {
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» Generating... (§emysql.yml§8)"));
			try {
				mysql.createNewFile();
				
				Configuration mysqlcfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(mysql);
				
				mysqlcfg.set("MySQL.HOST", "host");
				mysqlcfg.set("MySQL.PORT", "3306");
				mysqlcfg.set("MySQL.DATABASE", "database");
				mysqlcfg.set("MySQL.USERNAME", "username");
				mysqlcfg.set("MySQL.PASSWORD", "password");
				
				ConfigurationProvider.getProvider(YamlConfiguration.class).save(mysqlcfg, mysql);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
		Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» Fetching... (§eTranslations from LacoDev Services§8)"));
		Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
		
		translator.init();
		translator.fetch(getConfig().getString("General.Language").substring(0, 2));
		
		if(getConfig().getBoolean("General.Custom-Messages.Enable")) {
			translator.fetchCustom(getConfig().getString("General.Custom-Messages.Rest-API-Key"));
		}
		
	}

	private void registerCommands() {
		getProxy().getPluginManager().registerCommand(this, new CMDCheckPlayer("check"));
		getProxy().getPluginManager().registerCommand(this, new CMDCheckAlts("checkalts"));
		getProxy().getPluginManager().registerCommand(this, new CMDSessions("sessions"));
		getProxy().getPluginManager().registerCommand(this, new CMDStaffCore("staffcore"));
		getProxy().getPluginManager().registerCommand(this, new CMDMaintenance("maintenance"));
		getProxy().getPluginManager().registerCommand(this, new CMDBanManager("banmanager"));
		getProxy().getPluginManager().registerCommand(this, new CMDBan("ban"));
		getProxy().getPluginManager().registerCommand(this, new CMDUnBan("unban"));
		getProxy().getPluginManager().registerCommand(this, new CMDMute("mute"));
		getProxy().getPluginManager().registerCommand(this, new CMDUnMute("unmute"));
		getProxy().getPluginManager().registerCommand(this, new CMDPunishmentHistory("punishmenthistory"));
		getProxy().getPluginManager().registerCommand(this, new CMDWarn("warn"));
		getProxy().getPluginManager().registerCommand(this, new CMDWarns("warns"));
		getProxy().getPluginManager().registerCommand(this, new CMDWatchList("watchlist"));
		getProxy().getPluginManager().registerCommand(this, new CMDBanIp("banip"));
		getProxy().getPluginManager().registerCommand(this, new CMDUnBanIp("unbanip"));
		getProxy().getPluginManager().registerCommand(this, new CMDReport("report"));
		getProxy().getPluginManager().registerCommand(this, new CMDReportManager("reportmanager"));
		getProxy().getPluginManager().registerCommand(this, new CMDReports("reports"));
		getProxy().getPluginManager().registerCommand(this, new CMDKick("kick"));
		getProxy().getPluginManager().registerCommand(this, new CMDChatLog("chatlog"));
		getProxy().getPluginManager().registerCommand(this, new CMDTeamChat("teamchat"));
		getProxy().getPluginManager().registerCommand(this, new CMDResetPlayer("resetplayer"));
		getProxy().getPluginManager().registerCommand(this, new CMDChatFilter("chatfilter"));
		getProxy().getPluginManager().registerCommand(this, new CMDLabyMod("labymod"));
		getProxy().getPluginManager().registerCommand(this, new CMDCheckPunishment("checkpunishment"));
		getProxy().getPluginManager().registerCommand(this, new CMDBlackList("blacklist"));
		getProxy().getPluginManager().registerCommand(this, new CMDStaffRollback("staffrollback"));
	}

	private void registerEvents() {
		getProxy().getPluginManager().registerListener(this, new ListenerLogin());
		getProxy().getPluginManager().registerListener(this, new ListenerChat());
		getProxy().getPluginManager().registerListener(this, new ListenerServerPing());
		getProxy().getPluginManager().registerListener(this, new ListenerWatchListActions());
	}
	
	public static String getPermissionNotice(String config) {
		if(getInstance().getPermissions().getString(config) != null) {
			return ChatColor.stripColor(getInstance().getPermissions().getString(config));
		} else {
			return "Permission failed to load";
		}
	}
	
	public Configuration getConfig() {
		File config = new File(getDataFolder().getPath(), "config.yml");
		
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Configuration getPermissions() {
		File config = new File(getDataFolder().getPath(), "permissions.yml");
		
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Configuration getChatFilter() {
		File config = new File(getDataFolder().getPath(), "chatfilter.yml");
		
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Configuration getDiscord() {
		File config = new File(getDataFolder().getPath(), "discord.yml");
		
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getMSG(String key) {
		
		return ChatColor.translateAlternateColorCodes('&', translator.getTranslation(key)).replace("%newline%", "\n");
		
	}
	
	private void applyPrefix() {
		
		File file = new File(getDataFolder().getPath(), "config.yml");
	    try {
			Configuration configcfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			
			prefix = ChatColor.translateAlternateColorCodes('&', configcfg.getString("General.System-Prefix"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static String getPrefix() {
		return prefix.replace("%double_arrow%", "»");
	}
	
	public static SessionsHandler getSessionsHandler() {
		return sessions;
	}
	
    public static AntiMCLeaksHandler getAntiMCLeaksHandler() {
    	return antimcleakshandler;
    }
	
	public static MaintenanceHandler getMaintenanceHandler() {
		return maintenance;
	}

	public static TeamChatHandler getTeamChat() {
		return teamchat;
	}

	public static MySQLConnect getMySQL() {
		return mysql;
	}

	public static Main getInstance() {
		return instance;
	}
	
}
