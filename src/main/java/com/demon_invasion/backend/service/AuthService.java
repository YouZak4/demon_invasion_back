package com.demon_invasion.backend.service;

import com.demon_invasion.backend.model.dto.DtoRegister;
import com.demon_invasion.backend.model.dto.DtoUtilisateur;
import com.demon_invasion.backend.model.entities.Role;
import com.demon_invasion.backend.model.entities.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

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

    private Utilisateur setUtilisateurForRegistration(DtoRegister dtoRegister, Role roleUtilisateur) {
        Utilisateur utilisateur = new Utilisateur();
        BeanUtils.copyProperties(dtoRegister, utilisateur);
        utilisateur.setMotDePasse(passwordEncoder.encode(dtoRegister.getMotDePasse())); // hash du mdp
        utilisateur.setRoles(Set.of(roleUtilisateur));
        return utilisateur;
    }
}
