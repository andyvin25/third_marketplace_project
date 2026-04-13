package com.marketplace.Auth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("There is no user with this email " + username));
        System.out.println("user.getUsername() = " + user.getUsername());
        System.out.println("user.getAccountRoles() = " + user.getAccountRoles());
        for (Role role: user.getAccountRoles()) {
            System.out.println("role.getRoleName() = " + role.getRoleName());
            for (Permission permission: role.getPermissions()) {
                System.out.println("permission.getName() = " + permission.getName());
            }
        }
        return (UserDetails) user;
    }

}
