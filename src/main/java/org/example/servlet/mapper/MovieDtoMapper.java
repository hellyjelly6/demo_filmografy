package org.example.servlet.mapper;

import org.example.model.MovieEntity;
import org.example.servlet.dto.MovieIncomingDto;
import org.example.servlet.dto.MovieOutGoingDto;

import java.util.List;

public interface MovieDtoMapper {
    MovieEntity map(MovieIncomingDto incomingDto);

    MovieOutGoingDto map(MovieEntity movieEntity);

    List<MovieOutGoingDto> map(List<MovieEntity> movieEntities);
}
