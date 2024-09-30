package de.unimannheim.swt.pse.ai.MCTS.Helper;

import de.unimannheim.swt.pse.server.game.state.*;
import de.unimannheim.swt.pse.server.game.map.*;

public class GameStateFactory {

  public GameState createSampleGameState() {
    //create gameState
    GameState gameState = new GameState();
    gameState.setGrid(createEmptyGrid(7));
    Team[] teams = new Team[2];
    teams[0] = createTeam("1", 0);
    teams[1] = createTeam("2", 0);
    gameState.setTeams(teams);
    gameState.setCurrentTeam(1);

    gameState.getGrid()[0][3] = "b:2";
    gameState.getGrid()[6][3] = "b:1";

    //set last move
    Move lastMove = new Move();
    lastMove.setPieceId("1");
    lastMove.setTeamId("2");
    lastMove.setNewPosition(new int[]{3, 3});
    gameState.setLastMove(lastMove);

    //set pieces correctly
    Piece p1 = createPiece("1", "1");
    p1.setPosition(new int[]{4, 3});
    updatePosition(p1, new int[]{0, 0}, new int[]{1, 3}, gameState);
    Piece p2 = createPiece("2", "1");
    p2.setPosition(new int[]{5, 5});
    updatePosition(p2, new int[]{0, 0}, new int[]{5, 5}, gameState);
    teams[0].setPieces(new Piece[]{p1, p2});

    Piece p3 = createPiece("1", "2");
    p3.setPosition(new int[]{3, 3});
    updatePosition(p3, new int[]{0, 0}, new int[]{3, 3}, gameState);
    teams[1].setPieces(new Piece[]{p3});

    return gameState;
  }

  public GameState createImmediateCaptureWinState(boolean playerStarts) {
    //create gameState
    GameState gameState = new GameState();
    gameState.setGrid(createEmptyGrid(7));
    Team[] teams = new Team[2];
    teams[0] = createTeam("1", 0);
    teams[1] = createTeam("2", 0);
    gameState.setTeams(teams);
    gameState.setCurrentTeam(playerStarts ? 1 : 2);

    //set last move
    Move lastMove = new Move();
    lastMove.setPieceId("1");
    lastMove.setTeamId(playerStarts ? "2" : "1");
    lastMove.setNewPosition(new int[]{3, 3});
    gameState.setLastMove(lastMove);

    //set pieces correctly
    Piece p1 = createPiece("1", "1");
    p1.setPosition(new int[]{3, 3});
    PieceDescription p1Desc = createPieceDescriptionNormal();
    p1Desc.setMovement(createMovement(1, 0, 0, 0, 0, 0, 0, 0, false));
    p1.setDescription(p1Desc);
    updatePosition(p1, new int[]{0, 0}, new int[]{3, 3}, gameState);
    Piece p2 = createPiece("2", "1");
    p2.setPosition(new int[]{5, 5});
    updatePosition(p2, new int[]{0, 0}, new int[]{5, 5}, gameState);
    teams[0].setPieces(new Piece[]{p1, p2});

    Piece p3 = createPiece("1", "2");
    p3.setPosition(new int[]{2, 3});
    updatePosition(p3, new int[]{0, 0}, new int[]{2, 3}, gameState);
    teams[1].setPieces(new Piece[]{p3});

    return gameState;
  }

  public GameState createBaseCaptureWinState() {
    //create gameState
    GameState gameState = new GameState();
    gameState.setGrid(createEmptyGrid(7));
    Team[] teams = new Team[2];
    teams[0] = createTeam("1", 0);
    teams[1] = createTeam("2", 0);
    gameState.setTeams(teams);
    gameState.setCurrentTeam(1);

    gameState.getGrid()[0][3] = "b:2";
    gameState.getGrid()[6][3] = "b:1";

    //set last move
    Move lastMove = new Move();
    lastMove.setPieceId("1");
    lastMove.setTeamId("2");
    lastMove.setNewPosition(new int[]{3, 3});
    gameState.setLastMove(lastMove);

    //set pieces correctly
    Piece p1 = createPiece("1", "1");
    p1.setPosition(new int[]{1, 3});
    PieceDescription p1Desc = createPieceDescriptionNormal();
    p1Desc.setMovement(createMovement(1, 0, 0, 0, 0, 0, 0, 0, false));
    p1.setDescription(p1Desc);
    updatePosition(p1, new int[]{0, 0}, new int[]{1, 3}, gameState);
    Piece p2 = createPiece("2", "1");
    p2.setPosition(new int[]{5, 5});
    updatePosition(p2, new int[]{0, 0}, new int[]{5, 5}, gameState);
    teams[0].setPieces(new Piece[]{p1, p2});

    Piece p3 = createPiece("1", "2");
    p3.setPosition(new int[]{3, 3});
    updatePosition(p3, new int[]{0, 0}, new int[]{3, 3}, gameState);
    teams[1].setPieces(new Piece[]{p3});

    return gameState;
  }

  public GameState createNoMovesLeftState() {
    //create gameState
    GameState gameState = new GameState();
    gameState.setGrid(createEmptyGrid(7));
    Team[] teams = new Team[2];
    teams[0] = createTeam("1", 0);
    teams[1] = createTeam("2", 0);
    gameState.setTeams(teams);
    gameState.setCurrentTeam(1);

    //set last move
    Move lastMove = new Move();
    lastMove.setPieceId("1");
    lastMove.setTeamId("2");
    lastMove.setNewPosition(new int[]{3, 3});
    gameState.setLastMove(lastMove);

    //set pieces correctly
    Piece p1 = createPiece("1", "1");
    p1.setPosition(new int[]{1, 3});
    PieceDescription p1Desc = createPieceDescriptionNormal();
    p1Desc.setMovement(createMovement(0, 0, 0, 0, 0, 0, 0, 0, false));
    p1.setDescription(p1Desc);
    updatePosition(p1, new int[]{0, 0}, new int[]{1, 3}, gameState);
    Piece p2 = createPiece("2", "1");
    PieceDescription p2Desc = createPieceDescriptionNormal();
    p2Desc.setMovement(createMovement(0, 0, 0, 0, 0, 0, 0, 0, false));
    p2.setDescription(p2Desc);
    p2.setPosition(new int[]{5, 5});
    updatePosition(p2, new int[]{0, 0}, new int[]{5, 5}, gameState);
    teams[0].setPieces(new Piece[]{p1, p2});

    Piece p3 = createPiece("1", "2");
    p3.setPosition(new int[]{3, 3});
    PieceDescription p3Desc = createPieceDescriptionNormal();
    p3Desc.setMovement(createMovement(0, 0, 0, 0, 0, 0, 0, 0, false));
    p3.setDescription(p3Desc);
    updatePosition(p3, new int[]{0, 0}, new int[]{3, 3}, gameState);
    teams[1].setPieces(new Piece[]{p3});

    return gameState;
  }

  public GameState createCustomTestGameState(int team, int enemyTeam, String type,
      int[] positionPiece, int[] positionEnemyPiece, boolean canCapture) {
    //create gameState
    GameState gameState = new GameState();
    gameState.setGrid(createEmptyGrid(7));
    Team[] teams = new Team[4];
    teams[0] = createTeam("1", 0);
    teams[1] = createTeam("2", 0);
    teams[2] = createTeam("3", 0);
    teams[3] = createTeam("4", 0);
    gameState.setTeams(teams);
    gameState.setCurrentTeam(team);

    gameState.getGrid()[0][3] = "b:2";
    gameState.getGrid()[6][3] = "b:1";
    gameState.getGrid()[3][0] = "b:3";
    gameState.getGrid()[3][6] = "b:4";

    //set last move
    Move lastMove = new Move();
    lastMove.setPieceId("1");
    lastMove.setTeamId("2");
    lastMove.setNewPosition(new int[]{3, 3});
    gameState.setLastMove(lastMove);

    //set pieces correctly
    Piece p1 = createPiece("1", Integer.toString(team));
    p1.setPosition(positionPiece);
    PieceDescription p1Desc;
    switch (type) {
      case "horizontal":
        p1Desc = createPieceDescriptionNormal();
        p1Desc.setMovement(createMovement(0, 0, 1, 1, 0, 0, 0, 0, false));
        if (!canCapture) {
          p1Desc.setAttackPower(1);
        }
        p1.setDescription(p1Desc);
        break;
      case "vertical":
        p1Desc = createPieceDescriptionNormal();
        p1Desc.setMovement(createMovement(1, 1, 0, 0, 0, 0, 0, 0, false));
        if (!canCapture) {
          p1Desc.setAttackPower(1);
        }
        p1.setDescription(p1Desc);
        break;
      case "diagonal":
        p1Desc = createPieceDescriptionNormal();
        p1Desc.setMovement(createMovement(0, 0, 0, 0, 1, 0, 0, 1, false));
        if (!canCapture) {
          p1Desc.setAttackPower(1);
        }
        p1.setDescription(p1Desc);
        break;
      case "lshaped":
        p1Desc = createPieceDescriptionLshaped();
        if (!canCapture) {
          p1Desc.setAttackPower(1);
        }
        p1.setDescription(p1Desc);
        break;
      default:
        break;
    }
    updatePosition(p1, new int[]{0, 0}, positionPiece, gameState);
    teams[0].setPieces(new Piece[]{p1});

    Piece p3 = createPiece("1", Integer.toString(enemyTeam));
    p3.setPosition(positionEnemyPiece);
    updatePosition(p3, new int[]{0, 0}, positionEnemyPiece, gameState);
    teams[1].setPieces(new Piece[]{p3});

    return gameState;
  }


  private String[][] createEmptyGrid(int size) {
    String[][] grid = new String[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        grid[i][j] = "";
      }
    }
    return grid;
  }

  private static Team createTeam(String teamId, int numPieces) {
    Team team = new Team();
    team.setId(teamId);
    team.setColor("Team Color " + teamId);
    team.setBase(new int[]{3, 3});  // Corner bases
    team.setFlags(1);
    team.setPieces(new Piece[numPieces]);

    for (int i = 0; i < numPieces; i++) {
      team.getPieces()[i] = createPiece("" + (i + 1), teamId);
    }

    return team;
  }

  private static Piece createPiece(String pieceId, String teamId) {
    Piece piece = new Piece();
    piece.setId(pieceId);
    piece.setTeamId(teamId);
    piece.setDescription(createPieceDescriptionNormal());
    piece.setPosition(new int[]{0,
        0});  // Default starting position, should be adjusted based on actual game layout
    return piece;
  }

  private static PieceDescription createPieceDescriptionNormal() {
    PieceDescription description = new PieceDescription();
    description.setAttackPower(5);
    description.setCount(1);
    description.setType("Knight");
    description.setMovement(createMovement(1, 1, 1, 1, 1, 1, 1, 1, false));
    return description;
  }

  private static PieceDescription createPieceDescriptionLshaped() {
    PieceDescription description = new PieceDescription();
    description.setAttackPower(5);
    description.setCount(1);
    description.setType("Knight");
    description.setMovement(createMovement(0, 0, 0, 0, 0, 0, 0, 0, true));
    return description;
  }

  private static Movement createMovement(int up, int down, int left, int right, int upLeft,
      int upRight,
      int downLeft, int downRight, boolean lshaped) {
    Movement movement = new Movement();
    Shape shape = new Shape();
    if (lshaped) {
      shape.setType(ShapeType.lshape);
      movement.setShape(shape);
    }

    Directions directions = new Directions();
    directions.setDown(down);
    directions.setDownLeft(downLeft);
    directions.setDownRight(downRight);
    directions.setLeft(left);
    directions.setRight(right);
    directions.setUp(up);
    directions.setUpLeft(upLeft);
    directions.setUpRight(upRight);
    movement.setDirections(directions);  // Default to 1 in all directions
    return movement;
  }

  private void updatePosition(Piece piece, int[] oldPosition, int[] newPosition,
      GameState gameState) {
    String[][] grid = gameState.getGrid();
    grid[oldPosition[0]][oldPosition[1]] = "";
    grid[newPosition[0]][newPosition[1]] = "p:" + piece.getTeamId() + "_" + piece.getId();
    piece.setPosition(newPosition);

  }

  public static void printGameState(GameState gameStateInp) {
    System.out.println("GameState: ");
    System.out.println("Teamcount: " + gameStateInp.getTeams().length);
    System.out.println("Current GameBoard: ");
    String[][] gameBoard = gameStateInp.getGrid();

    for (String[] strings : gameBoard) {
      for (String string : strings) {
        if (string.isEmpty()) {
          System.out.print(string + "-----\t");
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

  public static void main(String[] args) {
    GameStateFactory factory = new GameStateFactory();
    GameState gameState = factory.createImmediateCaptureWinState(true);
    printGameState(gameState);
  }
}





