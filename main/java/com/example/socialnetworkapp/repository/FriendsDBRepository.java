package com.example.socialnetworkapp.repository;

import com.example.socialnetworkapp.domain.Friendship;
import com.example.socialnetworkapp.domain.Tuple;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendsDBRepository implements Repository<Tuple<Long,Long>, Friendship> {

    private String url;
    private String username;
    private String password;

//    private Validator<User> validator;


    public FriendsDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Friendship> findOne(Tuple<Long,Long> longTuple) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from friendship " +
                    "where user1_id = ? AND user2_id = ?");

        ) {
            statement.setLong(1,longTuple.getLeft());
            statement.setLong(2,longTuple.getRight());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long id1 = resultSet.getLong("user1_id");
                Long id2 = resultSet.getLong("user2_id");
                Friendship f = new Friendship();
                f.setId(new Tuple<Long,Long>(id1,id2));
                return Optional.ofNullable(f);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }


    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendship");
             ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next())
            {
                Long id1 = resultSet.getLong("user1_id");
                Long id2 = resultSet.getLong("user2_id");
                Friendship f = new Friendship();
                f.setId(new Tuple<Long,Long>(id1,id2));
                friendships.add(f);
            }
            return friendships;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Optional<Friendship> save(Friendship friendship) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO friendship (user1_id, user2_id,friendship_date) VALUES (?, ?, ?)")
        ) {
            statement.setLong(1, friendship.getId().getLeft());
            statement.setLong(2, friendship.getId().getRight());
            Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
            statement.setTimestamp(3,timestamp);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                return Optional.of(friendship);
            } else {
                return Optional.empty(); // Indicates that the insert was unsuccessful
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> longLongTuple) {
        Optional<Friendship> check = findOne(longLongTuple);
        if (check.isPresent()) {
            try (
                    Connection connecton = DriverManager.getConnection(url, username, password);
                    PreparedStatement statement = connecton.prepareStatement(
                            "delete from friendship where user1_id = ? And user2_id = ?")
            ) {
                statement.setLong(1, longLongTuple.getLeft());
                statement.setLong(2, longLongTuple.getRight());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Database Error");
            }
            return check;
        } else return Optional.empty();
    }

    @Override
    public Optional<Friendship> update(Friendship friendship) {
        return Optional.empty();
    }
}
