package com.demon_invasion.backend.model.dto;

import lombok.Data;

@Data
public class DtoRegister {
    private String pseudo;
    private String identifiant;
    private String motDePasse;
    private String email;
}
