package com.assignment.post.service.implementation;

import com.assignment.post.config.JwtRequestFilter;
import com.assignment.post.dto.CommentDTO;
import com.assignment.post.exception.comment.CommentCreationException;
import com.assignment.post.exception.comment.CommentDeletionException;
import com.assignment.post.model.Comment;
import com.assignment.post.model.Post;
import com.assignment.post.repository.CommentRepository;
import com.assignment.post.repository.PostRepository;
import com.assignment.post.mapper.CommentMapper;
import com.assignment.post.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public CommentDTO createComment(CommentDTO commentDTO) {
        log.info("createComment method called");
        try {
            int userId = JwtRequestFilter.userId;
            commentDTO.setUserId(userId);
            Post post = postRepository.findById(commentDTO.getPostId())
                    .orElseThrow(() ->
                            new CommentCreationException("No post found with the given post id")
                    );

            Comment comment = commentMapper.mapToEntity(commentDTO);
            comment.setPost(post);

            Comment savedComment = commentRepository.save(comment);
            log.info("Comment created successfully");

            return commentMapper.mapToDTO(savedComment);
        } catch (CommentCreationException e) {
            throw e;
        } catch (Exception e) {
            throw new CommentCreationException("An unexpected error occurred while creating the comment");
        }
    }

    @Override
    public String deleteComment(int id) {
        log.info("deleteComment method called");
        try {
            int userId = JwtRequestFilter.userId;
            Comment comment = commentRepository.findById(id)
                    .orElseThrow(() ->
                            new CommentDeletionException("Comment Not Found")
                    );

            if (comment.getUserId() == userId) {
                commentRepository.deleteById(id);
                log.info("Comment with ID");
                return "Comment Deleted";
            } else {
                return "Comment is not User's! Cannot Delete";
            }
        } catch (CommentDeletionException e) {
            throw e;
        } catch (Exception e) {
            throw new CommentDeletionException("An unexpected error occurred while deleting the comment");
        }
    }
}
