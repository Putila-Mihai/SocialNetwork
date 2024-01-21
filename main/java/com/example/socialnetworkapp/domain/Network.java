package com.example.socialnetworkapp.domain;

import com.example.socialnetworkapp.repository.*;

import java.util.ArrayList;

public class Network {
    ArrayList<Friendship> friendships = getFriendshipList();
    private final Repository<Long, User> userRepository;

    public Network(Repository<Long, User> userRepository) {
        this.userRepository = userRepository;
        friendships = getFriendshipList();
    }

    ArrayList<Friendship> getFriendshipList() {
        ArrayList<Friendship> friendships = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            for (User friend : user.getFriends()) {
                Friendship friendship = new Friendship();
                friendship.setId(new Tuple<Long, Long>(user.getId(), friend.getId()));
                friendships.add(friendship);
            }
        }

        return friendships;
    }
}
