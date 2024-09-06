package com.assignment.post.controller;

import com.assignment.post.config.JwtRequestFilter;
import com.assignment.post.dto.PostDTO;
import com.assignment.post.exception.post.PostCreationException;
import com.assignment.post.exception.post.PostNotFoundException;
import com.assignment.post.exception.post.PostUpdateException;
import com.assignment.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    private PostDTO postDTO1;
    private PostDTO postDTO2;

    @BeforeEach
    void setUp() {
        postDTO1 = new PostDTO();
        postDTO1.setId(1);
        postDTO1.setUserId(1);

        postDTO2 = new PostDTO();
        postDTO2.setId(2);
        postDTO2.setUserId(1);
    }

    @Test
    void testGetPostsByUserId_Success() {
        int userId = 1;
        List<PostDTO> posts = Arrays.asList(postDTO1, postDTO2);
        when(postService.getPostsByUserId(userId)).thenReturn(posts);

        List<PostDTO> result = postController.getPostsByUserId(userId);

        verify(postService, times(1)).getPostsByUserId(userId);
        assertEquals(posts, result);
    }

    @Test
    void testGetPostsByUserId_NotFound() {
        int userId = 1;
        when(postService.getPostsByUserId(userId)).thenThrow(new PostNotFoundException("Posts not found"));

        assertThrows(PostNotFoundException.class, () -> postController.getPostsByUserId(userId));
    }

    @Test
    void testGetPostsById_Success() {
        int id = 1;
        List<PostDTO> posts = Arrays.asList(postDTO1, postDTO2);
        when(postService.getPostsByUserId(id)).thenReturn(posts);

        List<PostDTO> result = postController.getPostsById(id);

        verify(postService, times(1)).getPostsByUserId(id);
        assertEquals(posts, result);
    }

    @Test
    void testGetPost_Success() {
        int id = 1;
        when(postService.getPost(id)).thenReturn(postDTO1);

        PostDTO result = postController.getPost(id);

        verify(postService, times(1)).getPost(id);
        assertEquals(postDTO1, result);
    }

    @Test
    void testGetPost_NotFound() {
        int id = 1;
        when(postService.getPost(id)).thenThrow(new PostNotFoundException("Post not found"));

        assertThrows(PostNotFoundException.class, () -> postController.getPost(id));
    }

    @Test
    void testGetPosts_Success() {
        List<PostDTO> posts = Arrays.asList(postDTO1, postDTO2);
        when(postService.getPosts()).thenReturn(posts);

        List<PostDTO> result = postController.getPosts();

        verify(postService, times(1)).getPosts();
        assertEquals(posts, result);
    }

    @Test
    void testGetPosts_Failure() {
        when(postService.getPosts()).thenThrow(new PostNotFoundException("Failed to fetch posts"));

        assertThrows(PostNotFoundException.class, () -> postController.getPosts());
    }

    @Test
    void testCreatePost_Success() {
        JwtRequestFilter.userId = 1;
        when(postService.createPost(any(PostDTO.class))).thenReturn(postDTO1);

        PostDTO result = postController.createPost(postDTO1);

        verify(postService, times(1)).createPost(postDTO1);
        assertEquals(postDTO1, result);
    }

    @Test
    void testCreatePost_Failure() {
        JwtRequestFilter.userId = 1;
        when(postService.createPost(any(PostDTO.class))).thenThrow(new PostCreationException("Failed to create post"));

        assertThrows(PostCreationException.class, () -> postController.createPost(postDTO1));
    }

    @Test
    void testDeletePost_Success() {
        int postId = 1;
        JwtRequestFilter.userId = 1;
        String expectedMessage = "Post deleted successfully";
        when(postService.deletePost(postId, JwtRequestFilter.userId)).thenReturn(expectedMessage);

        String result = postController.deletePost(postId);

        verify(postService, times(1)).deletePost(postId, JwtRequestFilter.userId);
        assertEquals(expectedMessage, result);
    }

    @Test
    void testDeletePost_NotFound() {
        int postId = 1;
        JwtRequestFilter.userId = 1;
        when(postService.deletePost(postId, JwtRequestFilter.userId)).thenThrow(new PostNotFoundException("Post not found"));

        assertThrows(PostNotFoundException.class, () -> postController.deletePost(postId));
    }

    @Test
    void testUpdatePost_Success() {
        int postId = 1;
        String expectedMessage = "Post updated successfully";
        when(postService.updatePost(postId, postDTO1)).thenReturn(expectedMessage);

        String result = postController.updatePost(postId, postDTO1);

        verify(postService, times(1)).updatePost(postId, postDTO1);
        assertEquals(expectedMessage, result);
    }

    @Test
    void testUpdatePost_Failure() {
        int postId = 1;
        when(postService.updatePost(postId, postDTO1)).thenThrow(new PostUpdateException("Failed to update post"));

        assertThrows(PostUpdateException.class, () -> postController.updatePost(postId, postDTO1));
    }
}
