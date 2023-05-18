package com.smartgridz.dao;

import com.smartgridz.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * This interface is used to get access to the users
 *
 * It works in conjunction with the JPA.  By extending the JpaRepository
 * it will actually handle the CRUD operations.
 */
public interface UserDao extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByUserName(String userName);

    User findByResetPasswordToken(String token);
}
