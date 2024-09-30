package de.unimannheim.swt.pse.ai.mcts;

import de.unimannheim.swt.pse.server.game.exceptions.GameOver;
import de.unimannheim.swt.pse.server.game.exceptions.InvalidMove;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import de.unimannheim.swt.pse.server.game.state.Piece;
import de.unimannheim.swt.pse.server.game.state.Team;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simulates a game based on the current game state, handling moves and conflicts within the game to
 * produce a simulated outcome.
 *
 * @author ohandsch
 */
public class GameSimulator {

  private GameState gameState;
  private MoveSelector moveSelector;

  private boolean gameOver;
  private int master;
  private int previousTeam;

  private int winner;

  /**
   * Constructs a GameSimulator with a given game state.
   *
   * @param gameState the initial state of the game to be simulated.
   * @author ohandsch
   */
  public GameSimulator(GameState gameState) {
    this.gameState = gameState;
    this.moveSelector = new MoveSelector(gameState);
    this.gameOver = false;
    this.master = gameState.getCurrentTeam();
  }

  /**
   * Simulates the game until it concludes, returning the winning team's ID.
   *
   * @return the ID of the winning team after game simulation.
   * @author ohandsch
   */
  public int simulateGame() {

    //System.out.println("Simulating game...");
    while (!this.gameOver) {
      //System.out.println("game not over");

      //Move move = moveSelector.selectRandomMove();
      //Move move = moveSelector.selectRandomMoveFast();
      Move move = moveSelector.selectRandomMove();

      //System.out.println("move selected");
      if (move == null) {
        //System.out.println("No more moves available");
        this.gameOver = true;
        this.winner = this.previousTeam;
        //System.out.println("Simulated result: " + getResult());

      } else {
      /*System.out.println(
          "Random move selected: " + move.getPieceId() + " to " + move.getNewPosition()[0] + ", "
              + move.getNewPosition()[1]);*/
        makeMove(move);
      }
    }
    //System.out.println("Simulated result after move: " + getResult());
    return getResult();
  }

  /**
   * Retrieves the result of the game simulation.
   *
   * @return the winner's team ID as an integer.
   * @author ohandsch
   */
  private int getResult() {
    return this.winner;
  }

  /**
   * Performs a move within the simulation and updates the game state accordingly.
   *
   * @param move the move to be made during the simulation.
   * @author ohandsch
   */
  public void makeMove(Move move) {
    // check if game is over
    if (this.gameOver) {
      return;
    }

    String[][] grid = this.gameState.getGrid();

    // create reference for piece
    int currentTeam = this.gameState.getCurrentTeam();
    String reference = "p:" + currentTeam + "_" + move.getPieceId();
    //System.out.println("make Move:: reference: " + reference);

    // get piece info
    Piece piece = this.getPiece(move.getPieceId(), currentTeam);

    // get coordinates of move and content of new position
    int[] newPosition = move.getNewPosition();
    //System.out.println("make Move:: new position: " + newPosition[0] + ", " + newPosition[1]);

    int[] currentPosition = piece.getPosition();
    //System.out.println("make Move:: current position: " + currentPosition[0] + ", " + currentPosition[1]);
    String targetContent = grid[newPosition[0]][newPosition[1]];

    if (targetContent.isEmpty()) {
      grid = updatePosition(piece, currentPosition, newPosition);
    } else {
      handleConflict(piece, currentPosition, newPosition, targetContent, reference);
    }

    updateGameStateAfterMove(move, grid);

  }

  /**
   * Updates the game state's grid after a piece is moved or a conflict is resolved.
   *
   * @param piece       the game piece being moved.
   * @param oldPosition the original position of the piece.
   * @param newPosition the new position of the piece after the move.
   * @return the updated grid of the game state.
   * @author ohandsch
   */
  private String[][] updatePosition(Piece piece, int[] oldPosition, int[] newPosition) {
    String[][] grid = gameState.getGrid();
    grid[oldPosition[0]][oldPosition[1]] = "";
    grid[newPosition[0]][newPosition[1]] = "p:" + gameState.getCurrentTeam() + "_" + piece.getId();
    piece.setPosition(newPosition);

    return grid;
  }

  /**
   * Handles conflicts encountered during a move, such as capturing an opponent's piece or landing
   * on an opponent's base.
   *
   * @param piece           the piece making the move.
   * @param currentPosition the current position of the piece.
   * @param newPosition     the intended new position of the piece.
   * @param targetContent   the content of the target grid cell.
   * @param reference       a reference string for the piece making the move.
   * @return the updated grid of the game state after the conflict has been resolved.
   * @author ohandsch
   */
  private String[][] handleConflict(Piece piece, int[] currentPosition, int[] newPosition,
      String targetContent, String reference) {
    String[][] grid = gameState.getGrid();
    if (targetContent.matches("p:[1-4]_[0-9]+")) { // piece
      // get team and piece id
      String[] enemyPieceInfo = targetContent.split("_");
      int team = Integer.parseInt(enemyPieceInfo[0].split(":")[1]);
      int pieceId = Integer.parseInt(enemyPieceInfo[1]);

      // get piece
      Piece enemyPiece = this.getPiece(pieceId + "", team);
      // set position of enemy piece to null
      //enemyPiece.setPosition(null);
      // team already there, pieces are pos are only set null
      for (Team t : gameState.getTeams()) {
        if (Integer.parseInt(t.getId()) != team) {
          continue;
        }
        ArrayList<Piece> pieceList = new ArrayList<>(Arrays.asList(t.getPieces()));
        pieceList.removeIf(p -> p.getId().equals(pieceId + ""));
        t.setPieces(pieceList.toArray(new Piece[0]));

        if (t.getPieces().length == 0) {
          this.gameOver = true;
          this.winner = this.gameState.getCurrentTeam();
        }
      }

      // set piece coordinates
      piece.setPosition(newPosition);

      // update grid
      grid[newPosition[0]][newPosition[1]] = reference;
      grid[currentPosition[0]][currentPosition[1]] = "";


    } else if (targetContent.matches("b:[1-4]")) { // base
      this.gameOver = true;
      this.winner = this.gameState.getCurrentTeam();
    }

    return grid;
  }

  /**
   * Updates the game state after a move is made, including setting the next team and updating the
   * last move made.
   *
   * @param move the move that was made.
   * @param grid the current grid of the game state after the move.
   * @author ohandsch
   */
  private void updateGameStateAfterMove(Move move, String[][] grid) {
    // Set next team, update last move, etc.
    this.gameState.setLastMove(move);
    this.previousTeam = this.gameState.getCurrentTeam();
    this.gameState.setCurrentTeam((gameState.getCurrentTeam() % gameState.getTeams().length) + 1);
    this.gameState.setGrid(grid);
  }

  /**
   * Retrieves a piece based on its ID and team ID.
   *
   * @param pieceId the ID of the piece to retrieve.
   * @param teamId  the ID of the team to which the piece belongs.
   * @return the piece if found, otherwise null.
   * @author ohandsch
   */
  private Piece getPiece(String pieceId, int teamId) {
    for (Piece piece : gameState.getTeams()[teamId - 1].getPieces()) {
      if (piece.getId().equals(pieceId)) {
        return piece;
      }
    }
    return null;
  }


  /**
   * Checks if the game has concluded.
   *
   * @return true if the game is over, otherwise false.
   * @author ohandsch
   */
  public boolean isGameOver() {
    return this.gameOver;
  }

  /**
   * Returns the current game state being simulated.
   *
   * @return the current GameState object.
   * @author ohandsch
   */
  public GameState getGameState() {
    return gameState;
  }

  // Exception classes for game simulation
  public static class GameOver extends Exception {

    public GameOver() {
      super("The game is over.");
    }
  }

  public static class InvalidMove extends Exception {

    public InvalidMove() {
      super("The move is invalid.");
    }
  }
}


