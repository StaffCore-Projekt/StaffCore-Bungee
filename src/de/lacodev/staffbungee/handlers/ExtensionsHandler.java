package de.lacodev.staffbungee.handlers;

import java.sql.ResultSet;
import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.enums.Extension;
import net.md_5.bungee.api.chat.TextComponent;

public class ExtensionsHandler {

	public ExtensionsHandler() {
		super();
		Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent("§cSystem §8» §8ExtensionManager §asuccessfully §8registered!"));
	}
	
	public boolean isHookSetup() {
		ResultSet rs = Main.getMySQL().querySilent("SELECT 1 FROM StaffCore_extensionsdb LIMIT 1");
		
		if(rs != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public String activeCount() {
		
		int count = 0;
		int total = Extension.values().length;
		
		for(Extension ex : Extension.values()) {
			if(ex.isActive()) {
				count++;
			}
		}
		return "§a" + count + " §8/ §7" + total;
	}
	
}
