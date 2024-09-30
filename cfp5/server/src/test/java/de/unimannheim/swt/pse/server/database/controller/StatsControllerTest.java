package de.unimannheim.swt.pse.server.database.controller;

import static org.junit.jupiter.api.Assertions.*;

import de.unimannheim.swt.pse.server.database.DatabaseInitializer;
import de.unimannheim.swt.pse.server.database.dao.StatsDAO;
import de.unimannheim.swt.pse.server.database.model.StatsModel;
import de.unimannheim.swt.pse.server.database.service.StatsService;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StatsControllerTest {

  private StatsController statsController;

  @BeforeAll
  public static void setupClass() {
    try {
      new DatabaseInitializer().initialize(); // Initialize Firebase
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @BeforeEach
  public void setup() {
    StatsDAO statsDAO = new StatsDAO();
    StatsService statsService = new StatsService(statsDAO);
    statsController = new StatsController(statsService);
  }

  @AfterEach
  public void tearDown() throws ExecutionException, InterruptedException {
    // Clean up test data after each test
    CompletableFuture<Void> deleteFuture1 = statsController.updateStats(
        new StatsModel("testUser1"));
    deleteFuture1.get();
    CompletableFuture<Void> deleteFuture2 = statsController.updateStats(
        new StatsModel("testUser2"));
    deleteFuture2.get();
    CompletableFuture<Void> deleteFuture3 = statsController.updateStats(
        new StatsModel("testUser3"));
    deleteFuture3.get();
  }

  @Test
  public void testGetStats_ExistingUser_ReturnsStats()
      throws ExecutionException, InterruptedException {
    StatsModel stats = new StatsModel("testUser1");
    stats.setGamesWon(5);
    stats.setGamesLost(3);

    CompletableFuture<Void> createFuture = statsController.updateStats(stats);
    createFuture.get();

    CompletableFuture<StatsModel> getFuture = statsController.getStats("testUser1");
    StatsModel retrievedStats = getFuture.get();

    assertEquals("testUser1", retrievedStats.getUserId());
    assertEquals(5, retrievedStats.getGamesWon());
    assertEquals(3, retrievedStats.getGamesLost());
  }

  @Test
  public void testGetStats_NullUserId_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> statsController.getStats(null));
  }

  @Test
  public void testUpdateStats_ValidStats_Success() throws ExecutionException, InterruptedException {
    StatsModel stats = new StatsModel("testUser2");
    stats.setGamesWon(10);
    stats.setGamesLost(5);

    CompletableFuture<Void> updateFuture = statsController.updateStats(stats);
    updateFuture.get();

    CompletableFuture<StatsModel> getFuture = statsController.getStats("testUser2");
    StatsModel retrievedStats = getFuture.get();

    assertEquals("testUser2", retrievedStats.getUserId());
    assertEquals(10, retrievedStats.getGamesWon());
    assertEquals(5, retrievedStats.getGamesLost());
  }

  @Test
  public void testUpdateStats_NullStats_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> statsController.updateStats(null));
  }

  @Test
  public void testUpdateStats_InvalidStats_ThrowsException() {
    StatsModel invalidStats = new StatsModel(null);
    assertThrows(NullPointerException.class, () -> statsController.updateStats(invalidStats));
  }

  @Test
  public void testGetLeaderboard_ValidTopN_ReturnsLeaderboard()
      throws ExecutionException, InterruptedException {
    StatsModel stats1 = new StatsModel("testUser1");
    stats1.setGamesWon(1000);
    StatsModel stats2 = new StatsModel("testUser2");
    stats2.setGamesWon(500);
    StatsModel stats3 = new StatsModel("testUser3");
    stats3.setGamesWon(800);

    CompletableFuture<Void> createFuture1 = statsController.updateStats(stats1);
    CompletableFuture<Void> createFuture2 = statsController.updateStats(stats2);
    CompletableFuture<Void> createFuture3 = statsController.updateStats(stats3);
    createFuture1.get();
    createFuture2.get();
    createFuture3.get();

    CompletableFuture<List<StatsModel>> leaderboardFuture = statsController.getLeaderboard(2);
    List<StatsModel> leaderboard = leaderboardFuture.get();

    assertEquals(2, leaderboard.size());
    assertEquals("testUser1", leaderboard.get(0).getUserId());
    assertEquals(1000, leaderboard.get(0).getGamesWon());
  }

  @Test
  public void testGetLeaderboard_NegativeTopN_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> statsController.getLeaderboard(-1));
  }

  @Test
  public void testGetLeaderboard_ZeroTopN_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> statsController.getLeaderboard(0));
  }

  @Test
  public void testUpdateLeaderboard_ExistingUser_Success()
      throws ExecutionException, InterruptedException {
    StatsModel stats = new StatsModel("testUser1");
    stats.setGamesWon(5);

    CompletableFuture<Void> createFuture = statsController.updateStats(stats);
    createFuture.get();

    CompletableFuture<List<StatsModel>> leaderboardFuture = statsController.getLeaderboard(1);
    List<StatsModel> leaderboard = leaderboardFuture.get();

    assertEquals(1, leaderboard.size());
    assertEquals("testUser1", leaderboard.get(0).getUserId());
    assertEquals(5, leaderboard.get(0).getGamesWon());
  }

  @Test
  public void testUpdateLeaderboard_NonExistingUser_Success()
      throws ExecutionException, InterruptedException {
    CompletableFuture<Void> updateFuture = statsController.updateLeaderboard("testUser2", true);
    updateFuture.get();

    CompletableFuture<List<StatsModel>> leaderboardFuture = statsController.getLeaderboard(1);
    List<StatsModel> leaderboard = leaderboardFuture.get();

    assertEquals(1, leaderboard.size());
  }

  @Test
  public void testUpdateLeaderboard_NullUserId_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> statsController.updateLeaderboard(null, true));
  }
}