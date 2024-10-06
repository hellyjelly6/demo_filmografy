package org.example.repository.mapper.impl;

import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImpl;
import org.example.model.GenreEntity;
import org.example.model.MovieEntity;
import org.example.repository.GenreEntityRepository;
import org.example.repository.impl.GenreEntityRepositoryImpl;
import org.example.repository.mapper.MovieResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MovieResultSetMapperImpl implements MovieResultSetMapper {

    @Override
    public MovieEntity map(ResultSet resultSet) throws SQLException {

        GenreEntityRepository genreRepository = new GenreEntityRepositoryImpl();
        Long movieId = resultSet.getLong("id");
        GenreEntity genreEntity = genreRepository.findById(resultSet.getLong("genre_id"));

        return new MovieEntity(
        movieId,
        resultSet.getString("title"),
        resultSet.getInt("release_year"),
        genreEntity,
        null);
    }
}
