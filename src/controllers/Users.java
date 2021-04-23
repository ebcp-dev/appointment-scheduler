package controllers;

import db.DBConnection;
import db.DBQuery;
import models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/*** This class has methods for interacting with the User model. */
public class Users {
    /** Database Connection. */
    Connection conn = DBConnection.startConnection();
    public Users() {}

    /** Update existing User.
     * @param User_ID parameters for sql query
     * @param User_Name parameters for sql query
     * @param Password parameters for sql query
     * @param Last_Updated_By parameters for sql query
     * @throws SQLException update query*/
    public void updateUser(Integer User_ID, String User_Name, String Password, String Last_Updated_By) throws SQLException {
        String updateStatement = "UPDATE users SET User_Name = ?, Password = ?, Last_Update = NOW(), Last_Updated_By = ?" +
                "WHERE User_ID = ?;";

        DBQuery.setPreparedStatement(conn, updateStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        /** Map variables to sql statement string. */
        ps.setString(1, User_Name);
        ps.setString(2, Password);
        ps.setString(3, Last_Updated_By);
        ps.setInt(4, User_ID);
        try {
            ps.execute();
            /** Show if statement was successfully executed. */
            if(ps.getUpdateCount() > 0) {
                System.out.println(ps.getUpdateCount() + " row(s) affected.");
            } else {
                System.out.println("User not found.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /** Delete existing User.
     * @param User_ID for sql query
     * @throws SQLException for delete query*/
    public void deleteUser(Integer User_ID)
            throws SQLException {
        String deleteStatement = "DELETE FROM users WHERE User_ID = ?;";
        DBQuery.setPreparedStatement(conn, deleteStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        /** Map variables to sql statement string. */
        ps.setInt(1, User_ID);
        try {
            ps.execute();
            /** Show if statement was successfully executed. */
            if(ps.getUpdateCount() > 0) {
                System.out.println(ps.getUpdateCount() + " row(s) deleted.");
            } else {
                System.out.println("User not found.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /** User login. */
    /** Returns User object if user is found on the database.
     * @param User_Name for sql query
     * @param Password for sql query
     * @throws SQLException select query
     * @return return User object*/
    public User loginUser(String User_Name, String Password) throws SQLException {
        User user = new User();
        String selectStatement = "SELECT * FROM users WHERE User_Name = ? AND Password = ?;";
        DBQuery.setPreparedStatement(conn, selectStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        /** Map variables to sql statement string. */
        ps.setString(1, User_Name);
        ps.setString(2, Password);
        try {
            ps.execute();
            ResultSet rs = ps.getResultSet();
            /** If login is successful. */
            if (rs.next()) {
                /** Store User attributes to a User variable. */
                Integer userID = rs.getInt("User_ID");
                String userName = rs.getString("User_Name");
                String password = rs.getString("Password");
                user = new User(userID, userName, password);
                System.out.println("User " + rs.getString("User_Name") + " logged in.");
            } else {
                /** Error message if login info is wrong. */
                System.out.println("Invalid credentials.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return user;
    }
}
