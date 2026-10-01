package com.demon_invasion.backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoRegister {
    @NotBlank(message = "Le pseudonyme est obligatoire.")
    private String pseudo;
    @NotBlank(message = "L'identifiant est obligatoire.")
    private String identifiant;
    @NotBlank(message = "Le mot de passe est obligatoire.")
    private String motDePasse;
    @NotBlank(message = "L'adresse mail est obligatoire.")
    @Email(message = "L'adresse mail n'est pas valide.")
    private String email;
}
