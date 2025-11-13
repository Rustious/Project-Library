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
import java.util.Objects;


public class dashboardController {

    @FXML
    private Button logoutButton;

    @FXML
    private Button browsebooksButton;

    @FXML
    private Button vieworderButton;

    @FXML
    private Button editprofileButton;

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<booksData> popularBooksList;

    @FXML
    private TableColumn<booksData,String> bookTitleColumn;

    @FXML
    private TableColumn<booksData,String> bookAuthorColumn;

    @FXML
    private TableColumn<booksData,Integer> amountSoldColumn;

    private ObservableList<booksData> Books;

    private String logInUsername;

    public void setUsername(String logInUsername) {
        this.logInUsername = logInUsername;
        loadUserDetails();
    }

    private void loadUserDetails() {
        String query = "SELECT firstName, lastName FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:DataTable.db");
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setString(1, logInUsername);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    String firstName = rs.getString("firstname");
                    String lastName = rs.getString("lastname");

                    // Set welcome message with first and last name
                    welcomeLabel.setText("Welcome, " + firstName + " " + lastName + "!");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        // Set up table columns
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        bookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        amountSoldColumn.setCellValueFactory(new PropertyValueFactory<>("soldCopies"));

        // Fetch data from the database and display in the table
        Books = FXCollections.observableArrayList(fetchBooksFromDatabase());

        // Sort books by the number of sold copies
        Books.sort(Comparator.comparingInt(booksData::getSoldCopies).reversed());

        // Show the top 5 books in the table
        popularBooksList.setItems(FXCollections.observableArrayList(Books.subList(0, Math.min(5, Books.size()))));

    }
    // Method to fetch books from the database
    private ObservableList<booksData> fetchBooksFromDatabase() {
        return getBooksData();
    }

    static ObservableList<booksData> getBooksData() {
        ObservableList<booksData> popularBooksList = FXCollections.observableArrayList();
        String url = "jdbc:sqlite:DataTable.db";  // Update with your actual path
        String query = "SELECT booktitle, bookauthor, bookprice, availablecopies, soldcopies FROM books";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Loop through the result set and add each book to the list
            while (rs.next()) {
                String bookTitle = rs.getString("booktitle");
                String bookAuthor = rs.getString("bookauthor");
                double bookPrice = rs.getDouble("bookprice");
                int availableCopies = rs.getInt("availablecopies");
                int soldCopies = rs.getInt("soldcopies");

                // Add each book as a booksData object to the books list
                popularBooksList.add(new booksData(bookTitle, bookAuthor, bookPrice, availableCopies, soldCopies));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return popularBooksList;
    }


    // Logout button handler
    @FXML
    private void handleLogout() {
        ChangeScene("login.fxml", logoutButton);
    }

    @FXML
    private void handleBrowseBooks() {
        ChangeScene("browseBooks.fxml",browsebooksButton);
    }

    @FXML
    private void handleViewOrders() {
        ChangeScene("orderHistory.fxml", vieworderButton);
    }

    @FXML
    private void handleEditProfile() {
        ChangeScene("userprofile.fxml", editprofileButton);
    }

    private void ChangeScene(String fxml, Control control) {
        try {
            Parent Root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
            Stage Stage = (Stage) control.getScene().getWindow();

            // Set the scene to the login page
            Stage.setScene(new Scene(Root));
            Stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
