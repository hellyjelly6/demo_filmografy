package org.example.repository.mapper;

import org.example.db.ConnectionManager;
import org.example.model.MovieEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface MovieResultSetMapper {
    MovieEntity map(ResultSet resultSet, ConnectionManager connectionManager) throws SQLException;
}
