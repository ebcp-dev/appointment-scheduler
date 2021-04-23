package models;
/*** This the class for the Country model. */
public class Country {
    private Integer Country_ID;
    private String Country;

    /** Constructor. */
    public Country() {
    }

    /** Constructor.
     * @param Country constructor parameters
     * @param Country_ID constructor parameters*/
    public Country(Integer Country_ID, String Country) {
        this.Country_ID = Country_ID;
        this.Country = Country;
    }

    /** Get Country_ID.
     * @return returns String field */
    public Integer getCountryID() {
        return Country_ID;
    }

    /** Get and Set country name.
     * @return returns String field*/
    public String getCountry() {
        return Country;
    }

    /** Display Country in String format.
     * @return returns String*/
    public String showCountry() {
        return String.format("Country %d: %s", this.Country_ID, this.Country);
    }
}
