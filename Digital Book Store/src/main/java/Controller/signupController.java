package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.DriverManager;
// import java.sql.*;
import java.sql.Connection;
// import java.sql.DriverManager;
import java.sql.PreparedStatement;
// import java.sql.ResultSet;
import java.sql.SQLException;

public class signupController {
    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField firstnameTextField;

    @FXML
    private TextField lastnameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmpasswordField;

    @FXML
    private Hyperlink loginpageLink;

    @FXML
    private Button SignupButton;

    @FXML
    private Label alertLabel;

    @FXML
    private void handlebacktologinpageHyperlink() {
        try{
            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent loginRoot = loader.load();

            // Get the current window (stage)
            Stage loginStage = (Stage) loginpageLink.getScene().getWindow();

            // Set the scene to the login page
            loginStage.setScene(new Scene(loginRoot));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignups() {
        String username = usernameTextField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmpasswordField.getText();
        String firstname = firstnameTextField.getText();
        String lastname = lastnameTextField.getText();
        //Allow users to register their details into the data server
        //If not all the credentials are there form an error message
        //Example, Please fill in your name
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || firstname.isEmpty() || lastname.isEmpty()) {
            alertLabel.setText("Please fill all the fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            alertLabel.setText("Passwords do not match");
            return;
        }


        //Create the registration part for the Database for the accounts
        try {
            // Establish a database connection (using SQLite as an example)
            Connection connection = DriverManager.getConnection("jdbc:sqlite:DataTable.db");

            // Use ? placeholders in the SQL query for prepared statement
            String insertQuery = "INSERT INTO users (username, firstname, lastname, password) VALUES (?, ?, ?, ?)";

            // Prepare the statement
            PreparedStatement insertuserStatement = connection.prepareStatement(insertQuery);

            // Set the values for the placeholders (1st ?, 2nd ?, 3rd ?, 4th ?)
            insertuserStatement.setString(1, username);  // 1st placeholder for username
            insertuserStatement.setString(2, firstname); // 2nd placeholder for firstname
            insertuserStatement.setString(3, lastname);  // 3rd placeholder for lastname
            insertuserStatement.setString(4, password);  // 4th placeholder for password

            // Execute the insertion
            int rowsAffected = insertuserStatement.executeUpdate();

            // Registration successful, show success message
            if (rowsAffected > 0) {
                alertLabel.setText("Registration successful!");
                clearFormData();  // Clear form fields
            } else {
                alertLabel.setText("Registration failed, please try again.");
            }

            // Close the statement and connection
            insertuserStatement.close();
            connection.close();
        } catch (SQLException e) {
            // Handle any SQL exceptions
            alertLabel.setText("Error: the user cannot be registered.");
            e.printStackTrace();
        }

    }

    // Helper function to clear form fields after successful registration
    private void clearFormData(){
        usernameTextField.clear();
        firstnameTextField.clear();
        lastnameTextField.clear();
        passwordField.clear();
        confirmpasswordField.clear();

    }
}
