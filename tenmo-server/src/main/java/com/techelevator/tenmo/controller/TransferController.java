package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.FriendsDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Friends;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TransferDao transferDao;
    @Autowired
    private FriendsDao friendsDao;

    private static final DateTimeFormatter LOG_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
    private static final String API_BASE_PATH = "/Transfer";


    @PreAuthorize("permitAll")
    @RequestMapping(path = API_BASE_PATH + "/directory", method = RequestMethod.GET)
    public List<String> getUsernames() {
        return userDao.findAllUsernames();
    }

    @RequestMapping(path = API_BASE_PATH + "/send", method = RequestMethod.GET)
    public String sendNull(Principal principal) {

        int userId = userDao.findIdByUsername(principal.getName());
        List<String> friendslistStr = new ArrayList<>();
        friendslistStr.add("Friends you can send money to:");
        List<Friends> friendslistObj = friendsDao.getFriendslist(userId);

        for (Friends friend : friendslistObj) {
            if (friend.isConfirmed()) {
                if (friend.getUserA() == userId) {
                    friendslistStr.add(userDao.findUsernameById(friend.getUserB()));
                } else if (friend.getUserB() == userId) {
                    friendslistStr.add(userDao.findUsernameById(friend.getUserA()));
                }
            }
        }

        if (friendslistStr.size() == 0) {
            return "You have no friends";
        }

        String result = String.join("\n", friendslistStr);


        return result;

    }

    @RequestMapping(path = API_BASE_PATH + "/send", method = RequestMethod.POST)
    public String send(@RequestParam String user, @RequestParam double amount, Principal principal) {
        int receiverId = userDao.findIdByUsername(user);
        int senderId = userDao.findIdByUsername(principal.getName());
        BigDecimal transferAmount = new BigDecimal(amount);
        Transfer transfer = null;
        boolean success = false;
        try {
            if (senderId != receiverId){
                if (transferAmount.compareTo(BigDecimal.ZERO) > 0) {
                    if (accountDao.getBalance(senderId).subtract(new BigDecimal(amount)).compareTo(BigDecimal.ZERO) > 0) {

                        accountDao.subtract(transferAmount, senderId);
                        accountDao.add(transferAmount, receiverId);
                        transfer = transferDao.send(transferAmount, senderId, receiverId);
                        success = true;

                    } else {
                        throw new Exception("Insufficient funds in account to complete send");
                    }
                } else {
                    throw new Exception("Send amount must be a positive number");
                }
            } else {
                throw new Exception("Cannot send funds to yourself");
            }
        } catch(Exception e) {
            System.out.println(e);
        }

        if (success) {
            return "Transfer successful \n" + getLog(transfer);
        } else {
            return "Transfer unsuccessful";
        }

    }
    @RequestMapping(path = API_BASE_PATH + "/request", method = RequestMethod.POST)
    public String request(@RequestParam String user, @RequestParam double amount, Principal principal){
        int receiverId = userDao.findIdByUsername(principal.getName());
        int senderId = userDao.findIdByUsername(user);
        BigDecimal transferAmount = new BigDecimal(amount);
        Transfer transfer = null;
        boolean success = false;
        try {
            if (senderId != receiverId){
                if (transferAmount.compareTo(BigDecimal.ZERO) > 0) {
                    transfer = transferDao.request(transferAmount, senderId, receiverId);
                    success = true;
                } else {
                    throw new Exception("Send amount must be a positive number");
                }
            } else {
                throw new Exception("Cannot send funds to yourself");
            }
        } catch(Exception e) {
            System.out.println(e);
        }

        if (success) {
            return "Request successful \n" + getLog(transfer);
        } else {
            return "Request unsuccessful";
        }

    }
    @RequestMapping(path = API_BASE_PATH + "/pending", method = RequestMethod.GET)
    public List<String> pending(Principal principal){
        int userId = userDao.findIdByUsername(principal.getName());
        List<String> pendingStr = new ArrayList<>();
        List<Transfer> pending = new ArrayList<>();
        pending = transferDao.pending(userId);
        for (Transfer transfer : pending){
            pendingStr.add(getRequestLog(transfer));
        }
        if (pendingStr.size()== 0){
            pendingStr.add("No pending transactions");
        }
        return pendingStr;
    }
    @RequestMapping(path = API_BASE_PATH + "/pending/{id}", method = RequestMethod.POST)
    public String approve(@PathVariable int id, Principal principal, @RequestParam(required = false) Boolean approve) {
        int userId = userDao.findIdByUsername(principal.getName());
        boolean success = false;
        boolean nsf = false;
        if (!transferDao.verifyUserInTransaction(id, userId)) {
            return "Cannot approve that transaction";
        }
        Transfer transfer = transferDao.getTransferById(id);
        try {
            if (transfer == null) {
                throw new Exception("no record of request found");
            }
            if (approve == null){
                return getRequestLog(transferDao.getTransferById(id));
            }
            if (transfer.isCompleted()){
                return "Not a pending transfer";
            }
            if (transfer.isRejected()){
                return "Someone has already rejected this transfer";
            }
            if (transfer.getReceiverId() == userId && approve){
                return "You cannot approve your own request";
            }
            if (transfer.getReceiverId() == userId && !approve){
                transferDao.reject(id);
                return "Unsent your request";
            }
            if (transfer.getSenderId() == userId && !approve){
                transferDao.reject(id);
                return "The request has been rejected";
            }
            if (transfer.getSenderId() == userId && approve) {


                if (accountDao.getBalance(userId).subtract(transfer.getTransferAmount()).compareTo(BigDecimal.ZERO) > 0) {

                    accountDao.subtract(transfer.getTransferAmount(), transfer.getSenderId());
                    accountDao.add(transfer.getTransferAmount(), transfer.getReceiverId());
                    transfer = transferDao.approve(id);
                    success = true;

                } else {
                    nsf = true;
                    throw new Exception("Insufficient funds in account to complete send");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        if (success){
            return "request has been approved";
        } if (nsf){
            return "insufficient funds";
        }
        return null;
    }

    private String getLog(Transfer transfer) {

        String log = "";
        String sender = userDao.findUsernameById(transfer.getSenderId());
        String receiver = userDao.findUsernameById(transfer.getReceiverId());

        log += LOG_FORMAT.format(transfer.getReceiveTime()) + " " + sender + " sent $" +
                transfer.getTransferAmount() +
                " to " + receiver;

        log += ", transaction ID is " + transfer.getTransferId();

        if (!transfer.isCompleted() && !transfer.isRejected()) {
            log += ", transfer is pending";
        }

        if (transfer.isRejected()) {
            log += ", transfer has been rejected";
        }

        return log;

    }

    private String getRequestLog(Transfer transfer){
        String log = "";
        String sender = userDao.findUsernameById(transfer.getSenderId());
        String receiver = userDao.findUsernameById(transfer.getReceiverId());

        log += LOG_FORMAT.format(transfer.getSendTime()) + " " + receiver + " is requesting $" + transfer.getTransferAmount() + " Transfer ID: " + transfer.getTransferId();
        return log;
    }


}