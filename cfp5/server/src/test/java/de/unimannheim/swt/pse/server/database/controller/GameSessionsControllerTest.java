package de.unimannheim.swt.pse.server.database.controller;

import static org.junit.jupiter.api.Assertions.*;

import de.unimannheim.swt.pse.server.database.DatabaseInitializer;
import de.unimannheim.swt.pse.server.database.dao.GameSessionsDAO;
import de.unimannheim.swt.pse.server.database.model.GameSessionsModel;
import de.unimannheim.swt.pse.server.database.service.GameSessionsService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameSessionsControllerTest {

  private GameSessionsController gameSessionsController;

  @BeforeAll
  public static void setupClass() {
    try {
      new DatabaseInitializer().initialize();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @BeforeEach
  public void setup() {
    GameSessionsDAO gameSessionsDAO = new GameSessionsDAO();
    GameSessionsService gameSessionsService = new GameSessionsService(gameSessionsDAO);
    gameSessionsController = new GameSessionsController(gameSessionsService);
  }

  @Test
  public void testAddGameSession_ValidSession_Success()
      throws ExecutionException, InterruptedException {
    GameSessionsModel session = new GameSessionsModel("session1", "1.2.3.4", false);

    CompletableFuture<String> future = gameSessionsController.addGameSession(session);
    String sessionId = future.get();

    assertEquals("session1", sessionId);
  }

  @Test
  public void testAddGameSession_NullSession_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> gameSessionsController.addGameSession(null));
  }

  @Test
  public void testAddGameSession_BlankSessionId_ThrowsException() {
    GameSessionsModel session = new GameSessionsModel("", "1.2.3.4", false);

    assertThrows(IllegalArgumentException.class,
        () -> gameSessionsController.addGameSession(session));
  }

  @Test
  public void testAddGameSession_BlankIP_ThrowsException() {
    GameSessionsModel session = new GameSessionsModel("session1", "", false);

    assertThrows(IllegalArgumentException.class,
        () -> gameSessionsController.addGameSession(session));
  }

  @Test
  public void testDeleteGameSession_ExistingSession_Success()
      throws ExecutionException, InterruptedException {
    GameSessionsModel session = new GameSessionsModel("session2", "1.2.3.5", false);
    gameSessionsController.addGameSession(session).get();

    CompletableFuture<Void> future = gameSessionsController.deleteGameSession("session2");
    future.get();

    CompletableFuture<List<GameSessionsModel>> getAllFuture = gameSessionsController.getAllCurrentGameSessions();
    List<GameSessionsModel> sessions = getAllFuture.get();
    assertFalse(sessions.contains(session));
  }

  @Test
  public void testDeleteGameSession_NullSessionId_ThrowsException() {
    assertThrows(IllegalArgumentException.class,
        () -> gameSessionsController.deleteGameSession(null));
  }

  @Test
  public void testGetAllCurrentGameSessions_ExistingSessions_ReturnsListOfSessions()
      throws ExecutionException, InterruptedException {
    GameSessionsModel session1 = new GameSessionsModel("session3", "1.2.3.6", false);
    GameSessionsModel session2 = new GameSessionsModel("session4", "1.2.3.7", false);
    gameSessionsController.addGameSession(session1).get();
    gameSessionsController.addGameSession(session2).get();

    CompletableFuture<List<GameSessionsModel>> future = gameSessionsController.getAllCurrentGameSessions();
    List<GameSessionsModel> sessions = future.get();
    ArrayList<String> sessionIds = new ArrayList<>();
    for (GameSessionsModel session : sessions) {
      sessionIds.add(session.getSessionId());
    }
    assertTrue(sessionIds.contains(session1.getSessionId()));
    assertTrue(sessionIds.contains(session2.getSessionId()));
  }

  @AfterAll
  public static void tearDownClass() throws ExecutionException, InterruptedException {
    GameSessionsDAO gameSessionsDAO = new GameSessionsDAO();
    GameSessionsService gameSessionsService = new GameSessionsService(gameSessionsDAO);
    GameSessionsController gameSessionsController = new GameSessionsController(gameSessionsService);

    CompletableFuture<Void> deleteFuture1 = gameSessionsController.deleteGameSession("session1");
    deleteFuture1.get();
    CompletableFuture<Void> deleteFuture2 = gameSessionsController.deleteGameSession("session2");
    deleteFuture2.get();
    CompletableFuture<Void> deleteFuture3 = gameSessionsController.deleteGameSession("session3");
    deleteFuture3.get();
    CompletableFuture<Void> deleteFuture4 = gameSessionsController.deleteGameSession("session4");
    deleteFuture4.get();
  }
}