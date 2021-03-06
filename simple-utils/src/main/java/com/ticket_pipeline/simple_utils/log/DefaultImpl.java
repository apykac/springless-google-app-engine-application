package com.ticket_pipeline.simple_utils.log;

import com.ticket_pipeline.simple_utils.StringUtils;

import java.util.function.Supplier;

public class DefaultImpl implements Logger {
    private static final Object[] EMPTY = new Object[0];
    private final org.slf4j.Logger logger;

    public DefaultImpl(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void trace(String message, Supplier<?>... suppliers) {
        if (LoggerFactory.isEnabled(LogLevel.TRACE)) {
            Object[] objects = suppliersToObject(suppliers);
            if (objects.length == 0) {
                logger.trace(message);
            } else {
                logger.trace(message, objects);
            }
        }
    }

    @Override
    public void warn(String message, Supplier<?>... suppliers) {
        if (LoggerFactory.isEnabled(LogLevel.WARN)) {
            Object[] objects = suppliersToObject(suppliers);
            if (objects.length == 0) {
                logger.warn(message);
            } else {
                logger.warn(message, objects);
            }
        }
    }

    @Override
    public void info(String message, Supplier<?>... suppliers) {
        if (LoggerFactory.isEnabled(LogLevel.INFO)) {
            Object[] objects = suppliersToObject(suppliers);
            if (objects.length == 0) {
                logger.info(message);
            } else {
                logger.info(message, objects);
            }
        }
    }

    @Override
    public void debug(String message, Supplier<?>... suppliers) {
        if (LoggerFactory.isEnabled(LogLevel.DEBUG)) {
            Object[] objects = suppliersToObject(suppliers);
            if (objects.length == 0) {
                logger.debug(message);
            } else {
                logger.debug(message, objects);
            }
        }
    }

    @Override
    public void error(String message, Supplier<?>... suppliers) {
        if (LoggerFactory.isEnabled(LogLevel.ERROR)) {
            Object[] objects = suppliersToObject(suppliers);
            if (objects.length == 0) {
                logger.error(message);
            } else {
                logger.error(message, objects);
            }
        }
    }

    private Object[] suppliersToObject(Supplier<?>[] suppliers) {
        if (suppliers == null || suppliers.length == 0) {
            return EMPTY;
        }
        Object[] objects = new Object[suppliers.length];
        int index = 0;
        for (Supplier<?> supplier : suppliers) {
            Object o = supplier.get();
            if (o instanceof Throwable) {
                objects[index++] = StringUtils.exceptionToString((Throwable) o);
            } else {
                objects[index++] = o;
            }
        }
        return objects;
    }
}
