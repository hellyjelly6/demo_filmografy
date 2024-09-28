package org.example.servlet.mapper.impl;

import org.example.model.ActorEntity;
import org.example.model.GenreEntity;
import org.example.model.MovieEntity;
import org.example.servlet.dto.MovieIncomingDto;
import org.example.servlet.dto.MovieOutGoingDto;
import org.example.servlet.mapper.MovieDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieDtoMapperImplTest {
    private MovieDtoMapper movieDtoMapper;


    @BeforeEach
    void setUp() {
        movieDtoMapper = new MovieDtoMapperImpl();
    }

    @DisplayName("MovieEntity map(MovieIncomingDto)")
    @Test
    void mapIncomingDtoToMovieEntity() {
        MovieIncomingDto movieIncomingDto = new MovieIncomingDto("Лестница", 2022, new GenreEntity());
        MovieEntity movieEntity = movieDtoMapper.map(movieIncomingDto);

        assertNotNull(movieEntity);
        assertNull(movieEntity.getId());
        assertEquals(movieIncomingDto.getTitle(), movieEntity.getTitle());
        assertEquals(movieIncomingDto.getReleaseYear(), movieEntity.getReleaseYear());
        assertEquals(movieIncomingDto.getGenre(), movieEntity.getGenre());
    }

    @DisplayName("MovieOutGoingDto map(MovieEntity)")
    @Test
    void mapMovieEntityToMovieOutGoingDto() {
        MovieEntity movieEntity = new MovieEntity(10L, "Лестница", 2022, new GenreEntity(), List.of(new ActorEntity(), new ActorEntity()));

        MovieOutGoingDto movieOutGoingDto = movieDtoMapper.map(movieEntity);

        assertNotNull(movieOutGoingDto);
        assertEquals(movieOutGoingDto.getId(), movieEntity.getId());
        assertEquals(movieOutGoingDto.getTitle(), movieEntity.getTitle());
        assertEquals(movieOutGoingDto.getReleaseYear(), movieEntity.getReleaseYear());
        assertEquals(movieOutGoingDto.getGenre(), movieEntity.getGenre());
    }

    @DisplayName("List<MovieOutGoingDto> map(List<MovieEntity>)")
    @Test
    void mapMovieEntityListToMovieOutGoingDtoList() {
        List<MovieEntity> movieEntityList = List.of(
                new MovieEntity(1L, "Лестница", 2022, new GenreEntity(), List.of(new ActorEntity(), new ActorEntity())),
                new MovieEntity(2L, "Голос из камня", 2016, new GenreEntity(), List.of(new ActorEntity())),
                new MovieEntity(3L, "Пластик", 2014, new GenreEntity(), List.of())
        );

        List<MovieOutGoingDto> movieOutGoingDtoList = movieDtoMapper.map(movieEntityList);

        assertNotNull(movieOutGoingDtoList);
        assertEquals(movieOutGoingDtoList.size(), movieEntityList.size());
    }
}