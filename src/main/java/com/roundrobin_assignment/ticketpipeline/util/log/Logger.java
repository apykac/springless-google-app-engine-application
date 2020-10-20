package com.roundrobin_assignment.ticketpipeline.util.log;

import org.slf4j.Marker;

import java.util.function.Supplier;

public interface Logger extends org.slf4j.Logger {
    void trace(String message, Supplier<?>... suppliers);

    void warn(String message, Supplier<?>... suppliers);

    void info(String message, Supplier<?>... suppliers);

    void debug(String message, Supplier<?>... suppliers);

    void error(String message, Supplier<?>... suppliers);

    @Override
    default String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isTraceEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void trace(String msg) {
        trace(msg, (Supplier<?>[]) null);
    }

    @Override
    default void trace(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void trace(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void trace(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void trace(String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isTraceEnabled(Marker marker) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void trace(Marker marker, String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void trace(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void trace(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void trace(Marker marker, String format, Object... argArray) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void trace(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isDebugEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void debug(String msg) {
        debug(msg, (Supplier<?>[]) null);
    }

    @Override
    default void debug(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void debug(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void debug(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void debug(String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isDebugEnabled(Marker marker) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void debug(Marker marker, String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void debug(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void debug(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void debug(Marker marker, String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void debug(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isInfoEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void info(String msg) {
        info(msg, (Supplier<?>[]) null);
    }

    @Override
    default void info(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void info(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void info(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void info(String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isInfoEnabled(Marker marker) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void info(Marker marker, String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void info(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void info(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void info(Marker marker, String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void info(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isWarnEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void warn(String msg) {
        warn(msg, (Supplier<?>[]) null);
    }

    @Override
    default void warn(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void warn(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void warn(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void warn(String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isWarnEnabled(Marker marker) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void warn(Marker marker, String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void warn(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void warn(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void warn(Marker marker, String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void warn(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isErrorEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void error(String msg) {
        error(msg, (Supplier<?>[]) null);
    }

    @Override
    default void error(String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void error(String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void error(String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void error(String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isErrorEnabled(Marker marker) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void error(Marker marker, String msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void error(Marker marker, String format, Object arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void error(Marker marker, String format, Object arg1, Object arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void error(Marker marker, String format, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void error(Marker marker, String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }
}
