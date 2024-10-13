package org.example.model;

import java.util.Objects;

/**
 * ManyToMany: ActorEntity <-> MovieEntity
 */
public class ActorToMovieEntity {
    private Long id;
    private Long movieId;
    private Long actorId;

    public ActorToMovieEntity() {}

    public ActorToMovieEntity(Long id, Long movieId, Long actorId) {
        this.id = id;
        this.movieId = movieId;
        this.actorId = actorId;
    }

    public Long getId() {return this.id;}
    public void setId(Long id) {this.id = id;}

    public Long getMovieId() {return movieId;}
    public void setMovieId(Long movieId) {this.movieId = movieId;}

    public Long getActorId() {return actorId;}
    public void setActorId(Long actorId) {this.actorId = actorId;}

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ActorToMovieEntity actorToMovie = (ActorToMovieEntity) obj;
        return id == actorToMovie.id;
    }
}
