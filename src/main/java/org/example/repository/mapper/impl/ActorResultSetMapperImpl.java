package org.example.repository.mapper.impl;

import org.example.model.ActorEntity;
import org.example.repository.mapper.ActorResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActorResultSetMapperImpl implements ActorResultSetMapper {
    @Override
    public ActorEntity map(ResultSet resultSet) throws SQLException {
        return new ActorEntity(
                resultSet.getLong("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getDate("birthdate"),
                null
        );
    }
}
