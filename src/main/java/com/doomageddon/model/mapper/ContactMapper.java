package com.doomageddon.model.mapper;


import com.doomageddon.model.dto.ContactDto;
import com.doomageddon.model.dto.CreateContactDto;
import com.doomageddon.model.dto.EditContactDto;
import com.doomageddon.model.entity.Contact;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

@Component
public class ContactMapper {

    public Contact toEntity(CreateContactDto createContactDto) {
        return Contact.builder()
                .firstName(createContactDto.getFirstName())
                .lastName(createContactDto.getLastName())
                .phone(ofNullable(createContactDto.getPhone()).orElse(0L))
                .build();
    }

    public ContactDto toDto(Contact contact) {
        return ContactDto.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .phone(contact.getPhone())
                .build();
    }

    public Contact edit(EditContactDto editContactDto, Contact contact) {
        return contact
                .setFirstName(editContactDto.getFirstName())
                .setLastName(editContactDto.getLastName())
                .setPhone(ofNullable(editContactDto.getPhone()).orElse(0L));
    }
}
