package com.blogs.userservice.repository;

import com.blogs.userservice.entity.BlogUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogUserRepository extends JpaRepository<BlogUser, Long> {
    Optional<BlogUser> findByUsername(String username);
    Optional<BlogUser>  findByEmail(String email);
}
