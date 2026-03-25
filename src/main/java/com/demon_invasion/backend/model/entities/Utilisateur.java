package com.demon_invasion.backend.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true, nullable = false)
    private String pseudo;

    @Column(nullable = false)
    private String identifiant;

    @Column(nullable = false)
    private String motDePasse;

    @Setter
    @Column(unique = true, nullable = false)
    private String email;

    @Setter
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "utilisateur_roles",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new LinkedHashSet<>();
}
