package models;
/*** This the class for the User model. */
public class User {
    /** Class fields. */
    private Integer User_ID;
    private String User_Name;
    private String Password;

    /** Constructor. */
    public User() {
    }

    /** Constructor.
     * @param User_ID constructor parameter
     * @param User_Name constructor parameter
     * @param Password constructor parameter*/
    public User(Integer User_ID, String User_Name, String Password) {
        this.User_ID = User_ID;
        this.User_Name = User_Name;
        this.Password = Password;
    }

    /** Get User_ID.
     * @return returns Integer field*/
    public Integer getUserId() {
        return User_ID;
    }

    /** Get and Set User_Name.
     * @return returns String field*/
    public String getUsername() {
        return User_Name;
    }

    /** Get and Set Password.
     * @return returns String field*/
    public String getPassword() {
        return Password;
    }

    /** Display User in String format.
     * @return returns String*/
    public String showUser() {
        return String.format("User %d: %s", this.User_ID, this.User_Name);
    }
}
