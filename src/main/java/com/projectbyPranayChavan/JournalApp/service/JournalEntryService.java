package com.projectbyPranayChavan.JournalApp.service;

import com.projectbyPranayChavan.JournalApp.entities.JournalEntry;
import com.projectbyPranayChavan.JournalApp.entities.User;
import com.projectbyPranayChavan.JournalApp.repository.JournalEntryRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    // save user from browser to database
    @Transactional /* this annotation treats entre method as single unit either every db operations
    from method will execute or none */
    public void saveJournal(JournalEntry journalEntry, String userName) {
        /* Generate unique id and time because user not gonna provide this two fields so
        create this two fields for user at service layer */
        try {
            User user = userService.findByUserName(userName);

            // creating values for variable id and date
            journalEntry.setId(UUID.randomUUID());
            journalEntry.setDate(LocalDateTime.now());

            JournalEntry saved = journalEntryRepository.save(journalEntry);
            // now add this entry to the user's list
            user.getJournalEntries().add(saved);
            userService.saveUser(user);
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void saveOnlyJournal(JournalEntry journal) {
        journalEntryRepository.save(journal);
    }

    // retrieve all data
    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    // retrieve data using id
    //Optional either return or null
    public Optional<JournalEntry> getById(UUID id) {
        return journalEntryRepository.findById(id);
    }

    // delete entry
    @Transactional
    public boolean deleteById(String userName, UUID id) {

        boolean removed = false;
        try {
            User user = userService.findByUserName(userName);
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if (removed) {
                userService.saveUser(user);
                journalEntryRepository.deleteById(id);
            }
        } catch (Exception e) {
            log.error("Error ",e);
            throw new RuntimeException("An error occurred while deleting the entry.", e);
        }
        return removed;
    }

}
