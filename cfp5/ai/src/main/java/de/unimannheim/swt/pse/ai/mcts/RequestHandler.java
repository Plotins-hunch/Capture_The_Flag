package de.unimannheim.swt.pse.ai.mcts;

import de.unimannheim.swt.pse.server.CtfApplication;
import de.unimannheim.swt.pse.server.controller.data.GameSessionRequest;
import de.unimannheim.swt.pse.server.controller.data.GameSessionResponse;
import de.unimannheim.swt.pse.server.controller.data.JoinGameRequest;
import de.unimannheim.swt.pse.server.controller.data.JoinGameResponse;
import de.unimannheim.swt.pse.server.controller.data.MoveRequest;
import de.unimannheim.swt.pse.server.game.exceptions.GameSessionNotFound;
import de.unimannheim.swt.pse.server.game.exceptions.InvalidMove;
import de.unimannheim.swt.pse.server.game.state.GameState;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class RequestHandler {

  /**
   * The RestTemplate used to send requests to the server
   */
  private final RestTemplate restTemplate = new RestTemplate();
  /**
   * The base url of the server
   */
  private String baseUrl;

  /**
   * This method is used to set the base url of the server
   *
   * @param address String which is the address of the server
   * @author aemsbach
   */
  public void setBaseUrl(String address) {
    if (address.isEmpty()) {
      this.baseUrl = "http://localhost:8888";
      return;
    }
    this.baseUrl = address;

  }

  /**
   * This method is used to create a request to the server for a new GameSession
   *
   * @param request GameSessionRequest which player sends when wanting to play new Game
   * @return gameSessionResponse which is the response from the server
   * @author aemsbach
   */
  public GameSessionResponse createGameSession(GameSessionRequest request) {

    ResponseEntity<GameSessionResponse> gameSessioneResponseEntity = restTemplate.exchange(
        this.baseUrl + "/api/gamesession",
        HttpMethod.POST,
        new HttpEntity<>(request),
        GameSessionResponse.class
    );
    System.out.println("gameSessioneResponseEntity:" + gameSessioneResponseEntity.getBody());
    return gameSessioneResponseEntity.getBody();
  }

  /**
   * This method is used to get a GameSession from the server
   *
   * @param sessionId String which is the id of the GameSession
   * @return gameSessionResponse which is the response from the server
   * @author aemsbach
   */
  public GameSessionResponse getGameSession(String sessionId) {
    ResponseEntity<GameSessionResponse> gameSessionResponseEntity = restTemplate.exchange(
        this.baseUrl + "/api/gamesession/{sessionId}",
        HttpMethod.GET,
        null,
        GameSessionResponse.class,
        sessionId
    );
    return gameSessionResponseEntity.getBody();
  }


  /**
   * This method is used to get the current GameState from the server
   *
   * @param sessionId String which is the id of the GameSession
   * @return GameState which is the response from the server
   * @author aemsbach
   */
  public GameState getGameState(String sessionId) {
    ResponseEntity<GameState> gameStateResponseEntity = restTemplate.exchange(
        this.baseUrl + "/api/gamesession/{sessionId}/state",
        HttpMethod.GET,
        null,
        GameState.class,
        sessionId
    );

    return gameStateResponseEntity.getBody();
  }

  /**
   * This method is used to join an existing GameSession
   *
   * @param sessionId   String which is the id of the GameSession
   * @param joinRequest JoinGameRequest which is the request to join the GameSession
   * @return JoinGameResponse which is the response from the server
   * @author aemsbach
   */
  public JoinGameResponse joinGame(String sessionId, JoinGameRequest joinRequest)
      throws GameSessionNotFound {
    try {
      ResponseEntity<JoinGameResponse> joinGameResponseEntity = restTemplate.exchange(
          this.baseUrl + "/api/gamesession/{sessionId}/join",
          HttpMethod.POST,
          new HttpEntity<>(joinRequest),
          JoinGameResponse.class,
          sessionId
      );
      return joinGameResponseEntity.getBody();
    } catch (HttpClientErrorException e) {
      int code = e.getStatusCode().value();
      switch (code) {
        case 404:
          System.out.println("Game Session not Found");
          throw new GameSessionNotFound();
        case 429:
          System.out.println("User is spectator");
          break;
        default:
          System.out.println("Unknown Error");
          break;
      }
      return null;
    }
  }

  /**
   * This method is used to make a move in the GameSession
   *
   * @param sessionId   String which is the id of the GameSession
   * @param moveRequest MoveRequest which is the request to make a move
   * @author aemsbach
   */
  public boolean makeMove(String sessionId, MoveRequest moveRequest) throws InvalidMove {
    try {
      restTemplate.exchange(
          this.baseUrl + "/api/gamesession/{sessionId}/move",
          HttpMethod.POST,
          new HttpEntity<>(moveRequest),
          Void.class,
          sessionId
      );
      return true;
    } catch (HttpClientErrorException e) {
      int code = e.getStatusCode().value();
      switch (code) {
        case 404:
          System.out.println("Game Session not Found");
          break;
        case 409:
          throw new InvalidMove();
        case 403:
          System.out.println("Not your turn");
          break;
        case 410:
          System.out.println("Game is over");
          break;
        default:
          System.out.println("Unknown Error");
          break;
      }
      return false;
    }
  }

  public static void main(String[] args) {
    RequestHandler requestHandler = new RequestHandler();
    CtfApplication.main(args);
    requestHandler.setBaseUrl("");
    GameSessionRequest gameSessionRequest = new GameSessionRequest();
    GameSessionResponse gameSessionResponse = requestHandler.createGameSession(gameSessionRequest);
    System.out.println("gameSessionResponse:" + gameSessionResponse);
  }


}

