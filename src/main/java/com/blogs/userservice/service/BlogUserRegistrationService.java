package com.blogs.userservice.service;

import com.blogs.userservice.entity.BlogUser;

import java.util.List;

public interface BlogUserRegistrationService {

    public BlogUser findById(Long id);

    BlogUser createUser(BlogUser user);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
    List<BlogUser> findAll();
    Boolean deleteBlogUser(Long id);
}
