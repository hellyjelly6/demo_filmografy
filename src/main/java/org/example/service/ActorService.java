package org.example.service;

import org.example.exception.NotFoundException;
import org.example.servlet.dto.*;

import java.util.List;

public interface ActorService {
    List<ActorOutGoingDto> findAll();

    ActorOutGoingDto findById(Long id) throws NotFoundException;

    ActorOutGoingDto save(ActorIncomingDto actorIncomingDto);

    ActorOutGoingDto update(ActorIncomingDto actorIncomingDto, Long id) throws NotFoundException;

    boolean delete(Long id) throws NotFoundException;

    void exists(Long id) throws NotFoundException;

}
