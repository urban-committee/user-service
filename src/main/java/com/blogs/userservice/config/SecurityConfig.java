package com.blogs.userservice.config;

import com.blogs.userservice.security.service.BlogUserDetailsServiceImpl;
import com.blogs.userservice.security.service.jwtTokenUtils.AuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {


    AuthenticationTokenFilter jwtAuthFilter;

    @Bean
    public AuthenticationTokenFilter authenticationJwtTokenFilter() {
        jwtAuthFilter = new AuthenticationTokenFilter();
        return jwtAuthFilter;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new BlogUserDetailsServiceImpl();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                       .requestMatchers("/").permitAll()
////                        /* .requestMatchers("/login", "/register", "/2fa", "/2fa-verify").permitAll()
////                        .requestMatchers("/api/auth/**","/api/auth/register" ,"/2fa", "/2fa-verify") */
//                        .anyRequest().authenticated());
//
//
//        return http.build();

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests(authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers("/api/v1.0/blogsite/user/authentication/signin","/api/v1.0/blogsite/user/register","/api/v1.0/blogsite/user/2fa-verify","/api/v1.0/blogsite/user/2fa-enable").permitAll()
                                        .requestMatchers("/api/authentication/signin").authenticated()
                                        .requestMatchers("/api/v1.0/blogsite/user/**").hasAnyRole("USER","ADMIN")

                                        //.requestMatchers("/api/authentication/signin").hasRole("USER")
                                        .anyRequest().denyAll()
                )
                .sessionManagement(sessionManagement ->  sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}
