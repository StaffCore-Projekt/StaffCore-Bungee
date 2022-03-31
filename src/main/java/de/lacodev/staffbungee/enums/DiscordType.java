package de.lacodev.staffbungee.enums;

public enum DiscordType {

    NETWORK_BAN("Network Ban"),
    NETWORK_MUTE("Network Mute"),
    NETWORK_KICK("Network Kick"),
    NETWORK_WARN("Network Warn"),
    REQUEST_PLAYERDATA("Playerdata Request"),
    PLAYER_REPORT("Player Report"),
    AUTO_REPORT("Automatic Report"),
    NETWORK_UNBAN("Network Unban"),
    NETWORK_UNMUTE("Network Unmute"),
    NETWORK_IPBAN("Network IP-Ban"),
    WATCHLIST_ALERT("WatchList Alert");

    String message;

    private DiscordType(String message) {
        this.message = message;
    }

    public String getTitle() {
        return message;
    }

}
