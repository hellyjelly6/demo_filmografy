package org.example.repository.SQLQuery;

public enum ActorSQLQuery {
    FIND_BY_ID_SQL ("""
            SELECT id, first_name, last_name, birthdate
            FROM actor
            WHERE id = ?
            LIMIT 1;
            """),
    DELETE_SQL ("""
            DELETE FROM actor
            WHERE id = ?;
            """),
    FIND_ALL_SQL("""
            SELECT id, first_name, last_name, birthdate
            FROM actor;
            """),
    SAVE_SQL("""
            INSERT INTO actor (first_name, last_name, birthdate)
            VALUES (?, ?, ?);
            """),
    UPDATE_SQL ("""
            UPDATE actor
            SET first_name = ?,
                last_name = ?,
                birthdate = ?
            WHERE id = ?;
            """),
    EXISTS_BY_ID_SQL("""
            SELECT EXISTS(
                SELECT 1
                FROM actor
                WHERE id = ?
                LIMIT 1);
            """),
    FIND_ACTOR_ID_BY_ACTOR_NAME("""
            SELECT id
            FROM actor
            WHERE first_name = ? AND last_name = ?
            LIMIT 1;
            """);


    private final String query;

    ActorSQLQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
