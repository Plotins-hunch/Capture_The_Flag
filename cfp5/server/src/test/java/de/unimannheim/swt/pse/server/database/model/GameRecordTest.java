package de.unimannheim.swt.pse.server.database.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameRecordTest {

  @Test
  void testGameRecordConstructor() {
    GameRecord gameRecord = new GameRecord("game1", "opponent1", true);
    assertEquals("game1", gameRecord.getGameId());
    assertEquals("opponent1", gameRecord.getOpponentId());
    assertTrue(gameRecord.isUserWon());
  }

  @Test
  void testSettersAndGetters() {
    GameRecord gameRecord = new GameRecord("game1", "opponent1", true);

    gameRecord.setGameId("game2");
    assertEquals("game2", gameRecord.getGameId());

    gameRecord.setOpponentId("opponent2");
    assertEquals("opponent2", gameRecord.getOpponentId());

    gameRecord.setUserWon(false);
    assertFalse(gameRecord.isUserWon());
  }

  @Test
  void testToMap() {
    GameRecord gameRecord = new GameRecord("game1", "opponent1", true);
    Map<String, Object> mapRepresentation = gameRecord.toMap();

    assertEquals("game1", mapRepresentation.get("gameId"));
    assertEquals("opponent1", mapRepresentation.get("opponentId"));
    assertEquals(true, mapRepresentation.get("userWon"));
  }
}

