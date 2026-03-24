package com.demon_invasion.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DtoAuthenticated {
    private String token;
    private String identifiant;
    private java.util.Set<String> roles;
}
