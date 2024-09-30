package de.unimannheim.swt.pse.ai.MCTS;

import static org.junit.jupiter.api.Assertions.*;

import de.unimannheim.swt.pse.ai.mcts.GameSimulator;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.ai.MCTS.Helper.GameStateFactory;

import de.unimannheim.swt.pse.server.game.state.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameSimulatorTest {

  private GameSimulator simulator;
  private final GameStateFactory gameStateFactory = new GameStateFactory();

  @BeforeEach
  public void setUp() {
    // Mock GameState or use a real instance depending on the need
    GameState initialState = gameStateFactory.createSampleGameState();  // Assume this sets up a basic game state
    simulator = new GameSimulator(initialState);
  }

  @Test
  public void testSimulateGame_WinnerDetermined() {
    // Setup the specific game state for winning condition
    GameState winState = gameStateFactory.createImmediateCaptureWinState(true);
    simulator = new GameSimulator(winState);
    int winnerId = simulator.simulateGame();
    assertTrue(winnerId > 0, "Should return a positive winner ID");
  }

  @Test
  public void testSimulateGame_NoMovesLeft() {
    GameState noMoveState = gameStateFactory.createNoMovesLeftState();
    simulator = new GameSimulator(noMoveState);
    simulator.simulateGame();
    assertTrue(simulator.isGameOver(),
        "Game should end with a game over exception when no moves are left.");
  }

  @Test
  public void testMakeMove_ValidMoveExecution() {
    Move validMove = new Move();
    validMove.setPieceId("1");
    validMove.setNewPosition(new int[]{3, 3});
    simulator.makeMove(validMove);
    assertEquals("p:1_1", simulator.getGameState().getGrid()[3][3],
        "Piece should move to the new position");
  }

  @Test
  public void testHandleConflict_PieceCapture() {
    GameState captureState = gameStateFactory.createImmediateCaptureWinState(false);
    simulator = new GameSimulator(captureState);
    Move captureMove = new Move();
    captureMove.setPieceId("1");
    captureMove.setNewPosition(new int[]{3, 3});  // Assuming this move captures an opponent piece
    simulator.makeMove(captureMove);
    assertEquals("p:2_1", simulator.getGameState().getGrid()[3][3],
        "Opponent piece should be captured and removed from the game state");
  }

  @Test
  public void testHandleConflict_BaseCaptureEndsGame() {
    GameState baseCaptureState = gameStateFactory.createBaseCaptureWinState();
    simulator = new GameSimulator(baseCaptureState);
    Move baseCaptureMove = new Move();  // Specify the base position
    baseCaptureMove.setPieceId("1");
    baseCaptureMove.setNewPosition(new int[]{0, 3});  // Assuming this move captures the base
    simulator.makeMove(baseCaptureMove);
    assertTrue(simulator.isGameOver(), "Capturing a base should end the game");
  }

  @Test
  public void testUpdatePosition_MovesPiece() {
    Move validMove = new Move();
    validMove.setPieceId("2");
    validMove.setNewPosition(new int[]{4, 5});
    simulator.makeMove(validMove);
    assertEquals("p:1_2", simulator.getGameState().getGrid()[4][5],
        "Grid should update to reflect the new piece position");
  }

  @Test
  public void testUpdateGameStateAfterMove() {
    Move validMove = new Move();
    validMove.setPieceId("1");
    validMove.setNewPosition(new int[]{2, 3});
    simulator.makeMove(validMove);
    assertEquals(2, simulator.getGameState().getCurrentTeam(),
        "Next team should be set after a move");
  }

  @Test
  public void testGetResult_ReturnsCorrectWinner() {
    GameState winState = gameStateFactory.createImmediateCaptureWinState(true);
    simulator = new GameSimulator(winState);
    int winner = simulator.simulateGame();
    assertEquals(1, winner, "Should return the correct winner's team ID");
  }

  @Test
  public void testIsGameOver() {
    GameState winState = gameStateFactory.createImmediateCaptureWinState(true);
    simulator = new GameSimulator(winState);
    assertFalse(simulator.isGameOver(), "Game should not be over initially");
    Move moveLeadingToWin = new Move();  // Assuming this move leads to a win
    moveLeadingToWin.setPieceId("1");
    moveLeadingToWin.setNewPosition(new int[]{2, 3});
    simulator.makeMove(moveLeadingToWin);
    assertTrue(simulator.isGameOver(), "Game should be over after the winning move");
  }
}

