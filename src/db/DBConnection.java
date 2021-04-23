package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/** This class contains the database connection process and details. */
public class DBConnection {
    /** URL components. */
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String ip = "//wgudb.ucertify.com/WJ05P2q";
    /** Full URL. */
    private static final String dburl = protocol + vendor + ip;
    /** Driver/Connection interface reference. */
    private static final String MYSQLDriver = "com.mysql.cj.jdbc.Driver";
    private static Connection conn = null;
    /** DB credentials. */
    private static final String username = "U05P2q";
    private static final String password = "53688565931";

    public static Connection startConnection() {
        try {
            Class.forName(MYSQLDriver);
            conn = DriverManager.getConnection(dburl, username, password);
            System.out.println("Connected to " + ip);
        }
        catch (ClassNotFoundException | SQLException e) {
            /** Show SQL errors. */
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void closeConnection() {
        try {
            conn.close();
            System.out.println("Connection closed.");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
