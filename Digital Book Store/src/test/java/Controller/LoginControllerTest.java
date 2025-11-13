package Controller;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Mock
    private TextField usernameField;

    @Mock
    private PasswordField passwordField;

    @Mock
    private Label alertLabel;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize Mocks
    }

    @Test
    void testAdminLoginSuccess() throws Exception {
        // Setup admin credentials
        when(usernameField.getText()).thenReturn("admin");
        when(passwordField.getText()).thenReturn("reading_admin");

        // Call login method
        loginController.handlelogin();

        // Ensure no error message is displayed
        assertNull(alertLabel.getText());
        assertFalse(alertLabel.isVisible(), "Admin login should succeed without error");
    }

    @Test
    void testUserLoginSuccess() throws Exception {
        // Setup user credentials
        when(usernameField.getText()).thenReturn("user123");
        when(passwordField.getText()).thenReturn("password123");

        // Mock successful query from the database
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Simulate successful login

        // Call login method
        loginController.handlelogin();

        // Ensure no error message is displayed
        assertNull(alertLabel.getText());
        assertFalse(alertLabel.isVisible(), "User login should succeed without error");
    }

    @Test
    void testInvalidLogin() throws Exception {
        // Setup invalid credentials
        when(usernameField.getText()).thenReturn("invaliduser");
        when(passwordField.getText()).thenReturn("invalidpassword");

        // Mock failed query from the database
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Simulate failed login

        // Call login method
        loginController.handlelogin();

        // Ensure error message is displayed
        assertTrue(alertLabel.isVisible(), "Alert label should be visible");
        assertEquals("Username or Password is incorrect", alertLabel.getText());
    }

    @Test
    void testEmptyUsernameOrPassword() {
        // Setup empty username or password
        when(usernameField.getText()).thenReturn("");
        when(passwordField.getText()).thenReturn("somepassword");

        // Call login method
        loginController.handlelogin();

        // Ensure error message for empty fields is displayed
        assertTrue(alertLabel.isVisible(), "Alert label should be visible for empty fields");
        assertEquals("Username or Password are empty", alertLabel.getText());
    }

    @Test
    void testDatabaseConnectionFailure() throws Exception {
        // Setup valid user credentials
        when(usernameField.getText()).thenReturn("user123");
        when(passwordField.getText()).thenReturn("password123");

        // Simulate SQL exception when trying to connect to database
        when(mockConnection.prepareStatement(anyString())).thenThrow(SQLException.class);

        // Call login method and expect exception handling
        assertThrows(RuntimeException.class, () -> loginController.handlelogin());
    }
}
