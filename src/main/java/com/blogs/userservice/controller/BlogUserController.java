package com.blogs.userservice.controller;


import com.blogs.userservice.entity.BlogUser;
import com.blogs.userservice.entity.Role;
import com.blogs.userservice.enums.RoleEnum;
import com.blogs.userservice.exception.MessageResponse;
import com.blogs.userservice.service.BlogUserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1.0/blogsite/user")
public class BlogUserController {

    @Autowired
    private BlogUserRegistrationService blogUserRegistrationService;

    @Autowired
    PasswordEncoder encoder;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody BlogUser user) {
        if (blogUserRegistrationService.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (blogUserRegistrationService.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        user.setPassword(encoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setId(1L);
        roles.add(role);
        user.setRoles(roles);
        blogUserRegistrationService.createUser(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/{id}")
    public BlogUser getUserById(@PathVariable Long id) {
        return blogUserRegistrationService.findById(id);
    }
    @GetMapping("/all")
    public List<BlogUser> getAllUser() {
        return blogUserRegistrationService.findAll();
    }

}
