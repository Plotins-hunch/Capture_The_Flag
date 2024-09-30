package de.unimannheim.swt.pse.ai.mcts;

import de.unimannheim.swt.pse.server.game.state.GameState;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the Monte Carlo Tree Search (MCTS) tree.
 */
public class MCTSNode {

  /**
   * Represents the state of the game at this node
   */
  private GameState state;

  /**
   * Parent node in the tree
   */
  private MCTSNode parent;

  /**
   * Children of this node
   */
  private List<MCTSNode> children;

  /**
   * Number of times this node has been visited
   */
  private int visitCount;

  /**
   * Sum of scores from all simulations through this node
   */
  private double winScore;

  /**
   * Flag indicating if the game is over at this node
   */
  private boolean gameOver = false;

  /**
   * Constructor for a new MCTSNode with a given game state.
   *
   * @param state the game state associated with this node.
   * @author ohandsch
   */
  public MCTSNode(GameState state) {
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
  public MCTSNode(MCTSNode parent, GameState state) {
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
  public MCTSNode findUCBChild() {
    MCTSNode bestChild = null;
    double maxUCB1 = Double.MIN_VALUE;
    for (MCTSNode child : children) {
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
  public MCTSNode findBestChild() {
    MCTSNode bestChild = null;
    double maxScore = Double.MIN_VALUE;
    for (MCTSNode child : children) {
      double score = child.winScore / child.visitCount;
      if (score > maxScore) {
        maxScore = score;
        bestChild = child;
      }
    }
    return bestChild;
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
    return winScore / visitCount + Math.sqrt(2) * (Math.sqrt(
        Math.log(parent.visitCount) / visitCount));
  }

  /**
   * Updates this node's visit count and win score following a simulation.
   *
   * @param score the score to add to the node's win score, usually 1.0 for a win or 0.0 for a
   *              loss.
   * @author ohandsch
   */
  public void backPropagate(double score) {
    MCTSNode node = this;
    while (node != null) {
      node.visitCount++;
      node.winScore += score;
      node = node.parent;
    }
  }

  /**
   * Selects a random child node from this node's children.
   *
   * @return a randomly selected child node.
   * @author ohandsch
   */
  public MCTSNode getRandomChildNode() {
    int randomIndex = (int) (Math.random() * children.size());
    return children.get(randomIndex);
  }

  /**
   * Adds a new child node to this node's list of children.
   *
   * @param child the MCTSNode to add as a child.
   * @author ohandsch
   */
  public void addChild(MCTSNode child) {
    children.add(child);
  }

  // Getters and setters
  public GameState getState() {
    return state;
  }

  public void setState(GameState state) {
    this.state = state;
  }

  public MCTSNode getParent() {
    return parent;
  }

  public void setParent(MCTSNode parent) {
    this.parent = parent;
  }

  public List<MCTSNode> getChildren() {
    return children;
  }

  public void setChildren(List<MCTSNode> children) {
    this.children = children;
  }

  public int getVisitCount() {
    return visitCount;
  }

  public void setVisitCount(int visitCount) {
    this.visitCount = visitCount;
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

}

