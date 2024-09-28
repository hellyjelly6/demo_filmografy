package org.example.servlet.mapper.impl;

import org.example.model.GenreEntity;
import org.example.servlet.dto.GenreIncomingDto;
import org.example.servlet.dto.GenreOutGoingDto;
import org.example.servlet.dto.MovieLimitedDto;
import org.example.servlet.mapper.GenreDtoMapper;

import java.util.List;

public class GenreDtoMapperImpl implements GenreDtoMapper {
    @Override
    public GenreEntity map(GenreIncomingDto genreIncomingDto) {
        return new GenreEntity(
                null,
                genreIncomingDto.getName(),
                null
        );
    }


    @Override
    public GenreOutGoingDto map(GenreEntity genreEntity) {
        List<MovieLimitedDto> movieList = genreEntity.getMovies().stream()
                .map( movie -> new MovieLimitedDto(
                        movie.getId(),
                        movie.getTitle()
                ))
                .toList();
        return new GenreOutGoingDto(
                genreEntity.getId(),
                genreEntity.getName(),
                movieList
        );
    }

    @Override
    public List<GenreOutGoingDto> map(List<GenreEntity> genreEntities) {
        return genreEntities.stream().map(this::map).toList();
    }
}
