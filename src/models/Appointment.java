package models;

import java.time.LocalDateTime;
/** This the class for the Appointment model. */
public class Appointment {
    private Integer Appointment_ID;
    private String Title;
    private String Description;
    private String Location;
    private String Type;
    private LocalDateTime Start;
    private LocalDateTime End;
    /** Foreign keys. */
    private Integer Customer_ID;
    private Integer Contact_ID;
    private Integer User_ID;
    /** Details to display. */
    private String Customer_Name;
    private String Contact_Details;

    /** Constructor. */
    public Appointment() {
    }

    /** Constructor.
     * @param Appointment_ID constructor parameters
     * @param Title constructor parameters
     * @param Description constructor parameters
     * @param Location constructor parameters
     * @param Type constructor parameters
     * @param Start constructor parameters
     * @param End constructor parameters
     * @param Customer_ID constructor parameters
     * @param Contact_ID constructor parameters
     * @param User_ID constructor parameters*/
    public Appointment(Integer Appointment_ID, String Title, String Description, String Location, String Type, LocalDateTime Start, LocalDateTime End, Integer Customer_ID, Integer Contact_ID, Integer User_ID) {
        this.Appointment_ID = Appointment_ID;
        this.Title = Title;
        this.Description = Description;
        this.Location = Location;
        this.Type = Type;
        this.Start = Start;
        this.End = End;
        this.Customer_ID = Customer_ID;
        this.Contact_ID = Contact_ID;
        this.User_ID = User_ID;
    }

    /** Constructor with Customer name and Contact details.
     * @param Appointment_ID constructor parameters
     * @param Title constructor parameters
     * @param Description constructor parameters
     * @param Location constructor parameters
     * @param Type constructor parameters
     * @param Start constructor parameters
     * @param End constructor parameters
     * @param Customer_ID constructor parameters
     * @param Contact_ID constructor parameters
     * @param User_ID constructor parameters
     * @param Customer_Name constructor parameters
     * @param Contact_Details constructor parameters*/
    public Appointment(Integer Appointment_ID, String Title, String Description, String Location, String Type, LocalDateTime Start, LocalDateTime End, Integer Customer_ID, Integer Contact_ID, Integer User_ID, String Customer_Name, String Contact_Details) {
        this.Appointment_ID = Appointment_ID;
        this.Title = Title;
        this.Description = Description;
        this.Location = Location;
        this.Type = Type;
        this.Start = Start;
        this.End = End;
        this.Customer_ID = Customer_ID;
        this.Contact_ID = Contact_ID;
        this.User_ID = User_ID;
        this.Customer_Name = Customer_Name;
        this.Contact_Details = Contact_Details;
    }

    /** Get Appointment_ID.
     * @return returns integer field*/
    public Integer getAppointment_ID() {
        return Appointment_ID;
    }

    /** Get Customer_ID.
     * @return returns integer field*/
    public Integer getCustomer_ID() {
        return Customer_ID;
    }

    /** Get Contact_ID.
     * @return returns integer field*/
    public Integer getContact_ID() {
        return Contact_ID;
    }

    /** Get User_ID.
     * @return returns integer field*/
    public Integer getUser_ID() {
        return User_ID;
    }

    /** Get Customer_Name.
     * @return returns String field*/
    public String getCustomer_Name() {
        return Customer_Name;
    }

    /** Get Contact_Details.
     * @return returns String field*/
    public String getContact_Details() {
        return Contact_Details;
    }

    /** Get and Set Title.
     * @return returns String field*/
    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    /** Get and Set Description.
     * @return returns String field*/
    public String getDescription() {
        return Description;
    }

    /** Get and Set Location.
     * @return returns String field*/
    public String getLocation() {
        return Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }

    /** Get and Set Type.
     * @return returns String field*/
    public String getType() {
        return Type;
    }

    /** Get and Set Start date and time.
     * @return returns LocalDateTime field*/
    public LocalDateTime getStart() {
        return Start;
    }

    /** Get and Set End.
     * @return returns LocalDateTime field*/
    public LocalDateTime getEnd() {
        return End;
    }

    /** Display appointment as String.
     * @return returns String*/
    public String showAppointment() {
        return String.format("\n" +
                "Appointment %d: %s\n" +
                "Description: %s\n" +
                "Location: %s\n" +
                "Type: %s\n" +
                "Start: %s\n" +
                "End: %s\n" +
                "Customer_ID: %d\n" +
                "Contact_ID: %d\n" +
                "User_ID: %d", this.Appointment_ID, this.Title, this.Description, this.Location, this.Type, this.Start, this.End, this.Customer_ID, this.Contact_ID, this.User_ID);
    }
}
