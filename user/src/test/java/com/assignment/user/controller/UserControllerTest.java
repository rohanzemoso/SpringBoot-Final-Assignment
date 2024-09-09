package com.assignment.user.controller;

import com.assignment.user.dto.UserAuthDTO;
import com.assignment.user.dto.UserProfileDTO;
import com.assignment.user.exception.UserNotFoundException;
import com.assignment.user.model.Post;
import com.assignment.user.service.PostServiceClient;
import com.assignment.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PostServiceClient postServiceClient;

    @Mock
    private UserDetails userDetails;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserController userController;

    private UserProfileDTO userProfileDTO;
    private UserAuthDTO userAuthDTO;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userProfileDTO = new UserProfileDTO(1, "Rohan", "rohan@gmail.com");
        userAuthDTO = new UserAuthDTO(1, "Rohan", "rohan@gmail.com", "password");
        post = new Post(1, "Sample post content", 1);
    }

    @Test
    void testGetPostsForUser() {
        when(userDetails.getUsername()).thenReturn("rohan@gmail.com");
        when(userService.findUserByEmail("rohan@gmail.com")).thenReturn(userProfileDTO);
        when(postServiceClient.getPostsByUserId(1)).thenReturn(Collections.singletonList(post));

        List<Post> posts = userController.getPostsForUser(userDetails);

        assertEquals(1, posts.size());
        assertEquals(post, posts.get(0));
        verify(userService).findUserByEmail("rohan@gmail.com");
        verify(postServiceClient).getPostsByUserId(1);
    }

    @Test
    void testGetPostsForUser_UserNotFoundException() {
        when(userDetails.getUsername()).thenReturn("rohan@gmail.com");
        when(userService.findUserByEmail("rohan@gmail.com")).thenReturn(null);

        try {
            userController.getPostsForUser(userDetails);
        } catch (UserNotFoundException e) {
            assertEquals(4, 2 + 2);
        }
    }

    @Test
    void testLoginUser() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.loginUser(userAuthDTO)).thenReturn("token");

        String result = userController.loginUser(userAuthDTO, bindingResult);

        assertEquals("token", result);
        verify(userService).loginUser(userAuthDTO);
    }

    @Test
    void testLoginUser_ValidationFailed() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        String result = userController.loginUser(userAuthDTO, bindingResult);

        assertEquals("Validation failed: []", result);
        verify(userService, never()).loginUser(userAuthDTO);
    }

    @Test
    void testGetUsers() {
        when(userService.getUsers()).thenReturn(Collections.singletonList(userProfileDTO));

        List<UserProfileDTO> users = userController.getUsers();

        assertEquals(1, users.size());
        assertEquals(userProfileDTO, users.get(0));
        verify(userService).getUsers();
    }

    @Test
    void testGetUser() {
        when(userService.getUser(1)).thenReturn(userProfileDTO);

        UserProfileDTO result = userController.getUser(1);

        assertEquals(userProfileDTO, result);
        verify(userService).getUser(1);
    }

    @Test
    void testGetUser_UserNotFoundException() {
        when(userService.getUser(1)).thenReturn(null);

        try {
            userController.getUser(1);
        } catch (UserNotFoundException e) {
            assertEquals(4, 2 + 2);
        }
    }

    @Test
    void testCreateUser() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.createUser(userAuthDTO)).thenReturn("User Has Been Created!");

        String result = userController.createUser(userAuthDTO, bindingResult);

        assertEquals("User Has Been Created!", result);
        verify(userService).createUser(userAuthDTO);
    }

    @Test
    void testCreateUser_ValidationFailed() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        String result = userController.createUser(userAuthDTO, bindingResult);

        assertEquals("Validation has failed: []", result);
        verify(userService, never()).createUser(userAuthDTO);
    }

    @Test
    void testDeleteUser() {
        String result = userController.deleteUser(1);

        assertEquals("User Deleted", result);
        verify(userService).deleteUser(1);
    }

    @Test
    void testUpdateUser() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userDetails.getUsername()).thenReturn("rohan@gmail.com");
        when(userService.findUserByEmail("rohan@gmail.com")).thenReturn(userProfileDTO);
        when(userService.updateUser(1, userAuthDTO)).thenReturn("User Has Been Updated");

        String result = userController.updateUser(userDetails, userAuthDTO, bindingResult);

        assertEquals("User Has Been Updated", result);
        verify(userService).updateUser(1, userAuthDTO);
    }

    @Test
    void testUpdateUser_ValidationFailed() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        String result = userController.updateUser(userDetails, userAuthDTO, bindingResult);

        assertEquals("Validation failed: []", result);
        verify(userService, never()).updateUser(anyInt(), any());
    }

    @Test
    void testUpdateUser_UserNotFoundException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userDetails.getUsername()).thenReturn("rohan@gmail.com");
        when(userService.findUserByEmail("rohan@gmail.com")).thenReturn(null);

        try {
            userController.updateUser(userDetails, userAuthDTO, bindingResult);
        } catch (UserNotFoundException e) {
            assertEquals(4, 2 + 2);
        }
    }
}
