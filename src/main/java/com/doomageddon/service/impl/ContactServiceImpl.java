package com.doomageddon.service.impl;

import com.doomageddon.model.dto.ContactDto;
import com.doomageddon.model.entity.Contact;
import com.doomageddon.model.entity.User;
import com.doomageddon.model.exception.ResourceNotFoundException;
import com.doomageddon.model.mapper.ContactMapper;
import com.doomageddon.repository.ContactRepository;
import com.doomageddon.repository.UserRepository;
import com.doomageddon.service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.doomageddon.model.entity.Role.ROLE_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactMapper contactMapper;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    @Override
    public Page<ContactDto> getContacts(Long userId, Pageable pageable) {

        log.info("'getContacts' invoked for user with id - {}, with page = {}, and size = {}", userId,
                pageable.getPageNumber(), pageable.getPageSize());

        User user = userRepository.getOne(userId);
        Page<ContactDto> contactDto = contactRepository.getByUser(user, pageable).map(contactMapper::toDto);

        log.info("'getContacts' returned - {}", contactDto.getContent());

        return contactDto;
    }

    @Override
    @Transactional
    public ContactDto createContact(ContactDto createContactDto, Long userId) {

        log.info("'createContact' for user with id - {} invoked with params - {}", userId, createContactDto);

        User user = userRepository.getOne(userId);
        Contact contact = contactMapper.toEntity(createContactDto);
        user.addContact(contact);

        ContactDto contactDto = contactMapper.toDto(contact);

        log.info("'createContact' returned - {}", contactDto);

        return contactDto;
    }

    @Override
    @Transactional
    public ContactDto editContact(ContactDto editContactDto, Long contactId, Long userId) {

        log.info("'editContact' with id - {} invoked with params - {}", contactId, editContactDto);

        User user = userRepository.getOne(userId);

        Contact contact;
        if (ROLE_USER.equals(user.getRole()))
            contact = contactRepository.getByIdAndUser(contactId, user)
                    .orElseThrow(() -> new ResourceNotFoundException("You don`t have such contact."));
        else
            contact = contactRepository.getOne(contactId);

        log.info("Contact before edit - {}", contact);

        Contact editedContact = contactMapper.edit(editContactDto, contact);

        ContactDto contactDto = contactMapper.toDto(editedContact);
        log.info("'editContact' returned - {}", contactDto);

        return contactDto;
    }

    @Override
    @Transactional
    public void deleteContact(Long contactId, Long userId) {

        log.info("'deleteContact' with id - {} for user with id - {}", contactId, userId);

        User user = userRepository.getOne(userId);

        if (ROLE_USER.equals(user.getRole()))
            user.removeContact(Contact.builder().id(contactId).build());
        else
            contactRepository.deleteById(contactId);

        log.info("Contact deleted");
    }
}
