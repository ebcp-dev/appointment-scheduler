package controllers;

import db.DBConnection;
import db.DBQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Country;
import models.FirstLevelDivision;
import utils.UserHolder;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
/** This class is the controller for AddCustomer.fxml */
public class AddCustomer implements Initializable {
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
    /** Database connection. */
    Connection conn = DBConnection.startConnection();
    /** ObservableLists to hold ComboBox options. */
    ObservableList<Country> countryOptions = FXCollections.observableArrayList();
    ObservableList<FirstLevelDivision> divisionOptions = FXCollections.observableArrayList();
    /** JavaFX */
    Parent root;
    Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /** Populate Country and First Level Division ComboBoxes. */
        try {
            getAllCountries();
            /** Disable while no countries are selected. */
            FirstLevelDivisionsComboBox.setDisable(true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /** Get all countries from database.
     * @throws SQLException get countries from db */
    public void getAllCountries() throws SQLException {
        /** Display country name in ComboBox options. */
        Callback<ListView<Country>, ListCell<Country>> factory = lv -> new ListCell<Country>() {
            @Override
            protected void updateItem(Country country, boolean empty) {
                super.updateItem(country, empty);
                setText(empty ? "" : country.getCountry());
            }
        };
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
     * @throws SQLException get divisions from db */
    public void getDivisions() throws SQLException {
        /** Display division name in ComboBox options. */
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
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        FirstLevelDivisionsComboBox.setItems(divisionOptions);
    }

    /** Insert new Customers
     * @param Customer_Name parameters for new Customer object
     * @param Address parameters for new Customer object
     * @param Postal_Code parameters for new Customer object
     * @param Phone parameters for new Customer object
     * @param Created_By parameters for new Customer object
     * @param Last_Updated_By parameters for new Customer object
     * @param Division_ID parameters for new Customer object
     * @throws SQLException insert customer to db*/
    public void insertNewCustomer(String Customer_Name, String Address, String Postal_Code, String Phone, String Created_By,
                                  String Last_Updated_By, Integer Division_ID) throws SQLException {
        /** Insert sql statement with PreparedStatement. */
        String insertStatement = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, " +
                "Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES(?, ?, ?, ?, NOW(), ?, NOW(), ?, ?);";
        DBQuery.setPreparedStatement(conn, insertStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        /** Map variables to sql statement string. */
        ps.setString(1, Customer_Name);
        ps.setString(2, Address);
        ps.setString(3, Postal_Code);
        ps.setString(4, Phone);
        ps.setString(5, Created_By);
        ps.setString(6, Last_Updated_By);
        ps.setInt(7, Division_ID);
        /** Execute statement. */
        try {
            ps.execute();
            /** Show if statement was successfully executed. */
            if(ps.getUpdateCount() > 0) {
                System.out.println(ps.getUpdateCount() + " new row(s).");
                /** Show confirmation after successful add. */
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Customer added.");
                alert.setContentText("Customer has been added to the database.");
                Optional<ButtonType> result = alert.showAndWait();
                /** Back to Customers table after adding. */
                root = FXMLLoader.load(getClass().getResource("../views/Customers.fxml"));
                stage = (Stage) SaveCustomerButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } else {
                System.out.println("No changes.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /** Add customer controls.
     * Detects country options and populates divisions options accordingly
     * @param event get event from view
     * @throws SQLException get divisions from db*/
    @FXML
    public void handleCountryComboBox(ActionEvent event) throws SQLException {
        Country currentCountry = (Country) CountryComboBox.getSelectionModel().getSelectedItem();
        /** Enable FirstLevelDivisionsComboBox when country is selected. */
        if (currentCountry.getCountry().length() > 0) {
            /** Clear previous division options from other countries if any. */
            divisionOptions.clear();
            FirstLevelDivisionsComboBox.setDisable(false);
            getDivisions();
        }
    }
    /** Save customer data to database when clicked.
     * @param event get event from view
     * @throws SQLException insert into db
     * @throws IOException load resource file*/
    @FXML
    public void handleSaveCustomerButton (ActionEvent event) throws SQLException, IOException {
        Country currentCountry = (Country) CountryComboBox.getValue();
        FirstLevelDivision currentDivision = (FirstLevelDivision) FirstLevelDivisionsComboBox.getValue();
        if (CustomerNameField.getText().length() > 0 && CustomerAddressField.getText().length() > 0 && CustomerPostalCodeField.getText().length() > 0 && CustomerPhoneField.getText().length() > 0 && currentCountry.getCountry().length() > 0 && currentDivision.getDivision().length() > 0) {
            /** Get values from update form. */
            String Customer_Name = CustomerNameField.getText();
            String Address = CustomerAddressField.getText();
            String Postal_Code = CustomerPostalCodeField.getText();
            String Phone = CustomerPhoneField.getText();
            Integer Division_ID = currentDivision.getDivisionID();
            /** Save current user as the most recent to update the data. */
            String CurrentUser = UserHolder.getInstance().getUser().getUsername();
            insertNewCustomer(Customer_Name, Address, Postal_Code, Phone, CurrentUser, CurrentUser, Division_ID);
        } else {
            /** Show warning if fields are empty. */
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fields cannot be empty.");
            alert.setContentText("Make sure none of the fields are empty.");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    /** Toolbar navigation controls. */
    /** Go to main menu page when clicked.
     * @throws IOException loading resource files */
    @FXML
    public void handleMainMenuButton () throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Main.fxml"));
        stage = (Stage) MainMenuButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    /** Go to main customers page when clicked.
     * @throws IOException loading resource files */
    @FXML
    public void handleCustomersButton () throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Customers.fxml"));
        stage = (Stage)CustomersButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    /** Go to main appointments page when clicked.
     * @throws IOException loading resource files */
    @FXML
    public void handleAppointmentsButton () throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Appointments.fxml"));
        stage = (Stage) AppointmentsButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
