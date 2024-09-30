package de.unimannheim.swt.pse.client.controller;

import de.unimannheim.swt.pse.client.GUI;
import de.unimannheim.swt.pse.client.HelperMethods;
import de.unimannheim.swt.pse.client.RequestHandler;
import de.unimannheim.swt.pse.server.CtfApplication;
import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * @author ${Patrick.Klaus}
 */
public class LandingPageController {

  public GridPane bgPane;
  public StackPane newGame;
  public ImageView joinGame;
  public StackPane mapEditor;
  public AnchorPane loginBtn;

  private MediaPlayer mediaPlayer;


  Image logInImage;
  Image logInHighlighted;
  @FXML
  void initialize() {
    if(GUI.userId == null) {
      logInImage = new Image(
          Objects.requireNonNull(getClass().getResource(
              "/assets/userAccount/profileBoardGuest.png"))
              .toString());
      logInHighlighted = new Image(
          Objects.requireNonNull(getClass().getResource(
              "/assets/userAccount/white_profileBoardGuest.png"))
              .toString());
    }
    else{
      if(GUI.userGender == 0){
        logInImage = new Image(
            Objects.requireNonNull(getClass().getResource(
                "/assets/userAccount/profileBoardDiverse.png"))
                .toString());
        logInHighlighted = new Image(
            Objects.requireNonNull(getClass().getResource(
                "/assets/userAccount/white_profileBoardDiverse.png"))
                .toString());
      }
      else if(GUI.userGender == 1){
        logInImage = new Image(
            Objects.requireNonNull(getClass().getResource(
                "/assets/userAccount/profileBoardWoman.png"))
                .toString());
        logInHighlighted = new Image(
            Objects.requireNonNull(getClass().getResource(
                "/assets/userAccount/white_profileBoardWoman.png"))
                .toString());
      }
      else if(GUI.userGender == 2){
        logInImage = new Image(
            Objects.requireNonNull(getClass().getResource(
                "/assets/userAccount/profileBoardMan.png"))
                .toString());
        logInHighlighted = new Image(
            Objects.requireNonNull(getClass().getResource(
                "/assets/userAccount/white_profileBoardMan.png"))
                .toString());
      }

    }

    String mainMenuMusic = Objects.requireNonNull(getClass().getResource("/music/MainMenu.mp3")).toString();
    Media music = new Media(mainMenuMusic);
    MediaPlayer mediaPlayer = new MediaPlayer(music);
    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    //mediaPlayer.play();
    this.mediaPlayer = mediaPlayer;
    this.bgPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
      if (oldValue == null && newValue != null) {
        Image img = new Image(Objects.requireNonNull(getClass().getResource("/assets/landingScreen.png")).toString());
        newValue.getRoot().setStyle("-fx-background-image: url('" + img.getUrl() + "'); " +
            "-fx-background-size: cover; -fx-background-repeat: no-repeat;");
      }
    });
    this.loginBtn.heightProperty().addListener((observable, oldValue, newValue) -> {

      ImageView imageView = new ImageView(logInImage);
      imageView.setFitWidth(this.loginBtn.getWidth());
      imageView.setFitHeight(this.loginBtn.getHeight());
      this.loginBtn.getChildren().add(imageView);
    });

    Thread thread = new Thread(this::musicThread);
    thread.setDaemon(true);
    thread.start();

  }

  private void musicThread(){
    boolean interrupted = false;
    while(!interrupted){
      if(this.mediaPlayer.getStatus().equals(Status.STOPPED)){
        this.mediaPlayer.play();
      }
    }
  }

  public void passInfo(MediaPlayer mediaPlayer) {
    this.mediaPlayer = mediaPlayer;


  }

  /**
   * Start the game server and switch to the selection map game menu
   * @author aemsbach
   * @param e ActionEvent on click of New Game Button
   * @throws IOException Exception when switching to the selectMap menu
   */
  public void switchToMapGameMenu(MouseEvent e) throws IOException {
    CtfApplication.main(new String[]{}); // Start the server
    //Set request handler for host
    RequestHandler requestHandler = new RequestHandler();
    requestHandler.setBaseUrl("http://localhost:8888");
    //Switch to selectMap Menu
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/SelectMap.fxml"));
    Parent root = fxmlLoader.load();
    //Set scene width and height
    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    //add stylesheet
    scene.getStylesheets().add(
    Objects.requireNonNull(getClass().getResource("/css/SelectMapStyle.css")).toExternalForm());
    stage.setScene(scene);

    //pass request handler to selectMapController
    SelectMapController selectMapController = fxmlLoader.getController();
    selectMapController.passInfo(requestHandler, mediaPlayer);
    Screen screen = Screen.getPrimary();
    stage.setHeight(screen.getVisualBounds().getHeight() * 0.999);
    stage.setWidth(screen.getVisualBounds().getWidth() * 0.999);

    stage.show();
    //Close the server when the window is closed
    stage.setOnCloseRequest(event -> {
      CtfApplication.kill();
      stage.close();
      System.exit(0);
    });

  }

  /**
   * @author paklaus
   * Switch to the map editor menu
   * @param e MouseEvent on click of map editor button
   * @throws IOException Exception when switching to the map editor menu
   */

  public void switchToMapEditor(MouseEvent e) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/MapEditorMenu.fxml"));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    stage.setOnCloseRequest(event -> {
      try {
        CtfApplication.kill();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });
    Scene scene = new Scene(root);
    stage.setScene(scene);

    Screen screen = Screen.getPrimary();
    stage.setHeight(screen.getVisualBounds().getHeight() * 0.999);
    stage.setWidth(screen.getVisualBounds().getWidth() * 0.999);

    stage.show();
  }

  @FXML
  public void switchToJoinGame(MouseEvent e) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/JoinGame.fxml"));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);

    stage.setScene(scene);

    Screen screen = Screen.getPrimary();
    stage.setHeight(screen.getVisualBounds().getHeight() * 0.999);
    stage.setWidth(screen.getVisualBounds().getWidth() * 0.999);
    stage.show();
  }

  public void mouseExited(MouseEvent mouseEvent) {
    HelperMethods.mouseExited(mouseEvent);
    Image backBtn = new Image(Objects.requireNonNull(getClass().getResource("/assets/woodButton1.png")).toString());
    ImageView img = new ImageView(backBtn);
    Label label = (Label) mouseEvent.getSource();
    img.setFitHeight(label.getHeight());
    img.setFitWidth(label.getWidth());
    if(label.getText().equals("New Game")){
      this.newGame.getChildren().remove(0);
      this.newGame.getChildren().add(0,img);}
    else if(label.getText().equals("Join Game")){
      Image image = (new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonthird.png")).toString()));
      this.joinGame.setImage(image);
    }
    else if(label.getText().equals("Map Editor")){
      this.mapEditor.getChildren().remove(0);
      this.mapEditor.getChildren().add(0,img);
    }
    else{
      this.loginBtn.getChildren().remove(0);
      ImageView imageView = new ImageView(this.logInImage);
      imageView.setFitWidth(this.loginBtn.getWidth());
      imageView.setFitHeight(this.loginBtn.getHeight());
      this.loginBtn.getChildren().add(imageView);
    }
  }

  /**
   * Highlight the button when the mouse enters the button
   * @author aemsbach
   * @param mouseEvent MouseEvent on mouse enter
   */
  public void mouseEntered(MouseEvent mouseEvent) {
    HelperMethods.mouseEntered(mouseEvent);
    Image backBtn = new Image(Objects.requireNonNull(getClass().getResource("/assets/woodButton1_white.png")).toString());
    ImageView img = new ImageView(backBtn);
    Label label = (Label) mouseEvent.getSource();
    img.setFitHeight(label.getHeight());
    img.setFitWidth(label.getWidth());
    if(label.getText().equals("New Game")){
      this.newGame.getChildren().remove(0);
      this.newGame.getChildren().add(0,img);}
    else if(label.getText().equals("Join Game")){
      Image image = (new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonthird_white.png")).toString()));
      this.joinGame.setImage(image);
    }
    else if(label.getText().equals("Map Editor")){
      this.mapEditor.getChildren().remove(0);
      this.mapEditor.getChildren().add(0,img);
    }
    else {
      this.loginBtn.getChildren().remove(0);
      ImageView imageView = new ImageView(this.logInHighlighted);
      imageView.setFitWidth(this.loginBtn.getWidth());
      imageView.setFitHeight(this.loginBtn.getHeight());
      this.loginBtn.getChildren().add(imageView);

    }
  }

  /**
   * Switch to the login screen
   * @author aemsbach
   * @param actionEvent MouseEvent on click of login button
   */
  public void switchToUserScreen(MouseEvent actionEvent) {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/Login.fxml"));
    try {
      Parent root = fxmlLoader.load();
      Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
      Scene scene = new Scene(root);
      stage.setScene(scene);
      Screen screen = Screen.getPrimary();
      stage.setHeight(screen.getVisualBounds().getHeight() * 0.999);
      stage.setWidth(screen.getVisualBounds().getWidth() * 0.999);
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
