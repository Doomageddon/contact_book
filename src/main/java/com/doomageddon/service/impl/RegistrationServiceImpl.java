package com.doomageddon.service.impl;

import com.doomageddon.model.dto.RegistrationDto;
import com.doomageddon.model.exception.AlreadyExistsException;
import com.doomageddon.model.mapper.UserMapper;
import com.doomageddon.repository.UserRepository;
import com.doomageddon.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registration(RegistrationDto registrationDto) {

        String userName = registrationDto.getName();

        log.info("Registration user with name: {}", userName);

        if (userRepository.existsByName(userName))
            throw new AlreadyExistsException(format("Name %s already used.", userName));

        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());

        userRepository.save(userMapper.toEntity(userName, encodedPassword));

        log.info("Registration user with name - {} has been successful.", userName);
    }
}
