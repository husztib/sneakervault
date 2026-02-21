package com.sneakervault.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Value("${sneakervault.admin.password:}")
    private String adminPassword;

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@RequestBody Map<String, String> body) {
        String password = body.get("password");
        if (adminPassword.equals(password)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(401).build();
    }
}
