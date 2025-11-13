package Databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BooksStorage {

    public static void main(String[] args) {
        //Path to the DataTable.db
        String url = "jdbc:sqlite:C:\\Users\\deont\\OneDrive\\Documents\\GitHub\\assignment2-Snowghosts\\Assignment2\\DataTable.db";
        //Inserting the different types of data into the books Table
        String insertSQL = "INSERT INTO books (booktitle, bookauthor, soldcopies, availablecopies, bookprice) VALUES (?, ?, ?, ?, ?)";

        //The data of the books stored in the Table
        String[][] books = {
                {"Absolute Java", "Savitch", "142", "10", "50"},
                {"JAVA: How to Program", "Deitel and Deitel", "475", "100", "70"},
                {"Computing Concepts with JAVA 8 Essentials", "Horstman", "60", "500", "89"},
                {"Java Software Solutions", "Lewis and Loftus", "12", "500", "99"},
                {"Java Program Design", "Cohoon and Davidson", "86", "2", "29"},
                {"Clean Code", "Robert Martin", "300", "100", "45"},
                {"Gray Hat C#", "Brandon Perry", "178", "300", "68"},
                {"Python Basics", "David Amos", "79", "1000", "49"},
                {"Bayesian Statistics The Fun Way", "Will Kurt", "155", "600", "42"}
        };

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            // Disable auto-commit for batch insert
            conn.setAutoCommit(false);

            // Loop over the array and insert each book into the database
            for (String[] book : books) {
                pstmt.setString(1, book[0]);  // title
                pstmt.setString(2, book[1]);  // author
                pstmt.setInt(3, Integer.parseInt(book[2]));  // soldCopies
                pstmt.setInt(4, Integer.parseInt(book[3]));  // physicalCopies
                pstmt.setDouble(5, Double.parseDouble(book[4]));  // price
                pstmt.addBatch();  // Add to batch
            }

            // Execute batch insertion
            pstmt.executeBatch();

            // Commit the changes
            conn.commit();

            System.out.println("Book data inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
