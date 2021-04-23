package controllers;

import db.DBConnection;
import db.DBQuery;
import models.Contact;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/** This class has methods for interacting with the Contact model. */
public class Contacts {
    /** Database connection. */
    private static Connection conn = DBConnection.startConnection();

    /** Get all Contacts from database.
     * @throws SQLException get contacts from db
     * @return return list of contacts*/
    public static List<Contact> getContacts() throws SQLException {
        List<Contact> allContacts = new ArrayList<Contact>();
        /** SELECT SQL statement to retrieve all appointments from database. */
        String selectStatement = "SELECT * FROM contacts;";
        DBQuery.setPreparedStatement(conn, selectStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        try {
            ps.execute();
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                /** Add to arraylist all appointments found. */
                Integer Contact_ID = rs.getInt("Contact_ID");
                String Contact_Name = rs.getString("Contact_Name");
                String Email = rs.getString("Email");
                /** Create Contact object to add to list. */
                Contact contactData = new Contact(Contact_ID, Contact_Name, Email);
                allContacts.add(contactData);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        /** Return List. */
        return allContacts;
    }

    /** Get Contact by Contact_ID.
     * @param Contact_ID for sql query
     * @return return Contact object*/
    public static Contact getContactByContactID(Integer Contact_ID) {
        Contact contact = new Contact();
        String selectStatement = "SELECT * FROM contacts WHERE Contact_ID = ?;";
        /** Execute statement */
        try {
            DBQuery.setPreparedStatement(conn, selectStatement);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            /** Map variables to sql statement string. */
            ps.setInt(1, Contact_ID);
            ps.execute();
            /** Show if statement was successfully executed. */
            ResultSet rs = ps.getResultSet();
            if(rs.next()) {
                contact = new Contact(rs.getInt("Contact_ID"), rs.getString("Contact_Name"), rs.getString("Email"));
            } else {
                System.out.println(ps.getResultSet().getStatement() + " Contact not found.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return contact;
    }
}
