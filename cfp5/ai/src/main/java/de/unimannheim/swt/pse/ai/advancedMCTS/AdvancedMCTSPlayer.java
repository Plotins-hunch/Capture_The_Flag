package de.unimannheim.swt.pse.ai.advancedMCTS;

import de.unimannheim.swt.pse.server.controller.data.GameSessionResponse;
import de.unimannheim.swt.pse.server.controller.data.JoinGameRequest;
import de.unimannheim.swt.pse.server.controller.data.JoinGameResponse;
import de.unimannheim.swt.pse.server.controller.data.MoveRequest;
import de.unimannheim.swt.pse.server.game.exceptions.GameSessionNotFound;
import de.unimannheim.swt.pse.server.game.exceptions.InvalidMove;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class manages a Bot that plays the game using the MCTS algorithm.
 */
public class AdvancedMCTSPlayer {

  private long TIMELIMITMILLIES = 10000;

  private final int NUMTHREADS =
      Runtime.getRuntime().availableProcessors() < 4 ? Runtime.getRuntime().availableProcessors()
          : Runtime.getRuntime().availableProcessors() / 2;

  private final ScheduledExecutorService scheduler;
  private volatile boolean isMyTurn = false;
  private final AtomicBoolean isTurnInProgress = new AtomicBoolean(false);

  private AdvancedRequestHandler requestHandler;

  private String gameSessionId;
  private String teamId;
  private String teamSecret;

  /**
   * Constructs a new MCTSPlayer with specified IP address and game session ID.
   *
   * @param ip            the IP address of the game server.
   * @param gameSessionId the ID of the game session to join.
   * @throws IOException if an I/O error occurs when setting up the player.
   * @author ohandsch
   */
  public AdvancedMCTSPlayer(String ip, String gameSessionId) throws IOException {
    this.gameSessionId = gameSessionId;

    //Setup the request handler as a guest
    String baseUrl = "http://" + ip + ":8888/";
    this.requestHandler = new AdvancedRequestHandler();
    requestHandler.setBaseUrl(baseUrl);

    this.scheduler = Executors.newSingleThreadScheduledExecutor();
  }

  /**
   * Starts the MCTSPlayer and attempts to join a game session. Sets up a scheduled task to manage
   * turn playing.
   *
   * @author ohandsch
   */
  public void start() {
    //Join the game session
    try {
      join(gameSessionId);
      scheduler.scheduleAtFixedRate(this::checkAndPlay, 0, 1, TimeUnit.SECONDS);
    } catch (IOException e) {
      System.err.println("Failed to start MCTSPlayer: " + e.getMessage());
      stopPlaying();
    }
  }


  /**
   * Checks and executes a play turn if it is the player's turn and a turn is not currently in
   * progress.
   *
   * @author ohandsch
   */
  public void checkAndPlay() {
    if (this.requestHandler.getGameSession(this.gameSessionId).isGameOver()) {
      stopPlaying();
    }
    GameState currentState = this.requestHandler.getGameState(this.gameSessionId);

    if (!isTurnInProgress.get() && checkIfMyTurn(currentState)) {
      isTurnInProgress.set(true);
      try {
        playTurn(currentState);
      } finally {
        isTurnInProgress.set(false);
      }
      isMyTurn = false;
    }
  }

  /**
   * Checks if it is currently this player's turn.
   *
   * @return true if it is the player's turn; false otherwise.
   * @author ohandsch
   */
  public boolean checkIfMyTurn(GameState currentState) {
    // Send API request to check if it's player's turn
    this.isMyTurn = currentState.getCurrentTeam() == Integer.parseInt(this.teamId);
    return isMyTurn;
  }

  /**
   * Performs the actions required for a turn, including  actual move calculation using the MCTS
   * algorithm and execution.
   *
   * @author ohandsch
   */
  public void playTurn(GameState currentState) {

    GameSessionResponse gameSessionResponse = requestHandler.getGameSession(this.gameSessionId);
    if (gameSessionResponse.getRemainingMoveTimeInSeconds() < 15
        && gameSessionResponse.getRemainingMoveTimeInSeconds() > 0) {
      int moveTimeInSeconds = gameSessionResponse.getRemainingMoveTimeInSeconds();
      if (moveTimeInSeconds < 1) {
        this.TIMELIMITMILLIES = (moveTimeInSeconds * 1000L) / 2;
      } else {
        this.TIMELIMITMILLIES = (moveTimeInSeconds * 1000L) - 1000L;
      }
    }

    // Logic to calculate the move
    System.out.println("Calculating and making a move");

    // Send the move via the API
    Move bestMove = new AdvancedMCTS(currentState).parallelSearch(NUMTHREADS, TIMELIMITMILLIES);

    System.out.println("actually making move");
    System.out.println("piece: " + bestMove.getPieceId() + " to x: " + bestMove.getNewPosition()[0]
        + " y: " + bestMove.getNewPosition()[1]);
    //actually making move
    MoveRequest moveRequest = new MoveRequest();
    moveRequest.setTeamSecret(this.teamSecret);
    moveRequest.setTeamId(this.teamId);
    moveRequest.setNewPosition(bestMove.getNewPosition());
    moveRequest.setPieceId(bestMove.getPieceId());
    try {
      this.requestHandler.makeMove(this.gameSessionId, moveRequest);
    } catch (InvalidMove e) {
      System.out.println(e.getMessage());
      this.playTurn(currentState);
    } catch (Exception e) {
      System.err.println("Failed to make move: " + e.getMessage());
      this.isTurnInProgress.set(false);
    }
    System.gc();
  }

  /**
   * Stops the MCTS player and terminates any ongoing scheduled tasks.
   *
   * @author ohandsch
   */
  public void stopPlaying() {
    scheduler.shutdown();
    try {
      if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
        scheduler.shutdownNow();
      }
    } catch (InterruptedException ie) {
      scheduler.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Attempts to join a game session using provided team information.
   *
   * @param gameSessionId the unique identifier for the game session.
   * @throws IOException if there is an error in network communication.
   * @author ohandsch
   */
  private void join(String gameSessionId) throws IOException {

    //Send the request to join the game session
    JoinGameRequest joinGameRequest = new JoinGameRequest();
    joinGameRequest.setTeamId("team1");
    try {
      JoinGameResponse response = this.requestHandler.joinGame(gameSessionId, joinGameRequest);
      this.gameSessionId = response.getGameSessionId();
      this.teamId = response.getTeamId();
      this.teamSecret = response.getTeamSecret();
      System.out.println("responseId:" + response.getTeamId());
    } catch (GameSessionNotFound e) {
      System.err.println("Failed to join game session: " + e.getMessage());
      throw new IOException(e);
    }

  }

  /**
   * Starts an MCTS-based bot for a given IP address and game session ID.
   *
   * @param ip            the IP address of the game server.
   * @param gameSessionId the unique identifier for the game session.
   * @author ohandsch
   */
  public static void startMCTSBot(String ip, String gameSessionId) {
    AdvancedMCTSPlayer player;
    try {
      player = new AdvancedMCTSPlayer(ip, gameSessionId);
      player.start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getTeamId() {
    return teamId;
  }

  public AdvancedRequestHandler getRequestHandler() {
    return requestHandler;
  }

  public void setRequestHandler(AdvancedRequestHandler requestHandler) {
    this.requestHandler = requestHandler;
  }

  public void setTeamId(String teamId) {
    this.teamId = teamId;
  }

  public static void main(String[] args) {
    startMCTSBot("127.0.0.1", "fe0ffcd1-2189-467a-acf7-e882ca3bac55");
  }
}
