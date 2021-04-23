package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Appointment;
import models.User;
import utils.UserHolder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
/** This class is the controller for Login.fxml */
public class Login extends Users implements Initializable {
    /** Login Form fields and labels. */
    @FXML
    private Label LoginUsernameLabel;
    @FXML
    private TextField LoginUsername;
    @FXML
    private Label LoginPasswordLabel;
    @FXML
    private PasswordField LoginPassword;
    @FXML
    private Button LoginButton;
    @FXML
    private Label LocationLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /** Get user's current time zone and display in a label. */
        ZoneId zone = ZoneId.systemDefault() ;
        /** Show current user's time zone. */
        LocationLabel.setText(zone.toString());
        try {
            /** Set resource bundle to user's default system language. */
            resourceBundle = ResourceBundle.getBundle("main/Nat", Locale.getDefault());
            /** Translate to resource language. */
            LoginUsernameLabel.setText(resourceBundle.getString("username"));
            LoginUsername.setPromptText(resourceBundle.getString("username"));
            LoginPasswordLabel.setText(resourceBundle.getString("password"));
            LoginPassword.setPromptText(resourceBundle.getString("password"));
            LoginButton.setText(resourceBundle.getString("login"));
        } catch (MissingResourceException e) {
            System.out.println("Missing resource");
        }
    }
    /** Handles login button.
     * @param event get event from page
     * @throws SQLException get user from db
     * @throws IOException load fxml*/
    @FXML
    private void handleLoginButton (ActionEvent event) throws SQLException, IOException {
        /** Get login details from text fields. */
        String usernameInput = LoginUsername.getText();
        String passwordInput = LoginPassword.getText();
        /** User_ID will not be null if login is successful. */
        if (loginUser(usernameInput, passwordInput).getUserId() != null) {
            /** Initialize new User object with current user details. */
            User user = loginUser(usernameInput, passwordInput);
            /** Save login activity to access log */
            accessLog("Login Successful: " + user.getUsername());
            /** Notify user if there are any appointments within 15 mins of logging on. */
            /** Get current time. */
            LocalDateTime currentTime = LocalDateTime.now();
            List<Appointment> appointmentTimes;
            try {
                /** Get appointment times from database and iterate through. */
                appointmentTimes = new ArrayList<Appointment>(Appointments.getAppointments());
                /** Count number of appointments within 15 mins. */
                int counter = 0;
                String apptTitle = "";
                /** Store separately for display purposes. */
                int apptID = 0;
                LocalDate apptDate = LocalDate.now();
                LocalTime apptTime = LocalTime.now();
                /** Iterate through appointments and find any appointments within 15 mins. */
                for (Appointment appt : appointmentTimes) {
                    if (appt.getUser_ID() == user.getUserId() && appt.getStart().isAfter(currentTime.minusMinutes(15)) && appt.getStart().isBefore(currentTime.plusMinutes(15))) {
                        apptID = appt.getAppointment_ID();
                        apptTitle = appt.getTitle();
                        apptDate = appt.getStart().toLocalDate();
                        apptTime = appt.getStart().toLocalTime();
                        counter++;
                    }
                }
                /** Alert user if they have an appointment within 15 mins of loggin on. */
                if (counter > 0) {
                    /** Display appointment details in Alert message. */
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Appointments");
                    alert.setHeaderText("Appointment " + apptID + ": " + apptTitle+ " is within 15 mins.");
                    alert.setContentText(String.format("Date: %tD, Time: %tT", apptDate, apptTime));
                    Optional<ButtonType> result = alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Appointments");
                    alert.setHeaderText("0 Appointments");
                    alert.setContentText("No upcoming appointments.");
                    Optional<ButtonType> result = alert.showAndWait();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            /** Calls main page after successful login. */
            Parent root = FXMLLoader.load(getClass().getResource("../views/Main.fxml"));
            /** Send data to UserHolder singleton class for all classes to access. */
            UserHolder holder = UserHolder.getInstance();
            holder.setUser(user);
            /** Go to next screen. */
            Stage stage = (Stage) LoginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            /** Set error messages to default language as well. */
            ResourceBundle resourceBundle = ResourceBundle.getBundle("main/Nat", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(resourceBundle.getString("loginFailed"));
            if (usernameInput.length() < 1 || passwordInput.length() < 1) {
                /** Display if fields are empty. */
                alert.setContentText(resourceBundle.getString("empty"));
                /** Track login activity as empty field. */
                accessLog("Login failed: Empty field(s).");
            } else {
                /** Display if login is incorrect. */
                alert.setContentText(resourceBundle.getString("incorrect"));
                /** Track login activity as wrong credentials. */
                accessLog("Login failed: Invalid credentials.");
            }
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    /** Tracks login activity of user in a text file.
     * @param user write user into login_activity file*/
    public void accessLog(String user) {
        /** Get current date and time. */
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        try {
            /** Write activity with timestamp to a file. */
            String fileName = "login_activity";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(timestamp.toString() + ", " + user + "\n");
            System.out.println("New login activity recorded.");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
