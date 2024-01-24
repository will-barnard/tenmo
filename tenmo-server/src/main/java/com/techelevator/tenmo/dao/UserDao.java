package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.util.List;

public interface UserDao {

    String findUsernameById(int id);

    List<User> findAll();

    List<String> findAllUsernames();

    User findByUsername(String username);

    int findIdByUsername(String username);

    int create(String username, String password);
}
