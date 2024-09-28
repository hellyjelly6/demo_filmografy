package org.example.servlet.mapper.impl;

import org.example.model.GenreEntity;
import org.example.model.MovieEntity;
import org.example.servlet.dto.GenreIncomingDto;
import org.example.servlet.dto.GenreOutGoingDto;
import org.example.servlet.mapper.GenreDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenreDtoMapperImplTest {
    private GenreDtoMapper genreDtoMapper;


    @BeforeEach
    void setUp() {
        genreDtoMapper = new GenreDtoMapperImpl();
    }

    @DisplayName("GenreEntity map(GenreIncomingDto)")
    @Test
    void mapIncongDtoToEntity() {
        GenreIncomingDto incomingDto = new GenreIncomingDto("Экшн");
        GenreEntity genreEntity = genreDtoMapper.map(incomingDto);

        assertNotNull(genreEntity);
        assertNull(genreEntity.getId());
        assertEquals(incomingDto.getName(), genreEntity.getName());
    }

    @DisplayName("GenreOutGoingDto map(GenreEntity)")
    @Test
    void mapEntityToOutgoingDto() {
        GenreEntity genreEntity = new GenreEntity(20L, "Биография", List.of(new MovieEntity(),new MovieEntity()));

        GenreOutGoingDto outgoingDto = genreDtoMapper.map(genreEntity);
        assertNotNull(outgoingDto);
        assertEquals(genreEntity.getId(), outgoingDto.getId());
        assertEquals(genreEntity.getName(), outgoingDto.getName());
        assertEquals(genreEntity.getMovies().size(), outgoingDto.getMovies().size());
    }

    @DisplayName(" List<GenreOutGoingDto> map(List<GenreEntity>)")
    @Test
    void mapEntityListToOutgoingDtoList() {
        List<GenreEntity> outgoingDtoList = List.of(
                new GenreEntity(1L, "жанр1", List.of(new MovieEntity(),new MovieEntity())),
                new GenreEntity(2L, "жанр2", List.of()),
                new GenreEntity(3L, "жанр3", List.of(new MovieEntity()))
        );

        List<GenreOutGoingDto> genreOutGoingDtoList = genreDtoMapper.map(outgoingDtoList);

        assertNotNull(genreOutGoingDtoList);
        assertEquals(outgoingDtoList.size(), genreOutGoingDtoList.size());
    }
}