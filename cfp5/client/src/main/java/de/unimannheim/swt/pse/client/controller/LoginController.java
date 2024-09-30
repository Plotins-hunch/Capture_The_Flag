package de.unimannheim.swt.pse.client.controller;

import de.unimannheim.swt.pse.client.GUI;
import de.unimannheim.swt.pse.client.HelperMethods;
import de.unimannheim.swt.pse.server.authentication.AuthController;
import de.unimannheim.swt.pse.server.database.DatabaseInitializer;
import de.unimannheim.swt.pse.server.database.dao.UserDAO;
import de.unimannheim.swt.pse.server.database.model.UserModel;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
/**
 * Controller for the Login Screen that allows user to log in to the game
 */
public class LoginController {

  public TextField usernameField;
  public TextField passwordField;
  public AnchorPane backBtn;
  public AnchorPane loginBtn;
  public AnchorPane createBtn;
  public AnchorPane bgPane;

  /**
   * Initializes the controller by setting the background image of the login screen and the images of the buttons
   * @author aemsbach
   */
  public void initialize() {
    this.usernameField.setPromptText("Username");
    this.passwordField.setPromptText("Password");

    this.bgPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
      Image img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/background.png")).toString());
      this.bgPane.setStyle("-fx-background-image: url('" + img.getUrl() + "'); " +
          "-fx-background-size: cover; -fx-background-repeat: no-repeat;");
    });

    this.backBtn.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image backBtnImage = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
      ImageView backBtnImageView = new ImageView(backBtnImage);
      backBtnImageView.setFitHeight(this.backBtn.getHeight());
      backBtnImageView.setFitWidth(this.backBtn.getWidth());
      this.backBtn.getChildren().add(backBtnImageView);

    });
    this.loginBtn.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image loginBtnImage = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
      ImageView loginBtnImageView = new ImageView(loginBtnImage);
      loginBtnImageView.setFitHeight(this.loginBtn.getHeight());
      loginBtnImageView.setFitWidth(this.loginBtn.getWidth());
      this.loginBtn.getChildren().add(loginBtnImageView);
    });

  }

  /**
   * This method is used to switch to the landing page when the back button is clicked
   * @author rkonradt
   * @param e ActionEvent which is the event of clicking the back button
   * @throws IOException if the FXML file is not found
   */
  @FXML
  public void switchToLandingPage(MouseEvent e) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/LandingPage.fxml"));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    Screen screen = Screen.getPrimary();
    stage.setHeight(screen.getVisualBounds().getHeight() * 0.999);
    stage.setWidth(screen.getVisualBounds().getWidth() * 0.999);
    stage.show();
  }

  /**
   * This method is used to switch to the register page when the create account button is clicked
   * @author rkonradt
   * @param e ActionEvent which is the event of clicking the create account button
   * @throws IOException if the FXML file is not found
   */
  @FXML
  public void switchToRegister(MouseEvent e) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/Register.fxml"));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * This method is used to switch to the profile page when the login button is clicked
   * @author rkonradt
   * @param e ActionEvent which is the event of clicking the login button
   * @throws IOException if the FXML file is not found
   */
  @FXML
  public void switchToProfile(MouseEvent e) throws IOException {
    new DatabaseInitializer().initialize();
    UserDAO userDAO = new UserDAO();
    AuthController authController = new AuthController(userDAO);

    // Perform sign-in synchronously
    try {
      CompletableFuture<UserModel> signInFuture = authController.signIn(
          this.usernameField.getText(), this.passwordField.getText());
      UserModel user = signInFuture.get(); // Blocking call to wait for sign-in completion
      GUI.userId = user.getUid();
      GUI.userGender = user.getGender();

    } catch (InterruptedException | ExecutionException exception) {
      System.err.println("Error during user sign-in: " + exception.getMessage());
      this.showResponse(e, true);
      return;

    }
    this.showResponse(e, false);


  }

  /**
   * This method is used to switch to the landing page after the login attempt
   * @author aemsbach
   * @param ignored MouseEvent which is the event of clicking the back button
   * @throws IOException if the FXML file is not found
   */
  public void switchToLandPage(MouseEvent ignored) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/LandingPage.fxml"));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) this.backBtn.getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * This method is used to show the response of the login attempt in a popup window
   * @author aemsbach
   * @param e MouseEvent which is the event of clicking the login button
   * @param error boolean which is true if the login attempt was unsuccessful
   * @throws IOException if the FXML file is not found
   */
  public void showResponse(MouseEvent e, boolean error) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/loginResponseScreen.fxml"));
    Parent root = fxmlLoader.load();
    Stage popupStage = new Stage();
    LoginResponseController controller = fxmlLoader.getController();
    controller.setLoginController(this);
    if(!error){
      controller.setTitle("Login successful!");
    }

    popupStage.initOwner(((Node) e.getSource()).getScene().getWindow());
    popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
    //popupStage.initStyle(StageStyle.TRANSPARENT);

    Scene scene = new Scene(root);

    popupStage.setScene(scene);
    popupStage.show();
  }

  /**
   * This method is used to change the cursor to a hand and highlights buttons when the mouse enters the buttons
   * @author aemsbach
   * @param mouseEvent the mouse event that triggered the action
   */
  public void mouseEntered(MouseEvent mouseEvent) {
    HelperMethods.mouseEntered(mouseEvent);
    Image img = new Image(
        Objects.requireNonNull(getClass().getResourceAsStream("/assets/buttonSmall_white.png")));
    ImageView imageView = new ImageView(img);
    Label label = (Label) (mouseEvent.getSource());
    if (label.getText().equals("Login")) {
      imageView.setFitHeight(this.loginBtn.getHeight());
      imageView.setFitWidth(this.loginBtn.getWidth());
      this.loginBtn.getChildren().remove(0);
      this.loginBtn.getChildren().add(imageView);
    } else if(label.getText().equals("Back")){
      imageView.setFitHeight(this.backBtn.getHeight());
      imageView.setFitWidth(this.backBtn.getWidth());
      this.backBtn.getChildren().remove(0);
      this.backBtn.getChildren().add(imageView);
    }
    else{
        this.createBtn.setStyle("-fx-background-color: #FFA500; -fx-opacity: 0.5");
    }
  }

  /**
   * This method is used to change the cursor back to normal and unhighlights buttons when the mouse exits the buttons
   * @author aemsbach
   * @param mouseEvent the mouse event that triggered the action
   */
  public void mouseExited(MouseEvent mouseEvent) {
    HelperMethods.mouseExited(mouseEvent);
    Image img = new Image(
        Objects.requireNonNull(getClass().getResourceAsStream("/assets/buttonSmall.png")));
    ImageView imageView = new ImageView(img);
    Label label = (Label) (mouseEvent.getSource());
    if (label.getText().equals("Login")) {
      imageView.setFitHeight(this.loginBtn.getHeight());
      imageView.setFitWidth(this.loginBtn.getWidth());
      this.loginBtn.getChildren().remove(0);
      this.loginBtn.getChildren().add(imageView);
    } else if(label.getText().equals("Back")){
      imageView.setFitHeight(this.backBtn.getHeight());
      imageView.setFitWidth(this.backBtn.getWidth());
      this.backBtn.getChildren().remove(0);
      this.backBtn.getChildren().add(imageView);
    }
    else{
        this.createBtn.setStyle("-fx-background-color: #FFA500; -fx-opacity: 0");
    }
  }
}
