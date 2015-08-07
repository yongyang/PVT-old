package org.jboss.pnc.pvt.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormatter {
	
	private static final String fieldStart = "\\$\\{";
	private static final String fieldEnd = "\\}";
	
	private static final String regex = fieldStart + "([^}]+)" + fieldEnd;
	private static final Pattern pattern = Pattern.compile(regex);
	
	public static String format(String format, Map<String, Object> objects) {
		Matcher m = pattern.matcher(format);
		String result = format;
		while (m.find()) {
			//String[] key = m.group(1).split("\\.");
			String key = m.group(1).toString();
			result = result.replaceFirst(regex, Matcher.quoteReplacement((String) (objects.get(key)) ) );
		}
		return result;
	}
}