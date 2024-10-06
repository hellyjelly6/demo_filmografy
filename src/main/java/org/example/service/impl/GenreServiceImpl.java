package org.example.service.impl;

import org.example.db.ConnectionManagerImpl;
import org.example.exception.NotFoundException;
import org.example.model.GenreEntity;
import org.example.repository.GenreEntityRepository;
import org.example.repository.MovieEntityRepository;
import org.example.repository.impl.GenreEntityRepositoryImpl;
import org.example.repository.impl.MovieEntityRepositoryImpl;
import org.example.repository.mapper.impl.GenreResultSetMapperImpl;
import org.example.service.GenreService;
import org.example.servlet.dto.GenreIncomingDto;
import org.example.servlet.dto.GenreOutGoingDto;
import org.example.servlet.mapper.GenreDtoMapper;
import org.example.servlet.mapper.impl.GenreDtoMapperImpl;

import java.util.List;

public class GenreServiceImpl implements GenreService {
    private GenreEntityRepository genreEntityRepository;
    private GenreDtoMapper genreDtoMapper;
    private MovieEntityRepository movieEntityRepository;

    public GenreServiceImpl() {
        genreEntityRepository = new GenreEntityRepositoryImpl();
        genreDtoMapper = new GenreDtoMapperImpl();
        movieEntityRepository = new MovieEntityRepositoryImpl();
    }

    public GenreServiceImpl(GenreEntityRepository genreEntityRepository, GenreDtoMapper genreDtoMapper, MovieEntityRepository movieEntityRepository) {
        this.genreEntityRepository = genreEntityRepository;
        this.genreDtoMapper = genreDtoMapper;
        this.movieEntityRepository = movieEntityRepository;
    }

    @Override
    public List<GenreOutGoingDto> findAll() {
        List<GenreEntity> genreEntities = genreEntityRepository.findAll();
        return genreDtoMapper.map(genreEntities);
    }

    @Override
    public GenreOutGoingDto findById(Long id) throws NotFoundException {
        exists(id);
        GenreEntity genreEntity = genreEntityRepository.findById(id);
        return genreDtoMapper.map(genreEntity);
    }

    @Override
    public GenreOutGoingDto save(GenreIncomingDto genreIncomingDto) {
        GenreEntity genreEntity = genreDtoMapper.map(genreIncomingDto);
        genreEntity = genreEntityRepository.save(genreEntity);
        return genreDtoMapper.map(genreEntity);
    }

    @Override
    public GenreOutGoingDto update(GenreIncomingDto genreIncomingDto, Long id) throws NotFoundException {
        exists(id);
        GenreEntity genreEntity = genreDtoMapper.map(genreIncomingDto);
        genreEntity.setId(id);
        genreEntity = genreEntityRepository.update(genreEntity);
        return genreDtoMapper.map(genreEntity);
    }

    @Override
    public boolean delete(Long id) throws NotFoundException {
        exists(id);
        return genreEntityRepository.deleteById(id);
    }

    @Override
    public void exists(Long id) throws NotFoundException {
        boolean isExists = genreEntityRepository.exists(id);
        if (!isExists) {
            throw new NotFoundException("Genre not found");
        }
    }
}
