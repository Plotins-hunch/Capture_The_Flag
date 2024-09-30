package de.unimannheim.swt.pse.ai.MCTS;

import static org.junit.jupiter.api.Assertions.*;

import de.unimannheim.swt.pse.ai.MCTS.Helper.GameStateFactory;
import de.unimannheim.swt.pse.ai.mcts.MCTS;
import de.unimannheim.swt.pse.ai.mcts.MCTS.Pair;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MCTSTest {

  private MCTS mcts;
  private GameStateFactory factory;
  private GameState initialState;


  @BeforeEach
  void setUp() {
    factory = new GameStateFactory();
    initialState = factory.createBaseCaptureWinState();
    mcts = new MCTS(initialState);
  }

  @Test
  void testConstructor_InitializesRootCorrectly() {
    assertNotNull(mcts);
    assertEquals(1, mcts.getRoot().getState().getCurrentTeam(),
        "Bot team ID should be initialized correctly.");
  }

  @Test
  void testSearch_ExecutesWithinTimeLimit() {
    long startTime = System.currentTimeMillis();
    Move resultMove = mcts.search(1000); //1000 milliseconds
    long endTime = System.currentTimeMillis();

    assertNotNull(resultMove, "Should return a move.");
    assertTrue(endTime - startTime < 1500, "Should finish within roughly 1000 milliseconds.");
  }

  @Test
  void testSearch_ReturnsBestMove() {
    Move resultMove = mcts.search(500);

    assertNotNull(resultMove, "Should return the best move.");
    assertEquals(resultMove.getNewPosition()[0], 0, "Should return the best move.");
    assertEquals(resultMove.getNewPosition()[1], 3, "Should return the best move.");
  }

  @Test
  void testMakeMove_ValidMoveExecution() {
    Move move = new Move();
    move.setPieceId("2");
    move.setTeamId("1");
    move.setNewPosition(new int[]{4, 5}); // Assuming this is a valid move position
    Pair<GameState, Boolean> result = mcts.simulateMove(mcts.getRoot().getState(), move);
    GameState newState = result.first();

    String[][] grid = newState.getGrid();
    assertEquals("p:1_2", grid[4][5], "Piece should be moved to the new position.");
  }

  @Test
  void testHandleConflict_PieceCapture() {
    GameState captureState = factory.createImmediateCaptureWinState(false);
    mcts = new MCTS(captureState);
    Move captureMove = new Move();
    captureMove.setPieceId("1");
    captureMove.setNewPosition(new int[]{3, 3});  // Assuming this move captures an opponent piece
    Pair<GameState, Boolean> result = mcts.simulateMove(mcts.getRoot().getState(), captureMove);
    GameState newState = result.first();
    assertEquals("p:2_1", newState.getGrid()[3][3],
        "Opponent piece should be captured and removed from the game state");

  }

  @Test
  void testHandleConflict_BaseCaptureEndsGame() {
    GameState baseCaptureState = factory.createBaseCaptureWinState();
    mcts = new MCTS(baseCaptureState);
    Move baseCaptureMove = new Move();  // Specify the base position
    baseCaptureMove.setPieceId("1");
    baseCaptureMove.setNewPosition(new int[]{0, 3});  // Assuming this move captures the base
    Pair<GameState, Boolean> result = mcts.simulateMove(mcts.getRoot().getState(), baseCaptureMove);
    boolean gameOver = result.second();
    assertTrue(gameOver, "Capturing a base should end the game");
  }

  @Test
  void testUpdatePosition_MovesPiece() {
    Move move = new Move();
    move.setPieceId("2");
    move.setTeamId("1");
    move.setNewPosition(new int[]{4, 5});

    Pair<GameState, Boolean> result = mcts.simulateMove(mcts.getRoot().getState(), move);
    GameState newState = result.first();

    String[][] grid = newState.getGrid();
    assertEquals("", grid[5][5], "Original position should be empty after the move.");
    assertEquals("p:1_2", grid[4][5], "New position should contain the moved piece.");
  }

  @Test
  void testUpdateGameStateAfterMove() {
    Move move = new Move();
    move.setPieceId("1");
    move.setTeamId("1");
    move.setNewPosition(new int[]{0, 3}); // Position to capture opponent's base

    Pair<GameState, Boolean> result = mcts.simulateMove(mcts.getRoot().getState(), move);
    GameState newState = result.first();

    assertEquals(move, newState.getLastMove(),
        "The last move should be updated correctly in the game state.");
    assertEquals(2, newState.getCurrentTeam(),
        "The next team should be set correctly after the move.");
  }
}

