package com.assignment.user.service;

import com.assignment.user.dto.UserAuthDTO;
import com.assignment.user.dto.UserPasswordDTO;
import com.assignment.user.dto.UserProfileDTO;
import java.util.List;

public interface UserService {
    String loginUser(UserAuthDTO user);
    String createUser(UserAuthDTO user);
    UserProfileDTO getUser(int id);
    List<UserProfileDTO> getUsers();
    void deleteUser(int id);
    UserProfileDTO findUserByEmail(String email);
    String updateUser(int id, UserAuthDTO user);
    String updatePassword(int id, UserPasswordDTO user);
}
