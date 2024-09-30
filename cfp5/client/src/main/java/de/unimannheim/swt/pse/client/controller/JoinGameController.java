package de.unimannheim.swt.pse.client.controller;

import de.unimannheim.swt.pse.client.HelperMethods;
import de.unimannheim.swt.pse.client.RequestHandler;
import de.unimannheim.swt.pse.server.controller.data.JoinGameRequest;
import de.unimannheim.swt.pse.server.controller.data.JoinGameResponse;
import de.unimannheim.swt.pse.server.database.DatabaseInitializer;
import de.unimannheim.swt.pse.server.database.controller.GameSessionsController;
import de.unimannheim.swt.pse.server.database.dao.GameSessionsDAO;
import de.unimannheim.swt.pse.server.database.model.GameSessionsModel;
import de.unimannheim.swt.pse.server.database.service.GameSessionsService;
import de.unimannheim.swt.pse.server.game.exceptions.GameSessionNotFound;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
/**
 * This class is used to control the JoinGame.fxml file.
 * It is used to join a game session by entering the IP address of the host and the gameSession ID.
 * It sends a request to the server to join the game session and switches to the waiting room.
 */
public class JoinGameController {

  public AnchorPane backPane;
  public AnchorPane connectPane;
  public Label joinGameLbl;
  public TextField joinGameField;
  public AnchorPane spectateBtn;
  public AnchorPane playBtn;
  public ComboBox sessionBox;
  public SplitPane splitPane;
  @FXML
  private AnchorPane backBtn;
  @FXML
  private TextField gameSessionField;


  /**
   * This method is used to switch back to the landing page when the back button is clicked
   * @author aemsbach
   * @param event ActionEvent which is the event of clicking the back button
   * @throws IOException if the FXML file is not found
   */
  @FXML
  void backToLandingPage(MouseEvent event) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/LandingPage.fxml")));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    Scene scene = new Scene(root);
    Screen screen = Screen.getPrimary();
    stage.setHeight(screen.getVisualBounds().getHeight() * 0.999);
    stage.setWidth(screen.getVisualBounds().getWidth() * 0.999);
    stage.setScene(scene);
  }



  /**
   * This method is used to switch to the waiting room after joining a game session
   * @author aemsbach
   * @param gameSessionId String which is the id of the game session
   * @param teamId String which is the id of the team
   * @param teamSecret String which is the secret of the team
   * @param requestHandler RequestHandler which is used to send requests to the server
   * @throws IOException if the FXML file is not found
   */
  private void switchToWaitingRoom(String gameSessionId, String teamId, String teamSecret, RequestHandler requestHandler) throws IOException {

    FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/GuestWaitingRoom.fxml")));
    Parent root = fxmlLoader.load();
    GuestWaitingRoomController guestWaitingRoomController = fxmlLoader.getController();
    guestWaitingRoomController.passInfo(gameSessionId, teamId, teamSecret, requestHandler);


    Stage stage = (Stage) this.backPane.getScene().getWindow();

    Scene scene = new Scene(root);

    stage.setScene(scene);

    Screen screen = Screen.getPrimary();
    stage.setHeight(screen.getVisualBounds().getHeight() * 0.999);
    stage.setWidth(screen.getVisualBounds().getWidth() * 0.999);
  }

  /**
   * This method sends a request to the server to join a game session given the IP address and gameSessionId
   * @author aemsbach
   * @throws IOException if the IP address or gameSessionId is invalid
   **/
  private void join(String teamId) throws IOException {
    //Check that the fields are not empty
    if(this.joinGameField.getText().isEmpty() || this.gameSessionField.getText().isEmpty()){
      return;
    }
    String IP = this.joinGameField.getText();
    String gameSessionId = this.gameSessionField.getText();
    //Set up the request handler as a guest
    String baseUrl = "http://" + IP + ":8888/";
    RequestHandler requestHandler = new RequestHandler();
    requestHandler.setBaseUrl(baseUrl);
    //Send the request to join the game session
    JoinGameRequest joinGameRequest = new JoinGameRequest();
    joinGameRequest.setTeamId(teamId);
    try {
      JoinGameResponse response = requestHandler.joinGame(gameSessionId, joinGameRequest);
      //Switch to the waiting room if the request was successful
      System.out.println("responseId:" + response.getTeamId());
      this.switchToWaitingRoom(response.getGameSessionId(), response.getTeamId(), response.getTeamSecret(), requestHandler);
    }
    catch (GameSessionNotFound e) {
      //Ask user for a valid gameSessionId
      this.gameSessionField.setText("");
      this.gameSessionField.setPromptText("GameSession not found");
    }

  }

  /**
   * This method is used to join a game session as a player when the play button is clicked
   * @author aemsbach
   * @param ignoredE MouseEvent which is the event of clicking the play button
   * @throws IOException if the FXML file is not found
   */
  public void joinAsPlayer(MouseEvent ignoredE) throws IOException {
    this.join("Team1");
  }
  /**
   * This method is used to join a game session as a spectator when the spectate button is clicked
   * @author aemsbach
   * @param ignoredE MouseEvent which is the event of clicking the spectate button
   * @throws IOException if the FXML file is not found
   */
  public void joinAsSpectator(MouseEvent ignoredE) throws IOException {
    this.join("Spectator");
  }

  /**
   * This method is used to initialize the controller by setting the background image, button images and text field prompts
   *
   * @author aemsbach
   */
  public void initialize() throws IOException {
    this.joinGameField.setPromptText("Enter IP...");
    this.gameSessionField.setPromptText("Enter GameSession ID...");
    //set background image
    this.splitPane.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image img = new Image(Objects.requireNonNull(getClass().getResource("/assets/background.png")).toString());
      this.splitPane.setStyle("-fx-background-image: url('" + img.getUrl() + "'); " +
          "-fx-background-size: cover; -fx-background-repeat: no-repeat;");

    });
    //set button images
    this.backBtn.heightProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        Image img = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/buttonklein.png")).toString());
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(this.backBtn.getHeight());
        imgView.setFitWidth(this.backBtn.getWidth());
        this.backBtn.getChildren().add(imgView);
    }});
    this.playBtn.heightProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        Image img = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/buttonklein.png")).toString());
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(this.playBtn.getHeight());
        imgView.setFitWidth(this.playBtn.getWidth());
        this.playBtn.getChildren().add(imgView);
    }});
    this.spectateBtn.heightProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        Image img = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/buttonklein.png")).toString());
        ImageView imgView = new ImageView(img);
        imgView.setFitHeight(this.spectateBtn.getHeight());
        imgView.setFitWidth(this.spectateBtn.getWidth());
        this.spectateBtn.getChildren().add(imgView);
    }});
    new DatabaseInitializer().initialize();
    GameSessionsDAO gameSessionsDAO = new GameSessionsDAO();
    GameSessionsService gameSessionsService = new GameSessionsService(gameSessionsDAO);
    GameSessionsController gameSessionsController = new GameSessionsController(gameSessionsService);

    // Retrieve all game sessions
    try {
      CompletableFuture<List<GameSessionsModel>> sessionsFuture = gameSessionsController.getAllCurrentGameSessions();
      List<GameSessionsModel> sessions = sessionsFuture.get(); // Blocking call to wait for session retrieval
      ObservableList<GameSessionsModel> gameSessions = FXCollections.observableArrayList(sessions);
      this.sessionBox.setItems(gameSessions);
      this.sessionBox.valueProperty().addListener((observable, oldValue, newValue) -> {
        GameSessionsModel gameSessionId = (GameSessionsModel)(newValue);
        GameSessionsModel ip = (GameSessionsModel)(newValue);
        this.gameSessionField.setText(gameSessionId.getSessionId());
        this.joinGameField.setText(ip.getIp());

      });

      System.out.println("Game sessions fetched successfully: " + sessions);
    } catch (InterruptedException | ExecutionException e) {
      System.err.println("Error during game session retrieval: " + e.getMessage());
    }

  }
  /**
   * Changes the cursor to a hand when the mouse enters the back button
   * @author aemsbach
   * @param mouseEvent the mouse event that triggered the action
   */
  public void mouseEntered(MouseEvent mouseEvent) {
    HelperMethods.mouseEntered(mouseEvent);
    Image backBtn = new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonSmall_white.png")).toString());
    ImageView img = new ImageView(backBtn);
    Label label = (Label) mouseEvent.getSource();
    img.setFitHeight(label.getHeight());
    img.setFitWidth(label.getWidth());
    if(label.getText().equals("Play")){
      this.playBtn.getChildren().remove(0);
      this.playBtn.getChildren().add(0,img);}
    else if(label.getText().equals("Spectate")){
      this.spectateBtn.getChildren().remove(0);
      this.spectateBtn.getChildren().add(0,img);
    }
    else{
      this.backBtn.getChildren().remove(0);
      this.backBtn.getChildren().add(0,img);

    }
  }
  /**
   * Changes the cursor back to the default when the mouse leaves the back button
   * @author aemsbach
   * @param mouseEvent the mouse event that triggered the action
   */
  public void mouseExited(MouseEvent mouseEvent) {
    HelperMethods.mouseExited(mouseEvent);
    Image backBtn = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
    ImageView img = new ImageView(backBtn);
    Label label = (Label) mouseEvent.getSource();
    img.setFitHeight(label.getHeight());
    img.setFitWidth(label.getWidth());
    if (label.getText().equals("Play")) {
      this.playBtn.getChildren().remove(0);
      this.playBtn.getChildren().add(0, img);
    } else if (label.getText().equals("Spectate")) {
      this.spectateBtn.getChildren().remove(0);
      this.spectateBtn.getChildren().add(0, img);
    } else {
      this.backBtn.getChildren().remove(0);
      this.backBtn.getChildren().add(0, img);
    }
  }
}

