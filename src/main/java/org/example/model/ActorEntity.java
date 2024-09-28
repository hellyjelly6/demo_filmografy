package org.example.model;

import org.example.repository.ActorToMovieEntityRepository;
import org.example.repository.impl.ActorToMovieEntityRepositoryImpl;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

/**
 * The actor filmed in the movie
 * Relation:
 * Many To Many: ActorEntity <-> MovieEntity
 */
public class ActorEntity {
    private static ActorToMovieEntityRepository actorToMovieRepository = new ActorToMovieEntityRepositoryImpl();

    private Long id;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private List<MovieEntity> movies = new ArrayList<>();

    // Конструкторы, геттеры и сеттеры

    public ActorEntity() {}

    public ActorEntity(Long id, String firstName, String lastName, Date birthDate, List<MovieEntity> movies) {
        this.firstName = firstName;
        this.id = id;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.movies = movies;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getFirstName() {return firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}

    public String getLastName() {return lastName;}
    public void setLastName(String lastName) {this.lastName = lastName;}

    public java.sql.Date getBirthDate() {return birthDate;}
    public void setBirthDate(Date birthDate) {this.birthDate = birthDate;}

    public List<MovieEntity> getMovies() {
        if(movies == null)
            movies = actorToMovieRepository.findMoviesByActorId(this.id);
        return movies;
    }
    public void setMovies(List<MovieEntity> movies) {this.movies = movies;}

}
