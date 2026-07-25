package com.trekconnect.auth.controller;

import com.trekconnect.auth.security.JwtKeyProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/.well-known")
public class JwksController {

    private final JwtKeyProvider jwtKeyProvider;

    @Autowired
    public JwksController(JwtKeyProvider jwtKeyProvider) {
        this.jwtKeyProvider = jwtKeyProvider;
    }

    @GetMapping(value = "/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getJwks() {
        return ResponseEntity.ok(jwtKeyProvider.getJwksJson());
    }
}
