package de.unimannheim.swt.pse.client.controller;
import static de.unimannheim.swt.pse.server.game.map.PlacementType.*;

import de.unimannheim.swt.pse.client.GUI;
import de.unimannheim.swt.pse.client.HelperMethods;
import de.unimannheim.swt.pse.server.database.DatabaseInitializer;
import de.unimannheim.swt.pse.server.database.controller.MapController;
import de.unimannheim.swt.pse.server.database.dao.MapDAO;
import de.unimannheim.swt.pse.server.database.service.MapService;
import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import de.unimannheim.swt.pse.server.game.map.PieceDescription;
import de.unimannheim.swt.pse.server.game.map.PlacementType;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
/**
 * @author paklaus
 * Controller for the MapEditor that does all logic for the frontend together with HelperMethods
 */
public class MapEditorController implements Initializable {
  /**
  *Array that holds the information for the playing field
   */
  String[][] playingFieldArr = new String[6][6];
  /**
  * UI input for width of playing field
   */
  @FXML
  TextField widthStr;
  /**
  int value of width of playing field
   */
  private int width = 0;
  /**
  * UI input for height of playing field
   */
  @FXML
  TextField heightStr;
  /**
   * Anchor Pane for the Back Button (animation)
   */
  @FXML
  AnchorPane backButtonPane;
  /**
   * Anchor Pane for the Create Piece Button (animation)
   */
  @FXML
  AnchorPane createAnchorPane;
  /**
   * Anchor Pane for the Create Map Button (animation)
   */
  @FXML
  AnchorPane createMapAnchorPane;
  /**
  *int value of height of playing field
   */
  private int height = 0;
  /**
   * label that displays error message above the create button
   */
  @FXML
  private Label errorMessage;
  /**
   * label that displays error message below the theme choice
   */
  @FXML
  private Label themeErrorMessage;
  /**
  * UI playing field representation
   */
  @FXML
  private GridPane playingField;
  /**
  * UI Element that is needed to create a background for the map
   */
  @FXML
  private StackPane playingFieldStackPane;
  /**
   * UI input for amount of teams
   */
  @FXML
  private ChoiceBox<Integer> teamAmountChoiceBox;
  /**
   * UI input for the strategy
   */
  @FXML
      private ChoiceBox<String> strategyChoiceBox;
  /**
   * UI input for amount of barriers
   */
  @FXML
      private TextField barrierAmountStr;
  /**
* int value of amount of barriers
*/
  private int barrierAmount = 0;
  /** background element **/
  @FXML
      private SplitPane background;
  /**
   * UI grid where many inputs fields are placed
   */
  @FXML
      private GridPane inputGrid;
  /**
   * UI input for amount of teams
   */
  @FXML
      private TextField flagAmountStr;
  /**
   * int value for amount of flags
   */
  private int flagAmount = 1;
  /**
   * UI grid where fields for map size input are placed
   */
  @FXML
      private GridPane mapSizeGrid;
  /**
   * UI element where pieces are displayed
   */
  @FXML
      private HBox pieceHolder;
  /**
  *UI Background of the playing field
   */
  @FXML
      private HBox playingFieldBackground;
  /**
   * UI input for amount of play time
   */
  @FXML
      private TextField playTimeStr;
  /**
   * UI input for amount game time
   */
  @FXML
      private TextField gameTimeStr;
  /**
   * UI input for Theme Choice: Water
   */
  @FXML
      private RadioButton waterRb;
  /**
   * UI input for Theme Choice: Water
   */
  @FXML
  private RadioButton landRb;
  /**
   * int value for playtime
   */
  int playTime = 0;
  /**
   * int value of game time
   */
  int gameTime = 0;
  /**
   * choices for the team amounts
   */
  Integer[] teamAmountArray = {2,3,4};
  /**
   * choices for the strategies
   */
  private final String[] strategyArray = {"symmetrical", "spaced_out", "defensive"};
  /**
   * data structure that holds the pieces added to a map
   */
  private ArrayList<PieceDescription> pieces = new ArrayList<>();
  /**
   * theme of the playing field
   */
  private String theme = "gras";
  /**
   * possible icons for a piece in Land mode
   */
  String imgAddressesLand = "/assets/pieces/blue_";
  /**
   * possible icons for a piece in water mode
   */
  String imgAddressesWater = "/assets/water_blue_";

  /**
   * gives the minimum dimensions the map needs to have
   */
  private final int minDimensions = 6;
  MapTemplate map = null;
  /**
   * @author paklaus
   * @param arg0, arg1
   * opens the scene
   */
  public void initialize(URL arg0, ResourceBundle arg1){
    int totalWidth = (int) playingFieldBackground.getWidth();
    int totalHeight = (int) playingFieldBackground.getHeight();
    teamAmountChoiceBox.getItems().addAll(teamAmountArray);
    teamAmountChoiceBox.setValue(2);
    teamAmountChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      updateValues("width");
      updateValues("height");
      updatePlayingField();
    });
    strategyChoiceBox.getItems().addAll(strategyArray);
    strategyChoiceBox.setValue("defensive");
    strategyChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      updateValues("width");
      updateValues("height");
      updatePlayingField();
    });
    updateValues("width");
    updateValues("height");
    HelperMethods.showMap(playingFieldArr, playingField, pieces, totalWidth, totalHeight, false, getTheme());
    this.playingFieldBackground.heightProperty().addListener((observable, oldValue, newValue) -> {

        if(map == null){
          changeTheme();
        }
    });
    this.backButtonPane.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image img = new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
      ImageView imageView = new ImageView(img);
      imageView.setFitHeight(newValue.doubleValue());
      imageView.setFitWidth(this.backButtonPane.getWidth());
      this.backButtonPane.getChildren().add(imageView);
    });
    this.createAnchorPane.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image img = new Image(Objects.requireNonNull(getClass().getResource("/assets/woodButton1.png")).toString());
      ImageView imageView = new ImageView(img);
      imageView.setFitHeight(newValue.doubleValue());
      imageView.setFitWidth(this.createAnchorPane.getWidth());
      this.createAnchorPane.getChildren().add(0,imageView);
    });
    this.createMapAnchorPane.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image img = new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
      ImageView imageView = new ImageView(img);
      imageView.setFitHeight(newValue.doubleValue());
      imageView.setFitWidth(this.createMapAnchorPane.getWidth());
      this.createMapAnchorPane.getChildren().add(imageView);
    });
    this.playingFieldBackground.heightProperty().addListener((observable, oldValue, newValue) -> {
    if(map != null){
      displayMapTemplate(map);
    }
    });
    this.background.sceneProperty().addListener((observable, oldValue, newValue) -> {
      if (oldValue == null && newValue != null) {
        Image img = new Image(Objects.requireNonNull(getClass().getResource("/assets/background.png")).toString());
        newValue.getRoot().setStyle("-fx-background-image: url('" + img.getUrl() + "'); " +
            "-fx-background-size: cover; -fx-background-repeat: no-repeat;");
      }
    });

  }
  /**
   * @author paklaus
   * opens the piece editor
   */
  public void switchToPieceEditor() throws IOException {

    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/PieceEditorMovement.fxml"));
    Parent root = fxmlLoader.load();
    PieceEditorMovementController controller = fxmlLoader.getController();
    controller.setMapEditorController(this);
    controller.setMapEditorScene(inputGrid.getScene());
    Stage popupStage = new Stage();
    popupStage.initOwner(widthStr.getScene().getWindow());
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.initStyle(StageStyle.TRANSPARENT);

    Scene scene = new Scene(root);
    popupStage.setScene(scene);
    popupStage.show();
  }
  /**
   * @author paklaus
   * @param e event from the action input
   * switches the UI back to the mapEditor Menu
   */
  public void switchToMapEditorMenu(MouseEvent e) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/MapEditorMenu.fxml"));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    Screen screen = Screen.getPrimary();
    stage.setHeight(screen.getVisualBounds().getHeight() * 0.999);
    stage.setWidth(screen.getVisualBounds().getWidth() * 0.999);
    stage.setScene(scene);
    stage.show();
  }
  /**
   * @author paklaus
   * @param condition that decides what values are updated
   * depending on the parameter it updates the int values of the corresponding UI inputs
   */
  public void updateValues(String condition){
    try {
      switch (condition) {
        case "width":
          width = Integer.parseInt(widthStr.getText());
          break;
        case "height":
          height = Integer.parseInt(heightStr.getText());
          break;
        case "barrier":
          barrierAmount = Integer.parseInt(barrierAmountStr.getText());
          break;
        case "flag":
          flagAmount = Integer.parseInt(flagAmountStr.getText());
          break;
        case "playTime":
          playTime = Integer.parseInt(playTimeStr.getText());
          break;
        case "gameTime":
          gameTime = Integer.parseInt(gameTimeStr.getText());
          break;
        default:
          System.out.println("No such condition");
      }
    }catch(NumberFormatException e){
      System.out.println("There has been an error parsing the input from the UI");
    }
  }
  /**
  * @author paklaus
  * adjusts the playing field to the new size
   */
  public void updatePlayingField(){
    mapSizeGrid.getChildren().removeIf(node -> node.getStyle().equals("-fx-text-fill: red"));
updateValues("width");
updateValues("height");
//throw error messages if the map is too small
      if(width < minDimensions){
        Label label = new Label("Too small");
        label.setStyle("-fx-text-fill: red");
        label.setPrefSize(200, 20);
        label.setAlignment(Pos.TOP_RIGHT);
        mapSizeGrid.add(label, 0, 2);
        if(height < minDimensions){
          Label label2 = new Label("Too small");
          label2.setStyle("-fx-text-fill: red");
          label2.setPrefSize(200, 20);
          label2.setAlignment(Pos.TOP_LEFT);
          mapSizeGrid.add(label2, 2, 2);
        }
        return;
      }
      if(height < minDimensions){
        Label label = new Label("Too small");
        label.setStyle("-fx-text-fill: red");
        label.setPrefSize(200, 20);
        label.setAlignment(Pos.TOP_LEFT);
        mapSizeGrid.add(label, 2, 2);
        return;
      }
    int totalWidth = (int) playingFieldBackground.getWidth();
    int totalHeight = (int) playingFieldBackground.getHeight();
playingFieldArr = HelperMethods.removeNullFromArray(new String[height][width]);
try{
  playingFieldArr = HelperMethods.placePieces(width, height, getPlacementType(), pieces.toArray(new PieceDescription[0]), teamAmountChoiceBox.getValue(), playingFieldArr);
}
catch(Exception e){
  errorMessage.setStyle("-fx-text-fill: red");
  errorMessage.setText("Too many pieces for the playing field size");
  return;

}
HelperMethods.showMap(playingFieldArr, playingField, pieces, totalWidth, totalHeight, false, getTheme());
  }
  /**  @author paklaus
   * updates the barriers on the playing field
   */
  public void updateBarriers() {
    updateValues("barrier");
    updateValues("width");
    updateValues("height");
    String[][] tempPlayingFieldArr = HelperMethods.updateBarriers(playingFieldArr, barrierAmount);
    if (tempPlayingFieldArr == null){
      Label label = new Label("Too many barriers");
      label.setStyle("-fx-text-fill: red");
      label.setPrefSize(200, 20);
      label.setAlignment(Pos.TOP_RIGHT);
      inputGrid.add(label, 2, 3);
      for (Node nodeIt : playingField.getChildren()) {

        if(nodeIt.getStyle().equals("-fx-background-color: grey;")){
          nodeIt.setStyle("-fx-border-color: black");
        }
      }
      return;
    }
    playingFieldArr = tempPlayingFieldArr;
    //remove error messages
    inputGrid.getChildren().removeIf(node -> node.getStyle().equals("-fx-text-fill: red"));
    int totalWidth = (int) playingFieldBackground.getWidth();
    int totalHeight = (int) playingFieldBackground.getHeight();
    HelperMethods.showMap(playingFieldArr, playingField, pieces, totalWidth, totalHeight, false, getTheme());
  }
  /**  @author paklaus
   * @param piece piece that is to the internal list
   * adds the piece given as parameter to internal list
   */
  public void addPiece(PieceDescription piece) {
    pieces.add(piece);
      updatePieceDisplay();
  }
  /**  @author paklaus
   * updates the piece display in the GUI with all the piece elements from the internal list
   */
  public void updatePieceDisplay(){
    Node n = pieceHolder.getChildren().get(0);
    pieceHolder.getChildren().clear();
    pieceHolder.getChildren().add(n);
    for(PieceDescription piece : pieces){
      VBox pieceElement = new VBox();
      pieceElement.setPrefWidth(100);
      pieceElement.setAlignment(Pos.TOP_CENTER);
      ImageView imageView = new ImageView();
      imageView.setFitHeight(100);
      imageView.setFitWidth(100);
      String url;
      if(!theme.equals("water")){
        url = (imgAddressesLand+ piece.getType() + ".png");
      }
      else{
        String typeName = piece.getType().split("_")[piece.getType().split("_").length-1];
        url =(imgAddressesWater + typeName + ".png");
      }
      Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(url)));
      imageView.setImage(img);
      pieceElement.getChildren().add(imageView);
      Label label = new Label(Integer.toString(piece.getCount()));
      label.setAlignment(Pos.CENTER);
      label.setPadding(new javafx.geometry.Insets(40, 0, 0, 0));
      pieceElement.getChildren().add(label);
      pieceHolder.getChildren().add(pieceElement);

    }
  }
  /**  @author paklaus
   * @return MapTemplate Template with the input by the user
   * creates and returns a fully functional MapTemplate with the input by the user
   */
  public MapTemplate createMap(MouseEvent e) throws IOException {
    if(errorMessage.getText().equals("Too many pieces for the playing field size")){
      return null;
    }
    errorMessage.setText("");
    errorMessage.setStyle("-fx-text-fill: red");
    updateValues("width");
    updateValues("height");
    if(width < minDimensions || height < minDimensions){
      errorMessage.setText("Please insert dimensions minimum 3");
    return null;
    }
    updateValues("flag");
    if(flagAmount < 1){
      errorMessage.setText("Please insert flag amount greater than 1");
      return null;
    }
    updateValues("barrier");
    if(barrierAmount < 0){
      errorMessage.setText("Please insert barrier amount that is not negative");
      return null;
    }
    updateValues("playTime");
    updateValues("gameTime");

    MapTemplate result = new MapTemplate();
    result.setGridSize(new int[]{height, width});
    result.setTeams(teamAmountChoiceBox.getValue());
    result.setFlags(flagAmount);
    if(pieces.isEmpty()){
      errorMessage.setText("Please add at least one piece");
      return null;
    }
    result.setPieces(pieces.toArray(new PieceDescription[0]));
    result.setBlocks(barrierAmount);
    switch(strategyChoiceBox.getValue()){
      case "space_out":
        result.setPlacement(spaced_out);
        break;
      case "defensive":
        result.setPlacement(defensive);
        break;
      default:
        result.setPlacement(symmetrical);
    }
    if(gameTime == 0){
      result.setTotalTimeLimitInSeconds(-1);
    }else if (gameTime < 300){
      errorMessage.setText("Please insert game time amount that is greater than 300 seconds");
      return null;
    }else if (gameTime < playTime){
      errorMessage.setText("Game Time Amount must be greater than Play Time Amount");
      return null;
    }
    else{
      result.setTotalTimeLimitInSeconds(gameTime);
    }
    if(playTime == 0){
      result.setMoveTimeLimitInSeconds(-1);
    }else if(playTime < 5){
      errorMessage.setText("Please insert game time amount that is greater than 5 seconds");
      return null;
    }
    result.setTotalTimeLimitInSeconds(gameTime);
    result.setMoveTimeLimitInSeconds(playTime);
    System.out.println("MapTemplate created\n"
        + "GridSize: x" + result.getGridSize()[0] + " y" + result.getGridSize()[1] + "\n"
        + "Teams: " + result.getTeams() + "\n"
        + "Flags: " + result.getFlags() + "\n"
        + "Pieces: " + result.getPieces().length + "\n"
        + "Blocks: " + result.getBlocks() + "\n"
        + "Placement: " + result.getPlacement() + "\n"
        + "TotalTimeLimitInSeconds: " + result.getTotalTimeLimitInSeconds() + "\n"
        + "MoveTimeLimitInSeconds: " + result.getMoveTimeLimitInSeconds() + "\n");
    GUI.maps.remove(GUI.maps.size()-1);
    GUI.maps.add(0, result);
    if(GUI.userId != null){
      new DatabaseInitializer().initialize();
      // Instantiate MapService and MapController
      MapDAO mapDAO = new MapDAO();
      MapService mapService = new MapService(mapDAO);
      MapController mapController = new MapController(mapService);
      CompletableFuture<Void> future = mapController.createMap(result, GUI.userId);
      try {
        future.join(); // Wait for completion
        System.out.println("Map created successfully");
      } catch (CompletionException e1) {
        System.err.println("Exception during map creation: " + e1.getCause().getMessage());
      }

    }
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/LandingPage.fxml"));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    return result;
  }
  /**  @author paklaus
   * @return PlacementType
   * returns placement Type that is input by the user in the GUI
   */
  public PlacementType getPlacementType(){
    return strategyChoiceBox.getValue().equals("symmetrical") ?
        symmetrical : strategyChoiceBox.getValue().equals("spaced_out") ?
        spaced_out : defensive;
  }
  /**  @author paklaus
   * @return Grid Pane returns the live rendering of the MapPreview
   * returns the live rendering of the MapPreview
   */
  public GridPane getPlayingField(){
    return playingField;
  }

  /**  @author paklaus
   * @return String[][] returns the Array representation of the MapPreview
   * returns the Array representation of the MapPreview
   */
  public String[][] getPlayingFieldArr(){
    return playingFieldArr;
  }
  public ArrayList<PieceDescription> getPieceDescriptions(){
    return pieces;
  }

  /**
   * @author paklaus
   * places the pieces in the playing field array by using the helper methods
   */
  public void placePieces(){
    updateValues("width");
    updateValues("height");
    try{
    playingFieldArr = HelperMethods.placePieces(width, height, getPlacementType(), pieces.toArray(new PieceDescription[0]), teamAmountChoiceBox.getValue(), playingFieldArr);
    }
    catch(Exception e){
      errorMessage.setStyle("-fx-text-fill: red");
      errorMessage.setText("Too many pieces for the playing field size");
      return;
    }
    }
  /**
   * @author paklaus
   * changes theme (background and pieces) of the playing field
   */
  public void changeTheme(){
    if(pieceHolder.getChildren().size() > 1){
      themeErrorMessage.setText("");
      themeErrorMessage.setStyle("-fx-text-fill: red");
      themeErrorMessage.setText("Theme can't be changed after pieces are added");
      if(getTheme().equals("water")){
        waterRb.setSelected(true);
        landRb.setSelected(false);
      }else{
        waterRb.setSelected(false);
        landRb.setSelected(true);
      }
      return;
    }
    if(playingFieldStackPane.getChildren().size() > 1){
      playingFieldStackPane.getChildren().remove(0);
    }
    String imgAddress;
    if(waterRb.isSelected()){
      imgAddress = "/assets/mapWater.png";
      setTheme("water");
    }else{
      imgAddress = "/assets/editor_mapGras.png";
      setTheme("gras");
    }
    Image waterImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imgAddress)));
    ImageView waterImgView = new ImageView(waterImg);
    waterImgView.setFitHeight(playingFieldBackground.getHeight());
    waterImgView.setFitWidth(playingFieldBackground.getWidth());
    playingFieldStackPane.getChildren().add(0, waterImgView);
    int totalWidth = (int) playingFieldBackground.getWidth();
    int totalHeight = (int) playingFieldBackground.getHeight();
    HelperMethods.showMap(playingFieldArr, playingField, pieces, totalWidth, totalHeight, false, getTheme());
  }
  /**@author paklaus
   * @param map takes a map template and sets the UI elements to the values of the map
   */
  public void setMap(MapTemplate map){
    this.map = map;
  }
  /**@author paklaus
   * @param map takes a map template and sets the UI elements to the values of the map
   */
  public void displayMapTemplate(MapTemplate map){
    if(map.getPieces()[0].getType().contains("water")) {
      waterRb.setSelected(true);
      landRb.setSelected(false);
    }
      if(playingFieldStackPane.getChildren().size() > 1){
        playingFieldStackPane.getChildren().remove(0);
      }
      String imgAddress;
      if(waterRb.isSelected()){
        imgAddress = "/assets/mapWater.png";
        setTheme("water");
      }else{
        imgAddress = "/assets/editor_mapGras.png";
        setTheme("gras");
      }
      Image waterImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imgAddress)));
      ImageView waterImgView = new ImageView(waterImg);
      waterImgView.setFitHeight(playingFieldBackground.getHeight());
      waterImgView.setFitWidth(playingFieldBackground.getWidth());
      playingFieldStackPane.getChildren().add(0, waterImgView);
    widthStr.setText(Integer.toString(map.getGridSize()[1]));
    heightStr.setText(Integer.toString(map.getGridSize()[0]));
    teamAmountChoiceBox.setValue(map.getTeams());
    strategyChoiceBox.setValue(map.getPlacement().toString());
    barrierAmountStr.setText(Integer.toString(map.getBlocks()));
    flagAmountStr.setText(Integer.toString(map.getFlags()));
    playTimeStr.setText(Integer.toString(map.getMoveTimeLimitInSeconds()));
    gameTimeStr.setText(Integer.toString(map.getTotalTimeLimitInSeconds()));
    pieces = new ArrayList<>();
    pieces.addAll(Arrays.asList(map.getPieces()));
    updatePieceDisplay();
    try{
    playingFieldArr = HelperMethods.placePieces(map.getGridSize()[1], map.getGridSize()[0], map.getPlacement(), pieces.toArray(new PieceDescription[0]), map.getTeams(), playingFieldArr);
    }
    catch(Exception e){
      System.out.println("Too many pieces for the playing field size");
      errorMessage.setStyle("-fx-text-fill: red");
      errorMessage.setText("Too many pieces for the playing field size");
      return;
    }
    int totalWidth = (int) playingFieldBackground.getWidth();
    int totalHeight = (int) playingFieldBackground.getHeight();
    updateValues("width");
    updateValues("height");
    updateBarriers();
    HelperMethods.showMap(playingFieldArr, playingField, pieces, totalWidth, totalHeight, false, getTheme());
  }
  /**
   * @author paklaus
   * @return returns the theme of the playing field
   */
  public String getTheme(){
    return theme;
  }
  /**
   * @author paklaus
   * @params inputs the new theme of the playing field
   */
  public void setTheme(String theme){
    this.theme = theme;
  }

  /**
   * @author paklaus
   * @return retruns the background of the playing field
   */
  public HBox getPlayingFieldBackground(){
    return playingFieldBackground;
  }
  /**
   * @author paklaus
   * @param mouseEvent event from the mouse
   * animation for the buttons that give them a glowing effect
   */
  public void mouseEntered(MouseEvent mouseEvent) {
    HelperMethods.mouseEntered(mouseEvent);
    Image backBtn = new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonSmall_white.png")).toString());
    ImageView img = new ImageView(backBtn);
    Label label = (Label) mouseEvent.getSource();
    img.setFitHeight(label.getHeight());
    img.setFitWidth(label.getWidth());
    if(label.getText().equals("Back")){
      this.backButtonPane.getChildren().remove(0);
      this.backButtonPane.getChildren().add(0,img);
    }else if(label.getText().equals("Create")) {
      this.createMapAnchorPane.getChildren().remove(0);
      this.createMapAnchorPane.getChildren().add(0, img);
    }
    else{
      img = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/assets/woodButton1_white.png")).toString()));
      img.setFitWidth(label.getWidth());
      img.setFitHeight(label.getHeight());
      this.createAnchorPane.getChildren().remove(0);
      this.createAnchorPane.getChildren().add(0,img);

    }
  }
  /**
   * @author paklaus
   * @param mouseEvent event from the mouse
   * animation for the buttons that give them a glowing effect
   */
  public void mouseExited(MouseEvent mouseEvent) {
    HelperMethods.mouseExited(mouseEvent);
    Image backBtn = new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
    ImageView img = new ImageView(backBtn);
    Label label = (Label) mouseEvent.getSource();
    img.setFitHeight(label.getHeight());
    img.setFitWidth(label.getWidth());
    if(label.getText().equals("Back")){
      this.backButtonPane.getChildren().remove(0);
      this.backButtonPane.getChildren().add(0,img);
    }
    else if(label.getText().equals("Create")){
      this.createMapAnchorPane.getChildren().remove(0);
      this.createMapAnchorPane.getChildren().add(0,img);

    }
    else{
      img = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/assets/woodButton1.png")).toString()));
      img.setFitWidth(label.getWidth());
      img.setFitHeight(label.getHeight());
      this.createAnchorPane.getChildren().clear();
      this.createAnchorPane.getChildren().add(0,img);

    }

  }
}