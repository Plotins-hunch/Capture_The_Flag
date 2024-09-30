package de.unimannheim.swt.pse.client.controller;

import de.unimannheim.swt.pse.client.HelperMethods;
import de.unimannheim.swt.pse.client.RequestHandler;
import de.unimannheim.swt.pse.server.CtfApplication;
import de.unimannheim.swt.pse.server.controller.data.GameSessionResponse;
import de.unimannheim.swt.pse.server.database.DatabaseInitializer;
import de.unimannheim.swt.pse.server.database.controller.GameSessionsController;
import de.unimannheim.swt.pse.server.database.dao.GameSessionsDAO;
import de.unimannheim.swt.pse.server.database.service.GameSessionsService;
import de.unimannheim.swt.pse.server.game.state.Team;
import de.unimannheim.swt.pse.server.game.state.Theme;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Controller for the game over screen that is shown when the game is over
 */
public class GameOverController {

  public Label resultLabel;
  public Label fallenPieces;
  public Label movesMade;
  public Label gameTime;
  public AnchorPane papyrusScroll;
  public AnchorPane backPane;
  private RequestHandler requestHandler;
  private String gameSessionId;
  private MediaPlayer mediaPlayer;
  private boolean isWinner = false;

  @FXML
  AnchorPane blueTeam;
  @FXML
  AnchorPane redTeam;
  @FXML
  AnchorPane purpleTeam;
  @FXML
  AnchorPane greenTeam;


  /**
   * Initializes the controller and sets the background image
   * @author aemsbach
   */
  public void initialize() {
    backPane.heightProperty().addListener(((observableValue, number, t1) -> {
      Image img = new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
      ImageView imageView = new ImageView(img);
      imageView.setFitWidth(this.backPane.getWidth());
      imageView.setFitHeight(this.backPane.getHeight());
      backPane.getChildren().add(imageView);
    }));
  }
  /**
   * Closes the window and returns to the main menu to start a new game/join game/create map
   * @author aemsbach
   * @param e the mouse event
   */
  public void backToMainMenu(MouseEvent e) throws IOException {
    if(this.mediaPlayer != null) {
      this.mediaPlayer.stop();
      this.mediaPlayer.dispose();
      MediaPlayer mediaPlayer2 = new MediaPlayer(new Media(
          Objects.requireNonNull(getClass().getResource("/music/MainMenu.mp3")).toString()));
      mediaPlayer2.setCycleCount(MediaPlayer.INDEFINITE);
      mediaPlayer2.play();
      this.mediaPlayer = mediaPlayer2;
    }

    if(requestHandler.getBaseUrl().equals("http://localhost:8888")){
      requestHandler.deleteGameSession(gameSessionId);
      new DatabaseInitializer().initialize();
      GameSessionsDAO gameSessionsDAO = new GameSessionsDAO();
      GameSessionsService gameSessionsService = new GameSessionsService(gameSessionsDAO);
      GameSessionsController gameSessionsController = new GameSessionsController(gameSessionsService);

      // Delete a game session
      gameSessionsController.deleteGameSession(this.gameSessionId);
      CtfApplication.kill();
    }
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/LandingPage.fxml"));
    LandingPageController controller = new LandingPageController();
    controller.passInfo(mediaPlayer);
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);

    String css = Objects.requireNonNull(getClass().getResource("/css/LandingPageStyle.css")).toExternalForm();
    scene.getStylesheets().add(css);

    stage.setScene(scene);
    Screen screen = Screen.getPrimary();
    stage.setHeight(screen.getVisualBounds().getHeight() * 0.999);
    stage.setWidth(screen.getVisualBounds().getWidth() * 0.999);
    stage.show();
  }
  /**
   * Passes the information to the controller to display the game over screen
   * @author aemsbach
   * @param requestHandler RequestHandler to send requests to the server
   * @param gameSessionId String which is the id of the game session
   * @param winner String[] which is the id of the winning team
   * @param teamId String which is the id of the team
   * @param mediaPlayer MediaPlayer which is the music player
   * @param theme Theme which is the theme of the game
   * @param movesMade int which is the amount of moves made
   * @param myTeam Team which is the team of the player
   * @param initPieceCount int which is the initial amount of pieces
   */
  public void passInfo(RequestHandler requestHandler, String gameSessionId, String[] winner, String teamId, MediaPlayer mediaPlayer, Theme theme, int movesMade, Team myTeam,
      int initPieceCount) {
    //Set important variables
    this.requestHandler = requestHandler;
    this.gameSessionId = gameSessionId;
    this.movesMade.setText(String.valueOf(movesMade));
    GameSessionResponse session = this.requestHandler.getGameSession(gameSessionId);
    //calculate game time
    int gameTime = (int) (session.getGameEnded().getTime() - session.getGameStarted().getTime())/1000;
    this.gameTime.setText(gameTime/60 + " : " + (gameTime%60 < 10 ? "0" : "")  + gameTime%60);
    System.out.println("initPieceCount: " + initPieceCount);
    System.out.println("myTeamPieces: " + myTeam.getPieces().length);
    this.fallenPieces.setText(String.valueOf(initPieceCount - myTeam.getPieces().length));
    //Set the result label
    if(winner.length == 1){
      if(Objects.equals(winner[0], teamId)){
        resultLabel.setText("You won");
        this.isWinner = true;
      }
    }

    else {
      for (String s : winner) {
        if (Objects.equals(s, teamId)) {
          resultLabel.setText("You tied");
          this.isWinner = true;
        }
      }
    }
    //Play appropriate music (win/loss)
    this.mediaPlayer = mediaPlayer;
    if(mediaPlayer != null){
      while(mediaPlayer.getVolume() > 0.1){
        mediaPlayer.setVolume(mediaPlayer.getVolume() - 0.1);
      }
      if (isWinner) {
        MediaPlayer winSound = new MediaPlayer(new Media(
            Objects.requireNonNull(getClass().getResource("/music/winner.mp3")).toString()));
        winSound.setVolume(0.1);
        winSound.play();
        this.mediaPlayer = winSound;
        while(winSound.getVolume() < 1){
          winSound.setVolume(winSound.getVolume() + 0.1);
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }

      }
      else {
        MediaPlayer loseSound = new MediaPlayer(
            new Media(Objects.requireNonNull(getClass().getResource("/music/loser.mp3")).toString()));

        loseSound.play();
        this.mediaPlayer = loseSound;

      }
    }
    //set background image
    Image papyrus = new Image(Objects.requireNonNull(getClass().getResource("/assets/resultPapyrus.png")).toString());
    this.papyrusScroll.setStyle("-fx-background-image: url('" + papyrus.getUrl() + "'); " +
        "-fx-background-size: cover; -fx-background-repeat: no-repeat;");
    switch(theme){
      case land:
        Image img = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/mapGras.png"))
                .toString());
        this.resultLabel.getScene().getRoot().setStyle(
            "-fx-background-image: url('" + img.getUrl() + "'); "
                + "-fx-background-size: cover; -fx-background-repeat: no-repeat;");

        this.blueTeam.widthProperty().addListener(((observableValue, number, t1) -> this.setImagesLand()));
        break;
      case sea:
        Image img2 = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/mapWater.png"))
                .toString());
        this.resultLabel.getScene().getRoot().setStyle(
            "-fx-background-image: url('" + img2.getUrl() + "'); "
                + "-fx-background-size: cover; -fx-background-repeat: no-repeat;");
        //this.blueTeam.widthProperty().addListener(((observableValue, number, t1) -> this.setImagesSea()));
        break;

    }

  }
  /**
   * Sets the piece images for the teams in the land theme
   * @author aemsbach
   */
  private void setImagesLand(){
    String image = "/assets/pieces/";
    Image bluePlayer = new Image(
        Objects.requireNonNull(getClass().getResourceAsStream(image + "blue_general.png")));
    Image redPlayer = new Image(
        Objects.requireNonNull(getClass().getResourceAsStream(image + "red_general.png")));
    Image greenPlayer = new Image(
        Objects.requireNonNull(getClass().getResourceAsStream(image + "green_general.png")));
    Image purplePlayer = new Image(
        Objects.requireNonNull(getClass().getResourceAsStream(image + "pink_general.png")));
    double height = this.blueTeam.getHeight();
    double width = this.blueTeam.getWidth();

    ImageView blue = new ImageView(bluePlayer);
    ImageView red = new ImageView(redPlayer);
    ImageView green = new ImageView(greenPlayer);
    ImageView purple = new ImageView(purplePlayer);
    ImageView[] imgs = {blue, red, green, purple};
    for(ImageView i : imgs){
      i.setFitWidth(width);
      i.setFitHeight(width);
      System.out.println("width: " + width + "height: " + height);
    }

    this.blueTeam.getChildren().add(blue);
    this.redTeam.getChildren().add(red);
    this.greenTeam.getChildren().add(green);
    this.purpleTeam.getChildren().add(purple);

  }

  public void mouseEntered(MouseEvent mouseEvent) {
    HelperMethods.mouseEntered(mouseEvent);
    Image backBtn = new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonSmall_white.png")).toString());
    ImageView img = new ImageView(backBtn);

    img.setFitHeight(backPane.getHeight());
    img.setFitWidth(backPane.getWidth());
    backPane.getChildren().remove(0);
    backPane.getChildren().add(img);
  }

  public void mouseExited(MouseEvent mouseEvent) {
    HelperMethods.mouseExited(mouseEvent);
    Image backBtn = new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
    ImageView img = new ImageView(backBtn);

    img.setFitHeight(backPane.getHeight());
    img.setFitWidth(backPane.getWidth());
    backPane.getChildren().remove(0);
    backPane.getChildren().add(img);
  }
}
