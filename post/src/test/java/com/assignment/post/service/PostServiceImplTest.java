package com.assignment.post.service;

import com.assignment.post.config.JwtRequestFilter;
import com.assignment.post.dto.PostDTO;
import com.assignment.post.exception.post.PostCreationException;
import com.assignment.post.exception.post.PostNotFoundException;
import com.assignment.post.exception.post.PostUpdateException;
import com.assignment.post.mapper.PostMapper;
import com.assignment.post.model.Post;
import com.assignment.post.repository.PostRepository;
import com.assignment.post.service.implementation.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;
    private PostDTO postDTO;

    @BeforeEach
    void setUp() {
        post = new Post(1, "Content", 1, null);
        postDTO = new PostDTO(1, "Content", 1, null);
    }

    @Test
    void testCreatePost_Success() {
        when(postMapper.dtoToEntity(postDTO)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.entitytoDTO(post)).thenReturn(postDTO);

        PostDTO result = postService.createPost(postDTO);

        assertNotNull(result);
        assertEquals(postDTO, result);
        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).entitytoDTO(post);
    }

    @Test
    void testCreatePost_Exception() {
        when(postMapper.dtoToEntity(postDTO)).thenThrow(new RuntimeException("Exception"));

        assertThrows(PostCreationException.class, () -> postService.createPost(postDTO));
    }

    @Test
    void testUpdatePost_PostNotFound() {
        int postId = 1;
        int userId = 1;
        PostDTO newContent = new PostDTO(postId, "New Content", userId, null);

        JwtRequestFilter.userId = userId;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostUpdateException.class, () -> postService.updatePost(postId, newContent));
    }

    @Test
    void testDeletePost_Success() {
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        String result = postService.deletePost(1, 1);

        assertEquals("Post Deleted", result);
        verify(postRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeletePost_NotOwned() {
        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        String result = postService.deletePost(1, 2);

        assertEquals("Not Your Post!, Cannot Delete", result);
        verify(postRepository, never()).deleteById(anyInt());
    }

    @Test
    void testDeletePost_NotFound() {
        when(postRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.deletePost(1, 1));
    }

    @Test
    void testGetPost_Success() {
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(postMapper.entitytoDTO(post)).thenReturn(postDTO);

        PostDTO result = postService.getPost(1);

        assertNotNull(result);
        assertEquals(postDTO, result);
        verify(postMapper, times(1)).entitytoDTO(post);
    }

    @Test
    void testGetPost_NotFound() {
        when(postRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.getPost(1));
    }

    @Test
    void testGetPosts_Success() {
        List<Post> posts = Arrays.asList(post);
        when(postRepository.findAll()).thenReturn(posts);
        when(postMapper.entitytoDTO(post)).thenReturn(postDTO);

        List<PostDTO> result = postService.getPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postMapper, times(1)).entitytoDTO(post);
    }

    @Test
    void testGetPosts_Exception() {
        when(postRepository.findAll()).thenThrow(new RuntimeException("Error fetching posts"));

        assertThrows(PostNotFoundException.class, () -> postService.getPosts());
    }

    @Test
    void testUpdatePost_Success() {
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        JwtRequestFilter.userId = 1;
        postDTO.setContent("Updated Content");

        String result = postService.updatePost(1, postDTO);

        assertEquals("Post Updated Successfully!", result);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testUpdatePost_NotOwned() {
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        JwtRequestFilter.userId = 2;

        String result = postService.updatePost(1, postDTO);

        assertEquals("Cannot Update Post, Not Your Post", result);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testUpdatePost_Exception() {
        when(postRepository.findById(1)).thenThrow(new RuntimeException("Post not found"));

        assertThrows(PostUpdateException.class, () -> postService.updatePost(1, postDTO));
    }

    @Test
    void testGetPostsByUserId_Success() {
        List<Post> posts = Arrays.asList(post);
        when(postRepository.findPostsByUserId(1)).thenReturn(posts);
        when(postMapper.entitytoDTO(post)).thenReturn(postDTO);

        List<PostDTO> result = postService.getPostsByUserId(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postMapper, times(1)).entitytoDTO(post);
    }

    @Test
    void testGetPostsByUserId_Exception() {
        when(postRepository.findPostsByUserId(1)).thenThrow(new RuntimeException("Error fetching posts by user ID"));

        assertThrows(PostNotFoundException.class, () -> postService.getPostsByUserId(1));
    }
}
