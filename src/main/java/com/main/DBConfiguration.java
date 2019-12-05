package com.main;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConfiguration {

    public static DSLContext initiateDB() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException {

        final Properties prop = new Properties();
        String propFileName = "flyway.properties";

        InputStream inputStream = DBConfiguration.class.getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        final String userName = prop.getProperty("flyway.user");
        final String password = prop.getProperty("flyway.password");
        final String url = "jdbc:mysql://localhost:3306/" + prop.getProperty("flyway.schemas");

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(url, userName, password);
            return DSL.using(conn, SQLDialect.MYSQL);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
