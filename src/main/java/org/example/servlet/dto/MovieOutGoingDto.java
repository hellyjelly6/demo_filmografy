package org.example.servlet.dto;

import org.example.model.ActorEntity;
import org.example.model.GenreEntity;

import java.util.ArrayList;
import java.util.List;

public class MovieOutGoingDto {
    private Long id;
    private String title;
    private int releaseYear;
    private GenreEntity genre;
    private List<ActorLimitedDto> actors = new ArrayList<>();

    public MovieOutGoingDto() {}

    public MovieOutGoingDto(Long id, String title, int releaseYear, GenreEntity genre, List<ActorLimitedDto> actors) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.actors = actors;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public GenreEntity getGenre() {
        return genre;
    }

    public void setGenre(GenreEntity genre) {
        this.genre = genre;
    }

    public List<ActorLimitedDto> getActors() {
        return actors;
    }

    public void setActors(List<ActorLimitedDto> actors) {
        this.actors = actors;
    }
}
