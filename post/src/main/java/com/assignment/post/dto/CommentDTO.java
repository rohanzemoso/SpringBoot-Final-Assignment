package com.assignment.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentDTO {

    @Positive(message = "ID must be a positive number")
    private int id;

    @NotEmpty(message = "Comment cannot be empty")
    @Size(max = 1000, message = "Comment must be less than 1000 characters")
    private String comment;

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be a positive number")
    private int userId;

    @NotNull(message = "Post ID cannot be null")
    @Positive(message = "Post ID must be a positive number")
    private int postId;
}
