package com.aimtechpartners.skye.stores.util;

import java.util.Properties;

/**
 * A little helper for the JDBC store properties used for initialization
 */
public class JdbcProperties extends Properties {


    public static final String JDBC_URL = "jdbcUrl";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String NAME = "name";


    // no-arg constructor
    public JdbcProperties() {

    }

    public JdbcProperties(String jdbcUrl, String username, String password) {
        setJdbcUrl(jdbcUrl);
        setUsername(username);
        setPassword(password);
        setName(jdbcUrl);
    }

    public JdbcProperties(Properties properties) {
        for (Object key : properties.keySet()) {
            this.setProperty(key.toString(), properties.get(key).toString());
        }
    }

    public String getName() {
        return getProperty(NAME);
    }

    public void setName(String name) {
        setProperty(NAME, name);
    }

    public String getJdbcUrl() {
        return getProperty(JDBC_URL);
    }

    public void setJdbcUrl(String jdbcUrl) {
        setProperty(JDBC_URL, jdbcUrl);
    }

    public String getUsername() {
        return getProperty(USERNAME);
    }

    public void setUsername(String username) {
        setProperty(USERNAME, username);
    }

    public String getPassword() {
        return getProperty(PASSWORD);
    }

    public void setPassword(String password) {
        setProperty(PASSWORD, password);
    }
}
