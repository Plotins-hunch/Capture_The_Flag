package de.unimannheim.swt.pse.ai.mcts;

import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import java.util.List;

/**
 * Main class of the MCTS algorithm where the best possible move is calculated and the game tree is
 * managed
 */
public class MCTS {

  private final MCTSNode root;
  private final int botTeamId;

  /**
   * Constructor initializes an MCTS instance with the initial game state.
   *
   * @param initialState the initial state of the game.
   * @author ohandsch
   */
  public MCTS(GameState initialState) {
    this.root = new MCTSNode(initialState);
    this.botTeamId = initialState.getCurrentTeam();
  }


  /**
   * Performs the MCTS search algorithm to find the best move without using parallelization.
   *
   * @param timeLimitMillis the time limit for the search in milliseconds.
   * @return the best move found during the search.
   * @author ohandsch
   */
  public Move search(long timeLimitMillis) {

    long endTime = System.currentTimeMillis() + timeLimitMillis;
    while (System.currentTimeMillis() < endTime) {
      MCTSNode selectedNode = selectPromisingNode(root);
      if (!isTerminal(selectedNode)) {
        expandNode(selectedNode);
      }
      MCTSNode nodeToExplore = selectedNode;
      if (!selectedNode.getChildren().isEmpty()) {
        nodeToExplore = selectedNode.getRandomChildNode();
      }
      boolean won = simulateRandomPlayout(nodeToExplore);
      backPropagate(nodeToExplore, won);
    }
    return findBestMove(root);
  }

  /**
   * Selects the most promising node to explore next based on the Upper Confidence Bound (UCB)
   * applied to trees.
   *
   * @param rootNode the root node of the current MCTS tree.
   * @return the most promising node to explore next.
   * @author ohandsch
   */
  private MCTSNode selectPromisingNode(MCTSNode rootNode) {
    MCTSNode node = rootNode;
    while (!node.getChildren().isEmpty()) {
      node = node.findUCBChild();
    }
    return node;
  }

  /**
   * Expands a node by creating new child nodes for all possible moves from the node's game state.
   *
   * @param node the node to expand.
   * @author ohandsch
   */
  public void expandNode(MCTSNode node) {
    List<Move> possibleMoves = new MoveSelector(node.getState()).getAllPossibleMoves();
    for (Move move : possibleMoves) {
      Pair<GameState, Boolean> result = simulateMove(node.getState(), move);
      GameState newState = result.first;
      MCTSNode newNode = new MCTSNode(node, newState);
      newNode.setGameOver(result.second);
      node.addChild(newNode);
    }
  }

  /**
   * Simulates a random playout from the given node's game state until a terminal state is reached.
   *
   * @param node the node from which to simulate the playout.
   * @return true if the playout resulted in a win for the bot's team, false otherwise.
   * @author ohandsch
   */
  private boolean simulateRandomPlayout(MCTSNode node) {
    GameState copiedState = GameStateCopier.deepCopy(node.getState());
    GameSimulator simulator = new GameSimulator(copiedState);
    return botTeamId == simulator.simulateGame();
  }

  /**
   * Backpropagates the result of a playout through the tree, updating the visit count and win score
   * of nodes.
   *
   * @param node the node from which to start backpropagation.
   * @param won  true if the playout resulted in a win, false otherwise.
   * @author ohandsch
   */
  private void backPropagate(MCTSNode node, boolean won) {
    double score = won ? 1.0 : 0.0;
    while (node != null) {
      node.setVisitCount(node.getVisitCount() + 1);
      node.setWinScore(node.getWinScore() + score);
      node = node.getParent();
    }
  }

  /**
   * Checks if the node represents a terminal state in the game.
   *
   * @param node the node to check.
   * @return true if the node represents a terminal state, false otherwise.
   * @author ohandsch
   */
  private boolean isTerminal(MCTSNode node) {
    return node.isGameOver();
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
    GameState copiedState = GameStateCopier.deepCopy(state);
    GameSimulator gameSimulator = new GameSimulator(copiedState);
    gameSimulator.makeMove(move);
    boolean gameOver = gameSimulator.isGameOver();
    GameState newState = gameSimulator.getGameState();
    return new Pair<>(newState, gameOver);
  }

  /**
   * Finds the best move from the root node based on the highest win rate.
   *
   * @param rootNode the root node of the MCTS tree.
   * @return the best move based on the search results.
   * @author ohandsch
   */
  private Move findBestMove(MCTSNode rootNode) {
    Move move = rootNode.findBestChild().getState().getLastMove();
    System.out.println(
        "Root visits: " + rootNode.getVisitCount() + " Win rate: "
            + rootNode.findBestChild().getWinScore() / rootNode.findBestChild().getVisitCount());
    System.out.println(
        "Best move found piece: " + move.getPieceId() + "x" + move.getNewPosition()[0] + "y"
            + move.getNewPosition()[1]);
    return move;
  }

  public MCTSNode getRoot() {
    return root;
  }


  /**
   * Utility class to store a pair of objects.
   *
   * @param <U> the type of the first object in the pair.
   * @param <V> the type of the second object in the pair.
   * @author ohandsch
   */
  public record Pair<U, V>(U first, V second) {

  }
}

