package com.assignment.post.service.implementation;

import com.assignment.post.config.JwtRequestFilter;
import com.assignment.post.dto.PostDTO;
import com.assignment.post.exception.post.PostCreationException;
import com.assignment.post.exception.post.PostNotFoundException;
import com.assignment.post.exception.post.PostUpdateException;
import com.assignment.post.mapper.PostMapper;
import com.assignment.post.model.Post;
import com.assignment.post.repository.PostRepository;
import com.assignment.post.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    @Override
    public PostDTO createPost(PostDTO postDTO) {
        log.info("createPost method called with PostDTO");
        try {
            Post post = postMapper.dtoToEntity(postDTO);
            Post savedPost = postRepository.save(post);
            return postMapper.entitytoDTO(savedPost);
        } catch (Exception e) {
            log.error("Error occurred while creating post");
            throw new PostCreationException("An unexpected error occurred while creating the post");
        }
    }

    @Override
    public String deletePost(int id, int userId) {
        log.info("deletePost method called with ID");
        try {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("No Post Found to Delete");
                        return new RuntimeException("No Post Found to Delete");
                    });

            if (post.getUserId() == userId) {
                postRepository.deleteById(id);
                log.info("Post deleted successfully");
                return "Post Deleted";
            } else {
                log.warn("Attempt to delete a post that is not owned by the user");
                return "Not Your Post!, Cannot Delete";
            }
        } catch (Exception e) {
            log.error("Error occurred while deleting post");
            throw new PostNotFoundException("An unexpected error occurred while deleting the post");
        }
    }

    @Override
    public PostDTO getPost(int id) {
        log.info("getPost method called");
        try {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Error fetching post");
                        return new RuntimeException("Error fetching post");
                    });

            PostDTO result = postMapper.entitytoDTO(post);
            log.info("Post fetched successfully");
            return result;
        } catch (Exception e) {
            log.error("Error occurred while fetching post");
            throw new PostNotFoundException("An unexpected error occurred while fetching the post");
        }
    }

    @Override
    public List<PostDTO> getPosts() {
        log.info("getPosts method called");
        try {
            List<Post> posts = postRepository.findAll();
            return posts.stream()
                    .map(postMapper::entitytoDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error occurred while fetching all posts");
            throw new PostNotFoundException("An unexpected error occurred while fetching the posts");
        }
    }

    @Override
    public String updatePost(int id, PostDTO newContent) {
        log.info("updatePost method called");
        try {
            int userId = JwtRequestFilter.userId;
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("Post not found");
                        return new RuntimeException("Post not found");
                    });

            if (post.getUserId() == userId) {
                post.setContent(newContent.getContent());
                postRepository.save(post);
                log.info("Post updated successfully");
                return "Post Updated Successfully!";
            } else {
                log.warn("Attempt to update a post that is not owned by the user");
                return "Cannot Update Post, Not Your Post";
            }
        } catch (Exception e) {
            log.error("Error occurred while updating post");
            throw new PostUpdateException("An unexpected error occurred while updating the post");
        }
    }

    @Override
    public List<PostDTO> getPostsByUserId(Integer userId) {
        log.info("getPostsByUserId method called");
        try {
            List<Post> posts = postRepository.findPostsByUserId(userId);
            return posts.stream()
                    .map(postMapper::entitytoDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error occurred while fetching posts by user ID");
            throw new PostNotFoundException("An unexpected error occurred while fetching posts by user ID");
        }
    }
}
