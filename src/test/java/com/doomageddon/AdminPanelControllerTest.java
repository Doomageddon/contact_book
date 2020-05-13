package com.doomageddon;

import com.doomageddon.model.dto.ContactDto;
import com.doomageddon.model.dto.EditContactDto;
import com.doomageddon.model.entity.Contact;
import com.doomageddon.model.entity.User;
import com.doomageddon.repository.ContactRepository;
import com.doomageddon.repository.UserRepository;
import com.doomageddon.util.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static com.doomageddon.model.entity.Role.ROLE_ADMIN;
import static java.util.stream.Stream.of;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(PER_CLASS)
public class AdminPanelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void setupDb() {

        User user = userRepository.save(User.builder().id(1L).name("John Doe").build());
        contactRepository.save(Contact.builder().id(1L).firstName("Thomas").lastName("Hawk").phone(0L).user(user).build());
        contactRepository.save(Contact.builder().id(2L).firstName("Peter").lastName("Hawk").phone(0L).user(user).build());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("argumentsForGetContacts")
    public void getUserContacts(ContactDto contactDto) {
        mockMvc.perform(get("/admin/panel/book/contact")
                .param("userId", "1")
                .param("page", "1")
                .param("size", "1")
                .with(tokenProvider.addBearerToken(ROLE_ADMIN.name())))
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
    @MethodSource("argumentsForEditContact")
    public void editContactTest(EditContactDto editContactDto) {
        mockMvc.perform(put("/admin/panel/book/contact/{contactId}", 2)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editContactDto))
                .with(tokenProvider.addBearerToken(ROLE_ADMIN.name())))
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

    private EditContactDto editContactDto() {
        return EditContactDto.builder().firstName("Peter").lastName("Hawk").phone(3333L).build();
    }

    @Test
    @SneakyThrows
    public void deleteContactTest() {
        mockMvc.perform(delete("/admin/panel/book/contact/{contactId}", 2)
                .with(tokenProvider.addBearerToken(ROLE_ADMIN.name())))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
