package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Comment;

import java.util.List;

public interface CommentDao {

    Comment getCommentByCommentId(int id);
    void createComment(Comment comment);
    List<Comment> getCommentsByTransferId(int id);

}
