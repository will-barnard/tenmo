package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int findIdByUsername(String username) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null) {
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public String findUsernameById(int id) {
        String sql = "SELECT username FROM tenmo_user WHERE user_id = ?;";
        String username = jdbcTemplate.queryForObject(sql, String.class, id);
        if (username != null) {
            return username;
        } else {
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }
        return users;
    }
    @Override
    public List<String> findAllUsernames() {
        List<String> usernames = new ArrayList<>();
        String sql = "SELECT username\n" +
                "FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            usernames.add(results.getString("username"));
        }
        return usernames;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public int create(String username, String password) {

        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        int newUserId = 0;

        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
        } catch (DataAccessException e) {
            System.out.println("Something went wrong creating a user");
        }

        return newUserId;

    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }
}
