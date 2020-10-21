package com.roundrobin_assignment.ticketpipeline.jdbc;

import com.roundrobin_assignment.ticketpipeline.config.context.Component;
import com.roundrobin_assignment.ticketpipeline.config.context.Constructor;
import com.roundrobin_assignment.ticketpipeline.dao.RowMapper;
import com.roundrobin_assignment.ticketpipeline.exception.JdbcRuntimeException;
import com.roundrobin_assignment.ticketpipeline.jdbc.datacource.DataSource;
import com.roundrobin_assignment.ticketpipeline.util.log.Logger;
import com.roundrobin_assignment.ticketpipeline.util.log.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcOperationsImpl implements JdbcOperations {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcOperationsImpl.class);

    private final DataSource dataSource;

    @Constructor
    public JdbcOperationsImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet, resultSet.getRow()));
            }
            return result;
        } catch (Exception e) {
            LOG.error("Exception during call: {}; cause: {}", () -> sql, () -> e);
            throw new JdbcRuntimeException(e);
        }
    }

    @Override
    public int update(String sql, Object... args) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (args != null && args.length != 0) {
                for (int i = 0; i < args.length; i++) {
                    preparedStatement.setObject(i + 1, args[i]);
                }
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                int i = 0;
                while (resultSet.next()) {
                    i++;
                }
                return i;
            }
        } catch (Exception e) {
            LOG.error("Exception during call: {}; cause: {}", () -> sql, () -> e);
            throw new JdbcRuntimeException(e);
        }
    }
}
