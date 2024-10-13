package org.example.repository.mapper.impl;

import org.example.db.ConnectionManager;
import org.example.model.GenreEntity;
import org.example.model.MovieEntity;
import org.example.repository.GenreRepository;
import org.example.repository.impl.GenreRepositoryImpl;
import org.example.repository.mapper.MovieResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MovieResultSetMapperImpl implements MovieResultSetMapper {

    @Override
    public MovieEntity map(ResultSet resultSet, ConnectionManager connectionManager) throws SQLException {

        GenreRepository genreRepository = new GenreRepositoryImpl(connectionManager);
        Long movieId = resultSet.getLong("id");
        GenreEntity genreEntity = genreRepository.findById(resultSet.getLong("genre_id"));

        return new MovieEntity(
        movieId,
        resultSet.getString("title"),
        resultSet.getInt("release_year"),
        genreEntity,
        new ArrayList<>());
    }
}
