package de.unimannheim.swt.pse.ai.advancedMCTS;

import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main class of the MCTS algorithm where the best possible move is calculated and the game tree is
 * managed
 */
public class AdvancedMCTS {

  private final AdvancedMCTSNode root;
  private final int botTeamId;
  private long endTime = 0;

  /**
   * Constructor initializes an MCTS instance with the initial game state.
   *
   * @param initialState the initial state of the game.
   * @author ohandsch
   */
  public AdvancedMCTS(GameState initialState) {
    this.root = new AdvancedMCTSNode(initialState);
    this.root.setDeletable(false);
    this.botTeamId = initialState.getCurrentTeam();
  }

  /**
   * Performs a parallel MCTS search using multiple threads.
   *
   * @param numThreads      the number of threads to use for the search.
   * @param timeLimitMillis the time limit for the search in milliseconds.
   * @return the best move found during the search.
   * @author ohandsch
   */
  public Move parallelSearch(int numThreads, long timeLimitMillis) {
    this.expandNode(root, true);
    List<AdvancedMCTSNode> roots;
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    roots = new ArrayList<>();
    endTime = System.currentTimeMillis() + timeLimitMillis;

    for (int i = 0; i < numThreads; i++) {
      AdvancedMCTSNode localRoot = new AdvancedMCTSNode(
          AdvancedGameStateCopier.deepCopy(root.getState()));
      localRoot.setDeletable(false);
      roots.add(localRoot);
      executor.submit(() -> this.search(localRoot));
    }
    executor.shutdown();
    try {
      boolean shutDown = executor.awaitTermination(timeLimitMillis, TimeUnit.MILLISECONDS);
      System.out.println("Shut down: " + shutDown);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    return aggregateResults(roots);
  }

  /**
   * Aggregates the results from each thread's individual MCTS root node and populate the actual
   * MCTS root node with the aggregates.
   *
   * @param roots a list of MCTSNode, each being the root node of a thread's MCTS tree.
   * @return the best move aggregated from all the roots.
   * @author ohandsch
   */
  public Move aggregateResults(List<AdvancedMCTSNode> roots) {
    System.out.println("Aggregating results");
    // Aggregate the visit counts and win scores from each root's children
    for (AdvancedMCTSNode localRoot : roots) {
      for (AdvancedMCTSNode child : localRoot.getChildren()) {
        AdvancedMCTSNode correspondingChild = this.getChildMatching(child.getState().getLastMove());
        root.setVisitCount(root.getVisitCount() + child.getVisitCount());
        if (correspondingChild != null) {
          correspondingChild.setVisitCount(
              correspondingChild.getVisitCount() + child.getVisitCount());
          correspondingChild.setWinScore(correspondingChild.getWinScore() + child.getWinScore());
        } else {
          root.addChild(new AdvancedMCTSNode(child.getState()));
        }
      }
    }
    System.out.println("Results Aggregated");
    return findBestMove(root);
  }

  /**
   * Finds the matching child node for a given move in the MCTS tree.
   *
   * @param move the move for which to find the corresponding child node.
   * @return the child MCTSNode matching the given move, or null if no match is found.
   * @author ohandsch
   */
  private AdvancedMCTSNode getChildMatching(Move move) {
    if (this.root.getChildren().isEmpty()) {
      return null;
    }
    for (AdvancedMCTSNode child : this.root.getChildren()) {
      Move lastMove = child.getState().getLastMove();

      if (!lastMove.getPieceId().equals(move.getPieceId())) {
        continue;
      }

      if (lastMove.getNewPosition()[0] != move.getNewPosition()[0] ||
          lastMove.getNewPosition()[1] != move.getNewPosition()[1]) {
        continue;
      }

      if (lastMove.getTeamId().equals(move.getTeamId())) {
        return child;
      }
    }
    return null;
  }


  /**
   * Performs the MCTS search algorithm to find the best move without using parallelization.
   *
   * @param rootInp the root node from which to start the search.
   * @author jdeiting
   */
  public void search(AdvancedMCTSNode rootInp) {

    while (System.currentTimeMillis() < endTime) {
      AdvancedMCTSNode selectedNode = selectPromisingNode(rootInp);

      if (selectedNode == rootInp || !isTerminal(selectedNode)) {
        expandNode(selectedNode, selectedNode == rootInp);

        AdvancedMCTSNode nodeToExplore = selectedNode;
        if (!selectedNode.getChildren().isEmpty()) {
          nodeToExplore = selectedNode.getRandomChildNode();
        }

        int numSimulations = getNumSimulations(nodeToExplore);
        boolean won;
        for (int i = 0; i < numSimulations; i++) {
          won = simulateRandomPlayout(nodeToExplore);
          backPropagate(nodeToExplore, won);
        }

        if (selectedNode.isDeletable()) {
          selectedNode.setState(null);
        }
      } else {
        if (selectedNode.isWinner()) {

          selectedNode.setWinScore(selectedNode.getWinScore() + 1);
        }
        selectedNode.setVisitCount(selectedNode.getVisitCount() + 1);
        backPropagate(selectedNode, selectedNode.isWinner());
      }


    }
    findBestMove(rootInp);
  }


  /**
   * Calculates the number of simulations to perform for a given node based on the number of winning
   * and losing moves. Makes sure to further explore interesting nodes with potential critical
   * moves.
   *
   * @param node the node for which to calculate the number of simulations.
   * @return the number of simulations to perform for the given node.
   * @author jdeiting
   */
  private int getNumSimulations(AdvancedMCTSNode node) {
    int baseSimulations = 10;
    int winningMoves = countWinningMoves(node);
    int losingMoves = countLosingMoves(node);
    int totalCriticalMoves = winningMoves + losingMoves;

    if (totalCriticalMoves > 0) {
      double criticalMoveRatio = (double) totalCriticalMoves / node.getChildren().size();
      int additionalSimulations = (int) (criticalMoveRatio * baseSimulations);
      return baseSimulations + additionalSimulations;
    }

    return baseSimulations;
  }

  /**
   * Counts the number of winning moves in a node's children.
   *
   * @param node the node for which to count the winning moves.
   * @return the number of winning moves in the node's children.
   * @author jdeiting
   */
  private int countWinningMoves(AdvancedMCTSNode node) {
    int count = 0;
    for (AdvancedMCTSNode child : node.getChildren()) {
      if (child.isWinner()) {
        count++;
      }
    }
    return count;
  }

  /**
   * Counts the number of losing moves in a node's children.
   *
   * @param node the node for which to count the losing moves.
   * @return the number of losing moves in the node's children.
   * @author jdeiting
   */
  private int countLosingMoves(AdvancedMCTSNode node) {
    int count = 0;
    for (AdvancedMCTSNode child : node.getChildren()) {
      if (child.isGameOver() && !child.isWinner()) {
        count++;
      }
    }
    return count;
  }


  /**
   * Selects the most promising node to explore next based on the Upper Confidence Bound (UCB)
   * applied to trees.
   *
   * @param rootNode the root node of the current MCTS tree.
   * @return the most promising node to explore next.
   * @author ohandsch
   */
  private AdvancedMCTSNode selectPromisingNode(AdvancedMCTSNode rootNode) {
    AdvancedMCTSNode node = rootNode;
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
  public void expandNode(AdvancedMCTSNode node, boolean isRoot) {
    List<Move> possibleMoves = new AdvancedMoveSelector(node.getState()).getAllPossibleMoves();
    for (Move move : possibleMoves) {

      Triple<GameState, Boolean, Boolean> result = simulateMove(node.getState(), move);
      GameState newState = result.first;
      AdvancedMCTSNode newNode = new AdvancedMCTSNode(node, newState);
      newNode.setGameOver(result.second);
      if (result.second) {
        newNode.setWinner(result.third);
      }
      if (isRoot) {
        newNode.setDeletable(false);
      }
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
  private boolean simulateRandomPlayout(AdvancedMCTSNode node) {
    GameState copiedState = AdvancedGameStateCopier.deepCopy(node.getState());
    AdvancedGameSimulator simulator = new AdvancedGameSimulator(copiedState);
    return botTeamId == simulator.simulateGame();
  }

  /**
   * Backpropagates the result of a playout through the tree, updating the visit count and win score
   * of nodes.
   *
   * @param node the node from which to start backpropagation.
   * @param won  true if the playout resulted in a win, false otherwise.
   * @author jdeiting
   */
  private void backPropagate(AdvancedMCTSNode node, boolean won) {
    double score = won ? 1.0 : 0.0;
    if (node != null) {
      node.backPropagate(score);
    }
  }

  /**
   * Checks if the node represents a terminal state in the game.
   *
   * @param node the node to check.
   * @return true if the node represents a terminal state, false otherwise.
   * @author ohandsch
   */
  private boolean isTerminal(AdvancedMCTSNode node) {
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
  public Triple<GameState, Boolean, Boolean> simulateMove(GameState state, Move move) {
    GameState copiedState = AdvancedGameStateCopier.deepCopy(state);
    AdvancedGameSimulator gameSimulator = new AdvancedGameSimulator(copiedState);
    gameSimulator.makeMove(move);
    boolean gameOver = gameSimulator.isGameOver();
    GameState newState = gameSimulator.getGameState();
    boolean winner = gameSimulator.getResult() == botTeamId;
    return new Triple<>(newState, gameOver, winner);
  }

  /**
   * Finds the best move from the root node based on the highest win rate.
   *
   * @param rootNode the root node of the MCTS tree.
   * @return the best move based on the search results.
   * @author ohandsch
   */
  private Move findBestMove(AdvancedMCTSNode rootNode) {
    Move move = rootNode.findBestChild().getState().getLastMove();
    System.out.println(
        "Root visits: " + rootNode.getVisitCount() + " Win rate: "
            + rootNode.findBestChild().getWinScore() / rootNode.findBestChild().getVisitCount());
    System.out.println(
        "Best move found piece: " + move.getPieceId() + "x" + move.getNewPosition()[0] + "y"
            + move.getNewPosition()[1]);
    return move;
  }

  public AdvancedMCTSNode getRoot() {
    return root;
  }


  /**
   * Utility class to store a pair of objects.
   *
   * @param <U> the type of the first object in the pair.
   * @param <V> the type of the second object in the pair.
   * @param <W> the type of the second object in the pair.
   * @author ohandsch
   */
  public record Triple<U, V, W>(U first, V second, W third) {

  }
}

