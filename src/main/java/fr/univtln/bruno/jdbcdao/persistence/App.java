package fr.univtln.bruno.jdbcdao.persistence;

import fr.univtln.bruno.jdbcdao.persistence.daos.ChienDAO;
import fr.univtln.bruno.jdbcdao.persistence.daos.PersonneDAO;
import fr.univtln.bruno.jdbcdao.persistence.entities.Chien;
import fr.univtln.bruno.jdbcdao.persistence.entities.Personne;
import fr.univtln.bruno.jdbcdao.persistence.exceptions.DataAccessException;
import fr.univtln.bruno.jdbcdao.persistence.exceptions.NotFoundException;
import lombok.extern.java.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

@Log
public class App {
    public static void main(String[] args) throws IOException {

        // We load the application configuration
        loadProperties("app.properties");
        configureLogger();

        try ( //First we get the DAO for persons
              PersonneDAO personneDAO = new PersonneDAO()) {

            //We use it to remove all data in the relation
            personneDAO.clean();

            //We add a new person
            Personne p1 = personneDAO.persist("A", "B");
            log.info(" p1 persisted " + p1);

            //and a list of persons
            personneDAO.persist(List.of(Personne.builder().nom("Durand").prenom("Jacques").build(), Personne.builder().nom("Dupond").prenom("Paul").build(), Personne.builder().nom("Martin").prenom("Pierre").build(), Personne.builder().nom("Laforge").prenom("Henry").build(), Personne.builder().nom("Laforge").prenom("Marie").build()));

            //We get two persons by id (an existing one and a missing one).
            long[] ids = {p1.getId(), -1};
            for (long id : ids) {
                try {
                    Optional<Personne> optionalPersonne = personneDAO.find(id);
                    log.info("Personne %d : %s".formatted(id, (optionalPersonne.isPresent() ? optionalPersonne.get() : "MISSING !")));
                } catch (NotFoundException e) {
                    log.severe("Personne %d not found".formatted(id));
                }
            }

            //we update a person
            p1 = Personne.builder().id(p1.getId()).nom("AA").prenom("BB").build();
            personneDAO.update(p1);
            Optional<Personne> p1new = personneDAO.find(p1.getId());
            log.info("Personne 1 (new): " + (p1new.isPresent() ? p1new.get() : "MISSING !"));

            //we remove a person by id
            personneDAO.remove(4);
            //or with reference
            personneDAO.remove(p1);

            //We get all the persons
            log.info(personneDAO.findAll().toString());

            //Now we illustrate joins
            try (ChienDAO chienDAO = new ChienDAO()) {
                chienDAO.clean();

                //We create a master and its dog
                Personne leMaitre = Personne.builder().nom("Le").prenom("Maitre").build();
                leMaitre = personneDAO.persist(leMaitre);
                Chien leChien = Chien.builder().nom("Rex").maitre(leMaitre).build();
                leChien = chienDAO.persist(leChien);
                log.info("Le Chien: " + leChien.toString());
                Chien toujourLeChien = chienDAO.find(leChien.getId()).orElseThrow(NotFoundException::new);
                log.info("Toujours le chien: " + toujourLeChien.toString());

                //Notice that master deletion removes the master from the dog
                personneDAO.remove(leMaitre);
                try {
                    log.info("Le chien sans maitre: " + chienDAO.find(leChien.getId()).get());
                } catch (NotFoundException e) {
                    log.severe("Personne %d not found".formatted(leChien.getId()));
                }
            }

        } catch (DataAccessException throwables) {
            throwables.printStackTrace();
        }
    }

    static void loadProperties(String propFileName) throws IOException {
        Properties properties = new Properties();
        InputStream inputstream = App.class.getClassLoader().getResourceAsStream(propFileName);
        if (inputstream == null) throw new FileNotFoundException();
        properties.load(inputstream);
        System.setProperties(properties);
    }

    static void configureLogger() {
        //Regarder src/main/ressources/logging.properties pour fixer le niveau de log
        String path;
        path = Objects.requireNonNull(App.class.getClassLoader().getResource("logging.properties")).getFile();
        System.setProperty("java.util.logging.config.file", path);
    }
}
