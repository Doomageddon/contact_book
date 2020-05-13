package com.doomageddon.service;

import com.doomageddon.model.dto.ContactDto;
import com.doomageddon.model.dto.CreateContactDto;
import com.doomageddon.model.dto.EditContactDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminPanelService {

    Page<ContactDto> getContacts(Long userId, Pageable pageable);

    ContactDto editContact(EditContactDto editContactDto, Long contactId);

    void deleteContact(Long contactId);
}
