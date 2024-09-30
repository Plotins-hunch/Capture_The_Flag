package de.unimannheim.swt.pse.server.database.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a model for game sessions used within the application. This class includes all
 * necessary attributes to describe a game session, such as session ID, IP address, and fullness
 * status. It facilitates storing and retrieving session data from a Firebase database by providing
 * a method to convert session details into a map format.
 */
public class GameSessionsModel {

  /**
   * The session ID of the game session.
   */
  private String sessionId;
  /**
   * The IP address of the game session.
   */
  private String ip;
  /**
   * The status to show whether the game is full yet
   */
  private boolean isFull;

  /**
   * Constructs a new GameSessionsModel with a session ID, IP address, and fullness status. This
   * constructor initializes the model with the provided values.
   *
   * @param sessionId The unique identifier for the game session.
   * @param ip        The IP address associated with the game session.
   * @param isFull    The fullness status of the game session (true if the session is full, false
   *                  otherwise).
   * @author ohandsch
   */
  public GameSessionsModel(String sessionId, String ip, boolean isFull) {
    this.sessionId = sessionId;
    this.ip = ip;
    this.isFull = isFull;
  }


  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  /**
   * Returns whether the game session is full.
   *
   * @return true if the session is full, false otherwise.
   */
  public boolean isFull() {
    return isFull;
  }

  /**
   * Sets the fullness status of the game session.
   *
   * @param isFull The new fullness status to be set (true if full, false if not).
   */
  public void setFull(boolean isFull) {
    this.isFull = isFull;
  }

  /**
   * Converts this GameSessionsModel into a map format for Firebase storage. This method packages
   * the session ID, IP address, and fullness status into a map, suitable for uploading to
   * Firebase.
   *
   * @return A map containing the session attributes suitable for Firebase operations.
   */
  public Map<String, Object> toMap() {
    Map<String, Object> result = new HashMap<>();
    result.put("sessionId", sessionId);
    result.put("ip", ip);
    result.put("isFull", isFull);
    return result;
  }

  @Override
  public String toString() {
    return "Game hosted by " + ip;
  }
}
