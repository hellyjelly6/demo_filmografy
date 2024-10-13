package org.example.service.impl;

import org.example.exception.NotFoundException;
import org.example.model.ActorEntity;
import org.example.repository.ActorRepository;
import org.example.repository.ActorToMovieRepository;
import org.example.repository.impl.ActorRepositoryImpl;
import org.example.repository.impl.ActorToMovieRepositoryImpl;
import org.example.service.ActorService;
import org.example.servlet.dto.ActorIncomingDto;
import org.example.servlet.dto.ActorOutGoingDto;
import org.example.servlet.mapper.ActorDtoMapper;
import org.example.servlet.mapper.impl.ActorDtoMapperImpl;

import java.util.List;

public class ActorServiceImpl implements ActorService {
    ActorRepository actorRepository;
    ActorToMovieRepository actorToMovieRepository;
    ActorDtoMapper actorDtoMapper;

    public ActorServiceImpl(ActorRepository actorRepository, ActorToMovieRepository actorToMovieRepository, ActorDtoMapper actorDtoMapper) {
        this.actorRepository = actorRepository;
        this.actorToMovieRepository = actorToMovieRepository;
        this.actorDtoMapper = actorDtoMapper;
    }

    public ActorServiceImpl(){
        this.actorRepository = new ActorRepositoryImpl();
        this.actorToMovieRepository = new ActorToMovieRepositoryImpl();
        this.actorDtoMapper = new ActorDtoMapperImpl();
    }

    @Override
    public List<ActorOutGoingDto> findAll() {
        List<ActorEntity> actorEntities = actorRepository.findAll();
        for (ActorEntity actorEntity : actorEntities) {
            actorEntity.setMovies(actorToMovieRepository.findMoviesByActorId(actorEntity.getId()));
        }
        return actorDtoMapper.mapList(actorEntities);
    }

    @Override
    public ActorOutGoingDto findById(Long id) throws NotFoundException {
        exists(id);
        ActorEntity actorEntity = actorRepository.findById(id);
        actorEntity.setMovies(actorToMovieRepository.findMoviesByActorId(actorEntity.getId()));
        return actorDtoMapper.map(actorEntity);
    }

    @Override
    public ActorOutGoingDto save(ActorIncomingDto actorIncomingDto) {
        ActorEntity actorEntity = actorDtoMapper.map(actorIncomingDto);
        actorEntity = actorRepository.save(actorEntity);
        actorEntity.setMovies(actorToMovieRepository.findMoviesByActorId(actorEntity.getId()));
        return actorDtoMapper.map(actorEntity);
    }

    @Override
    public ActorOutGoingDto update(ActorIncomingDto actorIncomingDto, Long id) throws NotFoundException {
        exists(id);
        ActorEntity actorEntity = actorDtoMapper.map(actorIncomingDto);
        actorEntity.setId(id);
        ActorEntity updated = actorRepository.update(actorEntity);
        updated.setMovies(actorToMovieRepository.findMoviesByActorId(actorEntity.getId()));
        return actorDtoMapper.map(updated);
    }

    @Override
    public boolean delete(Long id) throws NotFoundException {
        exists(id);
        return actorRepository.deleteById(id);
    }

    @Override
    public void exists(Long id) throws NotFoundException {
        boolean isExists = actorRepository.exists(id);
        if (!isExists) {
            throw new NotFoundException("Actor not found");
        }
    }
}
