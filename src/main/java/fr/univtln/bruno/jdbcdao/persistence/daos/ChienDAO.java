package fr.univtln.bruno.jdbcdao.persistence.daos;

import fr.univtln.bruno.jdbcdao.persistence.entities.Chien;
import fr.univtln.bruno.jdbcdao.persistence.exceptions.DataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChienDAO extends AbstractDAO<Chien> {

    public ChienDAO() {
        super("INSERT INTO CHIEN(NOM,MAITRE_ID) VALUES (?,?)",
                "UPDATE CHIEN SET NOM=?,MAITRE_ID=? WHERE ID=?");
    }

    @Override
    public String getTableName() {
        return "CHIEN";
    }

    @Override
    protected Chien fromResultSet(ResultSet resultSet) throws SQLException {
        PersonneDAO personneDAO = new PersonneDAO();
        Chien chien = null;
        try {
            chien = Chien.builder()
                    .id(resultSet.getLong("ID"))
                    .nom(resultSet.getString("NOM"))
                    .maitre(personneDAO.find(resultSet.getLong("MAITRE_ID")).orElse(null))
                    .build();
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return chien;
    }

    @Override
    public Chien persist(Chien chien) throws DataAccessException {
        try {
            persistPS.setString(1, chien.getNom());
            persistPS.setLong(2, chien.getMaitre().getId());
        } catch (SQLException throwables) {
            throw new DataAccessException(throwables.getLocalizedMessage());
        }
        return super.persist();
    }

    @Override
    public void update(Chien chien) throws DataAccessException {
        try {
            updatePS.setString(1, chien.getNom());
            updatePS.setLong(2, chien.getMaitre().getId());
        } catch (SQLException throwables) {
            throw new DataAccessException(throwables.getLocalizedMessage());
        }
        super.update();

    }
}
