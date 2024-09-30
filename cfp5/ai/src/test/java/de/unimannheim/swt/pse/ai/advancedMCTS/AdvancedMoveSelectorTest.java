package de.unimannheim.swt.pse.ai.advancedMCTS;

import static org.junit.jupiter.api.Assertions.*;

import de.unimannheim.swt.pse.ai.MCTS.Helper.GameStateFactory;
import de.unimannheim.swt.pse.server.game.state.*;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class AdvancedMoveSelectorTest {

  private AdvancedMoveSelector moveSelector;
  private GameStateFactory gameStateFactory;

  @BeforeEach
  public void setUp() {
    gameStateFactory = new GameStateFactory();
  }

  @Test
  public void testGetAllPossibleMoves_NoMoves() {
    GameState gameState = gameStateFactory.createNoMovesLeftState();
    moveSelector = new AdvancedMoveSelector(gameState);
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    assertTrue(possibleMoves.isEmpty(), "Expected no possible moves");
  }

  @Test
  public void testGetAllPossibleMoves_MultipleMoves() {
    GameState gameState = gameStateFactory.createSampleGameState();
    moveSelector = new AdvancedMoveSelector(gameState);
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    assertTrue(possibleMoves.size() > 1, "Expected multiple possible moves");
  }

  @Test
  public void testGetAllPossibleMoves_BlockedByPieces() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "horizontal",
        new int[]{3, 3}, new int[]{3, 4}, false);
    moveSelector = new AdvancedMoveSelector(gameState);
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    assertEquals(1, possibleMoves.size(), "Expected no possible moves due to blocking pieces");
  }

  @Test
  public void testGetAllPossibleMoves_BlockedByBoundaries() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "vertical",
        new int[]{0, 3}, new int[]{6, 3}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    assertEquals(1, possibleMoves.size(), "Expected no possible moves due to board boundaries");
  }

  @Test
  public void testGetAllPossibleMoves_DifferentMovementTypes() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "diagonal",
        new int[]{3, 3}, new int[]{5, 5}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    assertFalse(possibleMoves.isEmpty(), "Expected possible moves for diagonal movement");

    gameState = gameStateFactory.createCustomTestGameState(1, 2, "lshaped", new int[]{3, 3},
        new int[]{5, 5}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    possibleMoves = moveSelector.getAllPossibleMoves();
    assertFalse(possibleMoves.isEmpty(), "Expected possible moves for L-shaped movement");
  }

  @Test
  public void testIsValidMove_OutOfBounds() {
    GameState gameState = gameStateFactory.createSampleGameState();
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{-1, 3});
    assertFalse(moveSelector.getAllPossibleMoves().contains(move),
        "Expected move to be invalid due to out of bounds");
  }

  @Test
  public void testIsValidMove_SamePosition() {
    GameState gameState = gameStateFactory.createSampleGameState();
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(piece.getPosition());
    assertFalse(moveSelector.getAllPossibleMoves().contains(move),
        "Expected move to be invalid due to same position");
  }

  @Test
  public void testIsValidMove_BlockedBySameTeam() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "horizontal",
        new int[]{3, 3}, new int[]{3, 4}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{3, 4});
    assertFalse(moveSelector.getAllPossibleMoves().contains(move),
        "Expected move to be invalid due to blocking by same team");
  }

  @Test
  public void testIsValidMove_BlockedByOpponent() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "horizontal",
        new int[]{3, 3}, new int[]{3, 4}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{3, 4});
    assertFalse(moveSelector.getAllPossibleMoves().contains(move),
        "Expected move to be invalid due to blocking by opponent");
  }

  @Test
  public void testIsValidMove_CaptureOpponent() {
    GameState gameState = gameStateFactory.createImmediateCaptureWinState(true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{2, 3});
    move.setTeamId(piece.getTeamId());
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    boolean containsMove = false;
    for (Move m : possibleMoves) {
      if (isMovesEqual(m, move)) {
        containsMove = true;
        break;
      }
    }
    assertTrue(containsMove, "Expected move to be valid for capturing opponent piece");
  }

  @Test
  public void testIsValidMove_InvalidMovement() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "diagonal",
        new int[]{3, 3}, new int[]{5, 5}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{5, 4});
    assertFalse(moveSelector.getAllPossibleMoves().contains(move),
        "Expected move to be invalid due to invalid movement");
  }

  @Test
  public void testIsPathClear_ClearPath() {
    GameState gameState = gameStateFactory.createSampleGameState();
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{1, 4});
    move.setTeamId(piece.getTeamId());
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    boolean containsMove = false;
    for (Move m : possibleMoves) {
      if (isMovesEqual(m, move)) {
        containsMove = true;
        break;
      }
    }
    assertTrue(containsMove, "Expected path to be clear for the move");
  }

  @Test
  public void testIsPathClear_BlockedBySameTeam() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "horizontal",
        new int[]{3, 3}, new int[]{3, 5}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{3, 6});
    assertFalse(moveSelector.getAllPossibleMoves().contains(move),
        "Expected path to be blocked by same team piece");
  }

  @Test
  public void testIsPathClear_BlockedByOpponent() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "horizontal",
        new int[]{3, 3}, new int[]{3, 5}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{3, 6});
    assertFalse(moveSelector.getAllPossibleMoves().contains(move),
        "Expected path to be blocked by opponent piece");
  }

  @Test
  public void testIsPathClear_BlockedAtDestination() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "horizontal",
        new int[]{3, 3}, new int[]{3, 4}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{3, 4});
    assertFalse(moveSelector.getAllPossibleMoves().contains(move),
        "Expected path to be blocked at the destination");
  }

  @Test
  public void testHandlePositionContent_EmptyPosition() {
    GameState gameState = gameStateFactory.createSampleGameState();
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{2, 3});
    move.setTeamId(piece.getTeamId());
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    boolean containsMove = false;
    for (Move m : possibleMoves) {
      if (isMovesEqual(m, move)) {
        containsMove = true;
        break;
      }
    }
    assertTrue(containsMove, "Expected move to empty position to be valid");
  }

  @Test
  public void testHandlePositionContent_OccupiedBySameTeam() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "horizontal",
        new int[]{3, 3}, new int[]{3, 4}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{3, 4});
    assertFalse(moveSelector.getAllPossibleMoves().contains(move),
        "Expected move to position occupied by same team to be invalid");
  }

  @Test
  public void testHandlePositionContent_OccupiedByOpponentLowerPower() {
    GameState gameState = gameStateFactory.createImmediateCaptureWinState(true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{2, 3});
    move.setTeamId(piece.getTeamId());
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    boolean containsMove = false;
    for (Move m : possibleMoves) {
      if (isMovesEqual(m, move)) {
        containsMove = true;
        break;
      }
    }
    assertTrue(containsMove,
        "Expected move to capture opponent piece with lower power to be valid");
  }

  @Test
  public void testHandlePositionContent_OccupiedByOpponentHigherPower() {
    GameState gameState = gameStateFactory.createImmediateCaptureWinState(false);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[1].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{3, 3});
    assertFalse(moveSelector.getAllPossibleMoves().contains(move),
        "Expected move to capture opponent piece with higher power to be invalid");
  }

  @Test
  public void testHandlePositionContent_OccupiedByBlock() {
    GameState gameState = gameStateFactory.createSampleGameState();
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{0, 3});
    assertFalse(moveSelector.getAllPossibleMoves().contains(move),
        "Expected move to position occupied by block to be invalid");
  }

  @Test
  public void testHandlePositionContent_OccupiedByOwnBase() {
    GameState gameState = gameStateFactory.createSampleGameState();
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{6, 3});
    assertFalse(moveSelector.getAllPossibleMoves().contains(move),
        "Expected move to own base to be invalid");
  }

  @Test
  public void testHandlePositionContent_OccupiedByOpponentBase() {
    GameState gameState = gameStateFactory.createBaseCaptureWinState();
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{0, 3});
    move.setTeamId(piece.getTeamId());
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    boolean containsMove = false;
    for (Move m : possibleMoves) {
      if (isMovesEqual(m, move)) {

        containsMove = true;
        break;
      }
    }
    assertTrue(containsMove, "Expected move to capture opponent base to be valid");
  }

  @Test
  public void testValidateMoveDirection_ValidHorizontal() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "horizontal",
        new int[]{3, 3}, new int[]{3, 6}, false);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move expectedMove = new Move();
    expectedMove.setPieceId(piece.getId());
    expectedMove.setNewPosition(new int[]{3, 4});
    expectedMove.setTeamId(piece.getTeamId());
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    boolean containsMove = false;
    for (Move move : possibleMoves) {
      if (isMovesEqual(move, expectedMove)) {
        containsMove = true;
        break;
      }
    }
    assertTrue(containsMove, "Expected valid horizontal move to be allowed");
  }

  @Test
  public void testValidateMoveDirection_ValidVertical() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "vertical",
        new int[]{3, 3}, new int[]{6, 3}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{4, 3});
    move.setTeamId(piece.getTeamId());
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    boolean containsMove = false;
    for (Move m : possibleMoves) {
      if (isMovesEqual(m, move)) {
        containsMove = true;
        break;
      }
    }
    assertTrue(containsMove, "Expected valid vertical move to be allowed");
  }

  @Test
  public void testValidateMoveDirection_ValidDiagonal() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "diagonal",
        new int[]{3, 3}, new int[]{6, 6}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{4, 4});
    move.setTeamId(piece.getTeamId());
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    boolean containsMove = false;
    for (Move m : possibleMoves) {
      if (isMovesEqual(m, move)) {
        containsMove = true;
        break;
      }
    }
    assertTrue(containsMove, "Expected valid diagonal move to be allowed");
  }

  @Test
  public void testValidateMoveDirection_ValidLShaped() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "lshaped",
        new int[]{3, 3}, new int[]{6, 6}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{5, 4});
    move.setTeamId(piece.getTeamId());
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    boolean containsMove = false;
    for (Move m : possibleMoves) {
      if (isMovesEqual(m, move)) {
        containsMove = true;
        break;
      }
    }
    assertTrue(containsMove, "Expected valid L-shaped move to be allowed");
  }

  @Test
  public void testValidateMoveDirection_InvalidExceedRange() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "horizontal",
        new int[]{3, 3}, new int[]{3, 6}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{3, 5});
    assertFalse(moveSelector.getAllPossibleMoves().contains(move),
        "Expected move exceeding allowed range to be invalid");
  }

  @Test
  public void testValidateMoveDirection_InvalidMovementRule() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "horizontal",
        new int[]{3, 3}, new int[]{3, 6}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Piece piece = gameState.getTeams()[0].getPieces()[0];
    Move move = new Move();
    move.setPieceId(piece.getId());
    move.setNewPosition(new int[]{4, 4});
    assertFalse(moveSelector.getAllPossibleMoves().contains(move),
        "Expected move violating movement rules to be invalid");
  }

  @Test
  public void testSelectRandomMove_NoMoves() {
    GameState gameState = gameStateFactory.createNoMovesLeftState();
    moveSelector = new AdvancedMoveSelector(gameState);
    Move move = moveSelector.selectNotSoRandomMoveFast();
    assertNull(move, "Expected no move to be selected when no moves are available");
  }

  @Test
  public void testSelectRandomMove_SingleMove() {
    GameState gameState = gameStateFactory.createCustomTestGameState(1, 2, "horizontal",
        new int[]{3, 3}, new int[]{3, 5}, true);
    moveSelector = new AdvancedMoveSelector(gameState);
    Move move = moveSelector.selectNotSoRandomMoveFast();
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    boolean containsMove = false;
    for (Move m : possibleMoves) {
      if (isMovesEqual(m, move)) {
        containsMove = true;
        break;
      }
    }
    assertNotNull(move, "Expected a move to be selected when a single move is available");
    assertTrue(containsMove, "Expected the selected move to be a possible move");
  }

  @Test
  public void testSelectRandomMove_MultipleMoves() {
    GameState gameState = gameStateFactory.createSampleGameState();
    moveSelector = new AdvancedMoveSelector(gameState);
    Move move = moveSelector.selectNotSoRandomMoveFast();
    List<Move> possibleMoves = moveSelector.getAllPossibleMoves();
    boolean containsMove = false;
    for (Move m : possibleMoves) {
      if (isMovesEqual(m, move)) {
        containsMove = true;
        break;
      }
    }
    assertNotNull(move, "Expected a move to be selected when multiple moves are available");
    assertTrue(containsMove, "Expected the selected move to be a possible move");
  }

  private boolean isMovesEqual(Move move1, Move move2) {
    return move1.getPieceId().equals(move2.getPieceId()) &&
        Arrays.equals(move1.getNewPosition(), move2.getNewPosition()) &&
        move1.getTeamId().equals(move2.getTeamId());
  }
}
