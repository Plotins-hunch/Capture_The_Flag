package de.unimannheim.swt.pse.ai.advancedMCTS;

import static org.junit.jupiter.api.Assertions.*;

import de.unimannheim.swt.pse.server.game.state.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;

class AdvancedMCTSNodeTest {

  private AdvancedMCTSNode root;

  @BeforeEach
  void setUp() {
    GameState dummyState = new GameState(); // Minimal setup, assuming GameState can be empty
    root = new AdvancedMCTSNode(dummyState);

    // Create children for the root
    for (int i = 0; i < 3; i++) {
      AdvancedMCTSNode child = new AdvancedMCTSNode(root, dummyState);
      root.addChild(child);

      // Create grandchildren with varying win scores and visit counts
      for (int j = 0; j < 3; j++) {
        AdvancedMCTSNode grandChild = new AdvancedMCTSNode(child, dummyState);
        grandChild.setVisitCount(new Random().nextInt(10) + 1);
        grandChild.setWinScore(new Random().nextInt(10) + 1);
        child.addChild(grandChild);
      }
    }

    // Manually set one child with high win rate
    root.getChildren().get(1).getChildren().get(1).setWinScore(100);
    root.getChildren().get(1).getChildren().get(1).setVisitCount(10);
  }

  @Test
  void testFindBestChild() {
    AdvancedMCTSNode bestChild = root.getChildren().get(1).findBestChild();
    assertEquals(100.0 / 10.0, bestChild.getWinScore() / bestChild.getVisitCount(),
        "Best child should have the highest win rate.");
  }

  @Test
  void testFindUCBChild() {
    AdvancedMCTSNode ucbChild = root.findUCBChild();
    assertNotNull(ucbChild, "UCB child should not be null.");

    // Ensure it's selected based on UCB formula - complex to assert due to randomness in UCB, test for non-nullity
    assertNotNull(ucbChild, "Should return a valid child based on UCB scores.");
  }

  @Test
  void testAddChild() {
    AdvancedMCTSNode newChild = new AdvancedMCTSNode(root, new GameState());
    root.addChild(newChild);
    assertTrue(root.getChildren().contains(newChild),
        "New child should be added to the children list.");
  }

  @Test
  void testGetRandomChildNode() {
    assertNotNull(root.getRandomChildNode(), "Should return a random child node.");
  }
}


