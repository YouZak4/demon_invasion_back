package com.demon_invasion.backend.service;

import com.demon_invasion.backend.model.entities.Utilisateur;
import com.demon_invasion.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    /**
     * Retourne tous les utilisateurs contenus en base de données.
     * @return une liste d'utilisateurs
     */
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    /**
     * Retourne l'utilisateur à qui appartient l'identifiant en paramètre.
     * @param identifiant identifiant de l'utilisateur
     * @return un utilisateur
     */
    public Optional<Utilisateur> findByIdentifiant(String identifiant) {
        return utilisateurRepository.findByIdentifiant(identifiant);
    }

    /**
     * Vérifie si le pseudo en paramètre existe en base de donnée.
     * @param pseudo pseudo de l'utilisateur
     * @return un boolean
     */
    public boolean existsByPseudo(String pseudo) {
        return utilisateurRepository.existsByPseudo(pseudo);
    }

    /**
     * Retourne l'utilisateur à qui appartient le mail en paramètre.
     * @param email l'email de l'utilisateur
     * @return un utilisateur
     */
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    /**
     * Vérifie si le mail en paramètre existe en base de donnée.
     * @param email l'email de l'utilisateur
     * @return un boolean
     */
    public boolean existsByEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    /**
     * Permet de sauvegarder un utilisateur en base de donnée.
     * @param utilisateur l'utilisateur à sauvegarder
     * @return l'utilisateur sauvegardé
     */
    public Utilisateur save(Utilisateur utilisateur) {
        return utilisateurRepository.saveAndFlush(utilisateur);
    }
}
