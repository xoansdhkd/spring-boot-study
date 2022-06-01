package com.example.test.config.jwt;

import com.example.test.domain.user.User;
import com.example.test.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
         Optional<User> findUser = userRepository.findByUserName(username);
         if (findUser.isEmpty()) {
             throw new RuntimeException();
         }
         User user = findUser.get();
         return new UserDetailsImpl(user, Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString())));

    }
}
