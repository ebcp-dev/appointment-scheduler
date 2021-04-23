package main;

import db.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;
/** This is the main class of application. */
public class Main extends Application {

    @Override
    public void init() throws Exception {
        System.out.println("Starting application");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        /** Show login screen first. */
        Parent root = FXMLLoader.load(getClass().getResource("../views/Login.fxml"));
        /** Set app window's title. */
        primaryStage.setTitle("WGU Scheduler");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Closing application.");
    }

    public static void main(String[] args) throws SQLException {
        launch(args);
        DBConnection.closeConnection();
    }
}
