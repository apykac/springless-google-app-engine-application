package com.roundrobin_assignment.ticketpipeline.jdbc.datacource;

import com.roundrobin_assignment.ticketpipeline.config.context.Environment;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {
    private final HikariDataSource hikaruDataSource;

    public DataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Environment.getProp("datasource.url", String.class));
        config.setUsername(Environment.getProp("datasource.username", String.class));
        config.setPassword(Environment.getProp("datasource.password", String.class));
        config.setMaximumPoolSize(Environment.getProp("datasource.maximum-pool-size", 10, Integer.class));
        config.setDriverClassName(Environment.getProp("datasource.driver-class-name", String.class));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikaruDataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return hikaruDataSource.getConnection();
    }
}
