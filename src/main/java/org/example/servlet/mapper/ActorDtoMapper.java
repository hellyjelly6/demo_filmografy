package org.example.servlet.mapper;

import org.example.model.ActorEntity;
import org.example.servlet.dto.ActorIncomingDto;
import org.example.servlet.dto.ActorLimitedDto;
import org.example.servlet.dto.ActorOutGoingDto;

import java.util.List;

public interface ActorDtoMapper {
    ActorEntity map(ActorIncomingDto actorIncomingDto);

    ActorOutGoingDto map(ActorEntity actorEntity);

    List<ActorOutGoingDto> mapList(List<ActorEntity> actorEntities);

    ActorEntity map(ActorLimitedDto actorLimitedDto);

    List<ActorEntity> map(ActorLimitedDto[] actorIncomingDtos);
}
