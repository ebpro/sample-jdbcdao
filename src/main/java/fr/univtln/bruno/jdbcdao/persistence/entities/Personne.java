package fr.univtln.bruno.jdbcdao.persistence.entities;

import lombok.Builder;
import lombok.Data;

/**
 * A non mutable class that represents a person.
 * It conforms to the Entity interface to ease persistence with a DAO
 */
@Data
@Builder
public class Personne implements Entity {
    @Builder.Default
    private final long id = -1;
    private final String nom;
    private final String prenom;
}
