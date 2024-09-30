package de.unimannheim.swt.pse.client.controller;

import de.unimannheim.swt.pse.client.GUI;
import de.unimannheim.swt.pse.client.HelperMethods;
import de.unimannheim.swt.pse.client.MapPreview;

import de.unimannheim.swt.pse.server.CtfApplication;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * @author paklaus
 * Controller for the MapEditorMenu.fxml
 * first Menu when entering the controller in order to choose to edit a map or create a new one
 */
public class MapEditorMenuController implements Initializable {
  /** placeholder for map 1 */
  @FXML
  AnchorPane preview1;
  /** placeholder for map 2 */
  @FXML
  AnchorPane preview2;
  /** placeholder for map 3 */
  @FXML
  AnchorPane preview3;
  /** Pane for the back button 1 */
  @FXML
  AnchorPane backButtonPane;
  /** Anchor Pane for the create button */
  @FXML
  AnchorPane createAnchorPane;
  /** background of the scene */
  @FXML
  AnchorPane bgPane;
  /** background of the scene */
  @FXML
  SplitPane splitPane;

  /**
  * @author paklaus
   * @param mouseEvent MouseEvent
   * Switches to the landing page
   */
  public void switchToLandingPage(MouseEvent mouseEvent) throws IOException {
    try{
      CtfApplication.kill();
    }catch(Exception ignored){}
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/LandingPage.fxml"));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    stage.setOnCloseRequest(event -> {
      try {
        CtfApplication.kill();
      } catch (Exception ex) {
        System.out.println("Error while switching the landing page");
      }
    });
    Scene scene = new Scene(root);
    stage.setScene(scene);

    Screen screen = Screen.getPrimary();
    stage.setHeight(screen.getVisualBounds().getHeight());
    stage.setWidth(screen.getVisualBounds().getWidth());

    stage.show();
  }

  /**
   * @author paklaus
   * @param mouseEvent MouseEvent
   * Switches to the map editor page
   */
  public void switchToMapEditor(MouseEvent mouseEvent) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/MapEditor.fxml"));
    Parent root = fxmlLoader.load();
    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();

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
    else{
      img = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/assets/woodButton1.png")).toString()));
      img.setFitWidth(label.getWidth());
      img.setFitHeight(label.getHeight());
      this.createAnchorPane.getChildren().remove(0);
      this.createAnchorPane.getChildren().add(0,img);

    }

  }

  /**
   * @author paklaus
   * @param url arg 1
   * @param resourceBundle arg 2
   *  * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.preview1.heightProperty().addListener((observable, oldValue, newValue) -> {
      MapPreview preview = new MapPreview(GUI.maps.get(0));
      preview.createMapArr();
      preview.createPreview(preview1.getWidth(), preview1.getHeight());
      preview1.getChildren().add(preview.getplayingField());
    });
    this.preview2.heightProperty().addListener((observable, oldValue, newValue) -> {
      MapPreview preview = new MapPreview(GUI.maps.get(1));
      preview.createMapArr();
      preview.createPreview(preview2.getWidth(), preview2.getHeight());
      preview2.getChildren().add(preview.getplayingField());
    });
    this.preview3.heightProperty().addListener((observable, oldValue, newValue) -> {
      MapPreview preview = new MapPreview(GUI.maps.get(2));
      preview.createMapArr();
      preview.createPreview(preview3.getWidth(), preview3.getHeight());
      preview3.getChildren().add(preview.getplayingField());
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
      this.createAnchorPane.getChildren().add(imageView);
    });
    this.splitPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
      if (oldValue == null && newValue != null) {
        Image img = new Image(Objects.requireNonNull(getClass().getResource("/assets/background.png")).toString());
        splitPane.setStyle("-fx-background-image: url('" + img.getUrl() + "'); " +
            "-fx-background-size: cover; -fx-background-repeat: no-repeat;");
        System.out.println("Scene set");
      }
    });
  }
  /**
   * @author paklaus
   * @param mouseEvent MouseEvent
   *  * moves to the map editor with the map template that was clicked on, already selected (Map 1)
   */
  public void editMap1(MouseEvent mouseEvent) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/MapEditor.fxml"));
    Parent root = fxmlLoader.load();
    MapEditorController controller = fxmlLoader.getController();
    controller.setMap(GUI.maps.get(0));

    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
  /**
   * @author paklaus
   * @param mouseEvent MouseEvent
   * moves to the map editor with the map template that was clicked on, already selected (Map 2)
   */
  public void editMap2(MouseEvent mouseEvent) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/MapEditor.fxml"));
    Parent root = fxmlLoader.load();
    MapEditorController controller = fxmlLoader.getController();
    controller.setMap(GUI.maps.get(1));

    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
  /**
   * @author paklaus
   * @param mouseEvent MouseEvent
   * moves to the map editor with the map template that was clicked on, already selected (Map 3)
   */
  public void editMap3(MouseEvent mouseEvent) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/fxml/MapEditor.fxml"));
    Parent root = fxmlLoader.load();
    MapEditorController controller = fxmlLoader.getController();
    controller.setMap(GUI.maps.get(2));

    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

}
