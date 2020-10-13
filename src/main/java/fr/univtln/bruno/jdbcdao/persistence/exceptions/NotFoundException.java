package fr.univtln.bruno.jdbcdao.persistence.exceptions;

public class NotFoundException extends DataAccessException {
    public NotFoundException() {
        super("Entity not found");
    }
}
