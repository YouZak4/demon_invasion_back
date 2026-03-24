package com.demon_invasion.backend.controller;

import com.demon_invasion.backend.model.dto.*;
import com.demon_invasion.backend.model.entities.*;
import com.demon_invasion.backend.repository.*;
import com.demon_invasion.backend.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    /**
     * Inscription
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody DtoRegister request) {

        if (utilisateurRepository.existsByIdentifiant(request.getIdentifiant())) {
            return ResponseEntity.badRequest().body("Identifiant déjà utilisé.");
        }
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email déjà utilisé.");
        }

        // Récupère le rôle ROLE_UTILISATEUR (doit exister en BDD)
        Role roleUtilisateur = roleRepository.findByNom("ROLE_UTILISATEUR")
                .orElseThrow(() -> new RuntimeException("Rôle ROLE_UTILISATEUR introuvable en BDD"));

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPseudo(request.getPseudo());
        utilisateur.setIdentifiant(request.getIdentifiant());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse())); // hash du mdp
        utilisateur.setRoles(Set.of(roleUtilisateur));

        utilisateurRepository.save(utilisateur);

        return ResponseEntity.ok("Inscription réussie.");
    }

    // ── CONNEXION ─────────────────────────────────────────────────
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
