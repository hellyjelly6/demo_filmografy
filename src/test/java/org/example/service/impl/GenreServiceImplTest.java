package org.example.service.impl;

import org.example.exception.NotFoundException;
import org.example.model.GenreEntity;
import org.example.repository.GenreEntityRepository;
import org.example.repository.MovieEntityRepository;
import org.example.servlet.dto.GenreIncomingDto;
import org.example.servlet.dto.GenreOutGoingDto;
import org.example.servlet.mapper.GenreDtoMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class GenreServiceImplTest {
    @Mock
    private GenreEntityRepository mockGenreEntityRepository;

    @Mock
    private GenreDtoMapper mockDtoMapper;

    @Mock
    private MovieEntityRepository mockMovieEntityRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    private AutoCloseable closeable;
    private GenreEntity genreEntity;
    private GenreOutGoingDto genreOutGoingDto;
    private GenreIncomingDto genreIncomingDto;

    @BeforeEach
    void setUp() {
        // Инициализация моков
        closeable = MockitoAnnotations.openMocks(this);
        genreService = new GenreServiceImpl(mockGenreEntityRepository, mockDtoMapper);

        genreIncomingDto = new GenreIncomingDto("genre1");
        genreEntity = new GenreEntity(1L, "genre1", List.of());
        genreOutGoingDto = new GenreOutGoingDto(1L, "genre1", List.of());
    }

    @AfterEach
    void tearDown() throws Exception {
        // Закрытие моков после теста
        closeable.close();
    }

    @Test
    void findAll() {
        GenreEntity genreEntity2 = new GenreEntity(2L, "genre2", List.of());

        List<GenreOutGoingDto> genreOutGoingDtoList = List.of(
                new GenreOutGoingDto(1L, "genre1", List.of()),
                new GenreOutGoingDto(2L, "genre2", List.of())
        );

        // Настройка моков
        when(mockGenreEntityRepository.findAll()).thenReturn(List.of(genreEntity, genreEntity2));
        when(mockDtoMapper.map(anyList())).thenReturn(genreOutGoingDtoList);

        // Вызов метода сервиса
        List<GenreOutGoingDto> result = genreService.findAll();

        // Проверка корректности работы
        assertNotNull(result);
        assertEquals(2, result.size());  // Убедитесь, что результат содержит 2 объекта
        assertEquals("genre1", result.get(0).getName());
        assertEquals(genreOutGoingDtoList.get(1).getName(), result.get(1).getName());

        // Верификация вызовов моков
        verify(mockGenreEntityRepository).findAll();
        verify(mockDtoMapper).map(anyList());
    }

    @Test
    void findById() throws NotFoundException {

        when(mockGenreEntityRepository.exists(1L)).thenReturn(true);
        when(mockGenreEntityRepository.findById(1L)).thenReturn(genreEntity);
        when(mockDtoMapper.map(genreEntity)).thenReturn(genreOutGoingDto);

        GenreOutGoingDto result = genreService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("genre1", result.getName());

        verify(mockGenreEntityRepository).exists(1L);
        verify(mockGenreEntityRepository).findById(1L);
        verify(mockDtoMapper).map(genreEntity);
    }

    @Test
    void findByIdNotFound()  {
        when(mockGenreEntityRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> genreService.findById(1L));

        verify(mockGenreEntityRepository).exists(1L);
        verify(mockGenreEntityRepository, never()).findById(anyLong());
    }

    @Test
    void save() {
        when(mockDtoMapper.map(genreIncomingDto)).thenReturn(genreEntity);
        when(mockGenreEntityRepository.save(genreEntity)).thenReturn(genreEntity);
        when(mockDtoMapper.map(genreEntity)).thenReturn(genreOutGoingDto);

        GenreOutGoingDto result = genreService.save(genreIncomingDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("genre1", result.getName());

        verify(mockDtoMapper).map(genreIncomingDto);
        verify(mockGenreEntityRepository).save(genreEntity);
        verify(mockDtoMapper).map(genreEntity);
    }

    @Test
    void update() throws NotFoundException {
        when(mockGenreEntityRepository.exists(1L)).thenReturn(true);
        when(mockDtoMapper.map(genreIncomingDto)).thenReturn(genreEntity);
        when(mockGenreEntityRepository.update(genreEntity)).thenReturn(genreEntity);
        when(mockDtoMapper.map(genreEntity)).thenReturn(genreOutGoingDto);

        GenreOutGoingDto result = genreService.update(genreIncomingDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("genre1", result.getName());

        verify(mockGenreEntityRepository).exists(1L);
        verify(mockDtoMapper).map(genreIncomingDto);
        verify(mockGenreEntityRepository).update(genreEntity);
        verify(mockDtoMapper).map(genreEntity);
    }

    @Test
    void updateNotFound()  {
        when(mockGenreEntityRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> genreService.update(genreIncomingDto, 1L));

        verify(mockGenreEntityRepository).exists(1L);
        verify(mockGenreEntityRepository, never()).update(any(GenreEntity.class));

    }

    @Test
    void delete() throws NotFoundException {
        when(mockGenreEntityRepository.exists(1L)).thenReturn(true);
        when(mockGenreEntityRepository.deleteById(1L)).thenReturn(true);

        boolean result = genreService.delete(1L);

        assertTrue(result);

        verify(mockGenreEntityRepository).exists(1L);
        verify(mockGenreEntityRepository).deleteById(1L);
    }

    @Test
    void deleteNotFound()  {
        when(mockGenreEntityRepository.exists(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> genreService.delete(1L));

        verify(mockGenreEntityRepository).exists(1L);
        verify(mockGenreEntityRepository, never()).deleteById(anyLong());
        verify(mockMovieEntityRepository, never()).deleteConstraintByGenreId(anyLong());
    }
}