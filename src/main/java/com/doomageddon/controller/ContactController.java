package com.doomageddon.controller;

import com.doomageddon.model.dto.ContactDto;
import com.doomageddon.model.dto.CreateContactDto;
import com.doomageddon.model.dto.EditContactDto;
import com.doomageddon.service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/book/contact")
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    public Page<ContactDto> getUserContacts(@AuthenticationPrincipal(expression = "userId") Long userId,
                                            Pageable pageable) {

        log.info("Get contacts for user with id - {}", userId);
        Page<ContactDto> contactDto = contactService.getContacts(userId, pageable);
        log.info("Returned user contacts - {}", contactDto.getContent());

        return contactDto;
    }

    @PostMapping
    public CreateContactDto createContact(@RequestBody @Valid CreateContactDto createContactDto,
                                          @AuthenticationPrincipal(expression = "userId") Long userId) {

        log.info("Create contact for user with id - {} with params - {}", userId, createContactDto);
        CreateContactDto contactDto = contactService.createContact(createContactDto, userId);
        log.info("Created contact - {}", contactDto);

        return contactDto;
    }

    @PutMapping("/{contactId}")
    public ContactDto editContact(@PathVariable Long contactId,
                                  @RequestBody @Valid EditContactDto editContactDto,
                                  @AuthenticationPrincipal(expression = "userId") Long userId) {

        log.info("Edit contact {} for user with id - {} with params - {}", contactId, userId, editContactDto);
        ContactDto contactDto = contactService.editContact(editContactDto, contactId);
        log.info("Edited contact - {}", contactDto);

        return contactDto;
    }

    @DeleteMapping("/{contactId}")
    public void deleteContact(@PathVariable Long contactId) {

        log.info("Delete contact with id - {}", contactId);
        contactService.deleteContact(contactId);
        log.info("Contact deleted");
    }
}
