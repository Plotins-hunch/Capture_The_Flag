package de.unimannheim.swt.pse.server.game;

import de.unimannheim.swt.pse.server.game.exceptions.GameOver;
import de.unimannheim.swt.pse.server.game.exceptions.InvalidMove;
import de.unimannheim.swt.pse.server.game.exceptions.NoMoreTeamSlots;
import de.unimannheim.swt.pse.server.game.map.Directions;
import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import de.unimannheim.swt.pse.server.game.map.Movement;
import de.unimannheim.swt.pse.server.game.map.PieceDescription;
import de.unimannheim.swt.pse.server.game.map.PlacementType;
import de.unimannheim.swt.pse.server.game.map.Shape;
import de.unimannheim.swt.pse.server.game.map.ShapeType;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import de.unimannheim.swt.pse.server.game.state.Piece;
import de.unimannheim.swt.pse.server.game.state.Team;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GameEngineGame implements Game {

  /**
   * Current game state
   */
  private GameState gameState;
  /**
   * Date when the game was started
   */
  private Date startedDate;
  /**
   * Map Template
   */
  private MapTemplate template;
  /**
   * List of pieces of all teams
   */
  private Piece[] pieces;
  /**
   * Time passed since game was started
   */
  private int gameTime;
  /**
   * Time passed since last move
   */
  private int moveTime;
  /**
   * Winner(s) of the game
   */
  private String[] winner;
  /**
   * Flag to indicate if the game is over
   */
  private boolean gameOver;
  /**
   * Date when the game ended
   */
  private Date endedDate;
  /**
   * Amount of flags remaining for each team
   */
  private int[] flags;


  /**
   * @param template inputs the template for map
   * @return gameState where bases and blocks are placed onto playGrid, pieces have coordinate and
   * are placed on playGrid in placement type and initial variables are set
   * @author jdeiting
   */
  @Override
  public GameState create(MapTemplate template) {
    this.template = template;
    this.gameOver = false;

    //build new grid in right size
    String[][] playGrid = new String[template.getGridSize()[0]][template.getGridSize()[1]];
    //initialize grid with blanks
    for (int i = 0; i < playGrid.length; i++) {
      for (int j = 0; j < playGrid[0].length; j++) {
        playGrid[i][j] = "";
      }
    }
    //get team count
    int teamCount = template.getTeams();

    //create pieces
    Piece[] mapPieces = createPieces(template.getPieces(), teamCount);

    playGrid = placePieces(playGrid, template.getPlacement(), mapPieces, teamCount);

    playGrid = placeBlocks(playGrid, template.getBlocks());

    GameState resultGameState = new GameState();
    resultGameState.setGrid(playGrid);
    this.gameState = resultGameState;
    gameState.setTeams(new Team[teamCount]);
    this.flags = new int[teamCount];
    for (int i = 0; i < teamCount; i++) {
      this.flags[i] = template.getFlags();
    }
    return resultGameState;
  }


  /**
   * @param placementGridInp inputs the playGrid
   * @param teamInp          inputs the number of teams participating
   * @return playGrid with Base for each team placed onto it
   * @author jdeiting
   */
  public String[][] placeBaseForTeam(String[][] placementGridInp, int teamInp) {
    //placement center of placementArea
    placementGridInp[placementGridInp.length / 2][placementGridInp[0].length / 2] = "b:" + teamInp;
    return placementGridInp;
  }

  /**
   * @param gridInp          inputs the playGrid
   * @param placementTypeInp inputs how to place the Pieces
   * @param piecesInp        inputs all Pieces needed to be placed
   * @param teamCountInp     inputs the number of teams participating
   * @return playGrid with Pieces placed for each team determined to the placement type
   * @author jdeiting
   */
  public String[][] placePieces(String[][] gridInp, PlacementType placementTypeInp,
      Piece[] piecesInp, int teamCountInp) {

    //determine placementAreaSize
    int placementHalfSize;
    String[][] placementArea;
    int placementAreaHeight;
    int placementAreaWidth;

    switch (teamCountInp) {
      case 2:
        placementAreaHeight = (gridInp.length <= 5 ? gridInp.length / 2
            : gridInp.length / 2 - 2);
        placementAreaWidth = gridInp[0].length;
        break;
      case 3:
      case 4:
        int gridHalfSizeX = gridInp.length / 2;
        int gridHalfSizeY = gridInp[0].length / 2;
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
        break;
      default:
        throw new IllegalArgumentException("false team count");
    }

    //place Pieces and Base for each team
    for (int i = 1; i <= teamCountInp; i++) {
      //create the placementArea
      placementArea = new String[placementAreaHeight][placementAreaWidth];
      //fill placementArea with blanks
      for (int j = 0; j < placementArea.length; j++) {
        for (int k = 0; k < placementArea[0].length; k++) {
          placementArea[j][k] = "";
        }
      }
      //place base in middle of placementArea
      placementArea = placeBaseForTeam(placementArea, i);
      //split pieces into teams
      Piece[] teamPieces = new Piece[piecesInp.length / teamCountInp];
      int countTeamPieces = 0;
      for (Piece piece : piecesInp) {
        if (piece.getTeamId().equals("" + i)) {
          teamPieces[countTeamPieces] = piece;
          countTeamPieces++;
        }
      }

      if (placementArea.length * placementArea[0].length - 1 < teamPieces.length) {
        throw new IllegalArgumentException("To many pieces for playfield!");
      }
      //place pieces in needed type
      switch (placementTypeInp) {
        case symmetrical:
          placementArea = placePiecesSymmetrical(placementArea, teamPieces);
          break;
        case spaced_out:
          placementArea = placePiecesSpacedOut(placementArea, teamPieces);
          break;
        case defensive:
          placementArea = placePiecesDefensive(placementArea, teamPieces);
          break;
        default:
          throw new IllegalArgumentException("PlacementType not supported");
      }
      //rotate map, add piece placement on map and rotate map back
      gridInp = rotateMap(gridInp, i);
      int placementBeginX = (gridInp.length - placementArea.length);
      for (int j = 0; j < placementArea.length; j++, placementBeginX++) {
        int placementBeginY = gridInp[0].length / 2 - placementArea[0].length / 2;
        for (int k = 0; k < placementArea[0].length; k++, placementBeginY++) {
          gridInp[placementBeginX][placementBeginY] = placementArea[j][k];
        }
      }
      gridInp = rotateMapBack(gridInp, i);

      setPiecePositions(gridInp);

    }

    return gridInp;

  }

  /**
   * Sets the Piece positions in the Piece array based on the playGrid
   *
   * @param gridInp inputs the playGrid
   * @author jdeiting
   */
  public void setPiecePositions(String[][] gridInp) {

    for (int i = 0; i < gridInp.length; i++) {
      for (int j = 0; j < gridInp[0].length; j++) {
        if (gridInp[i][j].matches("p.*")) {
          String[] pieceIDs = gridInp[i][j].substring(2).split("_");
          getPiece(pieceIDs[1], Integer.parseInt(pieceIDs[0])).setPosition(new int[]{i, j});

        }
      }
    }

  }

  /**
   * @param areaInp       inputs a grid on which to place the Pieces
   * @param teamPiecesInp inputs an Array of Pieces from one team
   * @return updatedPlacementArea which has the input Pieces placed in a spaced out way
   * @author jdeiting
   */
  public String[][] placePiecesSpacedOut(String[][] areaInp, Piece[] teamPiecesInp) {

    //initialize rangeMatrix
    double[][] rangeMatrix = new double[areaInp.length][areaInp[0].length];
    //store maxpossible range
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
      rangeMatrix = updateRangeMatrix(rangeMatrix, pieceCoordinate);
    }
    return areaInp;
  }

  /**
   * @param areaInp       inputs a grid on which to place the Pieces
   * @param teamPiecesInp inputs an Array of Pieces from one team
   * @return updatedPlacementArea which has the input Pieces placed in a defensive way
   * @author jdeiting
   */
  public String[][] placePiecesDefensive(String[][] areaInp, Piece[] teamPiecesInp) {
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

  //tested, works

  /**
   * @param prioMatrixInp inputs a matrix with priorities on where to place Pieces
   * @return highestPrioCoordinate where the lowest number / highest priority of placement is
   * located
   * @author jdeiting
   */
  public int[] getHighestPrio(int[][] prioMatrixInp) {
    int[] resultCoordinate = {0, 0};
    int maxPrio = prioMatrixInp[0][0];
    for (int i = 0; i < prioMatrixInp.length; i++) {
      for (int j = 0; j < prioMatrixInp[0].length; j++) {
        if (prioMatrixInp[i][j] < maxPrio) {
          resultCoordinate[0] = i;
          resultCoordinate[1] = j;
          maxPrio = prioMatrixInp[i][j];
        }
      }
    }
    return resultCoordinate;
  }

  /**
   * @param areaInp       inputs a grid on which to place the Pieces
   * @param teamPiecesInp inputs an Array of Pieces from one team
   * @return updatedPlacementArea which has the input Pieces placed in a symmetrical way
   * @author jdeiting
   */
  public String[][] placePiecesSymmetrical(String[][] areaInp, Piece[] teamPiecesInp) {

    int width = areaInp[0].length;
    int height = areaInp.length;
    boolean isOdd = width % 2 != 0;
    int pieceSize = teamPiecesInp.length;
    boolean needExtra = !isOdd && (width - 1) * height - 1 < pieceSize;
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
   * @param rangeMatrixInp inputs a matrix filled with ranges
   * @return Coordinate that has the lowest range value in the rangeMatrixInp
   * @author jdeiting
   */
  public int[] getMaxRange(double[][] rangeMatrixInp) {
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

  /**
   * @param rangeMatrixInp inputs a matrix filled with ranges
   * @return Coordinate that has the lowest range value in the rangeMatrixInp
   * @author jdeiting
   */
  public int[] getMinRange(double[][] rangeMatrixInp) {
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


  /**
   * @param rangeMatrixInp   inputs a matrix with ranges between its "0" values
   * @param piecePositionInp inputs the position of a new placed Piece
   * @return updatedRangeMatrix with a new "0" value where the piece got placed and therefore
   * updated ranges
   * @author jdeiting
   */
  public double[][] updateRangeMatrix(double[][] rangeMatrixInp, int[] piecePositionInp) {
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


  /**
   * @param placementString inputs the current string
   * @return if the given String is empty and therefore a valid placement
   * @author jdeiting
   */
  public boolean isValidPlacement(String placementString) {
    return placementString.isEmpty();
  }

  /**
   * @param gridInp inputs a playGrid
   * @param team    inputs the team for which to rotate the grid
   * @return playGrid but rotated to a teams perspective
   * @author jdeiting
   */
  public String[][] rotateMap(String[][] gridInp, int team) {
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
        throw new IllegalArgumentException("Team not supported");
    }
    return resultGrid;
  }

  /**
   * @param gridInp inputs a for "team" rotated playGrid
   * @param team    inputs the team for which the grid is currently rotated
   * @return playGrid but rotated back to normal perspective
   * @author jdeiting
   */
  public String[][] rotateMapBack(String[][] gridInp, int team) {
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
   * @param pieceDescriptionInp inputs the list of PieceDescriptions
   * @param teamCountInp        inputs how many teams are playing
   * @return an array of the created Pieces
   * @author jdeiting
   */
  public Piece[] createPieces(PieceDescription[] pieceDescriptionInp, int teamCountInp) {
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
    this.pieces = resultPieces;

    return resultPieces;
  }


  /**
   * @param gridInp    inputs the playGrid
   * @param blockCount inputs the count of blocks to place
   * @return playGrid with blocks placed
   * @throws IllegalArgumentException if not enough place for blocks
   * @author jdeiting
   */
  public String[][] placeBlocks(String[][] gridInp, int blockCount) {

    ArrayList<String> blockCoordinates = new ArrayList<>();
    for (int i = 0; i < gridInp.length; i++) {
      for (int j = 0; j < gridInp[0].length; j++) {
        if (gridInp[i][j].isEmpty()) {
          blockCoordinates.add(i + "," + j);
        }
      }
    }
    while (blockCount > 0) {
      if (blockCoordinates.isEmpty()) {
        throw new IllegalArgumentException("Not enough space for blocks");
      }
      int randomBlock =
          blockCoordinates.size() / 2;
      String[] randomBlockCoordinate = blockCoordinates.get(randomBlock).split(",");
      int x = Integer.parseInt(randomBlockCoordinate[0]);
      int y = Integer.parseInt(randomBlockCoordinate[1]);
      gridInp[x][y] = "b";
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

    return gridInp;
  }


  /**
   * @return gameState
   * @author jdeiting
   */
  @Override
  public GameState getCurrentGameState() {
    for (Team t : this.gameState.getTeams()) {
      if (t != null) {
        System.out.println("TeamPieces: " + t.getPieces().length);
      }
    }
    return this.gameState;

  }

  /**
   * Updates a game and its state based on team join request (add team).
   *
   * <ul>
   *     <li>adds team if a slot is free (array element is null)</li>
   *     <li>if all team slots are finally assigned, implicitly starts the game by picking a starting team at random</li>
   * </ul>
   *
   * @param teamId Team ID
   * @return Team
   * @throws NoMoreTeamSlots No more team slots available
   * @author ldornied
   */
  @Override
  public Team joinGame(String teamId) {
    synchronized (this) {
      Team team = new Team();

      // check if spectator
      if (teamId.equals("Spectator")) {
        team.setId("0");
        return team;
      }

      // check if game capacity is reached
      if (this.getRemainingTeamSlots() == 0) {
        throw new NoMoreTeamSlots();
      }

      // add new team to game state
      Team[] teams = this.gameState.getTeams();

      for (int i = 0; i < teams.length; i++) {
        if (teams[i] == null) {
          teams[i] = team;
          team.setId(String.valueOf(i + 1));

          switch (Integer.parseInt(team.getId())) {
            case 1:
              team.setColor("Blue");
              break;
            case 2:
              team.setColor("Red");
              break;
            case 3:
              team.setColor("Green");
              break;
            case 4:
              team.setColor("Pink");
              break;
            default:
              throw new NoMoreTeamSlots();
          }

          team.setPieces(this.getTeamPieces(i + 1));

          boolean outerBreak = false;
          for (int j = 0; j < gameState.getGrid().length; j++) {
            for (int k = 0; k < gameState.getGrid()[0].length; k++) {
              if (gameState.getGrid()[j][k].equals("b:" + team.getId())) {
                team.setBase(new int[]{j, k});
                outerBreak = true;
                break;
              }
            }
            if (outerBreak) {
              break;
            }
          }

          team.setFlags(template.getFlags());
          break;
        }
      }

      this.gameState.setTeams(teams);

      // start game and time thread if all teams are joined
      if (this.getRemainingTeamSlots() == 0) {
        this.startedDate = new Date();
        // check if thread for game or move time needs to be started
        if (this.getRemainingGameTimeInSeconds() != -1
            || this.getRemainingMoveTimeInSeconds() != -1) {
          this.gameTime = 0;
          this.moveTime = 0;
          new GameTimeCounter().start();
        }
        this.gameState.setCurrentTeam((int) ((Math.random() * teams.length) + 1));
      }

      // return new team
      return team;
    }
  }

  /**
   * @param teamInp inputs the ID of the team
   * @return all pieces of the given team
   * @author jdeiting
   */
  public Piece[] getTeamPieces(int teamInp) {
    Piece[] resultPieces = new Piece[this.pieces.length / this.template.getTeams()];
    int countTeamPieces = 0;
    for (Piece piece : this.pieces) {
      if (piece.getTeamId().equals("" + teamInp)) {
        resultPieces[countTeamPieces++] = piece;
      }
    }
    return resultPieces;
  }

  /**
   * @return number of remaining team slots
   * @author ldornied
   */
  @Override
  public int getRemainingTeamSlots() {
    Team[] currentTeams = this.gameState.getTeams();
    int count = 0;

    for (Team currentTeam : currentTeams) {
      if (currentTeam == null) {
        count++;
      }
    }

    return count;
  }

  /**
   * Make a move
   *
   * @param move {@link Move}
   * @throws InvalidMove Requested move is invalid
   * @throws GameOver    Game is over
   * @author ldornied
   */
  @Override
  public synchronized void makeMove(Move move) {
    System.out.println("Making new move...");
    // check if game is over
    if (this.gameOver) {
      throw new GameOver();
    }

    // check whether move is valid
    if (this.isValidMove(move)) {
      String[][] grid = this.gameState.getGrid();

      // create reference for piece
      int currentTeam = this.gameState.getCurrentTeam();
      String reference = "p:" + currentTeam + "_" + move.getPieceId();

      // get piece info
      Piece piece = this.getPiece(move.getPieceId(), currentTeam);
      if (piece == null) {
        throw new InvalidMove();
      }

      // get coordinates of move and content of new position
      int[] newCoordinates = move.getNewPosition();
      int[] currentPosition = piece.getPosition();
      String newPosition = grid[newCoordinates[0]][newCoordinates[1]];

      // check if player or base on field
      if (newPosition.matches("p:[1-4]_[0-9]+")) { // player
        // get team and piece id from enemy piece
        System.out.println("Piece captured: " + newPosition);
        String[] enemyPieceInfo = newPosition.split("_");
        int enemyTeamId = Integer.parseInt(enemyPieceInfo[0].split(":")[1]);
        int enemyPieceId = Integer.parseInt(enemyPieceInfo[1]);

        // get piece
        Piece enemyPiece = this.getPiece(enemyPieceId + "", enemyTeamId);
        if (enemyPiece == null) {
          throw new InvalidMove();
        }

        // remove piece from pieces arrays
        Piece[] newPieces = new Piece[this.pieces.length - 1];
        int count = 0;
        for (Piece currentPiece : this.pieces) {

          if (!(currentPiece.getId().equals(enemyPieceId + "") && currentPiece.getTeamId()
              .equals(enemyTeamId + ""))) {

            newPieces[count++] = currentPiece;
          }
        }
        this.pieces = newPieces;

        Team team = this.gameState.getTeams()[enemyTeamId - 1];
        Piece[] teamPieces = team.getPieces();
        Piece[] newTeamPieces = new Piece[teamPieces.length - 1];
        count = 0;
        for (Piece currentPiece : teamPieces) {
          if (!currentPiece.getId().equals(enemyPieceId + "")) {
            newTeamPieces[count++] = currentPiece;
          }
        }
        team.setPieces(newTeamPieces);
        System.out.println("numOfPieces: " + team.getPieces().length);

        // set piece coordinates
        piece.setPosition(newCoordinates);

        // update grid
        grid[newCoordinates[0]][newCoordinates[1]] = reference;
        grid[currentPosition[0]][currentPosition[1]] = "";
      } else if (newPosition.matches("b:[1-4]")) { // base
        // get team of base
        System.out.println("flagCaptured: " + newPosition);
        int team = Integer.parseInt(newPosition.substring(2));

        // decrease flag count
        this.flags[team - 1]--;

        Team flagTeam = this.gameState.getTeams()[team - 1];
        flagTeam.setFlags(flagTeam.getFlags() - 1);

        // set winner(s) if flag count is 0, else respawn piece next to own base
        if (this.flags[team - 1] == 0) {
          // set ended date
          this.endedDate = new Date();

          // set new position
          piece.setPosition(newCoordinates);
          grid[newCoordinates[0]][newCoordinates[1]] = reference;

          // set winner
          this.winner = new String[]{currentTeam + ""};

          // set game over true
          this.gameOver = true;
          System.out.println("Game over 2");
          throw new GameOver();
        } else {
          // get base coordinates
          int[] baseCoordinates = new int[2];
          for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
              if (grid[i][j].equals("b:" + currentTeam)) { // index?
                baseCoordinates[0] = i;
                baseCoordinates[1] = j;
                break;
              }
            }
          }

          // get amount of pieces to determine search radius
          int numPieces = this.pieces.length;
          int searchRadius = ((int) Math.ceil(Math.sqrt(numPieces)) + 1) / 2;

          // set piece position next to base
          int[] newPiecePosition = new int[2];

          for (int radius = 1; radius < searchRadius; radius++) {
            // list of possible positions
            List<int[]> possiblePositions = new ArrayList<>();

            for (int i = -radius; i <= radius; i++) {
              for (int j = -radius; j <= radius; j++) {
                if (Math.abs(i) == radius || Math.abs(j) == radius) {
                  int x = baseCoordinates[0] + i;
                  int y = baseCoordinates[1] + j;

                  // check if field is empty or index is out of bounds
                  if (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length
                      && grid[x][y].isEmpty()) {
                    possiblePositions.add(new int[]{x, y});
                  }
                }
              }
            }

            // loop through possible positions and return random position
            if (!possiblePositions.isEmpty()) {
              newPiecePosition = possiblePositions.get(
                  (int) (Math.random() * possiblePositions.size()));
              break;
            }
          }

          // set new piece position
          piece.setPosition(newPiecePosition);

          // update grid
          grid[newPiecePosition[0]][newPiecePosition[1]] = reference;
          grid[currentPosition[0]][currentPosition[1]] = "";
        }
      } else { // empty field
        // set piece coordinates
        piece.setPosition(newCoordinates);

        // update grid
        grid[newCoordinates[0]][newCoordinates[1]] = reference;
        grid[currentPosition[0]][currentPosition[1]] = ""; // test
      }

      // set next team
      int nextTeam = (this.gameState.getCurrentTeam() % this.gameState.getTeams().length) + 1;

      // update game state
      this.gameState.setGrid(grid);
      this.gameState.setLastMove(move);
      this.gameState.setCurrentTeam(nextTeam);
      this.moveTime = 0;
      System.out.println("Move successful");
    } else {
      throw new InvalidMove();
    }
  }

  /**
   * Returns Piece from Piece array with id from parameter
   *
   * @param pieceId id of piece to return
   * @param team    team of piece to return
   * @return piece with id pieceId from specified team, null if not found
   * @author ldornied
   */
  protected Piece getPiece(String pieceId, int team) {

    for (Piece piece : this.pieces) {
      if (piece.getId().equals(pieceId) && piece.getTeamId().equals(team + "")) {
        return piece;
      }
    }
    return null;
  }

  /**
   * @return -1 if no total game time limit set, 0 if over, > 0 if seconds remain
   * @author ldornied
   */
  @Override
  public int getRemainingGameTimeInSeconds() {
    if (this.startedDate == null) {
      return -1;
    }

    return this.template.getTotalTimeLimitInSeconds() < 0 ? -1
        : Math.max(this.template.getTotalTimeLimitInSeconds() - this.gameTime, 0);

  }

  /**
   * @return -1 if no move time limit set, 0 if over, > 0 if seconds remain
   * @author ldornied
   */
  @Override
  public int getRemainingMoveTimeInSeconds() {
    if (this.startedDate == null) {
      return -1;
    }

    return this.template.getMoveTimeLimitInSeconds() < 0 ? -1
        : Math.max(this.template.getMoveTimeLimitInSeconds() - this.moveTime, 0);
  }

  /**
   * @param teamId inputs the team wanting to surrender
   * @throws IllegalArgumentException if number of teams is not 2 or 4
   * @author jdeiting
   */
  @Override
  public void giveUp(String teamId) {
    Team[] teams = gameState.getTeams();

    // set Teams pieces to 0, so that move will be skipped
    Team team = teams[Integer.parseInt(teamId) - 1];
    team.setPieces(new Piece[0]);

    // count remaining teams
    int teamsRemaining = 0;
    for (Team t : teams) {
      if (t.getPieces().length != 0) {
        teamsRemaining++;
      }
    }

    // stop game, if only one team remaining, and determine winner
    if (teamsRemaining == 1) {
      this.endedDate = new Date();

      for (Team t : teams) {
        if (t.getPieces().length != 0) {
          this.winner = new String[]{t.getId()};

        }
      }

      this.gameOver = true;
    }
  }

  /**
   * Checks whether a move is valid based on the current game state.
   *
   * @param move {@link Move}
   * @return true if move is valid based on current game state, false otherwise
   * @author ldornied
   */
  @Override
  public boolean isValidMove(Move move) {
    int currentTeam = this.gameState.getCurrentTeam();

    // get coordinates of move and grid
    Piece piece = this.getPiece(move.getPieceId(), currentTeam);
    if (piece == null) {
      return false;
    }
    int[] newPosition = new int[2];
    newPosition[0] = move.getNewPosition()[0];
    newPosition[1] = move.getNewPosition()[1];
    int[] currentPosition = new int[2];
    currentPosition[0] = piece.getPosition()[0];
    currentPosition[1] = piece.getPosition()[1];

    String[][] grid = this.gameState.getGrid();

    // check if move is out of bounds
    if (this.isOutOfBounds(newPosition, grid)) {
      return false;
    }

    // rotate grid, if necessary
    grid = this.rotateMap(grid, currentTeam);

    // update coordinates if grid was rotated
    switch (currentTeam) {
      case 1:
        break;
      case 2:
        newPosition[0] = grid.length - newPosition[0] - 1;
        newPosition[1] = grid[0].length - newPosition[1] - 1;
        currentPosition[0] = grid.length - currentPosition[0] - 1;
        currentPosition[1] = grid[0].length - currentPosition[1] - 1;
        break;
      case 3:
        int tempPos0 = newPosition[0];
        newPosition[0] = grid.length - newPosition[1] - 1;
        newPosition[1] = tempPos0;
        tempPos0 = currentPosition[0];
        currentPosition[0] = grid.length - currentPosition[1] - 1;
        currentPosition[1] = tempPos0;
        break;
      case 4:
        int tempPos1 = newPosition[1];
        newPosition[1] = grid[0].length - newPosition[0] - 1;
        newPosition[0] = tempPos1;
        tempPos1 = currentPosition[1];
        currentPosition[1] = grid[0].length - currentPosition[0] - 1;
        currentPosition[0] = tempPos1;
        break;
    }

    // check if piece was already removed
    if (piece.getPosition() == null) {
      return false;
    }

    // check if new position is the same as the current position
    if (currentPosition[0] == newPosition[0] && currentPosition[1] == newPosition[1]) {
      return false;
    }

    // check, if another Piece, base or block on field
    String position = grid[newPosition[0]][newPosition[1]]; // item on new position
    if (!position.isEmpty()) {
      if (position.matches("p:[1-4]_[0-9]+")) { // piece
        // get piece and team info
        String[] pieceInfo = position.split("_");
        Piece enemyPiece = this.getPiece(pieceInfo[1], Integer.parseInt(pieceInfo[0].substring(2)));
        if (enemyPiece == null) {
          return false;
        }

        // check if piece is from my team
        if (enemyPiece.getTeamId().equals(this.gameState.getCurrentTeam() + "")) {
          return false;
        }

        // check if enemy piece is stronger
        if (piece.getDescription().getAttackPower() < enemyPiece.getDescription()
            .getAttackPower()) {
          return false;
        }
      } else if (position.equals("b")) { // block
        return false;
      } else { // base
        // check if base is from my team
        if (position.substring(2).equals(this.gameState.getCurrentTeam() + "")) {
          return false;
        }
      }
    }

    // get movement and shape of piece
    Movement movement = piece.getDescription().getMovement();
    Directions direction = movement.getDirections();
    Shape shape = movement.getShape();

    // amount of steps taken in each direction
    // positive if left/up, negative if right/down
    int horizontalSteps = currentPosition[1] - newPosition[1];
    int verticalSteps = currentPosition[0] - newPosition[0];
    int absHorizontalSteps = Math.abs(horizontalSteps);
    int absVerticalSteps = Math.abs(verticalSteps);

    // check different movement types and if piece can move in that direction / that amount of steps
    if (shape != null && shape.getType() == ShapeType.lshape) {
      if (!(absHorizontalSteps == 2 && absVerticalSteps == 1 ||
          absHorizontalSteps == 1 && absVerticalSteps == 2)) {
        return false;
      } else {
        // horizontal l-shape
        if (absHorizontalSteps == 2) {
          // right
          if (horizontalSteps < 0) {
            if (!grid[currentPosition[0]][currentPosition[1] + 1].isEmpty() ||
                !grid[currentPosition[0]][currentPosition[1] + 2].isEmpty()) {
              return false;
            }
          } else { // left
            if (!grid[currentPosition[0]][currentPosition[1] - 1].isEmpty() ||
                !grid[currentPosition[0]][currentPosition[1] - 2].isEmpty()) {
              return false;
            }
          }
        } else { // vertical l-shape
          // up
          if (verticalSteps > 0) {
            if (!grid[currentPosition[0] - 1][currentPosition[1]].isEmpty() ||
                !grid[currentPosition[0] - 2][currentPosition[1]].isEmpty()) {
              return false;
            }
          } else {
            // down
            if (!grid[currentPosition[0] + 1][currentPosition[1]].isEmpty() ||
                !grid[currentPosition[0] + 2][currentPosition[1]].isEmpty()) {
              return false;
            }
          }
        }
      }
    } else {
      // up/down left/right - check if move is symmetrical
      if (absHorizontalSteps > 0 && absVerticalSteps > 0
          && absVerticalSteps != absHorizontalSteps) {
        return false;
      }
      if (horizontalSteps < 0 && verticalSteps == 0) {
        // right - check if move is valid
        if (absHorizontalSteps > direction.getRight()) {
          return false;
        }

        // check, if something is in the way or move is out of bounds
        for (int i = 1; i < absHorizontalSteps; i++) {
          if (!grid[currentPosition[0]][currentPosition[1] + i].isEmpty()) {
            return false;
          }
        }
      } else if (horizontalSteps > 0 && verticalSteps == 0) {
        // left - check if move is valid
        if (absHorizontalSteps > direction.getLeft()) {
          return false;
        }
        // check, if something is in the way
        for (int i = 1; i < absHorizontalSteps; i++) {
          if (!grid[currentPosition[0]][currentPosition[1] - i].isEmpty()) {
            return false;
          }
        }
      } else if (verticalSteps > 0 && horizontalSteps == 0) {
        // up - check if move is valid
        if (absVerticalSteps > direction.getUp()) {
          return false;
        }
        // check, if something is in the way
        for (int i = 1; i < absVerticalSteps; i++) {
          if (!grid[currentPosition[0] - i][currentPosition[1]].isEmpty()) {
            return false;
          }
        }
      } else if (verticalSteps < 0 && horizontalSteps == 0) {
        // down - check if move is valid
        if (absVerticalSteps > direction.getDown()) {
          return false;
        }
        // check, if something is in the way
        for (int i = 1; i < absVerticalSteps; i++) {
          if (!grid[currentPosition[0] + i][currentPosition[1]].isEmpty()) {
            return false;
          }
        }
      } else if (absHorizontalSteps == absVerticalSteps) {
        if (verticalSteps > 0 && horizontalSteps < 0) {
          // up right - check if move is valid
          if (absHorizontalSteps > direction.getUpRight()
              || absVerticalSteps > direction.getUpRight()) {
            return false;
          }
          // check, if something is in the way
          for (int i = 1; i < absVerticalSteps; i++) {
            if (!grid[currentPosition[0] - i][currentPosition[1] + i].isEmpty()) {
              return false;
            }
          }
        } else if (verticalSteps > 0) {
          // up left - check if move is valid
          if (absHorizontalSteps > direction.getUpLeft()) {
            return false;
          }
          // check, if something is in the way
          for (int i = 1; i < absVerticalSteps; i++) {
            if (!grid[currentPosition[0] - i][currentPosition[1] - i].isEmpty()) {
              return false;
            }
          }
        } else if (horizontalSteps < 0) {
          // down right - check if move is valid
          if (absHorizontalSteps > direction.getDownRight()) {
            return false;
          }
          // check, if something is in the way
          for (int i = 1; i < absVerticalSteps; i++) {
            if (!grid[currentPosition[0] + i][currentPosition[1] + i].isEmpty()) {
              return false;
            }
          }
        } else {
          // down left - check if move is valid
          if (absHorizontalSteps > direction.getDownLeft()) {
            return false;
          }
          // check, if something is in the way
          for (int i = 1; i < absVerticalSteps; i++) {
            if (!grid[currentPosition[0] + i][currentPosition[1] - i].isEmpty()) {
              return false;
            }
          }
        }
      }
    }

    // rotate back
    rotateMapBack(grid, currentTeam);

    return true;
  }


  /**
   * Checks if a position is out of bounds.
   *
   * @param position Position to check
   * @param grid     Current grid
   * @return true if position is out of bounds, false otherwise
   * @author ldornied
   */
  private boolean isOutOfBounds(int[] position, String[][] grid) {
    return position[0] < 0 || position[0] >= grid.length || position[1] < 0
        || position[1] >= grid[0].length;
  }

  /**
   * @return true if game is started, false otherwise
   * @author jdeiting
   */
  @Override
  public boolean isStarted() {
    return this.startedDate != null;
  }

  /**
   * Checks whether the game is over based on the current {@link GameState}.
   *
   * @return true if game is over, false if game is still running.
   * @author jdeiting
   */
  @Override
  public boolean isGameOver() {
    return this.gameOver;
  }

  /**
   * Get winner(s) (if any)
   *
   * @return {@link Team#getId()} if there is a winner
   * @author jdeiting
   */
  @Override
  public String[] getWinner() {
    return this.winner;
  }

  /**
   * @return Start {@link Date} of game
   * @author ldornied
   */
  @Override
  public Date getStartedDate() {
    return this.startedDate;
  }

  /**
   * @return End {@link Date} of game
   * @author jdeiting
   */
  @Override
  public Date getEndDate() {
    return this.endedDate;
  }

  /**
   * Checks whether a piece is able to make a move.
   *
   * @param piece {@link Piece}
   * @return true if piece can move, false otherwise
   * @author ldornied
   */
  protected boolean canMove(Piece piece) {

    // get position, movement and shape of piece
    Movement movement = piece.getDescription().getMovement();
    Directions direction = movement.getDirections();
    Shape shape = movement.getShape();
    int[] currentPosition = piece.getPosition();

    // adjust direction for each team
    int[] adjustedDirection = switch (Integer.parseInt(piece.getTeamId())) {
      case 2 -> // Team 2 (reverse both directions)
          new int[]{-1, -1};
      case 3 -> // Team 3 (rotate 90 degrees clockwise)
          new int[]{1, -1};
      case 4 -> // Team 4 (rotate 90 degrees counterclockwise)
          new int[]{-1, 1};
      default -> // Team 1 (standard perspective)
          new int[]{1, 1};
    };

    if (shape != null && shape.getType() == ShapeType.lshape) {
      int[] row = {-2, -2, 2, 2, 1, -1, 1, -1};
      int[] col = {-1, 1, -1, 1, 2, 2, -2, -2};
      for (int i = 0; i < 8; i++) {
        Move newMove = new Move();
        newMove.setPieceId(piece.getId());
        newMove.setTeamId(piece.getTeamId());
        newMove.setNewPosition(new int[]{currentPosition[0] + row[i], currentPosition[1] + col[i]});
        if (isValidMove(newMove)) {
          return true;
        }
      }
    } else {
      // get possible steps in each direction
      int left = direction.getLeft();
      int right = direction.getRight();
      int up = direction.getUp();
      int down = direction.getDown();
      int upRight = direction.getUpRight();
      int upLeft = direction.getUpLeft();
      int downRight = direction.getDownRight();
      int downLeft = direction.getDownLeft();

      // check if piece can move in each direction
      if (left > 0) {
        for (int i = 1; i <= left; i++) {
          if (currentPosition[1] - i >= 0) {
            Move newMove = new Move();
            newMove.setPieceId(piece.getId());
            newMove.setTeamId(piece.getTeamId());
            newMove.setNewPosition(
                new int[]{currentPosition[0], currentPosition[1] - i * adjustedDirection[1]});
            if (isValidMove(newMove)) {
              return true;
            }
          }
        }
      }
      if (right > 0) {
        for (int i = 1; i <= right; i++) {
          if (currentPosition[1] + i < gameState.getGrid()[0].length) {
            Move newMove = new Move();
            newMove.setPieceId(piece.getId());
            newMove.setTeamId(piece.getTeamId());
            newMove.setNewPosition(
                new int[]{currentPosition[0], currentPosition[1] + i * adjustedDirection[1]});
            if (isValidMove(newMove)) {
              return true;
            }
          }
        }
      }
      if (up > 0) {
        for (int i = 1; i <= up; i++) {
          if (currentPosition[0] - i >= 0) {
            Move newMove = new Move();
            newMove.setPieceId(piece.getId());
            newMove.setTeamId(piece.getTeamId());
            newMove.setNewPosition(
                new int[]{currentPosition[0] - i * adjustedDirection[0], currentPosition[1]});
            if (isValidMove(newMove)) {
              return true;
            }
          }
        }
      }
      if (down > 0) {
        for (int i = 1; i <= down; i++) {
          if (currentPosition[0] + i < gameState.getGrid().length) {
            Move newMove = new Move();
            newMove.setPieceId(piece.getId());
            newMove.setTeamId(piece.getTeamId());
            newMove.setNewPosition(
                new int[]{currentPosition[0] + i * adjustedDirection[0], currentPosition[1]});
            if (isValidMove(newMove)) {
              return true;
            }
          }
        }
      }
      if (upRight > 0) {
        for (int i = 1; i <= upRight; i++) {
          if (currentPosition[0] - i >= 0
              && currentPosition[1] + i < gameState.getGrid()[0].length) {
            Move newMove = new Move();
            newMove.setPieceId(piece.getId());
            newMove.setTeamId(piece.getTeamId());
            newMove.setNewPosition(new int[]{currentPosition[0] - i * adjustedDirection[0],
                currentPosition[1] + i * adjustedDirection[1]});
            if (isValidMove(newMove)) {
              return true;
            }
          }
        }
      }
      if (upLeft > 0) {
        for (int i = 1; i <= upLeft; i++) {
          if (currentPosition[0] - i >= 0 && currentPosition[1] - i >= 0) {
            Move newMove = new Move();
            newMove.setPieceId(piece.getId());
            newMove.setTeamId(piece.getTeamId());
            newMove.setNewPosition(new int[]{currentPosition[0] - i * adjustedDirection[0],
                currentPosition[1] - i * adjustedDirection[1]});
            if (isValidMove(newMove)) {
              return true;
            }
          }
        }
      }
      if (downRight > 0) {
        for (int i = 1; i <= downRight; i++) {
          if (currentPosition[0] + i < gameState.getGrid().length
              && currentPosition[1] + i < gameState.getGrid()[0].length) {
            Move newMove = new Move();
            newMove.setPieceId(piece.getId());
            newMove.setTeamId(piece.getTeamId());
            newMove.setNewPosition(new int[]{currentPosition[0] + i * adjustedDirection[0],
                currentPosition[1] + i * adjustedDirection[1]});
            if (isValidMove(newMove)) {
              return true;
            }
          }
        }
      }
      if (downLeft > 0) {
        for (int i = 1; i <= downLeft; i++) {
          if (currentPosition[0] + i < gameState.getGrid().length && currentPosition[1] - i >= 0) {
            Move newMove = new Move();
            newMove.setPieceId(piece.getId());
            newMove.setTeamId(piece.getTeamId());
            newMove.setNewPosition(new int[]{currentPosition[0] + i * adjustedDirection[0],
                currentPosition[1] - i * adjustedDirection[1]});
            if (isValidMove(newMove)) {
              return true;
            }
          }
        }
      }
    }
    // return false, if no valid move was found
    return false;
  }

  /**
   * Inner class to track the game and move time
   *
   * @author ldornied
   */
  private class GameTimeCounter extends Thread {

    /**
     * Starts thread and tracks seconds passed since the game/move started
     * <ul>
     *   <li> Updates game time and move time every second </li>
     *   <li> Stops thread if game time is over and sets game over</li>
     *   <li> Sets next team if move time is over</li>
     * </ul>
     *
     * @author ldornied
     */
    @Override
    public void run() {
      while (!this.isInterrupted()) {
        try {
          Thread.sleep(1000);
          gameTime++;
          moveTime++;


        } catch (InterruptedException e) {
          this.interrupt();
        }

        if (getRemainingGameTimeInSeconds() == 0) {
          // set ended date
          endedDate = new Date();

          // count remaining pieces for each team
          int numOfTeams = gameState.getTeams().length;
          int[] piecesRemaining = new int[numOfTeams];

          for (Piece piece : pieces) {
            piecesRemaining[Integer.parseInt(piece.getTeamId()) - 1]++;
          }

          // identify winner(s)
          String[] winnerTeam = new String[numOfTeams];
          int currentMax = 0;
          int index = 1;

          for (int i = 0; i < piecesRemaining.length; i++) {
            if (piecesRemaining[i] > currentMax) {
              // remove current winner(s) and index
              Arrays.fill(winnerTeam, 0 + "");
              index = 1;
              winnerTeam[0] = String.valueOf(i + 1);
            } else if (piecesRemaining[i] == currentMax) {
              winnerTeam[index++] = String.valueOf(i + 1);
            }
          }

          // set winner
          winner = winnerTeam;

          // set game over and interrupt thread
          gameOver = true;
          System.out.println("Game Over");
          this.interrupt();
        }

        // check if move time is over, team has no remaining pieces or piece cannot make move
        int currentTeam = gameState.getCurrentTeam();
        Team[] teams = gameState.getTeams();
        Team team = teams[currentTeam - 1];

        // count amount of remaining active players and check if game is over
        int remainingPlayers = 0;
        for (Team t : teams) {
          if (t.getPieces().length > 0) {
            remainingPlayers++;
          }
        }

        if (remainingPlayers == 1) {
          // only one team with pieces remaining -> game ends
          endedDate = new Date();
          for (int i = 0; i < teams.length; i++) {
            if (teams[i].getPieces().length > 0) {
              winner = new String[1];
              winner[0] = String.valueOf(i + 1);
              break;
            }
          }
          gameOver = true;
          System.out.println("Game Over 2");
          this.interrupt();
        }

        // check whether pieces can make moves
        boolean canMakeMove = false;

        for (Piece piece : pieces) {
          if (piece.getTeamId().equals(currentTeam + "")) {
            if (canMove(piece)) {
              canMakeMove = true;
              break;
            }
          }
        }

        if (!canMakeMove && remainingPlayers == 2) {
          // game ends in tie
          endedDate = new Date();
          System.out.println("Tie");

          winner = new String[remainingPlayers];
          for (int i = 1; i <= remainingPlayers; i++) {
            winner[i - 1] = String.valueOf(i);
          }

          gameOver = true;
          System.out.println("Game Over 3");
          this.interrupt();
        }

        // move skipped if time over, no pieces remaining or unable to make move
        if (getRemainingMoveTimeInSeconds() == 0 || team.getPieces().length == 0 || !canMakeMove) {
          moveTime = 0;
          gameState.setCurrentTeam((currentTeam % teams.length) + 1);
        }
      }
    }
  }
}
