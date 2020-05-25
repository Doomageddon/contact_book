package com.doomageddon.service;

import com.doomageddon.model.dto.ContactDto;
import com.doomageddon.model.entity.Contact;
import com.doomageddon.model.entity.User;
import com.doomageddon.model.exception.ResourceNotFoundException;
import com.doomageddon.model.mapper.ContactMapper;
import com.doomageddon.repository.ContactRepository;
import com.doomageddon.repository.UserRepository;
import com.doomageddon.service.impl.ContactServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.doomageddon.model.entity.Role.ROLE_ADMIN;
import static com.doomageddon.model.entity.Role.ROLE_USER;
import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

    @Mock
    private ContactMapper contactMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactServiceImpl service;

    @ParameterizedTest
    @MethodSource("argumentsForGetContacts")
    public void getContactsTest(User user, Pageable pageable,
                                Page<ContactDto> contactDtoPage, Page<Contact> contactPage,
                                Contact firstContact, Contact secondContact,
                                ContactDto firstContactDto, ContactDto secondContactDto) {

        when(userRepository.getOne(1L)).thenReturn(user);

        when(contactRepository.getByUser(user, pageable)).thenReturn(contactPage);

        when(contactMapper.toDto(firstContact)).thenReturn(firstContactDto);
        when(contactMapper.toDto(secondContact)).thenReturn(secondContactDto);

        Page<ContactDto> actual = service.getContacts(1L, pageable);

        assertEquals(2, actual.getTotalElements());
        verify(userRepository, times(1)).getOne(1L);
        verify(contactRepository, times(1)).getByUser(user, pageable);
        verify(contactMapper, times(1)).toDto(firstContact);
        verify(contactMapper, times(1)).toDto(secondContact);
        assertEquals(contactDtoPage, actual);

    }

    private static Stream<Arguments> argumentsForGetContacts() {
        return of(arguments(
                getUser(), pageable(),
                getContactDtoPage(), getContactPage(),
                getFirstContact(), getSecondContact(),
                getFirstContactDto(), getSecondContactDto()
        ));
    }

    private static User getUser() {
        return User.builder().id(1L).role(ROLE_USER).contacts(new HashSet<>()).build();
    }

    private static User getAdmin() {
        return User.builder().id(1L).role(ROLE_ADMIN).contacts(new HashSet<>()).build();
    }

    private static Pageable pageable() {
        return PageRequest.of(0, 20);
    }

    private static Contact getFirstContact() {
        return Contact.builder().id(1L).firstName("Peter").lastName("Hawk").phone(0L).build();
    }

    private static Contact getSecondContact() {
        return Contact.builder().id(2L).firstName("Thomas").lastName("Hawk").phone(0L).build();
    }

    private static Page<Contact> getContactPage() {
        return new PageImpl<>(List.of(getFirstContact(), getSecondContact()));
    }

    private static ContactDto getFirstContactDto() {
        return ContactDto.builder().id(1L).firstName("Peter").lastName("Hawk").phone(0L).build();
    }

    private static ContactDto getSecondContactDto() {
        return ContactDto.builder().id(2L).firstName("Thomas").lastName("Hawk").phone(0L).build();
    }

    private static Page<ContactDto> getContactDtoPage() {
        return new PageImpl<>(List.of(getFirstContactDto(), getSecondContactDto()));
    }

    @ParameterizedTest
    @MethodSource("argumentsForCreateContact")
    public void createContactTest(User user, Contact contact, ContactDto contactDto) {

        when(userRepository.getOne(1L)).thenReturn(user);

        when(contactMapper.toEntity(contactDto)).thenReturn(contact);
        when(contactMapper.toDto(contact)).thenReturn(contactDto);

        ContactDto actual = service.createContact(contactDto, 1L);

        verify(userRepository, times(1)).getOne(1L);
        verify(contactMapper, times(1)).toEntity(contactDto);
        assertEquals(contactDto, actual);

    }

    private static Stream<Arguments> argumentsForCreateContact() {
        return of(arguments(getUser(), getContactForCreate(), getContactDtoForCreate()));
    }

    private static Contact getContactForCreate() {
        return Contact.builder().firstName("Peter").lastName("Hawk").phone(0L).build();
    }

    private static ContactDto getContactDtoForCreate() {
        return ContactDto.builder().firstName("Peter").lastName("Hawk").phone(0L).build();
    }

    @ParameterizedTest
    @MethodSource("argumentsForEditWithRoleUser")
    public void editContactForUserRoleTest(User user, Contact contact, ContactDto contactDto, Contact editedContact) {

        when(userRepository.getOne(1L)).thenReturn(user);

        when(contactRepository.getByIdAndUser(1L, user)).thenReturn(Optional.of(contact));
        when(contactMapper.edit(contactDto, contact)).thenReturn(editedContact);
        when(contactMapper.toDto(editedContact)).thenReturn(contactDto);

        ContactDto actual = service.editContact(contactDto, 1L, 1L);

        verify(userRepository, times(1)).getOne(1L);
        verify(contactRepository, times(1)).getByIdAndUser(1L, user);
        verify(contactMapper, times(1)).edit(contactDto, contact);
        verify(contactMapper, times(1)).toDto(editedContact);
        assertEquals(contactDto, actual);

    }

    private static Stream<Arguments> argumentsForEditWithRoleUser() {
        return of(arguments(getUser(), getContactForEdit(), getContactDtoForEdit(), getEditedContact()));
    }

    @ParameterizedTest
    @MethodSource("argumentsForEditWithRoleUserThrowException")
    public void editContactForUserRoleWithExceptionTest(User user, ContactDto contactDto) {

        when(userRepository.getOne(1L)).thenReturn(user);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.editContact(contactDto, 1L, 1L));

        verify(userRepository, times(1)).getOne(1L);
        assertTrue(exception.getMessage().contains("You don`t have such contact."));

    }

    private static Stream<Arguments> argumentsForEditWithRoleUserThrowException() {
        return of(arguments(getUser(), getContactDtoForEdit()));
    }

    @ParameterizedTest
    @MethodSource("argumentsForEditWithRoleAdmin")
    public void editContactForAdminRoleTest(User user, Contact contact, ContactDto contactDto, Contact editedContact) {

        when(userRepository.getOne(1L)).thenReturn(user);

        when(contactRepository.getOne(1L)).thenReturn(contact);
        when(contactMapper.edit(contactDto, contact)).thenReturn(editedContact);
        when(contactMapper.toDto(editedContact)).thenReturn(contactDto);

        ContactDto actual = service.editContact(contactDto, 1L, 1L);

        verify(userRepository, times(1)).getOne(1L);
        verify(contactRepository, times(1)).getOne(1L);
        verify(contactMapper, times(1)).edit(contactDto, contact);
        verify(contactMapper, times(1)).toDto(editedContact);
        assertEquals(contactDto, actual);

    }

    private static Stream<Arguments> argumentsForEditWithRoleAdmin() {
        return of(arguments(getAdmin(), getContactForEdit(), getContactDtoForEdit(), getEditedContact()));
    }

    private static Contact getContactForEdit() {
        return Contact.builder().firstName("Peter").lastName("Hawk").phone(0L).build();
    }

    private static Contact getEditedContact() {
        return Contact.builder().firstName("Peter").lastName("Hawk").phone(333L).build();
    }

    private static ContactDto getContactDtoForEdit() {
        return ContactDto.builder().firstName("Peter").lastName("Hawk").phone(333L).build();
    }

    @Test
    public void deleteContactWithAdminTest() {

        when(userRepository.getOne(1L)).thenReturn(getAdmin());

        doNothing().when(contactRepository).deleteById(1L);

        service.deleteContact(1L, 1L);

        verify(userRepository, times(1)).getOne(1L);
        verify(contactRepository, times(1)).deleteById(1L);

    }
}
