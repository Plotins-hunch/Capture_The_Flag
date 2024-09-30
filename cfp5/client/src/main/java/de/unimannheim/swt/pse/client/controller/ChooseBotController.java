package de.unimannheim.swt.pse.client.controller;

import de.unimannheim.swt.pse.ai.advancedMCTS.AdvancedMCTSPlayer;
import de.unimannheim.swt.pse.ai.mcts.MCTSPlayer;
import de.unimannheim.swt.pse.ai.minimax.MinimaxBot;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Controller for the choose bot screen that is shown when the user wants to add a bot to the game
 */
public class ChooseBotController {

  public ImageView botBtn;

  public AnchorPane angryBotBtn;
  public AnchorPane happyBotBtn;
  public AnchorPane mehBotBtn;
  @FXML
  private Button addBotBtn;

  /**
   * Saves the bot selected by the user
   */
  private String botType;

  private String ip;
  private String gameSessionId;

  public void initialize() {
    this.botType = "";
    this.happyBotBtn.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/bot_easy.png")).toString());
      ImageView imgView = new ImageView(img);
      imgView.setFitHeight(happyBotBtn.getHeight());
      imgView.setFitWidth(happyBotBtn.getWidth());
      this.happyBotBtn.getChildren().add(imgView);
    });

    this.mehBotBtn.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/bot_medium.png")).toString());
      ImageView imgView = new ImageView(img);
      imgView.setFitHeight(mehBotBtn.getHeight());
      imgView.setFitWidth(mehBotBtn.getWidth());
      this.mehBotBtn.getChildren().add(imgView);
    });
    this.angryBotBtn.heightProperty().addListener((observable, oldValue, newValue) -> {
      Image img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/bot_hard.png")).toString());
      ImageView imgView = new ImageView(img);
      imgView.setFitHeight(angryBotBtn.getHeight());
      imgView.setFitWidth(angryBotBtn.getWidth());
      this.angryBotBtn.getChildren().add(imgView);
    });

  }

  /**
   * Adds a bot to the game based on the bot type selected by the user
   * @author aemsbach
   * @param ignored ActionEvent that is triggered when the user clicks on the add bot button
   */
  @FXML
  void addBotClicked(ActionEvent ignored) {
    switch(botType) {
      case "happy":
        System.out.println("Happy bot added");
        MinimaxBot.startMinimaxBot(this.ip, this.gameSessionId);

        break;
      case "medium":
        System.out.println("Medium bot added");
        MCTSPlayer.startMCTSBot(this.ip, this.gameSessionId);
        break;
      case "angry":
        System.out.println("Angry bot added");
        AdvancedMCTSPlayer.startMCTSBot(this.ip, this.gameSessionId);
        break;
      default:
        System.out.println("No bot added");
    }
    Stage stage = (Stage) addBotBtn.getScene().getWindow();
    stage.close();

  }

  /**
   * Changes the bot type to easy when the user clicks on the easy bot button
   * author aemsbach
   * @param ignored MouseEvent that is triggered when the user clicks on the easy bot button
   */
  public void happyBotClicked(MouseEvent ignored) {
    this.botType = "happy";
    Image img = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/bot_easySelected.png"))
            .toString());
    ImageView imgView = new ImageView(img);
    imgView.setFitHeight(this.happyBotBtn.getHeight());
    imgView.setFitWidth(this.happyBotBtn.getWidth());
    this.happyBotBtn.getChildren().remove(0);
    this.happyBotBtn.getChildren().add(0, imgView);
    img = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/bot_medium.png"))
            .toString());
    imgView = new ImageView(img);
    imgView.setFitHeight(this.mehBotBtn.getHeight());
    imgView.setFitWidth(this.mehBotBtn.getWidth());
    this.mehBotBtn.getChildren().remove(0);
    this.mehBotBtn.getChildren().add(0, imgView);
    img = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/bot_hard.png"))
            .toString());
    imgView = new ImageView(img);
    imgView.setFitHeight(this.angryBotBtn.getHeight());
    imgView.setFitWidth(this.angryBotBtn.getWidth());
    this.angryBotBtn.getChildren().remove(0);
    this.angryBotBtn.getChildren().add(0, imgView);

  }

  /**
   * Changes the bot type to medium when the user clicks on the medium bot button
   * author aemsbach
   * @param ignored MouseEvent that is triggered when the user clicks on the medium bot button
   */
  public void mediumBotClicked(MouseEvent ignored) {
    this.botType = "medium";

    Image img = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/bot_mediumSelected.png"))
            .toString());
    ImageView imgView = new ImageView(img);
    imgView.setFitHeight(this.mehBotBtn.getHeight());
    imgView.setFitWidth(this.mehBotBtn.getWidth());
    this.mehBotBtn.getChildren().remove(0);
    this.mehBotBtn.getChildren().add(0, imgView);
    img = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/bot_easy.png"))
            .toString());
    imgView = new ImageView(img);
    imgView.setFitHeight(this.happyBotBtn.getHeight());
    imgView.setFitWidth(this.happyBotBtn.getWidth());
    this.happyBotBtn.getChildren().remove(0);
    this.happyBotBtn.getChildren().add(0, imgView);
    img = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/bot_hard.png"))
            .toString());
    imgView = new ImageView(img);
    imgView.setFitHeight(this.angryBotBtn.getHeight());
    imgView.setFitWidth(this.angryBotBtn.getWidth());
    this.angryBotBtn.getChildren().remove(0);
    this.angryBotBtn.getChildren().add(0, imgView);


  }

  /**
   * Changes the cursor to a hand when the mouse enters the bot button and changes the button color to white
   * @author aemsbach
   * @param mouseEvent MouseEvent that is triggered when the mouse enters the bot button
   */
  public void enterBot(MouseEvent mouseEvent) {
    this.addBotBtn.getScene().setCursor(Cursor.HAND);
    Label label = (Label) mouseEvent.getSource();
    Image img;
    if(label.getText().equals("happyBot")) {
      img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/bot_easySelected.png"))
              .toString());
      ImageView imgView = new ImageView(img);
      imgView.setFitHeight(label.getHeight());
      imgView.setFitWidth(label.getWidth());
      this.happyBotBtn.getChildren().remove(0);
      this.happyBotBtn.getChildren().add(0, imgView);
    }
    else if(label.getText().equals("mehBot")) {
      img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/bot_mediumSelected.png"))
              .toString());
      ImageView imgView = new ImageView(img);
      imgView.setFitHeight(label.getHeight());
      imgView.setFitWidth(label.getWidth());
      this.mehBotBtn.getChildren().remove(0);
      this.mehBotBtn.getChildren().add(0, imgView);
    }
    else if(label.getText().equals("angryBot")) {
      img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/bot_hardSelected.png"))
              .toString());
      ImageView imgView = new ImageView(img);
      imgView.setFitHeight(label.getHeight());
      imgView.setFitWidth(label.getWidth());
      this.angryBotBtn.getChildren().remove(0);
      this.angryBotBtn.getChildren().add(0, imgView);
    }
    else{
      img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/buttonSmall_white.png"))
              .toString());
      this.botBtn.setImage(img);

    }

  }

  /**
   * Changes the cursor back to the default when the mouse leaves the bot button and changes the button color back to normal
   * author aemsbach
   * @param mouseEvent MouseEvent that is triggered when the mouse leaves the bot button
   */
  public void exitBot(MouseEvent mouseEvent) {
    this.addBotBtn.getScene().setCursor(Cursor.DEFAULT);
    Image backBtn = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/buttonSmall.png")).toString());
    this.botBtn.setImage(backBtn);
    Label label = (Label) mouseEvent.getSource();
    Image img;
    if(label.getText().equals("happyBot") && !this.botType.equals("happy")) {
      img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/bot_easy.png"))
              .toString());
      ImageView imgView = new ImageView(img);
      imgView.setFitHeight(label.getHeight());
      imgView.setFitWidth(label.getWidth());
      this.happyBotBtn.getChildren().remove(0);
      this.happyBotBtn.getChildren().add(0, imgView);
    }
    else if(label.getText().equals("mehBot") && !this.botType.equals("medium")) {
      img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/bot_medium.png"))
              .toString());
      ImageView imgView = new ImageView(img);
      imgView.setFitHeight(label.getHeight());
      imgView.setFitWidth(label.getWidth());
      this.mehBotBtn.getChildren().remove(0);
      this.mehBotBtn.getChildren().add(0, imgView);
    }
    else if(label.getText().equals("angryBot") && !this.botType.equals("angry")) {
      img = new Image(
          Objects.requireNonNull(getClass().getResource("/assets/bot_hard.png"))
              .toString());
      ImageView imgView = new ImageView(img);
      imgView.setFitHeight(label.getHeight());
      imgView.setFitWidth(label.getWidth());
      this.angryBotBtn.getChildren().remove(0);
      this.angryBotBtn.getChildren().add(0, imgView);
    }
  }

  /**
   * Changes the bot type to hard when the user clicks on the hard bot button
   * author aemsbach
   * @param ignored MouseEvent that is triggered when the user clicks on the hard bot button
   */
  public void angryBotClicked(MouseEvent ignored) {
    this.botType = "angry";
    Image img = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/bot_hardSelected.png"))
            .toString());
    ImageView imgView = new ImageView(img);
    imgView.setFitHeight(this.angryBotBtn.getHeight());
    imgView.setFitWidth(this.angryBotBtn.getWidth());
    this.angryBotBtn.getChildren().remove(0);
    this.angryBotBtn.getChildren().add(0, imgView);
    img = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/bot_easy.png"))
            .toString());
    imgView = new ImageView(img);
    imgView.setFitHeight(this.happyBotBtn.getHeight());
    imgView.setFitWidth(this.happyBotBtn.getWidth());
    this.happyBotBtn.getChildren().remove(0);
    this.happyBotBtn.getChildren().add(0, imgView);
    img = new Image(
        Objects.requireNonNull(getClass().getResource("/assets/bot_medium.png"))
            .toString());
    imgView = new ImageView(img);
    imgView.setFitHeight(this.mehBotBtn.getHeight());
    imgView.setFitWidth(this.mehBotBtn.getWidth());
    this.mehBotBtn.getChildren().remove(0);
    this.mehBotBtn.getChildren().add(0, imgView);

  }
  /**
   * Passes the IP and game session ID to the controller
   * author aemsbach
   * @param ip the IP of the server
   * @param gameSessionId the ID of the game session
   */
  public void passInfo(String ip, String gameSessionId) {
    this.ip = ip;
    this.gameSessionId = gameSessionId;
    this.addBotBtn.sceneProperty().addListener((observable, oldValue, newValue) -> newValue.getRoot().setStyle("-fx-background-color: linear-gradient(to bottom, burlywood, lightgoldenrodyellow);"));

  }
}
