package fr.univtln.bruno.jdbcdao.persistence.daos;

import fr.univtln.bruno.jdbcdao.persistence.datasources.DBCPDataSource;
import fr.univtln.bruno.jdbcdao.persistence.entities.Personne;
import fr.univtln.bruno.jdbcdao.persistence.exceptions.DataAccessException;
import fr.univtln.bruno.jdbcdao.persistence.exceptions.NotFoundException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class PersonneDAOTest {
    /**
     * The DAO to test
     */
    private static Optional<PersonneDAO> personneDAO;
    /**
     * A sample list of personnes
     */
    private final static Optional<List<Personne>> personnes = Optional.of(IntStream.range(0,AbstractDAO.DEFAULT_PAGE_SIZE*10).mapToObj(id->Personne.builder()
            .prenom("P%d".formatted(id))
            .nom("N%d".formatted(id))
            .build()).toList());
    /**
     * A copy of the real data in the database with actual ids.
     * during the current test.
     */
    private static Optional<List<Personne>> personnesInDB;

    /**
     * Create the schema and init the datasource once before all tests.
     */
    @BeforeAll
    static void init() {
        Properties properties = new Properties();
        try {
            properties.load(PersonneDAOTest.class.getClassLoader().getResourceAsStream("app.properties"));
            System.setProperties(properties);

            try (Connection connection = DBCPDataSource.getConnection()) {
                RunScript.execute(connection,
                        new InputStreamReader(PersonneDAOTest.class.getClassLoader().getResourceAsStream("create.H2.sql")));
            } catch (SQLException e) {
                log.error("Test database initialisation error: {}", e.getLocalizedMessage());
            }
        } catch (IOException e) {
            log.error("Config file access error: {}",e.getMessage());
        }
    }

    @AfterAll
    static void finish() {
    }

    /**
     * Creates a DAO.
     * Cleans the relation.
     * Inserts 10 pages of data.
     * Before each test.
     */
    @SneakyThrows
    @BeforeEach
    void setUp() {
        personneDAO = Optional.of(new PersonneDAO());
        personnesInDB = Optional.of(new ArrayList<>(AbstractDAO.DEFAULT_PAGE_SIZE*10));
        personneDAO.orElseThrow().clean();

        personnes.orElseThrow().forEach(p-> {
            try {
                personnesInDB.orElseThrow().add(personneDAO.orElseThrow().persist(p));
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        personneDAO.orElseThrow().close();
    }

    /**
     * Tests if a persisted data can be retrieved by id.
     */
    @Test
    void find() {
        try {
            Optional<Personne> p1 = personneDAO.orElseThrow().find(personnesInDB.orElseThrow().get(0).getId());
            assertEquals(personnesInDB.orElseThrow().get(0), p1.orElseThrow());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test if a missing data generates the correct Execption.
     */
    @SneakyThrows
    @Test
    void find404() {
            assertThrows(NotFoundException.class, ()->personneDAO.orElseThrow().find(-1));
    }

    /**
     * Tests if the default first page is retrieved
     */
    @SneakyThrows
    @Test
    void findAll() {
        Page<Personne> pagePersonnes = personneDAO.orElseThrow().findAll();
        List<Personne> inDB = personnesInDB.orElseThrow().subList(0, AbstractDAO.DEFAULT_PAGE_SIZE);

        assertAll("Check the page",
                ()->assertEquals(AbstractDAO.DEFAULT_PAGE_SIZE, pagePersonnes.pageSize()),
                ()->assertIterableEquals(inDB, pagePersonnes.resultList())
        );
    }

    /**
     * Tests a simple removal.
     */
    @SneakyThrows
    @Test
    void remove() {
        long idToRemove = personnesInDB.orElseThrow().get(personnesInDB.orElseThrow().size()/2).getId();
        personneDAO.orElseThrow().remove(idToRemove);
        assertThrows(NotFoundException.class, ()->personneDAO.orElseThrow().find(idToRemove));
    }

    /**
     * Test if a new persisted entity can be retrieved.
     */
    @SneakyThrows
    @Test
    void persist() {
        Personne p = Personne.builder().nom("X").prenom("Y").build();
        Personne addedPerson = personneDAO.orElseThrow().persist(p);
        Personne personInDB = personneDAO.orElseThrow().find(addedPerson.getId()).orElseThrow();
        assertAll(
                ()-> assertEquals(addedPerson.getNom(),personInDB.getNom()),
                ()-> assertEquals(addedPerson.getPrenom(),personInDB.getPrenom())
        );
    }

    /**
     * Tests if an entity can be updated.
     */
    @SneakyThrows
    @Test
    void update() {
        long idToUpdate = personnesInDB.orElseThrow().get(personnesInDB.orElseThrow().size()/2).getId();
        Personne personInDB = personneDAO.orElseThrow().find(idToUpdate).orElseThrow();
        personneDAO.orElseThrow().update(Personne.builder().id(idToUpdate).nom("X").prenom("Y").build());
        Personne UpdatedPersonInDB = personneDAO.orElseThrow().find(idToUpdate).orElseThrow();
        log.info("{}",UpdatedPersonInDB);
        assertAll(
                ()-> assertEquals("X", UpdatedPersonInDB.getNom()),
                ()-> assertEquals("Y", UpdatedPersonInDB.getPrenom())
        );
    }
}