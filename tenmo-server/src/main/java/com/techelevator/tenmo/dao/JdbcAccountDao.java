package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
public class JdbcAccountDao implements AccountDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Account getAccount(int userId) {

        Account account = null;
        String sql = "Select account_id, user_id, balance\n" +
                "From account\n" +
                "Where user_id = ?;";
        try {
            SqlRowSet rowset = jdbcTemplate.queryForRowSet(sql, userId);
            if(rowset.next()) {
                account = mapRowToAccount(rowset);
            }
        } catch (DataAccessException e){
            System.out.println("Something went wrong");
        }

        assert account != null;
        return account;
    }

    @Override
    public int createAccount(int userId) {

        String sql = "INSERT INTO account (user_id, balance) " +
                "VALUES (?, ?) RETURNING account_id";
        int newAccountId = 0;

        try {
            newAccountId = jdbcTemplate.queryForObject(sql, Integer.class, userId, 1000);
        } catch (DataAccessException e) {
            System.out.println("something went wrong creating an account");
        }

        return newAccountId;

    }

    @Override
    public BigDecimal getBalance(int userId) {
        BigDecimal balance = null;
        Account account = getAccount(userId);
        assert account != null;
        return account.getBalance();
    }

    @Override
    public void add(BigDecimal amount, int userId) {
        String sql = "UPDATE account\n" +
                "SET balance = (balance + ?)\n" +
                "WHERE user_id = ?;";
        jdbcTemplate.update(sql,amount, userId);
    }

    @Override
    public void subtract(BigDecimal amount, int userId) {
        String sql = "UPDATE account\n" +
                "SET balance = (balance - ?)\n" +
                "WHERE user_id = ?;";
        jdbcTemplate.update(sql,amount,userId);

    }

    private Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getInt("account_id"));
        account.setUserId(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));

        return account;
    }
}
