package org.example.repository.impl;

import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImpl;
import org.example.exception.OperationException;
import org.example.model.GenreEntity;
import org.example.repository.GenreRepository;
import org.example.repository.MovieRepository;
import org.example.repository.SQLQuery.GenreSQLQuery;
import org.example.repository.mapper.GenreResultSetMapper;
import org.example.repository.mapper.impl.GenreResultSetMapperImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreRepositoryImpl implements GenreRepository {
    private final ConnectionManager connectionManager;
    private final GenreResultSetMapper genreResultSetMapper;
    private final MovieRepository movieRepository;

    public GenreRepositoryImpl() {
        this.connectionManager = new ConnectionManagerImpl();
        this.genreResultSetMapper = new GenreResultSetMapperImpl();
        this.movieRepository = new MovieRepositoryImpl(this.connectionManager);
    }

    public GenreRepositoryImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.genreResultSetMapper = new GenreResultSetMapperImpl();
        this.movieRepository = new MovieRepositoryImpl(this.connectionManager);
    }


    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    @Override
    public GenreEntity findById(Long id)  {
        GenreEntity genreEntity = null;

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GenreSQLQuery.FIND_BY_ID_SQL.getQuery())){
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                genreEntity = genreResultSetMapper.map(resultSet);
            }
        } catch (SQLException e) {
            throw new OperationException("Error finding genre by id", e);
        }
        return genreEntity;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean result = false;
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GenreSQLQuery.DELETE_SQL.getQuery())){

            movieRepository.deleteConstraintByGenreId(id);

            preparedStatement.setLong(1, id);
            int affectedRows = preparedStatement.executeUpdate();

            result =  affectedRows > 0;
        } catch (SQLException e) {
            throw new OperationException("Error deleting genre by id", e);
        }
        return result;
    }

    @Override
    public List<GenreEntity> findAll()  {
        List<GenreEntity> genreEntityList = new ArrayList<>();

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GenreSQLQuery.FIND_ALL_SQL.getQuery())){

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                genreEntityList.add(genreResultSetMapper.map(resultSet));
            }
        } catch (SQLException e) {
            throw new OperationException("Error finding all genres", e);
        }

        return genreEntityList;
    }

    @Override
    public GenreEntity save(GenreEntity genreEntity) {
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GenreSQLQuery.SAVE_SQL.getQuery(), Statement.RETURN_GENERATED_KEYS)){

            preparedStatement.setString(1, genreEntity.getName());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                Long id = generatedKeys.getLong(1);
                genreEntity = new GenreEntity(
                        id,
                        genreEntity.getName(),
                        new ArrayList<>()
                );
            }
        } catch (SQLException e) {
            throw new OperationException("Error saving genre", e);
        }
        return genreEntity;
    }

    @Override
    public GenreEntity update(GenreEntity genreEntity)  {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GenreSQLQuery.UPDATE_SQL.getQuery())){

            preparedStatement.setString(1, genreEntity.getName());
            preparedStatement.setLong(2, genreEntity.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new OperationException("Error updating genre"+genreEntity.getId(), e);
        }
        return genreEntity;
    }

    @Override
    public boolean exists(Long id) {
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(GenreSQLQuery.EXISTS_BY_ID_SQL.getQuery())){
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new OperationException("Error checking genre exists", e);
        }
        return false;
    }
}
