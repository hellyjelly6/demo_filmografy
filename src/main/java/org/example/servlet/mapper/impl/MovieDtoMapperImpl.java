package org.example.servlet.mapper.impl;

import org.example.model.MovieEntity;
import org.example.servlet.dto.ActorLimitedDto;
import org.example.servlet.dto.MovieIncomingDto;
import org.example.servlet.dto.MovieOutGoingDto;
import org.example.servlet.mapper.ActorDtoMapper;
import org.example.servlet.mapper.GenreDtoMapper;
import org.example.servlet.mapper.MovieDtoMapper;

import java.util.List;

public class MovieDtoMapperImpl implements MovieDtoMapper {
    GenreDtoMapper genreDtoMapper = new GenreDtoMapperImpl();
    ActorDtoMapper actorDtoMapper = new ActorDtoMapperImpl();

    @Override
    public MovieEntity map(MovieIncomingDto incomingDto) {

        return new MovieEntity(
                null,
                incomingDto.getTitle(),
                incomingDto.getReleaseYear(),
                incomingDto.getGenre(),
                null
        );
    }


    @Override
    public MovieOutGoingDto map(MovieEntity movieEntity) {

        List<ActorLimitedDto> actorList = movieEntity.getActors().stream()
                .map( actor -> new ActorLimitedDto(
                        actor.getId(),
                        actor.getFirstName(),
                        actor.getLastName()
                )).toList();

        return new MovieOutGoingDto(
                movieEntity.getId(),
                movieEntity.getTitle(),
                movieEntity.getReleaseYear(),
                movieEntity.getGenre(),
                actorList
        );
    }

    @Override
    public List<MovieOutGoingDto> map(List<MovieEntity> movieEntities) {
        return movieEntities.stream()
                .map(this::map)
                .toList();
    }
}
