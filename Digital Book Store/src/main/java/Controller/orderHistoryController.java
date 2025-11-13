package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.IOException;

public class orderHistoryController {

    @FXML
    private Hyperlink dashboardpageLink;

    @FXML
    private Button exporthistoryButton;

    @FXML
    private TableView orderHistoryTable;

    @FXML
    private void handledashboardHyperLink() {
        try{
            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Get the current window (stage)
            Stage dashboardStage = (Stage) dashboardpageLink.getScene().getWindow();

            // Set the scene to the login page
            dashboardStage.setScene(new Scene(dashboardRoot));
            dashboardStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
