package fr.univtln.bruno.jdbcdao.persistence.entities;

/**
 * An interface to force an entity used in DAOs to have a long id.
 */
public interface Entity {
    long getId();
}
