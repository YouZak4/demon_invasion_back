package com.demon_invasion.backend.service;

import com.demon_invasion.backend.model.entities.Role;
import com.demon_invasion.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    /**
     * Retourne le role qui correspond au nom en paramètre.
     *
     * @param nom le nom du role
     * @return le role
     */
    public Optional<Role> findByNom(String nom) {
        return roleRepository.findByNom(nom);
    }

}
