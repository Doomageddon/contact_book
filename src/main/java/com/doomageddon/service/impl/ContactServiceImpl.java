package com.doomageddon.service.impl;

import com.doomageddon.model.dto.ContactDto;
import com.doomageddon.model.dto.CreateContactDto;
import com.doomageddon.model.dto.EditContactDto;
import com.doomageddon.model.entity.Contact;
import com.doomageddon.model.entity.User;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactMapper contactMapper;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ContactDto> getContacts(Long userId, Pageable pageable) {

        log.info("'getContacts' invoked with user id - {}", userId);

        User user = userRepository.getOne(userId);
        Page<ContactDto> contactDto = contactRepository.getByUser(user, pageable).map(contactMapper::toDto);

        log.info("'getContacts' returned - {}", contactDto.getContent());

        return contactDto;
    }

    @Override
    @Transactional
    public CreateContactDto createContact(CreateContactDto createContactDto, Long userId) {

        log.info("'createContact' for user with id - {} invoked with params - {}", userId, createContactDto);

        User user = userRepository.getOne(userId);
        Contact contact = contactMapper.toEntity(createContactDto);
        user.addContact(contact);

        log.info("'createContact' returned - {}", createContactDto);

        return createContactDto;
    }

    @Override
    @Transactional
    public ContactDto editContact(EditContactDto editContactDto, Long contactId) {

        log.info("'editContact' with id - {} invoked with params - {}", contactId, editContactDto);

        Contact contact = contactRepository.getOne(contactId);
        log.info("Contact before edit - {}", contact);

        Contact editedContact = contactMapper.edit(editContactDto, contact);

        ContactDto contactDto = contactMapper.toDto(editedContact);
        log.info("'editContact' returned - {}", contactDto);

        return contactDto;
    }

    @Override
    public void deleteContact(Long contactId) {

        log.info("'deleteContact' with id - {}", contactId);
        contactRepository.deleteById(contactId);
        log.info("Contact deleted");

    }
}
