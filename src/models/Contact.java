package models;
/*** This the class for the Contact model. */
public class Contact {
    /** Private fields. */
    private Integer Contact_ID;
    private String Contact_Name;
    private String Email;

    /** Constructor. */
    public Contact() {
    }

    /** Constructor.
     * @param Contact_ID constructor parameters
     * @param Contact_Name constructor parameters
     * @param Email constructor parameters*/
    public Contact(Integer Contact_ID, String Contact_Name, String Email) {
        this.Contact_ID = Contact_ID;
        this.Contact_Name = Contact_Name;
        this.Email = Email;
    }

    /** Get Contact_ID.
     * @return returns Integer field*/
    public Integer getContact_ID() {
        return Contact_ID;
    }

    /** Get and Set Contact_Name.
     * @return returns String field*/
    public String getContactName() {
        return Contact_Name;
    }

    /** Get and Set Email.
     * @return returns String field*/
    public String getEmail() {
        return Email;
    }

    /** Display Customer in String format.
     * @return returns String*/
    public String showCustomer() {
        return String.format("\n" +
                        "Contact %d: %s, %s", this.Contact_ID, this.Contact_Name, this.Email);
    }
}
