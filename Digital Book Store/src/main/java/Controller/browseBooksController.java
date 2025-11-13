package Controller;

import SubClasses.ShoppingCart;
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

public class browseBooksController {

    @FXML
    private Hyperlink dashboardpageLink;

    @FXML
    private Button paymentButton;

    @FXML
    private Button AddtoCartButton;

    @FXML
    private Button updatebooksButton;

    @FXML
    private Button removebookButton;

    @FXML
    private TableView<booksData> bookList;

    @FXML
    private TableColumn<booksData, String> bookTitleColumn;

    @FXML
    private TableColumn<booksData, String> bookAuthorColumn;

    @FXML
    private TableColumn<booksData, Integer> AvailableCopiesColumn;

    @FXML
    private TableColumn<booksData, Double> PriceColumn;

    private ObservableList<booksData> books;

    @FXML
    private TableView<ShoppingCart> shoppingCartTable;

    @FXML
    private TableColumn<ShoppingCart, String> CartBookTitleColumn;

    @FXML
    private TableColumn<ShoppingCart, String> CartAuthorTitleColumn;

    @FXML
    private TableColumn<ShoppingCart, Integer> CartBookQuantityPurchasedColumn;

    @FXML
    private TableColumn<ShoppingCart, Double> CartbookPriceColumn;


    private ObservableList<ShoppingCart> cartItems;


    private int currentId;

    @FXML
    public void initialize() {
        // Set up table columns
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        bookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        AvailableCopiesColumn.setCellValueFactory(new PropertyValueFactory<>("availableCopies"));
        PriceColumn.setCellValueFactory(new PropertyValueFactory<>("bookPrice"));

        //Setting up Shopping Cart column Tables
        CartBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        CartAuthorTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        CartBookQuantityPurchasedColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        CartbookPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        //Data that was used for the browse books table
        // Fetch data from the database and display in the table
        books = FXCollections.observableArrayList(fetchBooksFromDatabase());
        // Sort books by the number of sold copies
        books.sort(Comparator.comparingInt(booksData::getSoldCopies).reversed());
        // Show all books in the table
        bookList.setItems(books);

        //Fetch and Display for Shopping Cart Items
        cartItems = FXCollections.observableArrayList(fetchCartItems());
        shoppingCartTable.setItems(cartItems);
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
                String bookTitle = rs.getString("booktitle");
                String bookAuthor = rs.getString("bookauthor");
                double bookPrice = rs.getDouble("bookprice");
                int availableCopies = rs.getInt("availablecopies");
                int soldCopies = rs.getInt("soldcopies");

                // Add each book as a booksData object to the books list
                booksList.add(new booksData(bookTitle, bookAuthor, bookPrice, availableCopies, soldCopies));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return booksList;
    }

    private ObservableList<ShoppingCart> fetchCartItems() {
        ObservableList<ShoppingCart> cartList = FXCollections.observableArrayList();
        String query = "SELECT booktitle, bookauthor, quantity, price FROM usercart WHERE userid = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:DataTable.db");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, currentId); // Get current user's cart
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cartList.add(new ShoppingCart(
                        rs.getString("bookTitle"),
                        rs.getString("bookAuthor"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartList;
    }


    //This is use to add books into the shopping cart
    @FXML
    private void handleAddToCart() {
        booksData chosenBook = bookList.getSelectionModel().getSelectedItem();

        if (chosenBook != null) {
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Qauntity Selection");
            dialog.setHeaderText("Enter the desired quantity " + chosenBook.getBookTitle());

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(quantity -> {
                int desiredQuantity = Integer.parseInt(quantity);
                // Checking if there are enough quantity of the chosen books available
                int availableCopies = chosenBook.getAvailableCopies();

                //Checking if the desired copy the user wants is more than available copy if it is, it will show a warning message
                if (desiredQuantity > availableCopies) {
                    // Show warning if stock is insufficient
                    Alert StockAlert = new Alert(Alert.AlertType.WARNING);
                    StockAlert.setTitle("Stock Error");
                    StockAlert.setHeaderText("Not enough stock!");
                    StockAlert.setContentText("There are only " + availableCopies + " copies of " + chosenBook.getBookTitle() + " available.");
                    StockAlert.showAndWait();
                } else {
                    //Add the books to cart
                    addtoCart(chosenBook, desiredQuantity);

                    //This notifies the user when book have been successfully added to cart
                    Alert SuccessMessage = new Alert(Alert.AlertType.INFORMATION);
                    SuccessMessage.setTitle("Book Successfully Added");
                    SuccessMessage.setHeaderText("Book has been added to cart!");
                    SuccessMessage.setContentText(quantity + " copies of " + chosenBook.getBookTitle() + " has been added to the cart!");
                    SuccessMessage.showAndWait();

                    //after everything have been process it will refresh the cart
                    refreshingCart();
                }
            });
        } else {
            //warning will be created if there is no books selected
            Alert SelectAlert = new Alert(Alert.AlertType.WARNING);
            SelectAlert.setTitle("No Books Selected");
            SelectAlert.setHeaderText("Please select the book to add to cart.");
            SelectAlert.showAndWait();
        }
    }

    private void refreshingCart() {
        cartItems = FXCollections.observableArrayList(fetchCartItems());
        shoppingCartTable.setItems(cartItems);
    }

    // Method to add books to user's shopping cart
    private void addtoCart(booksData chosenBook, int desiredQuantity) {
        String checkingCart = "SELECT quantity FROM usercart WHERE booktitle = ? AND userid = ?";
        String updatingCart = "UPDATE usercart SET quantity = ? WHERE booktitle = ? AND userid = ?";
        String InsertingCart = "INSERT INTO usercart (booktitle, bookauthor, quantity, price, userid) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:DataTable.db")) {
            try (PreparedStatement checkCartStatement = connection.prepareStatement(checkingCart)) {
                checkCartStatement.setString(1, chosenBook.getBookTitle());
                checkCartStatement.setInt(2, currentId);
                ResultSet rs = checkCartStatement.executeQuery();


                if (rs.next()) {
                    int newQuantity = rs.getInt("quantity") + desiredQuantity;
                    try (PreparedStatement updatingStatment = connection.prepareStatement(updatingCart)) {
                        updatingStatment.setInt(1, newQuantity);
                        updatingStatment.setString(2, chosenBook.getBookTitle());
                        updatingStatment.setInt(3, currentId);
                        updatingStatment.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertingStatement = connection.prepareStatement(InsertingCart)) {
                        insertingStatement.setString(1, chosenBook.getBookTitle());
                        insertingStatement.setString(2, chosenBook.getBookAuthor());
                        insertingStatement.setInt(3, desiredQuantity);
                        insertingStatement.setDouble(4, chosenBook.getBookPrice());
                        insertingStatement.setInt(5, currentId);
                        insertingStatement.executeUpdate();
                    }
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @FXML
    private void handleRemoveBook() {
        ShoppingCart selectedItem = shoppingCartTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            // Prompt the user to enter the amount they want to remove
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Quantity Selection");
            dialog.setHeaderText("Enter the quantity to remove from " + selectedItem.getBookTitle());

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(quantity -> {
                int removeQuantity = Integer.parseInt(quantity);
                    // Remove or update the item in the cart
                    removeItem(selectedItem, removeQuantity);
                    refreshingCart(); // Refresh the cart after removal
            });
        }
    }

    //Use to remove items from the users shopping cart
    private void removeItem(ShoppingCart selectedItem, int removeQuantity) {
        String updateCartQuery = "UPDATE usercart SET quantity = ? WHERE booktitle = ? AND userid = ?";
        String deleteCartItemQuery = "DELETE FROM usercart WHERE booktitle = ? AND userid = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:DataTable.db")) {
            if (selectedItem.getQuantity() - removeQuantity > 0) {
                // If the quantity after removal is still greater than 0, update the quantity
                try (PreparedStatement updateStmt = conn.prepareStatement(updateCartQuery)) {
                    int newQuantity = selectedItem.getQuantity() - removeQuantity;
                    updateStmt.setInt(1, newQuantity);
                    updateStmt.setString(2, selectedItem.getBookTitle());
                    updateStmt.setInt(3, currentId);
                    updateStmt.executeUpdate();
                    System.out.println("Updated cart with new quantity: " + newQuantity);
                }
            } else {
                // If the quantity after removal is zero or less, delete the item from the cart
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteCartItemQuery)) {
                    deleteStmt.setString(1, selectedItem.getBookTitle());
                    deleteStmt.setInt(2, currentId);
                    deleteStmt.executeUpdate();
                    System.out.println("Removed book from cart: " + selectedItem.getBookTitle());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


        //This is use to go back to the Dashboard View
        @FXML
        private void handledashboardHyperLink() {
            ChangeScene("dashboard.fxml", dashboardpageLink);
        }

        //This is to go to the checkout page
        @FXML
        private void handleCheckout() {
            ChangeScene("checkout.fxml", paymentButton);
        }


        //Overall use method for change from one page to another
        private void ChangeScene(String fxml, Control control) {
            try {
                Parent Root = FXMLLoader.load(getClass().getResource(fxml));
                Stage Stage = (Stage) control.getScene().getWindow();

                // Set the scene to the login page
                Stage.setScene(new Scene(Root));
                Stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
