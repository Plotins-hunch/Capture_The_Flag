package de.unimannheim.swt.pse.client.controller;

import de.unimannheim.swt.pse.client.HelperMethods;
import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
/**
 * Controller for the Hosting Game Dialog that allows user to host a game as a player or spectator
 */
public class HostingGameController {

  public AnchorPane bgPane;
  public AnchorPane spectate;
  public AnchorPane playerPane;
  private SelectMapController selectMapController;
  /**
   * Initializes the controller by setting the background image of the dialog
   */
  @FXML
  public void initialize() {
    //set button image
    this.spectate.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image btn = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
      ImageView imageView = new ImageView(btn);
      imageView.setFitWidth(this.spectate.getWidth());
      imageView.setFitHeight(this.spectate.getHeight());
      this.spectate.getChildren().add(imageView);
    });
    //set button images
    this.playerPane.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image btn = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
      ImageView imageView = new ImageView(btn);
      imageView.setFitWidth(this.playerPane.getWidth());
      imageView.setFitHeight(this.playerPane.getHeight());
      this.playerPane.getChildren().add(imageView);
    });
    //set background image
    this.bgPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
      if (oldValue == null && newValue != null) {
        newValue.getRoot().setStyle("-fx-background-color: linear-gradient(to bottom, burlywood, lightgoldenrodyellow);");

      }
    });
  }

  public void setSelectMapController(SelectMapController selectMapController) {
    this.selectMapController = selectMapController;
  }

  public void hostAsSpectator(MouseEvent e) throws IOException {
    this.closeWindow(e);
    this.selectMapController.hostAsSpectator(e);

  }

  public void hostAsPlayer(MouseEvent actionEvent) throws IOException {
    this.closeWindow(actionEvent);
    this.selectMapController.hostAsPlayer(actionEvent);

  }

  public void closeWindow(MouseEvent ignoredE){
    Stage stage = (Stage) this.bgPane.getScene().getWindow();
    stage.close();
  }

  public void mouseEntered(MouseEvent mouseEvent) {
    HelperMethods.mouseEntered(mouseEvent);
    Image backBtn = new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonSmall_white.png")).toString());
    ImageView img = new ImageView(backBtn);
    Label label = (Label) mouseEvent.getSource();
    img.setFitHeight(label.getHeight());
    img.setFitWidth(label.getWidth());
    if(label.getText().equals("Play")){
      this.playerPane.getChildren().remove(0);
      this.playerPane.getChildren().add(0,img);}
    else{
      this.spectate.getChildren().remove(0);
      this.spectate.getChildren().add(0,img);
    }
  }

  public void mouseExited(MouseEvent mouseEvent) {
    HelperMethods.mouseExited(mouseEvent);
    Image backBtn = new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
    ImageView img = new ImageView(backBtn);
    Label label = (Label) mouseEvent.getSource();
    img.setFitHeight(label.getHeight());
    img.setFitWidth(label.getWidth());
    if(label.getText().equals("Play")){
      this.playerPane.getChildren().remove(0);
      this.playerPane.getChildren().add(0,img);}
    else{
      this.spectate.getChildren().remove(0);
      this.spectate.getChildren().add(0,img);
    }

  }
}
