package de.lacodev.staffbungee.handlers;

import de.lacodev.staffbungee.Main;

public class StaffCoreInformationHandler {

	public static void saveInfo() {
		Main.getMySQL().update("DELETE FROM StaffCore_infodb");
		
		Main.getMySQL().update("INSERT INTO StaffCore_infodb(TYPE,VALUE) VALUES ('LAST_USED_VERSION','"+ Main.getInstance().getDescription().getVersion() +"')");
		Main.getMySQL().update("INSERT INTO StaffCore_infodb(TYPE,VALUE) VALUES ('LAST_STARTUP','"+ System.currentTimeMillis() +"')");
	}
	
}
