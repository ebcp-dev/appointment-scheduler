package controllers;

import db.DBConnection;
import db.DBQuery;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.Appointment;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
/** This class is the controller for Appointments.fxml */
public class Appointments implements Initializable {
    /** Current selected row. */
    private Appointment appointmentRow;
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
    private TableColumn ContactColumn;
    @FXML
    private TableColumn TypeColumn;
    @FXML
    private TableColumn StartColumn;
    @FXML
    private TableColumn EndColumn;
    @FXML
    private TableColumn CustomerNameColumn;
    /** Appointments page controls. */
    @FXML
    private Button MainMenuButton;
    @FXML
    private Button CustomersButton;
    @FXML
    private Button RefreshTableButton;
    @FXML
    private Button EditAppointmentButton;
    @FXML
    private Button DeleteAppointmentButton;
    /** Filter combo boxes. */
    @FXML
    private ComboBox MonthComboBox;
    @FXML
    private ComboBox WeekComboBox;
    /** Database connection. */
    private static Connection conn = DBConnection.startConnection();
    /** Observable list hold data to populate table. */
    ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    ObservableList<String> allMonths = FXCollections.observableArrayList();
    ObservableList<String> allWeeksInMonth = FXCollections.observableArrayList();
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
        ContactColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Contact_Details"));
        TypeColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Type"));
        StartColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Start"));
        EndColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("End"));
        CustomerNameColumn.setCellValueFactory(new PropertyValueFactory<Appointment, String>("Customer_Name"));
        /** Call method to populate tableview with data. */
        try {
            showAllAppointments();
            /** Disable while no countries are selected. */
            WeekComboBox.setDisable(true);
            /** Iterate through months in the year. */
            for (int i = 1; i < Month.values().length+1; i++) {
                YearMonth currentMonth = YearMonth.of(YearMonth.now().getYear(), i);
                allMonths.add(currentMonth.getMonth().name());
            }
            MonthComboBox.setItems(allMonths);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        /** Get current selected row and save the data into the appointmentRow field to be accessed by methods in the class. */
        AppointmentsTable.setOnMouseClicked((MouseEvent event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                appointmentRow = (Appointment) AppointmentsTable.getSelectionModel().getSelectedItem();
            }
        });
    }

    /** SQL operations for appointments. */
    /** Find all appointments from the database for display.
     * @throws SQLException get appointments from db*/
    public void showAllAppointments() throws SQLException {
        /** SELECT SQL statement to retrieve all appointments from database. */
        String selectStatement = "SELECT * FROM appointments;";
        DBQuery.setPreparedStatement(conn, selectStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        try {
            ps.execute();
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                /** Add to arraylist all appointments found. */
                Integer Appointment_ID = rs.getInt("Appointment_ID");
                String Title = rs.getString("Title");
                String Description = rs.getString("Description");
                String Location = rs.getString("Location");
                String Type = rs.getString("Type");
                /** Convert to LocalDateTime. */
                LocalDateTime Start = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime End = rs.getTimestamp("End").toLocalDateTime();
                Integer Customer_ID = rs.getInt("Customer_ID");
                Integer Contact_ID = rs.getInt("Contact_ID");
                Integer User_ID = rs.getInt("User_ID");
                /** Details to display in table. */
                String customerNameCol = Customers.getCustomerByCustomerID(Customer_ID).getCustomer_Name();
                /** Display Contact as "name, email". */
                String contactDetailsCol = Contacts.getContactByContactID(Contact_ID).getContactName() + ", " + Contacts.getContactByContactID(Contact_ID).getEmail();
                Appointment apptData = new Appointment(Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, Contact_ID, User_ID, customerNameCol, contactDetailsCol);
                allAppointments.addAll(apptData);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        /** Set table data with observable list. */
        AppointmentsTable.setItems(allAppointments);
    }
    /** Get all appointment times from database.
     * @return returns array list of appointments
     * @throws SQLException get appointments from db*/
    public static List<Appointment> getAppointments() throws SQLException {
        List<Appointment> appointmentTimes = new ArrayList<Appointment>();
        /** SELECT SQL statement to retrieve all appointments from database. */
        String selectStatement = "SELECT * FROM appointments;";
        DBQuery.setPreparedStatement(conn, selectStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        try {
            ps.execute();
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                /** Add to arraylist all appointments found. */
                Integer Appointment_ID = rs.getInt("Appointment_ID");
                String Title = rs.getString("Title");
                String Description = rs.getString("Description");
                String Location = rs.getString("Location");
                String Type = rs.getString("Type");
                /** Convert to LocalDateTime. */
                LocalDateTime Start = rs.getTimestamp("Start").toLocalDateTime();
                LocalDateTime End = rs.getTimestamp("End").toLocalDateTime();
                Integer Customer_ID = rs.getInt("Customer_ID");
                Integer Contact_ID = rs.getInt("Contact_ID");
                Integer User_ID = rs.getInt("User_ID");
                /** Create Appointment object to add to list. */
                Appointment apptData = new Appointment(Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, Contact_ID, User_ID);
                appointmentTimes.add(apptData);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        /** Return List. */
        return appointmentTimes;
    }
    /** Delete existing Appointment.
     * @param Appointment_ID for sql query
     * @throws SQLException for delete query*/
    public static void deleteAppointment(Integer Appointment_ID) throws SQLException {
        String deleteStatement = "DELETE FROM appointments WHERE Appointment_ID = ?;";
        DBQuery.setPreparedStatement(conn, deleteStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        /** Map variables to sql statement string. */
        ps.setInt(1, Appointment_ID);
        try {
            ps.execute();
            /** Show if statement was successfully executed. */
            if(ps.getUpdateCount() > 0) {
                System.out.println(ps.getUpdateCount() + " appointment(s) deleted.");
            } else {
                System.out.println("Appointment not found.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    /** Delete existing Appointment.
     * @param Customer_ID for sql query
     * @throws SQLException for delete query*/
    public static void deleteAppointmentsByCustomerID(Integer Customer_ID) throws SQLException {
        String deleteStatement = "DELETE FROM appointments WHERE Customer_ID = ?;";
        DBQuery.setPreparedStatement(conn, deleteStatement);
        PreparedStatement ps = DBQuery.getPreparedStatement();
        /** Map variables to sql statement string. */
        ps.setInt(1, Customer_ID);

        try {
            ps.execute();
            /** Show if statement was successfully executed. */
            if(ps.getUpdateCount() > 0) {
                System.out.println(ps.getUpdateCount() + " appointment(s) deleted.");
            } else {
                System.out.println("Appointment not found.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /** Toolbar navigation controls. */
    /** Go to main menu page when clicked.
     * @throws IOException load resource file*/
    @FXML
    public void handleMainMenuButton () throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Main.fxml"));
        stage = (Stage) MainMenuButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    /** Go to main customers page when clicked.
     * @throws IOException load resource file*/
    @FXML
    public void handleCustomersButton () throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Customers.fxml"));
        stage = (Stage)CustomersButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /** These methods handle page controls. */
    /** Filter by month.
     * @param event get event from view*/
    @FXML
    public void handleMonthComboBox(ActionEvent event) {
        System.out.println("Filtered by month.");
        /** Get month selection. */
        String month = (String) MonthComboBox.getSelectionModel().getSelectedItem();
        YearMonth selectedMonth = YearMonth.of(YearMonth.now().getYear(), Month.valueOf(month));
        /** Enable FirstLevelDivisionsComboBox when country is selected. */
        if (selectedMonth != null) {
            /** Convert month to LocalDateTime to compare to appointment schedule. */
            LocalDateTime theMonth = LocalDateTime.of(YearMonth.now().getYear(), Month.valueOf(month), 1, 0, 0);
            LocalDateTime addMonth = theMonth.plusMonths(1);
            FilteredList<Appointment> filteredData = new FilteredList<>(allAppointments);
            /** Filter by month using lambda expression. */
            filteredData.setPredicate(row -> {
                /** If appointment is within the selected month, add to list. */
                return row.getStart().isAfter(theMonth.minusDays(1)) && row.getStart().isBefore(addMonth);
            });
            /** Show filtered months. */
            AppointmentsTable.setItems(filteredData);
            /** Clear previous weeks option from other months if any. */
            allWeeksInMonth.clear();
            /** Iterate through days in the month. */
            LocalDate firstOfMonth = selectedMonth.atDay( 1 );
            LocalDate lastOfMonth = selectedMonth.atEndOfMonth();
            LocalDate day = firstOfMonth;
            while(!day.isAfter(lastOfMonth)) {
                /** Show current week in month if at last day of the week (Sunday). */
                if(day.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                    allWeeksInMonth.add("Week of " + day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
                /** Increment day in month. */
                day = day.plusDays(1);
            }
            WeekComboBox.setItems(allWeeksInMonth);
            WeekComboBox.setDisable(false);
        }
    }
    /** Filter by week.
     * @param event get event from view*/
    @FXML
    public void handleWeekComboBox(ActionEvent event) {
        System.out.println("Filtered by week.");
        /** Get month selection. */
        String selectedWeek = (String) WeekComboBox.getSelectionModel().getSelectedItem();
        /** Enable FirstLevelDivisionsComboBox when country is selected. */
        if (selectedWeek != null) {
            /** Convert selectedWeek to LocalDateTime to compare to appointment schedule. */
            String formattedWeek = selectedWeek.substring(8) + " 00:00:00";
            LocalDateTime weekOf = LocalDateTime.parse(formattedWeek, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime addWeek = weekOf.plusWeeks(1);
            FilteredList<Appointment> filteredData = new FilteredList<>(allAppointments);
            /** Filter by week using lambda expression. */
            filteredData.setPredicate(row -> {
                /** If appointment is within the selected week, add to list. */
                return row.getStart().isAfter(weekOf.minusDays(1)) && row.getStart().isBefore(addWeek);
            });
            /** Show filtered months. */
            AppointmentsTable.setItems(filteredData);
        }
    }
    /** Go to new scene with edit appointment form.
     * @param event get event from page
     * @throws IOException load fxml
     * @throws SQLException load appointment data*/
    @FXML
    public void handleEditAppointmentButton (ActionEvent event) throws IOException, SQLException {
        if (appointmentRow != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/EditAppointment.fxml"));
            root = loader.load();
            /** Pass current selected appointment row data into edit scene. */
            EditAppointment editAppointment = loader.getController();
            editAppointment.appointmentData(appointmentRow);
            stage = (Stage)EditAppointmentButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You must select an appointment to edit.");
            alert.setTitle("Select Appointment.");
            alert.showAndWait();
        }
    }
    /** Delete current selected customer row in table.
     * @param event get event from page
     * @throws IOException load fxml
     * @throws SQLException delete query*/
    @FXML
    public void handleDeleteAppointmentButton (ActionEvent event) throws IOException, SQLException {
        if (appointmentRow != null) {
            /** Show warning before deleting. */
            String confirmation = String.format("Are you sure you want to delete Appointment: %s?", appointmentRow.getTitle());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, confirmation);
            alert.setTitle("Confirm Delete");
            alert.showAndWait();
            /** Delete if ok button is pressed. */
            if (alert.getResult().getButtonData().isDefaultButton()) {
                deleteAppointment(appointmentRow.getAppointment_ID());
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
    /** Refresh table with new updates.
     * @throws IOException load fxml*/
    @FXML
    public void handleRefreshTableButton () throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Appointments.fxml"));
        stage = (Stage)RefreshTableButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
