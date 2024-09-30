package de.unimannheim.swt.pse.ai.minimax;

import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import de.unimannheim.swt.pse.server.game.state.Piece;
import de.unimannheim.swt.pse.server.game.state.Team;

/**
 * Provides functionality to perform deep copies of game state objects, ensuring that modifications
 * to copies do not affect the original objects.
 *
 * @author ohandsch
 */
public class GameStateCopier {

  /**
   * Creates a deep copy of a GameState object.
   *
   * @param original the GameState to be copied.
   * @return a deep copy of the original GameState.
   * @author ohandsch
   */
  public static GameState deepCopy(GameState original) {
    if (original == null) {
      return null;
    }

    GameState copied = new GameState();

    // Deep copy the grid
    String[][] originalGrid = original.getGrid();
    String[][] copiedGrid = new String[originalGrid.length][];
    for (int i = 0; i < originalGrid.length; i++) {
      copiedGrid[i] = originalGrid[i].clone();
    }
    copied.setGrid(copiedGrid);

    // Deep copy the teams
    Team[] originalTeams = original.getTeams();
    Team[] copiedTeams = new Team[originalTeams.length];
    for (int i = 0; i < originalTeams.length; i++) {
      copiedTeams[i] = deepCopyTeam(originalTeams[i]);
    }
    copied.setTeams(copiedTeams);

    // Copy current team index
    copied.setCurrentTeam(original.getCurrentTeam());

    // Deep copy the last move
    copied.setLastMove(deepCopyMove(original.getLastMove()));

    return copied;
  }

  /**
   * Creates a deep copy of a Team object.
   *
   * @param original the Team to be copied.
   * @return a deep copy of the original Team.
   * @author ohandsch
   */
  public static Team deepCopyTeam(Team original) {
    if (original == null) {
      System.out.println("original team is null");
      return null;
    }

    Team copied = new Team();
    copied.setId(original.getId());
    copied.setColor(original.getColor());
    copied.setBase(original.getBase().clone());
    copied.setFlags(original.getFlags());

    Piece[] originalPieces = original.getPieces();
    Piece[] copiedPieces = new Piece[originalPieces.length];
    for (int i = 0; i < originalPieces.length; i++) {
      copiedPieces[i] = deepCopyPiece(originalPieces[i]);
    }
    copied.setPieces(copiedPieces);

    return copied;
  }

  /**
   * Creates a deep copy of a Piece object.
   *
   * @param original the Piece to be copied.
   * @return a deep copy of the original Piece.
   * @author ohandsch
   */
  public static Piece deepCopyPiece(Piece original) {
    if (original == null) {
      System.out.println("piece is null");
      return null;
    }

    Piece copied = new Piece();
    copied.setId(original.getId());
    copied.setTeamId(original.getTeamId());
    copied.setDescription(original.getDescription());
    if (original.getPosition() == null) {
      System.out.println("position is null");
    }
    copied.setPosition(
        original.getPosition().clone());
    return copied;
  }


  /**
   * Deep copies a Move object.
   *
   * @param original the Move to copy.
   * @return a deep copy of the original Move or null if the original is null.
   * @author ohandsch
   */
  private static Move deepCopyMove(Move original) {
    if (original == null) {
      return null;
    }

    Move copied = new Move();
    copied.setPieceId(original.getPieceId());
    copied.setNewPosition(original.getNewPosition().clone()); // Clone the position array
    copied.setTeamId(original.getTeamId());

    return copied;
  }
}

