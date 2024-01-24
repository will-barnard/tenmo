package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    Transfer send(BigDecimal amount, int senderId, int receiverId);

    Transfer request(BigDecimal amount, int senderId, int receiverId);

    Transfer approve(int transferId);

    List<Transfer> pending(int userId);

    List<Transfer> history(int userId);

    Transfer getTransferById(int transferId);

    Transfer reject(int transferId);

    boolean verifyUserInTransaction(int transferId, int userId);


}
