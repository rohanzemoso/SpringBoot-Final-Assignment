package com.assignment.post.controller;

import com.assignment.post.config.JwtRequestFilter;
import com.assignment.post.dto.PostDTO;
import com.assignment.post.exception.post.PostCreationException;
import com.assignment.post.exception.post.PostNotFoundException;
import com.assignment.post.exception.post.PostUpdateException;
import com.assignment.post.service.PostService;
import com.assignment.post.util.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostControllerTest {

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    private PostDTO postDTO1;
    private PostDTO postDTO2;

    @Before
    public void setUp() {
        postDTO1 = new PostDTO();
        postDTO1.setId(1);
        postDTO1.setUserId(1);

        postDTO2 = new PostDTO();
        postDTO2.setId(2);
        postDTO2.setUserId(1);
    }


    @Test
    public void testGetPostsByUserId_Success() {
        int userId = 1;
        List<PostDTO> posts = Arrays.asList(postDTO1, postDTO2);
        when(postService.getPostsByUserId(userId)).thenReturn(posts);

        List<PostDTO> result = postController.getPostsByUserId(userId);

        verify(postService, times(1)).getPostsByUserId(userId);
        assertEquals(posts, result);
    }

    @Test(expected = PostNotFoundException.class)
    public void testGetPostsByUserId_NotFound() {
        int userId = 1;
        when(postService.getPostsByUserId(userId)).thenThrow(new PostNotFoundException("Posts not found"));

        postController.getPostsByUserId(userId);
    }

    @Test
    public void testGetPostsById_Success() {
        int id = 1;
        List<PostDTO> posts = Arrays.asList(postDTO1, postDTO2);
        when(postService.getPostsByUserId(id)).thenReturn(posts);

        List<PostDTO> result = postController.getPostsById(id);

        verify(postService, times(1)).getPostsByUserId(id);
        assertEquals(posts, result);
    }

    @Test
    public void testGetPost_Success() {
        int id = 1;
        when(postService.getPost(id)).thenReturn(postDTO1);

        PostDTO result = postController.getPost(id);

        verify(postService, times(1)).getPost(id);
        assertEquals(postDTO1, result);
    }

    @Test(expected = PostNotFoundException.class)
    public void testGetPost_NotFound() {
        int id = 1;
        when(postService.getPost(id)).thenThrow(new PostNotFoundException("Post not found"));

        postController.getPost(id);
    }

    @Test
    public void testGetPosts_Success() {
        List<PostDTO> posts = Arrays.asList(postDTO1, postDTO2);
        when(postService.getPosts()).thenReturn(posts);

        List<PostDTO> result = postController.getPosts();

        verify(postService, times(1)).getPosts();
        assertEquals(posts, result);
    }

    @Test(expected = PostNotFoundException.class)
    public void testGetPosts_Failure() {
        when(postService.getPosts()).thenThrow(new PostNotFoundException("Failed to fetch posts"));

        postController.getPosts();
    }

    @Test
    public void testCreatePost_Success() {
        JwtRequestFilter.userId = 1;
        when(postService.createPost(any(PostDTO.class))).thenReturn(postDTO1);

        PostDTO result = postController.createPost(postDTO1);

        verify(postService, times(1)).createPost(postDTO1);
        assertEquals(postDTO1, result);
    }

    @Test(expected = PostCreationException.class)
    public void testCreatePost_Failure() {
        JwtRequestFilter.userId = 1;
        when(postService.createPost(any(PostDTO.class))).thenThrow(new PostCreationException("Failed to create post"));

        postController.createPost(postDTO1);
    }

    @Test
    public void testDeletePost_Success() {
        int postId = 1;
        JwtRequestFilter.userId = 1;
        String expectedMessage = "Post deleted successfully";
        when(postService.deletePost(postId, JwtRequestFilter.userId)).thenReturn(expectedMessage);

        String result = postController.deletePost(postId);

        verify(postService, times(1)).deletePost(postId, JwtRequestFilter.userId);
        assertEquals(expectedMessage, result);
    }

    @Test(expected = PostNotFoundException.class)
    public void testDeletePost_NotFound() {
        int postId = 1;
        JwtRequestFilter.userId = 1;
        when(postService.deletePost(postId, JwtRequestFilter.userId)).thenThrow(new PostNotFoundException("Post not found"));

        postController.deletePost(postId);
    }

    @Test
    public void testUpdatePost_Success() {
        int postId = 1;
        String expectedMessage = "Post updated successfully";
        when(postService.updatePost(postId, postDTO1)).thenReturn(expectedMessage);

        String result = postController.updatePost(postId, postDTO1);

        verify(postService, times(1)).updatePost(postId, postDTO1);
        assertEquals(expectedMessage, result);
    }

    @Test(expected = PostUpdateException.class)
    public void testUpdatePost_Failure() {
        int postId = 1;
        when(postService.updatePost(postId, postDTO1)).thenThrow(new PostUpdateException("Failed to update post"));

        postController.updatePost(postId, postDTO1);
    }
}
