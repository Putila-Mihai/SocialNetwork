package com.example.socialnetworkapp.repository;

import com.example.socialnetworkapp.domain.Mesaj;
import com.example.socialnetworkapp.domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class MesajDBRepository implements Repository<Long, Mesaj>{
    private String url;
    private String username;
    private String password;
    private UserDBRepository repository;

    public MesajDBRepository(String url, String username, String password, UserDBRepository repository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.repository = repository;
    }

    @Override
    public Optional<Mesaj> findOne(Long id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from mesaj " +
                    "where id = ?");

        ) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long id_ = resultSet.getLong("id");
                String textmesaj = resultSet.getString("textmesaj");
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                Timestamp timestamp = resultSet.getTimestamp("din");
                LocalDateTime din = timestamp.toLocalDateTime();
                Long reply = resultSet.getLong("reply");

                Optional<User> u1 = repository.findOne(id1);
                Optional<User> u2 = repository.findOne(id2);

                Mesaj mesaj = new Mesaj(textmesaj,u1,u2,din,reply);
                mesaj.setId(id_);
                return Optional.ofNullable(mesaj);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Mesaj> findAll() {
        Set<Mesaj> mesaje = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from mesaj");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id_ = resultSet.getLong("id");
                String textmesaj = resultSet.getString("textmesaj");
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                Timestamp timestamp = resultSet.getTimestamp("din");
                LocalDateTime din = timestamp.toLocalDateTime();
                Long reply = resultSet.getLong("reply");

                Optional<User> u1 = repository.findOne(id1);
                Optional<User> u2 = repository.findOne(id2);
                if (u1.isPresent() && u2.isPresent()) {
                    Mesaj mesaj = new Mesaj(textmesaj, u1.get(), u2.get(), din, reply);
                    mesaj.setId(id_);
                    mesaje.add(mesaj);
                }
            }
            return mesaje;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Mesaj> save(Mesaj entity) {
        //mesajValidator.validate(entity);
        String insertSQL="insert into mesaj (id, textmesaj, id1, id2, din, reply) values(?, ?, ?, ?, ?, ?)";
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement=connection.prepareStatement(insertSQL);)
        {
            statement.setLong(1, entity.getId());
            statement.setString(2, entity.getText());
            statement.setLong(3, entity.getFrom().getId());
            statement.setLong(4, entity.getTo().getId());
            Timestamp timestamp = Timestamp.valueOf(entity.getData());
            statement.setTimestamp(5,timestamp);
            statement.setLong(6, entity.getReply());
            int response=statement.executeUpdate();
            return response==0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Mesaj> delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        String deleteSQL="delete from mesaj where id=?";
        try (var connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(deleteSQL);
        ) {
            statement.setLong(1, id);

            Optional<Mesaj> mesaj = findOne(id);

            int response = 0;
            if (mesaj.isPresent()) {
                response = statement.executeUpdate();
            }

            return response == 0 ? Optional.empty() : mesaj;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Mesaj> update(Mesaj entity) {
        //mesajValidator.validate(entity);
        if(entity==null)
        {
            throw new IllegalArgumentException("Entity cannot be null!");
        }
        String updateSQL="update mesaj set textmesaj=?,id1=?,id2=?,din=?,reply=? where id=?";
        try(var connection= DriverManager.getConnection(url, username, password);
            PreparedStatement statement=connection.prepareStatement(updateSQL);)
        {
            statement.setString(1, entity.getText());
            statement.setLong(2, entity.getFrom().getId());
            statement.setLong(3, entity.getTo().getId());
            statement.setTimestamp(4, Timestamp.valueOf(entity.getData()));
            statement.setLong(5, entity.getReply());
            statement.setLong(6,entity.getId());

            int response= statement.executeUpdate();
            return response==0 ? Optional.of(entity) : Optional.empty();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
