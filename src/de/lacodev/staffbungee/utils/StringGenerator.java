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
	
}
