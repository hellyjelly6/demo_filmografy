package org.example.repository.impl;

import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImpl;
import org.example.exception.OperationException;
import org.example.model.ActorEntity;
import org.example.model.MovieEntity;
import org.example.repository.ActorToMovieRepository;
import org.example.repository.SQLQuery.ActorSQLQuery;
import org.example.repository.SQLQuery.ActorToMovieSQLQuery;
import org.example.repository.mapper.ActorResultSetMapper;
import org.example.repository.mapper.MovieResultSetMapper;
import org.example.repository.mapper.impl.ActorResultSetMapperImpl;
import org.example.repository.mapper.impl.MovieResultSetMapperImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActorToMovieRepositoryImpl implements ActorToMovieRepository {
    private final ConnectionManager connectionManager;
    private final MovieResultSetMapper movieResultSetMapper;
    private final ActorResultSetMapper actorResultSetMapper;

    public ActorToMovieRepositoryImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.movieResultSetMapper = new MovieResultSetMapperImpl();
        this.actorResultSetMapper = new ActorResultSetMapperImpl();
    }

    public ActorToMovieRepositoryImpl() {
        this.connectionManager = new ConnectionManagerImpl();
        this.movieResultSetMapper = new MovieResultSetMapperImpl();
        this.actorResultSetMapper = new ActorResultSetMapperImpl();
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }


    @Override
    public boolean deleteByMovieId(Long id) {
        boolean result;
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(ActorToMovieSQLQuery.DELETE_BY_MOVIE_ID_SQL.getQuery())) {

            preparedStatement.setLong(1, id);
            result = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new OperationException("Error deleting by movie id", e);
        }
        return result;
    }

    @Override
    public boolean deleteByActorId(Long id) {
        boolean result;
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(ActorToMovieSQLQuery.DELETE_BY_ACTOR_ID_SQL.getQuery())) {
            preparedStatement.setLong(1, id);
            result = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new OperationException("Error deleting by actor id", e);
        }
        return result;
    }

    @Override
    public List<MovieEntity> findMoviesByActorId(Long id) {
        List<MovieEntity> moviesList = new ArrayList<>();
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(ActorToMovieSQLQuery.FIND_ALL_BY_ACTOR_ID_SQL.getQuery())){

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                moviesList.add(movieResultSetMapper.map(resultSet, this.connectionManager));
            }
        } catch (SQLException e) {
            throw new OperationException("Error finding all movies by actor id", e);
        }
        return moviesList;
    }

    @Override
    public List<ActorEntity> findActorsByMovieId(Long id) {
        List<ActorEntity> actorList = new ArrayList<>();
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(ActorToMovieSQLQuery.FIND_ALL_BY_MOVIE_ID_SQL.getQuery())){

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                actorList.add(actorResultSetMapper.map(resultSet));
            }
        } catch (SQLException e) {
            throw new OperationException("Error finding all actors by movie id", e);
        }
        return actorList;
    }

    @Override
    public void saveActorsToMovieByUserName(Long movieId, ActorEntity actor) {
        Long actorId = 0L;
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(ActorSQLQuery.FIND_ACTOR_ID_BY_ACTOR_NAME.getQuery())){

            preparedStatement.setString(1, actor.getFirstName());
            preparedStatement.setString(2, actor.getLastName());

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                actorId = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new OperationException("Error finding actor id by actor name", e);
        }

        if (actorId == 0L) {
            throw new OperationException("Actor not found");
        }
        else{
            try(Connection connection = connectionManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(ActorToMovieSQLQuery.SAVE_ACTORS_TO_MOVIE_BY_USER_NAME_SQL.getQuery())) {
                preparedStatement.setLong(1, movieId);
                preparedStatement.setLong(2, actorId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new OperationException("Error saving reference actor_movie", e);
            }
        }

    }
}
