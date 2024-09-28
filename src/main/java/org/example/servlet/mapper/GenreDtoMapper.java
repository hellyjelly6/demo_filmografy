package org.example.servlet.mapper;

import org.example.model.GenreEntity;
import org.example.servlet.dto.GenreIncomingDto;
import org.example.servlet.dto.GenreOutGoingDto;

import java.util.List;

public interface GenreDtoMapper {
    GenreEntity map(GenreIncomingDto genreIncomingDto);

    GenreOutGoingDto map(GenreEntity genreEntity);

    List<GenreOutGoingDto> map(List<GenreEntity> genreEntities);
}
