package com.roundrobin_assignment.ticketpipeline.config.context;

import com.roundrobin_assignment.ticketpipeline.exception.InitContextRuntimeException;
import com.roundrobin_assignment.ticketpipeline.util.ClassPathResourcesUtil;
import com.roundrobin_assignment.ticketpipeline.util.log.LogLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("unchecked")
public class Environment {
    static {
        try {
            Properties properties = new Properties();
            properties.load(ClassPathResourcesUtil.getFileInputStream("application.properties"));
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                System.setProperty(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
        } catch (Exception e) {
            throw new InitContextRuntimeException("Error during init system params");
        }
    }

    private Environment() {
    }

//    public static <T> T getProperty(String propertyName, T defaultValue, Class<T> clazz) {
//        T propertyValue = getProperty(propertyName, clazz);
//        return propertyValue == null ? defaultValue : propertyValue;
//    }
//
//    public static <T> T getProperty(String propertyName, Class<T> clazz) {
//        String propertyValue = getProperty(propertyName);
//        if (clazz == LogLevel.class) {
//            return (T) LogLevel.getLogLevel(propertyValue);
//        } else {
//            throw new ClassCastException("Can't get property by class: " + (clazz == null ? null : clazz.getName()));
//        }
//    }
//
//    public static int getProperty(String propertyName, int defaultValue) {
//        String propertyValue = getProperty(propertyName);
//        return propertyValue == null ? defaultValue : Integer.parseInt(propertyValue);
//    }
//
//    public static String getProperty(String propertyName, String defaultValue) {
//        String propertyValue = getProperty(propertyName);
//        return propertyValue == null ? defaultValue : propertyValue;
//    }
//
//    public static String getProperty(String propertyName) {
//        return System.getProperty(propertyName);
//    }
//
//    public static <T> T getEnv(String propertyName, T defaultValue, Class<T> clazz) {
//        T propertyValue = getEnv(propertyName, clazz);
//        return propertyValue == null ? defaultValue : propertyValue;
//    }
//
//    public static <T> T getEnv(String propertyName, Class<T> clazz) {
//        String propertyValue = getEnv(propertyName);
//        if (clazz == LogLevel.class) {
//            return (T) LogLevel.getLogLevel(propertyValue);
//        } else {
//            throw new ClassCastException("Can't get property by class: " + (clazz == null ? null : clazz.getName()));
//        }
//    }
//
//    public static String getEnv(String envName, String defaultValue) {
//        String envValue = getEnv(envName);
//        return envValue == null ? defaultValue : envValue;
//    }
//
//    public static String getEnv(String envName) {
//        return System.getenv(envName);
//    }

    public static Map<String, String> getEnvMap() {
        return System.getenv();
    }

    public static Map<String, String> getPropertiesMap() {
        Properties properties = System.getProperties();
        Map<String, String> result = new HashMap<>((int) Math.min(properties.size() / 0.75F, 16));
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            result.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        return result;
    }

    public static <T> T getProp(String propertyName, T defaultValue, Class<T> clazz) {
        T propertyValue = getProp(propertyName, clazz);
        return propertyValue == null ? defaultValue : propertyValue;
    }

    public static <T> T getProp(String propertyName, Class<T> clazz) {
        String value = getFromInnerStorage(propertyName);
        return mapToClass(value, clazz);
    }

    public static String getFromInnerStorage(String propertyName) {
        String envValue = System.getenv(propertyName);
        if (envValue == null) {
            envValue = System.getProperty(propertyName);
        }
        return envValue;
    }

    private static <T> T mapToClass(String value, Class<T> clazz) {
        if (value == null) {
            return null;
        } else if (clazz == String.class) {
            return (T) value;
        } else if (clazz == LogLevel.class) {
            return (T) LogLevel.getLogLevel(value);
        } else if (clazz == Integer.class) {
            return (T) Integer.valueOf(value);
        } else {
            throw new ClassCastException("Can't get property by class: " + (clazz == null ? null : clazz.getName()));
        }
    }
}
