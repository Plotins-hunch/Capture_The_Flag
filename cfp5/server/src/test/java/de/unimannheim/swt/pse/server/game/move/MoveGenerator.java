package de.unimannheim.swt.pse.server.game.move;

import de.unimannheim.swt.pse.server.game.state.Move;

public class MoveGenerator {

  /**
   * Generates a move object
   * @author ldornied
   * @param teamId team id of piece
   * @param pieceId id of piece
   * @param newPosition new position of move
   * @return Move object
   */
    public Move generateMove(String teamId, String pieceId, int[] newPosition) {
      Move newMove = new Move();
      newMove.setTeamId(teamId);
      newMove.setPieceId(pieceId);
      newMove.setNewPosition(newPosition);
      return newMove;
    }
}
