package com.assignment.post.controller;

import com.assignment.post.util.Constants;
import com.assignment.post.dto.CommentDTO;
import com.assignment.post.exception.comment.CommentCreationException;
import com.assignment.post.exception.comment.CommentDeletionException;
import com.assignment.post.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping(Constants.BASE_COMMENT_URL)
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping(Constants.CREATE_COMMENT_URL)
    public CommentDTO createComment(@RequestBody CommentDTO commentDTO) {
        log.info("createComment method called with CommentDTO: {}", commentDTO);

        try {
            return commentService.createComment(commentDTO);
        } catch (Exception e) {
            throw new CommentCreationException("Failed to create comment");
        }
    }

    @DeleteMapping(Constants.DELETE_COMMENT_URL)
    public String deleteComment(@PathVariable int id) {
        log.info("deleteComment method called with ID: {}", id);

        try {
             return commentService.deleteComment(id);
        } catch (CommentDeletionException e) {
            throw new CommentDeletionException("Failed to delete comment");
        }
    }
}

