package de.unimannheim.swt.pse.client.controller;

import de.unimannheim.swt.pse.client.RequestHandler;
import de.unimannheim.swt.pse.server.controller.data.GameSessionResponse;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Team;
import de.unimannheim.swt.pse.server.game.state.Theme;
import java.io.IOException;
import java.util.Objects;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
/**
 * Controller for the waiting room that is shown to guests when the game is not started and that shows the number of players in the game
 */
public class GuestWaitingRoomController {

  public Label playerAmt;
  public AnchorPane woodenBoard;

  private Theme theme;
  private String gameSessionId;
  private String teamId;
  private String teamSecret;
  private RequestHandler requestHandler;

  /**
   * Passes the necessary information to the controller
   * @author aemsbach
   * @param gameSessionId the id of the game session
   * @param teamId the id of the team
   * @param teamSecret the secret of the team
   * @param requestHandler the request handler used to send requests to the server
   */
  public void passInfo(String gameSessionId, String teamId, String teamSecret,  RequestHandler requestHandler) {

    this.gameSessionId = gameSessionId;
    this.teamId = teamId;
    this.teamSecret = teamSecret;
    this.requestHandler = requestHandler;
    Thread checkGameStatus = new Thread(this::checkGameStatus);
    checkGameStatus.setDaemon(true);
    checkGameStatus.start();
    //set background image
    this.playerAmt.sceneProperty().addListener((observable, oldValue, newValue) -> {
      if (oldValue == null && newValue != null) {
        Image img = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/landingScreen.png")).toString());
        newValue.getRoot().setStyle("-fx-background-image: url('" + img.getUrl() + "'); " +
            "-fx-background-size: cover; -fx-background-repeat: no-repeat;");
      }
    });
    //set button images
    this.woodenBoard.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/woodenBoard.png")).toString());
      ImageView imageView = new ImageView(img);
      imageView.setFitWidth(this.woodenBoard.getWidth());
      imageView.setFitHeight(this.woodenBoard.getHeight());
      this.woodenBoard.getChildren().add(imageView);
    });


  }


  /**
   * This method is used to skip to the game board when the game has started
   * @author aemsbach
   * @throws IOException if the FXML file is not found
   */
  private void skipToGameBoard() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/GameBoard.fxml")));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) (this.playerAmt.getScene().getWindow());
    GameBoard gameBoardController = fxmlLoader.getController();
    gameBoardController.passInfo(this.gameSessionId, this.teamId, this.teamSecret, this.requestHandler,
        null);

    Scene scene = new Scene(root);


    stage.setScene(scene);
    Screen screen = Screen.getPrimary();
    stage.setHeight(screen.getVisualBounds().getHeight() * 0.999);
    stage.setWidth(screen.getVisualBounds().getWidth() * 0.999);

  }
  /**
   * This thread is used to check the status of the game and switch to the game board when the game has started
   * @author aemsbach
   */
  private void checkGameStatus() {

    while (true) {
      int numOfTeams = 0;
      GameState gameState = this.requestHandler.getGameState(this.gameSessionId);
      this.theme = Theme.land;
      //Check if game has started
      GameSessionResponse gameSessionResponse = this.requestHandler.getGameSession(this.gameSessionId);
      if (gameSessionResponse.getGameStarted() != null) {
        Platform.runLater(() -> {
            try {
              this.skipToGameBoard();
            } catch (IOException e) {
              e.printStackTrace();
            }
          });
          break;
      }


      for(Team team : gameState.getTeams()){
        if(team != null ) {
          if(!team.getId().equals("0"))
            numOfTeams++;
        }
        int finalNumOfTeams = numOfTeams;

        Platform.runLater(() -> this.playerAmt.setText(finalNumOfTeams + "/" + gameState.getTeams().length));
      }
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        break;
      }
    }}
}


