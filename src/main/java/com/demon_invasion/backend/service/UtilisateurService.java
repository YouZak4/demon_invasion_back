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

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Optional<Utilisateur> findByIdentifiant(String identifiant) {
        return utilisateurRepository.findByIdentifiant(identifiant);
    }

    public boolean existsByPseudo(String pseudo) {
        return utilisateurRepository.existsByPseudo(pseudo);
    }

    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    public Utilisateur save(Utilisateur utilisateur) {
        return utilisateurRepository.saveAndFlush(utilisateur);
    }
}
