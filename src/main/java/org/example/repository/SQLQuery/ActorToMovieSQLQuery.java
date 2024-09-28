package org.example.repository.SQLQuery;

public enum ActorToMovieSQLQuery {
    DELETE_BY_MOVIE_ID_SQL("""
            DELETE FROM actor_movie
            WHERE movie_id = ?;
            """),
    DELETE_BY_ACTOR_ID_SQL("""
            DELETE FROM actor_movie
            WHERE actor_id = ?;
            """),
    SAVE_ACTORS_TO_MOVIE_BY_USER_NAME_SQL("""
            INSERT INTO actor_movie (movie_id, actor_id)
            VALUES (?, ?);
            """),
    FIND_ALL_BY_ACTOR_ID_SQL("""
            SELECT id, title, release_year, genre_id
            FROM movie
            WHERE id = ANY(
                SELECT movie_id
                FROM actor_movie
                WHERE actor_id =?
            );
            """),
    FIND_ALL_BY_MOVIE_ID_SQL("""
            SELECT id, first_name, last_name, birthdate
            FROM actor
            WHERE id = ANY(
                SELECT actor_id
                FROM actor_movie
                WHERE movie_id =?
            );
            """);

    private final String query;

    ActorToMovieSQLQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
