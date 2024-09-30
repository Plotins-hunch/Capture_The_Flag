package de.unimannheim.swt.pse.client.controller;

import de.unimannheim.swt.pse.client.GUI;
import de.unimannheim.swt.pse.client.HelperMethods;
import de.unimannheim.swt.pse.client.MapPreview;
import de.unimannheim.swt.pse.client.RequestHandler;
import de.unimannheim.swt.pse.server.CtfApplication;
import de.unimannheim.swt.pse.server.controller.data.GameSessionRequest;
import de.unimannheim.swt.pse.server.controller.data.GameSessionResponse;
import de.unimannheim.swt.pse.server.database.DatabaseInitializer;
import de.unimannheim.swt.pse.server.database.controller.GameSessionsController;
import de.unimannheim.swt.pse.server.database.controller.MapController;
import de.unimannheim.swt.pse.server.database.dao.GameSessionsDAO;
import de.unimannheim.swt.pse.server.database.dao.MapDAO;
import de.unimannheim.swt.pse.server.database.model.GameSessionsModel;
import de.unimannheim.swt.pse.server.database.service.GameSessionsService;
import de.unimannheim.swt.pse.server.database.service.MapService;
import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Controller class for the select map scene
 * The user selects the map they want to play on and whether they want to play or watch the game
 */
public class SelectMapController {


    /**
     * the back button
     */
    public AnchorPane backButtonPane;
    /**
     * the base pane
     */
    public AnchorPane bgPane;
    /**
     * the create map button
     */
    public AnchorPane createAnchorPane;
    /**
     * The split pane
     */
    public SplitPane splitPane;
    /**
     * the class's requestHandler used to send requests to the server
     */
    private RequestHandler requestHandler;
    /**
     * the map the user selects to play on
     */
    private MapTemplate map;

    /**
     * the media player used to play music
     */
    private MediaPlayer mediaPlayer;

    /** Container to place the map 1*/
    @FXML
    private AnchorPane map1Container;
    /** Container to place the map 2*/
    @FXML
    private AnchorPane map2Container;
    /** Container to place the map 3*/
    @FXML
    private AnchorPane map3Container;


    /**
     * This method is called when the user clicks on the back button and leads back to the main menu
     * @author aemsbach
     * @param event the mouse event
     */
    @FXML
    void backToLandingPage(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/LandingPage.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            LandingPageController landingPageController = fxmlLoader.getController();
            landingPageController.passInfo(mediaPlayer);


            stage.setScene(scene);
            Screen screen = Screen.getPrimary();
            stage.setHeight(screen.getVisualBounds().getHeight());
            stage.setWidth(screen.getVisualBounds().getWidth());
            stage.show();
            CtfApplication.kill();
        } catch (IOException ignored) {


        }
    }

    /**
     * Method used to pass the requestHandler and mediaPlayer to the controller
     * @author aemsbach
     * @param requestHandler sends requests to server
     */
    public void passInfo(RequestHandler requestHandler, MediaPlayer mediaPlayer){
        this.requestHandler = requestHandler;
        this.mediaPlayer = mediaPlayer;

    }

    /**
     * initializes the scene by setting the background image and the size of the buttons
     * @author aemsbach
     */
    @FXML
    public void initialize() throws IOException {
        new DatabaseInitializer().initialize();
        // Instantiate MapService and MapController
        MapDAO mapDAO = new MapDAO();
        MapService mapService = new MapService(mapDAO);
        MapController mapController = new MapController(mapService);

        // Retrieve all maps created by a specific user

        if (GUI.userId != null) {
            try {
                GUI.maps.clear();
                CompletableFuture<List<MapTemplate>> mapsFuture = mapController.getAllMapsByUser(
                    GUI.userId);
                List<MapTemplate> maps = mapsFuture.get(); // Blocking call to wait for map retrieval
                GUI.maps.addAll(0, maps);
            }
            catch(InterruptedException | ExecutionException e) {
                System.err.println("Error during map retrieval: " + e.getMessage());

            }
        }
        //backButton.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        this.backButtonPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            Image back = new Image(Objects.requireNonNull(getClass().getResource("/assets/buttonklein.png")).toString());
            ImageView backView = new ImageView(back);
            backView.setFitHeight(newValue.doubleValue());
            backView.setFitWidth(this.backButtonPane.getWidth());

            this.backButtonPane.getChildren().add(backView);
        });

        this.createAnchorPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            Image img = new Image(Objects.requireNonNull(getClass().getResource("/assets/woodButton1.png")).toString());
            ImageView imageView = new ImageView(img);
            imageView.setFitHeight(newValue.doubleValue());
            imageView.setFitWidth(this.createAnchorPane.getWidth());
            this.createAnchorPane.getChildren().add(imageView);
        });
        this.splitPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            Image img = new Image(Objects.requireNonNull(getClass().getResource("/assets/background.png")).toString());
            this.splitPane.setStyle("-fx-background-image: url('" + img.getUrl() + "'); " +
                "-fx-background-size: cover; -fx-background-repeat: no-repeat;");

        });
        this.bgPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue.doubleValue() == 0 && newValue.doubleValue() != 0){
                this.bgPane.getScene().getWindow().setHeight(this.bgPane.getScene().getWindow().getHeight()-1);
            }
        });
        this.bgPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue.doubleValue() == 0 && newValue.doubleValue() != 0){
                System.out.println("width");
                this.bgPane.getScene().getWindow().setWidth(this.bgPane.getScene().getWindow().getWidth()-1);
            }
        });
        this.map1Container.heightProperty().addListener((observable, oldValue, newValue) -> {
            map1Container.getChildren().clear();
                MapPreview mp = new MapPreview(GUI.maps.get(0));
                mp.createMapArr();
                mp.createPreview(map1Container.getWidth(), map1Container.getHeight());
                map1Container.getChildren().add(mp.getplayingField());
            });
        this.map2Container.heightProperty().addListener((observable, oldValue, newValue) -> {
            map2Container.getChildren().clear();
            MapPreview mp = new MapPreview(GUI.maps.get(1));
            mp.createMapArr();
            mp.createPreview(map2Container.getWidth(), map2Container.getHeight());
            map2Container.getChildren().add(mp.getplayingField());
        });
        this.map3Container.heightProperty().addListener((observable, oldValue, newValue) -> {
            map3Container.getChildren().clear();
            MapPreview mp = new MapPreview(GUI.maps.get(2));
            mp.createMapArr();
            mp.createPreview(map3Container.getWidth(), map3Container.getHeight());
            map3Container.getChildren().add(mp.getplayingField());
        });
    }

    /**
     * Method used to show the popup for the user to select whether they want to host as a player or spectator
     * @author aemsbach
     * @param event the mouse event
     * @throws IOException if the fxml file is not found
     */
    void showPopup(MouseEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/HostingGame.fxml")));
        Parent root = fxmlLoader.load();
        HostingGameController hostingGameController = fxmlLoader.getController();
        hostingGameController.setSelectMapController(this);
        Stage popupStage = new Stage();
        popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
        popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        //popupStage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(root);

        popupStage.setScene(scene);
        popupStage.show();





    }

    /**
     * Method used to switch to the waiting room after the user has selected whether they want to host as a player or spectator
     * @author aemsbach
     * @param ignoredEvent the mouse event
     * @param map the map the user selected
     * @param teamId the team the user wants to join
     * @throws IOException if the fxml file is not found
     */
    public void switchToWaitingRoom(MouseEvent ignoredEvent, MapTemplate map, String teamId) throws IOException {
        //Create a new game session request
        GameSessionRequest gameSessionRequest = new GameSessionRequest();
        gameSessionRequest.setTemplate(map);
        GameSessionResponse response = requestHandler.createGameSession(gameSessionRequest);

        String localhost;
        try {
            localhost = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }


        System.out.println("This is the responseId: " + response.getId());
        new DatabaseInitializer().initialize();
        GameSessionsModel gameSessionsModel = new GameSessionsModel(response.getId(), localhost, false);
        GameSessionsDAO gameSessionsDAO = new GameSessionsDAO();
        GameSessionsService gameSessionsService = new GameSessionsService(gameSessionsDAO);
        GameSessionsController gameSessionsController = new GameSessionsController(gameSessionsService);
        // Add a new game session
        gameSessionsController.addGameSession(gameSessionsModel);
        //Switch to the host waiting room
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/HostScreen.fxml")));
        Parent root = fxmlLoader.load();
        //Pass the game session id and the requestHandler to the host screen
        HostScreenController hostScreen = fxmlLoader.getController();
        hostScreen.passInfo(response.getId(), requestHandler, map, teamId, mediaPlayer);
        System.out.println(response.getId());
        Stage stage = (Stage)  this.createAnchorPane.getScene().getWindow();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource("/css/HostScreenStyle.css")).toExternalForm());


        stage.setScene(scene);
        Screen screen = Screen.getPrimary();
        stage.setHeight(screen.getVisualBounds().getHeight());
        stage.setWidth(screen.getVisualBounds().getWidth());

    }


    /**
     * Method used to select the third map and show the popup for the user to select whether they want to host as a player or spectator
     * @author aemsbach
     * @param mouseEvent the mouse event
     * @throws IOException if the fxml file is not found
     */
    public void selectMap3(MouseEvent mouseEvent) throws IOException {
        this.map = GUI.maps.get(2);
        this.showPopup(mouseEvent);
    }

    /**
     * Method used to select the second map and show the popup for the user to select whether they want to host as a player or spectator
     * @author aemsbach
     * @param mouseEvent the mouse event
     * @throws IOException if the fxml file is not found
     */
    public void selectMap2(MouseEvent mouseEvent) throws IOException {
        this.map = GUI.maps.get(1);
        this.showPopup(mouseEvent);
    }

    /**
     * Method used to select the first map and show the popup for the user to select whether they want to host as a player or spectator
     * @author aemsbach
     * @param mouseEvent the mouse event
     */
    public void selectMap1(MouseEvent mouseEvent) {
        try {
            this.map = GUI.maps.get(0);
            this.showPopup( mouseEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method used to host as a spectator and switch to the waiting room scene
     * @author aemsbach
     * @param actionEvent the mouse event
     * @throws IOException if the fxml file is not found
     */
    public void hostAsSpectator(MouseEvent actionEvent) throws IOException {
        this.switchToWaitingRoom(actionEvent, this.map, "Spectator");
    }

    /**
     * Method used to host as a player and switch to the waiting room scene
     * @author aemsbach
     * @param actionEvent the mouse event
     * @throws IOException if the fxml file is not found
     */
    public void hostAsPlayer(MouseEvent actionEvent) throws IOException {
        this.switchToWaitingRoom(actionEvent, this.map, "Team1");
    }

    /**
     * Method used to change mouse from hand to normal cursor when mouse exits the button
     * @author aemsbach
     * @param mouseEvent the mouse event
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
     * Method used to change mouse from normal to hand cursor when mouse enters the button
     * @author aemsbach
     * @param mouseEvent the mouse event
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
        this.backButtonPane.getChildren().add(0,img);}
        else{
            img = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/assets/woodButton1_white.png")).toString()));
            img.setFitWidth(label.getWidth());
            img.setFitHeight(label.getHeight());
            this.createAnchorPane.getChildren().remove(0);
            this.createAnchorPane.getChildren().add(0,img);

        }
    }

    public void switchToMapEditor(MouseEvent mouseEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/MapEditorMenu.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        Screen screen = Screen.getPrimary();
        stage.setHeight(screen.getVisualBounds().getHeight());
        stage.setWidth(screen.getVisualBounds().getWidth());

        stage.show();
    }
}
