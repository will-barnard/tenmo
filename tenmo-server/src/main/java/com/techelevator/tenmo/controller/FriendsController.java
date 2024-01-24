package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.FriendsDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Friends;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class FriendsController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TransferDao transferDao;
    @Autowired
    private FriendsDao friendsDao;
    private static final String API_BASE_PATH = "/Friends";


    @RequestMapping(path = API_BASE_PATH + "/add", method = RequestMethod.POST)
    public String add(@RequestParam String user, Principal principal) {

        int userId = userDao.findIdByUsername(principal.getName());
        int friendId = userDao.findIdByUsername(user);

        if (friendId == -1) {
            return "Not a valid user";
        }

        Friends friends = friendsDao.getFriends(userId, friendId);

        if (friends == null) {
            friendsDao.createFriends(new Friends(userId, friendId, false, false));
        }

        if (friends != null) {

            if (friends.isConfirmed()) {
                return "You are already friends with " + user;
            }

            if (friends.isActive() && !friends.isConfirmed()) {
                friends.setConfirmed(true);
                friendsDao.updateFriends(friends);
                return "You are now friends with " + user;
            }

            if (!friends.isActive()) {
                friends.setActive(true);
                friendsDao.updateFriends(friends);
                return "Friend request sent to " + user;
            }

        }

        return null;


    }

    @RequestMapping(path = API_BASE_PATH + "/remove", method = RequestMethod.POST)
    public String remove(@RequestParam String user, Principal principal) {

        int userId = userDao.findIdByUsername(principal.getName());
        int friendId = userDao.findIdByUsername(user);

        if (friendId == -1) {
            return "Not a valid user";
        }

        Friends friends = friendsDao.getFriends(userId, friendId);

        if (friends == null) {
            friendsDao.createFriends(new Friends(userId, friendId, false, false));
        }

        if (friends != null) {

            if (friends.isConfirmed()) {
                friends.setActive(false);
                friends.setConfirmed(false);
                return "You are no longer friends with " + user;
            }

            if (friends.isActive() && !friends.isConfirmed()) {
                friends.setActive(false);
                friendsDao.updateFriends(friends);
                return "You have rejected a friend request from " + user;
            }

            if (!friends.isActive()) {
                return "You are not friends with " + user;
            }

        }

        return null;
    }

    @RequestMapping(path = API_BASE_PATH + "/friendslist", method = RequestMethod.GET)
    public String friendslist(Principal principal) {

        int userId = userDao.findIdByUsername(principal.getName());
        List<String> friendslistStr = new ArrayList<>();
        friendslistStr.add("Friendslist:");
        List<Friends> friendslistObj = friendsDao.getFriendslist(userId);

        for (Friends friend : friendslistObj) {
            if (friend.isConfirmed()) {
                if (friend.getUserA() == userId) {
                    friendslistStr.add(userDao.findUsernameById(friend.getUserB()));
                } else if (friend.getUserB() == userId) {
                    friendslistStr.add(userDao.findUsernameById(friend.getUserA()));
                }
            }
        }

        if (friendslistStr.size() == 0) {
            return "You have no friends";
        }

        String result = String.join("\n", friendslistStr);


        return result;

    }

    @RequestMapping(path = API_BASE_PATH + "/requests", method = RequestMethod.GET)
    public List<String> requests(Principal principal) {

        int userId = userDao.findIdByUsername(principal.getName());
        List<String> requestsStr = new ArrayList<>();
        List<Friends> requestsObj = friendsDao.getRequests(userId);

        for (Friends friend : requestsObj) {

            if (friend.getUserA() == userId) {
                requestsStr.add(userDao.findUsernameById(friend.getUserB()));
            } else if (friend.getUserB() == userId) {
                requestsStr.add(userDao.findUsernameById(friend.getUserA()));
            }

        }

        if (requestsStr.size() == 0) {
            requestsStr.add("You have no friend requests");
        }

        return requestsStr;

    }


}
