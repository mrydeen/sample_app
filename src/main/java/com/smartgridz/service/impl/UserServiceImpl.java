package com.smartgridz.service.impl;


import com.smartgridz.controller.dto.UserDto;
import com.smartgridz.domain.entity.Role;
import com.smartgridz.domain.RoleType;
import com.smartgridz.domain.entity.User;
import com.smartgridz.dao.RoleDao;
import com.smartgridz.dao.UserDao;
import com.smartgridz.service.UserService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class implements services for the User.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * This is the amount of time a reset password token should live
     */
    private static final Integer RESET_TOKEN_LIFE_TIME = 3 * 3600 * 1000; // 3 hours in milliseconds

    /**
     * This thread is used to keep track of when users request a password reset.  We
     * only want the ability for the password reset token to be active for
     * 3 hours. Need to look at the date that is set when the token is created
     */
    private static class ResetTokenWatchDog extends Thread {

        private UserService userService;

        public ResetTokenWatchDog(UserService userService) {
            this.userService = userService;
        }

        /**
         * This will run forever and check the reset tokens
         */
        public void run() {
            LOG.info("Watchdog started to clean password reset requests");
            while (true) {
                List<User> users = userService.findAllUsersWithResetTokens();
                Date now = new Date();
                for (User user: users) {
                    // If the token data is greater than the life time.
                    Date tokenDate = user.getTokenDate();
                    if ((now.getTime() - tokenDate.getTime()) > RESET_TOKEN_LIFE_TIME) {
                        LOG.info("Found old password reset token for user: {}, cleaning up", user.getUserName());
                        user.setResetPasswordToken(null);
                        user.setTokenDate(null);
                        userService.updateUser(user);
                    }
                }
                // Now wake up every 5 minutes, need to stick to an SLA of around 3 hours
                // for the password reset to clean up.
                try {
                    Thread.sleep(300000L);
                } catch (InterruptedException ie) {
                    LOG.info("User purge thread interrupted", ie);
                }
            }
        }
    }

    private UserDao userDao;
    private RoleDao roleDao;

    /**
     * This encoder comes from the SpringSecurity file.
     */
    private PasswordEncoder passwordEncoder;

    private ResetTokenWatchDog resetTokenWatchDog;

    /**
     * When we first start up, we want to make sure that an administrator user
     * was created
     */
    @PostConstruct
    void checkForAdminUser() {
        User admin = findUserByUserName("admin");
        if (admin == null) {
           admin = new User();
           admin.setUserName("admin");
           admin.setName("Administrator User");
           admin.setPassword(passwordEncoder.encode("xyzzy"));
           admin.setEmail("admin@smartgridz.com");

           Role adminRole = roleDao.findByType(RoleType.ADMINISTRATOR);
           admin.setRole(adminRole);

           // Now save the user.
           userDao.save(admin);
        }
    }

    public UserServiceImpl(UserDao userDao,
                           RoleDao roleDao,
                           PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
        this.resetTokenWatchDog = new ResetTokenWatchDog(this);
        this.resetTokenWatchDog.start();
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setUserName(userDto.getUserName());
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        // encrypt the password using spring security
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        RoleType roleType = RoleType.getFromString(userDto.getRole());
        Role role = roleDao.findByType(roleType);
        user.setRole(role);
        userDao.save(user);
    }

    public void updateUser(User user) {
        userDao.save(user);
    }

    @Override
    public void deleteUser(User user) {
        userDao.delete(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public User findUserByUserName(String userName) {
        return userDao.findByUserName(userName);
    }

    @Override
    public User findByResetPasswordToken(String token) {
        return userDao.findByResetPasswordToken(token);
    }

    public List<User> findAllUsersWithResetTokens() {
        List<User> users = userDao.findAll();
        return users.stream()
                .filter(u -> u.getResetPasswordToken() != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userDao.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public void updateResetPasswordToken(String token, String userName) throws ResourceAccessException {
        User user = userDao.findByUserName(userName);
        if (user != null) {
            user.setResetPasswordToken(token);
            user.setTokenDate(new Date());
            userDao.save(user);
        } else {
            throw new ResourceAccessException("No such user with user name: " + userName);
        }
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        // Make sure to reset the token as well
        user.setResetPasswordToken("");
        user.setTokenDate(null);
        userDao.save(user);
    }

    private UserDto mapToUserDto(User user){
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setUserName(user.getUserName());
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        // TODO: Need to make this pretty.
        userDto.setRole(user.getRole().getType().name());
        return userDto;
    }
}
