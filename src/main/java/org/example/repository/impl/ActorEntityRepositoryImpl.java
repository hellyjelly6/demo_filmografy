package org.example.repository.impl;

import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImpl;
import org.example.model.ActorEntity;
import org.example.repository.ActorEntityRepository;
import org.example.repository.ActorToMovieEntityRepository;
import org.example.repository.SQLQuery.ActorSQLQuery;
import org.example.repository.mapper.ActorResultSetMapper;
import org.example.repository.mapper.impl.ActorResultSetMapperImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActorEntityRepositoryImpl implements ActorEntityRepository {
    private final ConnectionManager connectionManager;
    private final ActorResultSetMapper actorResultSetMapper;
    private final ActorToMovieEntityRepository actorToMovieEntityRepository;

    public ActorEntityRepositoryImpl(ConnectionManagerImpl connectionManager) {
        this.connectionManager = connectionManager;
        this.actorResultSetMapper = new ActorResultSetMapperImpl();
        this.actorToMovieEntityRepository = new ActorToMovieEntityRepositoryImpl(this.connectionManager);
    }

    public ActorEntityRepositoryImpl() {
        this.connectionManager = new ConnectionManagerImpl();
        this.actorResultSetMapper = new ActorResultSetMapperImpl();
        this.actorToMovieEntityRepository = new ActorToMovieEntityRepositoryImpl(this.connectionManager);
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }



    @Override
    public ActorEntity findById(Long id) {
        ActorEntity actorEntity = null;
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(ActorSQLQuery.FIND_BY_ID_SQL.getQuery())) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                actorEntity = actorResultSetMapper.map(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding actor by id", e);
        }
        return actorEntity;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean result = false;
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(ActorSQLQuery.DELETE_SQL.getQuery())) {

            actorToMovieEntityRepository.deleteByActorId(id);

            preparedStatement.setLong(1, id);
            int affectedRows = preparedStatement.executeUpdate();

            result =  affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting actor by id" + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<ActorEntity> findAll()  {
        List<ActorEntity> actorList = new ArrayList<>();
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(ActorSQLQuery.FIND_ALL_SQL.getQuery())){

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                actorList.add(actorResultSetMapper.map(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all actors", e);
        }
        return actorList;
    }

    @Override
    public ActorEntity save(ActorEntity actorEntity)  {
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(ActorSQLQuery.SAVE_SQL.getQuery(), Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, actorEntity.getFirstName());
            preparedStatement.setString(2, actorEntity.getLastName());
            preparedStatement.setDate(3, actorEntity.getBirthDate());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                Long id = generatedKeys.getLong(1);
                actorEntity = new ActorEntity(
                        id,
                        actorEntity.getFirstName(),
                        actorEntity.getLastName(),
                        actorEntity.getBirthDate(),
                        null
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error saving actor",e);
        }
        return actorEntity;
    }

    @Override
    public ActorEntity update(ActorEntity actorEntity)  {
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(ActorSQLQuery.UPDATE_SQL.getQuery())){
            preparedStatement.setString(1, actorEntity.getFirstName());
            preparedStatement.setString(2, actorEntity.getLastName());
            preparedStatement.setDate(3, actorEntity.getBirthDate());
            preparedStatement.setLong(4, actorEntity.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating actor", e);
        }
        return actorEntity;
    }

    @Override
    public boolean exists(Long id) {
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(ActorSQLQuery.EXISTS_BY_ID_SQL.getQuery())){
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking actor exists", e);
        }
        return false;
    }
}

