package com.gogidix.rapidassist.database.indexing.service.adapters.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
    @GetMapping("/status")
    public ResponseEntity<String> status() { return ResponseEntity.ok("OK"); }
}