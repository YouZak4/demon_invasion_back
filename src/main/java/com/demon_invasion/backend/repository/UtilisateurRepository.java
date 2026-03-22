package com.demon_invasion.backend.repository;

import com.demon_invasion.backend.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
// Spring Data JPA fournit findAll(), findById(), save(), delete()... automatiquement
}


