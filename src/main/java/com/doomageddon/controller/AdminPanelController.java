package com.doomageddon.controller;

import com.doomageddon.model.dto.ContactDto;
import com.doomageddon.model.dto.EditContactDto;
import com.doomageddon.service.AdminPanelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/panel/book/contact")
public class AdminPanelController {

    private final AdminPanelService adminPanelService;

    @GetMapping
    public Page<ContactDto> getUserContacts(@RequestParam Long userId, Pageable pageable) {

        log.info("Get contacts for user with id - {}", userId);
        Page<ContactDto> contactDto = adminPanelService.getContacts(userId, pageable);
        log.info("Returned user contacts - {}", contactDto.getContent());

        return contactDto;
    }

    @PutMapping("/{contactId}")
    public ContactDto editContact(@RequestBody @Valid EditContactDto editContactDto, @PathVariable Long contactId, Long userId) {

        log.info("Edit contact {} for user with id - {} with params - {}", contactId, userId, editContactDto);
        ContactDto contactDto = adminPanelService.editContact(editContactDto, contactId);
        log.info("Edited contact - {}", contactDto);

        return contactDto;
    }

    @DeleteMapping("/{contactId}")
    public void deleteContact(@PathVariable Long contactId) {

        log.info("Delete contact with id - {}", contactId);
        adminPanelService.deleteContact(contactId);
        log.info("Contact deleted");
    }
}
