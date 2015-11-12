package com.transactstorm.storm_feeds;

public class StringUtil {
	public static String filter(String str) {
	    StringBuilder filtered = new StringBuilder(str.length());
	    for (int i = 0; i < str.length(); i++) {
	        char current = str.charAt(i);
	        if (current >= 0x20 && current <= 0x7e) {
	            filtered.append(current);
	        }
	        else{
	        	switch (current){
	        		case 0x171: filtered.append("1/2"); 
	        	}
	        }
	    }

	    return filtered.toString();
	}
}
