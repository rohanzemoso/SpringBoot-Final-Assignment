package com.assignment.post.controller;

import com.assignment.post.dto.CommentDTO;
import com.assignment.post.exception.comment.CommentCreationException;
import com.assignment.post.exception.comment.CommentDeletionException;
import com.assignment.post.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class CommentControllerTest {

    @InjectMocks
    private CommentController commentController;

    @Mock
    private CommentService commentService;

    private CommentDTO commentDTO;

    @BeforeEach
    public void setUp() {
        commentDTO = new CommentDTO(1, "Sample Comment", 1, 1);
    }

    @Test
     void testCreateComment_Success() {
        when(commentService.createComment(any(CommentDTO.class))).thenReturn(commentDTO);

        CommentDTO result = commentController.createComment(commentDTO);

        verify(commentService, times(1)).createComment(commentDTO);
        assertEquals(commentDTO, result);
    }

    @Test
     void testCreateComment_Failure() {
        when(commentService.createComment(any(CommentDTO.class))).thenThrow(new RuntimeException("Failed to create comment"));

        assertThrows(CommentCreationException.class, () -> commentController.createComment(commentDTO));
    }

    @Test
     void testDeleteComment_Success() {
        int commentId = 1;
        String expectedMessage = "Comment deleted successfully";

        when(commentService.deleteComment(commentId)).thenReturn(expectedMessage);

        String result = commentController.deleteComment(commentId);

        verify(commentService, times(1)).deleteComment(commentId);
        assertEquals(expectedMessage, result);
    }

    @Test
     void testDeleteComment_Failure() {
        int commentId = 1;

        when(commentService.deleteComment(commentId)).thenThrow(new CommentDeletionException("Failed to delete comment"));

        assertThrows(CommentDeletionException.class, () -> commentController.deleteComment(commentId));
    }
}
