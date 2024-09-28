package org.example.repository.mapper.impl;

import org.example.model.ActorToMovieEntity;
import org.example.repository.mapper.ActorToMovieResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActorToMovieResultSetMapperImpl implements ActorToMovieResultSetMapper {
    @Override
    public ActorToMovieEntity map(ResultSet resultSet) throws SQLException {
        return new ActorToMovieEntity(
                resultSet.getLong("id"),
                resultSet.getLong("movie_id"),
                resultSet.getLong("actor_id")
        );
    }
}
