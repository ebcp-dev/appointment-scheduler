package models;
/*** This the class for the FirstLevelDivision model. */
public class FirstLevelDivision extends Country{
    /** Class fields. */
    private Integer Division_ID;
    private String Division;
    /** Foreign key. */
    private Integer Country_ID;

    /** Constructor. */
    public FirstLevelDivision() {
    }

    /** Constructor.
     * @param Division_ID constructor parameter
     * @param Division constructor parameter
     * @param Country_ID constructor parameter*/
    public FirstLevelDivision(Integer Division_ID, String Division, Integer Country_ID) {
        this.Division_ID = Division_ID;
        this.Division = Division;
        this.Country_ID = Country_ID;
    }

    /** Get Division_ID.
     * @return returns Integer field*/
    public Integer getDivisionID() {
        return Division_ID;
    }

    /** Get and Set Division name.
     * @return returns String field*/
    public String getDivision() {
        return Division;
    }

    /** Display Division in String format.
     * @return returns String*/
    public String showDivision() {
        return String.format("Division %d: %s, Country_ID: %d", this.Division_ID, this.Division, this.Country_ID);
    }
}
