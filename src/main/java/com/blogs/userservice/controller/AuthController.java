//package com.blogs.userservice.controller;
//
//import com.blogs.userservice.entity.BlogUser;
//import com.blogs.userservice.entity.Role;
//import com.blogs.userservice.entity.LoginRequest;
//import com.blogs.userservice.repository.BlogUserRepository;
//import com.blogs.userservice.security.service.GoogleAuthUtil;
//import com.blogs.userservice.security.service.jwtTokenUtils.JwtTokenUtils;
//import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Set;
//
//@RestController
//@RequestMapping("/api/auth")
//public class AuthController {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JwtTokenUtils jwtTokenUtil;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private BlogUserRepository userRepository;
//
//@Autowired
//    private GoogleAuthUtil googleAuthUtil;
//
//    @PostMapping("/register")
//    public String register(@RequestBody BlogUser user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        Set<Role> roles = new HashSet<>();
//        Role role = new Role();
//        role.setId(1L);
//        user.setRoles( roles);
//        user.setTwoFactorEnabled(false); // Initially disabled until 2FA is enabled
//        GoogleAuthenticatorKey credentials = googleAuthUtil.createCredentials();
//        user.setSecret(credentials.getKey());
//        userRepository.save(user);
//        return googleAuthUtil.getQRCode(credentials); // Send QR Code URL
//    }
//
//    @PostMapping("/login")
//    public String login(@RequestBody LoginRequest loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequest.getEmail(),
//                        loginRequest.getPassword()
//                )
//        );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String username = loginRequest.getEmail();
//        BlogUser user = userRepository.findByEmail(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!googleAuthUtil.authorize(user.getSecret(), loginRequest.getVerificationCode())) {
//            throw new RuntimeException("Invalid 2FA code");
//        }
//
//        user.setTwoFactorEnabled(true);
//        userRepository.save(user);
//
//        return jwtTokenUtil.generateJwtToken(authentication);
//    }
//}
