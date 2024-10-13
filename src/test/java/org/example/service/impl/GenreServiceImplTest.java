package org.example.service.impl;

import org.example.exception.NotFoundException;
import org.example.model.GenreEntity;
import org.example.repository.GenreRepository;
import org.example.repository.MovieRepository;
import org.example.servlet.dto.GenreIncomingDto;
import org.example.servlet.dto.GenreOutGoingDto;
import org.example.servlet.mapper.GenreDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class GenreServiceImplTest {
    private GenreRepository mockGenreRepository;
    private GenreDtoMapper mockDtoMapper;
    private MovieRepository mockMovieRepository;
    private GenreServiceImpl genreService;

    private GenreEntity genreEntity;
    private GenreOutGoingDto genreOutGoingDto;
    private GenreIncomingDto genreIncomingDto;

    @BeforeEach
    void setUp() {
        mockGenreRepository = Mockito.mock(GenreRepository.class);
        mockDtoMapper = Mockito.mock(GenreDtoMapper.class);
        mockMovieRepository = Mockito.mock(MovieRepository.class);
        genreService = new GenreServiceImpl(mockGenreRepository, mockDtoMapper, mockMovieRepository);

        genreIncomingDto = new GenreIncomingDto("genre1");
        genreEntity = new GenreEntity(1L, "genre1", List.of());
        genreOutGoingDto = new GenreOutGoingDto(1L, "genre1", List.of());
    }


    @Test
    void findAll() {
        GenreEntity genreEntity2 = new GenreEntity(2L, "genre2", List.of());

        List<GenreOutGoingDto> genreOutGoingDtoList = List.of(
                new GenreOutGoingDto(1L, "genre1", List.of()),
                new GenreOutGoingDto(2L, "genre2", List.of())
        );

        // Настройка моков
        when(mockGenreRepository.findAll()).thenReturn(List.of(genreEntity, genreEntity2));
        when(mockDtoMapper.map(anyList())).thenReturn(genreOutGoingDtoList);

        // Вызов метода сервиса
        List<GenreOutGoingDto> result = genreService.findAll();

        // Проверка корректности работы
        assertNotNull(result);
        assertEquals(2, result.size());  // Убедитесь, что результат содержит 2 объекта
        assertEquals("genre1", result.get(0).getName());
        assertEquals(genreOutGoingDtoList.get(1).getName(), result.get(1).getName());

        // Верификация вызовов моков
        verify(mockGenreRepository).findAll();
        verify(mockDtoMapper).map(anyList());
    }

    @Test
    void findById() throws NotFoundException {

        when(mockGenreRepository.exists(1L)).thenReturn(true);
        when(mockGenreRepository.findById(1L)).thenReturn(genreEntity);
        when(mockDtoMapper.map(genreEntity)).thenReturn(genreOutGoingDto);

        GenreOutGoingDto result = genreService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("genre1", result.getName());

        verify(mockGenreRepository).exists(1L);
        verify(mockGenreRepository).findById(1L);
        verify(mockDtoMapper).map(genreEntity);
    }

    @Test
    void findByIdNotFound()  {
        when(mockGenreRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> genreService.findById(1L));

        verify(mockGenreRepository).exists(1L);
        verify(mockGenreRepository, never()).findById(anyLong());
    }

    @Test
    void save() {
        when(mockDtoMapper.map(genreIncomingDto)).thenReturn(genreEntity);
        when(mockGenreRepository.save(genreEntity)).thenReturn(genreEntity);
        when(mockDtoMapper.map(genreEntity)).thenReturn(genreOutGoingDto);

        GenreOutGoingDto result = genreService.save(genreIncomingDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("genre1", result.getName());

        verify(mockDtoMapper).map(genreIncomingDto);
        verify(mockGenreRepository).save(genreEntity);
        verify(mockDtoMapper).map(genreEntity);
    }

    @Test
    void update() throws NotFoundException {
        when(mockGenreRepository.exists(1L)).thenReturn(true);
        when(mockDtoMapper.map(genreIncomingDto)).thenReturn(genreEntity);
        when(mockGenreRepository.update(genreEntity)).thenReturn(genreEntity);
        when(mockDtoMapper.map(genreEntity)).thenReturn(genreOutGoingDto);

        GenreOutGoingDto result = genreService.update(genreIncomingDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("genre1", result.getName());

        verify(mockGenreRepository).exists(1L);
        verify(mockDtoMapper).map(genreIncomingDto);
        verify(mockGenreRepository).update(genreEntity);
        verify(mockDtoMapper).map(genreEntity);
    }

    @Test
    void updateNotFound()  {
        when(mockGenreRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> genreService.update(genreIncomingDto, 1L));

        verify(mockGenreRepository).exists(1L);
        verify(mockGenreRepository, never()).update(any(GenreEntity.class));

    }

    @Test
    void delete() throws NotFoundException {
        when(mockGenreRepository.exists(1L)).thenReturn(true);
        when(mockGenreRepository.deleteById(1L)).thenReturn(true);

        boolean result = genreService.delete(1L);

        assertTrue(result);

        verify(mockGenreRepository).exists(1L);
        verify(mockGenreRepository).deleteById(1L);
    }

    @Test
    void deleteNotFound()  {
        when(mockGenreRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> genreService.delete(1L));

        verify(mockGenreRepository).exists(1L);
        verify(mockGenreRepository, never()).deleteById(anyLong());
        verify(mockMovieRepository, never()).deleteConstraintByGenreId(anyLong());
    }
}