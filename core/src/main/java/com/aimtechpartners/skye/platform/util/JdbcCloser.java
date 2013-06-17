package com.aimtechpartners.skye.platform.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A little helper to handle JDBC connection closing
 */
public class JdbcCloser {

    private final static Logger LOGGER = LoggerFactory.getLogger(JdbcCloser.class);

    /**
     * Closes the provided connection
     *
     * @param conn connection
     */
    public static void closeConn(Connection conn) {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.warn("Problem closing MySQL connection", e);
            }
    }

    /**
     * Closes the provided resultset
     *
     * @param rs resultset
     */
    public static void closeRs(ResultSet rs) {
        if (rs != null)
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.warn("Problem closing MySQL connection", e);
            }
    }
}
