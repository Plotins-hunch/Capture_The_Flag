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
import javafx.stage.Stage;

/**
 * Controller for the register page that allows users to register a new account
 */
public class RegisterController {

  public TextField userNameField;
  public TextField passwordField;
  public TextField emailField;
  public AnchorPane registerBtn;
  public AnchorPane backBtn;
  public AnchorPane bgPane;
  public AnchorPane nonBinaryPic;
  public AnchorPane femalePic;
  public AnchorPane malePic;

  private String gender;


  /**
   * Initializes the controller by setting the background image of the page and the prompt text of the text fields
   * @author aemsbach
   */
  public void initialize() {
    this.userNameField.setPromptText("Username");
    this.passwordField.setPromptText("Password");
    this.emailField.setPromptText("Email");

    this.bgPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
      Image img = new Image(Objects.requireNonNull(getClass().getResource("/assets/background.png")).toString());
      this.bgPane.setStyle("-fx-background-image: url('" + img.getUrl() + "'); " +
          "-fx-background-size: cover; -fx-background-repeat: no-repeat;");
    });

    this.backBtn.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image image = new Image(
          Objects.requireNonNull(getClass().getResourceAsStream("/assets/buttonSmall.png")));
      ImageView imageView = new ImageView(image);
      imageView.setFitHeight(this.backBtn.getHeight());
      imageView.setFitWidth(this.backBtn.getWidth());
      this.backBtn.getChildren().add(imageView);
    });
    this.registerBtn.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image image = new Image(
          Objects.requireNonNull(getClass().getResourceAsStream("/assets/buttonSmall.png")));
      ImageView imageView = new ImageView(image);
      imageView.setFitHeight(this.registerBtn.getHeight());
      imageView.setFitWidth(this.registerBtn.getWidth());
      this.registerBtn.getChildren().add(imageView);
    });

    this.nonBinaryPic.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image image = new Image(Objects.requireNonNull(
          getClass().getResourceAsStream("/assets/userAccount/profilePicDiverse.png")));
      ImageView imageView = new ImageView(image);
      imageView.setFitHeight(this.nonBinaryPic.getHeight());
      imageView.setFitWidth(this.nonBinaryPic.getWidth());
      this.nonBinaryPic.getChildren().add(imageView);
    });
    this.femalePic.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image image = new Image(Objects.requireNonNull(
          getClass().getResourceAsStream("/assets/userAccount/profilePicWoman.png")));
      ImageView imageView = new ImageView(image);
      imageView.setFitHeight(this.femalePic.getHeight());
      imageView.setFitWidth(this.femalePic.getWidth());
      this.femalePic.getChildren().add(imageView);
    });
    this.malePic.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image image = new Image(Objects.requireNonNull(
          getClass().getResourceAsStream("/assets/userAccount/profilePicMan.png")));
      ImageView imageView = new ImageView(image);
      imageView.setFitHeight(this.malePic.getHeight());
      imageView.setFitWidth(this.malePic.getWidth());
      this.malePic.getChildren().add(imageView);
    });

  }

  /**
   * This method is used to switch to the login page when the back button or the register button is clicked
   * @author rkonradt
   * @param e ActionEvent which is the event of clicking the back button or the register button
   * @throws IOException if the FXML file is not found
   */
  @FXML
  public void switchToLogin(MouseEvent e) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/Login.fxml"));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * This method is used to switch to the landing page when the register button is clicked
   * It also performs the sign-up operation synchronously and sets the user ID in the GUI class
   * @author aemsbach
   * @param actionEvent ActionEvent which is the event of clicking the register button
   * @throws IOException if the FXML file is not found
   */
  public void switchToLandingPage(MouseEvent actionEvent) throws IOException {
    new DatabaseInitializer().initialize();
    UserDAO userDAO = new UserDAO();
    AuthController authController = new AuthController(userDAO);

    // Perform sign-up synchronously
    try {
      CompletableFuture<UserModel> signUpFuture = authController.signUp(this.emailField.getText(), this.passwordField.getText(), this.userNameField.getText(), this.gender);
      UserModel newUser = signUpFuture.get(); // Blocking call to wait for sign-up completion
      GUI.userId = newUser.getUid();
      GUI.userGender = newUser.getGender();
    } catch (InterruptedException | ExecutionException e) {
      System.err.println("Error during user sign-up: " + e.getMessage());
      this.showResponse(actionEvent, true);
      return;

    }
    this.showResponse(actionEvent, false);

  }

  public void showResponse(MouseEvent mouseEvent, boolean error) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/loginResponseScreen.fxml"));
    Parent root = fxmlLoader.load();
    Stage popupStage = new Stage();
    Scene scene = new Scene(root);
    popupStage.initOwner( ((Node) mouseEvent.getSource()).getScene().getWindow());
    popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
    LoginResponseController controller = fxmlLoader.getController();
    controller.setRegisterController(this);
    if(!error){
      controller.setTitle("Registration successful!");
    }
    popupStage.setScene(scene);
    popupStage.show();
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
    Stage stage = (Stage) this.registerBtn.getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Changes the cursor to a hand when the mouse enters the register button and changes the button color to white
   * @author aemsbach
   * @param mouseEvent MouseEvent that is triggered when the mouse enters the register button
   */
  public void mouseEntered(MouseEvent mouseEvent) {
    HelperMethods.mouseEntered(mouseEvent);
    Image img = new Image(
        Objects.requireNonNull(getClass().getResourceAsStream("/assets/buttonSmall_white.png")));
    ImageView imageView = new ImageView(img);
    Label label = (Label) (mouseEvent.getSource());
    if (label.getText().equals("Register")) {
      imageView.setFitHeight(this.registerBtn.getHeight());
      imageView.setFitWidth(this.registerBtn.getWidth());
      this.registerBtn.getChildren().remove(0);
      this.registerBtn.getChildren().add(imageView);
    } else if (label.getText().equals("Back")) {
      imageView.setFitHeight(this.backBtn.getHeight());
      imageView.setFitWidth(this.backBtn.getWidth());
      this.backBtn.getChildren().remove(0);
      this.backBtn.getChildren().add(imageView);
    } else if (label.getText().equals("nonBinary")) {
      img = new Image(Objects.requireNonNull(
          getClass().getResourceAsStream("/assets/userAccount/white_profilePicDiverse.png")));
      imageView = new ImageView(img);
      imageView.setFitHeight(this.nonBinaryPic.getHeight());
      imageView.setFitWidth(this.nonBinaryPic.getWidth());
      this.nonBinaryPic.getChildren().remove(0);
      this.nonBinaryPic.getChildren().add(imageView);
    } else if (label.getText().equals("female")) {
      img = new Image(Objects.requireNonNull(
          getClass().getResourceAsStream("/assets/userAccount/white_profilePicWoman.png")));
      imageView = new ImageView(img);
      imageView.setFitHeight(this.femalePic.getHeight());
      imageView.setFitWidth(this.femalePic.getWidth());
      this.femalePic.getChildren().remove(0);
      this.femalePic.getChildren().add(imageView);
    } else if (label.getText().equals("male")) {
      img = new Image(Objects.requireNonNull(
          getClass().getResourceAsStream("/assets/userAccount/white_profilePicMan.png")));
      imageView = new ImageView(img);
      imageView.setFitHeight(this.malePic.getHeight());
      imageView.setFitWidth(this.malePic.getWidth());
      this.malePic.getChildren().remove(0);
      this.malePic.getChildren().add(imageView);
    }
  }

  /**
   * Changes the cursor back to the default cursor when the mouse exits the register button and changes the button color back to the original color
   * @author aemsbach
   * @param mouseEvent MouseEvent that is triggered when the mouse exits the register button
   */
  public void mouseExited(MouseEvent mouseEvent) {
    HelperMethods.mouseExited(mouseEvent);
    Label label = (Label) (mouseEvent.getSource());
    if(label.getText().equals("Register")) {
      this.registerBtn.getChildren().remove(0);
      ImageView imageView = new ImageView(new Image(
          Objects.requireNonNull(getClass().getResourceAsStream("/assets/buttonSmall.png"))));
      imageView.setFitHeight(this.registerBtn.getHeight());
      imageView.setFitWidth(this.registerBtn.getWidth());
      this.registerBtn.getChildren().add(imageView);
    } else if(label.getText().equals("Back")){
      this.backBtn.getChildren().remove(0);
      ImageView imageView = new ImageView(new Image(
          Objects.requireNonNull(getClass().getResourceAsStream("/assets/buttonSmall.png"))));
      imageView.setFitHeight(this.backBtn.getHeight());
      imageView.setFitWidth(this.backBtn.getWidth());
      this.backBtn.getChildren().add(imageView);
    }
    else if(label.getText().equals("nonBinary") && (this.gender == null || !this.gender.equals("diverse"))){
      this.nonBinaryPic.getChildren().remove(0);
      ImageView imageView = new ImageView(new Image(Objects.requireNonNull(
          getClass().getResourceAsStream("/assets/userAccount/profilePicDiverse.png"))));
      imageView.setFitHeight(this.nonBinaryPic.getHeight());
      imageView.setFitWidth(this.nonBinaryPic.getWidth());
      this.nonBinaryPic.getChildren().add(imageView);
    }
    else if(label.getText().equals("female") && (this.gender == null || !this.gender.equals("female"))){
      this.femalePic.getChildren().remove(0);
      ImageView imageView = new ImageView(new Image(Objects.requireNonNull(
          getClass().getResourceAsStream("/assets/userAccount/profilePicWoman.png"))));
      imageView.setFitHeight(this.femalePic.getHeight());
      imageView.setFitWidth(this.femalePic.getWidth());
      this.femalePic.getChildren().add(imageView);
  }
    else if(label.getText().equals("male") && (this.gender == null || !this.gender.equals("male"))){
      this.malePic.getChildren().remove(0);
      ImageView imageView = new ImageView(new Image(Objects.requireNonNull(
          getClass().getResourceAsStream("/assets/userAccount/profilePicMan.png"))));
      imageView.setFitHeight(this.malePic.getHeight());
      imageView.setFitWidth(this.malePic.getWidth());
      this.malePic.getChildren().add(imageView);
    }
  }

  /**
   * Changes the gender and profle picture of the user to male
   * @author aemsbach
   * @param ignored MouseEvent that is triggered when the user clicks the button
   */
  public void maleClicked(MouseEvent ignored) {
    this.gender = "male";

    this.nonBinaryPic.getChildren().remove(0);
    ImageView imageView = new ImageView(new Image(Objects.requireNonNull(
        getClass().getResourceAsStream("/assets/userAccount/profilePicDiverse.png"))));
    imageView.setFitHeight(this.nonBinaryPic.getHeight());
    imageView.setFitWidth(this.nonBinaryPic.getWidth());
    this.nonBinaryPic.getChildren().add(imageView);

    this.femalePic.getChildren().remove(0);
    imageView = new ImageView(new Image(Objects.requireNonNull(
        getClass().getResourceAsStream("/assets/userAccount/profilePicWoman.png"))));
    imageView.setFitHeight(this.femalePic.getHeight());
    imageView.setFitWidth(this.femalePic.getWidth());
    this.femalePic.getChildren().add(imageView);

    Image img = new Image(Objects.requireNonNull(
        getClass().getResourceAsStream("/assets/userAccount/white_profilePicMan.png")));
    imageView = new ImageView(img);
    imageView.setFitHeight(this.malePic.getHeight());
    imageView.setFitWidth(this.malePic.getWidth());
    this.malePic.getChildren().remove(0);
    this.malePic.getChildren().add(imageView);

  }

  /**
   * Changes the gender and profle picture to female
   * @author aemsbach
   * @param ignored MouseEvent that is triggered when the user clicks the button
   */
  public void femaleClicked(MouseEvent ignored) {
    this.gender = "female";

    this.nonBinaryPic.getChildren().remove(0);
    ImageView imageView = new ImageView(new Image(Objects.requireNonNull(
        getClass().getResourceAsStream("/assets/userAccount/profilePicDiverse.png"))));
    imageView.setFitHeight(this.nonBinaryPic.getHeight());
    imageView.setFitWidth(this.nonBinaryPic.getWidth());
    this.nonBinaryPic.getChildren().add(imageView);

    Image img = new Image(Objects.requireNonNull(
        getClass().getResourceAsStream("/assets/userAccount/white_profilePicWoman.png")));
    imageView = new ImageView(img);
    imageView.setFitWidth(this.femalePic.getWidth());
    imageView.setFitHeight(this.femalePic.getHeight());
    this.femalePic.getChildren().remove(0);
    this.femalePic.getChildren().add(imageView);

    this.malePic.getChildren().remove(0);
    imageView = new ImageView(new Image(Objects.requireNonNull(
        getClass().getResourceAsStream("/assets/userAccount/profilePicMan.png"))));
    imageView.setFitHeight(this.malePic.getHeight());
    imageView.setFitWidth(this.malePic.getWidth());
    this.malePic.getChildren().add(imageView);
  }

  /**
   * Changes the profile picture and gender of the user to non-binary
   * @author aemsbach
   * @param ignored MouseEvent that is triggered when the user clicks the button
   */
  public void nonBinaryClicked(MouseEvent ignored) {
    this.gender = "diverse";

    this.malePic.getChildren().remove(0);
    ImageView imageView = new ImageView(new Image(Objects.requireNonNull(
        getClass().getResourceAsStream("/assets/userAccount/profilePicMan.png"))));
    imageView.setFitHeight(this.malePic.getHeight());
    imageView.setFitWidth(this.malePic.getWidth());
    this.malePic.getChildren().add(imageView);

    this.femalePic.getChildren().remove(0);
    imageView = new ImageView(new Image(Objects.requireNonNull(
        getClass().getResourceAsStream("/assets/userAccount/profilePicWoman.png"))));
    imageView.setFitHeight(this.femalePic.getHeight());
    imageView.setFitWidth(this.femalePic.getWidth());
    this.femalePic.getChildren().add(imageView);

    Image img = new Image(Objects.requireNonNull(
        getClass().getResourceAsStream("/assets/userAccount/white_profilePicDiverse.png")));
    imageView = new ImageView(img);
    imageView.setFitHeight(this.nonBinaryPic.getHeight());
    imageView.setFitWidth(this.nonBinaryPic.getWidth());
    this.nonBinaryPic.getChildren().remove(0);
    this.nonBinaryPic.getChildren().add(imageView);
  }
}
