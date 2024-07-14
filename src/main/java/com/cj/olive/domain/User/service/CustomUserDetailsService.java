package com.cj.olive.domain.User.service;

import com.cj.olive.domain.User.entity.User;
import com.cj.olive.domain.User.error.UserErrorCode;
import com.cj.olive.domain.User.model.CustomUserDetails;
import com.cj.olive.domain.User.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(UserErrorCode.LOGIN_FAILED.getMessage()));
        return new CustomUserDetails(user);
    }
}
