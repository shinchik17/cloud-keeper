package com.shinchik.cloudkeeper.security;

import com.shinchik.cloudkeeper.model.User;
import com.shinchik.cloudkeeper.security.SecurityUserDetails;
import com.shinchik.cloudkeeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public SecurityUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.findByUsername(username);
        return user
                .map(SecurityUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(username + "not found"));
    }

}
