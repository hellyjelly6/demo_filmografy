package org.example.servlet.dto;


import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

public class ActorOutGoingDto {
    private Long id;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private List<MovieLimitedDto> movies = new ArrayList<>();

    public ActorOutGoingDto() {}

    public ActorOutGoingDto(Long id, String firstName, String lastName, Date birthDate, List<MovieLimitedDto> movies) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.movies = movies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public List<MovieLimitedDto> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieLimitedDto> movies) {
        this.movies = movies;
    }
}
