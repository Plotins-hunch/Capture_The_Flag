package de.unimannheim.swt.pse.client.controller;

import de.unimannheim.swt.pse.client.HelperMethods;
import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Controller for the Login Response Dialog that shows whether a login attempt was successful or not
 */
public class LoginResponseController {

  @FXML
  private AnchorPane backBtn;

  @FXML
  private Label title;

  private LoginController loginController;
  private RegisterController registerController;

  /**
   * Initializes the controller by setting the background image of the dialog
   * @author aemsbach
   */
  public void initialize() {
    this.backBtn.sceneProperty().addListener((observable, oldValue, newValue) -> {
      if(oldValue == null && newValue != null) {
        newValue.getRoot().setStyle("-fx-background-color: linear-gradient(to bottom, burlywood, lightgoldenrodyellow);");
      }
    });

    this.backBtn.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image backBtn = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
      ImageView backBtnView = new ImageView(backBtn);
      backBtnView.setFitHeight(this.backBtn.getHeight());
      backBtnView.setFitWidth(this.backBtn.getWidth());
      this.backBtn.getChildren().add(backBtnView);
    });
  }


  public void setTitle(String title) {
    this.title.setText(title);
  }

  /**
   * Closes the dialog and returns to the landing page
   * @author aemsbach
   * @param event the mouse event that triggered the action
   * @throws IOException if the server cannot be reached
   */
  @FXML
  void backToLandingPage(MouseEvent event) throws IOException {

    Stage stage = (Stage) this.backBtn.getScene().getWindow();
    if(this.loginController!= null) {
      loginController.switchToLandPage(event);
    }
    else{
      registerController.switchToLandPage(event);
    }

    stage.close();


  }

  public void setLoginController(LoginController loginController) {
    this.loginController = loginController;
  }
  public void setRegisterController(RegisterController registerController) {
    this.registerController = registerController;
  }

  /**
   * Changes the cursor to a hand when the mouse enters the back button
   * @author aemsbach
   * @param event the mouse event that triggered the action
   */
  @FXML
  void mouseEntered(MouseEvent event) {
    HelperMethods.mouseEntered(event);
    Image backBtn = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/buttonSmall_white.png")).toString());
    ImageView backBtnView = new ImageView(backBtn);
    backBtnView.setFitHeight(this.backBtn.getHeight());
    backBtnView.setFitWidth(this.backBtn.getWidth());
    this.backBtn.getChildren().remove(0);
    this.backBtn.getChildren().add(backBtnView);

  }

  /**
   * Changes the cursor back to the default when the mouse leaves the back button
   * @author aemsbach
   * @param event the mouse event that triggered the action
   */
  @FXML
  void mouseExited(MouseEvent event) {
    HelperMethods.mouseExited(event);
    Image backBtn = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
    ImageView backBtnView = new ImageView(backBtn);
    backBtnView.setFitHeight(this.backBtn.getHeight());
    backBtnView.setFitWidth(this.backBtn.getWidth());
    this.backBtn.getChildren().remove(0);
    this.backBtn.getChildren().add(backBtnView);
  }

}

