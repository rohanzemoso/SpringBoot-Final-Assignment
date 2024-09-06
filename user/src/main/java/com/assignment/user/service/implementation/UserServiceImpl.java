package com.assignment.user.service.implementation;

import com.assignment.user.config.JWTService;
import com.assignment.user.dto.UserAuthDTO;
import com.assignment.user.dto.UserProfileDTO;
import com.assignment.user.exception.UserCreationException;
import com.assignment.user.exception.UserNotFoundException;
import com.assignment.user.mapper.UserMapper;
import com.assignment.user.model.User;
import com.assignment.user.repository.UserRepository;
import com.assignment.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           ObjectMapper objectMapper,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           AuthenticationManager authenticationManager,
                           JWTService jwtService,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Override
    public String loginUser(UserAuthDTO userDTO) {
        log.info("loginUser method called");
        try {
            User user = userMapper.UserDTOtoEntity(userDTO);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            Map<String, String> response = new HashMap<>();

            if (authentication.isAuthenticated()) {
                User authenticatedUser = userRepository.findUserByEmail(user.getEmail());
                if (authenticatedUser != null) {
                    response.put("token", jwtService.generateToken(authenticatedUser.getEmail(), authenticatedUser.getUserId()));
                    return objectMapper.writeValueAsString(response);
                } else {
                    return "failure: user not found in the database";
                }
            } else {
                return "failure: authentication failed";
            }
        } catch (Exception e) {
            log.error("Error during loginUser");
            throw new UserNotFoundException("Error occurred during login");
        }
    }

    @Override
    public String createUser(UserAuthDTO userDTO) {
        log.info("createUser method called");
        try {
            User user = userMapper.UserDTOtoEntity(userDTO);
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return "User Has Been Created!";
        } catch (Exception e) {
            log.error("Error during createUser");
            throw new UserCreationException("Error occurred while creating user");
        }
    }

    @Override
    public UserProfileDTO getUser(int id) {
        log.info("getUser method called");
        try {
            User user = userRepository.findById((long) id)
                    .orElseThrow(() -> new UserNotFoundException("Cannot find user"));
            return userMapper.toDTOWithoutPassword(user);
        } catch (Exception e) {
            log.error("Error during getUser");
            throw new UserNotFoundException("Error occurred while retrieving user");
        }
    }

    @Override
    public List<UserProfileDTO> getUsers() {
        log.info("getUsers method called");
        try {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .map(userMapper::toDTOWithoutPassword)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error during getUsers");
            throw new UserNotFoundException("Error occurred while retrieving users");
        }
    }

    @Override
    public void deleteUser(int id) {
        log.info("deleteUser method called");
        try {
            userRepository.deleteById((long) id);
        } catch (Exception e) {
            log.error("Error during deleteUser");
            throw new UserNotFoundException("Error occurred while deleting user");
        }
    }

    @Override
    public UserProfileDTO findUserByEmail(String email) {
        log.info("findUserByEmail method called");
        try {
            User user = userRepository.findUserByEmail(email);
            if (user == null) {
                throw new UserNotFoundException("User not found with email");
            }
            return userMapper.toDTOWithoutPassword(user);
        } catch (Exception e) {
            log.error("Error during findUserByEmail");
            throw new UserNotFoundException("Error occurred while finding user by email");
        }
    }

    @Override
    public String updateUser(int id, UserAuthDTO userDTO) {
        log.info("updateUser method called");
        try {
            User existingUser = userRepository.findById((long) id)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            existingUser.setName(userDTO.getName());
            existingUser.setEmail(userDTO.getEmail());

            userRepository.save(existingUser);
            return "User Has Been Updated";
        } catch (Exception e) {
            log.error("Error during updateUser");
            throw new UserNotFoundException("Error occurred while updating user");
        }
    }
}
