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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
/** This class is the controller for Customers.fxml */
public class Customers implements Initializable {
    private static Connection conn = DBConnection.startConnection();
    /** Current selected row. */
    private Customer customerRow;
    /** TableView columns */
    @FXML
    private TableView CustomerTable;
    @FXML
    private TableColumn CustomerIDColumn;
    @FXML
    private TableColumn CustomerNameColumn;
    @FXML
    private TableColumn CustomerAddressColumn;
    @FXML
    private TableColumn CustomerPostalCodeColumn;
    @FXML
    private TableColumn CustomerPhoneColumn;
    @FXML
    private TableColumn CustomerDivisionColumn;
    /** Customer controls. */
    @FXML
    private Button MainMenuButton;
    @FXML
    private Button AppointmentsButton;
    @FXML
    private Button RefreshTableButton;
    @FXML
    private Button AddCustomerButton;
    @FXML
    private Button EditCustomerButton;
    @FXML
    private Button DeleteCustomerButton;
    /** Observable list hold data to populate table. */
    ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    /** JavaFX */
    Parent root;
    Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /** Set properties of table columns. */
        CustomerIDColumn.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("Customer_ID"));
        CustomerNameColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("Customer_Name"));
        CustomerAddressColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("Address"));
        CustomerPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("Postal_Code"));
        CustomerPhoneColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("Phone"));
        CustomerDivisionColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("Division"));
        /** Call method to populate tableview with data. */
        try {
            showAllCustomers();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        /** Get current selected row and save the data into the customerRow field to be accessed by methods in the class. */
        CustomerTable.setOnMouseClicked((MouseEvent event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                customerRow = (Customer) CustomerTable.getSelectionModel().getSelectedItem();
            }
        });
    }

    /** SQL statement to populate tableview with customer data.
     * @throws SQLException get contacts from db*/
    @FXML
    public void showAllCustomers() throws SQLException {
        /** Create PreparedStatement. */
        /** Get customer data and matching division name. */
        String selectStatement = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, customers.Division_ID, first_level_divisions.Division, first_level_divisions.Division_ID FROM customers LEFT JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID;";
        DBQuery.setPreparedStatement(conn, selectStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        /** Execute sql statement. */
        try {
            Customer customerData;
            ps.execute(selectStatement);
            ResultSet rs = ps.getResultSet();
            while(rs.next()) {
                /** Store customer and first_level_division data into temp variables. */
                Integer Customer_ID = rs.getInt("customers.Customer_ID");
                String Customer_Name = rs.getString("customers.Customer_Name");
                String Address = rs.getString("customers.Address");
                String Postal_Code = rs.getString("customers.Postal_Code");
                String Phone = rs.getString("customers.Phone");
                Integer custDivision_ID = rs.getInt("customers.Division_ID");
                Integer divDivision_ID = rs.getInt("first_level_divisions.Division_ID");
                String Division = "";
                /** Get Division name based on Customer's Division_ID. */
                if (custDivision_ID == divDivision_ID) Division = rs.getString("first_level_divisions.Division");
                customerData = new Customer(Customer_ID, Customer_Name, Address, Postal_Code, Phone, custDivision_ID, Division);
                allCustomers.addAll(customerData);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        /** Set table data with observable list. */
        CustomerTable.setItems(allCustomers);
    }
    /** Get Customer by Customer_ID.
     * @param Customer_ID for sql query
     * @return return customer object*/
    public static Customer getCustomerByCustomerID(Integer Customer_ID) {
        Customer customer = new Customer();
        String selectStatement = "SELECT * FROM customers WHERE Customer_ID = ?;";
        /** Execute statement */
        try {
            DBQuery.setPreparedStatement(conn, selectStatement);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            /** Map variables to sql statement string. */
            ps.setInt(1, Customer_ID);
            ps.execute();
            /** Show if statement was successfully executed. */
            ResultSet rs = ps.getResultSet();
            if(rs.next()) {
                customer = new Customer(rs.getInt("Customer_ID"), rs.getString("Customer_Name"), rs.getString("Address"), rs.getString("Postal_Code"), rs.getString("Phone"), rs.getInt("Division_ID"));
            } else {
                System.out.println(ps.getResultSet().getStatement() + " Customer not found.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return customer;
    }
    /** Delete existing Customer.
     * @param Customer_ID for delete query*/
    public static void deleteCustomer(Integer Customer_ID) {
        String deleteStatement = "DELETE FROM customers WHERE Customer_ID = ?;";
        /** Execute statement */
        try {
            /** Delete appointments first due to foreign key constraints. */
            Appointments.deleteAppointmentsByCustomerID(Customer_ID);
            DBQuery.setPreparedStatement(conn, deleteStatement);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            /** Map variables to sql statement string. */
            ps.setInt(1, Customer_ID);
            ps.execute();
            /** Show if statement was successfully executed. */
            if(ps.getUpdateCount() > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Customer has been deleted.");
                alert.setTitle("Customer Deleted");
                alert.showAndWait();
                System.out.println(ps.getUpdateCount() + " customer(s) deleted.");
            } else {
                System.out.println("Customer not found.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /** Customers table controls. */
    /** Refresh table with new updates.
     * @throws IOException load fxml*/
    @FXML
    public void handleRefreshTableButton () throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Customers.fxml"));
        stage = (Stage)RefreshTableButton.getScene().getWindow();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
    /** Go to new scene with add customer form.
     * @param event get event from page
     * @throws IOException load fxml*/
    @FXML
    public void handleAddCustomerButton (ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/AddCustomer.fxml"));
        stage = (Stage)AddCustomerButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    /** Go to new scene with edit customer form.
     * @param event get event from page
     * @throws SQLException update query
     * @throws IOException load fxml*/
    @FXML
    public void handleEditCustomerButton (ActionEvent event) throws IOException, SQLException {
        if (customerRow != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/EditCustomer.fxml"));
            root = loader.load();
            /** Pass current selected customer row data into edit scene. */
            EditCustomer editCustomer = loader.getController();
            editCustomer.customerData(customerRow);
            stage = (Stage)EditCustomerButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You must select a customer to edit.");
            alert.setTitle("Select customer.");
            alert.showAndWait();
        }
    }
    /** Go to new scene with edit customer form.
     * @param event get even from page
     * @throws IOException load fxml*/
    @FXML
    public void handleAddAppointmentButton (ActionEvent event) throws IOException {
        if (customerRow != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/AddAppointment.fxml"));
            root = loader.load();
            /** Pass current selected customer row data into edit scene. */
            AddAppointment addAppointment = loader.getController();
            addAppointment.customerData(customerRow);
            stage = (Stage)EditCustomerButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You must select a customer to set up an appointment.");
            alert.setTitle("Select customer.");
            alert.showAndWait();
        }
    }
    /** Delete current selected customer row in table.
     * @param event get even from page
     * @throws IOException load fxml*/
    @FXML
    public void handleDeleteCustomerButton (ActionEvent event) throws IOException {
        if (customerRow != null) {
            /** Show warning before deleting. */
            String confirmation = String.format("Deleting this customer will delete all associated appointments. Are you sure you want to delete Customer: %s?", customerRow.getCustomer_Name());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, confirmation);
            alert.setTitle("Confirm Delete");
            alert.showAndWait();
            /** Delete if ok button is pressed. */
            if (alert.getResult().getButtonData().isDefaultButton()) {
                deleteCustomer(customerRow.getCustomer_ID());
                root = FXMLLoader.load(getClass().getResource("../views/Customers.fxml"));
                stage = (Stage)DeleteCustomerButton.getScene().getWindow();
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You must select a customer to delete.");
            alert.setTitle("Select customer.");
            alert.showAndWait();
        }
    }

    /** These methods handle page controls. */
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
