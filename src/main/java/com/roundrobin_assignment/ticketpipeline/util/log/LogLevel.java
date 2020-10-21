package com.roundrobin_assignment.ticketpipeline.util.log;

public class LogLevel implements Comparable<LogLevel> {
    public static final LogLevel OFF = new LogLevel(0, "OFF");
    public static final LogLevel ERROR = new LogLevel(1, "ERROR");
    public static final LogLevel WARN = new LogLevel(2, "WARN");
    public static final LogLevel INFO = new LogLevel(3, "INFO");
    public static final LogLevel DEBUG = new LogLevel(4, "DEBUG");
    public static final LogLevel TRACE = new LogLevel(5, "TRACE");

    private final int width;
    private final String name;

    public static LogLevel getLogLevel(String name) {
        if (name == null) {
            return null;
        }

        switch (name.toLowerCase()) {
            case "off":
                return OFF;
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

    public String getName() {
        return name;
    }

    private LogLevel(int width, String name) {
        this.width = width;
        this.name = name;
    }

    public boolean isEnabled(LogLevel logLevel) {
        return this.compareTo(logLevel) >= 0;
    }

    @Override
    public int compareTo(LogLevel o) {
        return width - o.width;
    }
}
