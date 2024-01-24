package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Comment;
import com.techelevator.tenmo.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcMessageDao implements MessageDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Message getMessageById(int messageId) {
        Message message = null;
        String sql = "SELECT * " +
                "from messages " +
                "where message_id = ?;";

        try {

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, messageId);
            if (rowSet.next()) {
                message = mapRowToMessage(rowSet);
            }

        } catch (Exception e) {
            System.out.println("Something went wrong getting a message");
        }

        return message;
    }

    @Override
    public void createMessage(Message message) {

        String sql = "INSERT INTO messages (user_a, user_b, messager_id, message_content, message_time) " +
                "VALUES (?, ?, ?, ?, ?);";

        try {

            int messageId = 0;
            messageId = jdbcTemplate.queryForObject(sql, int.class,
                    message.getUserA(), message.getUserB(), message.getMessagerId(),
                    message.getMessageContent(), message.getMessageTime());
            message.setMessageId(messageId);
            if (!message.equals(getMessageById(messageId))) {
                throw new Exception("Created message not equal to recorded comment");
            }

        } catch(Exception e) {
            System.out.println("Something went wrong creating a comment");
        }
    }

    @Override
    public List<Message> getMessagesFromUser(int userId, int friendId) {

        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * " +
                "from messages " +
                "where (user_a = ? and user_b = ?) or (user_a = ? and user_b = ?) " +
                "order by message_id desc;";

        try {

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId, friendId, friendId, userId);
            while (rowSet.next()) {
                messages.add(mapRowToMessage(rowSet));
            }

        } catch (Exception e) {
            System.out.println("Something went wrong getting a list of messages");
        }

        return messages;
    }
    @Override
    public List<Message> getAllMessagesForUser(int userId) {

        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * " +
                "from messages " +
                "where user_a = ? or user_b = ? " +
                "order by message_id desc;";

        try {

            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId, userId);
            while (rowSet.next()) {
                messages.add(mapRowToMessage(rowSet));
            }

        } catch (Exception e) {
            System.out.println("Something went wrong getting a list of messages");
        }

        return messages;
    }


    private Message mapRowToMessage(SqlRowSet rs) {
        Message message = new Message();
        message.setMessageId(rs.getInt("message_id"));
        message.setUserA(rs.getInt("user_a"));
        message.setUserB(rs.getInt("user_B"));
        message.setMessagerId(rs.getInt("messager_id"));
        message.setMessageContent(rs.getString("message_content"));
        message.setMessageTime(rs.getTimestamp("message_time").toLocalDateTime());

        return message;
    }

}
