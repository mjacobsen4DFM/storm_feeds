package com.transactstorm.storm_feeds;

import java.util.regex.Pattern;

public class BasicUtil {
	public static String CleanString(String string) {
		String cleanString = string;
		cleanString = cleanString.replaceAll("\u00BB", ">>");
		cleanString = cleanString.replaceAll("\u00BC", " 1/4");
		cleanString = cleanString.replaceAll("\u00BD", " 1/2");
		cleanString = cleanString.replaceAll("\u00BE", " 3/4");
		cleanString = cleanString.replaceAll("\u2013", "-");
		cleanString = cleanString.replaceAll("\u2014", "-");
		cleanString = cleanString.replaceAll("\u2018", "'");
		cleanString = cleanString.replaceAll("\u2019", "'");
		cleanString = cleanString.replaceAll("\u201A", "'");
		cleanString = cleanString.replaceAll("\u201B", "'");
		cleanString = cleanString.replaceAll("\u201C", "\"");
		cleanString = cleanString.replaceAll("\u201D", "\"");
		cleanString = cleanString.replaceAll("\u201E", "\"");
		cleanString = cleanString.replaceAll("\u201F", "\"");
		cleanString = cleanString.replaceAll("\u2026", "...");
		return cleanString;
	}
	
	 public static String Hyphenate(String input)
	 {
		// Replace invalid characters with empty strings.
		String hyphenated  = input;
		hyphenated = hyphenated.replaceAll("[\\s]", "-");
		hyphenated = hyphenated.replaceAll("[^a-zA-Z0-9-\\._]", "");
		hyphenated = hyphenated.replaceAll("\\-{2,}", "-");
		Pattern pattern = Pattern.compile("\\.([a-zA-Z]{2,5}[0-9]*)\\.");
		while (pattern.matcher(hyphenated).matches())
		{
			hyphenated = hyphenated.replaceAll("\\.([a-zA-Z]{2,5}[0-9]*)\\.", ".$1_.");
		}
		return hyphenated;
	 }
	
}
