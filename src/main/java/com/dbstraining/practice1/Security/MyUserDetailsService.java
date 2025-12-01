package com.dbstraining.practice1.Security;

import com.dbstraining.practice1.Model.Manager;
import com.dbstraining.practice1.Model.User;
import com.dbstraining.practice1.Repository.ManagerRepository;
import com.dbstraining.practice1.Repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;

    public MyUserDetailsService(UserRepository userRepository, ManagerRepository managerRepository) {
        this.userRepository = userRepository;
        this.managerRepository = managerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Try user
        User user = userRepository.findByEmail(username);
        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }

        // Try manager
        Manager manager = managerRepository.findByEmail(username);
        if (manager != null) {
            return new org.springframework.security.core.userdetails.User(
                    manager.getEmail(),
                    manager.getPassword(),
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_MANAGER"))
            );
        }

        throw new UsernameNotFoundException("No user or manager found with email: " + username);
    }
}
