package com.projectbyPranayChavan.JournalApp.entities;


import com.projectbyPranayChavan.JournalApp.enums.Sentiment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "Journal_Entries")
public class JournalEntry {

    // UUID is basically unique ids
    @Id
    private UUID id;
    @Column(nullable = false)
    private String title;
    private String content;
    private LocalDateTime date;
    private Sentiment sentiment;

}
