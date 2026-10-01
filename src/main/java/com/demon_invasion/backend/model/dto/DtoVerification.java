package com.demon_invasion.backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoVerification {
    @NotBlank(message = "Le jeton d'inscription est obligatoire.")
    private String jeton;
    @NotBlank(message = "Le code de vérification est obligatoire.")
    private String code;
}
