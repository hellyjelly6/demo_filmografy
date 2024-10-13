package org.example.service.impl;

import org.example.exception.NotFoundException;
import org.example.model.ActorEntity;
import org.example.repository.ActorRepository;
import org.example.repository.ActorToMovieRepository;
import org.example.servlet.dto.ActorIncomingDto;
import org.example.servlet.dto.ActorOutGoingDto;
import org.example.servlet.mapper.ActorDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class ActorServiceImplTest {
    private ActorRepository mockActorRepository;
    private ActorToMovieRepository mockActorToMovieRepository;
    private ActorDtoMapper mockActorDtoMapper;
    private ActorServiceImpl actorService;

    private ActorEntity actorEntity;
    private ActorIncomingDto actorIncomingDto;
    private ActorOutGoingDto actorOutGoingDto;

    @BeforeEach
    void setUp() {
        mockActorRepository = Mockito.mock(ActorRepository.class);
        mockActorToMovieRepository = Mockito.mock(ActorToMovieRepository.class);
        mockActorDtoMapper = Mockito.mock(ActorDtoMapper.class);
        actorService = new ActorServiceImpl(mockActorRepository, mockActorToMovieRepository, mockActorDtoMapper);

        actorIncomingDto = new ActorIncomingDto("Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23") );
        actorEntity = new ActorEntity(1L, "Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23"), List.of());
        actorOutGoingDto = new ActorOutGoingDto(1L, "Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23"), List.of());
    }

    @Test
    void findAll() {
        ActorEntity actorEntity2 = new ActorEntity(2L, "Алфи", "Аллен", java.sql.Date.valueOf("1986-09-12"), List.of());
        List<ActorOutGoingDto> actorOutGoingDtoList = List.of(
                actorOutGoingDto,
                new ActorOutGoingDto(2L, "Алфи", "Аллен", java.sql.Date.valueOf("1986-09-12"), List.of()));

        when(mockActorRepository.findAll()).thenReturn(List.of(actorEntity, actorEntity2));
        when(mockActorDtoMapper.mapList(anyList())).thenReturn(actorOutGoingDtoList);

        List<ActorOutGoingDto> result = actorService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Эмилия", result.get(0).getFirstName());
        assertEquals("Кларк", result.get(0).getLastName());
        assertEquals(java.sql.Date.valueOf("1986-10-23").toLocalDate(), result.get(0).getBirthDate().toLocalDate());
        assertEquals("Алфи", result.get(1).getFirstName());
        assertEquals("Аллен", result.get(1).getLastName());
        assertEquals(java.sql.Date.valueOf("1986-09-12").toLocalDate(), result.get(1).getBirthDate().toLocalDate());

        verify(mockActorRepository).findAll();
        verify(mockActorDtoMapper).mapList(anyList());
    }

    @Test
    void findById() throws NotFoundException {
        when(mockActorRepository.exists(1L)).thenReturn(true);
        when(mockActorRepository.findById(1L)).thenReturn(actorEntity);
        when(mockActorDtoMapper.map(actorEntity)).thenReturn(actorOutGoingDto);

        ActorOutGoingDto result = actorService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Эмилия", result.getFirstName());
        assertEquals("Кларк", result.getLastName());
        assertEquals(java.sql.Date.valueOf("1986-10-23").toLocalDate(), result.getBirthDate().toLocalDate());

        verify(mockActorRepository).exists(1L);
        verify(mockActorRepository).findById(1L);
        verify(mockActorDtoMapper).map(actorEntity);
    }

    @Test
    void findByIdNotFound()  {
        when(mockActorRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> actorService.findById(1L));

        verify(mockActorRepository).exists(1L);
        verify(mockActorRepository, never()).findById(anyLong());
    }

    @Test
    void save() {
        when(mockActorDtoMapper.map(actorIncomingDto)).thenReturn(actorEntity);
        when(mockActorRepository.save(actorEntity)).thenReturn(actorEntity);
        when(mockActorDtoMapper.map(actorEntity)).thenReturn(actorOutGoingDto);

        ActorOutGoingDto result = actorService.save(actorIncomingDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Эмилия", result.getFirstName());
        assertEquals("Кларк", result.getLastName());
        assertEquals(java.sql.Date.valueOf("1986-10-23").toLocalDate(), result.getBirthDate().toLocalDate());

        verify(mockActorDtoMapper).map(actorIncomingDto);
        verify(mockActorRepository).save(actorEntity);
        verify(mockActorDtoMapper).map(actorEntity);
    }

    @Test
    void update() throws NotFoundException {
        when(mockActorRepository.exists(1L)).thenReturn(true);
        when(mockActorDtoMapper.map(actorIncomingDto)).thenReturn(actorEntity);
        when(mockActorRepository.update(actorEntity)).thenReturn(actorEntity);
        when(mockActorDtoMapper.map(actorEntity)).thenReturn(actorOutGoingDto);

        ActorOutGoingDto result = actorService.update(actorIncomingDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Эмилия", result.getFirstName());
        assertEquals("Кларк", result.getLastName());
        assertEquals(java.sql.Date.valueOf("1986-10-23").toLocalDate(), result.getBirthDate().toLocalDate());

        verify(mockActorRepository).exists(1L);
        verify(mockActorDtoMapper).map(actorIncomingDto);
        verify(mockActorRepository).update(actorEntity);
        verify(mockActorDtoMapper).map(actorEntity);
    }

    @Test
    void updateNotFound()  {
        when(mockActorRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> actorService.update(actorIncomingDto, 1L));

        verify(mockActorRepository).exists(1L);
        verify(mockActorRepository, never()).findById(anyLong());
    }

    @Test
    void delete() throws NotFoundException {
        when(mockActorRepository.exists(1L)).thenReturn(true);
        when(mockActorRepository.deleteById(1L)).thenReturn(true);

        boolean result = actorService.delete(1L);

        assertTrue(result);

        verify(mockActorRepository).exists(1L);
        verify(mockActorRepository).deleteById(1L);
    }

    @Test
    void deleteNotFound()  {
        when(mockActorRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> actorService.delete(1L));

        verify(mockActorRepository).exists(1L);
        verify(mockActorRepository, never()).findById(anyLong());
    }
}