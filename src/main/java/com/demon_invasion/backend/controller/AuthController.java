package com.demon_invasion.backend.controller;

import com.demon_invasion.backend.model.dto.DtoInscriptionEnAttente;
import com.demon_invasion.backend.model.dto.DtoLogin;
import com.demon_invasion.backend.model.dto.DtoRegister;
import com.demon_invasion.backend.model.dto.DtoRenvoiCode;
import com.demon_invasion.backend.model.dto.DtoUtilisateur;
import com.demon_invasion.backend.model.dto.DtoVerification;
import com.demon_invasion.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<DtoInscriptionEnAttente> register(@Valid @RequestBody DtoRegister request) {
        return ResponseEntity.accepted().body(authService.register(request));   // 202 : pas encore créé
    }

    @GetMapping("/register/{jeton}")
    public ResponseEntity<DtoInscriptionEnAttente> getInscriptionEnAttente(@PathVariable String jeton) {
        return ResponseEntity.ok(authService.getInscriptionEnAttente(jeton));
    }

    @PostMapping("/register/verify")
    public ResponseEntity<DtoUtilisateur> verify(@Valid @RequestBody DtoVerification request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.verify(request));
    }

    @PostMapping("/register/resend")
    public ResponseEntity<DtoInscriptionEnAttente> resend(@Valid @RequestBody DtoRenvoiCode request) {
        return ResponseEntity.accepted().body(authService.renvoyerCode(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody DtoLogin request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
