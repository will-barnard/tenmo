package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {
    BigDecimal getBalance(int userId);
    Account getAccount(int userId);
    int createAccount(int userId);

    void add(BigDecimal amount, int userId);

    void subtract(BigDecimal amount, int userId);
}
