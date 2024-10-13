package org.example.service.impl;

import org.example.exception.NotFoundException;
import org.example.model.ActorEntity;
import org.example.model.GenreEntity;
import org.example.model.MovieEntity;
import org.example.repository.ActorToMovieRepository;
import org.example.repository.MovieRepository;
import org.example.servlet.dto.ActorLimitedDto;
import org.example.servlet.dto.MovieIncomingDto;
import org.example.servlet.dto.MovieOutGoingDto;
import org.example.servlet.mapper.ActorDtoMapper;
import org.example.servlet.mapper.MovieDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class MovieServiceImplTest {
    private MovieRepository mockmovieRepository;
    private ActorToMovieRepository mockactorToMovieRepository;
    private ActorDtoMapper mockactorDtoMapper;
    private MovieDtoMapper mockmovieDtoMapper;
    private MovieServiceImpl movieService;

    private MovieEntity movieEntity;
    private MovieIncomingDto movieIncomingDto;
    private MovieOutGoingDto movieOutGoingDto;

    @BeforeEach
    void setUp() {
        mockmovieRepository = Mockito.mock(MovieRepository.class);
        mockactorToMovieRepository = Mockito.mock(ActorToMovieRepository.class);
        mockactorDtoMapper = Mockito.mock(ActorDtoMapper.class);
        mockmovieDtoMapper = Mockito.mock(MovieDtoMapper.class);
        movieService = new MovieServiceImpl(mockmovieRepository, mockactorDtoMapper, mockmovieDtoMapper,
            mockactorToMovieRepository);

        movieIncomingDto = new MovieIncomingDto("Титаник", 1997, new GenreEntity());
        movieEntity = new MovieEntity(1L, "Титаник", 1997, new GenreEntity(), List.of());
        movieOutGoingDto = new MovieOutGoingDto(1L, "Титаник", 1997, new GenreEntity(), List.of());
    }


    @Test
    void findAll() {
        MovieEntity movieEntity2 = new MovieEntity(2L, "Интерстеллар", 2014, new GenreEntity(), List.of());

        List<MovieOutGoingDto> mockMovieList = Arrays.asList(
                new MovieOutGoingDto(1L, "Титаник", 1997, new GenreEntity(), List.of()),
                new MovieOutGoingDto(2L, "Интерстеллар", 2014, new GenreEntity(), List.of())
        );

        when(mockmovieRepository.findAll()).thenReturn(List.of(movieEntity, movieEntity2));
        when(mockmovieDtoMapper.map(anyList())).thenReturn(mockMovieList);

        List<MovieOutGoingDto> result = movieService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Титаник", result.get(0).getTitle());
        assertEquals("Интерстеллар", result.get(1).getTitle());
        assertEquals(1997, result.get(0).getReleaseYear());
        assertEquals(2014, result.get(1).getReleaseYear());
        assertEquals(mockMovieList.get(0).getGenre(), result.get(0).getGenre());
        assertEquals(mockMovieList.get(0).getActors(), result.get(0).getActors());
        assertEquals(mockMovieList.get(1).getActors(), result.get(1).getActors());

        verify(mockmovieRepository).findAll();
        verify(mockmovieDtoMapper).map(anyList());
    }

    @Test
    void findById() throws NotFoundException {
        when(mockmovieRepository.exists(1L)).thenReturn(true);
        when(mockmovieRepository.findById(1L)).thenReturn(movieEntity);
        when(mockmovieDtoMapper.map(movieEntity)).thenReturn(movieOutGoingDto);

        MovieOutGoingDto result = movieService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Титаник", result.getTitle());
        assertEquals(1997, result.getReleaseYear());
        assertEquals(movieOutGoingDto.getGenre(), result.getGenre());
        assertEquals(movieOutGoingDto.getActors(), result.getActors());

        verify(mockmovieRepository).exists(1L);
        verify(mockmovieRepository).findById(1L);
        verify(mockmovieDtoMapper).map(movieEntity);
    }

    @Test
    void findByIdNotFound()  {
        when(mockmovieRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> movieService.findById(1L));

        verify(mockmovieRepository).exists(1L);
        verify(mockmovieRepository, never()).findById(anyLong());
    }

    @Test
    void save() {
        when(mockmovieDtoMapper.map(movieIncomingDto)).thenReturn(movieEntity);
        when(mockmovieRepository.save(movieEntity)).thenReturn(movieEntity);
        when(mockmovieDtoMapper.map(movieEntity)).thenReturn(movieOutGoingDto);

        MovieOutGoingDto result = movieService.save(movieIncomingDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getId());
        assertEquals("Титаник", result.getTitle());
        assertEquals(1997, result.getReleaseYear());
        assertEquals(movieOutGoingDto.getGenre(), result.getGenre());
        assertEquals(movieOutGoingDto.getActors(), result.getActors());

        verify(mockmovieDtoMapper).map(movieIncomingDto);
        verify(mockmovieRepository).save(movieEntity);
        verify(mockmovieDtoMapper).map(movieEntity);
    }

    @Test
    void saveActorsForMovie() throws NotFoundException {
        Long movieId = 1L;

        ActorLimitedDto[] actorDtoArray = {
                new ActorLimitedDto(1L, "Эмилия", "Кларк"),
                new ActorLimitedDto(2L,"Кит", "Харингтон")
        };

        List<ActorEntity> actorEntityList = List.of(
                new ActorEntity(1L, "Эмилия", "Кларк", java.sql.Date.valueOf("1986-10-23"), List.of()),
                new ActorEntity(2L, "Кит", "Харингтон", java.sql.Date.valueOf("1986-12-26"), List.of())
        );

        when(mockmovieRepository.exists(movieId)).thenReturn(true);
        when(mockmovieRepository.findById(movieId)).thenReturn(movieEntity);
        when(mockactorDtoMapper.map(actorDtoArray)).thenReturn(actorEntityList);
        when(movieService.findById(movieId)).thenReturn(movieOutGoingDto);

        MovieOutGoingDto result = movieService.saveActorsForMovie(movieId, actorDtoArray);

        assertNotNull(result);
        assertEquals(movieOutGoingDto, result);

        verify(mockactorDtoMapper).map(actorDtoArray);

        for (ActorEntity actorEntity : actorEntityList) {
            verify(mockactorToMovieRepository).saveActorsToMovieByUserName(movieId, actorEntity);
        }

        // Проверяем, что метод findById был вызван дважды: один раз в методе и один раз в тесте
        verify(mockmovieRepository, times(2)).findById(movieId);
    }

    @Test
    void update() throws NotFoundException {
        when(mockmovieRepository.exists(1L)).thenReturn(true);
        when(mockmovieDtoMapper.map(movieIncomingDto)).thenReturn(movieEntity);
        when(mockmovieRepository.update(movieEntity)).thenReturn(movieEntity);
        when(mockmovieDtoMapper.map(movieEntity)).thenReturn(movieOutGoingDto);

        MovieOutGoingDto result = movieService.update(movieIncomingDto, 1L);

        assertNotNull(result);
        assertEquals(movieOutGoingDto, result);

        verify(mockmovieRepository).exists(1L);
        verify(mockmovieDtoMapper).map(movieIncomingDto);
        verify(mockmovieRepository).update(movieEntity);
        verify(mockmovieDtoMapper).map(movieEntity);
    }

    @Test
    void updateNotFound() {
        when(mockmovieRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> movieService.update(movieIncomingDto, 1L));

        verify(mockmovieRepository).exists(1L);
        verify(mockmovieRepository, never()).update(any(MovieEntity.class));
    }

    @Test
    void delete() throws NotFoundException {
        when(mockmovieRepository.exists(1L)).thenReturn(true);
        when(mockmovieRepository.deleteById(1L)).thenReturn(true);

        boolean result = movieService.delete(1L);

        assertTrue(result);

        verify(mockmovieRepository).exists(1L);
        verify(mockmovieRepository).deleteById(1L);
    }

    @Test
    void deleteNotFound() {
        when(mockmovieRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> movieService.delete(1L));

        verify(mockmovieRepository).exists(1L);
        verify(mockmovieRepository, never()).deleteById(anyLong());
    }
}