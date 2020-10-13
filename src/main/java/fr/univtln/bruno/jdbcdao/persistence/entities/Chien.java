package fr.univtln.bruno.jdbcdao.persistence.entities;

import lombok.Builder;
import lombok.Data;

/**
 * A mutable class that represents a dog.
 * It conforms to the Entity interface to ease persistence with a DAO
 */
@Data
@Builder
public class Chien implements Entity {
    @Builder.Default
    private long id = -1;
    private String nom;
    private Personne maitre;
}
