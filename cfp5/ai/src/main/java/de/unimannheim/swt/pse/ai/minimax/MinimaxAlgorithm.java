package de.unimannheim.swt.pse.ai.minimax;

import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import java.util.List;

/**
 * Main class for the Minimax algorithm implementation where the best move is calculated.
 */
public class MinimaxAlgorithm {

  private static final int MAX_DEPTH = 4;
  private final GameState currentState;
  private MoveSelector moveSelector;
  private boolean gameOver = false;
  private long stopTime;

  /**
   * Constructor for MinimaxAlgorithm
   *
   * @param currentState the current state of the game
   * @author rkonradt
   */
  public MinimaxAlgorithm(GameState currentState) {
    this.currentState = currentState;
  }


  /**
   * Get the best move for the current game state by calling miniMax
   *
   * @param timeLimitMillis given Timelimit
   * @return the best move for the current game state found by the miniMax algorithm
   * @author rkonradt
   */
  public Move getBestMove(long timeLimitMillis) {
    int bestVal = Integer.MIN_VALUE;
    Move bestMove = null;
    int moveVal;
    moveSelector = new MoveSelector(currentState);
    stopTime = System.currentTimeMillis() + timeLimitMillis;
    for (Move move : moveSelector.getAllPossibleMoves()) {
      GameState copiedState = GameStateCopier.deepCopy(currentState);
      simulateMove(copiedState, move);
      moveVal = miniMax(copiedState, MAX_DEPTH, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
      if (moveVal > bestVal) {
        bestVal = moveVal;
        bestMove = move;
      }
    }
    return bestMove;
  }

  /**
   * Minimax algorithm implementation with alpha-beta pruning
   *
   * @param currentGameState   the current state of the game
   * @param depth              the depth of the search tree
   * @param isMaximizingPlayer boolean to check if the player is maximizing
   * @param alpha              variable for pruning
   * @param beta               variable for pruning
   * @return the best value for the current game state
   * @author rkonradt
   */
  private int miniMax(GameState currentGameState, int depth, boolean isMaximizingPlayer, int alpha,
      int beta) {

    if (System.currentTimeMillis() < stopTime) {
      moveSelector = new MoveSelector(currentGameState);

      if (depth == 0
          || Math.abs(HeuristicEvaluator.evaluate(currentGameState, isMaximizingPlayer)) >= 100
          || this.gameOver) {
        return HeuristicEvaluator.evaluate(currentGameState, isMaximizingPlayer);
      }
      if (!isMaximizingPlayer) {
        int lowestVal = Integer.MAX_VALUE;
        List<Move> moves = moveSelector.getAllPossibleMoves();
        GameState[] modifiedStates = new GameState[moves.size()];
        int i = 0;
        for (Move move : moves) {
          GameState copiedState = GameStateCopier.deepCopy(currentGameState);
          Pair<GameState, Boolean> afterMove = simulateMove(copiedState, move);
          modifiedStates[i] = afterMove.first;
          this.gameOver = afterMove.second;
          i++;
        }
        for (GameState modifiedState : modifiedStates) {
          int eval = miniMax(modifiedState, depth - 1, true, alpha, beta);
          lowestVal = Math.min(lowestVal, eval);
          beta = Math.min(beta, eval);
          if (beta <= alpha) {
            break;
          }
        }
        return lowestVal;
      } else {
        int highestVal = Integer.MIN_VALUE;
        List<Move> moves = moveSelector.getAllPossibleMoves();
        GameState[] modifiedStates = new GameState[moves.size()];
        int i = 0;
        for (Move move : moves) {
          GameState copiedState = GameStateCopier.deepCopy(currentGameState);
          Pair<GameState, Boolean> afterMove = simulateMove(copiedState, move);
          modifiedStates[i] = afterMove.first;
          this.gameOver = afterMove.second;
          i++;
        }
        for (GameState modifiedState : modifiedStates) {
          int eval = miniMax(modifiedState, depth - 1, false, alpha, beta);
          highestVal = Math.max(highestVal, eval);
          alpha = Math.max(alpha, eval);
          if (beta <= alpha) {
            break;
          }
        }
        return highestVal;
      }
    }
    return HeuristicEvaluator.evaluate(currentGameState, isMaximizingPlayer);
  }


  /**
   * Simulates a move in the game state, creating a new state with the move made.
   *
   * @param state the current state of the game.
   * @param move  the move to simulate.
   * @return a pair containing the new game state after the move and a boolean indicating if the
   * game is over.
   * @author ohandsch
   */
  public Pair<GameState, Boolean> simulateMove(GameState state, Move move) {
    GameSimulator gameSimulator = new GameSimulator(state);
    gameSimulator.makeMove(move);
    boolean gameOver = gameSimulator.isGameOver();
    GameState newState = gameSimulator.getGameState();
    return new Pair<>(newState, gameOver);
  }



  /**
   * Utility class to store a pair of objects.
   *
   * @param <U> the type of the first object in the pair.
   * @param <V> the type of the second object in the pair.
   * @author ohandsch
   */
  public static class Pair<U, V> {

    public final U first;
    public final V second;

    public Pair(U first, V second) {
      this.first = first;
      this.second = second;
    }
  }
}
