package de.lacodev.staffbungee.enums;

public enum MigrationConfig {

    CONFIG_LITEBANS("LiteBans-Config"),
    CONFIG_ADVANCEDBAN("AdvancedBan-Config");

    String string;

    private MigrationConfig(String string) {
        this.string = string;
    }

    public String getConfigPrefix() {
        return string;
    }

}
