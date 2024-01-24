package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Transfer send(BigDecimal amount, int senderId, int receiverId) {
        Transfer transfer = null;
        String sql = "INSERT INTO transfer_log(sender_id, receiver_id, transfer_amount, send_time,receive_time,is_completed,is_rejected)\n" +
                "VALUES (?,?,?,?,?,true,false) RETURNING transfer_id;";
        try{
           int transferId = jdbcTemplate.queryForObject(sql, int.class,senderId,receiverId,amount, LocalDateTime.now(),LocalDateTime.now());
           transfer = getTransferById(transferId);
        } catch (Exception e){
            System.out.println("something went wrong with a send");
        }
        return transfer;
    }

    @Override
    public Transfer request(BigDecimal amount, int senderId, int receiverId) {
        Transfer transfer = null;
        String sql = "INSERT INTO transfer_log(sender_id, receiver_id, transfer_amount, send_time,receive_time,is_completed,is_rejected)\n" +
                "VALUES (?,?,?,?,?,false,false) RETURNING transfer_id;";
        try{
            int transferId = jdbcTemplate.queryForObject(sql, int.class,senderId,receiverId,amount, LocalDateTime.now(),LocalDateTime.now());
            transfer = getTransferById(transferId);
        } catch (Exception e){
            System.out.println("something went wrong with a request");
        }
        return transfer;
    }

    @Override
    public List<Transfer> pending(int userId) {
        List<Transfer> pendingTransfers = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM transfer_log " +
                "WHERE sender_id = ? AND is_completed = false;";
        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, userId);
            while (rs.next()) {
                pendingTransfers.add(mapRowToTransfer(rs));
            }
        } catch(Exception e) {
            System.out.println("Something went wrong getting pending transfers");
        }
        return pendingTransfers;
    }

    @Override
    public List<Transfer> history(int userId) {
        List<Transfer> history = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM transfer_log " +
                "WHERE sender_id = ? OR receiver_id = ?;";
        try {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, userId, userId);
            while (rs.next()) {
                history.add(mapRowToTransfer(rs));
            }
        } catch(Exception e) {
            System.out.println("Something went wrong getting history");
        }
        return history;
    }

    @Override
    public Transfer getTransferById(int transferId) {
        String sql = "SELECT *\n" +
                "FROM transfer_log\n" +
                "WHERE transfer_id = ?;";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferId);
            if (rowSet.next()) {
                return mapRowToTransfer(rowSet);
            }
        }catch (Exception e){
            System.out.println("something went wrong with get transfer by id");
        }
        return null;
    }

    @Override
    public Transfer approve(int transferId) {
        Transfer transfer = null;
        String sql = "UPDATE transfer_log\n" +
                "SET is_completed = true\n" +
                "WHERE transfer_id = ?;";
        try {
            int rowsAffected;
            rowsAffected = jdbcTemplate.update(sql, transferId);
            if (rowsAffected == 0) {
                throw new Exception("no rows affected");
            }
        } catch(Exception e) {
            System.out.println("Something went wrong approving a transfer");
        }
        return transfer;
    }

    @Override
    public Transfer reject(int transferId) {
        Transfer transfer = null;
        String sql = "UPDATE transfer_log\n" +
                "SET is_rejected = true\n" +
                "WHERE transfer_id = ?;";
        try {
            int rowsAffected;
            rowsAffected = jdbcTemplate.update(sql, transferId);
            if (rowsAffected == 0) {
                throw new Exception("no rows affected");
            }
        } catch(Exception e) {
            System.out.println("Something went wrong rejecting a transfer");
        }
        return transfer;
    }

    @Override
    public boolean verifyUserInTransaction(int transferId, int userId) {
        String sql = "SELECT *\n" +
                "FROM transfer_log\n" +
                "WHERE transfer_id = ?;";
        Transfer transfer = null;
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferId);
            if (rowSet.next()) {
                transfer = mapRowToTransfer(rowSet);
                if (transfer.getReceiverId() == userId || transfer.getSenderId() == userId) {
                    return true;
                }
            }
        }catch (Exception e){
            System.out.println("something went wrong with get transfer by id");
        }
        return false;
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setSenderId(rs.getInt("sender_id"));
        transfer.setReceiverId(rs.getInt("receiver_id"));
        transfer.setTransferAmount(rs.getBigDecimal("transfer_amount"));
        transfer.setSendTime(rs.getTimestamp("send_time").toLocalDateTime());
        transfer.setReceiveTime(rs.getTimestamp("receive_time").toLocalDateTime());
        transfer.setCompleted(rs.getBoolean("is_completed"));
        transfer.setRejected(rs.getBoolean("is_rejected"));
        return transfer;
    }


}
