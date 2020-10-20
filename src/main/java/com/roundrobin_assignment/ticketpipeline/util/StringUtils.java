package com.roundrobin_assignment.ticketpipeline.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.roundrobin_assignment.ticketpipeline.config.Context;

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

    public static String toJson(Object o) {
        if (o == null) {
            return null;
        } else {
            try {
                return Context.getObjectMapper().writeValueAsString(o);
            } catch (JsonProcessingException e) {
                return String.valueOf(o);
            }
        }
    }
}
