package de.unimannheim.swt.pse.server.database.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a record of a single game played, storing details about the game, the opponent, and
 * the outcome. This class is designed to facilitate easy storage and retrieval from a Firebase
 * database by providing a method to convert game details into a map format suitable for Firebase
 * operations.
 */
public class GameRecord {

  /**
   * The unique identifier of the game.
   */
  private String gameId;
  /**
   * The unique identifier of the opponent.
   */
  private String opponentId;
  /**
   * A boolean indicating whether the user won the game.
   */
  private boolean userWon;

  /**
   * Constructs a new GameRecord with identifiers for the game and the opponent, and the game
   * outcome. This constructor initializes the record with the provided values.
   *
   * @param gameId     the unique identifier for the game.
   * @param opponentId the unique identifier for the opponent in this game.
   * @param userWon    true if the user won the game, false otherwise.
   * @author ohandsch
   */
  public GameRecord(String gameId, String opponentId, boolean userWon) {
    this.gameId = gameId;
    this.opponentId = opponentId;
    this.userWon = userWon;
  }

  /**
   * Gets the game ID of this record.
   *
   * @return the game ID.
   */
  public String getGameId() {
    return gameId;
  }

  /**
   * Sets the game ID for this record.
   *
   * @param gameId the new game ID to be set.
   * @throws IllegalArgumentException if gameId is null or empty.
   * @author ohandsch
   */
  public void setGameId(String gameId) {
    if (gameId == null || gameId.trim().isEmpty()) {
      throw new IllegalArgumentException("Game ID cannot be null or empty.");
    }
    this.gameId = gameId;
  }

  /**
   * Gets the opponent's ID for this game record.
   *
   * @return the opponent's ID.
   */
  public String getOpponentId() {
    return opponentId;
  }

  /**
   * Sets the opponent's ID for this game record.
   *
   * @param opponentId the new opponent ID to be set.
   * @throws IllegalArgumentException if opponentId is null or empty.
   */
  public void setOpponentId(String opponentId) {
    if (opponentId == null || opponentId.trim().isEmpty()) {
      throw new IllegalArgumentException("Opponent ID cannot be null or empty.");
    }
    this.opponentId = opponentId;
  }

  /**
   * Returns whether the user won this game.
   *
   * @return true if the user won, false otherwise.
   */
  public boolean isUserWon() {
    return userWon;
  }

  /**
   * Sets whether the user won this game.
   *
   * @param userWon true if the user won, false if the user lost.
   */
  public void setUserWon(boolean userWon) {
    this.userWon = userWon;
  }

  /**
   * Converts this GameRecord into a map format for Firebase storage. This method packages the game
   * ID, opponent ID, and game outcome into a map, making it suitable for uploading to Firebase.
   *
   * @return A map containing the game record attributes suitable for Firebase operations.
   */
  public Map<String, Object> toMap() {
    Map<String, Object> recordMap = new HashMap<>();
    recordMap.put("gameId", gameId);
    recordMap.put("opponentId", opponentId);
    recordMap.put("userWon", userWon);
    return recordMap;
  }
}
