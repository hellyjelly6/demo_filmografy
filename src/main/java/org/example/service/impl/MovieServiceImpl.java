package org.example.service.impl;

import org.example.exception.NotFoundException;
import org.example.model.ActorEntity;
import org.example.model.MovieEntity;
import org.example.repository.ActorToMovieEntityRepository;
import org.example.repository.MovieEntityRepository;
import org.example.repository.impl.ActorToMovieEntityRepositoryImpl;
import org.example.repository.impl.MovieEntityRepositoryImpl;
import org.example.service.MovieService;
import org.example.servlet.dto.ActorLimitedDto;
import org.example.servlet.dto.MovieIncomingDto;
import org.example.servlet.dto.MovieOutGoingDto;
import org.example.servlet.mapper.ActorDtoMapper;
import org.example.servlet.mapper.MovieDtoMapper;
import org.example.servlet.mapper.impl.ActorDtoMapperImpl;
import org.example.servlet.mapper.impl.MovieDtoMapperImpl;

import java.util.List;

public class MovieServiceImpl implements MovieService {
    private MovieEntityRepository movieRepository  = new MovieEntityRepositoryImpl();
    private ActorDtoMapper actorDtoMapper = new ActorDtoMapperImpl();
    private MovieDtoMapper movieDtoMapper = new MovieDtoMapperImpl();
    private ActorToMovieEntityRepository actorToMovieRepository = new ActorToMovieEntityRepositoryImpl();


    @Override
    public List<MovieOutGoingDto> findAll(){
        List<MovieEntity> movieEntities = movieRepository.findAll();
        return movieDtoMapper.map(movieEntities);
    }

    @Override
    public MovieOutGoingDto findById(Long id) throws NotFoundException {
        exists(id);
        MovieEntity movieEntity = movieRepository.findById(id);
        return movieDtoMapper.map(movieEntity);
    }

    @Override
    public MovieOutGoingDto save(MovieIncomingDto movieIncomingDto) {
        MovieEntity movieEntity = movieDtoMapper.map(movieIncomingDto);
        MovieEntity saved = movieRepository.save(movieEntity);
        return movieDtoMapper.map(saved);
    }

    @Override
    public MovieOutGoingDto saveActorsForMovie(Long id, ActorLimitedDto[] actors)  throws NotFoundException{
        List<ActorEntity> actorEntity = actorDtoMapper.map(actors);
        for (ActorEntity actor : actorEntity) {
            actorToMovieRepository.saveActorsToMovieByUserName(id, actor);
        }
        return this.findById(id);
    }

    @Override
    public MovieOutGoingDto update(MovieIncomingDto movieIncomingDto, Long id) throws NotFoundException {
        exists(id);
        MovieEntity movieEntity = movieDtoMapper.map(movieIncomingDto);
        movieEntity.setId(id);
        MovieEntity updated = movieRepository.update(movieEntity);
        return movieDtoMapper.map(updated);
    }

    @Override
    public boolean delete(Long id) throws NotFoundException {
        exists(id);
        return movieRepository.deleteById(id);
    }

    @Override
    public void exists(Long id) throws NotFoundException {
       boolean exists = movieRepository.exists(id);
       if(!exists){
           throw new NotFoundException("Movie Not Found");
       }
    }

}
