package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.*;
import com.techelevator.tenmo.model.Friends;
import com.techelevator.tenmo.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class MessageController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TransferDao transferDao;
    @Autowired
    private FriendsDao friendsDao;
    @Autowired
    private MessageDao messageDao;
    private static final DateTimeFormatter LOG_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
    private static final String API_BASE_PATH = "/Friends/Messages";


    @RequestMapping(path = API_BASE_PATH, method = RequestMethod.GET)
    public String allMessages(Principal principal) {

        String messages = "";
        int userId = userDao.findIdByUsername(principal.getName());
        List<Message> messageListObj = messageDao.getAllMessagesForUser(userId);
        List<String> messageListStr = new ArrayList<>();

        for (Message message : messageListObj) {
            messageListStr.add(formatMessage(message));
        }
        if (messageListObj.size() == 0) {
            return "You have no messages";
        }
        messages = String.join("\n", messageListStr);

        return messages;

    }

    @RequestMapping(path = API_BASE_PATH + "/{username}", method = RequestMethod.GET)
    public String allMessagesFromUser(@PathVariable String username, Principal principal) {

        String messages = "";
        int userId = userDao.findIdByUsername(principal.getName());
        int friendId = userDao.findIdByUsername(username);
        if (friendId == -1) {
            return "No user exists with username " + username;
        }
        if (userId == friendId) {
            return "You cannot send messages to yourself";
        }
        Friends friends = friendsDao.getFriends(userId, friendId);
        if (!friends.isConfirmed()) {
            return "You are not friends with this user, and cannot send or receive messages.";
        } else {

            List<Message> messageListObj = messageDao.getMessagesFromUser(userId, friendId);
            List<String> messageListStr = new ArrayList<>();

            for (Message message : messageListObj) {
                messageListStr.add(formatMessage(message));
            }
            if (messageListObj.size() == 0) {
                return "You have no message history with this user";

            }

            messages = String.join("\n", messageListStr);

        }

        return messages;

    }

    @RequestMapping(path = API_BASE_PATH + "/{username}", method = RequestMethod.POST)
    public String allMessagesFromUser(@PathVariable String username, @RequestParam String send, Principal principal) {

        int userId = userDao.findIdByUsername(principal.getName());
        int friendId = userDao.findIdByUsername(username);
        if (friendId == -1) {
            return "No user exists with username " + username;
        }
        if (userId == friendId) {
            return "You cannot send messages to yourself";
        }
        Friends friends = friendsDao.getFriends(userId, friendId);
        if (friends == null) {
            return "You cannot send a message to a user you are not friends with";
        }
        if (!friends.isConfirmed()) {
            return "You cannot send a message to a user you are not friends with";
        }

        Message message = new Message(friends.getUserA(), friends.getUserB(), userId, send, LocalDateTime.now());
        boolean success = false;

        try {

            messageDao.createMessage(message);
            success = true;

        } catch(Exception e) {
            System.out.println("Something went wrong sending a message");
        }

        if (success) {
            return "Succesfully send message to " + username;
        } else {
            return "Unable to send message to " + username;
        }

    }


    private String formatMessage(Message message) {

        String str = "";
        String messager = userDao.findUsernameById(message.getMessagerId());
        str += LOG_FORMAT.format(message.getMessageTime()) + " " + messager + ": \"" + message.getMessageContent() + "\"";

        return str;

    }

}
