package de.unimannheim.swt.pse.ai.minimax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.unimannheim.swt.pse.ai.minimax.Helper.GameStateFactory;
import de.unimannheim.swt.pse.server.game.state.GameState;
import org.junit.jupiter.api.Test;

public class HeuristicEvaluatorTest {
  private final GameStateFactory factory = new GameStateFactory();

  @Test
  public void testEvaluateOpponentHasNoFlagsLeft() {
  GameState opponentHasNoFlagsLeftState = factory.createOpponentHasNoFlagsWinState(true);

  int value = HeuristicEvaluator.countFlags(opponentHasNoFlagsLeftState, false);

  assertEquals(-100, value, "Should return -100 if opponent has no flags left.");
  }

  @Test
  public void testMyFlagIsBetweenMyNearestPieceAndOpponentsNearestPiece() {
  GameState state = factory.createMyFlagIsBetweenMyNearestPieceAndOpponentsNearestPiece();
  boolean value = HeuristicEvaluator.myFlagBetweenNearestPieces(state);

  assertTrue(value, "Should return true if my flag is between my nearest piece and "
      + "opponent's nearest piece.");
  }

  @Test
  public void testHeuristicEvaluatorEvaluate() {
    GameState sampleGameState = factory.createSampleGameState();
    int value = HeuristicEvaluator.evaluate(sampleGameState, false);

    assertEquals(-38, value, "Should return -38 in this specific state.");
  }

  @Test
  public void testHeuristicEvaluatorEvaluate2() {
    GameState sampleGameState = factory.createSampleGameState2(true);
    int value = HeuristicEvaluator.evaluate(sampleGameState, true);

    assertEquals(9, value, "Should return 9 in this specific state.");
  }
}
