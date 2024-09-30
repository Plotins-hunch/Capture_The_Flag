package de.unimannheim.swt.pse.server.database.service;

import de.unimannheim.swt.pse.server.database.dao.GameSessionsDAO;
import de.unimannheim.swt.pse.server.database.model.GameSessionsModel;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.StringUtils;

/**
 * Service class for managing game session operations. This class provides a high-level API over the
 * {@link GameSessionsDAO} for performing CRUD operations on game sessions. It is designed to be
 * accessed through a controller to ensure that all game session operations are centralized and
 * consistent throughout the application. Direct usage of this service class outside of its
 * controller is discouraged to maintain a clean architecture and proper separation of concerns.
 */
public class GameSessionsService {

  /**
   * The DAO responsible for direct database interactions for game sessions.
   */
  private final GameSessionsDAO gameSessionsDAO;

  /**
   * Constructs a new game sessions service with the given DAO.
   *
   * @param gameSessionsDAO the DAO object that handles all data access operations related to game
   *                        sessions.
   */
  public GameSessionsService(GameSessionsDAO gameSessionsDAO) {
    this.gameSessionsDAO = gameSessionsDAO;
  }

  /**
   * Adds a new game session to the database.
   *
   * @param session The {@link GameSessionsModel} object containing the session data to be added.
   * @return A CompletableFuture that, when completed, will return the unique ID of the newly
   * created game session.
   * @throws RuntimeException         If an error occurs during the game session addition process.
   * @throws IllegalArgumentException If the game session is null or has an invalid session ID.
   * @author ohandsch
   */
  public CompletableFuture<String> addGameSession(GameSessionsModel session) {
    if (session == null || StringUtils.isBlank(session.getSessionId()) || StringUtils.isBlank(
        session.getIp())) {
      throw new IllegalArgumentException("Game session cannot be null");
    }
    return gameSessionsDAO.addGameSession(session)
        .exceptionally(ex -> {
          System.err.println("Error adding game session: " + ex.getMessage());
          throw new RuntimeException("Failed to add game session", ex);
        });
  }

  /**
   * Deletes a game session from the database based on its ID.
   *
   * @param sessionId The unique identifier of the game session to delete.
   * @return A CompletableFuture that completes when the game session is successfully deleted from
   * the database.
   * @throws RuntimeException         If an error occurs during the game session deletion process.
   * @throws IllegalArgumentException If the session ID is null or empty.
   * @author ohandsch
   */
  public CompletableFuture<Void> deleteGameSession(String sessionId) {

    if (StringUtils.isBlank(sessionId)) {
      throw new IllegalArgumentException("Session ID cannot be null");
    }
    return gameSessionsDAO.deleteGameSession(sessionId)
        .exceptionally(ex -> {
          System.err.println(
              "Error deleting game session with ID " + sessionId + ": " + ex.getMessage());
          throw new RuntimeException("Failed to delete game session", ex);
        });
  }

  /**
   * Retrieves all current game sessions from the database.
   *
   * @return A CompletableFuture that, when completed, will return a list of all current game
   * sessions.
   * @throws RuntimeException If an error occurs during the game session retrieval process.
   * @author ohandsch
   */
  public CompletableFuture<List<GameSessionsModel>> getAllCurrentGameSessions() {
    return gameSessionsDAO.getAllCurrentGameSessions()
        .exceptionally(ex -> {
          System.err.println("Error retrieving current game sessions: " + ex.getMessage());
          throw new RuntimeException("Failed to retrieve current game sessions", ex);
        });
  }
}

