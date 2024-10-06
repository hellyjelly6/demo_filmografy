package org.example.model;

import org.example.repository.ActorToMovieEntityRepository;
import org.example.repository.impl.ActorToMovieEntityRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Relation:
 * Many To Many: MovieEntity <-> ActorEntity
 * Many To One: MovieEntity -> GenreEntity
 */
public class MovieEntity {

    private static final ActorToMovieEntityRepository actorToMovieRepository = new ActorToMovieEntityRepositoryImpl();

    private Long id;
    private String title;
    private int releaseYear;
    private GenreEntity genre;
    private List<ActorEntity> actors = new ArrayList<>();


    // Конструкторы, геттеры и сеттеры

    public MovieEntity() {}

    public MovieEntity(Long id, String title, int releaseYear, GenreEntity genre, List<ActorEntity> actors) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
        this.actors = actors;
        this.genre = genre;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public int getReleaseYear() {return releaseYear;}
    public void setReleaseYear(int releaseYear) {this.releaseYear = releaseYear;}

    public GenreEntity getGenre() {return genre;}
    public void setGenre(GenreEntity genre) {this.genre = genre;}

    public List<ActorEntity> getActors() {
        if(actors == null) {
            actors = actorToMovieRepository.findActorsByMovieId(this.id);
        }
        return actors;
    }

    public void setActors(List<ActorEntity> actors) {this.actors = actors;}

}

