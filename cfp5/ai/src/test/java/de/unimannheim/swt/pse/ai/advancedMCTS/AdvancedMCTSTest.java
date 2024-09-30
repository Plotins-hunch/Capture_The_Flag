package de.unimannheim.swt.pse.ai.advancedMCTS;

import static org.junit.jupiter.api.Assertions.*;

import de.unimannheim.swt.pse.ai.MCTS.Helper.GameStateFactory;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdvancedMCTSTest {

  private AdvancedMCTS mcts;
  private GameStateFactory factory;
  private GameState initialState;

  @BeforeEach
  void setUp() {
    factory = new GameStateFactory();
    initialState = factory.createBaseCaptureWinState();
    mcts = new AdvancedMCTS(initialState);
  }

  @Test
  void testConstructor_InitializesRootCorrectly() {
    assertNotNull(mcts);
    assertEquals(1, mcts.getRoot().getState().getCurrentTeam(),
        "Bot team ID should be initialized correctly.");
  }

  @Test
  void testParallelSearch_ExecutesWithinTimeLimit() {
    long startTime = System.currentTimeMillis();
    Move resultMove = mcts.parallelSearch(4, 1000); // 4 threads, 1000 milliseconds
    long endTime = System.currentTimeMillis();

    assertNotNull(resultMove, "Should return a move.");
    assertTrue(endTime - startTime < 1500, "Should finish within roughly 1000 milliseconds.");
  }

  @Test
  void testParallelSearch_ReturnsBestMove() {
    Move resultMove = mcts.parallelSearch(1, 500); // Using 1 thread for more controlled behavior

    assertNotNull(resultMove, "Should return the best move.");
    assertEquals(resultMove.getNewPosition()[0], 0, "Should return the best move.");
    assertEquals(resultMove.getNewPosition()[1], 3, "Should return the best move.");
  }

  @Test
  void testAggregateResults_CombinesResultsCorrectly() {
    // Prepare mock nodes based on the real game state
    List<AdvancedMCTSNode> roots = new ArrayList<>();
    AdvancedMCTSNode node1 = new AdvancedMCTSNode(AdvancedGameStateCopier.deepCopy(initialState));

    AdvancedMCTSNode node2 = new AdvancedMCTSNode(AdvancedGameStateCopier.deepCopy(initialState));

    node1.setVisitCount(10);
    node1.setWinScore(5);
    node1.addChild(node1);
    node2.setVisitCount(20);
    node2.setWinScore(15);
    node2.addChild(node2);

    roots.add(node1);
    roots.add(node2);

    Move result = mcts.aggregateResults(roots);

    assertEquals(30, mcts.getRoot().getVisitCount(), "Total visits should be summed up.");
    assertNotNull(result, "Should return a move.");
  }

  @Test
  void testMakeMove_ValidMoveExecution() {
    Move move = new Move();
    move.setPieceId("2");
    move.setTeamId("1");
    move.setNewPosition(new int[]{4, 5}); // Assuming this is a valid move position
    AdvancedMCTS.Triple<GameState, Boolean, Boolean> result = mcts.simulateMove(
        mcts.getRoot().getState(),
        move);
    GameState newState = result.first();

    String[][] grid = newState.getGrid();
    assertEquals("p:1_2", grid[4][5], "Piece should be moved to the new position.");
  }

  @Test
  void testHandleConflict_PieceCapture() {
    GameState captureState = factory.createImmediateCaptureWinState(false);
    mcts = new AdvancedMCTS(captureState);
    Move captureMove = new Move();
    captureMove.setPieceId("1");
    captureMove.setNewPosition(new int[]{3, 3});  // Assuming this move captures an opponent piece
    AdvancedMCTS.Triple<GameState, Boolean, Boolean> result = mcts.simulateMove(
        mcts.getRoot().getState(),
        captureMove);
    GameState newState = result.first();
    assertEquals("p:2_1", newState.getGrid()[3][3],
        "Opponent piece should be captured and removed from the game state");

  }

  @Test
  void testHandleConflict_BaseCaptureEndsGame() {
    GameState baseCaptureState = factory.createBaseCaptureWinState();
    mcts = new AdvancedMCTS(baseCaptureState);
    Move baseCaptureMove = new Move();  // Specify the base position
    baseCaptureMove.setPieceId("1");
    baseCaptureMove.setNewPosition(new int[]{0, 3});  // Assuming this move captures the base
    AdvancedMCTS.Triple<GameState, Boolean, Boolean> result = mcts.simulateMove(
        mcts.getRoot().getState(),
        baseCaptureMove);
    boolean gameOver = result.second();
    assertTrue(gameOver, "Capturing a base should end the game");
  }

  @Test
  void testUpdatePosition_MovesPiece() {
    Move move = new Move();
    move.setPieceId("2");
    move.setTeamId("1");
    move.setNewPosition(new int[]{4, 5});

    AdvancedMCTS.Triple<GameState, Boolean, Boolean> result = mcts.simulateMove(
        mcts.getRoot().getState(),
        move);
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

    AdvancedMCTS.Triple<GameState, Boolean, Boolean> result = mcts.simulateMove(
        mcts.getRoot().getState(),
        move);
    GameState newState = result.first();

    assertEquals(move, newState.getLastMove(),
        "The last move should be updated correctly in the game state.");
    assertEquals(2, newState.getCurrentTeam(),
        "The next team should be set correctly after the move.");
  }

  @Test
  void testParallelSearch() {
    // Perform a parallel search with a controlled time limit and a manageable number of threads
    Move bestMove = mcts.parallelSearch(2, 1000); // 4 threads, 100 milliseconds

    assertNotNull(bestMove, "A best move should be found during the parallel search.");
    assertEquals("1", bestMove.getPieceId(),
        "The best move should involve the correct piece based on the game state.");
    assertArrayEquals(new int[]{0, 3}, bestMove.getNewPosition(),
        "The best move should target capturing the opponent's base.");
  }

}


