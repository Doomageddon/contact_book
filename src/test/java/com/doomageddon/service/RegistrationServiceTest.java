package com.doomageddon.service;

import com.doomageddon.model.dto.RegistrationDto;
import com.doomageddon.model.entity.User;
import com.doomageddon.model.exception.AlreadyExistsException;
import com.doomageddon.model.mapper.UserMapper;
import com.doomageddon.repository.UserRepository;
import com.doomageddon.service.impl.RegistrationServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Stream;

import static com.doomageddon.model.entity.Role.ROLE_USER;
import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    private final static String ENCRYPTED_PASSWORD = "{bcrypt}$2a$10$e3kqKRWc6uSaGCsjmijGw.7JyfnZSekmMcmLY4paqCfd03O0Ei2i6";

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationServiceImpl service;

    @ParameterizedTest
    @MethodSource("argumentsForRegistration")
    public void registrationTest(RegistrationDto registrationDto, User user) {

        String userName = registrationDto.getName();

        when(userRepository.existsByName(userName)).thenReturn(false);

        when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn(ENCRYPTED_PASSWORD);

        when(userMapper.toEntity(userName, ENCRYPTED_PASSWORD)).thenReturn(user);

        when(userRepository.save(user)).thenReturn(user);

        service.registration(registrationDto);

        verify(userRepository, times(1)).existsByName(userName);
        verify(passwordEncoder, times(1)).encode(registrationDto.getPassword());
        verify(userMapper, times(1)).toEntity(userName, ENCRYPTED_PASSWORD);
        verify(userRepository, times(1)).save(user);

    }

    private static Stream<Arguments> argumentsForRegistration() {
        return of(arguments(getRegistrationDto(), getUser()));
    }

    private static RegistrationDto getRegistrationDto() {
        return RegistrationDto.builder().name("John").password("secret").build();
    }

    private static User getUser() {
        return User.builder()
                .id(1L)
                .role(ROLE_USER)
                .isEnabled(true)
                .name("John")
                .password(ENCRYPTED_PASSWORD)
                .build();
    }

    @ParameterizedTest
    @MethodSource("argumentsForRegistrationWithException")
    public void registrationWithUserAlreadyExistsExceptionTest(RegistrationDto registrationDto) {

        String userName = registrationDto.getName();

        when(userRepository.existsByName(userName)).thenReturn(true);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class,
                () -> service.registration(registrationDto));

        verify(userRepository, times(1)).existsByName(userName);
        assertTrue(exception.getMessage().contains("Name John already used."));
    }

    private static Stream<Arguments> argumentsForRegistrationWithException() {
        return of(arguments(getRegistrationDto()));
    }
}
