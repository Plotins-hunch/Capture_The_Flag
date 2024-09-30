package de.unimannheim.swt.pse.server.controller;

import de.unimannheim.swt.pse.server.controller.data.GameSessionResponse;
import de.unimannheim.swt.pse.server.game.GameEngineGame;
import de.unimannheim.swt.pse.server.game.map.Directions;
import de.unimannheim.swt.pse.server.game.map.MapGenerator;
import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import de.unimannheim.swt.pse.server.game.map.Movement;
import de.unimannheim.swt.pse.server.game.map.PieceDescription;
import de.unimannheim.swt.pse.server.game.map.PlacementType;
import de.unimannheim.swt.pse.server.game.map.Shape;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import de.unimannheim.swt.pse.server.game.state.Piece;
import de.unimannheim.swt.pse.server.game.state.Team;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestClient {

  /**
   * prints information about the input GameSessionRequest
   *
   * @param gsr GameSessionRequest
   * @author jdeiting
   */
  void printGameSessionResponse(GameSessionResponse gsr) {
    System.out.println("GameSessionResponse:");
    System.out.println("ID: " + gsr.getId());
    System.out.println("Started Date: " + gsr.getGameStarted());
    System.out.println("Ended Date: " + gsr.getGameEnded());
    System.out.println("Is Game over?: " + gsr.isGameOver());
    System.out.println("Winner: " + Arrays.stream(gsr.getWinner()).toList());
    System.out.println();
  }

  /**
   * prints information about the input GameState
   *
   * @param gameStateInp GameState
   * @author jdeiting
   */

  void printGameState(GameState gameStateInp) {
    System.out.println("GameState: ");
    System.out.println("Teamcount: " + gameStateInp.getTeams().length);
    System.out.println("Current GameBoard: ");
    String[][] gameBoard = gameStateInp.getGrid();

    for (String[] strings : gameBoard) {
      for (String string : strings) {
        if (string.isEmpty()) {
          System.out.print(string + "-----\t");
        } else if (string.matches("b:.+")) {
          System.out.print("-" + string + "-\t");
        } else if (string.equals("b")) {
          System.out.print("--" + string + "--\t");
        } else {
          System.out.print(string + "\t");

        }
      }
      System.out.print("\n");
    }
    System.out.println();
    System.out.println("Current Team: " + gameStateInp.getCurrentTeam());
    Team[] teams = gameStateInp.getTeams();
    System.out.println("\nTeams: ");
    for (Team team : teams) {
      System.out.println("Team: " + team.getId());
      System.out.println("Color: " + team.getColor());
      System.out.println("Base Coordinate: " + team.getBase()[0] + " | " + team.getBase()[1]);
      System.out.println("FlagCount: " + team.getFlags());
      System.out.println("TeamPieces: ");
      Piece[] pieces = team.getPieces();
      for (Piece piece : pieces) {
        System.out.print("ID: " + piece.getId() + "\t");
        System.out.print("TeamID: " + piece.getTeamId() + "\t");
        System.out.print(
            "PieceCoord: " + piece.getPosition()[0] + " | " + piece.getPosition()[1] + "\t");
        System.out.print("Type: " + piece.getDescription().getType() + "\t");
        System.out.print("AttackPower: " + piece.getDescription().getAttackPower() + "\n");
      }
      System.out.println();
    }
  }

  /**
   * prints the input grid
   *
   * @param gridInp String[][]
   * @author jdeiting
   */
  void printGameBoard(String[][] gridInp) {
    System.out.println("\nGameBoard:");
    for (String[] strings : gridInp) {
      for (String string : strings) {
        if (string.isEmpty()) {
          System.out.print(string + "-----\t");
        } else if (string.equals("b")) {
          System.out.print("--" + string + "--\t");
        } else if (string.matches("b:.+")) {
          System.out.print("-" + string + "-\t");
        } else {
          System.out.print(string + "\t");
        }
      }
      System.out.print("\n");
    }
  }

  /**
   * Create a specific PieceDescription for testing
   *
   * @return customPieceDescription
   * @author jdeiting
   */
  PieceDescription getCustomPieceDescription() {
    PieceDescription customPiece = new PieceDescription();
    customPiece.setAttackPower(0);
    customPiece.setType("CustomPiece");
    customPiece.setCount(1);
    Movement customMovement = new Movement();
    customMovement.setShape(new Shape());
    Directions customDirections = new Directions();
    customDirections.setUp(1);
    customDirections.setDown(1);
    customDirections.setLeft(1);
    customDirections.setRight(1);
    customDirections.setUpLeft(1);
    customDirections.setUpRight(1);
    customDirections.setDownLeft(1);
    customDirections.setDownRight(1);
    customMovement.setDirections(customDirections);
    customPiece.setMovement(customMovement);
    return customPiece;
  }

  /**
   * Create a MapTemplate for testing
   *
   * @param gridSizeInp      size of the grid
   * @param flagsInp         number of flags
   * @param placementTypeInp type of placement
   * @param pieceCount       number of pieces
   * @param blocksInp        number of blocks
   * @param moveTimeInp      time for a move
   * @param totalTimeInp     total time for the game
   * @param teamCountInp     number of teams
   * @return creted MapTemplate
   * @author jdeiting
   */

  MapTemplate createMapTemplate(int[] gridSizeInp, int flagsInp, PlacementType placementTypeInp,
      int pieceCount, int blocksInp, int moveTimeInp, int totalTimeInp,
      int teamCountInp) {
    MapTemplate resultTemplate = new MapTemplate();
    MapGenerator mapGen = new MapGenerator();
    resultTemplate.setGridSize(gridSizeInp);
    resultTemplate.setFlags(flagsInp);
    resultTemplate.setPlacement(placementTypeInp);

    ArrayList<PieceDescription> pieceDescs = new ArrayList<>(
        List.of(mapGen.generatePieceDescriptions(pieceCount - 1)));
    pieceDescs.add(getCustomPieceDescription());
    resultTemplate.setPieces(pieceDescs.toArray(new PieceDescription[0]));
    resultTemplate.setBlocks(blocksInp);
    resultTemplate.setMoveTimeLimitInSeconds(moveTimeInp);
    resultTemplate.setTotalTimeLimitInSeconds(totalTimeInp);
    resultTemplate.setTeams(teamCountInp);
    return resultTemplate;
  }

  /**
   * TestClient for diverse testing of GameEngineGame
   *
   * @author jdeiting
   */
  void startTestClient() {

    //test
    GameEngineGame testGame = new GameEngineGame();
    TestClient testClient = new TestClient();
    MapTemplate testTemplate1 = testClient.createMapTemplate(new int[]{6, 6}, 1,
        PlacementType.symmetrical, 1, 0, 2, 600, 2);
    testGame.create(testTemplate1);
    Team startingTeam = testGame.joinGame(1 + "");
    startingTeam = testGame.joinGame(2 + "");
    //startingTeam = testGame.joinGame(3 + "");
    //startingTeam = testGame.joinGame(4 + "");
    testClient.printGameState(testGame.getCurrentGameState());
    System.out.println("Started at: " + testGame.getStartedDate().toString());

    int currentTeam = 0;
    //int moveUp1 = 8;
    //int moveUp2 = 1;
    int move1 = 0;
    int move2 = 1;
    int moveCounter = 5;

    while (testGame.getRemainingMoveTimeInSeconds() > 0) {
      try {
        if (currentTeam != testGame.getCurrentGameState().getCurrentTeam()) {
          currentTeam = testGame.getCurrentGameState().getCurrentTeam();
          System.out.println("\nCurrentTeam: " + currentTeam);
        }
        //MOVEMENT Team 1
        if (testGame.getRemainingMoveTimeInSeconds() < 2
            && testGame.getCurrentGameState().getCurrentTeam() == 1) {
          System.out.println("\nMaking Move, Team " + currentTeam + "\n");
          Move testMove = new Move();
          testMove.setPieceId(6 + "");
          testMove.setNewPosition(new int[]{move1, move2});
          System.out.println("Input Position: " + testMove.getNewPosition()[0] + " | "
              + testMove.getNewPosition()[1]);
          testGame.makeMove(testMove);
/*
          if (moveCounter > 0) {
            move1--;
            moveCounter--;
          } else {
            move1--;
            move2--;
          }*/
          move1--;
          //move2--;
          //moveUp1++;

        }
        /*
        //MOVEMENT Team 2
        if (testGame.getRemainingMoveTimeInSeconds() < 9 && testGame.getCurrentGameState().getCurrentTeam() == 2) {
          System.out.println(
              "\nMaking Move, Team " + testGame.getCurrentGameState().getCurrentTeam() + "\n");
          Move testMove = new Move();
          testMove.setPieceId(6 + "");
          testMove.setNewPosition(new int[]{moveUp2, 3});
          System.out.println("Input Position: " + testMove.getNewPosition()[0] + " | "
              + testMove.getNewPosition()[1]);
          testGame.makeMove(testMove);
          moveUp2++;
        }*/
        System.out.println("\nMoveTime: " + testGame.getRemainingMoveTimeInSeconds());
        //System.out.println("GameTime: " + testGame.getRemainingGameTimeInSeconds());
        {
          testClient.printGameBoard(testGame.getCurrentGameState().getGrid());
        }
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * multiple prints for testing symmetrical map generation
   *
   * @param testClientInp TestClient
   * @author jdeiting
   */
  void testSymmetrical(TestClient testClientInp) {
    GameEngineGame testGame = new GameEngineGame();
    testClientInp = new TestClient();
    for (int i = 1; i < 30; i++) {
      MapTemplate testTemplate1 = testClientInp.createMapTemplate(new int[]{10, 10}, 1,
          PlacementType.symmetrical, i, 3, 2, 600, 2);
      testGame.create(testTemplate1);
      printGameBoard(testGame.getCurrentGameState().getGrid());
    }

  }


  public static void main(String[] args) {
    TestClient testClient = new TestClient();
    //testClient.startTestClient();
    testClient.testSymmetrical(testClient);
  }

}
