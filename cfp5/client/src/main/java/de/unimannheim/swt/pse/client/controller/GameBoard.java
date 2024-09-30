package de.unimannheim.swt.pse.client.controller;

import de.unimannheim.swt.pse.client.RequestHandler;
import de.unimannheim.swt.pse.server.CtfApplication;
import de.unimannheim.swt.pse.server.controller.data.GameSessionResponse;
import de.unimannheim.swt.pse.server.controller.data.GiveupRequest;
import de.unimannheim.swt.pse.server.controller.data.MoveRequest;
import de.unimannheim.swt.pse.server.game.exceptions.InvalidMove;
import de.unimannheim.swt.pse.server.game.map.Directions;
import de.unimannheim.swt.pse.server.game.map.Shape;
import de.unimannheim.swt.pse.server.game.map.ShapeType;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import de.unimannheim.swt.pse.server.game.state.Piece;
import de.unimannheim.swt.pse.server.game.state.Team;
import de.unimannheim.swt.pse.server.game.state.Theme;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Controller for the game board that is shown to play the game

 */
public class GameBoard {

  public Label opponentPiecePower;
  public Label oppPower;
  public Label myPiecePower;
  public AnchorPane myPowerPane;
  public Label myPower;
  public AnchorPane oppPiecePane;
  public AnchorPane myBack1;
  public AnchorPane myBack2;
  public AnchorPane opponentback1;
  public AnchorPane opponentback2;
  public AnchorPane oppPowerBg;
  public AnchorPane myPowerBg;
  public Button surrenderButton;
  public GridPane borderOpp;
  public GridPane myBorder;
  public HBox team2Flags;
  public HBox team3Flags;
  public HBox team4Flags;
  public HBox team1Flags;
  @FXML
  private AnchorPane basePane;
  /**
   * The label that shows the time left in the game
   */
  public Label gameClock;
  /**
   * The pane in which the game board lies
   */
  @FXML
  private AnchorPane gameBoard;
  private String[][] grid = new String[][]{{""}};

  /**
   * The game board
   */
  private GridPane board = new GridPane();

  /**
   * The team playing the game and being controlled by the user
   */
  private Team team = new Team();
  /**
   * The team secret
   */
  private String teamSecret;

  /**
   * The label that shows the time left in the game
   */
  @FXML
  private Label gameTime;
  /**
   * The label that is showed when it's my turn
   */
  @FXML
  private Label myClock;
  /**
   * The label that shows the time left for the move
   */
  @FXML
  private Label myMoveTime;
  /**
   * The label that shows the time left for the opponent
   */
  @FXML
  private Label opponentClock;
  /**
   * The label that shows the time left for the opponent's move
   */
  @FXML
  private Label opponentMoveTime;

  /**
   * The target tile of the piece that is being moved
   */
  private final int[] targetTile = new int[2];
  /**
   * The piece that is being moved
   */
  private String pieceClicked = "-1";
  /**
   * The game session id
   */
  private String gameSessionId;
  /**
   * The request handler
   */
  private RequestHandler requestHandler;

  /**
   * The possible moves of the piece
   */
  private final ArrayList<Rectangle> possibleMoves = new ArrayList<>();
  /**
   * The helper class for the game board
   */
  private final GameBoardHelper helper = new GameBoardHelper();
  /**
   * The team array of the game
   */
  private Team[] teams;
  /**
   * The theme of the game
   */
  private Theme theme;
  /**
   * The media player for the music
   */
  private MediaPlayer mediaPlayer;
  /**
   * The images of the blocks
   */
  private Image[] blocks;

  private int moveCount;

  private int initPieceCount;


  /**
   * Initializes the game board
   *
   * @author aemsbach
   */
  @FXML
  public void initialize() {
    //Set the images of the blocks by choosing them randomly from the block images

    //Hide all power labels
    this.myPiecePower.setVisible(false);
    this.myPower.setVisible(false);
    this.myPowerBg.setVisible(false);
    this.opponentPiecePower.setVisible(false);
    this.oppPower.setVisible(false);
    this.oppPowerBg.setVisible(false);
    this.borderOpp.setVisible(false);


    this.gameBoard.getChildren().add(board);

    this.gameBoard.widthProperty().addListener((obs, oldVal, newVal) -> updateBoard());
    this.gameBoard.heightProperty().addListener((obs, oldVal, newVal) -> updateBoard());


  }

  /**
   * Constructs the game board with the grid set in the gameGrid variable
   *
   * @author aemsbach
   */
  public void constructBoard() {

    //set correct background
    switch (this.theme) {
      case land:
        Image img = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/mapGras.png"))
                .toString());
        this.basePane.setStyle(
            "-fx-background-image: url('" + img.getUrl() + "'); "
                + "-fx-background-size: cover; -fx-background-repeat: no-repeat;");
        Image[] blocks = new Image[3];
        String blck = "/assets/blocks/block";
        for (int i = 0; i < blocks.length; i++) {
          blocks[i] = new Image(
              Objects.requireNonNull(getClass().getResourceAsStream(blck + (i + 1) + ".png")));

        }
        //Set the images of the blocks
        this.blocks = blocks;
        if (mediaPlayer != null) {



          while (mediaPlayer.getVolume() > 0.1) {
            mediaPlayer.setVolume(mediaPlayer.getVolume() - 0.1);

            try {
              Thread.sleep(10);

            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }

          }
          //play the game music
          mediaPlayer.stop();
          mediaPlayer.dispose();
          MediaPlayer gameMusic = new MediaPlayer(new Media(
              Objects.requireNonNull(getClass().getResource("/music/Land.mp3")).toString()));

          gameMusic.setCycleCount(MediaPlayer.INDEFINITE);
          gameMusic.play();
          this.mediaPlayer = gameMusic;
        }
        break;

      case sea:
        Image[] seaBlocks = new Image[4];
        String seaBlock = "/assets/blocks/water_block";
        for (int i = 0; i < seaBlocks.length; i++) {
          seaBlocks[i] = new Image(
              Objects.requireNonNull(getClass().getResourceAsStream(seaBlock + (i + 1) + ".png")));

        }
        //Set the images of the blocks
        this.blocks = seaBlocks;
        Image seaImg = new Image(
            Objects.requireNonNull(getClass().getResource("/assets/mapWater.png"))
                .toString());
        this.basePane.setStyle(
            "-fx-background-image: url('" + seaImg.getUrl() + "'); "
                + "-fx-background-size: cover; -fx-background-repeat: no-repeat;");

        if (mediaPlayer != null) {
          mediaPlayer.stop();
          mediaPlayer.dispose();
          MediaPlayer gameMusic = new MediaPlayer(new Media(
              Objects.requireNonNull(getClass().getResource("/music/Sea.mp3")).toString()));
          gameMusic.setCycleCount(MediaPlayer.INDEFINITE);
          gameMusic.play();
          this.mediaPlayer = gameMusic;
        }
        break;
    }

    //Set important variables
    this.board.getChildren().removeAll(this.board.getChildren());
    this.board = new GridPane();
    String teamId = this.team.getId();
    Team t = this.team;
    Team[] teams = this.teams;
    //Calculate size of GameBoard Pane
    Rectangle2D screen = javafx.stage.Screen.getPrimary().getBounds();
    double width = screen.getWidth() / 2;
    double height = screen.getHeight();
    int rows = this.grid.length;
    int cols = this.grid[0].length;

    //Calculate size of tiles
    double tileMeasure = Math.min(width / cols, height / rows);
    //turn grid according to team for correct perspective
    switch (teamId) {
      case "2" -> this.turnGrid180();
      case "4" -> this.turnGrid90();
      case "3" -> this.turnGrid270();
    }
    //Construct the individual tiles of the gameBoard
    for (int i = 0; i < this.grid.length; i++) {
      for (int j = 0; j < this.grid[i].length; j++) {
        String field = grid[i][j];

        StackPane tile = new StackPane();
        Rectangle rect = new Rectangle(tileMeasure, tileMeasure);
        rect.setFill(Color.WHITE);
        rect.setOpacity(0.3);
        rect.setStroke(javafx.scene.paint.Color.BLACK);
        int finalJ = j;
        int finalI = i;
        rect.setId(field);
        //Set event handler for clicking on the tile
        rect.addEventHandler(MouseEvent.MOUSE_CLICKED,
            (MouseEvent event) -> this.handleRectClick(finalI, finalJ, t));

        //Set the style of the tile
        this.setTileStyle(rect);
        tile.setPickOnBounds(false);

        //Add the tile to the board
        tile.getChildren().add(rect);
        //Match the piece, base or block to the image
        if (teams != null) {
          if (field.matches("p.*")) {
            String pieceId = field.split("_")[1];
            String currentTeamId = field.split("_")[0].split(":")[1];
            this.helper.matchPieceToImage(pieceId, currentTeamId, tile, teams, tileMeasure, finalI,
                finalJ, t);
          } else if (field.matches("b:.*")) {
            this.helper.matchBaseToImage(tile, String.valueOf(field.charAt(2)), tileMeasure, finalI, finalJ, t);
          }
          if (field.length() == 1) {
            this.helper.matchBlockToImage(tile, tileMeasure);
          }
        }
        board.add(tile, j, i);
        this.board.setGridLinesVisible(true);
      }
    }

  }

  /**
   * Passes the game session id, team id and team secret to the game board controller
   *
   * @param gameSessionId  the game session id
   * @param teamId         the team id
   * @param teamSecret     the team secret
   * @param requestHandler the request handler
   * @author aemsbach
   */
  public void passInfo(String gameSessionId, String teamId, String teamSecret,
      RequestHandler requestHandler, MediaPlayer mediaPlayer) {
    this.team = new Team();
    this.mediaPlayer = mediaPlayer;

    this.gameSessionId = gameSessionId;

    this.team.setId(teamId);
    this.teamSecret = teamSecret;
    this.requestHandler = requestHandler;

    if (teamId.equals("0")) {

      this.surrenderButton.setText("Leave Game");
    }

    switch (teamId) {
      case "1":
        this.team.setColor("blue");
        break;
      case "2":
        this.team.setColor("red");
        break;
      case "3":
        this.team.setColor("green");
        break;
      case "4":
        this.team.setColor("pink");
        break;
    }

    //Start game thread
    Thread gameThread = new Thread(this::updateGameInfo);
    gameThread.setDaemon(true);
    gameThread.start();


    String color = this.team.getColor();
    color = switch (color) {
      case "blue" -> "#3A6F9E";
      case "red" -> "#a90505";
      case "green" -> "#5FA051";
      case "pink" -> "#684c6b";
      default -> color;
    };

    this.myClock.setStyle("-fx-text-fill: " + color);
    this.myMoveTime.setStyle("-fx-text-fill: " + color);


  }

  /**
   * Centers the game board in the pane when board is resized
   *
   * @author aemsbach
   */
  private void updateBoard() {
    //calculate center of game board pane
    double centerX = (this.gameBoard.getWidth() - this.board.getWidth()) / 2;
    double centerY = (this.gameBoard.getHeight() - this.board.getHeight()) / 2;
    //set the center of the game board
    AnchorPane.setTopAnchor(this.board, centerY);
    AnchorPane.setLeftAnchor(this.board, centerX);

  }

  /**
   * Opens the surrender dialog when the surrender button is clicked
   *
   * @param event ActionEvent triggered when user clicks on surrender button
   * @throws IOException if the fxml file cannot be loaded
   * @author aemsbach
   */
  @FXML
  void surrender(ActionEvent event) throws IOException {
    if (this.team.getId().equals("0")) {
      FXMLLoader fxmlLoader = new FXMLLoader(
          Objects.requireNonNull(getClass().getResource("/fxml/LandingPage.fxml")));
      LandingPageController controller = new LandingPageController();
      try {
        CtfApplication.kill();
        this.requestHandler.deleteGameSession(this.gameSessionId);
      } catch (Exception ignored) {
      }
      if (this.mediaPlayer != null) {
        this.mediaPlayer.stop();
        this.mediaPlayer.dispose();
        MediaPlayer mediaPlayer = new MediaPlayer(new Media(
            Objects.requireNonNull(getClass().getResource("/music/MainMenu.mp3")).toString()));
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
        this.mediaPlayer = mediaPlayer;
        controller.passInfo(this.mediaPlayer);
      }
      Parent root = fxmlLoader.load();
      Stage stage = (Stage) this.basePane.getScene().getWindow();
      Scene scene = new Scene(root);
      stage.setScene(scene);
      return;
    }

    FXMLLoader fxmlLoader = new FXMLLoader(
        Objects.requireNonNull(getClass().getResource("/fxml/surrenderDialog.fxml")));
    Parent root = fxmlLoader.load();
    SurrenderDialogController controller = fxmlLoader.getController();
    controller.setGameBoardController(this);
    Stage popupStage = new Stage();
    popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
    popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
    Scene scene = new Scene(root);
    popupStage.setScene(scene);
    popupStage.show();

  }

  /**
   * Send a give up request to the server and switch to the game over screen
   *
   * @throws IOException if the fxml file cannot be loaded
   * @author aemsbach
   */
  void sendGiveUpRequest() throws IOException {
    //send giveUpRequest to server
    GiveupRequest giveUp = new GiveupRequest();
    giveUp.setTeamId(this.team.getId());
    giveUp.setTeamSecret(this.teamSecret);
    requestHandler.giveUp(this.gameSessionId, giveUp);
    Stage stage = (Stage) this.basePane.getScene().getWindow();

    //Switches to game over page
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/GameOverScreen.fxml"));
    fxmlLoader.load();
    if (this.mediaPlayer != null)
      this.mediaPlayer.stop();

    Scene scene = new Scene(fxmlLoader.getRoot());

    stage.setScene(scene);
    Screen screen = Screen.getPrimary();
    stage.setHeight(screen.getVisualBounds().getHeight());
    stage.setWidth(screen.getVisualBounds().getWidth());
    GameOverController controller = fxmlLoader.getController();
    controller.passInfo(requestHandler, gameSessionId, new String[]{"surrender"}, this.team.getId(),
        this.mediaPlayer, this.theme, this.moveCount,
        Objects.requireNonNull(this.helper.getTeam(this.team.getId())),
        this.initPieceCount);

  }

  /**
   * Sets the style of the tile according to the field
   *
   * @param tile  the tile
   * @author aemsbach
   */
  private void setTileStyle(Rectangle tile) {

    tile.setFill(Color.WHITE);
    //bases are black
  }

  /**
   * Turns the grid 180 degrees
   *
   * @author aeemsbach
   */
  private void turnGrid180() {
    //calculate middle column and row
    int middleCol = this.grid[0].length / 2;
    int middleRow = this.grid.length / 2;
    //mirror the grid on the y-axis
    for (int i = 0; i < this.grid.length; i++) {
      for (int j = 0; j < middleCol; j++) {
        String temp = this.grid[i][j];
        this.grid[i][j] = this.grid[i][this.grid[0].length - 1 - j];
        this.grid[i][this.grid[0].length - 1 - j] = temp;
      }
    }
    //mirror the grid on the x-axis
    for (int i = 0; i < this.grid[0].length; i++) {
      for (int j = 0; j < middleRow; j++) {
        String temp = this.grid[j][i];
        this.grid[j][i] = this.grid[this.grid.length - 1 - j][i];
        this.grid[this.grid.length - 1 - j][i] = temp;
      }
    }
  }

  /**
   * Turns the grid 90 degrees clockwise
   *
   * @author aemsbach
   */
  private void turnGrid90() {
    int rows = this.grid.length;
    int cols = this.grid[0].length;
    String[][] newGrid = new String[cols][rows];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        newGrid[j][rows - 1 - i] = this.grid[i][j];
      }
    }
    this.grid = newGrid;
  }

  /**
   * Turns the grid 270 degrees clockwise
   *
   * @author aemsbach
   */
  private void turnGrid270() {
    int rows = this.grid.length;
    int cols = this.grid[0].length;
    String[][] newGrid = new String[cols][rows];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        newGrid[cols - 1 - j][i] = this.grid[i][j];
      }
    }
    this.grid = newGrid;
  }

  /**
   * Handles the click on a tile on the game board by showing possible moves of piece or moving
   * piece to target tile
   *
   * @param row  the row of the tile
   * @param col  the column of the tile
   * @param team the team that is making the move
   * @author aemsbach
   */
  private void handleRectClick(int row, int col, Team team) {
    //check if it's my turn
    if (!this.myMoveTime.isVisible()) {
      return;
    }
    //get the tile that was clicked
    StackPane tileClicked = this.helper.getTile(row, col);
    //check if a piece is already clicked
    //If a piece has been clicked, set the target tile of the piece and try to move the piece
    if (!this.pieceClicked.equals("-1")) {
      //calculate the non-rotated row and column of the target tile
      int tmp = row;
      switch (team.getId()) {
        case "1":
          break;
        case "2":
          row = this.grid.length - 1 - row;
          col = this.grid[0].length - 1 - col;
          break;
        case "4":
          row = this.grid.length - 1 - col;
          col = tmp;
          break;
        case "3":
          row = col;
          col = this.grid[0].length - 1 - tmp;
          break;
      }
      //Set target tile coordinates
      this.targetTile[0] = row;
      this.targetTile[1] = col;
      //Move the piece
      MoveRequest move = new MoveRequest();
      move.setNewPosition(targetTile);
      String split = this.pieceClicked.split("_")[1];
      move.setPieceId(split);
      move.setTeamId(team.getId());
      move.setTeamSecret(this.teamSecret);
      this.moveCount++;
      try {
        requestHandler.makeMove(this.gameSessionId, move);
      } catch (InvalidMove ignored) {

      }

      //reset the pieceClicked variable
      this.pieceClicked = "-1";
      //reset the possible moves
      for (Rectangle rect : possibleMoves) {
        assert tileClicked != null;
        this.setTileStyle(rect);
      }
      possibleMoves.clear();

    } else {
      //If no piece has been clicked:
      //check if the tile clicked contains a piece of the player's team
      assert tileClicked != null;
      if (tileClicked.getChildren().size() < 2
          || (tileClicked.getChildren().get(0)).getId().isEmpty()
          || (tileClicked.getChildren().get(0)).getId().charAt(0) != 'p'
          || (tileClicked.getChildren().get(0)).getId().charAt(2) != team.getId()
          .charAt(0)) {

        return;
      }
      //Set the pieceClicked variable to the current piece
      pieceClicked = (tileClicked.getChildren().get(0)).getId();

      //Get all the pieces of a team
      Piece[] teamPieces = team.getPieces();
      if (teamPieces.length > 0) {
        Piece pieceClick;
        //Get the actual piece object that was clicked
        Optional<Piece> p = Arrays.stream(teamPieces)
            .filter(piece -> (piece != null) && (piece.getId().equals("" + pieceClicked.charAt(4))))
            .findFirst();
        if (p.isEmpty()) {

          return;
        }
        pieceClick = p.get();

        this.handlePieceHover(pieceClicked, team.getId(), team);

        //Get the possible directions of the piece and check if they exist
        Directions dir = pieceClick.getDescription().getMovement().getDirections();
        if (dir != null) {
          //Show all possible moves of the piece on the board
          //Left, right, up, down, upLeft, upRight, downLeft, downRight
          for (int i = 1; i <= dir.getLeft(); i++) {
            if (col - i >= 0) {
              StackPane tile = this.helper.getTile(row, col - i);
              assert tile != null;
              helper.setPossibleMoveStyle(possibleMoves, tile, team.getId());
              if (!tile.getChildren().get(0).getId().isEmpty()) {
                break;
              }

            }
          }
          for (int i = 1; i <= dir.getRight(); i++) {
            if (col + i < this.grid[0].length) {
              StackPane tile = this.helper.getTile(row, col + i);
              assert tile != null;
              helper.setPossibleMoveStyle(possibleMoves, tile, team.getId());
              if (!tile.getChildren().get(0).getId().isEmpty()) {
                break;
              }
            }
          }
          for (int i = 1; i <= dir.getUp(); i++) {
            if (row - i >= 0) {
              StackPane tile = this.helper.getTile(row - i, col);
              assert tile != null;
              helper.setPossibleMoveStyle(possibleMoves, tile, team.getId());
              if (!tile.getChildren().get(0).getId().isEmpty()) {
                break;
              }
            }
          }
          for (int i = 1; i <= dir.getDown(); i++) {
            if (row + i < this.grid.length) {
              StackPane tile = this.helper.getTile(row + i, col);
              assert tile != null;
              helper.setPossibleMoveStyle(possibleMoves, tile, team.getId());
              if (!tile.getChildren().get(0).getId().isEmpty()) {
                break;
              }
            }
          }
          for (int i = 1; i <= dir.getUpLeft(); i++) {
            if (row - i >= 0 && col - i >= 0) {
              StackPane tile = this.helper.getTile(row - i, col - i);
              assert tile != null;
              helper.setPossibleMoveStyle(possibleMoves, tile, team.getId());
              if (!tile.getChildren().get(0).getId().isEmpty()) {
                break;
              }
            }
          }
          for (int i = 1; i <= dir.getUpRight(); i++) {
            if (row - i >= 0 && col + i < this.grid[0].length) {
              StackPane tile = this.helper.getTile(row - i, col + i);
              assert tile != null;
              helper.setPossibleMoveStyle(possibleMoves, tile, team.getId());
              if (!tile.getChildren().get(0).getId().isEmpty()) {
                break;
              }
            }
          }
          for (int i = 1; i <= dir.getDownLeft(); i++) {
            if (row + i < this.grid.length && col - i >= 0) {
              StackPane tile = this.helper.getTile(row + i, col - i);
              assert tile != null;
              helper.setPossibleMoveStyle(possibleMoves, tile, team.getId());
              if (!tile.getChildren().get(0).getId().isEmpty()) {
                break;
              }
            }
          }
          for (int i = 1; i <= dir.getDownRight(); i++) {
            if (row + i < this.grid.length && col + i < this.grid[0].length) {
              StackPane tile = this.helper.getTile(row + i, col + i);
              assert tile != null;
              helper.setPossibleMoveStyle(possibleMoves, tile, team.getId());
              if (!tile.getChildren().get(0).getId().isEmpty()) {
                break;
              }
            }
          }

        }
        //Do this if the piece moves in a lshape
        else {
          Shape shape = pieceClick.getDescription().getMovement().getShape();
          //Show all possible moves of the piece on the board
          if (shape.getType().equals(ShapeType.lshape)) {
            for (int i = 1; i <= 2; i++) {
              for (int j = 1; j <= 2; j++) {
                if (i == j) {
                  continue;
                }
                if (row - i >= 0 && col - j >= 0) {
                  StackPane tile = this.helper.getTile(row - i, col - j);
                  if(checkLMove(tileClicked, tile)){
                    assert tile != null;

                    helper.setPossibleMoveStyle(possibleMoves, tile, team.getId());
                  }

                }
                if (row - i >= 0 && col + j < this.grid[0].length) {
                  StackPane tile = this.helper.getTile(row - i, col + j);
                  if(checkLMove(tileClicked, tile)){
                    assert tile != null;
                    helper.setPossibleMoveStyle(possibleMoves, tile, team.getId());
                  }

                }
                if (row + i < this.grid.length && col - j >= 0) {
                  StackPane tile = this.helper.getTile(row + i, col - j);
                  if(checkLMove(tileClicked, tile)){
                    assert tile != null;
                    helper.setPossibleMoveStyle(possibleMoves, tile, team.getId());
                  }

                }
                if (row + i < this.grid.length && col + j < this.grid[0].length) {
                  StackPane tile = this.helper.getTile(row + i, col + j);
                  if(checkLMove(tileClicked, tile)){
                    assert tile != null;
                    helper.setPossibleMoveStyle(possibleMoves, tile, team.getId());
                  }

                }
              }
            }
          }
        }
      }

    }
  }

  /**
   * Checks if there is a piece in the way of the l-move
   * @author aemsbach
   * @param sourceTile the source tile
   * @param targetTile the target tile
   * @return true if the move is valid, false otherwise
   */
  private boolean checkLMove(StackPane sourceTile, StackPane targetTile) {
    int verticalSteps =
        GridPane.getRowIndex(sourceTile) - GridPane.getRowIndex(targetTile);
    int horizontalSteps =
        GridPane.getColumnIndex(sourceTile) - GridPane.getColumnIndex(targetTile);
    int absHorizontalSteps = Math.abs(horizontalSteps);
    if (absHorizontalSteps == 2) {
      // right
      if (horizontalSteps < 0) {
        if (
            !Objects.requireNonNull(this.helper.getTile(GridPane.getRowIndex(sourceTile),
                GridPane.getColumnIndex(sourceTile) + 1)).getChildren().get(0).getId().isEmpty()
                || !Objects.requireNonNull(this.helper.getTile(GridPane.getRowIndex(sourceTile),
                GridPane.getColumnIndex(sourceTile) + 2)).getChildren().get(0).getId().isEmpty()) {
            return false;
        }
      }
      return Objects.requireNonNull(this.helper.getTile(GridPane.getRowIndex(sourceTile),
          GridPane.getColumnIndex(sourceTile) - 1)).getChildren().get(0).getId().isEmpty()
          && Objects.requireNonNull(this.helper.getTile(GridPane.getRowIndex(sourceTile),
          GridPane.getColumnIndex(sourceTile) - 2)).getChildren().get(0).getId().isEmpty();

    }else { // vertical l-shape
      // up
      if (verticalSteps > 0) {
        return Objects.requireNonNull(this.helper.getTile(GridPane.getRowIndex(sourceTile) - 1,
            GridPane.getColumnIndex(sourceTile))).getChildren().get(0).getId().isEmpty()
            && Objects.requireNonNull(this.helper.getTile(GridPane.getRowIndex(sourceTile) - 2,
            GridPane.getColumnIndex(sourceTile))).getChildren().get(0).getId().isEmpty();
      } else {
        // down
        return Objects.requireNonNull(this.helper.getTile(GridPane.getRowIndex(sourceTile) + 1,
            GridPane.getColumnIndex(sourceTile))).getChildren().get(0).getId().isEmpty()
            && Objects.requireNonNull(this.helper.getTile(GridPane.getRowIndex(sourceTile) + 2,
            GridPane.getColumnIndex(sourceTile))).getChildren().get(0).getId().isEmpty();
      }
    }
  }



  /**
   * Handles the hover over a piece on the game board by showing the power of the piece
   * @author aemsbach
   * @param pieceId the id of the piece
   * @param team    the team of the piece
   * @param myTeam  the team of the player
   *
   */
  private void handlePieceHover(String pieceId, String team, Team myTeam){
    Piece piece = this.helper.getPiece(pieceId, myTeam);
    if(piece == null){
      return;
    }
    if(team.equals(myTeam.getId())){
      this.myPiecePower.setText(piece.getDescription().getAttackPower() + "");
      this.myPowerPane.setVisible(true);
      this.myPower.setVisible(true);
      this.myPiecePower.setVisible(true);
      this.myPowerBg.setVisible(true);

    }
    else{
      String color = Objects.requireNonNull(this.helper.getTeam(team)).getColor();
      color = switch (color) {
        case "blue" -> "#3A6F9E";
        case "red" -> "#a90505";
        case "green" -> "#5FA051";
        case "pink" -> "#684c6b";
        default -> color;
      };
      this.oppPower.setStyle("-fx-text-fill: " + color);
      this.opponentPiecePower.setStyle("-fx-text-fill: " + color + ";");
      this.oppPowerBg.setStyle("-fx-border-color: " + color + ";" + "-fx-background-color:  #D9D9D9;");
      this.opponentPiecePower.setText(piece.getDescription().getAttackPower() + "");
      this.opponentPiecePower.setVisible(true);
      this.oppPower.setVisible(true);
      this.oppPowerBg.setVisible(true);


    }
    if(myMoveTime.isVisible()){
      if(team.equals(myTeam.getId())){
        Cursor cursor = Cursor.HAND;
        this.gameBoard.getScene().setCursor(cursor);

      }
      else if(!this.pieceClicked.equals("-1")){
        Cursor cursor = Cursor.HAND;
        this.gameBoard.getScene().setCursor(cursor);

      }
    }


  }
  /**
   * Handles the removal of the hover over a piece on the game board by hiding the power of the piece
   * @author aemsbach
   *
   */
  private void handleMouseExit(){
    this.myPowerPane.setVisible(false);
    this.opponentPiecePower.setVisible(false);
    this.myPower.setVisible(false);
    this.oppPower.setVisible(false);
    this.myPowerBg.setVisible(false);
    this.oppPowerBg.setVisible(false);

    Cursor cursor = Cursor.DEFAULT;
    this.gameBoard.getScene().setCursor(cursor);

  }



  /**
   * Thread that updated the game board according to the current game state and time left in the
   * game
   * @author aemsbach
   */
  private void updateGameInfo() {
    //saves the id of the team that is making the move
    final int[] myMove = {-1};
    final Date[] startMoveTime = {new Date()};
    //Get the time limits from the map

    //get the initial game state
    GameState initState = this.requestHandler.getGameState(this.gameSessionId);
    this.grid = initState.getGrid();
    this.teams = initState.getTeams();
    //updateBoard();



    //Set the object variable team to the correct team object
    for (Team t : initState.getTeams()) {
      if (this.team.getId().equals(t.getId())) {
        this.team = t;
        break;
      }
    }
    //figure out the theme
    this.initPieceCount = this.team.getPieces().length;


      Piece randomPiece = teams[0].getPieces()[0];
        if(randomPiece.getDescription().getType().matches("water_.*")){
          this.theme = Theme.sea;
        }
        else{
          this.theme = Theme.land;
        }



    Platform.runLater(() -> {
      int[] initFlags = new int[initState.getTeams().length];
      HBox[] flags = new HBox[4];
      Image[] flagImgs = new Image[4];
      flags[0] = this.team1Flags;
      flags[1] = this.team2Flags;
      flags[2] = this.team3Flags;
      flags[3] = this.team4Flags;
      flagImgs[0] = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/blue_flag.png")).toString());
      flagImgs[1] = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/red_flag.png")).toString());
      flagImgs[2] = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/green_flag.png")).toString());
      flagImgs[3] = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/pink_flag.png")).toString());
      for (int i = 0; i < initState.getTeams().length; i++) {
        initFlags[i] = initState.getTeams()[i].getFlags();
        for(int j = 0; j < initFlags[i]; j++){
          ImageView flag = new ImageView(flagImgs[i]);
          flag.setFitHeight(75);
          flag.setFitWidth(75);
          flags[i].getChildren().add(flag);
        }
      }


      this.constructBoard();

    this.gameBoard.getChildren().add(board);});

    while (true) {
      //Get the current gameState and gameSession from server
      GameState state = this.requestHandler.getGameState(this.gameSessionId);
      if(state == null){
        break;
      }
      GameSessionResponse gameSession = this.requestHandler.getGameSession(this.gameSessionId);

      //Check if game is over
      if (gameSession.isGameOver() && this.basePane.getScene().getWindow() != null){
        Platform.runLater(() -> {

          FXMLLoader fxmlLoader = new FXMLLoader();
          Stage stage = (Stage) this.basePane.getScene().getWindow();
          fxmlLoader.setLocation(getClass().getResource("/fxml/GameOverScreen.fxml"));
          if(this.mediaPlayer != null)
            this.mediaPlayer.stop();
          try {
            fxmlLoader.load();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          GameOverController controller = fxmlLoader.getController();


          Scene scene = new Scene(fxmlLoader.getRoot());
          if(!this.team.getId().equals("0")){
            this.team = this.teams[(Integer.parseInt(this.team.getId())-1)];
          }
          controller.passInfo(requestHandler, gameSessionId, gameSession.getWinner(), this.team.getId(), this.mediaPlayer, this.theme, this.moveCount, this.team, this.initPieceCount);



          stage.setScene(scene);
          Screen screen = Screen.getPrimary();
          stage.setHeight(screen.getVisualBounds().getHeight());
          stage.setWidth(screen.getVisualBounds().getWidth());



        });
        break;
      }

      //update GUI to display the correct team's move
      Platform.runLater(() -> {
        if(this.mediaPlayer != null && this.mediaPlayer.isMute()){
          this.mediaPlayer.setMute(false);

          if(this.mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)){
            this.mediaPlayer.play();

          }
        }

        String color = this.team.getColor().toLowerCase();
        color = switch (color) {
          case "blue" -> "#3A6F9E";
          case "red" -> "#a90505";
          case "green" -> "#5FA051";
          case "pink" -> "#684c6b";
          default -> color;
        };


        this.myPiecePower.setStyle("-fx-text-fill: " + color+";");
        this.myPower.setStyle("-fx-text-fill: " +color);
        this.myPowerBg.setStyle("-fx-border-color: " + color + ";" + "-fx-background-color:  #D9D9D9;");


        if(gameSession.getRemainingGameTimeInSeconds() != -1){
          int seconds = gameSession.getRemainingGameTimeInSeconds()%60;
          this.gameTime.setText(gameSession.getRemainingGameTimeInSeconds()/60 + ":" + (seconds<10 ? "0" : "")+ seconds);
        }

        //check if it's a new team's turn
        //If it is a new team's turn, update the game board since a move has been made
        //and reset the startMoveTime
        if (myMove[0] != state.getCurrentTeam()) {
          startMoveTime[0] = new Date();
          this.grid = state.getGrid();
          Move lastMove = state.getLastMove();
          if(lastMove != null){
            this.helper.movePiece(lastMove, state.getCurrentTeam(), state.getTeams().length, this.teams);}
            this.teams = state.getTeams();
          if(helper.baseCaptured){
            this.helper.updateFlagCount(state.getTeams());
            helper.baseCaptured = false;
            assert lastMove != null;
            Piece pieceMoved = this.helper.getPiece(lastMove.getPieceId(),
                Objects.requireNonNull(this.helper.getTeam(String.valueOf(myMove[0]), this.teams)));
            assert pieceMoved != null;
            int[] coords = this.helper.rotateCoords(this.team.getId(), pieceMoved.getPosition()[0], pieceMoved.getPosition()[1]);
            Objects.requireNonNull(this.helper.getTile(coords[0], coords[1]))
                .getChildren().get(0).setId("p:" + pieceMoved.getTeamId() + "_" + pieceMoved.getId());
            this.helper.matchPieceToImage(lastMove.getPieceId(), pieceMoved.getTeamId(),
                this.helper.getTile(coords[0], coords[1]), this.teams,
                Objects.requireNonNull(this.helper.getTile(coords[0], coords[1])).getWidth(), coords[0], coords[1], this.helper.getTeam(this.team.getId()));

          }
          if(lastMove ==null) {
            this.gameBoard.getChildren().remove(board);
            this.constructBoard();
            this.updateBoard();
            this.gameBoard.getChildren().add(board);
          }

        }
        //Check if the game has a time limit
        //Check if the game has a move time limit
        //calculate time left for move
        if(gameSession.getRemainingMoveTimeInSeconds() != -1){
          //set time left for correct team
          int seconds = gameSession.getRemainingMoveTimeInSeconds()%60;
          if(myMove[0] == Integer.parseInt(this.team.getId())){
            myMoveTime.setText(gameSession.getRemainingMoveTimeInSeconds()/60 + ":" + (seconds<10 ? "0" : "")+ seconds);
          }
          else{
            this.opponentMoveTime.setText((gameSession.getRemainingMoveTimeInSeconds()/60) + ":" + (seconds<10 ? "0" : "")+ seconds );
          }
        }

        //check if it's my turn
        if (state.getCurrentTeam() == Integer.parseInt(this.team.getId())) {
          //set the correct turn and update GUI to show correct view
          myMove[0] = Integer.parseInt(this.team.getId());
          this.myClock.setVisible(true);
          this.myMoveTime.setVisible(true);
          this.myBack1.setVisible(true);
          this.myBack2.setVisible(true);
          this.myBorder.setVisible(true);

          this.opponentClock.setVisible(false);
          this.opponentMoveTime.setVisible(false);
          this.opponentback1.setVisible(false);
          this.opponentback2.setVisible(false);
          this.borderOpp.setVisible(false);
        } else {
          //set the correct turn and update GUI to show correct view
          myMove[0] = state.getCurrentTeam();
          this.myClock.setVisible(false);
          this.myMoveTime.setVisible(false);
          this.myBack1.setVisible(false);
          this.myBack2.setVisible(false);
          this.myBorder.setVisible(false);
          //show time left for opponent's move
          this.opponentClock.setVisible(true);
          this.opponentMoveTime.setVisible(true);
          this.opponentback1.setVisible(true);
          this.opponentback2.setVisible(true);
          this.borderOpp.setVisible(true);

          String color2 = Objects.requireNonNull(this.helper.getTeam(Integer.toString(state.getCurrentTeam()))).getColor();
          color2 = switch (color2) {
            case "blue" -> "#3A6F9E";
            case "red" -> "#a90505";
            case "green" -> "#5FA051";
            case "pink" -> "#684c6b";
            default -> color2;
          };

          this.opponentClock.setStyle("-fx-text-fill: " + color2);
          this.opponentMoveTime.setStyle("-fx-text-fill: " + color2);
        }

      });
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        break;
      }
    }
  }

  /**
   * Helper class for the game board controller Contains methods to help with the game board
   *
   * @author aemsbach
   */
  private class GameBoardHelper {

    private int counter;

    public boolean baseCaptured = false;

    /**
     * Gets the tile at the specified row and column
     * @author aemsbach
     * @param row the row of the tile
     * @param col the column of the tile
     * @return the tile at the specified row and column
     */
    private StackPane getTile(int row, int col) {
      //Check if the row and column are out of bounds
      if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length) {
        return null;
      }


      if(row == 0 && col == 0){
        return (StackPane) board.getChildren().get(0);
      }

      return (StackPane) board.getChildren().get(row * grid[0].length + col + 1);
    }

    private GameBoardHelper() {
    }

    /**
     * Updates the board after a move by moving the piece on the board to the target tile
     * @author aemsbach
     * @param lastMove the last move made
     * @param currentTeam the team  currently moving
     * @param length the length of the teams array
     * @param teams the teams in the game
     */
    private void movePiece(Move lastMove, int currentTeam, int length, Team[] teams){
      String teamId = (String.valueOf(currentTeam).equals("1") ? String.valueOf(length) : String.valueOf(currentTeam - 1));
      Piece piece = this.getPiece(lastMove.getPieceId(),
          Objects.requireNonNull(this.getTeam(teamId, teams)));
      assert piece != null;
      int initRow = piece.getPosition()[0];
      int initCol = piece.getPosition()[1];



      int newRow = lastMove.getNewPosition()[0];
      int newCol = lastMove.getNewPosition()[1];

      int[] coords = rotateCoords(team.getId(), initRow, initCol);
      initRow = coords[0];
      initCol = coords[1];

      StackPane initTile = this.getTile(initRow, initCol);

      assert initTile != null;

      ImageView pieceImg = (ImageView) initTile.getChildren().get(1);
      ImageView newPieceImg = new ImageView(pieceImg.getImage());

      initTile.getChildren().remove(1);

      if(grid[newRow][newCol].charAt(0) == 'b'){

        this.baseCaptured = true;
        return;
      }

      newPieceImg.setFitWidth(initTile.getWidth());
      newPieceImg.setFitHeight(initTile.getHeight());

      int[] newCoords = rotateCoords(team.getId(), newRow, newCol);

      StackPane newTile = this.getTile(newCoords[0], newCoords[1]);

      assert newTile != null;
      if(newTile.getChildren().size() > 1){
        newTile.getChildren().remove(1);
      }
      this.matchPieceToImage(lastMove.getPieceId(), teamId, newTile, teams, newTile.getWidth(), newCoords[0], newCoords[1], this.getTeam(team.getId()));
      ((Rectangle)(newTile.getChildren().get(0))).setFill(Color.WHITE);
      newTile.getChildren().get(0).setId(initTile.getChildren().get(0).getId());

      initTile.getChildren().get(0).setId("");



    }

    private int[] rotateCoords(String teamId, int row, int col){
      switch (teamId) {
        case "1":
          break;
        case "2":
          row = grid.length - row - 1;
          col = grid[0].length - col - 1;
          break;
        case "4":
          int tempCol = col;
          col = grid[0].length - row - 1;
          row = tempCol;
          break;
        case "3":

          int tempRow = row;
          row = grid.length - col - 1;
          col = tempRow;

          break;
      }
      return new int[]{row, col};

    }

    /**
     * Sets the style of the possible move tiles on the board to show the player where they can move
     * the piece
     * @author aemsbach
     * @param possibleMoves the list of possible moves
     * @param tile          the tile that is being styled
     */
    private void setPossibleMoveStyle(ArrayList<Rectangle> possibleMoves, StackPane tile,
        String teamId) {

      Rectangle rect = (Rectangle) tile.getChildren().get(0);
      if (checkMove(tile, teamId)) {
        rect.setFill(Color.GRAY);
        rect.setOpacity(0.3);
        possibleMoves.add(rect);

      }

    }


    /**
     * Checks if a possible move would land on a valid tile A valid tile is empty, holds an enemy
     * piece or an enemy base
     * @author aemsbach
     * @param tile the tile that is being checked
     * @return true if the move is valid, false otherwise
     */
    private boolean checkMove(StackPane tile, String teamId) {
      String text = (tile.getChildren().get(0)).getId();
      if (text.length() == 1) {
        return false;
      }

      return (text.isEmpty() || tile.getChildren().size() < 2
          || (text.charAt(0) == 'b' && text.charAt(2) != teamId.charAt(0))
          || (text.charAt(0) == 'p' && text.charAt(2) != teamId.charAt(0)));
    }

    /**
     * Matches the piece to the correct image
     * @author aemsbach
     * @param pieceId the id of the piece to be matched
     * @param teamId the id of the team the piece belongs to
     * @param tile the tile the piece is on
     * @param teams the teams in the game
     * @param tileMeasure the size of the tile
     * @param finalI the row of the tile
     * @param finalJ the column of the tile
     * @param myTeam the id of the team the user is playing as
     */
    private void matchPieceToImage(String pieceId, String teamId, StackPane tile, Team[] teams, double tileMeasure,
        int finalI, int finalJ, Team myTeam) {
      String image = "";
      Team team;
      String color;
      team = Arrays.stream(teams).filter(t -> teamId.equals(t.getId())).findFirst().orElse(null);
      if (team == null) {

        return;
      }
      color = team.getColor().toLowerCase();



      Piece piece = getPiece(pieceId, team);
      assert piece != null;


    if(theme == Theme.land){
      image = switch (piece.getDescription().getType()) {
        case "infantry" -> "/assets/pieces/"+ color +"_infanterist.png";
        case "grenadier" -> "/assets/pieces/" + color +"_grenadier.png";
        case "general" -> "/assets/pieces/" + color +"_general.png";
        case "cavalry" -> "/assets/pieces/" + color +"_cavalry.png";
        case "dragonfighter" -> "/assets/pieces/" + color +"_dragonfighter.png";
        case "musketeer" -> "/assets/pieces/" + color +"_musketeer.png";
        case "pikenier" -> "/assets/pieces/" + color +"_pikenier.png";
        case "priest" -> "/assets/pieces/" + color +"_priest.png";
        case "scout" -> "/assets/pieces/" + color +"_scout.png";
        case "artillery" -> "/assets/pieces/" + color +"_artillery.png";

        default -> image;
      };
      if (image.isEmpty()) {

        return;
      }
    }
    else{
      String s = piece.getDescription().getType();
      String[] desc = s.split("_");
      image = "/assets/" + desc[0] + "_" + color + "_" + desc[1] + ".png";

    }

      Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(image)));

      ImageView imgView = new ImageView(img);

      imgView.setFitHeight(tileMeasure);
      imgView.setFitWidth(tileMeasure);
      imgView.setPreserveRatio(true);


      imgView.addEventHandler(MouseEvent.MOUSE_CLICKED,
          (MouseEvent event) -> handleRectClick(finalI, finalJ, myTeam));
      imgView.addEventHandler(MouseEvent.MOUSE_ENTERED,
          (MouseEvent event) -> handlePieceHover(pieceId, teamId, myTeam));
      imgView.addEventHandler(MouseEvent.MOUSE_EXITED,
          (MouseEvent event) -> handleMouseExit());



      tile.getChildren().add(imgView);


    }


    /**
     * Matches the block to the correct image
     * @author aemsbach
     * @param tile the tile the block is on
     * @param tileMeasure the size of the tile
     */
    private void matchBlockToImage(StackPane tile, double tileMeasure){
      int block = counter%(blocks.length);
      this.counter++;

      ImageView blockImg = new ImageView(blocks[block]);
      blockImg.setFitHeight(tileMeasure);
      blockImg.setFitWidth(tileMeasure);
      tile.getChildren().add(blockImg);

    }
    /**
     * Matches the base to the correct image
     * @author aemsbach
     * @param tile the tile the base is on
     * @param teamId the id of the team the base belongs to
     * @param tileMeasure the size of the tile
     */
    private void matchBaseToImage(StackPane tile, String teamId, double tileMeasure, int row, int col, Team myTeam){
      String color = Objects.requireNonNull(getTeam(teamId)).getColor().toLowerCase();
      Image img;
      if (Objects.requireNonNull(theme) == Theme.land) {
        img = new Image(
            Objects.requireNonNull(
                getClass().getResourceAsStream("/assets/" + color + "_base.png")));
      } else {
        img = new Image(
            Objects.requireNonNull(
                getClass().getResourceAsStream("/assets/bases/water_" + color + "_waterbase.png")));
      }
      ImageView imgView = new ImageView(img);
      imgView.setFitHeight(tileMeasure);
      imgView.setFitWidth(tileMeasure);
      imgView.addEventHandler(MouseEvent.MOUSE_CLICKED,
          (MouseEvent event) -> handleRectClick(row, col, myTeam));

      tile.getChildren().add(imgView);
    }

    public Team getTeam(String id){
      for(Team t : teams){
        if(t.getId().equals(id)){
          return t;
        }
      }
      return null;
    }
    public Team getTeam(String id, Team[] teams){
      for(Team t : teams){
        if(t.getId().equals(id)){
          return t;
        }
      }
      return null;
    }
    /**
     * Updates the flag count of the teams on the board which shows how many flags each team has left
     * @author aemsbach
     */
    public void updateFlagCount(Team[] teams){
      for (int i = 0; i < teams.length; i++) {
        int flags = teams[i].getFlags();
        Label label = new Label();
        label.setText("Flags: " + flags);
        switch (i) {
          case 0:
            team1Flags.getChildren().clear();
            for(int j = 0; j < flags; j++){
              ImageView flag = new ImageView(new Image(
                  Objects.requireNonNull(getClass().getResource("/assets/blue_flag.png")).toString()));
              flag.setFitHeight(75);
              flag.setFitWidth(75);
              team1Flags.getChildren().add(flag);
            }
            break;
          case 1:
            team2Flags.getChildren().clear();
            for(int j = 0; j < flags; j++){
              ImageView flag = new ImageView(new Image(
                  Objects.requireNonNull(getClass().getResource("/assets/red_flag.png")).toString()));
              flag.setFitHeight(75);
              flag.setFitWidth(75);
              team2Flags.getChildren().add(flag);
            }
            break;
          case 2:
            team3Flags.getChildren().clear();
            for(int j = 0; j < flags; j++){
              ImageView flag = new ImageView(new Image(
                  Objects.requireNonNull(getClass().getResource("/assets/green_flag.png")).toString()));
              flag.setFitHeight(75);
              flag.setFitWidth(75);
              team3Flags.getChildren().add(flag);
            }
            break;
          case 3:
            team4Flags.getChildren().clear();
            for(int j = 0; j < flags; j++){
              ImageView flag = new ImageView(new Image(
                  Objects.requireNonNull(getClass().getResource("/assets/pink_flag.png")).toString()));
              flag.setFitHeight(75);
              flag.setFitWidth(75);
              team4Flags.getChildren().add(flag);
            }
            break;
        }
      }
    }

    /**
     * Gets the piece with the specified id from the team object
     * @author aemsbach
     * @param pieceId the id of the piece
     * @param team the team the piece belongs to
     * @return the piece with the specified id
     */
    private Piece getPiece(String pieceId, Team team) {
      for (Piece p : team.getPieces()) {
        if (p.getId().equals(pieceId)) {
          return p;
        }
      }
      return null;
    }
  }

}

