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
import models.Contact;
import models.Customer;
import models.User;
import utils.UserHolder;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;
/** This class is the controller for AddAppointment.fxml */
public class AddAppointment implements Initializable {
    /** Store current Customer data here. */
    private Customer currentCustomer;
    /** Display customer info with Labels. */
    @FXML
    private Label CustomerNameLabel;
    @FXML
    private Label CustomerAddressLabel;
    @FXML
    private Label CustomerPostalCodeLabel;
    @FXML
    private Label CustomerPhoneLabel;
    /** Display current user info with label. */
    @FXML
    private Label CurrentUserLabel;
    /** Form fields */
    @FXML
    private TextField TitleField;
    @FXML
    private TextField DescriptionField;
    @FXML
    private TextField LocationField;
    @FXML
    private TextField TypeField;
    @FXML
    private DatePicker StartDatePicker;
    @FXML
    private Spinner StartTimeSpinner;
    @FXML
    private DatePicker EndDatePicker;
    @FXML
    private Spinner EndTimeSpinner;
    @FXML
    private ComboBox ContactsComboBox;
    @FXML
    private Button SaveAppointmentButton;
    /** Page controls. */
    @FXML
    private Button MainMenuButton;
    @FXML
    private Button CustomersButton;
    @FXML
    private Button AppointmentsButton;
    /** Database connection. */
    private static Connection conn = DBConnection.startConnection();
    /** ObservableLists to hold ComboBox options. */
    ObservableList<Contact> contactOptions = FXCollections.observableArrayList();
    /** JavaFX */
    Parent root;
    Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /** Populate Contacts ComboBox and Time Spinners with hours. */
        try {
            getAllContacts();
            populateStartTimeSpinner();
            populateEndTimeSpinner();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /** Get customer data from customers table.
     * @param customer pass in customer object*/
    public void customerData (Customer customer) {
        /** Receive Customer data from customers page. */
        currentCustomer = customer;
        /** Set labels to customer data. */
        CustomerNameLabel.setText(customer.getCustomer_Name());
        CustomerAddressLabel.setText(customer.getAddress());
        CustomerPostalCodeLabel.setText(customer.getPostal_Code());
        CustomerPhoneLabel.setText(customer.getPhone());
        /** Display logged in user's username in a label. */
        User currentUser = UserHolder.getInstance().getUser();
        CurrentUserLabel.setText(currentUser.getUsername());
    }

    /** Populate time spinners with options. */
    public void populateStartTimeSpinner() {
        /** Show time by hours in time spinners. */
        SpinnerValueFactory<LocalTime> factory = new SpinnerValueFactory<LocalTime>() {
            {
                setValue(defaultValue());
            }
            private LocalTime defaultValue() {
                return LocalTime.now().truncatedTo(ChronoUnit.HOURS);
            }
            @Override
            public void decrement(int steps) {
                LocalTime value = getValue();
                setValue(value == null ? defaultValue() : value.minusHours(steps));
            }
            @Override
            public void increment(int steps) {
                LocalTime value = getValue();
                setValue(value == null ? defaultValue() : value.plusHours(steps));
            }
        };
        StartTimeSpinner.setValueFactory(factory);
    }
    public void populateEndTimeSpinner() {
        /** Show time by hours in time spinners. */
        SpinnerValueFactory<LocalTime> factory = new SpinnerValueFactory<LocalTime>() {
            {
                setValue(defaultValue());
            }
            private LocalTime defaultValue() {
                return LocalTime.now().truncatedTo(ChronoUnit.HOURS);
            }
            @Override
            public void decrement(int steps) {
                LocalTime value = getValue();
                setValue(value == null ? defaultValue() : value.minusHours(steps));
            }
            @Override
            public void increment(int steps) {
                LocalTime value = getValue();
                setValue(value == null ? defaultValue() : value.plusHours(steps));
            }
        };
        EndTimeSpinner.setValueFactory(factory);
    }

    /** Get all contacts from database.
     * @throws SQLException for SQL query*/
    public void getAllContacts() throws SQLException {
        /** Display country name in ComboBox options. */
        Callback<ListView<Contact>, ListCell<Contact>> factory = lv -> new ListCell<Contact>() {
            @Override
            protected void updateItem(Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                setText(empty ? "" : String.format("%s, %s", contact.getContactName(), contact.getEmail()));
            }
        };
        ContactsComboBox.setButtonCell(factory.call(null));
        ContactsComboBox.setCellFactory(factory);
        /** Get countries from database. */
        String selectStatement = "SELECT * FROM contacts;";
        DBQuery.setPreparedStatement(conn, selectStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        try {
            Contact contact;
            ps.execute();
            /** Get result set from query. */
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                Integer Contact_ID = rs.getInt("Contact_ID");
                String Contact_Name = rs.getString("Contact_Name");
                String Email = rs.getString("Email");
                contact = new Contact(Contact_ID, Contact_Name, Email);
                contactOptions.add(contact);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        ContactsComboBox.setItems(contactOptions);
    }
    /** Insert new Appointment.
     * @param Title String parameter
     * @param Description String parameter
     * @param Location String parameter
     * @param Type String parameter
     * @param Start LocalDateTime parameter
     * @param End LocalDateTime parameter
     * @param Created_By String parameter
     * @param Last_Updated_By String parameter
     * @param Customer_ID Integer parameter
     * @param User_ID Integer parameter
     * @param Contact_ID Integer parameter
     * @throws SQLException SQL insert */
    public void insertNewAppointment(String Title, String Description, String Location, String Type, LocalDateTime Start, LocalDateTime End, String Created_By, String Last_Updated_By, Integer Customer_ID, Integer User_ID, Integer Contact_ID) throws SQLException {
        /** Conditional insert to make sure customers have no overlapping appointments. */
        String insertStatement = "INSERT INTO appointments(Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) SELECT ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?, ?, ?, ? FROM dual WHERE NOT EXISTS(SELECT Start, End FROM appointments WHERE Start <= ? AND END >= ?);";
        DBQuery.setPreparedStatement(conn, insertStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        /** Map variables to sql statement string. */
        ps.setString(1, Title);
        ps.setString(2, Description);
        ps.setString(3, Location);
        ps.setString(4, Type);
        ps.setObject(5, Start);
        ps.setObject(6, End);
        ps.setString(7, Created_By);
        ps.setString(8, Last_Updated_By);
        ps.setInt(9, Customer_ID);
        ps.setInt(10, User_ID);
        ps.setInt(11, Contact_ID);
        ps.setObject(12, End);
        ps.setObject(13, Start);
        try {
            ps.execute();
            /** Show if statement was successfully executed. */
            if(ps.getUpdateCount() > 0) {
                System.out.println(ps.getUpdateCount() + " appointment(s) added.");
                /** Show confirmation after successful add. */
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment scheduled.");
                alert.setContentText("Appointment has been successfully scheduled for this customer.");
                Optional<ButtonType> result = alert.showAndWait();
                /** Back to Customers table after adding. */
                root = FXMLLoader.load(getClass().getResource("../views/Customers.fxml"));
                stage = (Stage) SaveAppointmentButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                /** Show error if appointment overlaps with other appointments. */
            } else {
                System.out.println("No appointment added.");
                /** Show confirmation after successful add. */
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No appointment scheduled.");
                alert.setContentText("Make sure appointment doesn't overlap with others.");
                Optional<ButtonType> result = alert.showAndWait();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /** Toolbar navigation controls. */
    /** Go to main menu page when clicked.
     * @throws IOException loading resource file*/
    @FXML
    public void handleMainMenuButton () throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Main.fxml"));
        stage = (Stage) MainMenuButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    /** Go to main customers page when clicked.
     * @throws IOException loading resource file*/
    @FXML
    public void handleCustomersButton () throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Customers.fxml"));
        stage = (Stage)CustomersButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    /** Go to main appointments page when clicked.
     * @throws IOException loading resource file*/
    @FXML
    public void handleAppointmentsButton () throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Appointments.fxml"));
        stage = (Stage) AppointmentsButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /** Add customer controls. */
    /** Save customer data to database when clicked.
     * @param event get event from page
     * @throws SQLException for inserting appointment */
    @FXML
    public void handleSaveAppointmentButton (ActionEvent event) throws SQLException {
        Contact currentContact = (Contact) ContactsComboBox.getValue();
        LocalDate startDate = StartDatePicker.getValue();
        LocalDate endDate = EndDatePicker.getValue();
        LocalTime startTime = (LocalTime) StartTimeSpinner.getValue();
        LocalTime endTime = (LocalTime) EndTimeSpinner.getValue();
        if (TitleField.getText().length() > 0 && DescriptionField.getText().length() > 0 && LocationField.getText().length() > 0 && TypeField.getText().length() > 0 && startDate != null && endDate != null && currentContact != null) {
            /** Convert date and time values. */
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
            /** If start date and time is after end date and time show error. */
            if (startDateTime.isAfter(endDateTime) || startDateTime.isEqual(endDateTime)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Date");
                alert.setContentText("Start date and time must be before end date and time.");
                Optional<ButtonType> result = alert.showAndWait();
                return;
                /** Show error if start date and time has passed. */
            } else if (startDateTime.isBefore(LocalDateTime.now()) || endDateTime.isBefore(LocalDateTime.now())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Date");
                alert.setContentText("Start or end date has passed.");
                Optional<ButtonType> result = alert.showAndWait();
                return;
            }
            /** Convert start date and time to EST. */
            ZonedDateTime startEST = ZonedDateTime.of(startDateTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));
            ZonedDateTime endEST = ZonedDateTime.of(endDateTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/New_York"));
            /** Convert start date and time to UTC. */
            ZonedDateTime startUTC = ZonedDateTime.of(startDateTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime endUTC = ZonedDateTime.of(endDateTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
            /** Show error if appointment is scheduled outside of business hours in EST. */
            if (startEST.getHour() < 8 || endEST.getHour() < 8 || startEST.getHour() > 22 || endEST.getHour() > 22) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Time");
                alert.setContentText("Must be scheduled within business hours (8am-10pm EST).");
                Optional<ButtonType> result = alert.showAndWait();
                return;
            }
            /** Get values from update form. */
            String Title = TitleField.getText();
            String Description = DescriptionField.getText();
            String Location = LocationField.getText();
            String Type = TypeField.getText();
            /** Save current user as the most recent to update the data. */
            User currentUser = UserHolder.getInstance().getUser();
            insertNewAppointment(Title, Description, Location, Type, startUTC.toLocalDateTime(), endUTC.toLocalDateTime(), currentUser.getUsername(), currentUser.getUsername(), currentCustomer.getCustomer_ID(), currentUser.getUserId(), currentContact.getContact_ID());
        } else {
            /** Show warning if fields are empty. */
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fields cannot be empty.");
            alert.setContentText("Make sure none of the fields are empty.");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }
}
