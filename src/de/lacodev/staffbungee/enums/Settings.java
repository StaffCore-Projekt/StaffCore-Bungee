package de.lacodev.staffbungee.enums;

public enum Settings {

	ADVERTISMENT_ENABLE("TRUE"),
	MAINTENANCE_ENABLE("FALSE"),
	MAINTENANCE_TEXT_LINE1("&cThe server is currently in Maintenance and will be back shortly!"),
	MAINTENANCE_TEXT_LINE2("&cServerCore by StaffCore-Bungee"),
	MAINTENANCE_VERSIONTEXT("&c&lMAINTENANCE"),
	MOTD_ENABLE("TRUE"),
	MOTD_TEXT_LINE1("&cYourServer.NET &8| &7Minecraft Network! &8[&c1.8-1.17&8]"),
	MOTD_TEXT_LINE2("&cNew &8> &7Staff Management System"),
	MOTD_MAXPLAYER_COUNT("100"),
	MOTD_FAKEPLAYERS_ENABLE("FALSE"),
	MOTD_FAKEPLAYERS_COUNT("23"),
	REPORT_ANTISPAM_COOLDOWN_SECONDS("20");
	
	private String standard;
	
	private Settings(String standard) {
		this.standard = standard;
	}
	
	public String getStandard() {
		return standard;
	}
	
}
