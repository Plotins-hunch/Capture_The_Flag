package de.unimannheim.swt.pse.client.controller;

import de.unimannheim.swt.pse.client.HelperMethods;
import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
/**
 * Controller for the Surrender Dialog that allows user to surrender the game
 */
public class SurrenderDialogController {

  public ImageView yesBtn;
  public ImageView noBtn;
  private GameBoard gameBoardController;

  public AnchorPane basePane;
  /**
   * Initializes the controller by setting the background image of the dialog
   * @author aemsbach
   */
  @FXML
  public void initialize() {
    this.basePane.sceneProperty().addListener((observable, oldValue, newValue) -> {
      if (oldValue == null && newValue != null) {
        newValue.getRoot().setStyle("-fx-background-color: linear-gradient(to bottom, burlywood, lightgoldenrodyellow);" );
      }
    });

  }
  /**
   * Sends a give up request to the server and closes the dialog
   * @author aemsbach
   * @param mouseEvent the mouse event that triggered the action
   * @throws IOException if the server cannot be reached
   */
  public void surrender(MouseEvent mouseEvent) throws IOException {
    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    stage.close();
    gameBoardController.sendGiveUpRequest();
  }

  /**
   * Changes the cursor to a hand when the mouse enters the surrender button
   * @author aemsbach
   * @param mouseEvent the mouse event that triggered the action
   */
  public void mouseEntered(MouseEvent mouseEvent) {
    HelperMethods.mouseEntered(mouseEvent);
    Image backBtn = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/buttonSmall_white.png")).toString());
    Label label = (Label) mouseEvent.getSource();
    if(label.getText().equals("No"))
      noBtn.setImage(backBtn);
    else
      yesBtn.setImage(backBtn);

  }

  /**
   * Changes the cursor back to the default when the mouse leaves the surrender button
   * @author aemsbach
   * @param mouseEvent the mouse event that triggered the action
   */
  public void mouseExited(MouseEvent mouseEvent) {
    HelperMethods.mouseExited(mouseEvent);
    Image backBtn = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
    Label label = (Label) mouseEvent.getSource();
    if(label.getText().equals("No"))
      noBtn.setImage(backBtn);
    else
      yesBtn.setImage(backBtn);
  }
  /**
   * Closes the dialog without sending a give up request to the server
   * @author aemsbach
   * @param mouseEvent the mouse event that triggered the action
   */
  public void noSurrender(MouseEvent mouseEvent) {
    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    stage.close();
  }

  public void setGameBoardController(GameBoard gameBoardController) {
    this.gameBoardController = gameBoardController;
  }
}
