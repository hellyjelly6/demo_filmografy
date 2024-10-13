package org.example.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Relation:
 * Many To Many: MovieEntity <-> ActorEntity
 * Many To One: MovieEntity -> GenreEntity
 */
public class MovieEntity {

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
        return actors == null ? new ArrayList<>() : actors;
    }

    public void setActors(List<ActorEntity> actors) {
        this.actors = actors != null ? actors : new ArrayList<>();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MovieEntity movie = (MovieEntity) obj;
        return id == movie.id;
    }
}

