package com.assignment.user.util;

public class Constants {
    private Constants() {
        throw new UnsupportedOperationException("This is a util class and cannot be instantiated");
    }
    public static final String USER_BASE_URL = "/user";
    public static final String GET_POSTS = "/get-posts";
    public static final String LOGIN_USER = "/login-user";
    public static final String GET_USERS = "/get-users";
    public static final String GET_USER_BY_ID = "/get-user/{id}";
    public static final String CREATE_USER = "/create";
    public static final String DELETE_USER = "/delete/{id}";
    public static final String UPDATE_USER = "/update-user";
}
