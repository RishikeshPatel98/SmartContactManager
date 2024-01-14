package com.smart.smartcontactmanager.controller;

import com.smart.smartcontactmanager.entities.User;
import com.smart.smartcontactmanager.entities.contactDetails;
import com.smart.smartcontactmanager.repositories.ContactDetailsRepository;
import com.smart.smartcontactmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactDetailsRepository contactRepository;



    //search handler
    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal)
    {
        System.out.println(query);
        User user=this.userRepository.getUserByUserName(principal.getName());
        List<contactDetails> contacts = this.contactRepository.findByNameContainingAndUser(query, user);
        return ResponseEntity.ok(contacts);
    }
}
