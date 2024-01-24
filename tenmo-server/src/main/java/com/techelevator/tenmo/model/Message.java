package com.techelevator.tenmo.model;

import java.time.LocalDateTime;

public class Message {

    private int messageId;
    private int userA;
    private int userB;
    private int messagerId;
    private String messageContent;
    private LocalDateTime messageTime;

    public Message() {}

    public Message(int messageId, int userA, int userB, int messagerId, String messageContent, LocalDateTime messageTime) {
        this.messageId = messageId;
        this.userA = userA;
        this.userB = userB;
        this.messagerId = messagerId;
        this.messageContent = messageContent;
        this.messageTime = messageTime;
    }

    public Message(int userA, int userB, int messagerId, String messageContent, LocalDateTime messageTime) {
        this.userA = userA;
        this.userB = userB;
        this.messagerId = messagerId;
        this.messageContent = messageContent;
        this.messageTime = messageTime;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getUserA() {
        return userA;
    }

    public void setUserA(int userA) {
        this.userA = userA;
    }

    public int getUserB() {
        return userB;
    }

    public void setUserB(int userB) {
        this.userB = userB;
    }

    public int getMessagerId() {
        return messagerId;
    }

    public void setMessagerId(int messagerId) {
        this.messagerId = messagerId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public LocalDateTime getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(LocalDateTime messageTime) {
        this.messageTime = messageTime;
    }
}
