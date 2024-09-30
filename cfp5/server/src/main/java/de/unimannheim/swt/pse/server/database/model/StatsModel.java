package de.unimannheim.swt.pse.server.database.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a statistics model for a user in the application, including records of games won,
 * lost, and individual game details. This class is used to encapsulate all game-related statistics
 * and provides methods to update these statistics accordingly. All statistic data can be converted
 * to a Map format suitable for storage and manipulation in a Firebase database.
 */
public class StatsModel {

  /**
   * The user's identifier to whom these statistics belong.
   */
  private String userId;
  /**
   * The total number of games the user has won.
   */
  private int gamesWon;
  /**
   * The total number of games the user has lost.
   */
  private int gamesLost;
  /**
   * A list of GameRecord objects, each representing a record of an individual game played.
   */
  private List<GameRecord> gameRecords;

  /**
   * Constructs a new StatsModel with the provided user ID. Initializes games won and lost to zero
   * and prepares an empty list for game records.
   *
   * @param userId the unique identifier of the user.
   * @author ohandsch
   */
  public StatsModel(String userId) {
    this.userId = userId;
    this.gamesWon = 0;
    this.gamesLost = 0;
    this.gameRecords = new ArrayList<>();
  }

  /**
   * Adds a win record to this user's statistics and stores the associated game record.
   *
   * @param record the game record to add as a win.
   * @throws NullPointerException if the provided record is null.
   * @author ohandsch
   */
  public void addWin(GameRecord record) {
    this.gamesWon++;
    this.gameRecords.add(record);
  }

  /**
   * Adds a loss record to this user's statistics and stores the associated game record.
   *
   * @param record the game record to add as a loss.
   * @throws NullPointerException if the provided record is null.
   */
  public void addLoss(GameRecord record) {
    this.gamesLost++;
    this.gameRecords.add(record);
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public int getGamesWon() {
    return gamesWon;
  }

  public void setGamesWon(int gamesWon) {
    this.gamesWon = gamesWon;
  }

  public int getGamesLost() {
    return gamesLost;
  }

  public void setGamesLost(int gamesLost) {
    this.gamesLost = gamesLost;
  }

  public List<GameRecord> getGameRecords() {
    return gameRecords;
  }

  public void setGameRecords(List<GameRecord> gameRecords) {
    this.gameRecords = gameRecords;
  }

  /**
   * Converts this StatsModel to a Map object for storage in a Firebase database.
   *
   * @return a Map representation of this StatsModel.
   */
  public Map<String, Object> toMap() {
    Map<String, Object> result = new HashMap<>();
    result.put("userId", userId);
    result.put("gamesWon", gamesWon);
    result.put("gamesLost", gamesLost);
    // Convert each GameRecord to a Map
    List<Map<String, Object>> records = new ArrayList<>();
    if (gameRecords != null) {
      for (GameRecord record : gameRecords) {
        records.add(record.toMap());
      }
    }
    result.put("gameRecords", records);

    return result;
  }
}
