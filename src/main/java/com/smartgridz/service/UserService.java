package com.smartgridz.service;

import com.smartgridz.controller.dto.UserDto;
import com.smartgridz.domain.entity.User;
import jakarta.transaction.Transactional;

import java.util.List;

@Transactional
public interface UserService {
    void saveUser(UserDto userDto);

    void deleteUser(User user);

    void deleteUserByLogin(String login);

    void updateUser(User user);

    User findUserByEmail(String email);

    User findUserByLogin(String login);

    User findUserByUserName(String userName);

    List<UserDto> findAllUsers();

    void updateResetPasswordToken(String token, String userName);

    void updatePassword(User user, String newPassword);

    User findByResetPasswordToken(String token);

    List<User> findAllUsersWithResetTokens();
}
