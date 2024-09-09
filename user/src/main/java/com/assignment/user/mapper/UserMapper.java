package com.assignment.user.mapper;

import com.assignment.user.dto.UserAuthDTO;
import com.assignment.user.dto.UserProfileDTO;
import com.assignment.user.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public UserProfileDTO toDTOWithoutPassword(User user) {
        return modelMapper.map(user, UserProfileDTO.class);
    }

    public UserAuthDTO toDTOWithPassword(User user) {
        return modelMapper.map(user, UserAuthDTO.class);
    }

    public User UserDTOtoEntity(UserProfileDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    public User UserDTOtoEntity(UserAuthDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
