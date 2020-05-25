package com.doomageddon.repository;

import com.doomageddon.model.entity.Contact;
import com.doomageddon.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Page<Contact> getByUser(User user, Pageable pageable);

    Optional<Contact> getByIdAndUser(Long contactId, User user);
}
