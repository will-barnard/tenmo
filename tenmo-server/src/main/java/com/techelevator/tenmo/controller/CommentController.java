package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.*;
import com.techelevator.tenmo.model.Comment;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@PreAuthorize("isAuthenticated()")
public class CommentController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TransferDao transferDao;
    @Autowired
    private FriendsDao friendsDao;
    @Autowired
    private CommentDao commentDao;
    private static final String API_BASE_PATH = "/Account/history";


    @RequestMapping(path = API_BASE_PATH + "/{id}", method = RequestMethod.POST)
    public String addComment(@PathVariable int id, @RequestParam String comment, Principal principal) {

        boolean success = false;
        Transfer transfer = transferDao.getTransferById(id);
        int commenterId = userDao.findIdByUsername(principal.getName());
        Comment newComment = new Comment(id, commenterId, comment, LocalDateTime.now());

        if (!transferDao.verifyUserInTransaction(id, commenterId)) {
            return "You cannot comment on a transaction you are not a part of";
        }

        try {
            commentDao.createComment(newComment);
            success = true;
        } catch(Exception e) {
            System.out.println("Something went wrong creating a comment");
        }

        if (success) {
            return "Succesfully added comment";
        } else {
            return "Unable to add comment";
        }

    }


}
