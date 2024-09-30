package de.unimannheim.swt.pse.client;

import de.unimannheim.swt.pse.server.game.map.PieceDescription;
import de.unimannheim.swt.pse.server.game.map.PlacementType;
import de.unimannheim.swt.pse.server.game.state.Piece;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author paklaus
 includes helping methods for the MapEditor
 */

public class HelperMethods {
  /**
   * possible icons for a piece in Land mode
   */
  static String imgAddressesLand = "/assets/pieces/";
  /**
   * possible icons for a piece in water mode
   */
  static String imgAddressesWater = "/assets/water_";

  public void switchScene(String fxml, ActionEvent event) {
    try {
      Parent root = FXMLLoader.load(
          Objects.requireNonNull(getClass().getClassLoader().getResource(fxml)));
      Scene scene = new Scene(root);
      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.setScene(scene);
      stage.show();
    } catch (IOException e) {
      System.out.println("Scene couldn't be switched");
    }
  }

  public void switchSceneWithCSS(String fxml, ActionEvent event, String css) {
    try {
      Parent root = FXMLLoader.load(
          Objects.requireNonNull(getClass().getClassLoader().getResource(fxml)));
      Scene scene = new Scene(root);
      scene.getStylesheets().add(
          Objects.requireNonNull(getClass().getClassLoader().getResource(css)).toExternalForm());
      Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      stage.setScene(scene);
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void mouseEntered(MouseEvent e){
    Scene scene = e.getPickResult().getIntersectedNode().getScene();
    scene.setCursor(javafx.scene.Cursor.HAND);
  }
  public static void mouseExited(MouseEvent e){
    Scene scene = e.getPickResult().getIntersectedNode().getScene();
    scene.setCursor(javafx.scene.Cursor.DEFAULT);
  }
  /**@author jdeiting
   * @param areaInp inputs the playGrid
   * @param teamPiecesInp inputs the pieces that are placed on the map
   * places pieces in a spaced out fashion
   */
  public static String[][] placePiecesSpacedOut(String[][] areaInp, Piece[] teamPiecesInp) {

    //initialize rangeMatrix
    double[][] rangeMatrix = new double[areaInp.length][areaInp[0].length];
    int maxRangeInit = (int) Math.sqrt(
        (rangeMatrix.length * rangeMatrix.length) + (rangeMatrix[0].length
            * rangeMatrix[0].length)) + 1;
    //set every entry to max
    for (int i = 0; i < rangeMatrix.length; i++) {
      for (int j = 0; j < rangeMatrix[0].length; j++) {
        rangeMatrix[i][j] = maxRangeInit;
      }
    }
    //set BaseEntry as 0
    rangeMatrix[areaInp.length / 2][areaInp[0].length / 2] = 0;
    //place every Piece and set their position
    for (Piece piece : teamPiecesInp) {
      int[] pieceCoordinate = getMaxRange(rangeMatrix);
      areaInp[pieceCoordinate[0]][pieceCoordinate[1]] =
          "p:" + piece.getTeamId() + "_" + piece.getId();
      piece.setPosition(pieceCoordinate);
      rangeMatrix = updateRangeMatrix(rangeMatrix, pieceCoordinate);
    }
    return areaInp;
  }
  /**@author jdeiting
   * @param rangeMatrixInp inputs the rangeMatrix
   * @return the coordinate of the max range in the rangeMatrix
  *
   */
  public static int[] getMaxRange(double[][] rangeMatrixInp) {
    int[] resultCoordinate = new int[2];
    double maxValue = 0;
    for (int i = 0; i < rangeMatrixInp.length; i++) {
      for (int j = 0; j < rangeMatrixInp[0].length; j++) {
        if (maxValue < rangeMatrixInp[i][j]) {
          resultCoordinate[0] = i;
          resultCoordinate[1] = j;
          maxValue = rangeMatrixInp[i][j];
        }
      }
    }
    return resultCoordinate;
  }
  /**@author jdeiting
   * @param rangeMatrixInp inputs the rangeMatrix
   * @param piecePositionInp inputs the position of the piece
   * @return the updated rangeMatrix
   */
  public static double[][] updateRangeMatrix(double[][] rangeMatrixInp, int[] piecePositionInp) {
    //usage of readable variables
    int piecePosX = piecePositionInp[0];
    int piecePosY = piecePositionInp[1];
    //set added Pieces range to 0
    rangeMatrixInp[piecePosX][piecePosY] = 0;
    //updating range entries based on added piece
    for (int i = 0; i < rangeMatrixInp.length; i++) {
      for (int j = 0; j < rangeMatrixInp[0].length; j++) {
        double rangeTemp = Math.sqrt(
            ((piecePosX - i) * (piecePosX - i)) + ((piecePosY - j) * (piecePosY - j)));
        if (rangeMatrixInp[i][j] > rangeTemp) {
          rangeMatrixInp[i][j] = rangeTemp;
        }
      }
    }
    return rangeMatrixInp;
  }
  /**@author jdeiting
   * @param areaInp inputs the playGrid
   * @param teamPiecesInp inputs the pieces that are placed on the map
   * places pieces in a defensive fashion
   */
  public static String[][] placePiecesDefensive(String[][] areaInp, Piece[] teamPiecesInp) {
    //create Matrix with range
    double[][] rangeMatrix = new double[areaInp.length][areaInp[0].length];
    int baseX = areaInp.length / 2;
    int baseY = areaInp[0].length / 2;

    for (int i = 0; i < rangeMatrix.length; i++) {
      for (int j = 0; j < rangeMatrix[0].length; j++) {
        rangeMatrix[i][j] = Math.sqrt((i - baseX) * (i - baseX) + (j - baseY) * (j - baseY));
      }
    }
    rangeMatrix[baseX][baseY] = Double.MAX_VALUE;

    for (Piece piece : teamPiecesInp) {
      int[] pieceCoordinate = getMinRange(rangeMatrix);
      areaInp[pieceCoordinate[0]][pieceCoordinate[1]] =
          "p:" + piece.getTeamId() + "_" + piece.getId();
      rangeMatrix[pieceCoordinate[0]][pieceCoordinate[1]] = Double.MAX_VALUE;
    }
    return areaInp;
  }
  /**@author jdeiting
   * @param rangeMatrixInp inputs the rangeMatrix
   * @return the coordinate of the min range in the rangeMatrix
   */
  public static int[] getMinRange(double[][] rangeMatrixInp) {
    int[] resultCoordinate = new int[2];
    double minValue = Double.MAX_VALUE;
    for (int i = 0; i < rangeMatrixInp.length; i++) {
      for (int j = 0; j < rangeMatrixInp[0].length; j++) {
        if (minValue > rangeMatrixInp[i][j]) {
          resultCoordinate[0] = i;
          resultCoordinate[1] = j;
          minValue = rangeMatrixInp[i][j];
        }
      }
    }
    return resultCoordinate;
  }
  /**@author jdeiting
   * @param areaInp inputs the playGrid
   * @param teamPiecesInp inputs the pieces that are placed on the map
   * places pieces in a symmetrical fashion
   */
  public static String[][] placePiecesSymmetrical(String[][] areaInp, Piece[] teamPiecesInp) {

    int width = areaInp[0].length;
    int height = areaInp.length;
    boolean isOdd = width % 2 != 0;
    int pieceSize = teamPiecesInp.length;
    boolean needExtra = isOdd ? false : (width - 1) * height - 1 < pieceSize;
    int placementWidth = isOdd || needExtra ? width : width - 1;
    int placementHeight = pieceSize / placementWidth + 1;
    int pieceCounter = 0;
    int piecesLeft = pieceSize;
    int heightCounter = 0;
    int middle = !needExtra && !isOdd ? placementWidth / 2 + 1 : placementWidth / 2;

    while (piecesLeft > placementWidth && heightCounter < placementHeight) {
      for (int i = width - 1; i >= width - placementWidth; i--) {
        if (!isValidPlacement(areaInp[heightCounter][i])) {
          continue;
        }
        areaInp[heightCounter][i] = "p:" + teamPiecesInp[pieceCounter].getTeamId() + "_"
            + teamPiecesInp[pieceCounter].getId();
        piecesLeft--;
        pieceCounter++;
      }
      heightCounter++;
    }
    if (piecesLeft == 0) {
      return areaInp;
    }

    if (placementWidth % 2 != 0 && piecesLeft % 2 == 0) {

      for (int i = 1; i <= middle; i++) {
        if (piecesLeft == 0) {
          break;
        }
        if (!isValidPlacement(areaInp[heightCounter][middle - i])) {
          continue;
        }

        areaInp[heightCounter][middle - i] =
            "p:" + teamPiecesInp[pieceCounter].getTeamId() + "_"
                + teamPiecesInp[pieceCounter].getId();
        pieceCounter++;
        piecesLeft--;
        if (piecesLeft == 0) {
          break;
        }
        if (!isValidPlacement(areaInp[heightCounter][middle + i])) {
          continue;
        }
        areaInp[heightCounter][middle + i] =
            "p:" + teamPiecesInp[pieceCounter].getTeamId() + "_"
                + teamPiecesInp[pieceCounter].getId();
        pieceCounter++;
        piecesLeft--;

      }
      return areaInp;
    }

    int leftPlacement = middle - 1;
    int rightPlacement = middle;
    int modChanger = 0;

    while (piecesLeft > 0) {

      if (modChanger % 2 == 0) {
        if (rightPlacement >= areaInp[0].length || !isValidPlacement(
            areaInp[heightCounter][rightPlacement])) {
          if (piecesLeft == 1 && heightCounter + 1 < height) {
            areaInp[heightCounter + 1][middle] =
                "p:" + teamPiecesInp[pieceCounter].getTeamId() + "_"
                    + teamPiecesInp[pieceCounter].getId();
            break;
          }
          rightPlacement++;
          modChanger++;
          continue;
        }
        areaInp[heightCounter][rightPlacement] =
            "p:" + teamPiecesInp[pieceCounter].getTeamId() + "_"
                + teamPiecesInp[pieceCounter].getId();
        rightPlacement++;
      } else {

        if (piecesLeft == 1 && heightCounter + 1 < height) {
          areaInp[heightCounter + 1][middle] =
              "p:" + teamPiecesInp[pieceCounter].getTeamId() + "_"
                  + teamPiecesInp[pieceCounter].getId();
          break;
        }
        if (leftPlacement >= 0 && !isValidPlacement(
            areaInp[heightCounter][leftPlacement])) {
          leftPlacement++;
          modChanger++;
          continue;
        }
        areaInp[heightCounter][leftPlacement] =
            "p:" + teamPiecesInp[pieceCounter].getTeamId() + "_"
                + teamPiecesInp[pieceCounter].getId();
        leftPlacement--;
      }
      piecesLeft--;
      pieceCounter++;
      modChanger++;
    }
    return areaInp;
  }

  /**
   * @author jdeiting
   * @param placementString inputs the string that is checked
   * @return true if the string is empty
   */
  public static boolean isValidPlacement(String placementString) {
    return placementString.isEmpty();
  }
  /**@author paklaus
   * @param placementGridInp inputs the playGrid
   * @param teamInp          inputs the number of teams participating
   * @return playGrid with Base for each team placed onto it
   */
  public static String[][] placeBaseForTeam(String[][] placementGridInp, int teamInp) {
    //placement center of placementArea
    placementGridInp[placementGridInp.length / 2][placementGridInp[0].length / 2] = "b:" + teamInp;
    return placementGridInp;
  }
  /**@author jdeiting
   * @param gridInp inputs a playGrid
   * @param team    inputs the team for which to rotate the grid
   * @return playGrid but rotated to a teams perspective
   *
   */
  public static String[][] rotateMap(String[][] gridInp, int team) {
    String[][] resultGrid;
    switch (team) {
      case 1:
        //just return
        resultGrid = gridInp;
        break;
      case 2:
        //180 deg
        resultGrid = new String[gridInp.length][gridInp[0].length];
        for (int i = 0; i < resultGrid.length; i++) {
          for (int j = 0; j < resultGrid[0].length; j++) {
            resultGrid[i][j] = gridInp[gridInp.length - 1 - i][gridInp[0].length - 1 - j];
          }
        }
        break;
      //counter-clockwise 90 deg
      case 3:
        resultGrid = new String[gridInp[0].length][gridInp.length];
        for (int i = 0; i < resultGrid.length; i++) {
          for (int j = 0; j < resultGrid[0].length; j++) {
            resultGrid[i][j] = gridInp[j][gridInp[0].length - 1 - i];
          }
        }
        break;
      //clockwise 90 deg
      case 4:
        resultGrid = new String[gridInp[0].length][gridInp.length];
        for (int i = 0; i < resultGrid.length; i++) {
          for (int j = 0; j < resultGrid[0].length; j++) {
            resultGrid[i][j] = gridInp[gridInp.length - 1 - j][i];
          }
        }
        break;
      default:
        System.out.println("This team is not supported");
        throw new IllegalArgumentException("Team not supported");
    }
    return resultGrid;
  }
  /**@author jdeiting
   * @param gridInp inputs a for "team" rotated playGrid
   * @param team    inputs the team for which the grid is currently rotated
   * @return playGrid but rotated back to normal perspective
   *
   */
  public static String[][] rotateMapBack(String[][] gridInp, int team) {
    String[][] resultGrid;
    switch (team) {
      case 1:
        //just return
        resultGrid = gridInp;
        break;
      case 2:
        //180 deg
        resultGrid = new String[gridInp.length][gridInp[0].length];
        for (int i = 0; i < resultGrid.length; i++) {
          for (int j = 0; j < resultGrid[0].length; j++) {
            resultGrid[i][j] = gridInp[gridInp.length - 1 - i][gridInp[0].length - 1 - j];
          }
        }
        break;
      //clockwise 90 deg
      case 3:
        resultGrid = new String[gridInp[0].length][gridInp.length];
        for (int i = 0; i < resultGrid.length; i++) {
          for (int j = 0; j < resultGrid[0].length; j++) {
            resultGrid[i][j] = gridInp[gridInp.length - 1 - j][i];
          }
        }
        break;
      //counter-clockwise 90 deg
      case 4:
        resultGrid = new String[gridInp[0].length][gridInp.length];
        for (int i = 0; i < resultGrid.length; i++) {
          for (int j = 0; j < resultGrid[0].length; j++) {
            resultGrid[i][j] = gridInp[j][gridInp[0].length - 1 - i];
          }
        }
        break;
      default:
        throw new IllegalArgumentException("Team not supported");
    }
    return resultGrid;
  }
  /**
   * @author paklaus
   * Displays the array as a preview of the map on the right side of the screen
   * @param playingFieldArr inputs the playGrid
   * @param playingField inputs the GridPane where the preview should be displayed
   * @param pieces inputs the pieces that are placed on the map
   * @param width inputs the total width (in px) the playing field is allowed to have
   * @param height inputs the total width (in px) the playing field is allowed to have
   * @param preview inputs if the preview is a preview or the actual playing field
   * @param theme inputs the theme of the map
   * takes the array and displays it as a grid pane
   */
  public static void showMap(String[][] playingFieldArr, GridPane playingField, ArrayList<PieceDescription> pieces, double width, double height, boolean preview, String theme){
    if(!preview){
      width -= 100;
      height -= 100;
      width -= (width % playingFieldArr[0].length);
      height -= (height % playingFieldArr.length);
    }
    double cellWidth = width / playingFieldArr[0].length;
    double cellHeight = height / playingFieldArr.length;
    width = cellWidth * playingFieldArr[0].length;
    height = cellHeight * playingFieldArr.length;
    playingField.getChildren().clear();
    playingField.setMinSize(width, height);
    playingField.setPrefSize(width, height);
    playingField.setMaxSize(width, height);
    for(int i = 0; i < playingFieldArr.length; i++){
      for(int j = 0; j < playingFieldArr[0].length; j++) {
        Pane pane = new Pane();
        pane.setMaxSize(cellWidth,cellHeight);
        pane.setMinSize(cellWidth, cellHeight);
        pane.setPrefSize(cellWidth, cellHeight);
        if(playingFieldArr[i][j] == null){
          pane.setStyle("-fx-border-color: black;");
        }
        else if(playingFieldArr[i][j].equals("b")){
          Image barrierImg = new Image(Objects.requireNonNull(HelperMethods.class.getResourceAsStream("/assets/blocks/block" + (((int) (Math.random()*3.0)) + 1 ) + ".png")));
          ImageView barrierImgView = new ImageView(barrierImg);
          barrierImgView.setFitHeight(cellHeight);
          barrierImgView.setFitWidth(cellWidth);
          pane.getChildren().add(barrierImgView);
          pane.setStyle("-fx-border-color: black;");
        }

        else if(playingFieldArr[i][j].contains("p:")){
          String pieceType = playingFieldArr[i][j].split("_")[1];
          int pieceTypeInt = Integer.parseInt(pieceType);
          ArrayList<PieceDescription> tempPieces = new ArrayList<>(pieces);
          String type = "";
          int pieceCount = 0;
          for(PieceDescription piece : tempPieces){
            if(pieceTypeInt <= piece.getCount()+ pieceCount){
              type = piece.getType();
              break;
            }
            else{
              pieceCount += piece.getCount();
            }
          }
          String team = switch (playingFieldArr[i][j].charAt(2)) {
            case '2' -> "red";
            case '3' -> "green";
            case '4' -> "pink";
            default -> "blue";
          };
          String url;
              if(!theme.equals("water")){
               url = (imgAddressesLand + team + "_" + type + ".png");
              }
              else{
                String typeName = type.split("_")[type.split("_").length-1];
                url =(imgAddressesWater + team + "_" + typeName + ".png");
              }
          Image pieceImg = new Image(Objects.requireNonNull(HelperMethods.class.getResourceAsStream(url)));
          ImageView pieceImgView = new ImageView(pieceImg);
          pieceImgView.setFitHeight(cellHeight);
          pieceImgView.setFitWidth(cellWidth);
          pieceImgView.setStyle("-fx-background-color:#FFAD33;");
          pane.getChildren().add(pieceImgView);
          pane.setStyle("-fx-border-color: black;");
        }
        else if(playingFieldArr[i][j].equals("b:1")){
          String url = !theme.equals("water") ?"/assets/blue_base.png" : "/assets/bases/water_blue_waterbase.png";
          Image baseImg = new Image(Objects.requireNonNull(HelperMethods.class.getResourceAsStream(url)));
          ImageView barrierImgView = new ImageView(baseImg);
          barrierImgView.setFitHeight(cellHeight);
          barrierImgView.setFitWidth(cellWidth);
          pane.getChildren().add(barrierImgView);
          pane.setStyle("-fx-border-color: black;");
        }
        else if(playingFieldArr[i][j].equals("b:2")){
          String url = !theme.equals("water") ?"/assets/red_base.png" : "/assets/bases/water_red_waterbase.png";
          Image baseImg = new Image(Objects.requireNonNull(HelperMethods.class.getResourceAsStream(url)));
          ImageView barrierImgView = new ImageView(baseImg);
          barrierImgView.setFitHeight(cellHeight);
          barrierImgView.setFitWidth(cellWidth);
          pane.getChildren().add(barrierImgView);
          pane.setStyle("-fx-border-color: black;");
        }
        else if(playingFieldArr[i][j].equals("b:3")){
          String url = !theme.equals("water") ?"/assets/green_base.png" : "/assets/bases/water_green_waterbase.png";
          Image baseImg = new Image(Objects.requireNonNull(HelperMethods.class.getResourceAsStream(url)));
          ImageView barrierImgView = new ImageView(baseImg);
          barrierImgView.setFitHeight(cellHeight);
          barrierImgView.setFitWidth(cellWidth);
          pane.getChildren().add(barrierImgView);
          pane.setStyle("-fx-border-color: black;");
        }
        else if(playingFieldArr[i][j].equals("b:4")){
          String url = !theme.equals("water") ?"/assets/pink_base.png" : "/assets/bases/water_pink_waterbase.png";
          Image baseImg = new Image(Objects.requireNonNull(HelperMethods.class.getResourceAsStream(url)));
          ImageView barrierImgView = new ImageView(baseImg);
          barrierImgView.setFitHeight(cellHeight);
          barrierImgView.setFitWidth(cellWidth);
          pane.getChildren().add(barrierImgView);
          pane.setStyle("-fx-border-color: black;");
        } else{
          pane.setStyle("-fx-border-color: black;");
        }
        playingField.add(pane, j, i, 1, 1);
      }
    }
  }

  /**
   * @author paklaus
   * @param input array with some null values
   * @return all null values replaced with ""
   */
  public static String[][] removeNullFromArray(String[][] input){
    for(int i = 0; i < input.length; i++){
      for(int j = 0; j < input[0].length; j++){
        if(input[i][j] == null){
          input[i][j] = "";
        }
      }
    }
    return input;
  }

  /**
   * @author jdeiting
   * @param pieceDescriptionInp piece description from the map
   * @param teamCountInp amount of teams
   * @return string where each piece is represented like their "count" in the piece description
   */
  public static Piece[] createPieces(PieceDescription[] pieceDescriptionInp, int teamCountInp) {
    int pieceCounter = 0;
    //get total count of pieces
    for (PieceDescription pieceDescription : pieceDescriptionInp) {
      pieceCounter += pieceDescription.getCount();
    }
    Piece[] resultPieces = new Piece[pieceCounter * teamCountInp];
    //create pieces for each unique piece
    for (int i = 1; i <= teamCountInp; i++) {
      pieceCounter = 1;
      for (PieceDescription pieceDescription : pieceDescriptionInp) {
        //create pieces for count of unique piece
        for (int j = 0; j < pieceDescription.getCount(); j++, pieceCounter++) {

          Piece piece = new Piece();
          piece.setDescription(pieceDescription);
          piece.setId("" + pieceCounter);
          piece.setTeamId("" + i);
          resultPieces[(resultPieces.length / teamCountInp * (i - 1)) + pieceCounter - 1] = piece;
        }
      }
    }
    return resultPieces;
  }
  /**
   * @author jdeiting
   * @param width inputs the width of the playing field
   * @param height inputs the height of the playing field
   * @param teamAmount inputs the amount of teams
   * @return depending on the amount of teams the placement area for each team's pieces is determined
   *
   */
  public static String[][] determinePlacementArea(int width, int height, int teamAmount){
    int placementHalfSize;
    int placementAreaHeight = 0;
    int placementAreaWidth = 0;
    if (teamAmount == 2) {
      placementAreaHeight = (height <= 5 ? height / 2
          : height / 2 - 2);
      placementAreaWidth = width;
    } else if (teamAmount == 4 || teamAmount == 3) {
      int gridHalfSizeX = height / 2;
      int gridHalfSizeY = width / 2;
      if (gridHalfSizeX < gridHalfSizeY / 2) {
        placementHalfSize = gridHalfSizeX;
      } else if (gridHalfSizeY < gridHalfSizeX / 2) {
        placementHalfSize = gridHalfSizeY;
      } else if (gridHalfSizeX < gridHalfSizeY) {
        placementHalfSize = gridHalfSizeY / 2;
      } else {
        placementHalfSize = gridHalfSizeX / 2;
      }
      placementAreaHeight = placementHalfSize;
      placementAreaWidth = placementHalfSize * 2 + 1;
    } else {
      System.out.println("false team count");
      //throw new IllegalArgumentException("false team count");
    }
    return new String[placementAreaHeight][placementAreaWidth];
  }
  /**
   * @author paklaus
   * @param width inputs the width of the playing field
    * @param height inputs the height of the playing field
   * @param placementTypeInp inputs the type of placement
   * @param pieceDesInp inputs the piece description
   * @param teamAmountInp inputs the amount of teams
   * @param playingFieldArr inputs the playing field
   * @return returns the playing field with the pieces placed on it
   * places the pieces on the playing field
   */

  public static String[][] placePieces(int width, int height, PlacementType placementTypeInp, PieceDescription[] pieceDesInp, int teamAmountInp, String[][] playingFieldArr) {
    //create pieces
    Piece[] piecesInp = HelperMethods.createPieces(pieceDesInp, teamAmountInp);
    //determine placementAreaSize
    String[][] placementArea = HelperMethods.determinePlacementArea(width, height, teamAmountInp);
    //place Pieces and Base for each team
    for (int i = 1; i <= teamAmountInp; i++) {
      //create the placementArea
      placementArea = new String[placementArea.length][placementArea[0].length];
      //fill placementArea with blanks
      placementArea = removeNullFromArray(placementArea);
      //place base in middle of placementArea
      placementArea = HelperMethods.placeBaseForTeam(placementArea, i);
      //split pieces into teams
      Piece[] teamPieces = new Piece[piecesInp.length / teamAmountInp];
      int countTeamPieces = 0;
      for (Piece piece : piecesInp) {
        if (piece.getTeamId().equals("" + i)) {
          teamPieces[countTeamPieces] = piece;
          countTeamPieces++;
        }
      }
      if (placementArea.length * placementArea[0].length - 1 < teamPieces.length) {
        throw new IllegalArgumentException("To many pieces for playing field!");
      }
      //place pieces in needed type
      placementArea = switch (placementTypeInp) {
        case symmetrical -> HelperMethods.placePiecesSymmetrical(placementArea, teamPieces);
        case spaced_out -> HelperMethods.placePiecesSpacedOut(placementArea, teamPieces);
        case defensive -> HelperMethods.placePiecesDefensive(placementArea, teamPieces);
      };
      //rotate map, add piece placement on map and rotate map back
      playingFieldArr = HelperMethods.rotateMap(playingFieldArr, i);
      int placementBeginX = (playingFieldArr.length - placementArea.length);
      for (int j = 0; j < placementArea.length; j++, placementBeginX++) {
        int placementBeginY = playingFieldArr[0].length / 2 - placementArea[0].length / 2;
        for (int k = 0; k < placementArea[0].length; k++, placementBeginY++) {
          playingFieldArr[placementBeginX][placementBeginY] = placementArea[j][k];
        }
      }
      playingFieldArr = HelperMethods.rotateMapBack(playingFieldArr, i);

    }
    return playingFieldArr;
  }

  /**
   * @author paklaus
   * @param playingFieldArr array of the playing field
   * @param barrierAmountInput playing field itself
   * @return playing field with barriers placed on it
   * places the barriers on the playing field
   */
  public static String[][] updateBarriers(String[][] playingFieldArr, int barrierAmountInput) {//remove all barriers from the array
    for (int i = 0; i < playingFieldArr.length; i++) {
      for (int j = 0; j < playingFieldArr[0].length; j++) {
        if(playingFieldArr[i][j].equals("b")){
          playingFieldArr[i][j] = "";
        }
      }
    }
    //get all free cells
    int blockCount = barrierAmountInput;
    ArrayList<String> blockCoordinates = new ArrayList<>();
    for (int i = 0; i < playingFieldArr.length; i++) {
      for (int j = 0; j < playingFieldArr[0].length; j++) {
        if(playingFieldArr[i][j].isEmpty()){
          blockCoordinates.add(i + "," + j);
        }
      }
    }
    while (blockCount > 0) {
      if (blockCoordinates.isEmpty()) {
        return null;
      }

      int randomBlock =
          blockCoordinates.size() / 2;
      String[] randomBlockCoordinate = blockCoordinates.get(randomBlock).split(",");
      int x = Integer.parseInt(randomBlockCoordinate[0]);
      int y = Integer.parseInt(randomBlockCoordinate[1]);
      playingFieldArr[x][y] = "b";
      blockCoordinates.remove(randomBlock);
      blockCoordinates.removeIf(block -> {
        String[] blockCoordinate = block.split(",");
        int x1 = Integer.parseInt(blockCoordinate[0]);
        int y1 = Integer.parseInt(blockCoordinate[1]);
        return (x1 == x && y1 == y + 1) || (x1 == x && y1 == y - 1) || (x1 == x + 1 && y1 == y)
            || (x1 == x - 1 && y1 == y) || (x1 == x + 1 && y1 == y + 1) || (x1 == x - 1
            && y1 == y - 1) || (x1 == x + 1 && y1 == y - 1) || (x1 == x - 1 && y1 == y + 1);
      });

      blockCount--;
    }



    return playingFieldArr;
  }
}
