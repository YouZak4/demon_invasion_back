package com.demon_invasion.backend.repository;

import com.demon_invasion.backend.model.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
// Spring Data JPA fournit findAll(), findById(), save(), delete()... automatiquement
    Optional<Utilisateur> findByEmail(String email);
}


