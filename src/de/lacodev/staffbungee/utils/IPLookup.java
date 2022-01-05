package de.lacodev.staffbungee.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class IPLookup {
	
	private static String url = "http://ip-api.com/json/";
	private static String fields = "?fields=status,message,country,countryCode,region,regionName,city,zip,lat,lon,timezone,isp,org,as,proxy,query";
	
	private static HashMap<String, JsonObject> iplog = new HashMap<>();
	
	public static void logIp(String ip) {
		iplog.put(ip, readJsonFromUrl(url + ip + fields).getAsJsonObject());
	}
	
	public static String getCountry(String ip) {
		try {
			if(iplog.get(ip).get("status").getAsString().matches("success")) {
				return iplog.get(ip).get("countryCode").getAsString();
			} else {
				return "Request failed";
			}
		} catch (NullPointerException e) {
			System.err.println("StaffCore » Region Detection failed");
			return "Request failed";
		}
	}
	
	public static String getRegion(String ip) {
		try {
			if(iplog.get(ip).get("status").getAsString().matches("success")) {
				return iplog.get(ip).get("regionName").getAsString();
			} else {
				return "Request failed";
			}
		} catch (NullPointerException e) {
			System.err.println("StaffCore » Region Detection failed");
			return "Request failed";
		}
	}
	
	public static boolean isUsingProxy(String ip) {
		try {
			if(iplog.get(ip).get("status").getAsString().matches("success")) {
				return iplog.get(ip).get("proxy").getAsBoolean();
			} else {
				return false;
			}
		} catch (NullPointerException e) {
			System.err.println("StaffCore » VPN Detection failed");
			return false;
		}
	}
	
	@SuppressWarnings("deprecation")
	private static JsonElement readJsonFromUrl(String url) {
		JsonParser parser = new JsonParser();
		
		JsonElement jsonElement;
		try {
			jsonElement = parser.parse(new InputStreamReader(new URL(url).openStream(), Charset.forName("UTF-8")));
			return jsonElement;
		} catch (JsonIOException | JsonSyntaxException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
