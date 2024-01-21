package com.example.socialnetworkapp.repository;

import com.example.socialnetworkapp.domain.User;
import com.example.socialnetworkapp.repository.paging.Page;
import com.example.socialnetworkapp.repository.paging.Pageable;

import java.sql.*;
import java.util.ArrayList;

public class UserDBPageRepository extends UserDBRepository {


    public UserDBPageRepository(String url, String username, String password) {
        super(url, username, password);
    }

    public boolean checkpassword(String email, String password) {
        try (Connection connection = DriverManager.getConnection(url, username, this.password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, password_hash FROM users WHERE email = ?")
        ) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    long userId = resultSet.getLong("id");
                    String storedPasswordHash = resultSet.getString("password_hash");

                    return checkPasswordEquality(userId, password, storedPasswordHash);
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkPasswordEquality(long userId, String providedPassword, String storedPasswordHash) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             CallableStatement statement = connection.prepareCall("{ ? = call check_password_equality(?, ?) }")
        ) {
            // Set the user ID
            statement.setLong(2, userId);

            // Set the provided password
            statement.setString(3, providedPassword);

            // Register the output parameter for the function return value
            statement.registerOutParameter(1, Types.BOOLEAN);

            // Execute the function
            statement.execute();
            boolean a = statement.getBoolean(1);
            // Return the result of the check_password_equality function
            return statement.getBoolean(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Page<User> findAll(Pageable pageable) {
        ArrayList<User> users = new ArrayList<User>();
        try (
                Connection connecton = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connecton.prepareStatement("select * from users ORDER BY id LIMIT ? OFFSET ? ")
        ) {
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, pageable.getPageNumber() * pageable.getPageSize());
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                User u = new User(results.getString("first_name"), results.getString("last_name"));
                users.add(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database Error");
        }

        return new Page<>(pageable, users);
}

}