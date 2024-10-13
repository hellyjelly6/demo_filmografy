package org.example.service.impl;

import org.example.exception.NotFoundException;
import org.example.model.GenreEntity;
import org.example.repository.GenreRepository;
import org.example.repository.MovieRepository;
import org.example.repository.impl.GenreRepositoryImpl;
import org.example.repository.impl.MovieRepositoryImpl;
import org.example.service.GenreService;
import org.example.servlet.dto.GenreIncomingDto;
import org.example.servlet.dto.GenreOutGoingDto;
import org.example.servlet.mapper.GenreDtoMapper;
import org.example.servlet.mapper.impl.GenreDtoMapperImpl;

import java.util.List;

public class GenreServiceImpl implements GenreService {
    private GenreRepository genreRepository;
    private MovieRepository movieRepository;
    private GenreDtoMapper genreDtoMapper;

    public GenreServiceImpl() {
        genreRepository = new GenreRepositoryImpl();
        movieRepository = new MovieRepositoryImpl();
        genreDtoMapper = new GenreDtoMapperImpl();
    }

    public GenreServiceImpl(GenreRepository genreRepository, GenreDtoMapper genreDtoMapper, MovieRepository movieRepository) {
        this.genreRepository = genreRepository;
        this.genreDtoMapper = genreDtoMapper;
        this.movieRepository = movieRepository;
    }

    @Override
    public List<GenreOutGoingDto> findAll() {
        List<GenreEntity> genreEntities = genreRepository.findAll();
        for (GenreEntity genreEntity : genreEntities) {
            genreEntity.setMovies(movieRepository.findMoviesByGenreId(genreEntity.getId()));
        }
        return genreDtoMapper.map(genreEntities);
    }

    @Override
    public GenreOutGoingDto findById(Long id) throws NotFoundException {
        exists(id);
        GenreEntity genreEntity = genreRepository.findById(id);
        genreEntity.setMovies(movieRepository.findMoviesByGenreId(genreEntity.getId()));
        return genreDtoMapper.map(genreEntity);
    }

    @Override
    public GenreOutGoingDto save(GenreIncomingDto genreIncomingDto) {
        GenreEntity genreEntity = genreDtoMapper.map(genreIncomingDto);
        genreEntity = genreRepository.save(genreEntity);
        return genreDtoMapper.map(genreEntity);
    }

    @Override
    public GenreOutGoingDto update(GenreIncomingDto genreIncomingDto, Long id) throws NotFoundException {
        exists(id);
        GenreEntity genreEntity = genreDtoMapper.map(genreIncomingDto);
        genreEntity.setId(id);
        genreEntity = genreRepository.update(genreEntity);
        genreEntity.setMovies(movieRepository.findMoviesByGenreId(genreEntity.getId()));
        return genreDtoMapper.map(genreEntity);
    }

    @Override
    public boolean delete(Long id) throws NotFoundException {
        exists(id);
        return genreRepository.deleteById(id);
    }

    @Override
    public void exists(Long id) throws NotFoundException {
        boolean isExists = genreRepository.exists(id);
        if (!isExists) {
            throw new NotFoundException("Genre not found");
        }
    }
}
