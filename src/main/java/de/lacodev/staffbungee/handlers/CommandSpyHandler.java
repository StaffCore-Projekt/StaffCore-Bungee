package de.lacodev.staffbungee.handlers;

import java.util.HashMap;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandSpyHandler {

    public HashMap<ProxiedPlayer, String> cmdSpy = new HashMap<>();

    public CommandSpyHandler() {
        super();
    }

    public boolean isSpying(ProxiedPlayer player) {
        return cmdSpy.containsKey(player);
    }

    public String getSpyingServer(ProxiedPlayer player) {
        if (isSpying(player)) {
            return cmdSpy.get(player);
        } else {
            return null;
        }
    }

    public void startSpying(ProxiedPlayer player) {
        cmdSpy.put(player, "Global");
    }

    public void startSpying(ProxiedPlayer player, String subserver) {
        cmdSpy.put(player, subserver);
    }

    public void stopSpying(ProxiedPlayer player) {
        cmdSpy.remove(player);
    }

}
