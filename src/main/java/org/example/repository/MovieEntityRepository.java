package org.example.repository;

import org.example.model.MovieEntity;

import java.util.List;

public interface MovieEntityRepository extends Repository<MovieEntity, Long> {
    List<MovieEntity> findMoviesByGenreId(Long id);

    boolean deleteConstraintByGenreId(Long id);
}
