package com.roundrobin_assignment.ticketpipeline.util;

import java.util.Collection;
import java.util.Map;

public class CollectionUtils {
    private CollectionUtils() {
    }

    public static boolean notEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean notEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}
