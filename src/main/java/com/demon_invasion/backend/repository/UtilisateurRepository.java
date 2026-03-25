package com.demon_invasion.backend.repository;

import com.demon_invasion.backend.model.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByIdentifiant(String identifiant);

    boolean existsByPseudo(String pseudo);

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);
}


