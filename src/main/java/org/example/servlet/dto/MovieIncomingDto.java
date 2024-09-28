package org.example.servlet.dto;

import org.example.model.GenreEntity;

public class MovieIncomingDto {
    private String title;
    private int releaseYear;
    private GenreEntity genre;

    public MovieIncomingDto() {}

    public MovieIncomingDto(String title, int releaseYear, GenreEntity genre) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.genre = genre;
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
}
