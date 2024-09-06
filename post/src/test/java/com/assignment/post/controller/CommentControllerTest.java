package com.assignment.post.controller;

import com.assignment.post.dto.CommentDTO;
import com.assignment.post.exception.comment.CommentCreationException;
import com.assignment.post.exception.comment.CommentDeletionException;
import com.assignment.post.service.CommentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    private CommentDTO commentDTO;

    @Before
    public void setUp() {
        commentDTO = new CommentDTO(1, "Sample Comment", 1, 1);
    }

    @Test
    public void testCreateComment_Success() {
        when(commentService.createComment(any(CommentDTO.class))).thenReturn(commentDTO);

        CommentDTO result = commentController.createComment(commentDTO);

        verify(commentService, times(1)).createComment(commentDTO);
        assertEquals(commentDTO, result);
    }

    @Test(expected = CommentCreationException.class)
    public void testCreateComment_Failure() {
        when(commentService.createComment(any(CommentDTO.class))).thenThrow(new RuntimeException("Failed to create comment"));

        commentController.createComment(commentDTO);
    }

    @Test
    public void testDeleteComment_Success() {
        int commentId = 1;
        String expectedMessage = "Comment deleted successfully";

        when(commentService.deleteComment(commentId)).thenReturn(expectedMessage);

        String result = commentController.deleteComment(commentId);

        verify(commentService, times(1)).deleteComment(commentId);
        assertEquals(expectedMessage, result);
    }

    @Test(expected = CommentDeletionException.class)
    public void testDeleteComment_Failure() {
        int commentId = 1;

        when(commentService.deleteComment(commentId)).thenThrow(new CommentDeletionException("Failed to delete comment"));

        commentController.deleteComment(commentId);
    }
}
