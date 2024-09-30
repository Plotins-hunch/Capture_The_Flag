package de.unimannheim.swt.pse.client.controller;

import de.unimannheim.swt.pse.server.game.map.Movement;
import de.unimannheim.swt.pse.server.game.map.PieceDescription;
import de.unimannheim.swt.pse.server.game.map.Shape;
import de.unimannheim.swt.pse.server.game.map.ShapeType;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @author paklaus
 * Controller for the PieceEditorLShape.fxml => creates pieces with l shapes moves
 */
public class PieceEditorLShapeController implements Initializable {
  /**
   *label to display error messages if the input can't be turned into a piece
   */
  @FXML
  private Label errorMessage;
  /** background Pane*/
  @FXML
  GridPane background;
  /**
   * button to close the window
   */
  @FXML
  private Button closeButton;
  /**
   * combobox to select the icon of the piece
   */
  @FXML
  private ComboBox<String> iconComboBox;
  /**
   * textfield to enter the power of the piece
   */
  @FXML
  private TextField amountPowerStr;
  /**
   * textfield to enter the Amount of the piece
   */
  @FXML
  private TextField amountPiecesStr;
  /** radiobutton to select the normal movement */
  @FXML
  private RadioButton normalMovement;
  /**
   * reference to the controllor of the mapEditor
   */
  private MapEditorController mapEditorController;
  /**
   * reference to the scene of the mapEditor
   */
  private Scene mapEditorScene;
  /** array of the icons addresses for the land theme */
  private final String[] iconArrayLand = {"/assets/pieces/blue_artillery.png", "/assets/pieces/blue_cavalry.png", "/assets/pieces/blue_dragonfighter.png","/assets/pieces/blue_general.png", "/assets/pieces/blue_grenadier.png", "/assets/pieces/blue_infanterist.png", "/assets/pieces/blue_musketeer.png","/assets/pieces/blue_pikenier.png","/assets/pieces/blue_priest.png","/assets/pieces/blue_scout.png"};
  /** array of the icons addresses for the water theme */
  private final String[] iconArrayWater = {"/assets/water_blue_flyboat.png","/assets/water_blue_frigate.png","/assets/water_blue_jesus.png","/assets/water_blue_mermaid.png","/assets/water_blue_octopus.png","/assets/water_blue_privateer.png","/assets/water_blue_sailingboat.png"};
  /**
   *@author paklaus
   * initialize the combobox with the icons
   * @param arg0 URL
   * @param arg1 ResourceBundle
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1){
    normalMovement.heightProperty().addListener((observable, oldValue, newValue) -> {
      if(mapEditorController.getTheme().equals("gras")){
        iconComboBox.setItems(FXCollections.observableArrayList("Artillery", "Cavalry", "Dragonfighter", "General", "Grenadier", "Infanterist", "Musketeer", "Pikenier", "Priest", "Scout"));
      }
      else{
        iconComboBox.setItems(FXCollections.observableArrayList("Artillery", "Cavalry", "Dragonfighter", "General", "Grenadier", "Infanterist", "Musketeer", "Pikenier", "Priest", "Scout"));

        iconComboBox.setItems(FXCollections.observableArrayList("Flyboat", "Frigate", "Jesus", "Mermaid", "Octopus", "Privateer", "Sailingboat", "Flyboat"));
      }
      iconComboBox.setCellFactory(param -> new ListCell<>() {
        private final ImageView imageView = new ImageView();
        @Override
        protected void updateItem(String item, boolean empty) {
          super.updateItem(item, empty);
          if (empty || item == null) {
            setGraphic(null);
          } else {
            int index = getListView().getItems().indexOf(item);
            Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(mapEditorController.getTheme().equals("gras") ? iconArrayLand[index] : iconArrayWater[index])));
            imageView.setImage(img);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            setGraphic(imageView);
          }
        }
      });
        });
    this.background.sceneProperty().addListener((observable, oldValue, newValue) -> {
      if (oldValue == null && newValue != null) {
        Image img = new Image(Objects.requireNonNull(getClass().getResource("/assets/background.png")).toString());
        newValue.getRoot().setStyle("-fx-background-image: url('" + img.getUrl() + "'); " +
            "-fx-background-size: cover; -fx-background-repeat: no-repeat;");
      }
    });
  }
  /**@author paklaus
   * closes the popUp Window
   */
  public void closeWindow(){
    Stage stage = (Stage) closeButton.getScene().getWindow();
    stage.close();
  }
  /** @author paklaus
   * takes the input of the GUI and if suitable creates a pieces and adds it to the mapEditor
   */
  public void addPiece(){
    PieceDescription piece = createPiece();
    if(piece == null){
      return;
    }
    mapEditorController.addPiece(piece);
    mapEditorController.placePieces();
    closeWindow();
  }
  /**
   * @author paklaus
   * @return piecesdescription of a pieces that is created from the input of the GUI
   */
  public PieceDescription createPiece(){
    errorMessage.setText("");
    PieceDescription pieceDescription = new PieceDescription();
    if(iconComboBox.getValue() == null){
      errorMessage.setText("Please select a piece icon");
      return null;
    }
    if(mapEditorController.getTheme().equals("gras")){
      pieceDescription.setType(iconComboBox.getValue().toLowerCase());
    }else{
      pieceDescription.setType("water_" + iconComboBox.getValue().toLowerCase());
    }
    if(Integer.parseInt(amountPowerStr.getText()) <= 0){
      errorMessage.setText("Please enter an power greater than 0");
      return null;
    }
    pieceDescription.setAttackPower(Integer.parseInt(amountPowerStr.getText()));
    if(Integer.parseInt(amountPiecesStr.getText()) <= 0){
      errorMessage.setText("Please enter an amount greater than 0");
      return null;
    }
    pieceDescription.setCount(Integer.parseInt(amountPiecesStr.getText()));
    Movement movement = new Movement();
      ShapeType shapeType = ShapeType.lshape;
      Shape shape = new Shape();
      shape.setType(shapeType);
      movement.setShape(shape);
    pieceDescription.setMovement(movement);
    Stage stage = (Stage) closeButton.getScene().getWindow();
    stage.close();
    return pieceDescription;
  }
  /**
   * @author paklaus
   * switches to the LShape Piece Editor
   */
  public void switchToPieceEditorMovement(){
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/PieceEditorMovement.fxml"));
    Parent root = null;
    try {
      root = fxmlLoader.load();
    } catch (IOException ex) {
      System.out.println("Error loading fxml file in PieceEditorMovementController");
    }
    PieceEditorMovementController controller = fxmlLoader.getController();
    controller.setMapEditorController(this.mapEditorController);
    controller.setMapEditorScene(this.mapEditorController.getPlayingField().getScene());
    Stage popupStage = new Stage();
    popupStage.initOwner(mapEditorController.getPlayingField().getScene().getWindow());
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.initStyle(StageStyle.TRANSPARENT);

    assert root != null;
    Scene scene = new Scene(root);
    popupStage.setScene(scene);
    popupStage.show();


    Stage stage = (Stage) normalMovement.getScene().getWindow();
    stage.close();
  }

  /** @author paklaus*/
  public void setMapEditorScene(Scene mapEditorScene){
    this.mapEditorScene = mapEditorScene;
  }
  /** @author paklaus*/
  public void setMapEditorController(MapEditorController mapEditorController){
    this.mapEditorController = mapEditorController;
  }

  public Scene getMapEditorScene() {
    return mapEditorScene;
  }
}
