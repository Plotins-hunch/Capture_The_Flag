package de.unimannheim.swt.pse.ai.minimax;

import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import de.unimannheim.swt.pse.server.game.state.Piece;
import de.unimannheim.swt.pse.server.game.state.Team;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Simulates a game based on the current game state, handling moves and conflicts within the game to
 * produce a simulated outcome.
 *
 * @author ohandsch
 */
public class GameSimulator {

  private final GameState gameState;

  private boolean gameOver;

  /**
   * Constructs a GameSimulator with a given game state.
   *
   * @param gameState the initial state of the game to be simulated.
   * @author ohandsch
   */
  public GameSimulator(GameState gameState) {
    this.gameState = gameState;
    this.gameOver = false;
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

    // get piece info
    Piece piece = this.getPiece(move.getPieceId(), currentTeam);

    // get coordinates of move and content of new position
    int[] newPosition = move.getNewPosition();

    assert piece != null;
    int[] currentPosition = piece.getPosition();
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
   * @author ohandsch
   */
  private void handleConflict(Piece piece, int[] currentPosition, int[] newPosition,
      String targetContent, String reference) {
    String[][] grid = gameState.getGrid();
    if (targetContent.matches("p:[1-4]_[0-9]+")) { // piece
      // get team and piece id
      String[] enemyPieceInfo = targetContent.split("_");
      int team = Integer.parseInt(enemyPieceInfo[0].split(":")[1]);
      int pieceId = Integer.parseInt(enemyPieceInfo[1]);

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
        }
      }

      // set piece coordinates
      piece.setPosition(newPosition);

      // update grid
      grid[newPosition[0]][newPosition[1]] = reference;
      grid[currentPosition[0]][currentPosition[1]] = "";


    } else if (targetContent.matches("b:[1-4]")) { // base
      this.gameOver = true;
    }

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

}