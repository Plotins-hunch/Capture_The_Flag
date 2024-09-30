package de.unimannheim.swt.pse.ai.advancedMCTS;

import de.unimannheim.swt.pse.server.game.state.GameState;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the Monte Carlo Tree Search (MCTS) tree.
 */
public class AdvancedMCTSNode {

  /**
   * Represents the state of the game at this node
   */
  private GameState state;
  /**
   * Parent node in the tree
   */
  private AdvancedMCTSNode parent;
  /**
   * Children of this node
   */
  private List<AdvancedMCTSNode> children;
  /**
   * Number of times this node has been visited
   */
  private int visitCount;
  /**
   * Sum of scores from all simulations through this node
   */
  private double winScore;

  /**
   * Flag indicating if the game is over
   */
  private boolean gameOver = false;

  /**
   * Indicated whether the gameState of this node can be deleted after expanding its children
   */
  private boolean isDeletable = true;

  private boolean isWinner;

  /**
   * Constructor for a new MCTSNode with a given game state.
   *
   * @param state the game state associated with this node.
   * @author ohandsch
   */
  public AdvancedMCTSNode(GameState state) {
    this.state = state;
    this.children = new ArrayList<>();
    this.visitCount = 0;
    this.winScore = 0.0;
  }

  /**
   * Constructor for a new MCTSNode with a parent node and a game state.
   *
   * @param parent the parent node in the MCTS tree.
   * @param state  the game state associated with this node.
   * @author ohandsch
   */
  public AdvancedMCTSNode(AdvancedMCTSNode parent, GameState state) {
    this(state);
    this.parent = parent;
  }

  /**
   * Selects the child node with the highest Upper Confidence Bound (UCB1) score to balance
   * exploration and exploitation.
   *
   * @return the child node with the highest UCB1 score.
   * @author ohandsch
   */
  public AdvancedMCTSNode findUCBChild() {
    AdvancedMCTSNode bestChild = null;
    double maxUCB1 = Double.MIN_VALUE;
    for (AdvancedMCTSNode child : children) {
      double ucb1Value = child.getUCB1Score();
      if (ucb1Value > maxUCB1) {
        maxUCB1 = ucb1Value;
        bestChild = child;
      }
    }
    return bestChild;
  }

  /**
   * Finds the child with the best score, based on the win rate.
   *
   * @return the child node with the highest win rate score.
   * @author ohandsch
   */
  public AdvancedMCTSNode findBestChild() {
    AdvancedMCTSNode bestChild = null;
    double maxScore = Double.MIN_VALUE;
    for (AdvancedMCTSNode child : children) {
      double score = child.winScore / child.visitCount;
      if (score > maxScore) {
        maxScore = score;
        bestChild = child;
      }
    }
    return bestChild;
  }

  /**
   * Backpropagates the result of a simulation through this node and its parents.
   *
   * @param score the score of the simulation, 1 for a win and 0 for a loss.
   * @author ohandsch
   */
  public void backPropagate(double score) {
    this.visitCount++;
    this.winScore += score;
    if (parent != null) {
      parent.backPropagate(1 - score);
    }
  }

  /**
   * Calculates the UCB1 (Upper Confidence Bound 1) score for this node.
   *
   * @return the UCB1 score calculated for this node.
   * @author ohandsch
   */
  private double getUCB1Score() {
    if (visitCount == 0) {
      return Double.MAX_VALUE;
    }
    return 2 * (winScore / visitCount) + Math.sqrt(2) * (Math.sqrt(
        Math.log(parent.visitCount) / visitCount));
  }

  /**
   * Selects a random child node from this node's children.
   *
   * @return a randomly selected child node.
   * @author ohandsch
   */
  public AdvancedMCTSNode getRandomChildNode() {
    int randomIndex = (int) (Math.random() * children.size());
    return children.get(randomIndex);
  }

  /**
   * Adds a new child node to this node's list of children.
   *
   * @param child the MCTSNode to add as a child.
   * @author ohandsch
   */
  public void addChild(AdvancedMCTSNode child) {
    children.add(child);
  }

  // Getters and setters
  public GameState getState() {
    return state;
  }

  public void setState(GameState state) {
    this.state = state;
  }

  public AdvancedMCTSNode getParent() {
    return parent;
  }

  public void setParent(AdvancedMCTSNode parent) {
    this.parent = parent;
  }

  public List<AdvancedMCTSNode> getChildren() {
    return children;
  }

  public void setChildren(List<AdvancedMCTSNode> children) {
    this.children = children;
  }

  public int getVisitCount() {
    return visitCount;
  }

  public void setVisitCount(int visitCount) {
    this.visitCount = visitCount;
  }

  public void setWinner(boolean isWinner) {
    this.isWinner = isWinner;
  }

  public double getWinScore() {
    return winScore;
  }

  public void setWinScore(double winScore) {
    this.winScore = winScore;
  }

  public boolean isGameOver() {
    return gameOver;
  }

  public void setGameOver(boolean gameOver) {
    this.gameOver = gameOver;
  }

  public boolean isDeletable() {
    return isDeletable;
  }

  public void setDeletable(boolean deletable) {
    isDeletable = deletable;
  }

  public boolean isWinner() {
    return isWinner;
  }

}

