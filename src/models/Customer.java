package models;
/*** This the class for the Customer model. */
public class Customer {
    /*** Class fields. */
    private Integer Customer_ID;
    private String Customer_Name;
    private String Address;
    private String Postal_Code;
    private String Phone;
    /** Foreign key. */
    private Integer Division_ID;
    private String Division;

    /** Constructor. */
    public Customer() {
    }

    /** Constructor.
     * @param Customer_ID constructor parameters
     * @param Customer_Name constructor parameters
     * @param Address constructor parameters
     * @param Postal_Code constructor parameters
     * @param Phone constructor parameters
     * @param Division_ID constructor parameters*/
    public Customer(Integer Customer_ID, String Customer_Name, String Address, String Postal_Code, String Phone, Integer Division_ID) {
        this.Customer_ID = Customer_ID;
        this.Customer_Name = Customer_Name;
        this.Address = Address;
        this.Postal_Code = Postal_Code;
        this.Phone = Phone;
        this.Division_ID = Division_ID;
    }

    /** Constructor.
     * @param Customer_ID constructor parameters
     * @param Customer_Name constructor parameters
     * @param Address constructor parameters
     * @param Postal_Code constructor parameters
     * @param Phone constructor parameters
     * @param Division_ID constructor parameters
     * @param Division constructor parameters*/
    public Customer(Integer Customer_ID, String Customer_Name, String Address, String Postal_Code, String Phone, Integer Division_ID, String Division) {
        this.Customer_ID = Customer_ID;
        this.Customer_Name = Customer_Name;
        this.Address = Address;
        this.Postal_Code = Postal_Code;
        this.Phone = Phone;
        this.Division_ID = Division_ID;
        this.Division = Division;
    }

    /** Get Customer_ID.
     * @return returns integer field*/
    public Integer getCustomer_ID() {
        return Customer_ID;
    }

    /** Get Division_ID.
     * @return returns integer field*/
    public Integer getDivision_ID() {
        return Division_ID;
    }

    /** Get and Set Customer_Name.
     * @return returns String field*/
    public String getCustomer_Name() {
        return Customer_Name;
    }

    /** Get and Set Division.
     * @return returns String field*/
    public String getDivision() {
        return Division;
    }

    /** Get and Set Address.
     * @return returns String field*/
    public String getAddress() {
        return Address;
    }

    /** Get and Set Postal_Code.
     * @return returns String field*/
    public String getPostal_Code() {
        return Postal_Code;
    }

    /** Get and Set Phone.
     * @return returns String field*/
    public String getPhone() {
        return Phone;
    }

    /** Display Customer in String format.
     * @return returns String*/
    public String showCustomer() {
        return String.format("\n" +
                "Customer_ID: %d\n" +
                "Customer_Name: %s\n" +
                "Address: %s\n" +
                "Postal_Code: %s\n" +
                "Phone: %s\n" +
                "Division_ID: %d\n", this.Customer_ID, this.Customer_Name, this.Address, this.Postal_Code,
                this.Phone, this.Division_ID);
    }
}
