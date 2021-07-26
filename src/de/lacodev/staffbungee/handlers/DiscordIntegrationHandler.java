package de.lacodev.staffbungee.handlers;

import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gson.JsonObject;
import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.DiscordType;
import de.lacodev.staffbungee.utils.DiscordWebhook;
import de.lacodev.staffbungee.utils.DiscordWebhook.EmbedObject;

public class DiscordIntegrationHandler {

	String webhookUrl;
	String webhookId;
	String webhookToken;
	String webhookName;
	String webhookAvatar = "https://www.staffcore-bungee.net/assets/img/brand/favicon.png";
	String webhookTestUrl = "https://webhook.site/fd8f8570-cb7b-4331-9d72-acaaaf841f2f";
	JsonObject webhookResponse;
	
	public DiscordIntegrationHandler(String webhookurl) {

		webhookUrl = webhookurl;
		
	}
	
    public void sendMessageToWebHook(DiscordType type, String message, String targetuuid) throws Exception {

    	Main.getInstance().getProxy().getScheduler().runAsync(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				Date last_login = new Date(System.currentTimeMillis());
				SimpleDateFormat last_login_format = new SimpleDateFormat(Main.getInstance().getConfig().getString("General.Time-Format"));
				String loginLastDate = last_login_format.format(last_login);
				
				DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
			    webhook.setAvatarUrl(webhookAvatar);
			    webhook.setUsername(webhookName);
			    
			    EmbedObject obj = new EmbedObject();
			    
			    if(message.contains("\n")) {
			    	String[] msg = message.split("\n");
			    	
			    	for(String s : msg) {
			    		if(s.contains(">")) {
			    			String[] split = s.split(">");
				    		
				    		obj.addField(split[0], split[1], true);
			    		} else {
			    			obj.setDescription(s);
			    		}
			    	}
			    } else {
			    	if(message.contains(">")) {
		    			String[] split = message.split(">");
			    		
			    		obj.addField(split[0], split[1], true);
		    		} else {
		    			obj.setDescription(message);
		    		}
			    }
			    obj.setTitle(type.getTitle());
			    obj.setColor(Color.RED);
			    obj.setThumbnail("https://www.mc-heads.net/body/"+ targetuuid +".png");
			    obj.setFooter("StaffCore-Bungee © 2021 - " + loginLastDate, null);
			    obj.setAuthor("StaffCore-Bungee Hook", "https://www.staffcore-bungee.net", "https://www.staffcore-bungee.net/assets/img/brand/favicon.png");
			    obj.setUrl("https://www.staffcore-bungee.net");
			    
			    webhook.addEmbed(obj);
			    
			    try {
					webhook.execute();
				} catch (IOException e1) {
					e1.printStackTrace();
				} //Handle exception
			}
    		
    	});

    }
	
}
