package Controller;

import SubClasses.booksData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Comparator;
import java.util.Optional;

public class admindashboardController {

    @FXML
    private Button logoutButton;

    @FXML
    private Button updatebookButton;

    @FXML
    private TableView<booksData> AdminBooksList;

    @FXML
    private TableColumn<booksData, String> AdminbookTitleColumn;

    @FXML
    private TableColumn<booksData, String> AdminbookAuthorColumn;

    @FXML
    private TableColumn<booksData, Integer> AdminAvailableCopiesColumn;

    @FXML
    private TableColumn<booksData, Double> AdminPriceColumn;

    @FXML
    private TableColumn<booksData, Double> AdminSoldColumn;

    private ObservableList<booksData> Adminbooks;

    @FXML
    public void initialize() {
        //Listing the different items from the books Database Table to the tableview
        AdminbookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        AdminbookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        AdminAvailableCopiesColumn.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        AdminPriceColumn.setCellValueFactory(new PropertyValueFactory<>("bookPrice"));
        AdminSoldColumn.setCellValueFactory(new PropertyValueFactory<>("soldCopies"));

        // Fetch data from the database and display in the table
        Adminbooks = FXCollections.observableArrayList(fetchBooksFromDatabase());
        // Sort books by the number of sold copies
        Adminbooks.sort(Comparator.comparingInt(booksData::getSoldCopies).reversed());
        // Show all books in the table
        AdminBooksList.setItems(Adminbooks);
    }

    static ObservableList<booksData> fetchBooksFromDatabase() {
        ObservableList<booksData> booksList = FXCollections.observableArrayList();
        String url = "jdbc:sqlite:DataTable.db";  // Update with your actual path
        String query = "SELECT booktitle, bookauthor, bookprice, availablecopies, soldcopies FROM books";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Loop through the result set and add each book to the list
            while (rs.next()) {
                booksList.add(new booksData(
                        rs.getString("booktitle"),
                        rs.getString("bookauthor"),
                        rs.getDouble("bookprice"),
                        rs.getInt("availablecopies"),
                        rs.getInt("soldcopies")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return booksList;
    }

    @FXML
    private void handleupdateBooks() {
        booksData selectedBook = AdminBooksList.getSelectionModel().getSelectedItem();

        if (selectedBook != null) {
            // Create a dialog to get the new quantity
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selectedBook.getAvailableCopies()));
            dialog.setTitle("Update Book Quantity");
            dialog.setHeaderText("Update Quantity for: " + selectedBook.getBookTitle());
            dialog.setContentText("Enter new quantity:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newQuantityStr -> {
                try {
                    int newQuantity = Integer.parseInt(newQuantityStr);
                    // Update the quantity in the database
                    updateBookQuantityInDatabase(selectedBook.getBookTitle(), newQuantity);
                    // Refresh the table data
                    refreshBookTable();
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter a valid number for the quantity.");
                }
            });
        } else {
            showAlert("No Book Selected", "Please select a book to update.");
        }
    }

    //Use to update the books database
    private void updateBookQuantityInDatabase(String bookTitle, int newQuantity) {
        String updateQuery = "UPDATE books SET availablecopies = ? WHERE booktitle = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:DataTable.db");
             PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
            updateStatement.setInt(1, newQuantity);
            updateStatement.setString(2, bookTitle);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update the book quantity.");
        }
    }


    //method is use for refresh the arraylist table to the updated version of it in the Adminbooks table
    private void refreshBookTable() {
        Adminbooks = FXCollections.observableArrayList(fetchBooksFromDatabase());
        AdminBooksList.setItems(Adminbooks);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout() {
        try {
            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent loginRoot = loader.load();

            // This will get the current window stage which is login
            Stage loginStage = (Stage) logoutButton.getScene().getWindow();

            // Set the scene to the login page
            loginStage.setScene(new Scene(loginRoot));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
