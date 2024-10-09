package org.example.service.impl;

import org.example.exception.NotFoundException;
import org.example.model.ActorEntity;
import org.example.repository.ActorEntityRepository;
import org.example.repository.ActorToMovieEntityRepository;
import org.example.repository.impl.ActorEntityRepositoryImpl;
import org.example.repository.impl.ActorToMovieEntityRepositoryImpl;
import org.example.service.ActorService;
import org.example.servlet.dto.ActorIncomingDto;
import org.example.servlet.dto.ActorOutGoingDto;
import org.example.servlet.mapper.ActorDtoMapper;
import org.example.servlet.mapper.impl.ActorDtoMapperImpl;

import java.util.List;

public class ActorServiceImpl implements ActorService {
    ActorEntityRepository actorEntityRepository;
    ActorToMovieEntityRepository actorToMovieEntityRepository;
    ActorDtoMapper actorDtoMapper;

    public ActorServiceImpl(ActorEntityRepository actorEntityRepository, ActorToMovieEntityRepository actorToMovieEntityRepository, ActorDtoMapper actorDtoMapper) {
        this.actorEntityRepository = actorEntityRepository;
        this.actorToMovieEntityRepository = actorToMovieEntityRepository;
        this.actorDtoMapper = actorDtoMapper;
    }

    public ActorServiceImpl(){
        this.actorEntityRepository = new ActorEntityRepositoryImpl();
        this.actorToMovieEntityRepository = new ActorToMovieEntityRepositoryImpl();
        this.actorDtoMapper = new ActorDtoMapperImpl();
    }

    @Override
    public List<ActorOutGoingDto> findAll() {
        List<ActorEntity> actorEntities = actorEntityRepository.findAll();
        for (ActorEntity actorEntity : actorEntities) {
            actorEntity.setMovies(actorToMovieEntityRepository.findMoviesByActorId(actorEntity.getId()));
        }
        return actorDtoMapper.mapList(actorEntities);
    }

    @Override
    public ActorOutGoingDto findById(Long id) throws NotFoundException {
        exists(id);
        ActorEntity actorEntity = actorEntityRepository.findById(id);
        actorEntity.setMovies(actorToMovieEntityRepository.findMoviesByActorId(actorEntity.getId()));
        return actorDtoMapper.map(actorEntity);
    }

    @Override
    public ActorOutGoingDto save(ActorIncomingDto actorIncomingDto) {
        ActorEntity actorEntity = actorDtoMapper.map(actorIncomingDto);
        actorEntity = actorEntityRepository.save(actorEntity);
        actorEntity.setMovies(actorToMovieEntityRepository.findMoviesByActorId(actorEntity.getId()));
        return actorDtoMapper.map(actorEntity);
    }

    @Override
    public ActorOutGoingDto update(ActorIncomingDto actorIncomingDto, Long id) throws NotFoundException {
        exists(id);
        ActorEntity actorEntity = actorDtoMapper.map(actorIncomingDto);
        actorEntity.setId(id);
        ActorEntity updated = actorEntityRepository.update(actorEntity);
        updated.setMovies(actorToMovieEntityRepository.findMoviesByActorId(actorEntity.getId()));
        return actorDtoMapper.map(updated);
    }

    @Override
    public boolean delete(Long id) throws NotFoundException {
        exists(id);
        return actorEntityRepository.deleteById(id);
    }

    @Override
    public void exists(Long id) throws NotFoundException {
        boolean isExists = actorEntityRepository.exists(id);
        if (!isExists) {
            throw new NotFoundException("Actor not found");
        }
    }
}
