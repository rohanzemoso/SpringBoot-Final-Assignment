package com.assignment.post.service;

import com.assignment.post.config.JwtRequestFilter;
import com.assignment.post.dto.CommentDTO;
import com.assignment.post.exception.comment.CommentCreationException;
import com.assignment.post.exception.comment.CommentDeletionException;
import com.assignment.post.mapper.CommentMapper;
import com.assignment.post.model.Comment;
import com.assignment.post.model.Post;
import com.assignment.post.repository.CommentRepository;
import com.assignment.post.repository.PostRepository;
import com.assignment.post.service.implementation.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentMapper commentMapper;

    private CommentDTO commentDTO;
    private Comment comment;
    private Post post;

    @BeforeEach
    void setUp() {
        commentDTO = new CommentDTO();
        commentDTO.setId(1);
        commentDTO.setPostId(1);

        post = new Post();
        post.setId(1);

        comment = new Comment();
        comment.setId(1);
        comment.setPost(post);
    }

    @Test
    void testCreateComment_Success() {
        JwtRequestFilter.userId = 1;
        when(postRepository.findById(commentDTO.getPostId())).thenReturn(Optional.of(post));
        when(commentMapper.mapToEntity(commentDTO)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.mapToDTO(comment)).thenReturn(commentDTO);

        CommentDTO result = commentService.createComment(commentDTO);

        verify(postRepository, times(1)).findById(commentDTO.getPostId());
        verify(commentMapper, times(1)).mapToEntity(commentDTO);
        verify(commentRepository, times(1)).save(comment);
        verify(commentMapper, times(1)).mapToDTO(comment);
        assertEquals(commentDTO, result);
    }

    @Test
    void testCreateComment_PostNotFound() {
        JwtRequestFilter.userId = 1;
        when(postRepository.findById(commentDTO.getPostId())).thenReturn(Optional.empty());

        assertThrows(CommentCreationException.class, () -> commentService.createComment(commentDTO));
    }

    @Test
    void testCreateComment_Exception() {
        JwtRequestFilter.userId = 1;
        when(postRepository.findById(commentDTO.getPostId())).thenReturn(Optional.of(post));
        when(commentMapper.mapToEntity(commentDTO)).thenReturn(comment);
        when(commentRepository.save(comment)).thenThrow(new RuntimeException());

        assertThrows(CommentCreationException.class, () -> commentService.createComment(commentDTO));
    }

    @Test
    void testDeleteComment_Success() {
        JwtRequestFilter.userId = 1;

        Comment comment = mock(Comment.class);
        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        when(comment.getUserId()).thenReturn(1);

        String result = commentService.deleteComment(1);

        verify(commentRepository, times(1)).findById(1);
        verify(commentRepository, times(1)).deleteById(1);
        assertEquals("Comment Deleted", result);
    }

    @Test
    void testDeleteComment_UserMismatch() {
        JwtRequestFilter.userId = 1;

        Comment commentObject = mock(Comment.class);
        when(commentRepository.findById(1)).thenReturn(Optional.of(commentObject));
        when(commentObject.getUserId()).thenReturn(2);

        String result = commentService.deleteComment(1);

        verify(commentRepository, times(1)).findById(1);
        verify(commentRepository, never()).deleteById(1);
        assertEquals("Comment is not User's! Cannot Delete", result);
    }

    @Test
    void testDeleteComment_NotFound() {
        JwtRequestFilter.userId = 1;
        when(commentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(CommentDeletionException.class, () -> commentService.deleteComment(1));
    }

    @Test
    void testDeleteComment_Exception() {
        JwtRequestFilter.userId = 1;

        Comment comment = mock(Comment.class);
        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        when(comment.getUserId()).thenThrow(new CommentDeletionException("An unexpected error occurred"));

        assertThrows(CommentDeletionException.class, () -> commentService.deleteComment(1));
    }

    @Test
    void testDeleteComment_Exception1() {
        JwtRequestFilter.userId = 1;
        when(commentRepository.findById(1)).thenThrow(new RuntimeException("Unexpected error"));

        CommentDeletionException thrownException = assertThrows(CommentDeletionException.class, () -> {
            commentService.deleteComment(1);
        });
        assertEquals("An unexpected error occurred while deleting the comment", thrownException.getMessage());
    }
}
