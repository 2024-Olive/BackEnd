package com.cj.olive.domain.User.service;

import com.cj.olive.domain.User.entity.User;
import com.cj.olive.domain.User.error.UserErrorCode;
import com.cj.olive.domain.User.model.CustomUserDetails;
import com.cj.olive.domain.User.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Username not found: {}", username);
                    return new UsernameNotFoundException(UserErrorCode.LOGIN_FAILED.getMessage());
                });
        return new CustomUserDetails(user);
    }
}
