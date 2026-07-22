package com.projectbyPranayChavan.JournalApp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Indexed;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    private String email;
    private boolean sentimentAnalysis;

    @Column(nullable = false)
    private String password;

    // we are creating this table so we can map user and journalEntry one user can have multiple journal Entry
    @OneToMany
    @JoinTable(
            name = "user_journal",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "journal_id")
    )
    private List<JournalEntry> journalEntries = new ArrayList<>();

    private List<String> roles;
}
