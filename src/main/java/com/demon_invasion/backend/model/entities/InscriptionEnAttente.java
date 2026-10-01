package com.demon_invasion.backend.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inscriptions_en_attente")
@Data
@NoArgsConstructor
public class InscriptionEnAttente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identifiant opaque transmis au front à la place de l'e-mail
    @Column(unique = true, nullable = false)
    private String jeton;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String pseudo;
    @Column(nullable = false)
    private String identifiant;
    @Column(nullable = false)
    private String motDePasse;   // déjà haché
    @Column(nullable = false)
    private String codeHash;     // code haché, jamais en clair
    @Column(nullable = false)
    private LocalDateTime expiration;
    @Column(nullable = false)
    private int tentatives = 0;
    @Column(nullable = false)
    private LocalDateTime dernierEnvoi;
    // E-mail déjà lié à un compte : on simule une inscription normale mais on envoie une alerte au lieu d'un code
    @Column(nullable = false)
    private boolean compteExistant = false;
}