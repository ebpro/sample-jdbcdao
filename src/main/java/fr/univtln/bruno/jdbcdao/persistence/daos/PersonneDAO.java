package fr.univtln.bruno.jdbcdao.persistence.daos;

import fr.univtln.bruno.jdbcdao.persistence.entities.Personne;
import fr.univtln.bruno.jdbcdao.persistence.exceptions.DataAccessException;
import lombok.extern.java.Log;

import java.sql.ResultSet;
import java.sql.SQLException;

@Log
public class PersonneDAO extends AbstractDAO<Personne> {

    public PersonneDAO() {
        super("INSERT INTO PERSONNE(NOM,PRENOM) VALUES (?,?)",
                "UPDATE PERSONNE SET NOM=?, PRENOM=? WHERE ID=?");
    }

    @Override
    protected Personne fromResultSet(ResultSet resultSet) throws SQLException {
        return Personne.builder()
                .id(resultSet.getInt("ID"))
                .nom(resultSet.getString("NOM"))
                .prenom(resultSet.getString("PRENOM"))
                .build();
    }

    @Override
    public Personne persist(Personne personne) throws DataAccessException {
        return persist(personne.getNom(), personne.getPrenom());
    }

    public Personne persist(final String nom, final String prenom) throws DataAccessException {
        try {
            persistPS.setString(1, nom);
            persistPS.setString(2, prenom);
        } catch (SQLException throwables) {
            throw new DataAccessException(throwables.getLocalizedMessage());
        }
        return super.persist();
    }

    @Override
    public void update(Personne personne) throws DataAccessException {
        try {
            updatePS.setString(1, personne.getNom());
            updatePS.setString(2, personne.getPrenom());
            updatePS.setLong(3, personne.getId());
        } catch (SQLException throwables) {
            throw new DataAccessException(throwables.getLocalizedMessage());
        }
        super.update();
    }

    @Override
    public String getTableName() {
        return "PERSONNE";
    }
}
