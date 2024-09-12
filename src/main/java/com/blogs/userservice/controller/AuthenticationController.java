package com.blogs.userservice.controller;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.blogs.userservice.entity.BlogUser;
import com.blogs.userservice.entity.Role;
import com.blogs.userservice.enums.RoleEnum;
import com.blogs.userservice.payload.request.LoginRequest;
import com.blogs.userservice.payload.request.TwoFactorRequest;
import com.blogs.userservice.payload.response.JwtResponse;
import com.blogs.userservice.repository.BlogUserRepository;
import com.blogs.userservice.security.service.BlogUserDetailsImpl;
//import com.blogs.userservice.security.service.QRCodeService;
import com.blogs.userservice.security.service.GoogleAuthUtil;
import com.blogs.userservice.security.service.TwoFactorAuthService;
import com.blogs.userservice.security.service.jwtTokenUtils.JwtTokenUtils;
import com.blogs.userservice.service.RoleService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.warrenstrange.googleauth.GoogleAuthenticator;


@RestController
@RequestMapping("/api/v1.0/blogsite/user")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);


    @Autowired
    private BlogUserRepository userRepository;

    @Autowired
    RoleService roleService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtTokenUtils jwtUtils;
    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @Autowired
    private GoogleAuthUtil googleAuthUtil;


//    @Autowired
//    private QRCodeService qrCodeService;
//
//    @Autowired
//    private GoogleAuthenticator googleAuthenticator;

    @PostMapping("/authentication/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login process started........");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        BlogUserDetailsImpl userDetails = (BlogUserDetailsImpl) authentication.getPrincipal();
        BlogUser user = userRepository.findByEmail(userDetails.getEmail()).orElseThrow();
        boolean enabled2FA = user.isTwoFactorEnabled();
        logger.info("enabled2FA {}", enabled2FA);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                enabled2FA,
                roles));
    }

//    @PostMapping("/2fa/setup")
//    public String setup2FA(@RequestParam String username) {
//        Optional<BlogUser> user = userRepository.findByEmail(username);
//
//        if (user == null) {
//            return "User not found";
//        }
//
//        String secret = googleAuthenticator.createCredentials().getKey();
//        user.ifPresent(secret);
//        user.setTwoFactorEnabled(true);
//        userRepository.save(user);
//
//        return qrCodeService.generateQRCodeImage(secret, username);
//    }
//
//    @PostMapping("/2fa/verify")
//    public boolean verify2FA(@RequestParam String username, @RequestParam int code) {
//        User user = userRepository.findByUsername(username);
//
//        if (user == null || !user.isTwoFactorEnabled()) {
//            return false;
//        }
//
//        return googleAuthenticator.authorize(user.getSecret(), code);
//    }

    @PostMapping("/2fa-enable")
    public ResponseEntity<String> enable2FA(@RequestBody TwoFactorRequest request) {
        BlogUser user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        System.out.println(user.getEmail() + "," + user.getSecret());
        boolean isCodeValid = twoFactorAuthService.verifyTOTP(user.getSecret(), request.getCode());
        if (isCodeValid) {
            return ResponseEntity.ok("Enable 2FA successful");
        }
        return ResponseEntity.status(403).body("Invalid 2FA code");
    }


    @PostMapping("/2fa-verify")
    public ResponseEntity<String> verify2FA(@RequestBody TwoFactorRequest request) {
        BlogUser user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        boolean isCodeValid = twoFactorAuthService.verifyTOTP(user.getSecret(), request.getCode());
        if (isCodeValid) {
            return ResponseEntity.ok("2FA successful");
        }
        return ResponseEntity.status(403).body("Invalid 2FA code");
    }

//    @PostMapping("/login")
//    public String login(@RequestBody com.blogs.userservice.entity.LoginRequest loginRequest) {
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
//        return jwtUtils.generateJwtToken(authentication);
//    }


    @PutMapping("/{userId}/roles")
    public ResponseEntity<BlogUser> updateUserRoles(@PathVariable Long userId, @RequestBody Set<RoleEnum> roles) {
        BlogUser updatedUser = roleService.updateUserRoles(userId, roles);
        return ResponseEntity.ok(updatedUser);
    }

}