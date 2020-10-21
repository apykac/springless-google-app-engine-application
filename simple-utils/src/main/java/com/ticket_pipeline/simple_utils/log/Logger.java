package com.ticket_pipeline.simple_utils.log;

import java.util.function.Supplier;

public interface Logger {
    void trace(String message, Supplier<?>... suppliers);

    void warn(String message, Supplier<?>... suppliers);

    void info(String message, Supplier<?>... suppliers);

    void debug(String message, Supplier<?>... suppliers);

    void error(String message, Supplier<?>... suppliers);
}
