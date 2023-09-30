package edu.school21.connect;

import com.zaxxer.hikari.HikariDataSource;

public class DataAccessObject {
    private final HikariDataSource ds;
    private final String JDBC_URL = "jdbc:postgresql://localhost:5432/postgres";
    private final String USER = "postgres";
    private final String PASSWORD = "060601Yumzhana!";

    public DataAccessObject() {
        ds = new HikariDataSource();
        ds.setJdbcUrl(JDBC_URL);
        ds.setUsername(USER);
        ds.setPassword(PASSWORD);
    }

    public HikariDataSource getDataSource() {
        return ds;
    }

    public void closeDataSource() {
        ds.close();
    }
}
