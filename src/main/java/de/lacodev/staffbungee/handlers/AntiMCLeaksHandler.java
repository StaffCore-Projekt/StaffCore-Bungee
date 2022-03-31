package de.lacodev.staffbungee.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.lacodev.staffbungee.Main;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import net.md_5.bungee.api.chat.TextComponent;

public class AntiMCLeaksHandler {

    // Handler for anti-mcleaks.lacodev.de Services

    public AntiMCLeaksHandler() {
        super();
    }

    private ArrayList<String> cache = new ArrayList<>();
    private long nextRefresh = System.currentTimeMillis() + (1000 * 60 * 10);

    public void cacheAccounts() {

        try {
            cache.clear();

            int totalAccounts = readJsonFromUrl("https://anti-mcleaks.lacodev.de/api/v1/total/").getAsJsonObject().get("total").getAsInt();

            JsonObject allAccountsData = readJsonFromUrl("https://anti-mcleaks.lacodev.de/api/v1/accounts/").getAsJsonObject();

            for (int i = 1; i <= totalAccounts; i++) {
                JsonObject data = allAccountsData.get(String.valueOf(i)).getAsJsonObject();

                cache.add(data.get("uuid").getAsString());
            }


            Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
            Main.getInstance().getProxy().getConsole()
                .sendMessage(new TextComponent("§cSystem §8» §aSuccessfully §8cached §a" + totalAccounts + " Accounts"));
            Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
        } catch (NullPointerException e) {
            Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
            Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §7Anti-MCLeaks is not reachable :("));
            Main.getInstance().getProxy().getConsole()
                .sendMessage(new TextComponent("§cSystem §8» §cServices might be in maintenance! Please be patient"));
            Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
        }

    }

    public boolean isAccountCached(String uuid) {
        if (Main.getInstance().getConfig().getBoolean("MCLeaks-Blocker.Cache-Updater.Enable")) {
            if (System.currentTimeMillis() >= nextRefresh) {
                cacheAccounts();
                nextRefresh = System.currentTimeMillis() +
                    (1000 * 60 * Main.getInstance().getConfig().getInt("MCLeaks-Blocker.Cache-Updater.Period-In-Minutes"));
            }
        }
        return cache.contains(uuid);
    }

    public ArrayList<String> getAccountCache() {
        return cache;
    }

    @SuppressWarnings("deprecation")
    private JsonElement readJsonFromUrl(String url) {
        JsonParser parser = new JsonParser();

        try {

            JsonElement jsonElement = (JsonObject) parser.parse(new InputStreamReader(new URL(url).openStream(), Charset.forName("UTF-8")));

            return jsonElement;
        } catch (IOException | ClassCastException e) {

        }
        return null;
    }

}
