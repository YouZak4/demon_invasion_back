package com.demon_invasion.backend.service;

import com.demon_invasion.backend.exception.InvalidCredentialsException;
import com.demon_invasion.backend.model.dto.DtoLogin;
import com.demon_invasion.backend.model.dto.DtoRegister;
import com.demon_invasion.backend.model.dto.DtoUtilisateur;
import com.demon_invasion.backend.model.entities.Role;
import com.demon_invasion.backend.model.entities.Utilisateur;
import com.demon_invasion.backend.security.CustomUserDetailsService;
import com.demon_invasion.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String ROLE_UTILISATEUR = "ROLE_UTILISATEUR";

    private static final String ROLE_NOT_FOUND = "Le rôle suivant n'est pas présent en base de données : ";

    private static final String PSEUDO_ALREADY_EXISTS = "Le pseudonyme que vous avez saisi est déjà pris.";

    private static final String EMAIL_ALREADY_EXISTS = "L'adresse mail que vous avez saisi est déjà prise.";

    private final UtilisateurService utilisateurService;

    private final CustomUserDetailsService userDetailsService;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    /**
     * Cette méthode permet à un utilisateur de créer son compte.
     *
     * @param dtoRegister contient les informations saisis par l'utilisateur
     * @return une DTO contenant les informations de l'utilisateur
     */
    public DtoUtilisateur register(DtoRegister dtoRegister) {
        if (utilisateurService.existsByPseudo(dtoRegister.getPseudo())) {
            throw new RuntimeException(PSEUDO_ALREADY_EXISTS);
        }
        if (utilisateurService.existsByEmail(dtoRegister.getEmail())) {
            throw new RuntimeException(EMAIL_ALREADY_EXISTS);
        }

        Role roleUtilisateur = roleService.findByNom(ROLE_UTILISATEUR).orElseThrow(
                () -> new RuntimeException(ROLE_NOT_FOUND + ROLE_UTILISATEUR)
        );

        Utilisateur utilisateur = utilisateurService.save(setUtilisateurForRegistration(dtoRegister, roleUtilisateur));

        DtoUtilisateur dtoUtilisateur = new DtoUtilisateur();
        BeanUtils.copyProperties(utilisateur, dtoUtilisateur);

        return dtoUtilisateur;
    }

    /**
     * Cette méthode permet de sauvegardé en base de données les informations de connexion de l'utilisateur.
     *
     * @param dtoRegister     Contient les informations de connexion de l'utilisateur
     * @param roleUtilisateur Contient le role de l'utilisateur qui ce connecte à l'application
     * @return un DTO contenant les informations de l'utilisateur
     */
    private Utilisateur setUtilisateurForRegistration(DtoRegister dtoRegister, Role roleUtilisateur) {
        Utilisateur utilisateur = new Utilisateur();
        BeanUtils.copyProperties(dtoRegister, utilisateur);
        utilisateur.setMotDePasse(passwordEncoder.encode(dtoRegister.getMotDePasse())); // hash du mdp
        utilisateur.setRoles(Set.of(roleUtilisateur));
        return utilisateur;
    }


    /**
     * Cette méthode permet à l'utilisateur de ce connecter à l'application
     *
     * @param request les informations de connexion de l'utilisateur
     * @return un token de connexion
     */
    public String login(DtoLogin request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getIdentifiant(), request.getMotDePasse())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return jwtService.generateToken(authentication);

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Pseudo ou mot de passe incorrect.");
        }
    }
}
