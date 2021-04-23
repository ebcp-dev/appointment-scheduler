package controllers;

import db.DBConnection;
import db.DBQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import models.Country;
import models.Customer;
import models.FirstLevelDivision;
import utils.UserHolder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
/** This class is the controller for EditCustomer.fxml */
public class EditCustomer {
    /** Store current Customer data here. */
    Customer currentCustomer;
    /** Form fields */
    @FXML
    private TextField CustomerNameField;
    @FXML
    private TextField CustomerAddressField;
    @FXML
    private TextField CustomerPostalCodeField;
    @FXML
    private TextField CustomerPhoneField;
    @FXML
    private ComboBox CountryComboBox;
    @FXML
    private ComboBox FirstLevelDivisionsComboBox;
    @FXML
    private Button SaveCustomerButton;
    /** Page controls. */
    @FXML
    private Button MainMenuButton;
    @FXML
    private Button CustomersButton;
    @FXML
    private Button AppointmentsButton;
    @FXML
    private Button DeleteCustomerButton;
    /** Database connection. */
    Connection conn = DBConnection.startConnection();
    /** ObservableLists to hold ComboBox options. */
    ObservableList<Country> countryOptions = FXCollections.observableArrayList();
    ObservableList<FirstLevelDivision> divisionOptions = FXCollections.observableArrayList();
    /** JavaFX */
    Parent root;
    Stage stage;

    /** Populate form with Customer data. */
    /** Get customer data from customers table.
     * @param customer customer data
     * @throws SQLException get divisions from db*/
    public void customerData (Customer customer) throws SQLException {
        System.out.println("Editing: Customer " + customer.getCustomer_ID());
        /** Populate Country ComboBox. */
        getAllCountries();
        currentCustomer = customer;
        /** Populate customer fields. */
        CustomerNameField.setText(customer.getCustomer_Name());
        CustomerAddressField.setText(customer.getAddress());
        CustomerPostalCodeField.setText(customer.getPostal_Code());
        CustomerPhoneField.setText(customer.getPhone());
        CountryComboBox.setValue(getCountryByDivisionID(customer.getDivision_ID()));
        /** Populate FirstLevelDivisions ComboBox */
        getDivisions();
    }
    /** Get all countries from database.
     * @throws SQLException get countries from db*/
    public void getAllCountries() throws SQLException {
        /** Display country name in ComboBox options. */
        /** Use lambda expression to override methods for displaying text in the ComboBox. */
        Callback<ListView<Country>, ListCell<Country>> factory = lv -> new ListCell<Country>() {
            @Override
            protected void updateItem(Country country, boolean empty) {
                super.updateItem(country, empty);
                setText(empty ? "" : country.getCountry());
            }
        };
        CountryComboBox.setConverter(new StringConverter<Country>() {
            @Override
            public String toString(Country object) {
                return object.getCountry();
            }
            @Override
            public Country fromString(String string) {
                return (Country) CountryComboBox.getValue();
            }
        });
        CountryComboBox.setButtonCell(factory.call(null));
        CountryComboBox.setCellFactory(factory);
        /** Get countries from database. */
        String selectStatement = "SELECT * FROM countries;";
        DBQuery.setPreparedStatement(conn, selectStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        try {
            Country country;
            ps.execute();
            /** Get result set from query. */
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                Integer Country_ID = rs.getInt("Country_ID");
                String Country = rs.getString("Country");
                country = new Country(Country_ID, Country);
                countryOptions.add(country);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        CountryComboBox.setItems(countryOptions);
    }
    /** Get first level divisions from database based on Country_ID.
     * @throws SQLException get divisions from db*/
    public void getDivisions() throws SQLException {
        /** Display division name in ComboBox options.  */
        /** Use lambda expression to override methods for displaying text in the ComboBox. */
        Callback<ListView<FirstLevelDivision>, ListCell<FirstLevelDivision>> factory = lv -> new ListCell<FirstLevelDivision>() {
            @Override
            protected void updateItem(FirstLevelDivision division, boolean empty) {
                super.updateItem(division, empty);
                setText(empty ? "" : division.getDivision());
            }
        };
        FirstLevelDivisionsComboBox.setButtonCell(factory.call(null));
        FirstLevelDivisionsComboBox.setCellFactory(factory);
        /** Get divisions from database. */
        Country currentCountry = (Country) CountryComboBox.getValue();
        String selectStatement = "SELECT * FROM first_level_divisions WHERE Country_ID = ?;";
        DBQuery.setPreparedStatement(conn, selectStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        /** Map variables to sql statement string. */
        ps.setInt(1, currentCountry.getCountryID());
        try {
            FirstLevelDivision division;
            ps.execute();
            /** Get result set from query. */
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                Integer Division_ID = rs.getInt("Division_ID");
                String Division = rs.getString("Division");
                Integer Country_ID = rs.getInt("Country_ID");
                division = new FirstLevelDivision(Division_ID, Division, Country_ID);
                divisionOptions.addAll(division);
                /** Set FirstLevelDivisionsComboBox default to current customer's division. */
                if (Division_ID == currentCustomer.getDivision_ID()) {
                    FirstLevelDivisionsComboBox.setValue(division);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        FirstLevelDivisionsComboBox.setItems(divisionOptions);
    }

    /** SQL methods for updating customers. */
    /** Update existing Customer.
     * @param Customer_ID params for sql update
     * @param Customer_Name params for sql update
     * @param Address params for sql update
     * @param Postal_Code params for sql update
     * @param Phone params for sql update
     * @param Last_Updated_By params for sql update
     * @param Division_ID params for sql update
     * @throws SQLException update query*/
    public void updateCustomer(Integer Customer_ID, String Customer_Name, String Address, String Postal_Code, String Phone, String Last_Updated_By, Integer Division_ID) throws SQLException {
        String updateStatement = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = NOW(), Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?;";
        /** Create PreparedStatement. */
        DBQuery.setPreparedStatement(conn, updateStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        /** Map variables to sql statement string. */
        ps.setString(1, Customer_Name);
        ps.setString(2, Address);
        ps.setString(3, Postal_Code);
        ps.setString(4, Phone);
        ps.setString(5, Last_Updated_By);
        ps.setInt(6, Division_ID);
        ps.setInt(7, Customer_ID);
        /** Execute statement. */
        try {
            ps.execute();
            /** Show if statement was successfully executed. */
            if(ps.getUpdateCount() > 0) {
                System.out.println(ps.getUpdateCount() + " customer(s) updated.");
                /** Show confirmation after successful edit. */
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Customer edit.");
                alert.setContentText("Customer has been successfully edited.");
                Optional<ButtonType> result = alert.showAndWait();
                /** Back to Customers table after adding. */
                root = FXMLLoader.load(getClass().getResource("../views/Customers.fxml"));
                stage = (Stage) SaveCustomerButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } else {
                System.out.println(ps.getResultSet().getStatement() + " Customer not found.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    /** Get country name based on Division_ID of Customer.
     * @param Division_ID for sql select
     * @return returns Country object
     * @throws SQLException sql query*/
    public Country getCountryByDivisionID (Integer Division_ID) throws SQLException {
        Country customerCountry = new Country();
        String selectStatement = "SELECT countries.*, first_level_divisions.Country_ID FROM countries, first_level_divisions WHERE first_level_divisions.Division_ID = ? AND countries.Country_ID = first_level_divisions.Country_ID;";
        /** Create PreparedStatement. */
        DBQuery.setPreparedStatement(conn, selectStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        /** Map variables to sql statement string. */
        ps.setInt(1, Division_ID);
        /** Execute statement. */
        try {
            ps.execute();
            ResultSet rs = ps.getResultSet();
            /** Set country based on Division_ID on the ComboBox. */
            if (rs.next()) {
                customerCountry = new Country(rs.getInt("Country_ID"), rs.getString("Country"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return customerCountry;
    }

    /** Edit controls.
     * @param event get event from page
     * @throws SQLException load countries from db*/
    @FXML
    public void handleCountryComboBox(ActionEvent event) throws SQLException {
        Country currentCountry = (Country) CountryComboBox.getSelectionModel().getSelectedItem();
        /** Enable FirstLevelDivisionsComboBox when country is selected. */
        if (currentCountry.getCountry().length() > 0) {
            /** Clear previous division options from other countries if any. */
            divisionOptions.clear();
            getDivisions();
        }
    }
    /** Save customer data to database when clicked.
     * @param event get event from page
     * @throws SQLException update customer in db*/
    @FXML
    public void handleSaveCustomerButton (ActionEvent event) throws SQLException {
        if (CustomerNameField.getText().length() > 0 && CustomerAddressField.getText().length() > 0 && CustomerPostalCodeField.getText().length() > 0 && CustomerPhoneField.getText().length() > 0 && FirstLevelDivisionsComboBox.getValue() != null) {
            /** Get values from update form. */
            Integer Customer_ID = currentCustomer.getCustomer_ID();
            String Customer_Name = CustomerNameField.getText();
            String Address = CustomerAddressField.getText();
            String Postal_Code = CustomerPostalCodeField.getText();
            String Phone = CustomerPhoneField.getText();
            FirstLevelDivision customerDivision = (FirstLevelDivision) FirstLevelDivisionsComboBox.getValue();
            Integer Division_ID = customerDivision.getDivisionID();
            /** Save current user as the most recent to update the data. */
            String Last_Updated_By = UserHolder.getInstance().getUser().getUsername();
            updateCustomer(Customer_ID, Customer_Name, Address, Postal_Code, Phone, Last_Updated_By, Division_ID);
        }
    }
    /** Delete current customer.
     * @param event get event from page
     * @throws IOException load fxml*/
    @FXML
    public void handleDeleteCustomerButton (ActionEvent event) throws IOException {
        if (currentCustomer != null) {
            /** Show warning before deleting. */
            String confirmation = String.format("Deleting this customer will delete all associated appointments. Are you sure you want to delete Customer: %s?", currentCustomer.getCustomer_Name());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, confirmation);
            alert.setTitle("Confirm Delete");
            alert.showAndWait();
            /** Delete if ok button is pressed. */
            if (alert.getResult().getButtonData().isDefaultButton()) {
                Customers.deleteCustomer(currentCustomer.getCustomer_ID());
                root = FXMLLoader.load(getClass().getResource("../views/Customers.fxml"));
                stage = (Stage)DeleteCustomerButton.getScene().getWindow();
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.show();
            }
        }
    }

    /** Toolbar navigation controls. */
    /** Go to main menu page when clicked.
     * @throws IOException load fxml*/
    @FXML
    public void handleMainMenuButton () throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Main.fxml"));
        stage = (Stage) MainMenuButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    /** Go to main customers page when clicked.
     * @throws IOException load fxml*/
    @FXML
    public void handleCustomersButton () throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Customers.fxml"));
        stage = (Stage)CustomersButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    /** Go to main appointments page when clicked.
     * @throws IOException load fxml*/
    @FXML
    public void handleAppointmentsButton () throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Appointments.fxml"));
        stage = (Stage) AppointmentsButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
