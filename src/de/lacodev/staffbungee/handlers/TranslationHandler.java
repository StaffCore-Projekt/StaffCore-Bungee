package de.lacodev.staffbungee.handlers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.lacodev.staffbungee.Main;
import net.md_5.bungee.api.chat.TextComponent;

public class TranslationHandler {
	
	// Handler for translate.lacodev.de Services
	
	private HashMap<String, String> fallback = new HashMap<>();
	private HashMap<String, String> language = new HashMap<>();
	private HashMap<String, String> custom = new HashMap<>();
	private ArrayList<String> keys = new ArrayList<>();
	
	public TranslationHandler() {
		super();
	}
	
	public void init() {
		
		fallback.clear();
		
		try {
			JsonObject message_keys = readJsonFromUrl("https://www.lacodev.de/services/api/v1/keys").getAsJsonObject();
			int totalKeys = message_keys.size();
			
			for(int i = 1; i <= totalKeys; i++) {
				
				JsonObject data = (JsonObject) message_keys.getAsJsonObject(String.valueOf(i));
				
				keys.add(data.get("key").getAsString());
			}
			
			JsonObject translation = readJsonFromUrl("https://www.lacodev.de/services/api/v1/all/lang/us").getAsJsonObject();
			
			for(String key : keys) {
				
				try {
					
					JsonObject msg = (JsonObject) translation.getAsJsonObject(key);
					
					fallback.put(key, msg.get("translation").getAsString());
					
				} catch (NullPointerException e) {
					
				}
			}
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §aSuccessfully §8cached §7" + fallback.size() + " §aFallback-Translations§8(§7US§8)"));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
		} catch(NullPointerException e) {
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §7Translator is not reachable :("));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §cServices might be in maintenance! Please be patient"));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
		}
	}
	
	public void fetch(String lang) {
		language.clear();
		
		try {
			JsonObject translation = readJsonFromUrl("https://www.lacodev.de/services/api/v1/all/lang/"+ lang).getAsJsonObject();
			
			for(String key : keys) {
				
				try {
					JsonObject msg = (JsonObject) translation.getAsJsonObject(key);
					
					language.put(key, msg.get("translation").getAsString());
				} catch(NullPointerException e) {
					
				}
			}
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §aSuccessfully §8cached §7" + language.size() + " §aRequest-Translations§8(§7"+ lang.toUpperCase() +"§8)"));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
		} catch(NullPointerException e) {
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §7Translator is not reachable :("));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §cServices might be in maintenance! Please be patient"));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
		}
	}
	
	public void fetchCustom(String restapikey) {
		custom.clear();
		
		try {
			JsonObject translation = readJsonFromUrl("https://www.lacodev.de/services/api/v1/restricted/key/"+ restapikey).getAsJsonObject();
			
			for(String key : keys) {
				
				try {
					JsonObject msg = (JsonObject) translation.getAsJsonObject(key);
					
					custom.put(key, msg.get("text_message").getAsString());
				} catch(NullPointerException e) {
					
				}
			}
			
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §aSuccessfully §8cached §7" + custom.size() + " §aMessages§8(§7CUSTOM§8)"));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
		} catch (ClassCastException | NullPointerException e) {
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §7Translator denied your request!"));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §cServices might be in maintenance! Or you entered the wrong API-Key"));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §cTry adding messages ;) If you have, please contact our support!"));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(""));
		}
		
	}
	
	public String getTranslation(String key) {
		
		if(custom.containsKey(key)) {
			return custom.get(key);
		} else if(!language.containsKey(key)) {
			return fallback.get(key);
		} else {
			return language.get(key);
		}
		
	}
	
	private JsonElement readJsonFromUrl(String url) {
		JsonParser parser = new JsonParser();
		
		JsonElement jsonElement;
		try {
			jsonElement = (JsonObject) parser.parse(new InputStreamReader(new URL(url).openStream(), Charset.forName("UTF-8")));
			return jsonElement;
		} catch (JsonIOException | JsonSyntaxException | IOException e) {
			
		}
		return null;
	}

}
