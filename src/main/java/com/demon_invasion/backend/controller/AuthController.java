package com.demon_invasion.backend.controller;

import com.demon_invasion.backend.model.dto.DtoLogin;
import com.demon_invasion.backend.model.dto.DtoRegister;
import com.demon_invasion.backend.model.dto.DtoUtilisateur;
import com.demon_invasion.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Inscription
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody DtoRegister request) {
        DtoUtilisateur dtoUtilisateur = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoUtilisateur);
    }

    /**
     * Connexion
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DtoLogin request) {
        String token = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
