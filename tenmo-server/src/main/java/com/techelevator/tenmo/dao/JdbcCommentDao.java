package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcCommentDao implements CommentDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Comment getCommentByCommentId(int id){

        Comment comment = null;
        String sql = "SELECT * " +
                "from transfer_comments " +
                "where comment_id = ?;";

        try {

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
            if (rowSet.next()) {
                comment = mapRowToComment(rowSet);
            }

        } catch (Exception e) {
            System.out.println("Something went wrong getting a comment");
        }

        return comment;

    }

    @Override
    public void createComment(Comment comment) {

        String sql = "INSERT INTO transfer_comments (transfer_id, commenter_id, comment_content, comment_time) " +
                "VALUES (?, ?, ?, ?);";

        try {

            int commentId = 0;
            commentId = jdbcTemplate.queryForObject(sql, int.class, comment.getTransferId(), comment.getCommenterId(), comment.getCommentContent(), comment.getCommentTime());
            comment.setCommentId(commentId);
            if (!comment.equals(getCommentByCommentId(commentId))) {
                throw new Exception("Created comment not equal to recorded comment");
            }

        } catch(Exception e) {
            System.out.println("Something went wrong creating a comment");
        }
    }

    @Override
    public List<Comment> getCommentsByTransferId(int id){

        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT * " +
                "from transfer_comments " +
                "where transfer_id = ? " +
                "order by comment_id desc;";

        try {

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
            while (rowSet.next()) {
                comments.add(mapRowToComment(rowSet));
            }

        } catch (Exception e) {
            System.out.println("Something went wrong getting a comment");
        }

        return comments;

    }

    private Comment mapRowToComment(SqlRowSet rs) {
        Comment comment = new Comment();
        comment.setCommentId(rs.getInt("comment_id"));
        comment.setTransferId(rs.getInt("transfer_id"));
        comment.setCommenterId(rs.getInt("commenter_id"));
        comment.setCommentContent(rs.getString("comment_content"));
        comment.setCommentTime(rs.getTimestamp("comment_time").toLocalDateTime());

        return comment;
    }

}
