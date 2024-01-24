package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Friends;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Component
public class JbdcFriendsDao implements FriendsDao{

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Friends getFriends(int user, int friend) {

        Friends friends = null;
        String sql = "SELECT * " +
                "FROM friends " +
                "WHERE (user_a = ? and user_b = ?) or (user_a = ? and user_b = ?);";

        try {

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, user, friend, friend, user);
            if (rowSet.next()) {
                friends = mapRowToFriends(rowSet);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return friends;

    }
    @Override
    public void createFriends(Friends friends) {

        Friends newFriends = null;
        String sql = "INSERT INTO friends (user_a, user_b, is_confirmed, is_active) " +
                "VALUES (?, ?, ?, ?)";
        try {

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, friends.getUserA(), friends.getUserB(), friends.isConfirmed(), friends.isActive());
            newFriends = mapRowToFriends(rowSet);
            if (!friends.equals(newFriends)) {
                throw new Exception("error creating correct friends record in database");
            }

        } catch(Exception e) {
            System.out.println(e);
        }

    }

    @Override
    public void updateFriends(Friends friends) {

        String sql = "UPDATE friends " +
                "SET user_a = ?, user_b = ?, is_confirmed = ?, is_active = ? " +
                "WHERE (user_a = ? and user_b = ?) or (user_a = ? and user_b = ?);";

        try {

            int rowsAffected = 0;
            rowsAffected = jdbcTemplate.update(sql, friends.getUserA(), friends.getUserB(),
                                                friends.isConfirmed(), friends.isActive(),
                                                friends.getUserA(), friends.getUserB(),
                                                friends.getUserB(), friends.getUserA());

            if (rowsAffected == 0) {
                throw new Exception("Something went wrong, no rows affected");
            }
            if (rowsAffected > 1) {
                throw new Exception("Something went wrong, more than one row affected");
            }

        } catch(Exception e) {
            System.out.println(e);
        }

    }

    @Override
    public List<Friends> getFriendslist(int user) {

        List<Friends> friendslist = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM friends " +
                "WHERE user_a = ? OR user_b = ?;";

        try {

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, user, user);
            while (rowSet.next()) {
                friendslist.add(mapRowToFriends(rowSet));
            }

        } catch(Exception e) {
            System.out.println(e);
        }

        return friendslist;

    }

    @Override
    public List<Friends> getRequests(int user) {

        List<Friends> requestlist = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM friends " +
                "WHERE (user_a = ? OR user_b = ?) AND is_confirmed = false AND is_active = true;";

        try {

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, user, user);
            while (rowSet.next()) {
                requestlist.add(mapRowToFriends(rowSet));
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return requestlist;
    }



    private Friends mapRowToFriends(SqlRowSet rs) {
        Friends friends = new Friends();
        friends.setUserA(rs.getInt("user_a"));
        friends.setUserB(rs.getInt("user_b"));
        friends.setConfirmed(rs.getBoolean("is_confirmed"));
        friends.setActive(rs.getBoolean("is_active"));
        return friends;
    }
}
