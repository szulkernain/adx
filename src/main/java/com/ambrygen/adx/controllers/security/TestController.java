package com.ambrygen.adx.controllers.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('SUBSCRIBER') or hasRole('ADMIN') or hasRole('EDITOR')")
    public ResponseEntity<List<String>> userAccess() {
        List<String> items = new ArrayList<>();
        items.add("Item 1");
        items.add("Item 2");
        items.add("Item 3");
        return ResponseEntity.ok(items);
    }

    @GetMapping("/subscriber")
    @PreAuthorize("hasRole('SUBSCRIBER')")
    public ResponseEntity<List<String>> subscriberAccess() {
        List<String> subscriberItems = new ArrayList<>();
        subscriberItems.add("Subscriber 1");
        subscriberItems.add("Subscriber 2");
        subscriberItems.add("Subscriber 3");
        return ResponseEntity.ok(subscriberItems);
    }

    @GetMapping("/editor")
    @PreAuthorize("hasRole('EDITOR')")
    public ResponseEntity<List<String>> editorAccess() {
        List<String> editorItems = new ArrayList<>();
        editorItems.add("Editor 1");
        editorItems.add("Editor 2");
        editorItems.add("Editor 3");
        return ResponseEntity.ok(editorItems);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> adminAccess() {
        List<String> adminItems = new ArrayList<>();
        adminItems.add("Admin 1");
        adminItems.add("Admin 2");
        adminItems.add("Admin 3");
        return ResponseEntity.ok(adminItems);
    }
}

