package de.lacodev.staffbungee.utils;

import java.util.Random;

public class StringGenerator {
	
	static String strAllowedCharacters = 
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	static Random random = new Random();

    public static String getRandomString(int length) {
        
        StringBuilder sbRandomString = new StringBuilder(length);
        
        for(int i = 0 ; i < length; i++){
            
            //get random integer between 0 and string length
            int randomInt = random.nextInt(strAllowedCharacters.length());
            
            //get char from randomInt index from string and append in StringBuilder
            sbRandomString.append( strAllowedCharacters.charAt(randomInt) );
        }
        
        return sbRandomString.toString();
        
    }
    
    public static String getMySQLFriendly(String message) {  	
    	return message.replace("'", "&#180;");
    }
    
	private final static String[] incorrectChars = new String[] { 
		      "\002", "\003", "\004", "\005", "\006", "\007", "\t", "\020", "\021", "\022", 
		      "\024", "\025", "\026", "\027", "\031", "\017", "\032", "\016", "\013", "\033", 
		      "\f", "\r", "\b" };
		  
	  public static String readChannelMessage(String string) {
		    byte b;
		    int i;
		    String[] arrayOfString;
		    for (i = (arrayOfString = incorrectChars).length, b = 0; b < i; ) {
		      String character = arrayOfString[b];
		      string = string.replace(character, " ");
		      b++;
		    } 
		    return string.replace("\t", " ").replace("  ", " ").replace("\n", " ").trim();
		  }
		
	
}
