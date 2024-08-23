package com.blogs.userservice.repository;

import com.blogs.userservice.entity.BlogUser;
import com.blogs.userservice.entity.Role;
import com.blogs.userservice.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleEnum name);
}
