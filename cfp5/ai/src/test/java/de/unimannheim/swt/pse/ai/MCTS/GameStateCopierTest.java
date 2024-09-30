package de.unimannheim.swt.pse.ai.MCTS;

import static org.junit.jupiter.api.Assertions.*;


import de.unimannheim.swt.pse.ai.MCTS.Helper.GameStateFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import de.unimannheim.swt.pse.ai.mcts.GameStateCopier;
import de.unimannheim.swt.pse.server.game.state.*;

class GameStateCopierTest {

  private GameState original;
  private final GameStateFactory factory = new GameStateFactory();

  @BeforeEach
  void setUp() {
    original = factory.createSampleGameState();
  }

  @Test
  void testDeepCopyGameState() {
    GameState copied = GameStateCopier.deepCopy(original);
    assertNotSame(original, copied,
        "Copied gameState should not be the same as the original gameState");
    assertGameStateEqualsAndIndependent(original, copied);
  }

  @Test
  void testDeepCopyTeam() {
    Team originalTeam = original.getTeams()[0];
    Team copiedTeam = GameStateCopier.deepCopyTeam(originalTeam);
    assertNotSame(originalTeam, copiedTeam,
        "Copied team should not be the same as the original team");
    assert copiedTeam != null;
    assertTeamEqualsAndIndependent(originalTeam, copiedTeam);
  }

  @Test
  void testDeepCopyPiece() {
    Piece originalPiece = original.getTeams()[0].getPieces()[0];
    Piece copiedPiece = GameStateCopier.deepCopyPiece(originalPiece);
    assertNotSame(originalPiece, copiedPiece,
        "Copied piece should not be the same as the original piece");
    assert copiedPiece != null;
    assertPieceEqualsAndIndependent(originalPiece, copiedPiece);
  }

  private void assertGameStateEqualsAndIndependent(GameState original, GameState copied) {
    assertArrayEquals(original.getGrid(), copied.getGrid(), "Grids should be equal");
    assertEquals(original.getCurrentTeam(), copied.getCurrentTeam(),
        "Current teams should be equal");
    assertNotSame(original.getTeams(), copied.getTeams(), "Teams should not be the same");

    for (int i = 0; i < original.getTeams().length; i++) {
      assertTeamEqualsAndIndependent(original.getTeams()[i], copied.getTeams()[i]);
    }
    assertNotSame(original.getLastMove(), copied.getLastMove(),
        "Last moves should not be the same");
    assertMoveEqualsAndIndependent(original.getLastMove(), copied.getLastMove());
  }

  private void assertTeamEqualsAndIndependent(Team original, Team copied) {
    assertEquals(original.getId(), copied.getId(), "Team IDs should be equal");
    assertEquals(original.getColor(), copied.getColor(), "Team colors should be equal");
    assertArrayEquals(original.getBase(), copied.getBase(), "Bases should be equal");
    assertEquals(original.getFlags(), copied.getFlags(), "Flags should be equal");
    assertNotSame(original.getPieces(), copied.getPieces(), "Pieces should not be the same");

    for (int i = 0; i < original.getPieces().length; i++) {
      assertPieceEqualsAndIndependent(original.getPieces()[i], copied.getPieces()[i]);
    }
  }

  private void assertPieceEqualsAndIndependent(Piece original, Piece copied) {
    assertEquals(original.getId(), copied.getId(), "Piece IDs should be equal");
    assertEquals(original.getTeamId(), copied.getTeamId(), "Team IDs should be equal");
    assertArrayEquals(original.getPosition(), copied.getPosition(), "Positions should be equal");
  }

  private void assertMoveEqualsAndIndependent(Move original, Move copied) {
    assertEquals(original.getPieceId(), copied.getPieceId(), "Piece IDs should be equal");
    assertEquals(original.getTeamId(), copied.getTeamId(), "Team IDs should be equal");
    assertArrayEquals(original.getNewPosition(), copied.getNewPosition(),
        "New positions should be equal");
  }
}

