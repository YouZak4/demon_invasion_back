package com.demon_invasion.backend.repository;

import com.demon_invasion.backend.model.entities.InscriptionEnAttente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InscriptionEnAttenteRepository extends JpaRepository<InscriptionEnAttente, Long> {
    Optional<InscriptionEnAttente> findByEmail(String email);

    Optional<InscriptionEnAttente> findByJeton(String jeton);

    // Suppression en une seule requête, sans charger chaque ligne en mémoire
    @Modifying
    @Query("DELETE FROM InscriptionEnAttente i WHERE i.dernierEnvoi < :limite")
    int deleteByDernierEnvoiBefore(@Param("limite") LocalDateTime limite);
}
