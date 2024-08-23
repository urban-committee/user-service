package com.blogs.userservice.controller;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.blogs.userservice.entity.BlogUser;
import com.blogs.userservice.enums.RoleEnum;
import com.blogs.userservice.payload.request.LoginRequest;
import com.blogs.userservice.payload.response.JwtResponse;
import com.blogs.userservice.security.service.BlogUserDetailsImpl;
import com.blogs.userservice.security.service.jwtTokenUtils.JwtTokenUtils;
import com.blogs.userservice.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1.0/blogsite/user")
public class AuthenticationController {

    @Autowired
    RoleService roleService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtTokenUtils jwtUtils;

    @PostMapping("/authentication/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        BlogUserDetailsImpl userDetails = (BlogUserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PutMapping("/{userId}/roles")
    public ResponseEntity<BlogUser> updateUserRoles(@PathVariable Long userId, @RequestBody Set<RoleEnum> roles) {
        BlogUser updatedUser = roleService.updateUserRoles(userId, roles);
        return ResponseEntity.ok(updatedUser);
    }

}