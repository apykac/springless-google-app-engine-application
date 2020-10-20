package com.roundrobin_assignment.ticketpipeline.jdbc.datacource;

import com.roundrobin_assignment.ticketpipeline.config.context.Component;
import com.roundrobin_assignment.ticketpipeline.config.context.Constructor;
import com.roundrobin_assignment.ticketpipeline.config.context.Destroy;
import com.roundrobin_assignment.ticketpipeline.config.context.Environment;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DataSource {
    private final HikariDataSource hikaruDataSource;

    @Constructor
    public DataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Environment.getProp("datasource.url", String.class));
        config.setUsername(Environment.getProp("datasource.username", String.class));
        config.setPassword(Environment.getProp("datasource.password", String.class));
//        config.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
//        config.setUsername("postgres");
//        config.setPassword("1");
        config.setMaximumPoolSize(Environment.getProp("datasource.maximum-pool-size", 10, Integer.class));
        config.setDriverClassName(Environment.getProp("datasource.driver-class-name", String.class));
//        config.setDriverClassName("org.postgresql.Driver");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikaruDataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return hikaruDataSource.getConnection();
    }

    @Destroy(-1)
    public void destroy() {
        hikaruDataSource.close();
    }
}
