package com.demon_invasion.backend.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Utilisateur {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String pseudo;

    @Getter
    @Setter
    @Column(nullable = false)
    private String identifiant;

    @Getter
    @Setter
    @Column(nullable = false)
    private String motDePasse;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String email;

    @Getter
    @Setter
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "utilisateur_roles",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new LinkedHashSet<>();
}
