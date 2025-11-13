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

public class editprofileController {
    @FXML
    private Hyperlink dashboardLink;
    @FXML
    private Button updatechangeButton;
    @FXML
    private TextField firstnameText;
    @FXML
    private TextField lastnameText;
    @FXML
    private PasswordField passwordText;
    @FXML
    private PasswordField confirmPasswordText;
    @FXML
    private Label errorLabel;

    @FXML
    private void handledashboardHyperLink() {
        try {
            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent loginRoot = loader.load();

            // Get the current window (stage)
            Stage loginStage = (Stage) dashboardLink.getScene().getWindow();

            // Set the scene to the login page
            loginStage.setScene(new Scene(loginRoot));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //This is use to update the different changes in the users account
    @FXML
    private void handleUpdateChange(){
        String firstname = firstnameText.getText();
        String lastname = lastnameText.getText();
        String password = passwordText.getText();
        String confirmPassword = confirmPasswordText.getText();

        //Make sure that the fields are properly filled before proceeding
        if(firstname.isEmpty() || lastname.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            errorLabel.setText("To Proceed file up all the fields");
            return;
        }

        //This makes sure that the password are the same before proceeding
        if(!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords must match");
            return;
        }

        //Understand what is happening for updating
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:DataTable.db")) {
            String updateQuery = "UPDATE users SET firstname = ?, lastname = ?, password = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);

            // Bind the parameters
            updateStatement.setString(1, firstname);
            updateStatement.setString(2, lastname);
            updateStatement.setString(3, password);// Assuming the username of the logged-in user is stored

            // Execute the update
            int rowsAffected = updateStatement.executeUpdate();

            // Check if the update was successful
            if (rowsAffected > 0) {
                errorLabel.setText("Profile updated successfully!");
            } else {
                errorLabel.setText("Failed to update profile. Please try again.");
            }

        } catch (SQLException e) {
            errorLabel.setText("Error updating profile.");
            e.printStackTrace();
        }
    }
}
