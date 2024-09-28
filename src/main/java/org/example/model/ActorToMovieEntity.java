package org.example.model;

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
}
