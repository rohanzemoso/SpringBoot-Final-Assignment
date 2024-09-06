package com.assignment.post.util;

public class Constants {
    private Constants() {
        throw new UnsupportedOperationException("This is a util class and cannot be instantiated");
    }

    public static final String JWT_PARSER_NOT_INITIALIZED = "JWT parser not initialized";
    public static final String POST_BASE = "/post";
    public static final String USER_POSTS = "/user/{userId}";
    public static final String GET_POST = "/get-post/{id}";
    public static final String GET_ALL_POSTS = "/get-all-posts";
    public static final String CREATE_POST = "/create";
    public static final String DELETE_POST = "/delete/{id}";
    public static final String UPDATE_POST = "/update-post/{id}";
    public static final String BASE_COMMENT_URL = "/comment";
    public static final String CREATE_COMMENT_URL = "/create";
    public static final String DELETE_COMMENT_URL = "/delete/{id}";
    public static final String GET_POSTS_BY_ID = "/get-posts/{id}";


}


