package de.unimannheim.swt.pse.client.controller;

import java.util.Objects;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Controller for the Loading Screen that is shown when the game is started and that shows the game
 * logo
 */
public class LoadingScreenController {

  public AnchorPane basePane;
  private MediaPlayer mediaPlayer;

  @FXML
  private Label text;

  private boolean interrupted = false;

  /**
   * Initializes the controller by setting the background image of the dialog and starting the
   * music
   */
  @FXML
  void initialize() {

    //start music
    MediaPlayer mediaPlayer = new MediaPlayer(new Media(
        getClass().getResource("/music/IntroToMenu.mp3").toString()));
    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    mediaPlayer.play();
    this.mediaPlayer = mediaPlayer;
    //set background image
    this.basePane.sceneProperty().addListener((observable, oldValue, newValue) -> {
      if (oldValue == null && newValue != null) {
        this.basePane.setStyle("-fx-background-color: #000000;");
        Image img = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/loadingScreen.png")).toString());
        ImageView imageView = new ImageView(img);
        this.basePane.getChildren().add(imageView);
        AnchorPane.setTopAnchor(imageView,
            Math.abs((this.basePane.getHeight() - img.getHeight())) / 4);
        AnchorPane.setLeftAnchor(imageView,
            Math.abs((this.basePane.getWidth() - img.getWidth())) / 8);
        //skip to landing page if any key is pressed
        newValue.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
          this.mediaPlayer.stop();
          this.mediaPlayer.dispose();
          MediaPlayer mediaPlayer2 = new MediaPlayer(new Media(
              Objects.requireNonNull(getClass().getResource("/music/MainMenu.mp3")).toString()));
          mediaPlayer2.setCycleCount(MediaPlayer.INDEFINITE);
          mediaPlayer2.play();
          this.mediaPlayer = mediaPlayer2;
          switchToLandingPage();
        });
      }
    });

    //start counter
    Thread thread = new Thread(this::counter);
    thread.setDaemon(true);
    thread.start();
    //start text animation
    Thread thread2 = new Thread(this::thread);
    thread2.setDaemon(true);
    thread2.start();


  }

  /**
   * Switches to the landing page
   *
   * @author aemsbach
   */
  private void switchToLandingPage() {
    this.interrupted = true;
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("/fxml/LandingPage.fxml"));
    Screen screen = Screen.getPrimary();

    try {
      loader.load();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    LandingPageController controller = loader.getController();
    controller.passInfo(mediaPlayer);
    Scene scene = new Scene(loader.getRoot());
    Stage stage = (Stage) basePane.getScene().getWindow();
    stage.setScene(scene);
    stage.setHeight(screen.getVisualBounds().getHeight() * 0.999);
    stage.setWidth(screen.getVisualBounds().getWidth() * 0.999);
    stage.show();

  }


  /**
   * Shows loading screen for 10 seconds and then switches to landing page
   * @author aemsbach
   */
  private void counter() {
    int seconds = 0;
    while (seconds < 11) {
      if(this.interrupted){
        return;
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ignored) {

      }
      seconds++;

    }
    this.interrupted = true;  //stop text animation
    Platform.runLater(this::switchToLandingPage);

  }

  /**
   * Animates the text on the loading screen by changing its opacity
   * @author aemsbach
   */
  private void thread() {
    boolean descent = true;
    while (!this.interrupted) {
      if (this.text.getOpacity() > 0.5 && descent) {
        this.text.setOpacity(this.text.getOpacity() - 0.1);
      } else if (this.text.getOpacity() < 1 && !descent) {
        this.text.setOpacity(this.text.getOpacity() + 0.1);
      }
      if (this.text.getOpacity() <= 0.5) {
        descent = false;
      }
      if (this.text.getOpacity() == 1) {
        descent = true;
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException ignored) {
      }
    }
  }


}
