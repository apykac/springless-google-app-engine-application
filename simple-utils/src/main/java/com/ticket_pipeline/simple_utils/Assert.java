package com.ticket_pipeline.simple_utils;

import java.util.function.Supplier;

public class Assert {
    private Assert() {
    }

    public static void notNull(Object o) {
        notNull(o, null);
    }

    public static void notNull(Object o, Supplier<String> message) {
        if (o == null) {
            throw new IllegalArgumentException(message == null ? "Object is null" : message.get());
        }
    }
}
