package com.ticket_pipeline.simple_utils.log;

import com.ticket_pipeline.simple_utils.Environment;

public class LoggerFactory {
    private static LogLevel logLevel = Environment.getProp("log.logLevel", LogLevel.TRACE, LogLevel.class);

    private LoggerFactory() {
    }

    public static LogLevel getLogLevel() {
        return logLevel;
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
