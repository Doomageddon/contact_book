package com.doomageddon.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;

    private String password;

    private Boolean isEnabled;

    @Builder.Default
    @Enumerated(STRING)
    @Column(name = "role")
    @ElementCollection(fetch = EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    List<Role> roles = new ArrayList<>();

    @Builder.Default
    @Setter(PRIVATE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "user", cascade = ALL)
    private Set<Contact> contacts = new HashSet<>();

    public void addContact(Contact contact) {
        contact.setUser(this);
        contacts.add(contact);
    }
}
