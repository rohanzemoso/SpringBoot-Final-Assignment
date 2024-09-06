package com.assignment.post.service;

import com.assignment.post.dto.PostDTO;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface PostService {
    PostDTO createPost(PostDTO postDTO);
    String deletePost(int id, int userId);
    PostDTO getPost(int id);
    List<PostDTO> getPosts();
    String updatePost(int id, PostDTO newContent);
    List<PostDTO> getPostsByUserId(@PathVariable("userId") Integer userId);
}
