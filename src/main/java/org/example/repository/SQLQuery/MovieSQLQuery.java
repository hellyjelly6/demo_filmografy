package org.example.repository.SQLQuery;

public enum MovieSQLQuery {
    FIND_BY_ID_SQL ("""
            SELECT id, title, release_year, genre_id
            FROM movie
            WHERE id = ?
            LIMIT 1;
            """),
    DELETE_SQL ("""
            DELETE FROM movie
            WHERE id = ?;
            """),
    FIND_ALL_SQL("""
            SELECT id, title, release_year, genre_id
            FROM movie;
            """),
    SAVE_SQL("""
            INSERT INTO movie (title, release_year, genre_id)
            VALUES (?, ?, ?);
            """),
    UPDATE_SQL ("""
            UPDATE movie
            SET title = ?,
                release_year = ?,
                genre_id = ?
            WHERE id = ?;
            """),
    EXISTS_BY_ID_SQL("""
            SELECT EXISTS(
                SELECT 1
                FROM movie
                WHERE id = ?
                LIMIT 1);
            """),
    FIND_BY_GENRE_ID("""
            SELECT id, title, release_year, genre_id
            FROM movie
            WHERE genre_id = ?;
            """),
    DELETE_CONSTRAINT_BY_GENRE_ID("""
            UPDATE movie
            SET
                genre_id = NULL
            WHERE genre_id = ?;
            """);


    private final String query;

    MovieSQLQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
