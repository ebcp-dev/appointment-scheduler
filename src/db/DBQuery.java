package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/** This class contains the PreparedStatement methods for creating SQL queries. */
public class DBQuery {
    private static PreparedStatement statement;

    /** Create Statement object
     * @param conn connection object
     * @param sqlStatement sql statement to be executed
     * @throws SQLException sql query*/
    public static void setPreparedStatement(Connection conn, String sqlStatement) throws SQLException {
        statement = conn.prepareStatement(sqlStatement);
    }
    /** Return statement object
     * @return return PreparedStatement object*/
    public static PreparedStatement getPreparedStatement() { return statement; }
}
