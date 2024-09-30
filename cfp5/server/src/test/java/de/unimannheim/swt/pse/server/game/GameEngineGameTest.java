package de.unimannheim.swt.pse.server.game;


import de.unimannheim.swt.pse.server.controller.data.GameSessionRequest;
import de.unimannheim.swt.pse.server.game.exceptions.GameOver;
import de.unimannheim.swt.pse.server.game.exceptions.NoMoreTeamSlots;
import de.unimannheim.swt.pse.server.game.map.MapGenerator;
import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import de.unimannheim.swt.pse.server.game.map.PieceDescription;
import de.unimannheim.swt.pse.server.game.map.PlacementType;
import de.unimannheim.swt.pse.server.game.map.ShapeType;
import de.unimannheim.swt.pse.server.game.move.MoveGenerator;
import de.unimannheim.swt.pse.server.game.state.Piece;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import de.unimannheim.swt.pse.server.game.state.StateGenerator;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
class GameEngineGameTest {

  private static final GameEngineGame gameEngineGame = new GameEngineGame();

  @BeforeAll
  static void setUp() {
    // generate new MapTemplate
    MapGenerator mapGenerator = new MapGenerator();
    MapTemplate mapTemplate = mapGenerator.generateMap(new int[]{10, 10}, 2, 1, 5, 5,
        PlacementType.symmetrical, 60, 30);

    // create Pieces
    PieceDescription[] pieceDescription = new PieceDescription[mapTemplate.getPieces().length];
    for (int i = 0; i < pieceDescription.length - 1; i++) {
      pieceDescription[i] = mapGenerator.getCustomPieceDescription(3, 3, 3, 3, 3, 3, 3, 3, null,
          10);
    }
    pieceDescription[4] = mapGenerator.getCustomPieceDescription(0, 0, 0, 0, 0, 0, 0, 0,
        ShapeType.lshape, 10);
    mapTemplate.setPieces(pieceDescription);

    // create mapTemplate in game engine
    gameEngineGame.create(mapTemplate);
  }

  void printOut2ArrayString(String[][] expected, String[][] result) {
    //getting results shown on the console
    System.out.println("Expected:");
    for (int i = 0; i < expected.length; i++) {
      for (int j = 0; j < expected[i].length; j++) {
        if (expected[i][j].isEmpty()) {
          System.out.print(expected[i][j] + "-----\t");
        } else {
          System.out.print(expected[i][j] + "\t");
        }
      }
      System.out.print("\n");
    }
    System.out.println("Result:");
    for (int i = 0; i < result.length; i++) {
      for (int j = 0; j < result[i].length; j++) {
        if (result[i][j].isEmpty()) {
          System.out.print(result[i][j] + "-----\t");
        } else {
          System.out.print(result[i][j] + "\t");
        }
      }
      System.out.print("\n");
    }

  }

  void printOut1ArrayInt(int[] expected, int[] result) {
    System.out.println("Expected");
    for (int i : expected) {
      System.out.println(i);
    }
    System.out.println("Result");
    for (int i : result) {
      System.out.println(i);
    }
  }

  void printOut2ArrayInt(int[][] expected, int[][] result) {
    System.out.println("Expected:");
    for (int[] ints : expected) {
      for (int j = 0; j < ints.length; j++) {
        System.out.print(ints[j] + "\t");
      }
      System.out.println();
    }
    System.out.println("Result:");
    for (int[] ints : result) {
      for (int j = 0; j < ints.length; j++) {
        System.out.print(ints[j] + "\t");
      }
      System.out.println();
    }
  }

  //test for even grid size for Team 1
  @Test
  void testPlaceBaseForTeam10X10Team1() {
    String[][] gridInp = new String[10][10];
    for (String[] strings : gridInp) {
      Arrays.fill(strings, "");
    }
    int team = 1;
    String[][] expected =
        {{"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "b:1", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", ""}};
    String[][] result = gameEngineGame.placeBaseForTeam(gridInp, team);
    assertArrayEquals(expected, result);
  }


  //test for odd grid size with Team 1
  @Test
  void testPlaceBaseForTeam11X11Team1() {
    String[][] gridInp = new String[11][11];
    for (String[] strings : gridInp) {
      Arrays.fill(strings, "");
    }
    int team = 1;
    String[][] expected =
        {{"", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "b:1", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", "", "", ""}};
    String[][] result = gameEngineGame.placeBaseForTeam(gridInp, team);
    //printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  //test for not quadratic grid size for Team 1
  @Test
  void testPlaceBaseForTeam10X19Team1() {
    String[][] gridInp = new String[6][9];
    for (String[] strings : gridInp) {
      Arrays.fill(strings, "");
    }
    int team = 1;
    String[][] expected =
        {{"", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "b:1", "", "", "", ""},
            {"", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", ""}};
    String[][] result = gameEngineGame.placeBaseForTeam(gridInp, team);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlaceBaseForTeam10X19Team2() {
    String[][] gridInp = new String[6][9];
    for (String[] strings : gridInp) {
      Arrays.fill(strings, "");
    }
    int team = 2;
    String[][] expected =
        {{"", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "b:2", "", "", "", ""},
            {"", "", "", "", "", "", "", "", ""},
            {"", "", "", "", "", "", "", "", ""}};
    String[][] result = gameEngineGame.placeBaseForTeam(gridInp, team);
    assertArrayEquals(expected, result);
  }

  @Test
  void testGetMaxRange5x5Last() {
    double[][] rangeMatrix =
        {{1, 2, 3, 4, 5},
            {2, 3, 4, 5, 6},
            {3, 4, 5, 6, 7},
            {4, 5, 6, 7, 8},
            {5, 6, 7, 8, 9}};

    int[] expected = {rangeMatrix.length - 1, rangeMatrix[0].length - 1};
    int[] result = gameEngineGame.getMaxRange(rangeMatrix);
    assertArrayEquals(expected, result);
  }

  @Test
  void testGetMaxRange5x5First() {
    double[][] rangeMatrix =
        {{10, 2, 3, 4, 5},
            {2, 3, 4, 5, 6},
            {3, 4, 5, 6, 7},
            {4, 5, 6, 7, 8},
            {5, 6, 7, 8, 9}};

    int[] expected = {0, 0};
    int[] result = gameEngineGame.getMaxRange(rangeMatrix);
    assertArrayEquals(expected, result);
  }

  @Test
  void testGetMaxRange4x5() {
    double[][] rangeMatrix =
        {{0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0},
            {0, 0, 0, 0, 0}};

    int[] expected = {2, 3};
    int[] result = gameEngineGame.getMaxRange(rangeMatrix);
    assertArrayEquals(expected, result);
  }

  @Test
  void testUpdateRangeMatrix() {
    double[][] rangeMatrix =
        {{7, 7, 7, 7, 7},
            {7, 7, 8, 7, 7},
            {7, 7, 7, 7, 7},
            {7, 7, 7, 7, 7},
            {7, 7, 0, 7, 7}};
    int[] position = {2, 1};
    double[][] expected =
        {{2, 2, 2, 3, 4},
            {1, 1, 1, 2, 3},
            {1, 0, 1, 2, 3},
            {1, 1, 1, 2, 3},
            {2, 2, 0, 3, 4}};
    double[][] result = gameEngineGame.updateRangeMatrix(rangeMatrix, position);
    for (int i = 0; i < result.length; i++) {
      for (int j = 0; j < result[i].length; j++) {
        result[i][j] = Math.round(result[i][j]);
      }
    }
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesSpacedOut() {
    int[] gridSize = new int[2];
    gridSize[0] = 5;
    gridSize[1] = 5;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    String[][] result = gameEngineGame.placePiecesSpacedOut(areaGrid,
        stateGenerator.generatePieces(5, 1));
    String[][] expected =
        {{"p:1_1", "", "", "", "p:1_3"},
            {"", "", "p:1_5", "", ""},
            {"", "", "", "", ""},
            {"", "", "", "", ""},
            {"p:1_4", "", "", "", "p:1_2"}};
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesDefensive5Pieces() {
    int[] gridSize = new int[2];
    gridSize[0] = 5;
    gridSize[1] = 5;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesDefensive(areaGrid,
        stateGenerator.generatePieces(5, 1));
    String[][] expected =
        {{"", "", "", "", ""},
            {"", "p:1_5", "p:1_1", "", ""},
            {"", "p:1_2", "b:1", "p:1_3", ""},
            {"", "", "p:1_4", "", ""},
            {"", "", "", "", ""}};
    //printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  //OutOfBounceTest
  @Test
  void testPlacePiecesDefensive14Pieces() {
    int[] gridSize = new int[2];
    gridSize[0] = 5;
    gridSize[1] = 5;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesDefensive(areaGrid,
        stateGenerator.generatePieces(14, 1));
    String[][] expected =
        {{"", "p:1_13", "p:1_9", "p:1_14", ""},
            {"", "p:1_5", "p:1_1", "p:1_6", ""},
            {"p:1_10", "p:1_2", "b:1", "p:1_3", "p:1_11"},
            {"", "p:1_7", "p:1_4", "p:1_8", ""},
            {"", "", "p:1_12", "", ""}};
    assertArrayEquals(expected, result);
  }

  @Test
  void testGetHighestPrioAuto() {
    int[][] prioMatrix = new int[3][4];
    for (int[] rows : prioMatrix) {
      Arrays.fill(rows, 10);
    }
    for (int i = prioMatrix.length - 1; i >= 0; i--) {
      for (int j = prioMatrix.length - 1; j >= 0; j--) {
        prioMatrix[i][j] = 1;
        int[] result = gameEngineGame.getHighestPrio(prioMatrix);
        int[] expected = new int[2];
        expected[0] = i;
        expected[1] = j;
        assertArrayEquals(expected, result);
      }
    }
  }

  @Test
  void testPlacePiecesOddSymmetricalFirstRowOne() {
    int[] gridSize = new int[2];
    gridSize[0] = 5;
    gridSize[1] = 5;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(1, 1));
    String[][] expected =
        {{"", "", "p:1_1", "", ""},
            {"", "", "", "", ""},
            {"", "", "b:1", "", ""},
            {"", "", "", "", ""},
            {"", "", "", "", ""}};
    //printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesOddSymmetricalFirstRowFull() {
    int[] gridSize = new int[2];
    gridSize[0] = 5;
    gridSize[1] = 5;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(5, 1));
    String[][] expected =
        {{"p:1_4", "p:1_2", "p:1_1", "p:1_3", "p:1_5"},
            {"", "", "", "", ""},
            {"", "", "b:1", "", ""},
            {"", "", "", "", ""},
            {"", "", "", "", ""}};
    printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesOddSymmetricalSecondRowOne() {
    int[] gridSize = new int[2];
    gridSize[0] = 5;
    gridSize[1] = 5;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(6, 1));
    String[][] expected =
        {{"p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"", "", "p:1_6", "", ""},
            {"", "", "b:1", "", ""},
            {"", "", "", "", ""},
            {"", "", "", "", ""}};
    //printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesOddSymmetricalSecondRowTwo() {
    int[] gridSize = new int[2];
    gridSize[0] = 5;
    gridSize[1] = 5;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(7, 1));
    String[][] expected =
        {{"p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"", "p:1_6", "", "p:1_7", ""},
            {"", "", "b:1", "", ""},
            {"", "", "", "", ""},
            {"", "", "", "", ""}};
    //printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesOddSymmetricalSecondRowFull() {
    int[] gridSize = new int[2];
    gridSize[0] = 5;
    gridSize[1] = 5;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(10, 1));
    String[][] expected =
        {{"p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"p:1_9", "p:1_7", "p:1_6", "p:1_8", "p:1_10"},
            {"", "", "b:1", "", ""},
            {"", "", "", "", ""},
            {"", "", "", "", ""}};
    printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesOddSymmetricalBaseInterfering() {
    int[] gridSize = new int[2];
    gridSize[0] = 5;
    gridSize[1] = 5;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(11, 1));
    String[][] expected =
        {{"p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"p:1_10", "p:1_9", "p:1_8", "p:1_7", "p:1_6"},
            {"", "", "b:1", "", ""},
            {"", "", "p:1_11", "", ""},
            {"", "", "", "", ""}};
    //printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesOddSymmetricalTwoSurroundBase() {
    int[] gridSize = new int[2];
    gridSize[0] = 5;
    gridSize[1] = 5;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(12, 1));
    String[][] expected =
        {{"p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"p:1_10", "p:1_9", "p:1_8", "p:1_7", "p:1_6"},
            {"", "p:1_11", "b:1", "p:1_12", ""},
            {"", "", "", "", ""},
            {"", "", "", "", ""}};
    printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesOddSymmetricalThreeSurroundBase() {
    int[] gridSize = new int[2];
    gridSize[0] = 5;
    gridSize[1] = 5;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(13, 1));
    String[][] expected =
        {{"p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"p:1_10", "p:1_9", "p:1_8", "p:1_7", "p:1_6"},
            {"", "p:1_11", "b:1", "p:1_12", ""},
            {"", "", "p:1_13", "", ""},
            {"", "", "", "", ""}};
    //printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }


  @Test
  void testPlacePiecesEvenSymmetricalFirstRowOne() {
    int[] gridSize = new int[2];
    gridSize[0] = 6;
    gridSize[1] = 6;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(1, 1));
    String[][] expected =
        {{"", "", "", "p:1_1", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "b:1", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""}};
    printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesEvenSymmetricalFirstRowFull() {
    int[] gridSize = new int[2];
    gridSize[0] = 6;
    gridSize[1] = 6;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(5, 1));
    String[][] expected =
        {{"", "p:1_4", "p:1_2", "p:1_1", "p:1_3", "p:1_5"},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "b:1", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""}};
    printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesEvenSymmetricalSecondRowOne() {
    int[] gridSize = new int[2];
    gridSize[0] = 6;
    gridSize[1] = 6;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(6, 1));
    String[][] expected =
        {{"", "p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"", "", "", "p:1_6", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "b:1", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""}};
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesEvenSymmetricalSecondRowTwo() {
    int[] gridSize = new int[2];
    gridSize[0] = 6;
    gridSize[1] = 6;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(7, 1));
    String[][] expected =
        {{"", "p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"", "", "p:1_6", "", "p:1_7", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "b:1", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""}};
    printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesEvenSymmetricalSecondRowFull() {
    int[] gridSize = new int[2];
    gridSize[0] = 6;
    gridSize[1] = 6;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(10, 1));
    String[][] expected =
        {{"", "p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"", "p:1_9", "p:1_7", "p:1_6", "p:1_8", "p:1_10"},
            {"", "", "", "", "", ""},
            {"", "", "", "b:1", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""}};
    printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesEvenSymmetricalBaseInterfering() {
    int[] gridSize = new int[2];
    gridSize[0] = 6;
    gridSize[1] = 6;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(11, 1));
    String[][] expected =
        {{"", "p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"", "p:1_10", "p:1_9", "p:1_8", "p:1_7", "p:1_6"},
            {"", "", "", "p:1_11", "", ""},
            {"", "", "", "b:1", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""}};
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesEvenSymmetricalThreeSurroundBase() {
    int[] gridSize = new int[2];
    gridSize[0] = 6;
    gridSize[1] = 6;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(13, 1));
    String[][] expected =
        {{"", "p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"", "p:1_10", "p:1_9", "p:1_8", "p:1_7", "p:1_6"},
            {"", "", "p:1_12", "p:1_11", "p:1_13", ""},
            {"", "", "", "b:1", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""}};
    printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }


  @Test
  void testPlacePiecesEvenSymmetricalExtraRowOne() {
    int[] gridSize = new int[2];
    gridSize[0] = 6;
    gridSize[1] = 6;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(31, 1));
    String[][] expected =
        {{"p:1_6", "p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"p:1_12", "p:1_11", "p:1_10", "p:1_9", "p:1_8", "p:1_7"},
            {"p:1_18", "p:1_17", "p:1_16", "p:1_15", "p:1_14", "p:1_13"},
            {"p:1_23", "p:1_22", "p:1_21", "b:1", "p:1_20", "p:1_19"},
            {"p:1_29", "p:1_28", "p:1_27", "p:1_26", "p:1_25", "p:1_24"},
            {"", "", "p:1_31", "p:1_30", "", ""}};
    printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesEvenSymmetricalExtraRowTwo() {
    int[] gridSize = new int[2];
    gridSize[0] = 6;
    gridSize[1] = 6;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(32, 1));
    String[][] expected =
        {{"p:1_6", "p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"p:1_12", "p:1_11", "p:1_10", "p:1_9", "p:1_8", "p:1_7"},
            {"p:1_18", "p:1_17", "p:1_16", "p:1_15", "p:1_14", "p:1_13"},
            {"p:1_23", "p:1_22", "p:1_21", "b:1", "p:1_20", "p:1_19"},
            {"p:1_29", "p:1_28", "p:1_27", "p:1_26", "p:1_25", "p:1_24"},
            {"", "", "p:1_31", "p:1_30", "p:1_32", ""}};
    printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesEvenSymmetricalExtraRowThree() {
    int[] gridSize = new int[2];
    gridSize[0] = 6;
    gridSize[1] = 6;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(33, 1));
    String[][] expected =
        {{"p:1_6", "p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"p:1_12", "p:1_11", "p:1_10", "p:1_9", "p:1_8", "p:1_7"},
            {"p:1_18", "p:1_17", "p:1_16", "p:1_15", "p:1_14", "p:1_13"},
            {"p:1_23", "p:1_22", "p:1_21", "b:1", "p:1_20", "p:1_19"},
            {"p:1_29", "p:1_28", "p:1_27", "p:1_26", "p:1_25", "p:1_24"},
            {"", "p:1_33", "p:1_31", "p:1_30", "p:1_32", ""}};
    printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }

  @Test
  void testPlacePiecesEvenSymmetricalExtraRowFull() {
    int[] gridSize = new int[2];
    gridSize[0] = 6;
    gridSize[1] = 6;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] areaGrid = mapGenerator.generateEmptyGrid(gridSize);
    areaGrid = gameEngineGame.placeBaseForTeam(areaGrid, 1);
    String[][] result = gameEngineGame.placePiecesSymmetrical(areaGrid,
        stateGenerator.generatePieces(35, 1));
    String[][] expected =
        {{"p:1_6", "p:1_5", "p:1_4", "p:1_3", "p:1_2", "p:1_1"},
            {"p:1_12", "p:1_11", "p:1_10", "p:1_9", "p:1_8", "p:1_7"},
            {"p:1_18", "p:1_17", "p:1_16", "p:1_15", "p:1_14", "p:1_13"},
            {"p:1_23", "p:1_22", "p:1_21", "b:1", "p:1_20", "p:1_19"},
            {"p:1_29", "p:1_28", "p:1_27", "p:1_26", "p:1_25", "p:1_24"},
            {"p:1_35", "p:1_33", "p:1_31", "p:1_30", "p:1_32", "p:1_34"}};
    printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);
  }


  @Test
  void testPlacePiecesInsufficientSpace() {
    int[] gridSize = new int[2];
    gridSize[0] = 5;
    gridSize[1] = 5;
    MapGenerator mapGenerator = new MapGenerator();
    StateGenerator stateGenerator = new StateGenerator();
    String[][] result = mapGenerator.generateEmptyGrid(gridSize);
    assertThrows(IllegalArgumentException.class, () -> gameEngineGame.placePieces(result,
        PlacementType.symmetrical, stateGenerator.generatePieces(100, 2), 2));
  }


  @Test
  void testPlaceBlocksFull() {
    String[][] grid =
        {{"test", "", "", "", ""},
            {"", "", "test", "", ""},
            {"", "", "", "", ""},
            {"", "", "", "test", ""},
            {"", "", "test", "", ""}};
    String[][] result = gameEngineGame.placeBlocks(grid, 7);

    String[][] expected =
        {{"test", "", "b", "", "b"},
            {"", "", "test", "", ""},
            {"b", "", "b", "", "b"},
            {"", "", "", "test", ""},
            {"b", "", "test", "b", ""}};
    //printOut2ArrayString(expected, result);
    assertArrayEquals(expected, result);

    grid = gameEngineGame.getCurrentGameState().getGrid();
    System.out.println("Test 4");
    printOut2ArrayString(grid, grid);
  }

  @Order(1)
  @Test
  void testJoinGame() {
    String[][] grid = gameEngineGame.getCurrentGameState().getGrid();
    System.out.println("Test 5");
    printOut2ArrayString(grid, grid);
    // check if all team slots are empty before joining
    int expectedRemainingSlots = 2; // Assuming a game can have 2 teams
    assertEquals(expectedRemainingSlots, gameEngineGame.getRemainingTeamSlots());

    // check if team slots are filled after joining
    gameEngineGame.joinGame("1");
    expectedRemainingSlots = 1; // Assuming a game can have 2 teams
    assertEquals(expectedRemainingSlots, gameEngineGame.getRemainingTeamSlots());

    // check if game is started if not all teams have joined
    assertFalse(gameEngineGame.isStarted());

    // check that no more teams can join
    gameEngineGame.joinGame("2");
    assertThrows(NoMoreTeamSlots.class, () -> gameEngineGame.joinGame("3"));

    // check that game is started when all teams have joined
    assertTrue(gameEngineGame.isStarted());
  }

  @Order(2)
  @Test
  void testMoves() {
    // move generator to create moves
    MoveGenerator moveGenerator = new MoveGenerator();

    // get gamestate
    GameState gameState = gameEngineGame.getCurrentGameState();
    String[][] grid = gameState.getGrid();
    System.out.println("Test before: ");
    printOut2ArrayString(grid, grid);

    // create move
    System.out.println("Move 1");
    Piece piece1 = gameEngineGame.getPiece(4 + "", 1);
    Move move1 = moveGenerator.generateMove(1 + "", 4 + "",
        new int[]{piece1.getPosition()[0] - 3, piece1.getPosition()[1]});
    gameState.setCurrentTeam(1);

    // Tests move 1
    // Test if move is valid
    assertTrue(gameEngineGame.isValidMove(move1));

    // make move
    gameEngineGame.makeMove(move1);

    // Test if move was successful
    assertEquals(move1.getNewPosition(), gameEngineGame.getPiece(4 + "", 1).getPosition());
    assertEquals(move1, gameState.getLastMove());

    // create move 2
    System.out.println("Move 2");
    Piece piece2 = gameEngineGame.getPiece(5 + "", 2);
    Move move2 = moveGenerator.generateMove(2 + "", 5 + "",
        new int[]{piece2.getPosition()[0] + 2, piece2.getPosition()[1] - 1});

    // Tests move 2
    // Test if move is valid
    assertTrue(gameEngineGame.isValidMove(move2));

    // make move
    gameEngineGame.makeMove(move2);

    // Test if move was successful
    assertEquals(move2.getNewPosition(), gameEngineGame.getPiece(5 + "", 2).getPosition());
    assertEquals(move2, gameState.getLastMove());

    // create move 3
    System.out.println("Move 3");
    Piece piece3 = gameEngineGame.getPiece(4 + "", 1);
    Move move3 = moveGenerator.generateMove(1 + "", 4 + "",
        new int[]{piece3.getPosition()[0] - 2, piece3.getPosition()[1]});

    // Tests move 3
    // Test if move is valid
    assertTrue(gameEngineGame.isValidMove(move3));

    // make move
    gameEngineGame.makeMove(move3);

    // Test if move was successful
    assertEquals(move3.getNewPosition(), gameEngineGame.getPiece(4 + "", 1).getPosition());
    assertEquals(move3, gameState.getLastMove());

    // Test if thrown piece was removed
    assertNull(gameEngineGame.getPiece(3 + "", 2));

    printOut2ArrayString(grid, grid);
    // create move 4
    System.out.println("Move 4");
    Piece piece4 = gameEngineGame.getPiece(4 + "", 1);
    Move move4 = moveGenerator.generateMove(1 + "", 4 + "",
        new int[]{piece4.getPosition()[0] - 1, piece4.getPosition()[1] + 1});
    gameState.setCurrentTeam(1);

    // Tests move 4
    // Test if move is valid
    assertTrue(gameEngineGame.isValidMove(move4));

    // make move and check if game is over
    assertThrows(GameOver.class, () -> gameEngineGame.makeMove(move4));
  }

  @Order(3)
  @Test
  void testCanMove() {
    GameState gameState = gameEngineGame.getCurrentGameState();
    String[][] grid = gameState.getGrid();
    printOut2ArrayString(grid, grid);

    // select piece 1
    System.out.println("Piece 1: ");
    Piece piece1 = gameEngineGame.getPiece(2 + "", 1);

    // test if piece can move initially
    assertTrue(gameEngineGame.canMove(piece1));

    // block piece's movement
    grid[6][5] = "b";
    grid[6][4] = "b";
    grid[6][3] = "b";
    grid[7][3] = "b";
    grid[8][4] = "b";
    grid[8][3] = "b";
    printOut2ArrayString(grid, grid);

    // test that piece cannot move
    assertFalse(gameEngineGame.canMove(piece1));

    // select piece 2
    System.out.println("Piece 2: ");
    Piece piece2 = gameEngineGame.getPiece(2 + "", 2);
    gameEngineGame.getCurrentGameState().setCurrentTeam(2);

    // test if piece can move initially
    assertTrue(gameEngineGame.canMove(piece2));

    // block piece's movement
    grid[3][4] = "b";
    grid[3][5] = "b";
    grid[3][6] = "b";
    grid[1][5] = "b";
    grid[1][6] = "b";
    grid[1][4] = "b";
    printOut2ArrayString(grid, grid);

    // test that piece cannot move
    assertFalse(gameEngineGame.canMove(piece2));
  }

  @Test
  void testRotateMapTeam1() {
    String[][] gridInput = new String[4][5];
    gridInput[gridInput.length - 1][gridInput[0].length - 1] = "test";
    String[][] result = gameEngineGame.rotateMap(gridInput, 1);
    String[][] expected = new String[4][5];
    expected[gridInput.length - 1][gridInput[0].length - 1] = "test";
    assertArrayEquals(expected, result);
  }

  @Test
  void testRotateMapTeam2() {
    String[][] gridInput = new String[4][5];
    gridInput[gridInput.length - 1][gridInput[0].length - 1] = "test";
    String[][] result = gameEngineGame.rotateMap(gridInput, 2);
    String[][] expected = new String[4][5];
    expected[0][0] = "test";
    assertArrayEquals(expected, result);
  }

  @Test
  void testRotateMapTeam3() {
    MapGenerator mapGenerator = new MapGenerator();
    String[][] gridInput = mapGenerator.generateEmptyGrid(new int[]{4, 5});
    gridInput[gridInput.length - 1][gridInput[0].length - 1] = "test";
    String[][] result = gameEngineGame.rotateMap(gridInput, 3);
    String[][] expected = mapGenerator.generateEmptyGrid(new int[]{5, 4});
    expected[0][expected[0].length - 1] = "test";
    assertArrayEquals(expected, result);
  }

  @Test
  void testRotateMapTeam4() {
    MapGenerator mapGenerator = new MapGenerator();
    String[][] gridInput = mapGenerator.generateEmptyGrid(new int[]{4, 5});
    gridInput[gridInput.length - 1][gridInput[0].length - 1] = "test";
    String[][] result = gameEngineGame.rotateMap(gridInput, 4);
    String[][] expected = mapGenerator.generateEmptyGrid(new int[]{5, 4});
    expected[expected.length - 1][0] = "test";
    assertArrayEquals(expected, result);
  }

  @Test
  void testRotateMapBackTeam1() {
    String[][] gridInput = new String[4][5];
    gridInput[gridInput.length - 1][gridInput[0].length - 1] = "test";
    String[][] result = gameEngineGame.rotateMapBack(gridInput, 1);
    String[][] expected = new String[4][5];
    expected[gridInput.length - 1][gridInput[0].length - 1] = "test";
    assertArrayEquals(expected, result);
  }

  @Test
  void testRotateMapBackTeam2() {
    String[][] gridInput = new String[4][5];
    gridInput[gridInput.length - 1][gridInput[0].length - 1] = "test";
    String[][] result = gameEngineGame.rotateMapBack(gridInput, 2);
    String[][] expected = new String[4][5];
    expected[0][0] = "test";
    assertArrayEquals(expected, result);
  }

  @Test
  void testRotateMapBackTeam3() {
    MapGenerator mapGenerator = new MapGenerator();
    String[][] gridInput = mapGenerator.generateEmptyGrid(new int[]{4, 5});
    gridInput[gridInput.length - 1][gridInput[0].length - 1] = "test";
    String[][] result = gameEngineGame.rotateMapBack(gridInput, 3);
    String[][] expected = mapGenerator.generateEmptyGrid(new int[]{5, 4});
    expected[expected.length - 1][0] = "test";
    assertArrayEquals(expected, result);
  }

  @Test
  void testRotateMapBackTeam4() {
    MapGenerator mapGenerator = new MapGenerator();
    String[][] gridInput = mapGenerator.generateEmptyGrid(new int[]{4, 5});
    gridInput[gridInput.length - 1][gridInput[0].length - 1] = "test";
    String[][] result = gameEngineGame.rotateMapBack(gridInput, 4);
    String[][] expected = mapGenerator.generateEmptyGrid(new int[]{5, 4});
    expected[0][expected[0].length - 1] = "test";
    assertArrayEquals(expected, result);
  }

  void test() {
    GameSessionRequest testung = new GameSessionRequest();
  }
}
