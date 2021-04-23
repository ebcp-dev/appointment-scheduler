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
import models.Appointment;
import models.Contact;
import models.Customer;
import models.User;
import utils.UserHolder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
/** This class controls EditAppointment.fxml */
public class EditAppointment {
    /** Store current Customer data here. */
    private Customer currentCustomer;
    /** Store current Appointment data here. */
    private Appointment currentAppointment;
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
    @FXML
    private Button DeleteAppointmentButton;
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

    /** Receive appointment data from Appointments page.
     * @param appointment get appointment data
     * @throws SQLException get contacts from db*/
    public void appointmentData(Appointment appointment) throws SQLException {
        System.out.println("Editing: Appointment " + appointment.getAppointment_ID());
        /** Initialize global variables. */
        currentAppointment = appointment;
        currentCustomer = Customers.getCustomerByCustomerID(appointment.getCustomer_ID());
        /** Populate form fields with appointment details. */
        TitleField.setText(appointment.getTitle());
        DescriptionField.setText(appointment.getDescription());
        LocationField.setText(appointment.getLocation());
        TypeField.setText(appointment.getType());
        StartDatePicker.setValue(appointment.getStart().toLocalDate());
        EndDatePicker.setValue(appointment.getEnd().toLocalDate());
        /** Show customer details on the side. */
        CustomerNameLabel.setText(currentCustomer.getCustomer_Name());
        CustomerAddressLabel.setText(currentCustomer.getAddress());
        CustomerPostalCodeLabel.setText(currentCustomer.getPostal_Code());
        CustomerPhoneLabel.setText(currentCustomer.getPhone());
        /** Populate time spinners. */
        populateStartTimeSpinner();
        populateEndTimeSpinner();
        /** Populate Contacts ComboBox. */
        showContacts();
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
                /** Set time spinner default to Appointment time. */
                return currentAppointment.getStart().toLocalTime().truncatedTo(ChronoUnit.HOURS);
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
                /** Set time spinner default to Appointment time. */
                return currentAppointment.getEnd().toLocalTime().truncatedTo(ChronoUnit.HOURS);
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

    /** Get all countries from database.
     * @throws SQLException get contacts from db*/
    public void showContacts() throws SQLException {
        /** Display country name in ComboBox options using lambda expression. */
        Callback<ListView<Contact>, ListCell<Contact>> factory = lv -> new ListCell<Contact>() {
            @Override
            protected void updateItem(Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                setText(empty ? "" : String.format("%s, %s", contact.getContactName(), contact.getEmail()));
            }
        };
        ContactsComboBox.setButtonCell(factory.call(null));
        ContactsComboBox.setCellFactory(factory);
        /** Get contacts from database. */
        for (Contact contact: Contacts.getContacts()) {
            contactOptions.addAll(contact);
            if (contact.getContact_ID() == currentAppointment.getContact_ID()) ContactsComboBox.setValue(contact);
        }
        ContactsComboBox.setItems(contactOptions);
    }
    /** Update existing Appointment.
     * @param Appointment_ID parameters for Appointment update
     * @param Title parameters for Appointment update
     * @param Description parameters for Appointment update
     * @param Location parameters for Appointment update
     * @param Type parameters for Appointment update
     * @param Start parameters for Appointment update
     * @param End parameters for Appointment update
     * @param Last_Updated_By parameters for Appointment update
     * @param Customer_ID parameters for Appointment update
     * @param User_ID parameters for Appointment update
     * @param Contact_ID parameters for Appointment update
     * @throws SQLException for update query*/
    public void updateAppointment(Integer Appointment_ID, String Title, String Description, String Location, String Type, LocalDateTime Start, LocalDateTime End, String Last_Updated_By, Integer Customer_ID, Integer User_ID, Integer Contact_ID) throws SQLException {
        /** Conditional update to make sure customers have no overlapping appointments. */
        String updateStatement = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = NOW(), Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ? AND NOT EXISTS(SELECT * FROM (SELECT Start, End FROM appointments appts WHERE appts.Start <= ? AND appts.END >= ? AND appts.Appointment_ID != ?)tempTable);";
        DBQuery.setPreparedStatement(conn, updateStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        /** Map variables to sql statement string. */
        ps.setString(1, Title);
        ps.setString(2, Description);
        ps.setString(3, Location);
        ps.setString(4, Type);
        ps.setObject(5, Start);
        ps.setObject(6, End);
        ps.setString(7, Last_Updated_By);
        ps.setInt(8, Customer_ID);
        ps.setInt(9, User_ID);
        ps.setInt(10, Contact_ID);
        ps.setInt(11, Appointment_ID);
        ps.setObject(12, End);
        ps.setObject(13, Start);
        ps.setInt(14, Appointment_ID);
        try {
            ps.execute();
            /** Show if statement was successfully executed. */
            if(ps.getUpdateCount() > 0) {
                System.out.println(ps.getUpdateCount() + " appointment(s) updated.");
                /** Show confirmation after successful edit. */
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment edit.");
                alert.setContentText("Appointment has been successfully edited.");
                Optional<ButtonType> result = alert.showAndWait();
                /** Go back to appointments screen after updating in database. */
                root = FXMLLoader.load(getClass().getResource("../views/Appointments.fxml"));
                stage = (Stage)DeleteAppointmentButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } else {
                System.out.println("Update failed.");
                /** Show warning if overlapping appointments. */
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Update failed.");
                alert.setContentText("Make sure appointment doesn't overlap with others.");
                Optional<ButtonType> result = alert.showAndWait();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /** Add customer controls. */
    /** Save customer data to database when clicked.
     * @param event get event from page
     * @throws SQLException update query*/
    @FXML
    public void handleSaveAppointmentButton (ActionEvent event) throws SQLException {
        Contact currentContact = (Contact) ContactsComboBox.getValue();
        LocalDate startDate = StartDatePicker.getValue();
        LocalDate endDate = EndDatePicker.getValue();
        LocalTime startTime = (LocalTime) StartTimeSpinner.getValue();
        LocalTime endTime = (LocalTime) EndTimeSpinner.getValue();
        if (TitleField.getText().length() > 0 && DescriptionField.getText().length() > 0 && LocationField.getText().length() > 0 && TypeField.getText().length() > 0 && startDate != null && endDate != null && currentContact != null) {
            /** Get values from update form. */
            String Title = TitleField.getText();
            String Description = DescriptionField.getText();
            String Location = LocationField.getText();
            String Type = TypeField.getText();
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
            /** Save current user as the most recent to update the data. */
            User currentUser = UserHolder.getInstance().getUser();
            updateAppointment(currentAppointment.getAppointment_ID(), Title, Description, Location, Type, startUTC.toLocalDateTime(), endUTC.toLocalDateTime(), currentUser.getUsername(), currentCustomer.getCustomer_ID(), currentUser.getUserId(), currentAppointment.getContact_ID());
        } else {
            /** Show warning if fields are empty. */
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fields cannot be empty.");
            alert.setContentText("Make sure none of the fields are empty.");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }
    /** Delete current selected customer row in table.
     * @param event get event from page
     * @throws IOException load fxml
     * @throws SQLException delete query*/
    @FXML
    public void handleDeleteAppointmentButton (ActionEvent event) throws IOException, SQLException {
        if (currentAppointment != null) {
            /** Show warning before deleting. */
            String confirmation = String.format("Are you sure you want to delete Appointment: %s?", currentAppointment.getTitle());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, confirmation);
            alert.setTitle("Confirm Delete");
            alert.showAndWait();
            /** Delete if ok button is pressed. */
            if (alert.getResult().getButtonData().isDefaultButton()) {
                Appointments.deleteAppointment(currentAppointment.getAppointment_ID());
                root = FXMLLoader.load(getClass().getResource("../views/Appointments.fxml"));
                stage = (Stage)DeleteAppointmentButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You must select an appointment to delete.");
            alert.setTitle("Select appointment.");
            alert.showAndWait();
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
        stage = (Stage)AppointmentsButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
