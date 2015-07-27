package org.jboss.pnc.pvt.util;

import java.lang.reflect.Field;
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
			String[] found = m.group(1).split("\\.");
			Object o = objects.get(found[0]);
			Field f = null;
			try {
				f = o.getClass().getField(found[1]);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			String newVal = null;
			try {
				newVal = f.get(o).toString();
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			result = result.replaceFirst(regex, newVal);
		}
		return result;
	}
}