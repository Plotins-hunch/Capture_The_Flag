package de.unimannheim.swt.pse.client.controller;



import de.unimannheim.swt.pse.client.HelperMethods;
import de.unimannheim.swt.pse.client.RequestHandler;
import de.unimannheim.swt.pse.server.controller.data.GameSessionResponse;
import de.unimannheim.swt.pse.server.controller.data.JoinGameRequest;
import de.unimannheim.swt.pse.server.controller.data.JoinGameResponse;
import de.unimannheim.swt.pse.server.database.DatabaseInitializer;
import de.unimannheim.swt.pse.server.database.controller.GameSessionsController;
import de.unimannheim.swt.pse.server.database.dao.GameSessionsDAO;
import de.unimannheim.swt.pse.server.database.service.GameSessionsService;
import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Team;
import de.unimannheim.swt.pse.server.game.state.Theme;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
/**
 * Controller for the Host Screen that is shown when the game is hosted and the host is waiting for players to join
 * It shows the game session id and the ip address of the host, as well as the amount of players that have joined
 * It also allows the host to add bots to the game
 */
public class HostScreenController {

  public Label playerAmt;
  public AnchorPane woodenBoard;
  public AnchorPane backPane;
  public Label backBtn;
  public AnchorPane ipCopyPane;
  public AnchorPane idCopyPane;
  @FXML
  private Label gameSessionIdLabel;
  private String gameSessionId;
  private RequestHandler requestHandler;

  @FXML
  private Label ipAdress;

  private String teamId;
  private String teamSecret;
  private MapTemplate tmpMap;
  public boolean interrupted = false;
  Thread gameStatusThread;
  private Theme theme;
  private MediaPlayer mediaPlayer;


  /**
   * Initializes the controller by setting the background images, showing ip and gameSessionId and starting the music
   * @author aemsbach
   */
  public void initialize() {
    //get ip address of host
    InetAddress localhost;
    try {
       localhost = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }
    //set background image
    this.ipAdress.sceneProperty().addListener((observable, oldValue, newValue) -> {
      if (oldValue == null && newValue != null) {
        Image img = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/landingScreen.png")).toString());
        newValue.getRoot().setStyle("-fx-background-image: url('" + img.getUrl() + "'); " +
            "-fx-background-size: cover; -fx-background-repeat: no-repeat;");
      }
    });
    //set background images
    this.woodenBoard.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/woodenBoard.png")).toString());
      ImageView imageView = new ImageView(img);
      imageView.setFitHeight(newValue.doubleValue());
      imageView.setFitWidth(this.woodenBoard.getWidth());
      this.woodenBoard.getChildren().add(imageView);
    });

    this.backPane.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
      ImageView imageView = new ImageView(img);
      imageView.setFitHeight(newValue.doubleValue());
      imageView.setFitWidth(this.backPane.getWidth());
      this.backPane.getChildren().add(imageView);
    });
    this.idCopyPane.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/copySymbol.png")).toString());
      ImageView imageView = new ImageView(img);
      imageView.setFitHeight(newValue.doubleValue());
      imageView.setFitWidth(this.idCopyPane.getWidth());
      this.idCopyPane.getChildren().add(imageView);
    });
    this.ipCopyPane.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/copySymbol.png")).toString());
      ImageView imageView = new ImageView(img);
      imageView.setFitHeight(newValue.doubleValue());
      imageView.setFitWidth(this.ipCopyPane.getWidth());
      this.ipCopyPane.getChildren().add(imageView);
    });
    this.backBtn.addEventHandler(MouseEvent.MOUSE_ENTERED, this::changeCursorToHand);
    this.backBtn.addEventHandler(MouseEvent.MOUSE_EXITED, this::changeCursorToDefault);
    this.ipAdress.setText(localhost.getHostAddress());
    //check whether game has started and how many players have joined
    Thread checkGameStatus = new Thread(this::checkGameStatus);
    checkGameStatus.setDaemon(true);
    checkGameStatus.start();
    gameStatusThread = checkGameStatus;

    this.ipAdress.addEventHandler(MouseEvent.MOUSE_ENTERED, this::changeCursorToHand);
    this.ipAdress.addEventHandler(MouseEvent.MOUSE_EXITED, this::changeCursorToDefault);
    this.gameSessionIdLabel.addEventHandler(MouseEvent.MOUSE_ENTERED, this::changeCursorToHand);
    this.gameSessionIdLabel.addEventHandler(MouseEvent.MOUSE_EXITED, this::changeCursorToDefault);
    this.ipCopyPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> copyIp());
    this.idCopyPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> copyGameSessionId());



  }
  /**
   * Changes the cursor to a hand when the mouse enters the ip address or game session id label
   * @author aemsbach
   * @param event the mouse event that triggered the action
   */
  private void changeCursorToHand(MouseEvent event){
    HelperMethods.mouseEntered(event);
    Image backBtn = new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonSmall_white.png")).toString());
    ImageView img = new ImageView(backBtn);
    Label label = (Label) event.getSource();
    img.setFitHeight(label.getHeight());
    img.setFitWidth(label.getWidth());
    if(label.getText().toString().equals("Back")){
      this.backPane.getChildren().remove(0);
      this.backPane.getChildren().add(0,img);}

  }
  /**
   * Changes the cursor back to the default when the mouse leaves the ip address or game session id label
   * @author aemsbach
   * @param event the mouse event that triggered the action
   */
  private void changeCursorToDefault(MouseEvent event){
    HelperMethods.mouseExited(event);
    Image backBtn = new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
    ImageView img = new ImageView(backBtn);
    Label label = (Label) event.getSource();
    img.setFitHeight(label.getHeight());
    img.setFitWidth(label.getWidth());
    if(label.getText().toString().equals("Back")){
      this.backPane.getChildren().remove(0);
      this.backPane.getChildren().add(0,img);}
  }


  /**
   * Builds a popup that allows the host to add bots to the game
   * @author aemsbach
   * @param ignoredEvent the action event that triggered the action
   * @throws IOException if the server cannot be reached
   */
  public void buildPopup(ActionEvent ignoredEvent) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/ChooseBot.fxml")));
    Parent root = fxmlLoader.load();
    ChooseBotController chooseBotController = fxmlLoader.getController();
    chooseBotController.passInfo(this.ipAdress.getText() ,this.gameSessionId);
    Stage popupStage = new Stage();
    Scene scene = new Scene(root);
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.setScene(scene);
    popupStage.show();

  }
  /**
   * Closes the host screen and switches to the select map screen when the back button is clicked
   * Also deletes the game session from the server and stops the game status thread
   * @author aemsbach
   * @param event the mouse event that triggered the action
   * @throws IOException if the server cannot be reached
   */
  @FXML
  void backToSelectMap(MouseEvent event) throws IOException {

    this.gameStatusThread.interrupt();
    this.requestHandler.deleteGameSession(this.gameSessionId);
    new DatabaseInitializer().initialize();
    GameSessionsDAO gameSessionsDAO = new GameSessionsDAO();
    GameSessionsService gameSessionsService = new GameSessionsService(gameSessionsDAO);
    GameSessionsController gameSessionsController = new GameSessionsController(gameSessionsService);

    // Delete a game session
    gameSessionsController.deleteGameSession(this.gameSessionId);

    FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/SelectMap.fxml")));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    SelectMapController selectMapController = fxmlLoader.getController();
    selectMapController.passInfo(this.requestHandler, this.mediaPlayer);


    Scene scene = new Scene(root);
    Screen screen = Screen.getPrimary();
    stage.setHeight(screen.getVisualBounds().getHeight() * 0.999);
    stage.setWidth(screen.getVisualBounds().getWidth() * 0.999);

    stage.setScene(scene);

  }

  /**
   * Method used to pass information to the host screen controller
   * @author aemsbach
   * @param gameSessionId the id of the game session
   * @param requestHandler the request handler used to send requests to the server
   * @param map the map that is played
   * @param teamId the id of the team
   * @param mediaPlayer the media player used to play music
   */
  public void passInfo(String gameSessionId, RequestHandler requestHandler , MapTemplate map, String teamId, MediaPlayer mediaPlayer){
    this.gameSessionId = gameSessionId;
    this.requestHandler = requestHandler;
    this.tmpMap = map;
    this.mediaPlayer = mediaPlayer;
    JoinGameRequest joinGameRequest = new JoinGameRequest();
    joinGameRequest.setTeamId(teamId);
    JoinGameResponse response = this.requestHandler.joinGame(this.gameSessionId, joinGameRequest);
    this.teamId = response.getTeamId();
    this.teamSecret = response.getTeamSecret();
    this.gameSessionIdLabel.setText(this.gameSessionId);

    Thread checkGameStatus = new Thread(this::checkGameStatus);
    checkGameStatus.setDaemon(true);
    checkGameStatus.start();
  }

  /**
   * Skips to the game board when the game has started and all the players are in the game
   * @throws IOException if the server cannot be reached or the fxml file is not found
   */
  private void skipToGameBoard() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/GameBoard.fxml")));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) (this.ipAdress.getScene().getWindow());
    GameBoard gameBoardController = fxmlLoader.getController();
    gameBoardController.passInfo(this.gameSessionId, this.teamId, this.teamSecret, this.requestHandler,
        mediaPlayer);

    Scene scene = new Scene(root);

    scene.getStylesheets().add(
        Objects.requireNonNull(getClass().getResource("/css/GameBoardStyle.css")).toExternalForm());
    stage.setScene(scene);
    Screen screen = Screen.getPrimary();
    stage.setHeight(screen.getVisualBounds().getHeight() * 0.999);
    stage.setWidth(screen.getVisualBounds().getWidth() * 0.999);
  }

  /**
   * Builds a popup that allows the host to add bots to the game
   * @author aemsbach
   * @param event the action event that triggered the action
   * @throws IOException if the server cannot be reached
   */
  @FXML
  void addBotScreen(ActionEvent event) throws IOException {
    buildPopup(event);
  }

  /**
   * Thread that checks if game has started and checks how many teams are in the game
   * If game has started, switch to game board
   * @author aemsbach
   */
  private void checkGameStatus() {
    while (!Thread.currentThread().isInterrupted()) {
      GameState gameState = this.requestHandler.getGameState(this.gameSessionId);
      this.theme = Theme.land;
      if (this.interrupted) {
        break;
      }
      int numOfTeams = 0;
      //Check if game has started by checking if start date is set
      GameSessionResponse gameSessionResponse = this.requestHandler.getGameSession(this.gameSessionId);
      if (gameSessionResponse.getGameStarted() != null) {
        Platform.runLater(() -> {
          try {
            skipToGameBoard();
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
        break;
        }


      //count number of teams
      for(Team t : gameState.getTeams()){
        if(t != null && !t.getId().equals("0")){
          numOfTeams++;
        }
      }
      int finalNumOfTeams = numOfTeams;
      Platform.runLater(() -> this.playerAmt.setText(finalNumOfTeams + "/" + this.tmpMap.getTeams()));
      try {
        Thread.sleep(1500);
      } catch (InterruptedException e) {
        break;
      }
    }
  }

  /**
   * Copies the ip to the clipboard
   * @author rkonrdat
   */
  public void copyIp()
  {
    Image img = new Image(Objects.requireNonNull(getClass().getResource("/assets/copiedSymbol.png")).toString());
    ImageView imageView = new ImageView(img);
    imageView.setFitHeight(this.ipCopyPane.getHeight());
    imageView.setFitWidth(this.ipCopyPane.getWidth());
    this.ipCopyPane.getChildren().remove(0);
    this.ipCopyPane.getChildren().add(imageView);
    ImageView img2 = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/assets/copySymbol.png")).toString()));
    img2.setFitHeight(this.idCopyPane.getHeight());
    img2.setFitWidth(this.idCopyPane.getWidth());
    this.idCopyPane.getChildren().remove(0);
    this.idCopyPane.getChildren().add(img2);
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(this.ipAdress.getText());
    clipboard.setContent(content);
  }
  /**
   * Copies the game session id to the clipboard
   * @author rkonrdat
   */
  public void copyGameSessionId()
  {
    Image img = new Image(Objects.requireNonNull(getClass().getResource("/assets/copiedSymbol.png")).toString());
    ImageView imageView = new ImageView(img);
    imageView.setFitHeight(this.idCopyPane.getHeight());
    imageView.setFitWidth(this.idCopyPane.getWidth());
    this.idCopyPane.getChildren().remove(0);
    this.idCopyPane.getChildren().add(imageView);

    ImageView img2 = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/assets/copySymbol.png")).toString()));
    img2.setFitHeight(this.ipCopyPane.getHeight());
    img2.setFitWidth(this.ipCopyPane.getWidth());
    this.ipCopyPane.getChildren().remove(0);
    this.ipCopyPane.getChildren().add(img2);

    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(this.gameSessionIdLabel.getText());
    clipboard.setContent(content);
  }


}

