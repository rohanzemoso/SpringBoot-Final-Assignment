package com.assignment.user.controller;

import com.assignment.user.util.Constants;
import com.assignment.user.dto.UserProfileDTO;
import com.assignment.user.dto.UserAuthDTO;
import com.assignment.user.model.Post;
import com.assignment.user.service.PostServiceClient;
import com.assignment.user.service.UserService;
import com.assignment.user.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.List;

@RestController
@RequestMapping(Constants.USER_BASE_URL)
public class UserController {

    private final UserService userService;
    private final PostServiceClient postServiceClient;

    @Autowired
    public UserController(UserService userService, PostServiceClient postServiceClient) {
        this.userService = userService;
        this.postServiceClient = postServiceClient;
    }

    @GetMapping(Constants.GET_POSTS)
    public List<Post> getPostsForUser(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        UserProfileDTO userDTO = userService.findUserByEmail(username);
        if (userDTO == null) {
            throw new UserNotFoundException("User not found with email: " + username);
        }
        int userID = userDTO.getUserId();
        return postServiceClient.getPostsByUserId(userID);
    }

    @PostMapping(Constants.LOGIN_USER)
    public String loginUser(@Valid @RequestBody UserAuthDTO userAuthDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "Validation failed: " + result.getAllErrors();
        }
        return userService.loginUser(userAuthDTO);
    }

    @GetMapping(Constants.GET_USERS)
    public List<UserProfileDTO> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(Constants.GET_USER_BY_ID)
    public UserProfileDTO getUser(@PathVariable int id) {
        UserProfileDTO userDTO = userService.getUser(id);
        if (userDTO == null) {
            throw new UserNotFoundException("User not found with ID: " + id);
        }
        return userDTO;
    }

    @PostMapping(Constants.CREATE_USER)
    public String createUser(@Valid @RequestBody UserAuthDTO userAuthDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "Validation has failed: " + result.getAllErrors();
        }
        return userService.createUser(userAuthDTO);
    }

    @DeleteMapping(Constants.DELETE_USER)
    public String deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return "User Deleted";
    }

    @PutMapping(Constants.UPDATE_USER)
    public String updateUser(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UserAuthDTO userAuthDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "Validation failed: " + result.getAllErrors();
        }
        String email = userDetails.getUsername();
        UserProfileDTO userDTO = userService.findUserByEmail(email);
        if (userDTO == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        int id = userDTO.getUserId();
        return userService.updateUser(id, userAuthDTO);
    }
}
