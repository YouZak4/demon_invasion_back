package com.demon_invasion.backend.service;

import com.demon_invasion.backend.exception.AlreadyExist;
import com.demon_invasion.backend.exception.InvalidCredentialsException;
import com.demon_invasion.backend.exception.InvalidVerificationCodeException;
import com.demon_invasion.backend.exception.TooManyRequestsException;
import com.demon_invasion.backend.model.dto.DtoInscriptionEnAttente;
import com.demon_invasion.backend.model.dto.DtoLogin;
import com.demon_invasion.backend.model.dto.DtoRegister;
import com.demon_invasion.backend.model.dto.DtoRenvoiCode;
import com.demon_invasion.backend.model.dto.DtoUtilisateur;
import com.demon_invasion.backend.model.dto.DtoVerification;
import com.demon_invasion.backend.model.entities.InscriptionEnAttente;
import com.demon_invasion.backend.model.entities.Role;
import com.demon_invasion.backend.model.entities.Utilisateur;
import com.demon_invasion.backend.repository.InscriptionEnAttenteRepository;
import com.demon_invasion.backend.security.CustomUserDetailsService;
import com.demon_invasion.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String ROLE_UTILISATEUR = "ROLE_UTILISATEUR";
    private static final String ROLE_NOT_FOUND = "Le rôle suivant n'est pas présent en base de données : ";
    private static final String PSEUDO_ALREADY_EXISTS = "Le pseudonyme que vous avez saisi est déjà pris.";
    private static final String EMAIL_ALREADY_EXISTS = "L'adresse mail que vous avez saisi est déjà prise.";
    private static final String IDENTIFIANT_ALREADY_EXISTS = "Cet identifiant est déjà utilisé, veuillez recommencer l'inscription avec un autre identifiant.";
    private static final String COMPTE_CREE_ENTRE_TEMPS = "Ce pseudo, cet identifiant ou cette adresse mail vient d'être utilisé par un autre compte, veuillez recommencer l'inscription.";
    private static final String INSCRIPTION_INTROUVABLE = "Ce lien de vérification n'est plus valide, veuillez recommencer l'inscription.";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UtilisateurService utilisateurService;
    private final CustomUserDetailsService userDetailsService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final InscriptionEnAttenteRepository inscriptionEnAttenteRepository;
    private final MailService mailService;
    @Value("${app.verification.expiration-minutes}")
    private long expirationMinutes;
    @Value("${app.verification.max-tentatives}")
    private int maxTentatives;
    @Value("${app.verification.delai-renvoi-secondes}")
    private long delaiRenvoiSecondes;
    @Value("${app.verification.duree-vie-heures}")
    private long dureeVieHeures;

    /**
     * Cette méthode permet à un utilisateur de créer son compte.
     *
     * @param dto contient les informations saisis par l'utilisateur
     * @return le jeton de l'inscription en attente et l'adresse mail masquée
     */
    @Transactional
    public DtoInscriptionEnAttente register(DtoRegister dto) {
        String email = dto.getEmail().trim().toLowerCase();

        // Le pseudo est public (visible en jeu) : le signaler ne révèle rien
        if (utilisateurService.existsByPseudo(dto.getPseudo())) throw new AlreadyExist(PSEUDO_ALREADY_EXISTS);
        // L'e-mail, lui, ne doit pas pouvoir être testé : même réponse qu'il soit libre ou non
        boolean compteExistant = utilisateurService.existsByEmail(email);

        // Si une inscription en attente existe déjà pour ce mail, on l'écrase
        InscriptionEnAttente attente = inscriptionEnAttenteRepository.findByEmail(email).orElseGet(InscriptionEnAttente::new);

        // Nouveau jeton à chaque inscription : un ancien lien de vérification ne fonctionne plus
        attente.setJeton(UUID.randomUUID().toString());
        attente.setEmail(email);
        attente.setPseudo(dto.getPseudo());
        attente.setIdentifiant(dto.getIdentifiant());
        attente.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        attente.setCompteExistant(compteExistant);
        genererEtEnvoyerCode(attente);

        return versDto(attente);
    }

    /**
     * Cette méthode permet au front de réafficher la page de vérification (après un rafraîchissement par exemple).
     *
     * @param jeton le jeton de l'inscription en attente
     * @return le jeton et l'adresse mail masquée
     */
    @Transactional(readOnly = true)
    public DtoInscriptionEnAttente getInscriptionEnAttente(String jeton) {
        return versDto(trouverParJeton(jeton));
    }

    /**
     * Cette méthode renvoie un nouveau code à une inscription en attente.
     * L'ancien code devient invalide et le compteur de tentatives repart à zéro.
     *
     * @param dto contient le jeton de l'inscription en attente
     * @return le jeton, l'adresse mail masquée et le délai avant le prochain renvoi
     */
    @Transactional
    public DtoInscriptionEnAttente renvoyerCode(DtoRenvoiCode dto) {
        InscriptionEnAttente attente = trouverParJeton(dto.getJeton());

        // Anti-spam : un seul envoi par fenêtre de delaiRenvoiSecondes
        long secondesRestantes = secondesAvantRenvoi(attente);
        if (secondesRestantes > 0) {
            throw new TooManyRequestsException("Veuillez patienter " + secondesRestantes + " secondes avant de demander un nouveau code.");
        }

        genererEtEnvoyerCode(attente);
        return versDto(attente);
    }

    /**
     * Arrondi à la seconde supérieure : on n'annonce jamais un délai plus court que le délai réel.
     */
    private long secondesAvantRenvoi(InscriptionEnAttente attente) {
        LocalDateTime prochainEnvoiPossible = attente.getDernierEnvoi().plusSeconds(delaiRenvoiSecondes);
        long millisRestantes = Duration.between(LocalDateTime.now(), prochainEnvoiPossible).toMillis();
        return millisRestantes <= 0 ? 0 : (millisRestantes + 999) / 1000;
    }

    private void genererEtEnvoyerCode(InscriptionEnAttente attente) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000)); // 000000 à 999999
        attente.setCodeHash(passwordEncoder.encode(code));
        attente.setExpiration(LocalDateTime.now().plusMinutes(expirationMinutes));
        attente.setTentatives(0);
        attente.setDernierEnvoi(LocalDateTime.now());
        inscriptionEnAttenteRepository.save(attente);

        // Compte existant : le code n'est jamais communiqué, la vérification échouera comme un code incorrect
        if (attente.isCompteExistant()) {
            mailService.envoyerAlerteCompteExistant(attente.getEmail());
        } else {
            mailService.envoyerCodeVerification(attente.getEmail(), code);
        }
    }

    private InscriptionEnAttente trouverParJeton(String jeton) {
        return inscriptionEnAttenteRepository.findByJeton(jeton)
                // Trop ancienne : traitée comme déjà supprimée, sans attendre le prochain passage de NettoyageInscriptionService
                .filter(attente -> attente.getDernierEnvoi().plusHours(dureeVieHeures).isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new InvalidVerificationCodeException(INSCRIPTION_INTROUVABLE));
    }

    private DtoInscriptionEnAttente versDto(InscriptionEnAttente attente) {
        return new DtoInscriptionEnAttente(attente.getJeton(), masquerEmail(attente.getEmail()), secondesAvantRenvoi(attente));
    }

    /**
     * Garde la première lettre et le domaine : "romain@outlook.fr" devient "r****@outlook.fr".
     * Le nombre d'étoiles est fixe pour ne pas révéler la longueur de l'adresse.
     */
    private String masquerEmail(String email) {
        int arobase = email.indexOf('@');
        return email.charAt(0) + "****" + email.substring(arobase);
    }

    @Transactional(noRollbackFor = InvalidVerificationCodeException.class) // sinon la tentative ratée n'est pas comptée
    public DtoUtilisateur verify(DtoVerification dto) {
        InscriptionEnAttente attente = trouverParJeton(dto.getJeton());
        String email = attente.getEmail();

        if (attente.getExpiration().isBefore(LocalDateTime.now())) {
            inscriptionEnAttenteRepository.delete(attente);
            throw new InvalidVerificationCodeException("Le code a expiré, veuillez recommencer l'inscription.");
        }
        if (attente.getTentatives() >= maxTentatives) {
            inscriptionEnAttenteRepository.delete(attente);
            throw new InvalidVerificationCodeException("Trop de tentatives, veuillez recommencer l'inscription.");
        }
        if (!passwordEncoder.matches(dto.getCode(), attente.getCodeHash())) {
            attente.setTentatives(attente.getTentatives() + 1);
            throw new InvalidVerificationCodeException("Code incorrect.");
        }

        // Re-vérif : quelqu'un a pu prendre le pseudo/mail entre-temps
        if (utilisateurService.existsByEmail(email)) throw new AlreadyExist(EMAIL_ALREADY_EXISTS);
        if (utilisateurService.existsByPseudo(attente.getPseudo())) throw new AlreadyExist(PSEUDO_ALREADY_EXISTS);
        // Vérifié seulement ici, une fois le code validé : tester des identifiants exige
        // de posséder la boîte mail et de refaire une inscription complète à chaque essai
        if (utilisateurService.existsByIdentifiant(attente.getIdentifiant())) throw new AlreadyExist(IDENTIFIANT_ALREADY_EXISTS);

        Role role = roleService.findByNom(ROLE_UTILISATEUR).orElseThrow();
        Utilisateur u = new Utilisateur();
        u.setEmail(email);
        u.setPseudo(attente.getPseudo());
        u.setIdentifiant(attente.getIdentifiant());
        u.setMotDePasse(attente.getMotDePasse()); // déjà haché, on ne re-encode pas !
        u.setRoles(Set.of(role));
        try {
            u = utilisateurService.save(u);
        } catch (DataIntegrityViolationException e) {
            // Deux validations simultanées : les vérifs ci-dessus sont passées, mais la contrainte unique en base a bloqué la seconde
            throw new AlreadyExist(COMPTE_CREE_ENTRE_TEMPS);
        }
        inscriptionEnAttenteRepository.delete(attente);

        DtoUtilisateur res = new DtoUtilisateur();
        BeanUtils.copyProperties(u, res);
        return res;
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
