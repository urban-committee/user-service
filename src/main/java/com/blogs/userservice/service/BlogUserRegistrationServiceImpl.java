package com.blogs.userservice.service;

import com.blogs.userservice.entity.BlogUser;
import com.blogs.userservice.repository.BlogUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogUserRegistrationServiceImpl implements BlogUserRegistrationService {


    @Autowired
    private BlogUserRepository blogUserRepository;


    @Override
    public BlogUser findById(Long id) {
        return blogUserRepository.findById(id).get();
    }

    @Override
    public BlogUser createUser(BlogUser user) {
        return blogUserRepository.save(user);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return blogUserRepository.findByUsername(username).isPresent();
    }

    @Override
    public Boolean existsByEmail(String email) {
        return blogUserRepository.findByEmail(email).isPresent();
    }

    @Override
    public List<BlogUser> findAll() {
        return blogUserRepository.findAll();
    }


}
