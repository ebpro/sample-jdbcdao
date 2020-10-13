package fr.univtln.bruno.jdbcdao.persistence.exceptions;

public class DataAccessException extends Exception {
    public DataAccessException(String localizedMessage) {
        super(localizedMessage);
    }
}
