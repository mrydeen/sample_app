package com.smartgridz.dao;

import com.smartgridz.domain.entity.Role;
import com.smartgridz.domain.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * This class allows access to the Role database.
 */
public interface RoleDao extends JpaRepository<Role, Long> {

    Role findByType(RoleType type);
}
