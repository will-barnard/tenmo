package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Message;

import java.util.List;

public interface MessageDao {

    Message getMessageById(int messageId);
    void createMessage(Message message);
    List<Message> getMessagesFromUser(int userId, int friendId);
    List<Message> getAllMessagesForUser(int userId);

}
