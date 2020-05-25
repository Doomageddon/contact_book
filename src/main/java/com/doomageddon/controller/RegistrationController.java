package com.doomageddon.controller;

import com.doomageddon.model.dto.RegistrationDto;
import com.doomageddon.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public void registration(@RequestBody RegistrationDto registrationDto) {

        log.info("Registration user with name - {}", registrationDto.getName());
        registrationService.registration(registrationDto);
        log.info("Registration successful.");
    }
}
