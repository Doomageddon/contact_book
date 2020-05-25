package com.doomageddon.controller;

import com.doomageddon.model.dto.RegistrationDto;
import com.doomageddon.util.IntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT_UNORDERED;
import static java.util.stream.Stream.of;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@DatabaseSetup("/dbunit/contacts/setup_db.xml")
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("argumentsForRegistration")
    @ExpectedDatabase(assertionMode = NON_STRICT_UNORDERED, value = "/dbunit/registration/expected_for_registration.xml")
    public void registrationTest(RegistrationDto registrationDto) {
        mockMvc.perform(post("/registration")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private Stream<Arguments> argumentsForRegistration() {
        return of(arguments(registrationDto()));
    }

    private RegistrationDto registrationDto() {
        return RegistrationDto.builder().name("John").password("secret").build();
    }
}
