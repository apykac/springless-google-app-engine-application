package com.ticket_pipeline.simple_exchange.jdbc;

import com.ticket_pipeline.simple_exchange.exception.JdbcException;

import java.util.List;

public interface JdbcOperations {
    <T> List<T> query(String sql, RowMapper<T> rowMapper) throws JdbcException;

    int update(String sql, Object... args) throws JdbcException;
}
