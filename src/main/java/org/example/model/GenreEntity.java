package org.example.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The genre of movie
 * <p></p>
 * Many To One: MovieEntity -> GenreEntity
 */
public class GenreEntity {
    private Long id;
    private String name;
    private List<MovieEntity> movies = new ArrayList<>();

    public GenreEntity() {}

    public GenreEntity(Long id, String name, List<MovieEntity> movies) {
        this.id = id;
        this.name = name;
        this.movies = movies;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public List<MovieEntity> getMovies() {
        return movies == null ? new ArrayList<>() : movies;
    }
    public void setMovies(List<MovieEntity> movies) {
        this.movies = movies != null ? movies : new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenreEntity that = (GenreEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
