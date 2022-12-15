package com.example.system.Services;


import com.example.system.Entities.User;
import com.example.system.Repositories.UserRepo;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

import java.lang.module.ResolutionException;
import java.util.HashSet;
import java.util.Set;

@Service
class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepo userRepository;

    @Override
    public org.springframework.security.core.userdetails.User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }
        if(!user.isEnabled()){
            throw new DisabledException("User is disabled");
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().getRole()));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPass(), grantedAuthorities);
    }

}