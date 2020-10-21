package com.ticket_pipeline.simple_exchange.jdbc.datacource;

import com.ticket_pipeline.simple_context.Component;
import com.ticket_pipeline.simple_context.Constructor;
import com.ticket_pipeline.simple_context.Destroy;
import com.ticket_pipeline.simple_utils.Environment;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DataSource {
    private HikariDataSource hikaruDataSource;
    private int poolSize;

    @Constructor
    public DataSource() {
        poolSize = Environment.getProp("datasource.maximum-pool-size", 4, Integer.class);
        hikaruDataSource = getHikariDataSource(poolSize);
    }

    public Connection getConnection() throws SQLException {
        return hikaruDataSource.getConnection();
    }

    public synchronized int resizeTreadPool(int newThreadPoolSize) {
        if (newThreadPoolSize != poolSize && newThreadPoolSize > 0) {
            destroy();
            hikaruDataSource = getHikariDataSource(newThreadPoolSize);
            poolSize = newThreadPoolSize;
        }
        return poolSize;
    }

    private HikariDataSource getHikariDataSource(int newThreadPoolSize) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Environment.getProp("datasource.url", String.class));
        config.setUsername(Environment.getProp("datasource.username", String.class));
        config.setPassword(Environment.getProp("datasource.password", String.class));
        config.setMaximumPoolSize(newThreadPoolSize);
        config.setDriverClassName(Environment.getProp("datasource.driver-class-name", String.class));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }

    public int getPoolSize() {
        return poolSize;
    }

    @Destroy(-1)
    public void destroy() {
        hikaruDataSource.close();
    }
}
