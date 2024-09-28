package org.example.repository.SQLQuery;

public enum GenreSQLQuery {
    FIND_BY_ID_SQL ("""
            SELECT id, genre_name
            FROM genre
            WHERE id = ?
            LIMIT 1;
            """),
    DELETE_SQL ("""
            DELETE FROM genre
            WHERE id = ?;
            """),
    FIND_ALL_SQL("""
            SELECT id, genre_name
            FROM genre;
            """),
    SAVE_SQL("""
            INSERT INTO genre (genre_name)
            VALUES (?);
            """),
    UPDATE_SQL ("""
            UPDATE genre
            SET genre_name = ?
            WHERE id = ?;
            """),
    EXISTS_BY_ID_SQL("""
            SELECT EXISTS(
                SELECT 1
                FROM genre
                WHERE id = ?
                LIMIT 1);
            """);

    private final String query;

    public String getQuery() {
        return query;
    }

    GenreSQLQuery(String query) {
        this.query = query;
    }
}
