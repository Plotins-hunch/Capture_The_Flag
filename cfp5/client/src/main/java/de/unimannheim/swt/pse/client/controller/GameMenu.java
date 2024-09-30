package de.unimannheim.swt.pse.client.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class GameMenu {

  @FXML
  private Button playGame;

  /**
   * Switches Scene to the game board
   * @author aemsbach
   * @param event the Actionevent
   * @throws IOException if the fxml file is not found
   */
  @FXML
  void switchToGameBoard(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("/fxml/GameBoard.fxml"));
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
  }

}


