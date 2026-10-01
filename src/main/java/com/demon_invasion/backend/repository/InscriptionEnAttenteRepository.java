package com.demon_invasion.backend.repository;

import com.demon_invasion.backend.model.entities.InscriptionEnAttente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InscriptionEnAttenteRepository extends JpaRepository<InscriptionEnAttente, Long> {
    Optional<InscriptionEnAttente> findByEmail(String email);

    Optional<InscriptionEnAttente> findByJeton(String jeton);
}
