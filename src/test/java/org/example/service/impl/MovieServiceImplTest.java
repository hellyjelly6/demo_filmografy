package org.example.service.impl;

import org.example.exception.NotFoundException;
import org.example.model.ActorEntity;
import org.example.model.GenreEntity;
import org.example.model.MovieEntity;
import org.example.repository.ActorToMovieEntityRepository;
import org.example.repository.MovieEntityRepository;
import org.example.servlet.dto.ActorLimitedDto;
import org.example.servlet.dto.MovieIncomingDto;
import org.example.servlet.dto.MovieOutGoingDto;
import org.example.servlet.mapper.ActorDtoMapper;
import org.example.servlet.mapper.MovieDtoMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class MovieServiceImplTest {
    @Mock
    private MovieEntityRepository mockmovieEntityRepository;

    @Mock
    private ActorToMovieEntityRepository mockactorToMovieEntityRepository;

    @Mock
    private ActorDtoMapper mockactorDtoMapper;

    @Mock
    private MovieDtoMapper mockmovieDtoMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    private AutoCloseable closeable;
    private MovieEntity movieEntity;
    private MovieIncomingDto movieIncomingDto;
    private MovieOutGoingDto movieOutGoingDto;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        movieService = new MovieServiceImpl(mockmovieEntityRepository, mockactorDtoMapper, mockmovieDtoMapper, mockactorToMovieEntityRepository);

        movieIncomingDto = new MovieIncomingDto("Титаник", 1997, new GenreEntity());
        movieEntity = new MovieEntity(1L, "Титаник", 1997, new GenreEntity(), List.of());
        movieOutGoingDto = new MovieOutGoingDto(1L, "Титаник", 1997, new GenreEntity(), List.of());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void findAll() {
        MovieEntity movieEntity2 = new MovieEntity(2L, "Интерстеллар", 2014, new GenreEntity(), List.of());

        List<MovieOutGoingDto> mockMovieList = Arrays.asList(
                new MovieOutGoingDto(1L, "Титаник", 1997, new GenreEntity(), List.of()),
                new MovieOutGoingDto(2L, "Интерстеллар", 2014, new GenreEntity(), List.of())
        );

        when(mockmovieEntityRepository.findAll()).thenReturn(List.of(movieEntity, movieEntity2));
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

        verify(mockmovieEntityRepository).findAll();
        verify(mockmovieDtoMapper).map(anyList());
    }

    @Test
    void findById() throws NotFoundException {
        when(mockmovieEntityRepository.exists(1L)).thenReturn(true);
        when(mockmovieEntityRepository.findById(1L)).thenReturn(movieEntity);
        when(mockmovieDtoMapper.map(movieEntity)).thenReturn(movieOutGoingDto);

        MovieOutGoingDto result = movieService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Титаник", result.getTitle());
        assertEquals(1997, result.getReleaseYear());
        assertEquals(movieOutGoingDto.getGenre(), result.getGenre());
        assertEquals(movieOutGoingDto.getActors(), result.getActors());

        verify(mockmovieEntityRepository).exists(1L);
        verify(mockmovieEntityRepository).findById(1L);
        verify(mockmovieDtoMapper).map(movieEntity);
    }

    @Test
    void findByIdNotFound() throws NotFoundException {
        when(mockmovieEntityRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> movieService.findById(1L));

        verify(mockmovieEntityRepository).exists(1L);
        verify(mockmovieEntityRepository, never()).findById(anyLong());
    }

    @Test
    void save() {
        when(mockmovieDtoMapper.map(movieIncomingDto)).thenReturn(movieEntity);
        when(mockmovieEntityRepository.save(movieEntity)).thenReturn(movieEntity);
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
        verify(mockmovieEntityRepository).save(movieEntity);
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

        when(mockmovieEntityRepository.exists(movieId)).thenReturn(true);
        when(mockmovieEntityRepository.findById(movieId)).thenReturn(movieEntity);
        when(mockactorDtoMapper.map(actorDtoArray)).thenReturn(actorEntityList);
        when(movieService.findById(movieId)).thenReturn(movieOutGoingDto);

        MovieOutGoingDto result = movieService.saveActorsForMovie(movieId, actorDtoArray);

        assertNotNull(result);
        assertEquals(movieOutGoingDto, result);

        verify(mockactorDtoMapper).map(actorDtoArray);

        for (ActorEntity actorEntity : actorEntityList) {
            verify(mockactorToMovieEntityRepository).saveActorsToMovieByUserName(movieId, actorEntity);
        }

        // Проверяем, что метод findById был вызван дважды: один раз в методе и один раз в тесте
        verify(mockmovieEntityRepository, times(2)).findById(movieId);
    }

    @Test
    void update() throws NotFoundException {
        when(mockmovieEntityRepository.exists(1L)).thenReturn(true);
        when(mockmovieDtoMapper.map(movieIncomingDto)).thenReturn(movieEntity);
        when(mockmovieEntityRepository.update(movieEntity)).thenReturn(movieEntity);
        when(mockmovieDtoMapper.map(movieEntity)).thenReturn(movieOutGoingDto);

        MovieOutGoingDto result = movieService.update(movieIncomingDto, 1L);

        assertNotNull(result);
        assertEquals(movieOutGoingDto, result);

        verify(mockmovieEntityRepository).exists(1L);
        verify(mockmovieDtoMapper).map(movieIncomingDto);
        verify(mockmovieEntityRepository).update(movieEntity);
        verify(mockmovieDtoMapper).map(movieEntity);
    }

    @Test
    void updateNotFound() throws NotFoundException {
        when(mockmovieEntityRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> movieService.update(movieIncomingDto, 1L));

        verify(mockmovieEntityRepository).exists(1L);
        verify(mockmovieEntityRepository, never()).update(any(MovieEntity.class));
    }

    @Test
    void delete() throws NotFoundException {
        when(mockmovieEntityRepository.exists(1L)).thenReturn(true);
        when(mockmovieEntityRepository.deleteById(1L)).thenReturn(true);

        boolean result = movieService.delete(1L);

        assertTrue(result);

        verify(mockmovieEntityRepository).exists(1L);
        verify(mockmovieEntityRepository).deleteById(1L);
    }

    @Test
    void deleteNotFound() throws NotFoundException {
        when(mockmovieEntityRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> movieService.delete(1L));

        verify(mockmovieEntityRepository).exists(1L);
        verify(mockmovieEntityRepository, never()).deleteById(anyLong());
    }
}