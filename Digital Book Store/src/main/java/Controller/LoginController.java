package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private Button signupButton;

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label alertLabel;

    private final String ADMIN_USERNAME = "admin";
    private final String ADMIN_PASSWORD = "reading_admin";




    @FXML
    private void handleSignup() {
        try {
            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("signupForm.fxml"));
            Parent signupRoot = loader.load();

            // Get the current window (stage)
            Stage signupStage = (Stage) signupButton.getScene().getWindow();

            // Set the scene to the login page
            signupStage.setScene(new Scene(signupRoot));
            signupStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handlelogin() {
        try {
            //This is for the Username and Password fields in Login Page
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                alertLabel.setText("Username or Password are empty");
                alertLabel.setVisible(true);
            }

            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                // Load the Admin dashboard page
                FXMLLoader loader = new FXMLLoader(getClass().getResource("admindashboard.fxml"));
                Parent adminRoot = loader.load();

                // Get the current window (stage)
                Stage loginStage = (Stage) loginButton.getScene().getWindow();

                // Set the scene to the login page
                loginStage.setScene(new Scene(adminRoot));
                loginStage.show();
            } else {
                // Show warning if credentials are incorrect
                alertLabel.setText("Your credentials are incorrect, please try again.");
                alertLabel.setVisible(true);
            }

            String query = "SELECT * FROM users WHERE username = ? AND password = ?";

            try(Connection conn = DriverManager.getConnection("jdbc:sqlite:DataTable.db");
            PreparedStatement preparedStatement = conn.prepareStatement(query)) {

                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
                        Parent dashboardRoot = loader.load();

                        // Get controller and pass the username to it
                        dashboardController dashboardCtrl = loader.getController();
                        dashboardCtrl.setUsername(username);

                        // Get the current window (stage)
                        Stage loginStage = (Stage) loginButton.getScene().getWindow();

                        // Set the scene to the login page
                        loginStage.setScene(new Scene(dashboardRoot));
                        loginStage.show();
                    } else {
                        alertLabel.setText("Username or Password is incorrect");
                        alertLabel.setVisible(true);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}