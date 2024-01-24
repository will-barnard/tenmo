package com.techelevator.tenmo.model;

import java.time.LocalDateTime;

public class Comment {

    private int commentId;
    private int transferId;
    private int commenterId;
    private String commentContent;
    private LocalDateTime commentTime;

    public Comment() {}

    public Comment(int commentId, int transferId, int commenterId, String commentContent, LocalDateTime commentTime) {
        this.commentId = commentId;
        this.transferId = transferId;
        this.commenterId = commenterId;
        this.commentContent = commentContent;
        this.commentTime = commentTime;
    }

    public Comment(int transferId, int commenterId, String commentContent, LocalDateTime commentTime) {
        this.transferId = transferId;
        this.commenterId = commenterId;
        this.commentContent = commentContent;
        this.commentTime = commentTime;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(int commenterId) {
        this.commenterId = commenterId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public LocalDateTime getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(LocalDateTime commentTime) {
        this.commentTime = commentTime;
    }
}
