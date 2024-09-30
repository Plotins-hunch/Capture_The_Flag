package de.unimannheim.swt.pse.server.database.controller;

import de.unimannheim.swt.pse.server.database.model.GameSessionsModel;
import de.unimannheim.swt.pse.server.database.service.GameSessionsService;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller class for managing game sessions. This class is the primary interface for managing
 * game sessions in the application. Developers should use this controller to interact with game
 * session functionalities and should avoid using the GameSessionsService directly to maintain
 * proper encapsulation and modular architecture.
 */
public class GameSessionsController {

  /**
   * Service layer object that handles business logic for game session operations. This field is not
   * intended for direct access from outside this controller.
   */
  private final GameSessionsService gameSessionsService;

  /**
   * Constructs a GameSessionsController with the necessary service layer dependency.
   *
   * @param gameSessionsService the service object handling game session logic.
   * @author ohandsch
   */
  public GameSessionsController(GameSessionsService gameSessionsService) {
    this.gameSessionsService = gameSessionsService;
  }

  /**
   * Adds a new game session to the database.
   *
   * @param session the game session to add.
   * @return a CompletableFuture that resolves to the ID of the newly added game session.
   * @author ohhandsch
   */
  public CompletableFuture<String> addGameSession(GameSessionsModel session) {
    return gameSessionsService.addGameSession(session)
        .thenApply(sessionId -> {
          System.out.println("Game session added successfully with ID: " + sessionId);
          return sessionId;
        });
  }

  /**
   * Deletes a game session from the database.
   *
   * @param sessionId the ID of the game session to delete.
   * @return a CompletableFuture that resolves when the game session has been successfully deleted.
   * @author ohandsch
   */
  public CompletableFuture<Void> deleteGameSession(String sessionId) {
    return gameSessionsService.deleteGameSession(sessionId)
        .thenRun(
            () -> System.out.println("Game session deleted successfully with ID: " + sessionId));
  }

  /**
   * Retrieves all current game sessions from the database.
   *
   * @return a CompletableFuture that resolves to a list of all current game sessions.
   * @author ohandsch
   */
  public CompletableFuture<List<GameSessionsModel>> getAllCurrentGameSessions() {
    return gameSessionsService.getAllCurrentGameSessions()
        .thenApply(gameSessions -> {
          System.out.println("Current game sessions retrieved successfully");
          return gameSessions;
        });
  }
  
}

