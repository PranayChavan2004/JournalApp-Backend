package com.projectbyPranayChavan.JournalApp.controller;


import com.projectbyPranayChavan.JournalApp.entities.JournalEntry;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/forPractice") // to access methods inside this controller now must localhost:8080/journal/___
/* now if GET localhost:8080/journal  will retrieve data
POST localhost:8080/journal  will use to add the data in hashmap
like this if we want then we can also add mapping in GetMapping("/getData") so now
 to retrieve data url: localhost:8080/journal/getData */

public class JournalEntryController {


    // to store the journal entries
    private Map<UUID, JournalEntry> journalEntries = new HashMap<>();

    // this method will be used to retrieve all entries
    @GetMapping
    public List<JournalEntry> getAll() {
        /* we are using arraylist for desplaying all records because we store journal entries
        as values in journalEntries where their id is their keys*/
        return new ArrayList<>(journalEntries.values());
    }

    @PostMapping
    // @RequestBody used because to add the data the data will be send in the requestbody from client
    public String addJournalEntry(@RequestBody JournalEntry entry) {


        /* we are storing the journal entry here in journalEntries hashmap
        journal id is our key and entire journal data is our value */
        journalEntries.put(entry.getId(), entry);
        return "Data Added"; /* we are using postman to check http request this String message will help to
        know request is handle properly or not */


    }

    // to retrive data by id hence we are using @PathVariable so we can retireve data
    @GetMapping("/{myid}") // here we are providing myid to retrieve specific data
    // note : { provided_name} == @PathVariable typr provided_name

    public JournalEntry getById(@PathVariable UUID myid) {

        // we will use method of hashmap to retrive particular data using key
        return journalEntries.get(myid);
    }

    // to delete entry
    //{id}==@PathVariable long id  names must be match[Error if {id} and @PathVariable long myid]
    @DeleteMapping("/{id}")
    public String deleteEntry(@PathVariable UUID id) {
        journalEntries.remove(id);
        return "Data Deleted Successfully...";

    }

    //Update the data for particular id
    @PutMapping("/{id}") // provide id which needed to update
    public String updateEntry(@PathVariable UUID id, @RequestBody JournalEntry entry) {
        journalEntries.put(id, entry); // hashmap will update/override existing data for this id
        // in entry provide updated data for id
        return "Data Updates Successfully for fiven id...";
    }

    // if we want to replace the data for id with another id+entry
    @PutMapping("/replace/{id}")
    public String replaceEntry(@PathVariable UUID id, @RequestBody JournalEntry entry) {
        if (journalEntries.containsKey(id)) {
            journalEntries.remove(id); // remove entire data for id
            journalEntries.put(entry.getId(), entry); // now key=id from new entry and data=new entry
        }

        return "Data is replaced successfully for provided id";
    }


}
