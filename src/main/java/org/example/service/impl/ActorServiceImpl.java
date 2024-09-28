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
    ActorEntityRepository actorEntityRepository = new ActorEntityRepositoryImpl();
    ActorToMovieEntityRepository actorToMovieEntityRepository = new ActorToMovieEntityRepositoryImpl();
    ActorDtoMapper actorDtoMapper = new ActorDtoMapperImpl();


    @Override
    public List<ActorOutGoingDto> findAll() {
        List<ActorEntity> actorEntities = actorEntityRepository.findAll();
        return actorDtoMapper.mapList(actorEntities);
    }

    @Override
    public ActorOutGoingDto findById(Long id) throws NotFoundException {
        exists(id);
        ActorEntity actorEntity = actorEntityRepository.findById(id);
        return actorDtoMapper.map(actorEntity);
    }

    @Override
    public ActorOutGoingDto save(ActorIncomingDto actorIncomingDto) {
        ActorEntity actorEntity = actorDtoMapper.map(actorIncomingDto);
        actorEntity = actorEntityRepository.save(actorEntity);
        return actorDtoMapper.map(actorEntity);
    }

    @Override
    public ActorOutGoingDto update(ActorIncomingDto actorIncomingDto, Long id) throws NotFoundException {
        exists(id);
        ActorEntity actorEntity = actorDtoMapper.map(actorIncomingDto);
        actorEntity.setId(id);
        ActorEntity updated = actorEntityRepository.update(actorEntity);
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
