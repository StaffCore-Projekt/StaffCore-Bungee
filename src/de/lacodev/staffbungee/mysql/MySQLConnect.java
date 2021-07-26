package de.lacodev.staffbungee.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.lacodev.staffbungee.Main;
import net.md_5.bungee.api.chat.TextComponent;

public class MySQLConnect {

	public static String HOST;
	public static String PORT;
	public static String DATABASE;
	public static String USER;
	public static String PASSWORD;
	private Connection con;
	
    private static String mysql = "§cSystem §8(§7MySQL§8) §8- ";
	  
	public MySQLConnect(String host, String database, String user, String password) {
		
		HOST = host;
	    DATABASE = database;
	    USER = user;
	    PASSWORD = password;
	    
	    connect();
	}

	public void connect() {
		
		try {
			this.con = DriverManager.getConnection("jdbc:mysql://" + HOST + ":"+ PORT +"/" + DATABASE + "?autoReconnect=true&useSSL=false", USER, PASSWORD);
			
			updateTables("ALTER TABLE StaffCore_sessionsdb ADD VIRTUAL_HOST VARCHAR(255)");
			
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(mysql + "§aSuccessfully §7connected to database §a" + DATABASE));
		} catch (SQLException | NullPointerException e) {
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(mysql + "§cFailed to §7connected to database §c" + DATABASE));
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(mysql + "§cErrorCode: §7" + ((SQLException) e).getSQLState() + ":" + ((SQLException) e).getErrorCode()));
		}
		
	}
	
	public void close() {
		
		try {
			if (this.con != null) {
		        this.con.close();
		    }
		} catch(SQLException e) {
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(mysql + "§cErrorCode: §7" + e.getSQLState() + ":" + e.getErrorCode()));
		}
		
	}
	
	public boolean isConnected() {
		return con == null ? false : true;
	}
	
	public void update(String qry) {
		try {
	      Statement st = this.con.createStatement();
	      st.executeUpdate(qry);
	      st.close();
	    } catch (SQLException | NullPointerException e) {
	      connect();
	      System.err.println(e);
	      Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(mysql + "§cErrorCode: §7" + ((SQLException) e).getSQLState() + ":" + ((SQLException) e).getErrorCode()));
	    }
	}
	
	public void updateTables(String qry) {
		try {
	      Statement st = this.con.createStatement();
	      st.executeUpdate(qry);
	      st.close();
	    } catch (SQLException e) {
	      
	    }
	}
	
	public ResultSet query(String qry) {
		ResultSet rs = null;
		try {
			Statement st = this.con.createStatement();
			rs = st.executeQuery(qry);
		} catch (SQLException | NullPointerException e) {
			connect();
			System.err.println(e);
			Main.getInstance().getProxy().getConsole().sendMessage(new TextComponent(mysql + "§cErrorCode: §7" + ((SQLException) e).getSQLState() + ":" + ((SQLException) e).getErrorCode()));
		}
		return rs;
	}
}
