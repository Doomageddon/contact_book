package com.doomageddon.model.mapper;

import com.doomageddon.model.entity.User;
import org.springframework.stereotype.Component;

import static com.doomageddon.model.entity.Role.ROLE_USER;

@Component
public class UserMapper {

    public User toEntity(String userName, String password) {
        return User.builder()
                .name(userName)
                .password(password)
                .role(ROLE_USER)
                .isEnabled(true)
                .build();
    }
}
