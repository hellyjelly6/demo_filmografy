package org.example.service;

import org.example.exception.NotFoundException;
import org.example.servlet.dto.GenreIncomingDto;
import org.example.servlet.dto.GenreOutGoingDto;

import java.util.List;

public interface GenreService {


    List<GenreOutGoingDto> findAll();

    GenreOutGoingDto findById(Long id) throws NotFoundException;

    GenreOutGoingDto save(GenreIncomingDto genreIncomingDto);

    GenreOutGoingDto update(GenreIncomingDto genreIncomingDto, Long id) throws NotFoundException;

    boolean delete(Long id) throws NotFoundException;

    void exists(Long id) throws NotFoundException;
}
