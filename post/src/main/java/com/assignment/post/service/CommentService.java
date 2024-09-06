package com.assignment.post.service;

import com.assignment.post.dto.CommentDTO;

public interface CommentService {
    CommentDTO createComment(CommentDTO commentDTO);
    String deleteComment(int id);
}
