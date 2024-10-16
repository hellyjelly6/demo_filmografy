package org.example.repository.impl;

import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImpl;
import org.example.exception.OperationException;
import org.example.model.MovieEntity;
import org.example.repository.ActorToMovieRepository;
import org.example.repository.MovieRepository;
import org.example.repository.SQLQuery.MovieSQLQuery;
import org.example.repository.mapper.MovieResultSetMapper;
import org.example.repository.mapper.impl.MovieResultSetMapperImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieRepositoryImpl implements MovieRepository {
    private final ConnectionManager connectionManager;
    private final MovieResultSetMapper resultSetMapper;
    private final ActorToMovieRepository actorToMovieRepository;

    public MovieRepositoryImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.resultSetMapper = new MovieResultSetMapperImpl();
        this.actorToMovieRepository = new ActorToMovieRepositoryImpl(this.connectionManager);
    }

    public MovieRepositoryImpl() {
        this.connectionManager = new ConnectionManagerImpl();
        this.resultSetMapper = new MovieResultSetMapperImpl();
        this.actorToMovieRepository = new ActorToMovieRepositoryImpl(this.connectionManager);
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    @Override
    public MovieEntity findById(Long id)  {
        MovieEntity movieEntity = null;
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(MovieSQLQuery.FIND_BY_ID_SQL.getQuery())){
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                movieEntity = resultSetMapper.map(resultSet, this.connectionManager);
            }
        } catch (SQLException e) {
            throw new OperationException("Error finding movie by id", e);
        }
        return movieEntity;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean isDelete = false;
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(MovieSQLQuery.DELETE_SQL.getQuery())){

            actorToMovieRepository.deleteByMovieId(id);

            preparedStatement.setLong(1, id);
            isDelete = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new OperationException("Error deleting movie by id"+e.getMessage(), e);
        }
        return isDelete;
    }

    @Override
    public List<MovieEntity> findAll()  {
        List<MovieEntity> movieEntities = new ArrayList<>();
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(MovieSQLQuery.FIND_ALL_SQL.getQuery())){

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                movieEntities.add(resultSetMapper.map(resultSet, this.connectionManager));
            }
        } catch (SQLException e) {
            throw new OperationException("Error finding all movies", e);
        }
        return movieEntities;
    }

    @Override
    public MovieEntity save(MovieEntity movieEntity)  {
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(MovieSQLQuery.SAVE_SQL.getQuery(), Statement.RETURN_GENERATED_KEYS)){

            preparedStatement.setString(1, movieEntity.getTitle());
            preparedStatement.setInt(2, movieEntity.getReleaseYear());
            if(movieEntity.getGenre() != null){
                preparedStatement.setLong(3, movieEntity.getGenre().getId());
            }
            else{
                preparedStatement.setNull(3, Types.BIGINT);
            }
            preparedStatement.setLong(3, movieEntity.getGenre().getId());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                Long id = generatedKeys.getLong(1);
                movieEntity = new MovieEntity(
                        id,
                        movieEntity.getTitle(),
                        movieEntity.getReleaseYear(),
                        movieEntity.getGenre(),
                        new ArrayList<>()
                );

            }

        } catch (SQLException e) {
            throw new OperationException("Error saving movie", e);
        }
        return movieEntity;
    }

    @Override
    public MovieEntity update(MovieEntity movieEntity)  {
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(MovieSQLQuery.UPDATE_SQL.getQuery())){

            preparedStatement.setString(1, movieEntity.getTitle());
            preparedStatement.setInt(2, movieEntity.getReleaseYear());
            if (movieEntity.getGenre() == null) {
                preparedStatement.setNull(3, Types.BIGINT);
            } else {
                preparedStatement.setLong(3, movieEntity.getGenre().getId());
            }
            preparedStatement.setLong(4, movieEntity.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new OperationException("Error updating movie", e);
        }
        return movieEntity;
    }

    @Override
    public boolean exists(Long id) {
        boolean isExists = false;
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(MovieSQLQuery.EXISTS_BY_ID_SQL.getQuery())) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getBoolean(1) ;
            }
        } catch (SQLException e) {
            throw new OperationException("Error checking movie exists", e);
        }
        return isExists;
    }

    @Override
    public List<MovieEntity> findMoviesByGenreId(Long id) {
        List<MovieEntity> moviesList = new ArrayList<>();
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(MovieSQLQuery.FIND_BY_GENRE_ID.getQuery())){

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                moviesList.add(resultSetMapper.map(resultSet, this.connectionManager));
            }
        } catch (SQLException e) {
            throw new OperationException("Error finding all movies by actor id", e);
        }
        return moviesList;
    }

    @Override
    public boolean deleteConstraintByGenreId(Long id) {
        boolean isUpdated = false;
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(MovieSQLQuery.DELETE_CONSTRAINT_BY_GENRE_ID.getQuery())){
            preparedStatement.setLong(1, id);
            isUpdated = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new OperationException("Error deleting movie by id"+e.getMessage(), e);
        }
        return isUpdated;
    }
}