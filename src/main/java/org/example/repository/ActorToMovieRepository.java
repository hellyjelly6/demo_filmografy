package org.example.repository;

import org.example.model.ActorEntity;
import org.example.model.MovieEntity;

import java.util.List;

public interface ActorToMovieRepository {
    boolean deleteByMovieId(Long id);

    boolean deleteByActorId(Long id);

    List<MovieEntity> findMoviesByActorId(Long id);

    List<ActorEntity> findActorsByMovieId(Long id);

    void saveActorsToMovieByUserName(Long movieId, ActorEntity actor);
}
