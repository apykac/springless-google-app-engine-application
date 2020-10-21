package com.ticket_pipeline.simple_utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtils {
    private StringUtils() {
    }

    public static String left(String s, int limit) {
        if (isEmpty(s) || s.length() <= limit) {
            return s;
        }
        return s.substring(0, limit);
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean hasText(CharSequence str) {
        return (str != null && str.length() > 0 && containsText(str));
    }

    public static boolean hasText(String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String exceptionToString(Throwable t) {
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
