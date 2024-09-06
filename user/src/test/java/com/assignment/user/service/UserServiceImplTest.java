package com.assignment.user.service;

import com.assignment.user.config.JWTService;
import com.assignment.user.dto.UserAuthDTO;
import com.assignment.user.dto.UserProfileDTO;
import com.assignment.user.exception.UserCreationException;
import com.assignment.user.exception.UserNotFoundException;
import com.assignment.user.mapper.UserMapper;
import com.assignment.user.model.User;
import com.assignment.user.repository.UserRepository;
import com.assignment.user.service.implementation.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserAuthDTO userAuthDTO;
    private UserProfileDTO userProfileDTO;
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User(1, "Rohan", "rohan@gmail.com", "password", null);
        userAuthDTO = new UserAuthDTO(1, "Rohan", "rohan@gmail.com", "password");
        userProfileDTO = new UserProfileDTO(1, "Rohan", "rohan@gmail.com");
        authentication = mock(Authentication.class);
    }

    @Test
    public void testLoginUser_SuccessfulAuthentication() throws Exception {
        when(userMapper.UserDTOtoEntity(userAuthDTO)).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);
        when(jwtService.generateToken(user.getEmail(), user.getUserId())).thenReturn("mockToken");

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", "mockToken");
        when(objectMapper.writeValueAsString(tokenMap)).thenReturn("{\"token\":\"mockToken\"}");

        String result = userService.loginUser(userAuthDTO);

        assertEquals("{\"token\":\"mockToken\"}", result);
        verify(userMapper, times(1)).UserDTOtoEntity(userAuthDTO);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(user.getEmail(), user.getUserId());
    }

    @Test
    public void testLoginUser_UserNotFound() throws Exception {
        when(userMapper.UserDTOtoEntity(userAuthDTO)).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(null);

        String result = userService.loginUser(userAuthDTO);

        assertEquals("failure: user not found in the database", result);
        verify(userRepository, times(1)).findUserByEmail(user.getEmail());
    }

    @Test
    public void testLoginUser_AuthenticationFailed() throws Exception {
        when(userMapper.UserDTOtoEntity(userAuthDTO)).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        String result = userService.loginUser(userAuthDTO);

        assertEquals("failure: authentication failed", result);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void testLoginUser_ExceptionThrown() throws Exception {
        when(userMapper.UserDTOtoEntity(userAuthDTO)).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenAnswer((Answer<Authentication>) invocation -> {
                    throw new RuntimeException("Authentication error");
                });

        assertThrows(UserNotFoundException.class, () -> userService.loginUser(userAuthDTO));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void testCreateUser_Success() {
        when(userMapper.UserDTOtoEntity(userAuthDTO)).thenReturn(user);
        when(bCryptPasswordEncoder.encode(userAuthDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        String result = userService.createUser(userAuthDTO);

        assertEquals("User Has Been Created!", result);
        verify(userMapper, times(1)).UserDTOtoEntity(userAuthDTO);
        verify(bCryptPasswordEncoder, times(1)).encode(userAuthDTO.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testCreateUser_Exception() {
        when(userMapper.UserDTOtoEntity(userAuthDTO)).thenReturn(user);
        doThrow(RuntimeException.class).when(userRepository).save(user);

        assertThrows(UserCreationException.class, () -> userService.createUser(userAuthDTO));
    }

    @Test
    public void testGetUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTOWithoutPassword(user)).thenReturn(userProfileDTO);

        UserProfileDTO result = userService.getUser(1);

        assertNotNull(result);
        assertEquals("Rohan", result.getName());
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toDTOWithoutPassword(user);
    }

    @Test
    public void testGetUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(1));
    }

    @Test
    public void testGetUsers_Success() {
        List<User> userList = Collections.singletonList(user);
        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toDTOWithoutPassword(user)).thenReturn(userProfileDTO);

        List<UserProfileDTO> result = userService.getUsers();

        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDTOWithoutPassword(user);
    }

    @Test
    public void testGetUsers_Exception() {
        when(userRepository.findAll()).thenThrow(RuntimeException.class);

        assertThrows(UserNotFoundException.class, () -> userService.getUsers());
    }

    @Test
    public void testDeleteUser_Success() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteUser_Exception() {
        doThrow(RuntimeException.class).when(userRepository).deleteById(1L);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1));
    }

    @Test
    public void testFindUserByEmail_Success() {
        when(userRepository.findUserByEmail("rohan@gmail.com")).thenReturn(user);
        when(userMapper.toDTOWithoutPassword(user)).thenReturn(userProfileDTO);

        UserProfileDTO result = userService.findUserByEmail("rohan@gmail.com");

        assertNotNull(result);
        assertEquals("rohan@gmail.com", result.getEmail());
        verify(userRepository, times(1)).findUserByEmail("rohan@gmail.com");
        verify(userMapper, times(1)).toDTOWithoutPassword(user);
    }

    @Test
    public void testFindUserByEmail_UserNotFound() {
        when(userRepository.findUserByEmail("rohan@gmail.com")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.findUserByEmail("rohan@gmail.com"));
    }

    @Test
    public void testUpdateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        String result = userService.updateUser(1, userAuthDTO);

        assertEquals("User Has Been Updated", result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1, userAuthDTO));
    }
}
