package de.unimannheim.swt.pse.client.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProfileController {

  /**
   * This method is used to switch to the landing page when the back button is clicked
   * @author rkonradt
   * @param e ActionEvent which is the event of clicking the back button
   * @throws IOException if the FXML file is not found
   */
  @FXML
  public void switchToLandingPage(ActionEvent e) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/LandingPage.fxml"));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

}
