package com.doomageddon.controller;

import com.doomageddon.model.dto.ContactDto;
import com.doomageddon.repository.ContactRepository;
import com.doomageddon.repository.UserRepository;
import com.doomageddon.util.IntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT_UNORDERED;
import static java.util.stream.Stream.of;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@DatabaseSetup("/dbunit/contacts/setup_db.xml")
public class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @ParameterizedTest
    @WithUserDetails(value = "John Doe")
    @MethodSource("argumentsForGetContacts")
    public void getUserContactsTest(ContactDto contactDto) {
        mockMvc.perform(get("/book/contact")
                .param("page", "1")
                .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(2)))
                .andExpect(jsonPath("$.content[0].firstName", is(contactDto.getFirstName())))
                .andExpect(jsonPath("$.content[0].lastName", is(contactDto.getLastName())))
                .andDo(print());
    }

    private Stream<Arguments> argumentsForGetContacts() {
        return of(arguments(contactDto()));
    }

    private ContactDto contactDto() {
        return ContactDto.builder().firstName("Peter").lastName("Hawk").phone(0L).build();
    }

    @SneakyThrows
    @ParameterizedTest
    @WithUserDetails(value = "John Doe")
    @MethodSource("argumentsForCreateContact")
    @ExpectedDatabase(assertionMode = NON_STRICT_UNORDERED, value = "/dbunit/contacts/expected_for_create.xml")
    public void createContactTest(ContactDto createContactDto) {
        mockMvc.perform(post("/book/contact")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createContactDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(createContactDto.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(createContactDto.getLastName())))
                .andExpect(jsonPath("$.phone", is(0)))
                .andDo(print());
    }

    private Stream<Arguments> argumentsForCreateContact() {
        return of(arguments(createContactDto()));
    }

    private ContactDto createContactDto() {
        return ContactDto.builder().firstName("Simon").lastName("Doe").build();
    }

    @SneakyThrows
    @ParameterizedTest
    @WithUserDetails(value = "Admin")
    @MethodSource("argumentsForEditContact")
    @ExpectedDatabase(assertionMode = NON_STRICT_UNORDERED, value = "/dbunit/contacts/expected_for_edit_by_admin.xml")
    public void editContactTest(ContactDto editContactDto) {
        mockMvc.perform(put("/book/contact/{contactId}", 2)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editContactDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.firstName", is(editContactDto.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(editContactDto.getLastName())))
                .andExpect(jsonPath("$.phone", is(3333)))
                .andDo(print());
    }

    private Stream<Arguments> argumentsForEditContact() {
        return of(arguments(editContactDto()));
    }

    private ContactDto editContactDto() {
        return ContactDto.builder().firstName("Peter").lastName("Hawk").phone(3333L).build();
    }

    @Test
    @SneakyThrows
    @WithUserDetails(value = "Admin")
    @ExpectedDatabase(assertionMode = NON_STRICT_UNORDERED, value = "/dbunit/contacts/expected_for_delete_by_admin.xml")
    public void deleteContactTest() {
        mockMvc.perform(delete("/book/contact/{contactId}", 2))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
