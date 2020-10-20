package com.roundrobin_assignment.ticketpipeline.util.log;

public class LogLevel implements Comparable<LogLevel> {
    public static final LogLevel ERROR = new LogLevel(1);
    public static final LogLevel WARN = new LogLevel(2);
    public static final LogLevel INFO = new LogLevel(3);
    public static final LogLevel DEBUG = new LogLevel(4);
    public static final LogLevel TRACE = new LogLevel(5);

    private final int width;

    public static LogLevel getLogLevel(String name) {
        if (name == null) {
            return null;
        }

        switch (name.toLowerCase()) {
            case "error":
                return ERROR;
            case "warn":
                return WARN;
            case "info":
                return INFO;
            case "debug":
                return DEBUG;
            case "trace":
                return TRACE;
            default:
                return null;
        }
    }

    public LogLevel(int width) {
        this.width = width;
    }

    public boolean isEnabled(LogLevel logLevel) {
        return this.compareTo(logLevel) >= 0;
    }

    @Override
    public int compareTo(LogLevel o) {
        return width - o.width;
    }
}
