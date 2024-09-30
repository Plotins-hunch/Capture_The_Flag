package de.unimannheim.swt.pse.ai.mcts;

import de.unimannheim.swt.pse.server.game.map.Directions;
import de.unimannheim.swt.pse.server.game.map.Shape;
import de.unimannheim.swt.pse.server.game.map.ShapeType;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import de.unimannheim.swt.pse.server.game.state.Piece;
import de.unimannheim.swt.pse.server.game.state.Team;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for selecting moves for a team based on a given game state. It
 * considers all possible legal moves for the current team and provides methods to select moves
 * randomly or based on certain criteria.
 *
 * @author ohandsch
 */
public class MoveSelector {

  private GameState gameState;
  private Team team;
  private final Piece[] pieces;


  /**
   * Constructs a MoveSelector with the specified game state.
   *
   * @param gameState the current game state used to determine possible moves.
   * @author ohandsch
   */
  public MoveSelector(GameState gameState) {
    this.gameState = gameState;
    Team[] teams = gameState.getTeams();
    this.pieces = this.getPieces();
    for (Team t : teams) {
      if (Integer.parseInt(t.getId()) == gameState.getCurrentTeam()) {
        this.team = t;
        break;
      }
    }
  }

  /**
   * Retrieves all pieces in the game, regardless of team.
   *
   * @return an array of all pieces in the game state.
   * @author ohandsch
   */
  public Piece[] getPieces() {
    ArrayList<Piece> pieces = new ArrayList<>();
    for (Team t : this.gameState.getTeams()) {
      Collections.addAll(pieces, t.getPieces());
    }
    return pieces.toArray(new Piece[0]);
  }


  /**
   * Calculates all possible moves for the current team based on the current state.
   *
   * @return a list of all legal moves the current team can make.
   * @author ohandsch
   */
  public List<Move> getAllPossibleMoves() {
    List<Move> moves = new ArrayList<>();
    for (Team t : gameState.getTeams()) {
      if (Integer.parseInt(t.getId()) == gameState.getCurrentTeam()) {
        this.team = t;
        break;
      }
    }
    for (Piece piece : team.getPieces()) {
      if (piece.getPosition() == null) {
        System.out.println("Error!!!! piece is null");
        throw new IllegalArgumentException("piece position not allowed to be zero");
      }
      moves.addAll(getPossibleMovesForPiece(piece, Integer.parseInt(this.team.getId())));
    }
    return moves;
  }


  /**
   * Determines all possible moves for a specific piece belonging to a team.
   *
   * @param piece  the piece to calculate moves for.
   * @param teamId the team ID to which the piece belongs.
   * @return a list of potential moves for the specified piece.
   * @author ohandsch
   */
  private List<Move> getPossibleMovesForPiece(Piece piece, int teamId) {
    List<Move> moves = new ArrayList<>();
    int startRow = piece.getPosition()[0];
    int startCol = piece.getPosition()[1];
    Directions notUpdatedDirections = piece.getDescription().getMovement().getDirections();

    // Check directional moves
    if (notUpdatedDirections != null) {
      Directions directions = getUpdatedDirection(notUpdatedDirections, teamId);

      assert directions != null;
      addMovesInDirection(piece, directions.getLeft(),
          new int[]{0, -1}, moves); // left
      addMovesInDirection(piece, directions.getRight(),
          new int[]{0, 1}, moves); // right
      addMovesInDirection(piece, directions.getUp(),
          new int[]{-1, 0}, moves); // up
      addMovesInDirection(piece, directions.getDown(),
          new int[]{1, 0}, moves); // down
      addMovesInDirection(piece, directions.getUpLeft(),
          new int[]{-1, -1}, moves); // up-left
      addMovesInDirection(piece, directions.getUpRight(),
          new int[]{-1, 1}, moves); // up-right
      addMovesInDirection(piece, directions.getDownLeft(),
          new int[]{1, -1}, moves); // down-left
      addMovesInDirection(piece, directions.getDownRight(),
          new int[]{1, 1}, moves); // down-right
    }

    // Check L-shaped moves
    Shape shape = piece.getDescription().getMovement().getShape();
    if (shape != null && shape.getType().equals(ShapeType.lshape)) {
      getLShapedMoves(startRow, startCol, piece, moves);
    }

    return moves;
  }

  /**
   * Adds possible moves in a specific direction for a piece up to a specified number of steps.
   *
   * @param piece  the piece to move.
   * @param steps  the number of steps the piece can move in this direction.
   * @param coords the direction coordinates.
   * @param moves  the list of moves to add to.
   * @author jdeiting
   */
  private void addMovesInDirection(Piece piece, int steps, int[] coords, List<Move> moves) {
    int dRow = coords[0];
    int dCol = coords[1];
    int[] startPosition = piece.getPosition();
    for (int i = 1; i <= steps; i++) {
      int newRow = startPosition[0] + i * dRow;
      int newCol = startPosition[1] + i * dCol;
      if (newRow >= 0 && newRow < gameState.getGrid().length && newCol >= 0
          && newCol < gameState.getGrid()[0].length) {
        Move move = createMove(piece.getId(), newRow, newCol);
        if (isValidMove(move, piece)) {
          moves.add(move);
        }
      }
    }
  }


  /**
   * Generates L-shaped moves for a piece similar to a knight in chess.
   *
   * @param row   the starting row of the piece.
   * @param col   the starting column of the piece.
   * @param piece the piece to calculate moves for.
   * @param moves the list of moves to add to.
   * @author ohandsch
   */
  private void getLShapedMoves(int row, int col, Piece piece, List<Move> moves) {
    int[][] movesPattern = {{-2, -1}, {-1, -2}, {-2, 1}, {-1, 2}, {2, -1}, {1, -2}, {2, 1}, {1, 2}};

    for (int[] movePattern : movesPattern) {
      int targetRow = row + movePattern[0];
      int targetCol = col + movePattern[1];
      Move move = createMove(piece.getId(), targetRow, targetCol);
      if (isValidMove(move, piece)) {
        moves.add(move);
      }
    }

  }


  /**
   * Determines if a move is valid based on game rules.
   *
   * @param move  the move to validate.
   * @param piece the piece making the move.
   * @return true if the move is legal, false otherwise.
   * @author ohandsch
   */
  private boolean isValidMove(Move move, Piece piece) {
    String[][] grid = this.gameState.getGrid();

    if (piece.getPosition() == null) {
      return false;
    }

    // updating coordinates based on rotation
    int[] newPosition = move.getNewPosition();
    int[] currentPosition = piece.getPosition();

    // Check if the new position is out of bounds
    if (isOutOfBounds(newPosition, grid)) {
      return false;
    }

    if (isSamePosition(currentPosition, newPosition)) {
      return false;
    }

    return isPathClear(grid, currentPosition, newPosition, piece);
  }

  /**
   * Checks if a specified position is out of the bounds of the game grid.
   *
   * @param position The coordinates to check.
   * @param grid     The game grid.
   * @return true if the position is out of bounds, false otherwise.
   * @author ohandsch
   */
  private boolean isOutOfBounds(int[] position, String[][] grid) {
    // Check if the position's coordinates are outside the grid dimensions
    return position[0] < 0 || position[0] >= grid.length || position[1] < 0
        || position[1] >= grid[0].length;
  }

  /**
   * Checks if two positions are the same.
   *
   * @param currentPosition The current position of a piece.
   * @param newPosition     The new intended position of the piece.
   * @return true if both positions are the same, false otherwise.
   * @author ohandsch
   */
  private boolean isSamePosition(int[] currentPosition, int[] newPosition) {
    return currentPosition[0] == newPosition[0] && currentPosition[1] == newPosition[1];
  }


  /**
   * Determines if the path for a move is clear of obstacles and legal according to game rules.
   *
   * @param grid            The game grid.
   * @param currentPosition The current position of the piece making the move.
   * @param newPosition     The intended new position of the piece.
   * @param piece           The piece attempting the move.
   * @return true if the path is clear and the move is allowed, false otherwise.
   * @author ohandsch
   */
  private boolean isPathClear(String[][] grid, int[] currentPosition, int[] newPosition,
      Piece piece) {
    String positionContent = grid[newPosition[0]][newPosition[1]];
    if (!positionContent.isEmpty()) {
      return handlePositionContent(positionContent, piece, newPosition, grid, currentPosition);
    }
    return validateMoveDirection(grid, currentPosition, newPosition, piece);
  }


  /**
   * Handles the content of a position a piece is moving into, managing interactions such as
   * captures or collisions.
   *
   * @param positionContent The content at the position in the grid.
   * @param piece           The piece making the move.
   * @param newPosition     The intended new position of the piece.
   * @param grid            The game grid.
   * @param currentPosition The current position of the piece.
   * @return true if the move is successful, false if the move is invalid.
   * @author ohandsch
   */
  private boolean handlePositionContent(String positionContent, Piece piece, int[] newPosition,
      String[][] grid, int[] currentPosition) {

    if (positionContent.matches("p:[1-4]_[0-9]+")) { // Handle piece
      return handlePieceInteraction(positionContent, piece, newPosition, grid, currentPosition);
    } else if (positionContent.equals("b")) { // Handle block
      return false;
    } else { // Handle base
      return handleBaseInteraction(positionContent, piece, newPosition, grid, currentPosition);
    }
  }


  /**
   * Manages interactions when a piece encounters another piece on the grid.
   *
   * @param positionContent The content at the position in the grid indicating another piece.
   * @param piece           The piece making the move.
   * @param newPosition     The intended new position of the piece.
   * @param grid            The game grid.
   * @param currentPosition The current position of the piece.
   * @return true if the piece can legally move to the new position, false otherwise.
   * @author ohandsch
   */
  private boolean handlePieceInteraction(String positionContent, Piece piece, int[] newPosition,
      String[][] grid, int[] currentPosition) {
    String[] pieceInfo = positionContent.split("_");
    Piece enemyPiece = getPiece(pieceInfo[1], Integer.parseInt(pieceInfo[0].substring(2)));
    // check if piece is from my team
    assert enemyPiece != null;
    if (enemyPiece.getTeamId().equals(this.gameState.getCurrentTeam() + "")) {
      return false;
    }
    // check if enemy piece is stronger
    if (piece.getDescription().getAttackPower() < enemyPiece.getDescription()
        .getAttackPower()) {
      return false;
    }
    return validateMoveDirection(grid, currentPosition, newPosition, piece);
  }

  /**
   * Manages interactions when a piece encounters a base on the grid.
   *
   * @param positionContent The content at the position in the grid indicating a base.
   * @param piece           The piece making the move.
   * @param newPosition     The intended new position of the piece.
   * @param grid            The game grid.
   * @param currentPosition The current position of the piece.
   * @return true if the move to the base is allowed, false otherwise.
   * @author ohandsch
   */
  private boolean handleBaseInteraction(String positionContent, Piece piece, int[] newPosition,
      String[][] grid, int[] currentPosition) {
    if (!positionContent.substring(2).equals(piece.getTeamId())) {
      return validateMoveDirection(grid, currentPosition, newPosition, piece);
    }
    return false;
  }


  /**
   * Validates if the direction and distance of the proposed move are permissible according to the
   * piece's movement capabilities.
   *
   * @param grid            The current state of the game board.
   * @param currentPosition The current coordinates of the piece.
   * @param newPosition     The proposed new coordinates of the piece.
   * @param piece           The piece attempting the move.
   * @return true if the move is within the allowed directions and distances, false otherwise.
   * @author ohandsch
   */
  private boolean validateMoveDirection(String[][] grid, int[] currentPosition, int[] newPosition,
      Piece piece) {

    Shape shape = piece.getDescription().getMovement().getShape();

    if (shape != null && shape.getType() == ShapeType.lshape) {
      return isValidLShapeMove(grid, currentPosition, newPosition);
    } else {
      Directions directions = getUpdatedDirection(
          piece.getDescription().getMovement().getDirections(),
          Integer.parseInt(piece.getTeamId()));
      return isValidDirectionMove(grid, currentPosition, newPosition, directions);
    }
  }


  /**
   * Validates if a move follows the L-shaped pattern typical of knights in chess and checks if the
   * path is clear.
   *
   * @param grid            The game board.
   * @param currentPosition The starting position of the piece.
   * @param newPosition     The ending position of the piece..
   * @return true if the move is a valid L-shape and the path is unobstructed, false otherwise.
   * @author jdeiting
   */
  private boolean isValidLShapeMove(String[][] grid, int[] currentPosition, int[] newPosition) {
    int horizontalSteps = newPosition[1] - currentPosition[1];
    int verticalSteps = newPosition[0] - currentPosition[0];
    int absHorizontalSteps = Math.abs(horizontalSteps);
    int absVerticalSteps = Math.abs(verticalSteps);

    // Check if the move is exactly an L shape
    if (!((absHorizontalSteps == 2 && absVerticalSteps == 1) ||
        (absHorizontalSteps == 1 && absVerticalSteps == 2))) {
      return false;
    }

    // Path checking for L-shaped move
    return isPathClearForLShape(grid, currentPosition, horizontalSteps, verticalSteps);
  }


  /**
   * Checks if the path is clear for an L-shaped move, ensuring that no intermediate steps are
   * blocked.
   *
   * @param grid            The game board.
   * @param currentPosition The starting position of the piece.
   * @param horizontalSteps The horizontal component of the L-shaped move.
   * @param verticalSteps   The vertical component of the L-shaped move.
   * @return true if all intermediate positions in the L-shaped path are clear, false otherwise.
   * @author jdeiting
   */
  private boolean isPathClearForLShape(String[][] grid, int[] currentPosition, int horizontalSteps,
      int verticalSteps) {
    // Determine the direction of the longer segment (two steps)
    int majorStepX = 0;
    int majorStepY = 0;

    if (Math.abs(verticalSteps) == 2) { // Moving vertically two steps
      majorStepX = Integer.signum(verticalSteps);
    } else if (Math.abs(horizontalSteps) == 2) { // Moving horizontally two steps
      majorStepY = Integer.signum(horizontalSteps);
    }

    // First intermediate position (after one step in the major direction)
    int[] firstIntermediatePosition = {
        currentPosition[0] + majorStepX,
        currentPosition[1] + majorStepY
    };

    // Second intermediate position (completing two steps in the major direction)
    int[] secondIntermediatePosition = {
        currentPosition[0] + 2 * majorStepX,
        currentPosition[1] + 2 * majorStepY
    };

    // Check bounds and occupation for all positions
    if (isOutOfBounds(firstIntermediatePosition, grid) || isOutOfBounds(secondIntermediatePosition,
        grid)) {
      return false;
    }
    return grid[firstIntermediatePosition[0]][firstIntermediatePosition[1]].isEmpty() &&
        grid[secondIntermediatePosition[0]][secondIntermediatePosition[1]].isEmpty();
  }


  /**
   * Updates the directions based on the team's orientation on the board.
   *
   * @param directions The original directions object associated with a piece.
   * @param teamId     The ID of the team, which affects the orientation.
   * @return A new Directions object adjusted for the team's perspective.
   * @author ohandsch
   */
  private Directions getUpdatedDirection(Directions directions, int teamId) {
    Directions newDirections = new Directions();
    switch (teamId) {
      case 1:
        return directions;
      case 2:
        newDirections.setDown(directions.getUp());
        newDirections.setUp(directions.getDown());
        newDirections.setLeft(directions.getRight());
        newDirections.setRight(directions.getLeft());
        newDirections.setDownLeft(directions.getUpRight());
        newDirections.setUpRight(directions.getDownLeft());
        newDirections.setUpLeft(directions.getDownRight());
        newDirections.setDownRight(directions.getUpLeft());
        return newDirections;
      case 3:
        newDirections.setDown(directions.getRight());
        newDirections.setUp(directions.getLeft());
        newDirections.setLeft(directions.getDown());
        newDirections.setRight(directions.getUp());
        newDirections.setDownLeft(directions.getDownRight());
        newDirections.setUpRight(directions.getUpLeft());
        newDirections.setUpLeft(directions.getDownLeft());
        newDirections.setDownRight(directions.getUpRight());
        return newDirections;
      case 4:
        newDirections.setDown(directions.getLeft());
        newDirections.setUp(directions.getRight());
        newDirections.setLeft(directions.getUp());
        newDirections.setRight(directions.getDown());
        newDirections.setDownLeft(directions.getUpLeft());
        newDirections.setUpRight(directions.getDownRight());
        newDirections.setUpLeft(directions.getUpRight());
        newDirections.setDownRight(directions.getDownLeft());
        return newDirections;
      default:
        return null;
    }
  }


  /**
   * Validates a move in a straight line, either horizontally or vertically, ensuring no pieces
   * block the path.
   *
   * @param grid            The game board.
   * @param currentPosition The starting position of the piece.
   * @param newPosition     The ending position of the piece.
   * @param directions      The set of directions before adjustment for team orientation.
   * @return true if the move is straight and unobstructed, false otherwise.
   * @author ohandsch
   */
  private boolean isValidDirectionMove(String[][] grid, int[] currentPosition, int[] newPosition,
      Directions directions) {
    int verticalSteps = newPosition[0] - currentPosition[0];
    int horizontalSteps = newPosition[1] - currentPosition[1];

    // Horizontal movement
    if (verticalSteps == 0) {
      return isValidHorizontalMove(grid, currentPosition, horizontalSteps, directions);
    }
    // Vertical movement
    if (horizontalSteps == 0) {
      return isValidVerticalMove(grid, currentPosition, verticalSteps, directions);
    }
    // Diagonal movement
    if (Math.abs(horizontalSteps) == Math.abs(verticalSteps)) {
      return isValidDiagonalMove(grid, currentPosition, horizontalSteps, verticalSteps,
          directions);
    }

    return false;
  }


  /**
   * Checks if a horizontal move is valid based on the piece's movement capabilities and board
   * conditions.
   *
   * @param grid            The game board.
   * @param currentPosition The current position of the piece.
   * @param steps           The number of steps to move horizontally.
   * @param directions      The directions allowed for the piece.
   * @return true if the horizontal move is within the piece's capabilities and the path is
   * unobstructed, false otherwise.
   * @author ohandsch
   */
  private boolean isValidHorizontalMove(String[][] grid, int[] currentPosition, int steps,
      Directions directions) {
    if (steps == 0) {
      return true;
    }
    int direction = Integer.signum(steps);
    int maxSteps = direction > 0 ? directions.getRight() : directions.getLeft();

    if (Math.abs(steps) > maxSteps) {
      return false;
    }

    for (int i = 1; i < Math.abs(steps); i++) {
      if (!grid[currentPosition[0]][currentPosition[1] + i * direction].isEmpty()) {
        return false;
      }
    }
    return true;
  }


  /**
   * Checks if a vertical move is valid based on the piece's movement capabilities and board
   * conditions.
   *
   * @param grid            The game board.
   * @param currentPosition The current position of the piece.
   * @param steps           The number of steps to move vertically.
   * @param directions      The directions allowed for the piece.
   * @return true if the vertical move is within the piece's capabilities and the path is
   * unobstructed, false otherwise.
   * @author ohandsch
   */
  private boolean isValidVerticalMove(String[][] grid, int[] currentPosition, int steps,
      Directions directions) {
    if (steps == 0) {
      return true; // no movement
    }
    int direction = Integer.signum(steps);
    int maxSteps = direction > 0 ? directions.getDown() : directions.getUp();

    if (Math.abs(steps) > maxSteps) {
      return false;
    }

    for (int i = 1; i < Math.abs(steps); i++) {
      if (!grid[currentPosition[0] + i * direction][currentPosition[1]].isEmpty()) {
        return false;
      }
    }
    return true;
  }


  /**
   * Validates diagonal moves, checking both the distance and path clearance based on the piece's
   * movement capabilities.
   *
   * @param grid            The game board.
   * @param currentPosition The starting position of the piece.
   * @param horizontalSteps The horizontal component of the move.
   * @param verticalSteps   The vertical component of the move.
   * @param directions      The directions allowed for the piece.
   * @return true if the diagonal move is valid and unobstructed, false otherwise.
   * @author ohandsch
   */
  private boolean isValidDiagonalMove(String[][] grid, int[] currentPosition,
      int horizontalSteps, int verticalSteps, Directions directions) {
    int steps = Math.abs(horizontalSteps);
    boolean isPositiveHorizontal = horizontalSteps > 0;
    boolean isPositiveVertical = verticalSteps > 0;

    int maxSteps;
    if (isPositiveVertical && isPositiveHorizontal) {
      maxSteps = directions.getDownRight();
    } else if (isPositiveVertical) {
      maxSteps = directions.getDownLeft();
    } else if (isPositiveHorizontal) {
      maxSteps = directions.getUpRight();
    } else {
      maxSteps = directions.getUpLeft();
    }

    if (Math.abs(
        horizontalSteps) > 0 && Math.abs(
        verticalSteps) > 0
        && Math.abs(
        verticalSteps) != Math.abs(
        horizontalSteps)) {
      return false;
    }

    if (steps > maxSteps) {
      return false;
    }

    for (int i = 1; i < steps; i++) {
      if (!grid[currentPosition[0] + i * Integer.signum(verticalSteps)][currentPosition[1]
          + i * Integer.signum(horizontalSteps)].isEmpty()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Retrieves a Piece object by its ID and team.
   *
   * @param pieceId The ID of the piece to find.
   * @param team    The team ID to which the piece belongs.
   * @return The Piece object if found, otherwise null.
   * @author ohandsch
   */
  private Piece getPiece(String pieceId, int team) {
    for (Piece piece : this.pieces) {
      if (piece.getId().equals(pieceId) && piece.getTeamId().equals(team + "")) {
        return piece;
      }
    }
    return null;
  }


  /**
   * Creates a move object for a given piece and target position.
   *
   * @param pieceId   The ID of the piece making the move.
   * @param targetRow The target row on the grid to which the piece moves.
   * @param targetCol The target column on the grid to which the piece moves.
   * @return A new Move object representing the move to the specified position.
   * @author ohandsch
   */
  private Move createMove(String pieceId, int targetRow, int targetCol) {
    Move move = new Move();
    move.setPieceId(pieceId);
    move.setNewPosition(new int[]{targetRow, targetCol});
    move.setTeamId(this.team.getId());
    return move;
  }


  /**
   * Selects a move not purely at random but with some considerations, faster than a fully evaluated
   * random selection.
   *
   * @return A Move object selected with certain preferences or null if no suitable moves are
   * available.
   * @author ohandsch
   */
  public Move selectRandomMove() {
    List<Move> moves = getAllPossibleMoves();
    if (moves.isEmpty()) {
      return null;
    }
    int randomIndex = (int) (Math.random() * moves.size());
    return moves.get(randomIndex);
  }


  public GameState getGameState() {
    return this.gameState;
  }

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }

  public Team getTeam() {
    return this.team;
  }


}
