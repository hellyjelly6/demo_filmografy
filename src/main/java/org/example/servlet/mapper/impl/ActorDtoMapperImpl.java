package org.example.servlet.mapper.impl;

import org.example.model.ActorEntity;
import org.example.servlet.dto.ActorIncomingDto;
import org.example.servlet.dto.ActorLimitedDto;
import org.example.servlet.dto.ActorOutGoingDto;
import org.example.servlet.dto.MovieLimitedDto;
import org.example.servlet.mapper.ActorDtoMapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ActorDtoMapperImpl implements ActorDtoMapper {
    @Override
    public ActorEntity map(ActorIncomingDto actorIncomingDto) {
        return new ActorEntity(
                null,
                actorIncomingDto.getFirstName(),
                actorIncomingDto.getLastName(),
                actorIncomingDto.getBirthDate(),
                null
        );
    }


    @Override
    public ActorOutGoingDto map(ActorEntity actorEntity) {
        List<MovieLimitedDto> movieList = actorEntity.getMovies().stream()
                .map( movie -> new MovieLimitedDto(
                        movie.getId(),
                        movie.getTitle()
                ))
                .toList();
        return new ActorOutGoingDto(
                actorEntity.getId(),
                actorEntity.getFirstName(),
                actorEntity.getLastName(),
                actorEntity.getBirthDate(),
                movieList
        );
    }

    @Override
    public List<ActorOutGoingDto> mapList(List<ActorEntity> actorEntities) {
        return actorEntities.stream()
                .map(this::map)
                .toList();
    }

    @Override
    public ActorEntity map(ActorLimitedDto actorLimitedDto) {
        return new ActorEntity(
                actorLimitedDto.getId(),
                actorLimitedDto.getFirstName(),
                actorLimitedDto.getLastName(),
                null,
                null
        );
    }

    @Override
    public List<ActorEntity> map(ActorLimitedDto[] actorIncomingDtos) {
        return Arrays.stream(actorIncomingDtos)
                .map(this::map)
                .collect(Collectors.toList());
    }
}
