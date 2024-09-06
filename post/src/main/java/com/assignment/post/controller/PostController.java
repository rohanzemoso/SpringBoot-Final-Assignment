package com.assignment.post.controller;

import com.assignment.post.config.JwtRequestFilter;
import com.assignment.post.util.Constants;
import com.assignment.post.dto.PostDTO;
import com.assignment.post.exception.post.PostCreationException;
import com.assignment.post.exception.post.PostNotFoundException;
import com.assignment.post.exception.post.PostUpdateException;
import com.assignment.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.POST_BASE)
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping(Constants.USER_POSTS)
    public List<PostDTO> getPostsByUserId(@PathVariable("userId") Integer userId) {
        try {
            return postService.getPostsByUserId(userId);
        } catch (PostNotFoundException e) {
            throw new PostNotFoundException("Posts not found for user ID: " + userId);
        }
    }

    @GetMapping(Constants.GET_POSTS_BY_ID)
    public List<PostDTO> getPostsById(@PathVariable int id){
        return postService.getPostsByUserId(id);
    }

    @GetMapping(Constants.GET_POST)
    public PostDTO getPost(@PathVariable int id) {
        try {
            return postService.getPost(id);
        } catch (PostNotFoundException e) {
            throw new PostNotFoundException("Post not found with ID: " + id);
        }
    }

    @GetMapping(Constants.GET_ALL_POSTS)
    public List<PostDTO> getPosts() {
        try {
            return postService.getPosts();
        } catch (Exception e) {
            throw new PostNotFoundException("Failed to fetch posts");
        }
    }

    @PostMapping(Constants.CREATE_POST)
    public PostDTO createPost(@RequestBody PostDTO postDTO) {
        try {
            postDTO.setUserId(JwtRequestFilter.userId);
            return postService.createPost(postDTO);
        } catch (PostCreationException e) {
            throw new PostCreationException("Failed to create post");
        }
    }

    @DeleteMapping(Constants.DELETE_POST)
    public String deletePost(@PathVariable int id) {
        try {
            int userId = JwtRequestFilter.userId;
            return postService.deletePost(id, userId);
        } catch (PostNotFoundException e) {
            throw new PostNotFoundException("Post not found with ID: " + id);
        }
    }

    @PutMapping(Constants.UPDATE_POST)
    public String updatePost(@PathVariable int id, @RequestBody PostDTO postDTO) {
        try {
            return postService.updatePost(id, postDTO);
        } catch (PostUpdateException e) {
            throw new PostUpdateException("Failed to update post with ID: " + id);
        }
    }
}
