package com.example.socialnetworkapp.repository;

import com.example.socialnetworkapp.domain.User;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDBRepository implements Repository<Long, User> {

    protected String url;
    protected String username;
    protected String password;

//    private Validator<User> validator;


    public UserDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<User> findOne(Long longID) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users where id = ?");

        ) {
            statement.setLong(1, longID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String passwordHash = resultSet.getString("password_hash");
                User u = new User(firstName, lastName,email,passwordHash);
                u.setId(longID);
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String passwordHash = resultSet.getString("password_hash");
                User user = new User(firstName, lastName, email, passwordHash);
                user.setId(id);
                users.add(user);

            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> save(User entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO users (id, first_name, last_name,password_hash,email) VALUES (?, ?, ?,?,?)")
        ) {
            statement.setLong(1, entity.getId());
            statement.setString(2, entity.getfirstName());
            statement.setString(3, entity.getLastName());
            statement.setString(4, entity.getPassword());
            statement.setString(5, entity.getEmail());
            int rowsInserted = statement.executeUpdate();
            updatePassword(entity,entity.getPassword());
            if (rowsInserted > 0) {
                return Optional.of(entity);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updatePassword(User user, String newPassword) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             CallableStatement statement = connection.prepareCall("SELECT update_user_password(?, ?)")
        ) {
            statement.setLong(1, user.getId());

            statement.setString(2, newPassword);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> delete(Long aLong) {
        Optional<User> check = findOne(aLong);
        if (check.isPresent()) {
            try (
                    Connection connecton = DriverManager.getConnection(url, username, password);
                    PreparedStatement statement = connecton.prepareStatement(
                            "delete from users where id = ?")
            ) {
                statement.setLong(1, aLong);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Database Error");
            }
            return check;
        } else return Optional.empty();
    }

    @Override
    public Optional<User> update(User entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE users SET first_name = ?, last_name = ? WHERE id = ?")
        ) {
            statement.setString(1, entity.getfirstName());
            statement.setString(2, entity.getLastName());
            statement.setLong(3, entity.getId());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                return Optional.of(entity);
            } else {
                return Optional.empty(); // Indicates that the update did not affect any rows (user with given ID not found)
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<User> getByName(String nume) {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users WHERE first_name LIKE '%' || ? || '%' OR last_name LIKE '%'|| ? || '%'");

        ) {
            statement.setString(1, nume);
            statement.setString(2, nume);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User user = new User(firstName, lastName);
                user.setId(id);
                users.add(user);
            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
