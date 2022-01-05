package de.lacodev.staffbungee.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import de.lacodev.staffbungee.Main;
import de.lacodev.staffbungee.managers.PlayerManager;
import de.lacodev.staffbungee.objects.Session;
import de.lacodev.staffbungee.utils.StringGenerator;
import de.lacodev.staffbungee.utils.VersionDetector;
import net.md_5.bungee.BungeeCord;

public class SessionsHandler {
	
	public SessionsHandler() {
		super();
	}
	
	private static HashMap<String, Session> sessions = new HashMap<>();
	
	public void startSession(String uuid) {
		String uniqueid = "#SC21" + StringGenerator.getRandomString(25);
		
		String virtual = BungeeCord.getInstance().getPlayer(UUID.fromString(uuid)).getPendingConnection().getVirtualHost().getHostString() + ":" + BungeeCord.getInstance().getPlayer(UUID.fromString(uuid)).getPendingConnection().getVirtualHost().getPort();
		
		Main.getMySQL().update("INSERT INTO StaffCore_sessionsdb(SESSION_ID,UUID,SESSION_START,SESSION_END,IP_ADDRESS,MC_VERSION,VIRTUAL_HOST,FINISHED) "
				+ "VALUES ('"+ uniqueid +"','"+ uuid +"','"+ System.currentTimeMillis() +"','0','"+ PlayerManager.getLastKnownIp(uuid) +"',"
						+ "'"+ VersionDetector.getClientProtocol(BungeeCord.getInstance().getPlayer(UUID.fromString(uuid)).getPendingConnection()) +"','"+ virtual +"','0')");
		
		sessions.put(uuid, new Session(uniqueid, System.currentTimeMillis(), 0));
	}
	
	public void stopSession(String uuid) {
		Session session = sessions.get(uuid);
		
		Main.getMySQL().update("UPDATE StaffCore_sessionsdb SET SESSION_END = '"+ System.currentTimeMillis() +"',CLIENT = '"+ PlayerManager.getBrand(BungeeCord.getInstance().getPlayer(UUID.fromString(uuid)).getName()) +"',FINISHED = '1' WHERE SESSION_ID = '"+ session.getUniqueid() +"'");
		
		sessions.remove(uuid);
	}
	
	public Session get(String sessionid) throws SQLException {
		
		Session session = new Session(sessionid, 0, 0, sessionid, sessionid, sessionid, sessionid);
		
		if(Main.getMySQL().isConnected()) {
			ResultSet rs = Main.getMySQL().query("SELECT * FROM StaffCore_sessionsdb WHERE SESSION_ID = '"+ sessionid +"'");
			
			if(rs.next()) {
				return new Session(sessionid, rs.getLong("SESSION_START"), rs.getLong("SESSION_END"), rs.getString("MC_VERSION"), rs.getString("IP_ADDRESS"), rs.getString("UUID"), rs.getString("VIRTUAL_HOST"));
			} else {
				return session;
			}
		}
		return session;
		
	}

}
