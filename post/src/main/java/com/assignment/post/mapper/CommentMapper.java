package com.assignment.post.mapper;

import com.assignment.post.dto.CommentDTO;
import com.assignment.post.model.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    private final ModelMapper modelMapper;

    public CommentMapper() {
        this.modelMapper = new ModelMapper();
    }

    public CommentDTO mapToDTO(Comment comment) {
        return modelMapper.map(comment, CommentDTO.class);
    }

    public Comment mapToEntity(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO, Comment.class);
    }
}
