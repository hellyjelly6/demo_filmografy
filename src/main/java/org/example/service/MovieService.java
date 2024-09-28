package org.example.service;

import org.example.exception.NotFoundException;
import org.example.servlet.dto.ActorLimitedDto;
import org.example.servlet.dto.MovieIncomingDto;
import org.example.servlet.dto.MovieOutGoingDto;

import java.util.List;

public interface MovieService {

    List<MovieOutGoingDto> findAll();

    MovieOutGoingDto findById(Long id) throws NotFoundException;

    MovieOutGoingDto save(MovieIncomingDto movieIncomingDto);

    MovieOutGoingDto saveActorsForMovie(Long id, ActorLimitedDto[] actors)  throws NotFoundException;

    MovieOutGoingDto update(MovieIncomingDto movieIncomingDto, Long id) throws NotFoundException;

    boolean delete(Long id) throws NotFoundException;

    void exists(Long id) throws NotFoundException;
}
