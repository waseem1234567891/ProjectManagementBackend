package com.example.ProjectManagementBackend.services;

import com.example.ProjectManagementBackend.models.CustomUserDetail;
import com.example.ProjectManagementBackend.models.User;
import com.example.ProjectManagementBackend.respositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> byEmail = userRepo.findByEmail(email);
        User user=byEmail.get();
        return new CustomUserDetail(user);
    }
}
