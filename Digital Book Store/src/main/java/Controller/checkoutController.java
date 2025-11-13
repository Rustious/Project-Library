package Controller;

import SubClasses.ShoppingCart;
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
// import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;

public class checkoutController {

    @FXML
    private Hyperlink backtoshoppingcartLink;

    @FXML
    private Button confirmpaymentButton;

    @FXML
    private TextField cardNoField;

    @FXML
    private TextField expiryDateField;

    @FXML
    private TextField CVVField;

    @FXML
    private Label warningLabel;

    @FXML
    private TableView<ShoppingCart> CheckoutTable;

    @FXML
    private TableColumn<ShoppingCart, String> CheckoutBookTitleColumn;

    @FXML
    private TableColumn<ShoppingCart, String> CheckoutAuthorTitleColumn;

    @FXML
    private TableColumn<ShoppingCart, Integer> CheckoutBookQuantityColumn;

    @FXML
    private TableColumn<ShoppingCart, Double> CheckoutPricePerBookColumn;

    private ObservableList<ShoppingCart> CheckoutTableItems;

    private int currentId;



    @FXML
    public void initialize() {
        //Setting up Shopping Cart column Tables
        CheckoutBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        CheckoutAuthorTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookAuthor"));
        CheckoutBookQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        CheckoutPricePerBookColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        CheckoutTableItems = FXCollections.observableArrayList(fetchCartItems());
        CheckoutTable.setItems(CheckoutTableItems);
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
                        rs.getString("booktitle"),
                        rs.getString("bookauthor"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartList;
    }


    @FXML
    private void handleBackToShoppingCartLink() {
        try{
            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("browseBooks.fxml"));
            Parent BrowseBooksRoot = loader.load();

            // Get the current window (stage)
            Stage BrowseBooksStage = (Stage) backtoshoppingcartLink.getScene().getWindow();

            // Set the scene to the login page
            BrowseBooksStage.setScene(new Scene(BrowseBooksRoot));
            BrowseBooksStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleConfirmpaymentButton() {
        //make sure all field are filled
        if (validateFields()) {
            // Generate a unique order number
            String orderNumber = generateOrderNumber();

            //This notifies that payment has been confirmed
            Alert informationAlert = new Alert(Alert.AlertType.INFORMATION);
            informationAlert.setTitle("Payment Confirmation");
            informationAlert.setHeaderText("Thank you for confirming your order");
            informationAlert.setContentText("Your order number is " + orderNumber);
            informationAlert.showAndWait();

        }

    }

    private String generateOrderNumber() {
        Random OrderNumber = new Random();
        return "ORD No. " + (OrderNumber.nextInt(100));
    }

    //Make it so that it cannot allow alphabets as well
    private boolean validateFields() {
        //Validate the Card Number (16 Digits)
        String cardNumber = cardNoField.getText().trim();
        // \\d is a special format where you can only key in digits exactly 16 digits
        if (cardNumber.isEmpty() || !cardNumber.matches("\\d{16}")) {
            warningLabel.setText("Invalid Card Number, must be 16 digits");
            return false;
        }


        //Validate the CVV number
        String CVV = CVVField.getText().trim();
        // \\d3 only allows 3 digits
        if (CVV.isEmpty() || !CVV.matches("\\d{3}")) {
            warningLabel.setText("Invalid CVV, must be 3 digits");
            return false;
        }

        //Validate expiryDate
        String expiryDate = expiryDateField.getText().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM/yy");

        try {
            YearMonth cardexpiredDate = YearMonth.parse(expiryDate, formatter);

            if (cardexpiredDate.isBefore(YearMonth.now())) {
                warningLabel.setText("Invalid Expiry Date, must be in the future");
                return false;
            }
        } catch (DateTimeParseException e) {
            warningLabel.setText("");
            return true;
        }
        return true;
    }
}
