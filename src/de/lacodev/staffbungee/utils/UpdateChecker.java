package de.lacodev.staffbungee.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

import de.lacodev.staffbungee.Main;

public class UpdateChecker {
	
	private Main instance;
	private Integer resourceId;
	
	public UpdateChecker(Main instance, Integer resourceId) {
		this.instance = instance;
		this.resourceId = resourceId;
	}
	
	public void getLatestVersion(Consumer<String> consumer) {
		
		try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
				Scanner scanner = new Scanner(inputStream)) {
			if(scanner.hasNext()) {
				consumer.accept(scanner.next());
			}
		} catch(IOException ex) {
			this.instance.getLogger().info("Updatechecker couldn't find any updatelogs");
		}
		
	}

}
