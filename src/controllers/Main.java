package controllers;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
/** This class is the controller for Main.fxml */
public class Main {
    @FXML
    private Button CustomerMenuButton;
    @FXML
    private Button AppointmentsMenuButton;
    @FXML
    private Button ReportsMenuButton;
    @FXML
    private Button LogOutButton;
    /** JavaFX */
    Parent root;
    Stage stage;

    /** Go to Customers screen customer button is clicked.
     * @param event get event from page
     * @throws IOException load fxml*/
    @FXML
    private void handleCustomerMenuButton(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Customers.fxml"));
        stage = (Stage)CustomerMenuButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    /** Go to Appointments screen appointments button is clicked.
      @param event get event from page
     * @throws IOException load fxml*/
    @FXML
    private void handleAppointmentsMenuButton(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Appointments.fxml"));
        stage = (Stage)AppointmentsMenuButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    /** Go to Reports screen reports button is clicked.
      @param event get event from page
     * @throws IOException load fxml*/
    @FXML
    private void handleReportsMenuButton(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("../views/Reports.fxml"));
        stage = (Stage)ReportsMenuButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    /** Exit app when logout button is clicked.
     * @param event get event from page*/
    @FXML
    private void logOutButtonHandler(ActionEvent event) {
        /** Show logout confirmation. */
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Exit App");
        alert.setContentText("Are you sure you want to close?");
        Optional<ButtonType> result = alert.showAndWait();
        /** Logout if ok button is clicked. */
        if (result.get() == ButtonType.OK) {
            DBConnection.closeConnection();
            System.out.println("App closed.");
            System.exit(0);
        }
        else{
            System.out.println("Exit canceled.");
        }
    }
}
