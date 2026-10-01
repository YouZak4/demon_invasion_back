package com.demon_invasion.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Ce qui est renvoyé au front pour une inscription en attente :
 * jamais l'adresse mail complète, seulement sa version masquée.
 */
@Data
@AllArgsConstructor
public class DtoInscriptionEnAttente {
    private String jeton;
    private String emailMasque;
    // Permet au front d'afficher le bon décompte, même après un rafraîchissement
    private long secondesAvantRenvoi;
}
