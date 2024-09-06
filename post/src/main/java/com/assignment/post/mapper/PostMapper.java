package com.assignment.post.mapper;

import com.assignment.post.dto.PostDTO;
import com.assignment.post.dto.CommentDTO;
import com.assignment.post.model.Post;
import com.assignment.post.model.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    private final ModelMapper modelMapper;

    public PostMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public PostDTO entitytoDTO(Post post) {
        return modelMapper.map(post, PostDTO.class);
    }

    public Post dtoToEntity(PostDTO postDTO) {
        return modelMapper.map(postDTO, Post.class);
    }

    public CommentDTO entitytoDTO(Comment comment) {
        return modelMapper.map(comment, CommentDTO.class);
    }

    public Comment dtoToEntity(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO, Comment.class);
    }
}
