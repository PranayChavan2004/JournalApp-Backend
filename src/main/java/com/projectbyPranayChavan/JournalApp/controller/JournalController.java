package com.projectbyPranayChavan.JournalApp.controller;

import com.projectbyPranayChavan.JournalApp.entities.JournalEntry;
import com.projectbyPranayChavan.JournalApp.entities.User;
import com.projectbyPranayChavan.JournalApp.service.JournalEntryService;
import com.projectbyPranayChavan.JournalApp.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalController {

    // injecting JournalEntryService so we can use methods inside of it
    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;


    // this method will be used to retrieve the journal of the user (his own journals only )
    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllJournalEntriesofUser() {

        /* since we are using authentication the user have
         access to retrieve data only if he is authorized so no need to take
          username from request because it leaking data when user login to its account
           and sends request to get journals then this method will be called */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<JournalEntry> entries = user.getJournalEntries();
        if (entries != null && !entries.isEmpty()) {
            return new ResponseEntity<>(entries, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping
    // @RequestBody used because to add the data the data will be send in the requestbody from client
    public ResponseEntity<String> addJournalEntry(@RequestBody JournalEntry entry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            journalEntryService.saveJournal(entry, userName);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Data Saved Successfully");
        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }


    // to retrieve data by user_id hence we are using @PathVariable so we can retrieve data
    @GetMapping("/id/{myid}")
    public ResponseEntity<JournalEntry> getById(@PathVariable UUID myid) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User user = userService.findByUserName(userName);

        List<JournalEntry> collect = user.getJournalEntries()
                .stream()
                .filter(x -> x.getId().equals(myid))
                .collect(Collectors.toList());

        if (!collect.isEmpty()) {
            return new ResponseEntity<>(collect.get(0), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    // to delete journal entry using username
    //{id}==@PathVariable long id  names must be match[Error if {id} and @PathVariable long myid]
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEntry(@PathVariable UUID id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
       // here id is journalId
        boolean deleted = journalEntryService.deleteById(userName, id);

        if (deleted) {
            return ResponseEntity.ok("Record Deleted Successfully");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Journal Entry Not Found");
    }

    //Update the data for particular id
    // Update journal entry of a particular user

    @PutMapping("/{journalId}")
    public ResponseEntity<String> updateEntry(
            @PathVariable UUID journalId,
            @RequestBody JournalEntry newEntry) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User user = userService.findByUserName(userName);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User Not Found");
        }

        JournalEntry oldEntry = user.getJournalEntries()
                .stream()
                .filter(entry -> entry.getId().equals(journalId))
                .findFirst()
                .orElse(null);

        if (oldEntry == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Journal Entry Not Found");
        }

        oldEntry.setTitle(
                newEntry.getTitle() != null && !newEntry.getTitle().isBlank()
                        ? newEntry.getTitle()
                        : oldEntry.getTitle()
        );

        oldEntry.setContent(
                newEntry.getContent() != null && !newEntry.getContent().isBlank()
                        ? newEntry.getContent()
                        : oldEntry.getContent()
        );

        oldEntry.setDate(LocalDateTime.now());

        journalEntryService.saveOnlyJournal(oldEntry);

        return ResponseEntity.ok("Journal Updated Successfully");
    }


}