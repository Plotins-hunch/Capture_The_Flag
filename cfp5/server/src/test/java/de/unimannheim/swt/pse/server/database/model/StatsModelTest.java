package de.unimannheim.swt.pse.server.database.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StatsModelTest {

  @Test
  void testStatsModelInitialization() {
    StatsModel statsModel = new StatsModel("user1");
    assertEquals("user1", statsModel.getUserId());
    assertEquals(0, statsModel.getGamesWon());
    assertEquals(0, statsModel.getGamesLost());
    assertTrue(statsModel.getGameRecords().isEmpty());
  }

  @Test
  void testAddWin() {
    StatsModel statsModel = new StatsModel("user1");
    GameRecord winRecord = new GameRecord("1", "user1", true);
    statsModel.addWin(winRecord);

    assertEquals(1, statsModel.getGamesWon());
    assertEquals(winRecord, statsModel.getGameRecords().get(0));
  }

  @Test
  void testAddLoss() {
    StatsModel statsModel = new StatsModel("user1");
    GameRecord lossRecord = new GameRecord("1", "user1", true);
    statsModel.addLoss(lossRecord);

    assertEquals(1, statsModel.getGamesLost());
    assertEquals(lossRecord, statsModel.getGameRecords().get(0));
  }

  @Test
  void testSettersAndGetters() {
    StatsModel statsModel = new StatsModel("user1");
    statsModel.setUserId("user2");
    assertEquals("user2", statsModel.getUserId());

    statsModel.setGamesWon(5);
    assertEquals(5, statsModel.getGamesWon());

    statsModel.setGamesLost(3);
    assertEquals(3, statsModel.getGamesLost());

    List<GameRecord> records = new ArrayList<>();
    records.add(new GameRecord("1", "user1", true)); //
    statsModel.setGameRecords(records);
    assertEquals(records, statsModel.getGameRecords());
  }

  @Test
  void testToMap() {
    StatsModel statsModel = new StatsModel("user1");
    GameRecord record = new GameRecord("1", "user1", true);
    statsModel.addWin(record);

    Map<String, Object> mapRepresentation = statsModel.toMap();

    assertEquals("user1", mapRepresentation.get("userId"));
    assertEquals(1, mapRepresentation.get("gamesWon"));
    assertEquals(0, mapRepresentation.get("gamesLost"));
    assertNotNull(mapRepresentation.get("gameRecords"));

    // Assuming GameRecord.toMap is correctly implemented
    List<Map<String, Object>> recordsList = (List<Map<String, Object>>) mapRepresentation.get(
        "gameRecords");
    assertFalse(recordsList.isEmpty());
  }
}

