package com.assignment.post.service.implementation;

import com.assignment.post.config.JwtRequestFilter;
import com.assignment.post.dto.CommentDTO;
import com.assignment.post.exception.comment.CommentCreationException;
import com.assignment.post.exception.comment.CommentDeletionException;
import com.assignment.post.mapper.CommentMapper;
import com.assignment.post.model.Comment;
import com.assignment.post.model.Post;
import com.assignment.post.repository.CommentRepository;
import com.assignment.post.repository.PostRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceImplTest {

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

    @Before
    public void setUp() {
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
    public void testCreateComment_Success() {
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

    @Test(expected = CommentCreationException.class)
    public void testCreateComment_PostNotFound() {
        JwtRequestFilter.userId = 1;
        when(postRepository.findById(commentDTO.getPostId())).thenReturn(Optional.empty());

        commentService.createComment(commentDTO);
    }

    @Test(expected = CommentCreationException.class)
    public void testCreateComment_Exception() {
        JwtRequestFilter.userId = 1;
        when(postRepository.findById(commentDTO.getPostId())).thenReturn(Optional.of(post));
        when(commentMapper.mapToEntity(commentDTO)).thenReturn(comment);
        when(commentRepository.save(comment)).thenThrow(new RuntimeException());

        commentService.createComment(commentDTO);
    }

    @Test
    public void testDeleteComment_Success() {
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
    public void testDeleteComment_UserMismatch() {
        JwtRequestFilter.userId = 1;


        Comment comment = mock(Comment.class);


        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        when(comment.getUserId()).thenReturn(2);


        String result = commentService.deleteComment(1);


        verify(commentRepository, times(1)).findById(1);
        verify(commentRepository, never()).deleteById(1);
        assertEquals("Comment is not User's! Cannot Delete", result);
    }


    @Test(expected = CommentDeletionException.class)
    public void testDeleteComment_NotFound() {
        JwtRequestFilter.userId = 1;
        when(commentRepository.findById(1)).thenReturn(Optional.empty());

        commentService.deleteComment(1);
    }

    @Test(expected = CommentDeletionException.class)
    public void testDeleteComment_Exception() {
        JwtRequestFilter.userId = 1;

        Comment comment = mock(Comment.class);
        when(commentRepository.findById(1)).thenReturn(Optional.of(comment));
        when(comment.getUserId()).thenThrow(new CommentDeletionException("An unexpected error occurred"));
        commentService.deleteComment(1);
    }

    @Test
    public void testDeleteComment_Exception1() {
        JwtRequestFilter.userId = 1;
        when(commentRepository.findById(1)).thenThrow(new RuntimeException("Unexpected error"));

        CommentDeletionException thrownException = assertThrows(CommentDeletionException.class, () -> {
            commentService.deleteComment(1);
        });
        assertEquals("An unexpected error occurred while deleting the comment", thrownException.getMessage());
    }
}
