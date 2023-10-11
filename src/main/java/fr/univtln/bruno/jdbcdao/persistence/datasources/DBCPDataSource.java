package fr.univtln.bruno.jdbcdao.persistence.datasources;

import fr.univtln.bruno.jdbcdao.persistence.App;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBCPDataSource {

    private static final BasicDataSource ds = new BasicDataSource();

    static {
        ds.setUrl(App.getProperty("jdbcdao.datasource.url"));
        ds.setUsername(App.getProperty("jdbcdao.datasource.username"));
        ds.setPassword(App.getProperty("jdbcdao.datasource.password"));

        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
    }

    private DBCPDataSource() {
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}