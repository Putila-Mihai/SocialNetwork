package com.example.socialnetworkapp.repository;

import com.example.socialnetworkapp.domain.Cerere;
import com.example.socialnetworkapp.domain.Status;
import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.domain.validators.ValidatorCerere;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CerereDBRepository implements Repository<Long, Cerere> {
    private String url;
    private String username;
    private String password;
    private UserDBRepository repository;
    private ValidatorCerere validator;

    public CerereDBRepository(String url, String username, String password, UserDBRepository repository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.repository = repository;
    }
    @Override
    public Optional<Cerere> findOne(Long id) {

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from cerere " +
                    "where id = ?");

        ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long id_ = resultSet.getLong("id");
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                Status status= Status.valueOf(resultSet.getString("status"));

                Optional<User> u1 = repository.findOne(id1);
                Optional<User> u2 = repository.findOne(id2);
                Cerere prietenie = new Cerere(u1, u2, status);
                prietenie.setId(id_);
                return Optional.ofNullable(prietenie);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Iterable<Cerere> findAll() {
        Set<Cerere> cereri = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from cerere");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id_ = resultSet.getLong("id");
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                Status status= Status.valueOf(resultSet.getString("status"));

                Optional<User> u1 = repository.findOne(id1);
                Optional<User> u2 = repository.findOne(id2);

                if (u1.isPresent() && u2.isPresent()) {
                    Cerere cerere = new Cerere(u1, u2, status);
                    cerere.setId(id_);
                    cereri.add(cerere);
                }

            }
            return cereri;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Cerere> save(Cerere entity) {
        //validator.validate(entity);
        String insertSQL="insert into cerere (id,id1,id2,status) values(?,?,?,?)";
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement=connection.prepareStatement(insertSQL);)
        {
            statement.setLong(1,entity.getId());
            statement.setLong(2,entity.getUser1().getId());
            statement.setLong(3,entity.getUser2().getId());
            statement.setString(4,entity.getStatus().toString());
            int response=statement.executeUpdate();
            return response==0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Cerere> delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        String deleteSQL="delete from cerere where id=?";
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(deleteSQL);
        ) {
            statement.setLong(1, id);

            Optional<Cerere> cerere = findOne(id);

            int response = 0;
            if (cerere.isPresent()) {
                response = statement.executeUpdate();
            }

            return response == 0 ? Optional.empty() : cerere;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Cerere> update(Cerere entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null!");
        }
            String updateSQL = "update cerere set id1=?,id2=?,status=? where id=?";
            try (var connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(updateSQL);) {
                statement.setLong(1, entity.getUser1().getId());
                statement.setLong(2, entity.getUser2().getId());
                statement.setString(3, entity.getStatus().toString());
                statement.setLong(4, entity.getId());

                int response = statement.executeUpdate();
                return response == 0 ? Optional.of(entity) : Optional.empty();
            } catch (SQLException e) {
                throw new RuntimeException("DB error");
            }
    }

}
