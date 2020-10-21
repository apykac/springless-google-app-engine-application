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
    private HikariDataSource hikaruDataSource;

    @Constructor
    public DataSource() {
        hikaruDataSource = getHikariDataSource(Environment.getProp("app.thread-count", 4, Integer.class));
    }

    public Connection getConnection() throws SQLException {
        return hikaruDataSource.getConnection();
    }

    public void resizeTreadPool(int newThreadCount) {
        destroy();
        hikaruDataSource = getHikariDataSource(newThreadCount);
    }

    private HikariDataSource getHikariDataSource(int threadCount) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Environment.getProp("datasource.url", String.class));
        config.setUsername(Environment.getProp("datasource.username", String.class));
        config.setPassword(Environment.getProp("datasource.password", String.class));
        config.setMaximumPoolSize(threadCount + 1);
        config.setDriverClassName(Environment.getProp("datasource.driver-class-name", String.class));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }

    @Destroy(-1)
    public void destroy() {
        hikaruDataSource.close();
    }
}
