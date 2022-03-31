package de.lacodev.staffbungee.enums;

public enum Settings {

    ADVERTISMENT_ENABLE("TRUE", "BOOLEAN"),
    MAINTENANCE_ENABLE("FALSE", "BOOLEAN"),
    MAINTENANCE_TEXT_LINE1("&cThe server is currently in Maintenance!", "TEXT"),
    MAINTENANCE_TEXT_LINE2("&cServerCore by StaffCore-Bungee", "TEXT"),
    MAINTENANCE_VERSIONTEXT("&c&lMAINTENANCE", "TEXT"),
    MOTD_ENABLE("TRUE", "BOOLEAN"),
    MOTD_TEXT_LINE1("&cYourServer.NET &8| &7Minecraft Network! &8[&c1.8-1.18&8]", "TEXT"),
    MOTD_TEXT_LINE2("&cNew &8> &7Staff Management System", "TEXT"),
    MOTD_MAXPLAYER_COUNT("100", "NUMBER"),
    MOTD_FAKEPLAYERS_ENABLE("FALSE", "BOOLEAN"),
    MOTD_FAKEPLAYERS_COUNT("23", "NUMBER"),
    REPORT_ANTISPAM_COOLDOWN_SECONDS("20", "NUMBER");

    private String standard;
    private String expectedInput;

    private Settings(String standard, String expectedInput) {
        this.standard = standard;
        this.expectedInput = expectedInput;
    }

    public String getStandard() {
        return standard;
    }

    public String getExpectedInput() {
        return expectedInput;
    }
}
