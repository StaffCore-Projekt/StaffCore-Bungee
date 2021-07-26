package de.lacodev.staffbungee.utils;

import java.util.HashMap;

import net.md_5.bungee.api.connection.PendingConnection;

public class VersionDetector {

	private static HashMap<Integer,String> versions = new HashMap<>();
	
	public static String getClientProtocol(PendingConnection connection) {
		if(versions.containsKey(connection.getVersion())) {
			return versions.get(connection.getVersion());
		} else {
			return "Unknown Version";
		}
	}
	
	public static void setup() {
		versions.put(3, "1.7 - 1.7.1");
		versions.put(4, "1.7.2 - 1.7.5");
		versions.put(5, "1.7.6 - 1.7.10");
		versions.put(47, "1.8 - 1.8.9");
		versions.put(107, "1.9");
		versions.put(108, "1.9.1");
		versions.put(109, "1.9.2");
		versions.put(110, "1.9.3 - 1.9.4");
		versions.put(210, "1.10 - 1.10.2");
		versions.put(315, "1.11");
		versions.put(316, "1.11.1 - 1.11.2");
		versions.put(335, "1.12");
		versions.put(338, "1.12.1");
		versions.put(340, "1.12.2");
		versions.put(393, "1.13");
		versions.put(401, "1.13.1");
		versions.put(404, "1.13.2");
		versions.put(477, "1.14");
		versions.put(480, "1.14.1");
		versions.put(485, "1.14.2");
		versions.put(490, "1.14.3");
		versions.put(498, "1.14.4");
		versions.put(573, "1.15");
		versions.put(575, "1.15.1");
		versions.put(578, "1.15.2");
		versions.put(735, "1.16");
		versions.put(736, "1.16.1");
		versions.put(751, "1.16.2");
		versions.put(753, "1.16.3");
		versions.put(754, "1.16.4 - 1.16.5");
		versions.put(755, "1.17");
	}
	
}
