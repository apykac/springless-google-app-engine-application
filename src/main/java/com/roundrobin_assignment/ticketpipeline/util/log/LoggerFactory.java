package com.roundrobin_assignment.ticketpipeline.util.log;

public class LoggerFactory {
    private static LogLevel logLevel = LogLevel.TRACE;

    private LoggerFactory() {
    }

    public static void setLogLevel(LogLevel logLevel) {
        LoggerFactory.logLevel = logLevel == null ? LoggerFactory.logLevel : logLevel;
    }

    public static boolean isEnabled(LogLevel logLevel) {
        return LoggerFactory.logLevel.isEnabled(logLevel);
    }

    public static Logger getLogger(Class<?> clazz) {
        return new DefaultImpl(org.slf4j.LoggerFactory.getLogger(clazz));
    }

    public static Logger getLogger(String loggerName) {
        return new DefaultImpl(org.slf4j.LoggerFactory.getLogger(loggerName));
    }
}
