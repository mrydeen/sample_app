package com.smartgridz.config.web;

import com.smartgridz.domain.entity.Role;
import com.smartgridz.domain.entity.User;
import com.smartgridz.dao.UserDao;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * As we want to use the users from the database for authentication, we need
 * to load the users from the database.  This will be used by the SpringSecurity file.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserDao userDao;

    public CustomUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userDao.findByLogin(userName);
        LOG.error("Finding user by userName: {}", userName);
        if (user != null) {
            LOG.error("User not null.: {}", user.toString());
            List<Role> roleList = new ArrayList<Role>();
            roleList.add(user.getRole());
            return new org.springframework.security.core.userdetails.User(user.getLogin(),
                    user.getPassword(),
                    mapRoleToAuthorities(roleList));
        }else{
            LOG.error("User not found");
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

    private Collection<? extends GrantedAuthority> mapRoleToAuthorities(Collection<Role> roles) {
        Collection<? extends GrantedAuthority> mapRoles = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getType().getSpringAuthType()))
                .collect(Collectors.toList());
        return mapRoles;
    }
}
