package com.blogs.userservice.controller;


import com.blogs.userservice.entity.BlogUser;
import com.blogs.userservice.entity.Role;
import com.blogs.userservice.enums.RoleEnum;
import com.blogs.userservice.exception.MessageResponse;
import com.blogs.userservice.security.service.BlogUserDetailsImpl;
import com.blogs.userservice.security.service.GoogleAuthUtil;
import com.blogs.userservice.service.BlogUserRegistrationService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1.0/blogsite/user")
public class BlogUserController {

    private static final Logger logger = LoggerFactory.getLogger(BlogUserController.class);

    @Autowired
    private BlogUserRegistrationService blogUserRegistrationService;

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    private GoogleAuthUtil googleAuthUtil;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody BlogUser user) {
        if (blogUserRegistrationService.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!",null));
        }

        if (blogUserRegistrationService.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!",null));
        }
        user.setPassword(encoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setId(1L);
        roles.add(role);
        user.setRoles(roles);
        GoogleAuthenticatorKey credentials = googleAuthUtil.createCredentials();
        user.setSecret(credentials.getKey());
        String qrcode=null;
        blogUserRegistrationService.createUser(user);
        if (user.isTwoFactorEnabled()){
           qrcode=googleAuthUtil.getQRCode(credentials);
        }
        return ResponseEntity.ok(new MessageResponse("User registered successfully!",qrcode));
    }


    @GetMapping("/{id}")
    public BlogUser getUserById(@PathVariable Long id) {
        return blogUserRegistrationService.findById(id);
    }
    @GetMapping("/all")
    public List<BlogUser> getAllUser() {
        return blogUserRegistrationService.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteBlog(@PathVariable Long id) {
        Boolean updated = blogUserRegistrationService.deleteBlogUser(id);
        return ResponseEntity.ok(updated);
    }

}
