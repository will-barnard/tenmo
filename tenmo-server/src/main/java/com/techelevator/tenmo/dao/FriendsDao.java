package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Friends;

import java.util.List;

public interface FriendsDao {

    Friends getFriends(int userId, int friend);
    void createFriends(Friends friends);
    void updateFriends(Friends friends);
    List<Friends> getFriendslist(int user);
    List<Friends> getRequests(int user);

}
