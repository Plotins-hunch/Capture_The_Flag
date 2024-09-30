package de.unimannheim.swt.pse.client;

import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import de.unimannheim.swt.pse.server.game.map.PieceDescription;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.layout.GridPane;

/**@author paklaus
* This class is used to create a GripPane from a Map Template that previews the map
 */
public class MapPreview {
  /** map template that needs to be displayed*/
  MapTemplate sampleMap;
  /** basis in javafx for the map template that needs to be displayed*/
  GridPane playingField;
  public MapPreview(MapTemplate map){
    playingField = new GridPane();
    sampleMap = map;
  }
  /**@author paklaus
   * creates a preview of the map from a map array
   * @param width width of the preview (pixels)
   * @param height height of the preview (pixels)
   */
  public void createPreview(double width, double height){
    ArrayList<PieceDescription> pieces = new ArrayList<>(Arrays.asList(sampleMap.getPieces()));
    createMapArr();
    String theme = sampleMap.getPieces()[0].getType().contains("water") ? "water" : "grass";
    HelperMethods.showMap(createMapArr(), playingField, pieces, width, height, true, theme);
  }
  /**@author paklaus
   * creates an array that can be used for the display later on
   * @return String[][] that can be used to display the map
   */
  public String[][] createMapArr(){
    //build new grid in right size
    String[][] playGrid = new String[sampleMap.getGridSize()[0]][sampleMap.getGridSize()[1]];
    //initialize grid with blanks
    for (int i = 0; i < playGrid.length; i++) {
      for (int j = 0; j < playGrid[0].length; j++) {
        playGrid[i][j] = "";
      }
    }
    //get team count
    //create pieces
    playGrid = HelperMethods.placePieces(playGrid.length, playGrid[0].length, sampleMap.getPlacement(), sampleMap.getPieces(), sampleMap.getTeams(), playGrid);
    for (String[] strings : playGrid) {
      for (int j = 0; j < playGrid[0].length; j++) {
        System.out.print(strings[j] + " ");
      }
      System.out.println();
    }
    playGrid =HelperMethods.updateBarriers(playGrid, sampleMap.getBlocks());
    return playGrid;
  }

  /**
   * @author paklaus
   * @return GridPane that can be used to display the map
   */
  public GridPane getplayingField(){
    return playingField;
  }


}
