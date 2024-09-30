package de.unimannheim.swt.pse.ai.minimax;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import de.unimannheim.swt.pse.ai.minimax.Helper.GameStateFactory;
import de.unimannheim.swt.pse.server.game.map.Directions;
import de.unimannheim.swt.pse.server.game.map.Movement;
import de.unimannheim.swt.pse.server.game.map.PieceDescription;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import de.unimannheim.swt.pse.server.game.state.Piece;
import de.unimannheim.swt.pse.server.game.state.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    //assertPieceDescriptionEqualsAndIndependent(original.getDescription(), copied.getDescription());
  }

  private void assertPieceDescriptionEqualsAndIndependent(PieceDescription original,
      PieceDescription copied) {
    assertEquals(original.getAttackPower(), copied.getAttackPower(),
        "Attack powers should be equal");
    assertEquals(original.getCount(), copied.getCount(), "Counts should be equal");
    assertEquals(original.getType(), copied.getType(), "Types should be equal");
    assertNotSame(original.getMovement(), copied.getMovement(), "Movements should not be the same");
    assertMovementEqualsAndIndependent(original.getMovement(), copied.getMovement());
  }

  private void assertMovementEqualsAndIndependent(Movement original, Movement copied) {
    assertNotSame(original.getShape(), copied.getShape(), "Shapes should not be the same");
    assertEquals(original.getShape().getType(), copied.getShape().getType(),
        "Shape types should be equal");
    assertDirectionsEqualsAndIndependent(original.getDirections(), copied.getDirections());
  }

  private void assertDirectionsEqualsAndIndependent(Directions original, Directions copied) {
    assertEquals(original.getUp(), copied.getUp(), "Up directions should be equal");
    assertEquals(original.getDown(), copied.getDown(), "Down directions should be equal");
    assertEquals(original.getLeft(), copied.getLeft(), "Left directions should be equal");
    assertEquals(original.getRight(), copied.getRight(), "Right directions should be equal");
    assertNotSame(original, copied);
  }

  private void assertMoveEqualsAndIndependent(Move original, Move copied) {
    assertEquals(original.getPieceId(), copied.getPieceId(), "Piece IDs should be equal");
    assertEquals(original.getTeamId(), copied.getTeamId(), "Team IDs should be equal");
    assertArrayEquals(original.getNewPosition(), copied.getNewPosition(),
        "New positions should be equal");
  }
}

