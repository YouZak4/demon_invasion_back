package com.demon_invasion.backend.controller;

import com.demon_invasion.backend.model.dto.DtoAuthenticated;
import com.demon_invasion.backend.model.dto.DtoLogin;
import com.demon_invasion.backend.model.dto.DtoRegister;
import com.demon_invasion.backend.model.dto.DtoUtilisateur;
import com.demon_invasion.backend.security.JwtService;
import com.demon_invasion.backend.security.UserDetailsServiceImpl;
import com.demon_invasion.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    /**
     * Inscription
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody DtoRegister request) {
        DtoUtilisateur dtoUtilisateur = authService.register(request);
        return ResponseEntity.ok("Inscription réussie.", dtoUtilisateur);
    }

    /**
     * Connexion
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DtoLogin request) {
        try {
            // Spring Security vérifie identifiant + mot de passe
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getIdentifiant(),
                            request.getMotDePasse()
                    )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Identifiant ou mot de passe incorrect.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getIdentifiant());
        String token = jwtService.generateToken(userDetails);

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new DtoAuthenticated(token, request.getIdentifiant(), roles));
    }
}
