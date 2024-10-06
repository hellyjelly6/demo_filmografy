package org.example.servlet.mapper.impl;

import org.example.model.ActorEntity;
import org.example.model.MovieEntity;
import org.example.servlet.dto.ActorIncomingDto;
import org.example.servlet.dto.ActorLimitedDto;
import org.example.servlet.dto.ActorOutGoingDto;
import org.example.servlet.mapper.ActorDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActorDtoMapperImplTest {
    ActorDtoMapper actorDtoMapper;

    @BeforeEach
    void setUp() {
        actorDtoMapper = new ActorDtoMapperImpl();
    }

    @DisplayName("ActorEntity map(ActorIncomingDto)")
    @Test
    void mapIncomingDtoToActor() {
        ActorIncomingDto actorIncomingDto = new ActorIncomingDto("Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23"));

        ActorEntity actorEntity = actorDtoMapper.map(actorIncomingDto);

        assertNotNull(actorEntity);
        assertNull(actorEntity.getId());
        assertEquals(actorEntity.getFirstName(), actorIncomingDto.getFirstName());
        assertEquals(actorEntity.getLastName(), actorIncomingDto.getLastName());
        assertEquals(actorEntity.getBirthDate(), actorIncomingDto.getBirthDate());
    }


    @DisplayName("ActorOutGoingDto map(ActorEntity)")
    @Test
    void mapActorToOutgoingDto() {
        ActorEntity actorEntity = new ActorEntity(10L, "Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23"), List.of(new MovieEntity(),new MovieEntity()));

        ActorOutGoingDto actorOutGoingDto = actorDtoMapper.map(actorEntity);

        assertNotNull(actorOutGoingDto);
        assertEquals(actorOutGoingDto.getId(), actorEntity.getId());
        assertEquals(actorOutGoingDto.getFirstName(), actorEntity.getFirstName());
        assertEquals(actorOutGoingDto.getLastName(), actorEntity.getLastName());
        assertEquals(actorOutGoingDto.getBirthDate(), actorEntity.getBirthDate());
    }

    @DisplayName("List<ActorOutGoingDto> mapList(List<ActorEntity>)")
    @Test
    void mapActorListToOutgoingDtoList() {
        List<ActorEntity> actorEntities = List.of(
                new ActorEntity(1L, "Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23"), List.of(new MovieEntity(),new MovieEntity())),
                new ActorEntity(2L, "Алфи", "Аллен", java.sql.Date.valueOf("1986-09-12"), List.of(new MovieEntity())),
                new ActorEntity(3L, "Софи", "Тернер", java.sql.Date.valueOf("1996-02-21"), List.of(new MovieEntity(),new MovieEntity()))
        );

        List<ActorOutGoingDto> result = actorDtoMapper.mapList(actorEntities);

        assertNotNull(result);
        assertEquals(result.size(), actorEntities.size());
    }

    @DisplayName("ActorEntity map(ActorLimitedDto)")
    @Test
    void mapLimitedDtoToActor() {
        ActorLimitedDto actorLimitedDto = new ActorLimitedDto(10L, "Эмилия", "Кларк");

        ActorEntity actorEntity = actorDtoMapper.map(actorLimitedDto);

        assertNotNull(actorEntity);
        assertEquals(actorEntity.getId(), actorLimitedDto.getId());
        assertEquals(actorEntity.getFirstName(), actorLimitedDto.getFirstName());
        assertEquals(actorEntity.getLastName(), actorLimitedDto.getLastName());
        assertNull(actorEntity.getBirthDate());
    }

    @DisplayName("List<ActorEntity> map(ActorLimitedDto[])")
    @Test
    void testActorArrayLimitedDtoToListActor() {
        ActorLimitedDto[] actorLimitedDtoArray = new ActorLimitedDto[]{
                new ActorLimitedDto(1L, "Эмилия", "Кларк"),
                new ActorLimitedDto(2L, "Софи", "Тернер")
        };

        List<ActorEntity> actorEntities = actorDtoMapper.map(actorLimitedDtoArray);

        assertNotNull(actorEntities);
        assertEquals(actorEntities.size(), actorLimitedDtoArray.length);
    }
}