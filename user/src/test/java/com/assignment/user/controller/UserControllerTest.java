package com.assignment.user.controller;

import com.assignment.user.dto.UserAuthDTO;
import com.assignment.user.dto.UserPasswordDTO;
import com.assignment.user.dto.UserProfileDTO;
import com.assignment.user.exception.UserNotFoundException;
import com.assignment.user.model.Post;
import com.assignment.user.service.PostServiceClient;
import com.assignment.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

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
    private UserPasswordDTO userPasswordDTO;

    private Post post;

    @Before
    public void setUp() {
        userProfileDTO = new UserProfileDTO(1, "Rohan", "rohan@gmail.com");
        userAuthDTO = new UserAuthDTO(1, "Rohan", "rohan@gmail.com", "password");
        userPasswordDTO = new UserPasswordDTO("oldPassword", "newPassword");
        post = new Post(1, "Sample post content", 1);
    }

    @Test
    public void testGetPostsForUser() {
        when(userDetails.getUsername()).thenReturn("rohan@gmail.com");
        when(userService.findUserByEmail("rohan@gmail.com")).thenReturn(userProfileDTO);
        when(postServiceClient.getPostsByUserId(1)).thenReturn(Collections.singletonList(post));

        List<Post> posts = userController.getPostsForUser(userDetails);

        assertEquals(1, posts.size());
        assertEquals(post, posts.get(0));
        verify(userService).findUserByEmail("rohan@gmail.com");
        verify(postServiceClient).getPostsByUserId(1);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetPostsForUser_UserNotFoundException() {
        when(userDetails.getUsername()).thenReturn("rohan@gmail.com");
        when(userService.findUserByEmail("rohan@gmail.com")).thenReturn(null);

        userController.getPostsForUser(userDetails);
    }

    @Test
    public void testLoginUser() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.loginUser(userAuthDTO)).thenReturn("token");

        String result = userController.loginUser(userAuthDTO, bindingResult);

        assertEquals("token", result);
        verify(userService).loginUser(userAuthDTO);
    }

    @Test
    public void testLoginUser_ValidationFailed() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        String result = userController.loginUser(userAuthDTO, bindingResult);

        assertEquals("Validation failed:- []", result);
        verify(userService, never()).loginUser(userAuthDTO);
    }

    @Test
    public void testGetUsers() {
        when(userService.getUsers()).thenReturn(Collections.singletonList(userProfileDTO));

        List<UserProfileDTO> users = userController.getUsers();

        assertEquals(1, users.size());
        assertEquals(userProfileDTO, users.get(0));
        verify(userService).getUsers();
    }

    @Test
    public void testGetUser() {
        when(userService.getUser(1)).thenReturn(userProfileDTO);

        UserProfileDTO result = userController.getUser(1);

        assertEquals(userProfileDTO, result);
        verify(userService).getUser(1);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUser_UserNotFoundException() {
        when(userService.getUser(1)).thenReturn(null);

        userController.getUser(1);
    }

    @Test
    public void testCreateUser() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.createUser(userAuthDTO)).thenReturn("User Has Been Created!");

        String result = userController.createUser(userAuthDTO, bindingResult);

        assertEquals("User Has Been Created!", result);
        verify(userService).createUser(userAuthDTO);
    }

    @Test
    public void testCreateUser_ValidationFailed() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        String result = userController.createUser(userAuthDTO, bindingResult);

        assertEquals("Validation has failed: []", result);
        verify(userService, never()).createUser(userAuthDTO);
    }

    @Test
    public void testDeleteUser_Success() {

        when(userDetails.getUsername()).thenReturn("rohan@gmail.com");
        when(userService.findUserByEmail("rohan@gmail.com")).thenReturn(userProfileDTO);


        String result = userController.deleteUser(userDetails, 1);

        assertEquals("User Deleted", result);
        verify(userService).deleteUser(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUser_DifferentUserId() {

        when(userDetails.getUsername()).thenReturn("rohan@gmail.com");
        when(userService.findUserByEmail("rohan@gmail.com")).thenReturn(userProfileDTO);


        userController.deleteUser(userDetails, 2);
    }

    @Test(expected = UserNotFoundException.class)
    public void testDeleteUser_UserNotFoundException() {

        when(userDetails.getUsername()).thenReturn("rohan@gmail.com");
        when(userService.findUserByEmail("rohan@gmail.com")).thenReturn(null);

        userController.deleteUser(userDetails, 1);
    }


    @Test
    public void testUpdateUser() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userDetails.getUsername()).thenReturn("rohan@gmail.com");
        when(userService.findUserByEmail("rohan@gmail.com")).thenReturn(userProfileDTO);
        when(userService.updateUser(1, userAuthDTO)).thenReturn("User Has Been Updated");

        String result = userController.updateUser(userDetails, userAuthDTO, bindingResult);

        assertEquals("User Has Been Updated", result);
        verify(userService).updateUser(1, userAuthDTO);
    }

    @Test
    public void testUpdateUser_ValidationFailed() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        String result = userController.updateUser(userDetails, userAuthDTO, bindingResult);

        assertEquals("Validation failed: []", result);
        verify(userService, never()).updateUser(anyInt(), any());
    }

    @Test(expected = UserNotFoundException.class)
    public void testUpdateUser_UserNotFoundException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userDetails.getUsername()).thenReturn("rohan@gmail.com");
        when(userService.findUserByEmail("rohan@gmail.com")).thenReturn(null);

        userController.updateUser(userDetails, userAuthDTO, bindingResult);
    }

    @Test
    public void testUpdatePassword_Success() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userDetails.getUsername()).thenReturn("rohan@gmail.com");
        when(userService.findUserByEmail("rohan@gmail.com")).thenReturn(userProfileDTO);
        when(userService.updatePassword(1, userPasswordDTO)).thenReturn("Password updated successfully");

        String result = userController.updatePassword(userDetails, userPasswordDTO, bindingResult);

        assertEquals("Password updated successfully", result);
        verify(userService).updatePassword(1, userPasswordDTO);
    }

    @Test
    public void testUpdatePassword_ValidationFailed() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.emptyList());

        String result = userController.updatePassword(userDetails, userPasswordDTO, bindingResult);

        assertEquals("Validation failed: []", result);
        verify(userService, never()).updatePassword(anyInt(), any());
    }

    @Test(expected = UserNotFoundException.class)
    public void testUpdatePassword_UserNotFoundException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userDetails.getUsername()).thenReturn("rohan@gmail.com");
        when(userService.findUserByEmail("rohan@gmail.com")).thenReturn(null);

        userController.updatePassword(userDetails, userPasswordDTO, bindingResult);
    }

}
