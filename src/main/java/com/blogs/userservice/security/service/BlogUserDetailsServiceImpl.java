package com.blogs.userservice.security.service;


import com.blogs.userservice.repository.BlogUserRepository;
import com.blogs.userservice.entity.BlogUser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class BlogUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    BlogUserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        BlogUser user = userRepository.findByEmail(email)//userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + email));

        return BlogUserDetailsImpl.build(user);
    }

}