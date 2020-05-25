package com.doomageddon.service.security;

import com.doomageddon.model.entity.User;
import com.doomageddon.model.exception.ResourceNotFoundException;
import com.doomageddon.model.security.UserDetailsImpl;
import com.doomageddon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    public final UserRepository userRepository;

    @Override
    @SneakyThrows
    public UserDetails loadUserByUsername(String userName) {

        log.info("loadUserByUsername invoked with userName: {}", userName);

        User user = userRepository.findByName(userName)
                .orElseThrow(() -> new ResourceNotFoundException(format("User with name %s does not exist. ", userName)));

        log.info("loadUserByUsername returned with user: {}", user);

        return new UserDetailsImpl(user);
    }
}
