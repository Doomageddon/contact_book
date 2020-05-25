package com.doomageddon.service;

import com.doomageddon.model.dto.ContactDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContactService {

    Page<ContactDto> getContacts(Long userId, Pageable pageable);

    ContactDto createContact(ContactDto createContactDto, Long userId);

    ContactDto editContact(ContactDto editContactDto, Long contactId, Long userId);

    void deleteContact(Long contactId, Long userId);
}
