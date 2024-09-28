package org.example.servlet.dto;

import java.util.ArrayList;
import java.util.List;

public class GenreOutGoingDto {
    private Long id;
    private String name;
    private List<MovieLimitedDto> movies = new ArrayList<>();

    public GenreOutGoingDto(Long id, String name, List<MovieLimitedDto> movies) {
        this.id = id;
        this.name = name;
        this.movies = movies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public List<MovieLimitedDto> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieLimitedDto> movies) {
        this.movies = movies;
    }
}
