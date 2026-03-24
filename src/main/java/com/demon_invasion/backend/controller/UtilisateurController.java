package com.demon_invasion.backend.controller;

import com.demon_invasion.backend.model.entities.Utilisateur;
import com.demon_invasion.backend.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @GetMapping
    public List<Utilisateur> getAll() {
        return utilisateurService.getAllUtilisateurs();
    }

}
