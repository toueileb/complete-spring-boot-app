package com.springapi.springapi.service;

import com.springapi.springapi.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);

    UserDto getUser(String email);

    UserDto getUserByUserId(String userId);

    UserDto updateUser(String userId,UserDto userDto);

    void delete(String userId);

    List<UserDto> getUsers(int page, int limit);
}
