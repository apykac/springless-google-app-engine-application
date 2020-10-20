package com.roundrobin_assignment.ticketpipeline.jdbc;

import com.roundrobin_assignment.ticketpipeline.dao.RowMapper;

import java.util.List;

public interface JdbcOperations {
    <T> List<T> query(String sql, RowMapper<T> rowMapper);

    int update(String sql, Object... args);
}
