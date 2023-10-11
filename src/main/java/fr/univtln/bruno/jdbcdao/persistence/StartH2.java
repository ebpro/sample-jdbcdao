package fr.univtln.bruno.jdbcdao.persistence;

import fr.univtln.bruno.jdbcdao.persistence.datasources.DBCPDataSource;
import lombok.extern.java.Log;
import org.h2.tools.RunScript;
import org.h2.tools.Server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

@Log
public class StartH2 {
    public static void main(String[] args) {
        try {
            App.loadProperties("app.properties");
            App.configureLogger();
            Server server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers", "-pgAllowOthers", "-ifNotExists", "-baseDir", "/tmp/db").start();
            log.info(server.getStatus());
        } catch (SQLException e) {
            log.info("Server start error. " + e.getMessage());
        } catch (IOException e) {
            log.info("Configuration error. " + e.getMessage());
        }
            try (Connection connection = DBCPDataSource.getConnection()) {
                RunScript.execute(connection,
                        new InputStreamReader(StartH2.class.getClassLoader().getResourceAsStream("create.H2.sql")));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }
}
