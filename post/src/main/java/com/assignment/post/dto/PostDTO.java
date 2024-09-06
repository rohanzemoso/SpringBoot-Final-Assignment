package com.assignment.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    @Positive(message = "ID must be a positive number")
    private int id;

    @NotEmpty(message = "Content cannot be empty")
    @Size(max = 500, message = "Content must be less than 500 characters")
    private String content;

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be a positive number")
    private int userId;

    private List<CommentDTO> comments;

}
