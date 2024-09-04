package com.blogs.userservice.controller;


import com.blogs.userservice.entity.BlogUser;
import com.blogs.userservice.repository.BlogUserRepository;
import com.blogs.userservice.security.service.BlogUserDetailsImpl;
import com.blogs.userservice.security.service.TwoFactorAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class TwoFactorAuthController {
    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthController.class);

    @Autowired
    private BlogUserRepository userService;

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @GetMapping("/2fa")
    public ResponseEntity<String> generateQRCode(Authentication authentication) throws Exception {
        BlogUserDetailsImpl userPrincipal = (BlogUserDetailsImpl) authentication.getPrincipal();
        BlogUser user = userService.findByEmail(userPrincipal.getEmail()).orElseThrow();
        String secret = twoFactorAuthService.generateSecretKey();
        user.setSecret(secret);
        userService.save(user);
        String qrCodeImage = twoFactorAuthService.getQRCode(secret, user.getUsername());
        return ResponseEntity.ok(qrCodeImage);
    }

    @PostMapping("/2fa-verify")
    public ResponseEntity<String> verify2FA(@RequestParam int code, Authentication authentication) {
        BlogUserDetailsImpl userPrincipal = (BlogUserDetailsImpl) authentication.getPrincipal();
        BlogUser user = userService.findByEmail(userPrincipal.getEmail()).orElseThrow();
        boolean isCodeValid = twoFactorAuthService.verifyTOTP(user.getSecret(), code);
        if (isCodeValid) {
            return ResponseEntity.ok("2FA successful");
        }
        return ResponseEntity.status(403).body("Invalid 2FA code");
    }
}
