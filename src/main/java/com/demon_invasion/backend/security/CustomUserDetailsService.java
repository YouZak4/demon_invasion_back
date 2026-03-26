package com.demon_invasion.backend.security;

import com.demon_invasion.backend.model.CustomUserDetails;
import com.demon_invasion.backend.model.entities.Utilisateur;
import com.demon_invasion.backend.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    UtilisateurRepository utilisateurRepository;

    /**
     * Appelé automatiquement par Spring Security via le contrat de l'interface {@link UserDetailsService}.
     * Il suffit d'implémenter cette interface pour que {@link DaoAuthenticationProvider}
     * invoque cette méthode lors de chaque tentative d'authentification,
     * sans aucun appel explicite dans le code métier.
     */
    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String pseudo) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
        return new CustomUserDetails(utilisateur);
    }
}