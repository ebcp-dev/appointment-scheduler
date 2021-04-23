package controllers;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Appointment;
import models.Contact;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
/** This class is the controller for Reports.fxml */
public class Reports implements Initializable {
    /** List views. */
    @FXML
    private ListView AppointmentsByMonth;
    @FXML
    private ListView AppointmentsByType;
    @FXML
    private ListView AppointmentsByAMPM;
    /** TableView columns */
    @FXML
    private TableView AppointmentsTable;
    @FXML
    private TableColumn AppointmentIDColumn;
    @FXML
    private TableColumn TitleColumn;
    @FXML
    private TableColumn DescriptionColumn;
    @FXML
    private TableColumn LocationColumn;
    @FXML
    private TableColumn TypeColumn;
    @FXML
    private TableColumn StartColumn;
    @FXML
    private TableColumn EndColumn;
    @FXML
    private TableColumn CustomerIDColumn;
    @FXML
    private ComboBox ContactsComboBox;
    /** Page controls. */
    @FXML
    private Button MainMenuButton;
    @FXML
    private Button CustomersButton;
    @FXML
    private Button AppointmentsButton;
    /** Database connection. */
    private static Connection conn = DBConnection.startConnection();
    /** Observable list hold data to populate table. */
    ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    ObservableList<Contact> allContacts = FXCollections.observableArrayList();
    /** Store months. */
    int[] appointmentsByMonth = new int[12];
    int[] appointmentsByAMPM = new int[2];
    HashMap<String, Integer> appointmentsByType = new HashMap<>();
    /** JavaFX */
    Parent root;
    Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /** Set properties of table columns. */
        AppointmentIDColumn.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("Appointment_ID"));
        TitleColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Title"));
        DescriptionColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Description"));
        LocationColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Location"));
        TypeColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Type"));
        StartColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Start"));
        EndColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("End"));
        CustomerIDColumn.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("Customer_ID"));
        try {
            populateReports();
            showContacts();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /** Show appointments count by month and type.
     * @throws SQLException get appointments from db*/
    public void populateReports() throws SQLException {
        for (Appointment appt: Appointments.getAppointments()) {
            /** Store appointments into observable list. */
            allAppointments.addAll(appt);
            /** Match month with array index. */
            appointmentsByMonth[appt.getStart().toLocalDate().getMonthValue()-1]++;
            /** Add each appointment type and count as key, value pair. */
            Integer appointmentCount = appointmentsByType.get(appt.getType());
            if (appointmentCount != null) {
                appointmentsByType.put(appt.getType(), appointmentCount+1);
            } else {
                appointmentsByType.put(appt.getType(), 1);
            }
            /** Determine if appointment was scheduled in AM or PM hours. */
            if (appt.getStart().getHour() < 12) {
                appointmentsByAMPM[0]++;
            } else {
                appointmentsByAMPM[1]++;
            }
        }
        /** Display as 'Month: Number of appointments.' in ListView. */
        int monthCount = 1;
        for (int apptsInMonth: appointmentsByMonth) {
            AppointmentsByMonth.getItems().add(String.format("%s: %d", Month.of(monthCount), apptsInMonth));
            monthCount++;
        }
        /** Display as 'Type: Number of appointments.' in ListView. */
        for (Map.Entry<String, Integer> entry : appointmentsByType.entrySet()) {
            AppointmentsByType.getItems().add(String.format("%s: %d", entry.getKey(), entry.getValue()));
        }
        /** Display as 'AM: Count' and 'PM: Count' in ListView. */
        AppointmentsByAMPM.getItems().add("AM: " + appointmentsByAMPM[0]);
        AppointmentsByAMPM.getItems().add("PM: " + appointmentsByAMPM[1]);
        AppointmentsTable.setItems(allAppointments);
    }

    /** Get all contacts from database.
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
            allContacts.addAll(contact);
        }
        ContactsComboBox.setItems(allContacts);
    }

    /** Filter Appointments Table by Contact details.
     * @param event get event from page*/
    @FXML
    public void handleContactsComboBox (ActionEvent event) {
        Contact contactFilter = (Contact) ContactsComboBox.getValue();
        /** Filter table by contact. */
        FilteredList<Appointment> filteredData = new FilteredList<>(allAppointments);
        filteredData.setPredicate(row -> {
            return row.getContact_ID() == contactFilter.getContact_ID();
        });
        AppointmentsTable.setItems(filteredData);
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
