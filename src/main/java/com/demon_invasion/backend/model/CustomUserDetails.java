package com.demon_invasion.backend.model;


import com.demon_invasion.backend.model.entities.Role;
import com.demon_invasion.backend.model.entities.Utilisateur;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final Utilisateur utilisateur;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        this.authorities = buildAuthorities(utilisateur.getRoles());
    }

    private static List<GrantedAuthority> buildAuthorities(Set<Role> roles) {
        return roles.stream()
                .filter(Objects::nonNull)
                .map(Role::getNom)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(name -> new SimpleGrantedAuthority("ROLE_" + name))
                .collect(Collectors.toList());
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return utilisateur.getMotDePasse();
    }

    @Override
    @NonNull
    public String getUsername() {
        return utilisateur.getPseudo();
    }
}