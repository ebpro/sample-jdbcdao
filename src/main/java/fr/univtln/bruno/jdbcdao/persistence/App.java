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
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

@Log
public class App {
    protected static final Properties properties = new Properties();

    static void loadProperties(String propFileName) throws IOException {
        InputStream inputstream = App.class.getClassLoader().getResourceAsStream(propFileName);
        if (inputstream == null) throw new FileNotFoundException();
        properties.load(inputstream);
    }

    static void configureLogger() {
        //Regarder src/main/ressources/logging.properties pour fixer le niveau de log
        String path;
        path = Objects.requireNonNull(App.class
                .getClassLoader()
                .getResource("logging.properties"))
                .getFile();
        System.setProperty("java.util.logging.config.file", path);
    }

    public static void main(String[] args) throws IOException {

        loadProperties("app.properties");
        configureLogger();

        try ( //First we get the DAA for persons
              PersonneDAO personneDAO = new PersonneDAO()) {

            //We use it to remove all data in the relation
            personneDAO.clean();

            //We add a new person
            Personne p1 = personneDAO.persist("A", "B");
            log.info(" p1 persisted " + p1);

            //and a list of persons
            personneDAO.persist(Arrays.asList(
                    Personne.builder().nom("C").prenom("D").build(),
                    Personne.builder().nom("E").prenom("F").build(),
                    Personne.builder().nom("G").prenom("H").build(),
                    Personne.builder().nom("I").prenom("J").build(),
                    Personne.builder().nom("K").prenom("L").build())
            );

            //We get persons by id (including a missing one).
            long[] ids = {p1.getId(), -1};
            for (long id : ids) {
                Optional<Personne> optionalPersonne = personneDAO.find(id);
                log.info("Personne " + id + " : " + (optionalPersonne.isPresent() ? optionalPersonne.get() : "MISSING !"));
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

                //Notice that master deletion removes the master to the dog
                personneDAO.remove(leMaitre);
                log.info("Le chien sans maitre: " + chienDAO.find(leChien.getId()).orElseThrow(NotFoundException::new));
            }

        } catch (DataAccessException throwables) {
            throwables.printStackTrace();
        }
    }

    public static String getProperty(String s) {
        return properties.getProperty(s);
    }
}
