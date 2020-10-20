package com.roundrobin_assignment.ticketpipeline.util.log;

import java.util.function.Supplier;

public interface Logger {
    void trace(String message, Supplier<?>... suppliers);

    void warn(String message, Supplier<?>... suppliers);

    void info(String message, Supplier<?>... suppliers);

    void debug(String message, Supplier<?>... suppliers);

    void error(String message, Supplier<?>... suppliers);
}
