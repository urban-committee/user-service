package com.blogs.userservice.service;


import com.blogs.userservice.entity.BlogUser;
import com.blogs.userservice.entity.Role;
import com.blogs.userservice.enums.RoleEnum;
import com.blogs.userservice.repository.BlogUserRepository;
import com.blogs.userservice.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class RoleService {

    private final BlogUserRepository blogUserRepository;
    private final RoleRepository roleRepository;

    public RoleService(BlogUserRepository blogUserRepository, RoleRepository roleRepository) {
        this.blogUserRepository = blogUserRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public BlogUser updateUserRoles(Long userId, Set<RoleEnum> roleEnums) {
        Optional<BlogUser> userOpt = blogUserRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        BlogUser user = userOpt.get();
        Set<Role> roles = user.getRoles();

        // Clear existing roles
        roles.clear();

        // Add new roles
        for (RoleEnum roleEnum : roleEnums) {
            Role role = roleRepository.findByName(roleEnum)
                    .orElseGet(() -> roleRepository.save(new Role(roleEnum)));
            roles.add(role);
        }

        return blogUserRepository.save(user);
    }
}
