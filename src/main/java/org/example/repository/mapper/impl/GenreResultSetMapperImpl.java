package org.example.repository.mapper.impl;

import org.example.model.GenreEntity;
import org.example.repository.mapper.GenreResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreResultSetMapperImpl implements GenreResultSetMapper {
    @Override
    public GenreEntity map(ResultSet resultSet) throws SQLException {
        return new GenreEntity(
                resultSet.getLong("id"),
                resultSet.getString("genre_name"),
                null
        );
    }
}
