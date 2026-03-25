package com.demon_invasion.backend.model.dto;

import com.demon_invasion.backend.model.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoUtilisateur {
    private Long id;
    private String pseudo;
    private String email;
    private Set<Role> roles;
}
