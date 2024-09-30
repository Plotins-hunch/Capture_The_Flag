package de.unimannheim.swt.pse.server.database.model;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a model for maps used in the game. This class includes detailed attributes of the map
 * such as grid size, number of teams, flags, and various gameplay elements. It is designed to
 * facilitate easy storage and retrieval from a Firebase database.
 */
public class MapModel {

  /**
   * The unique identifier of the map.
   */
  private String mapId;
  /**
   * The user who created the map.
   */
  private String createdBy;
  /**
   * The size of the grid.
   */
  private int[] gridSize;
  /**
   * The number of teams in the game.
   */
  private int teams;
  /**
   * The amount flags in the game.
   */
  private int flags;
  /**
   * list of the different pieces used
   */
  private MapPieceDescription[] pieces;
  /**
   * The amount of blocks in the game.
   */
  private int blocks;
  /**
   * The placement type of the pieces.
   */
  private String placement;
  /**
   * The total time limit for the game.
   */
  private int totalTimeLimitInSeconds;
  /**
   * The time limit for each move in the game.
   */
  private int moveTimeLimitInSeconds;

  /**
   * Represents a piece on the map. This class includes attributes such as type, attack power,
   * count, and movement.
   */
  public static class MapPieceDescription {

    private String type;
    private int attackPower;
    private int count;
    private MapMovement mapMovement;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public int getAttackPower() {
      return attackPower;
    }

    public void setAttackPower(int attackPower) {
      this.attackPower = attackPower;
    }

    public int getCount() {
      return count;
    }

    public void setCount(int count) {
      this.count = count;
    }

    public MapMovement getMovement() {
      return mapMovement;
    }

    public void setMovement(MapMovement mapMovement) {
      this.mapMovement = mapMovement;
    }

    /**
     * Converts this MapPieceDescription into a map format for Firebase storage.
     *
     * @return A map representing this piece's data.
     */
    public Map<String, Object> toMap() {
      Map<String, Object> pieceMap = new HashMap<>();
      pieceMap.put("type", type);
      pieceMap.put("attackPower", attackPower);
      pieceMap.put("count", count);
      if (mapMovement != null) {
        pieceMap.put("movement", mapMovement.toMap());
      }
      return pieceMap;
    }
  }

  /**
   * Describes the movement capabilities of a map piece including directions and shape.
   */
  public static class MapMovement {

    private MapsDirections mapsDirections;
    private MapShape mapShape;

    public MapsDirections getDirections() {
      return mapsDirections;
    }

    public void setDirections(MapsDirections mapsDirections) {
      this.mapsDirections = mapsDirections;
    }

    public MapShape getShape() {
      return mapShape;
    }

    public void setShape(MapShape mapShape) {
      this.mapShape = mapShape;
    }

    /**
     * Converts this MapMovement into a map format for Firebase storage.
     *
     * @return A map representing this movement's data.
     */
    public Map<String, Object> toMap() {
      Map<String, Object> movementMap = new HashMap<>();

      if (mapsDirections != null) {
        // Assuming Directions class also has a toMap method
        movementMap.put("directions", mapsDirections.toMap());
      }

      if (mapShape != null) {
        // Assuming Shape class also has a toMap method or can be represented directly
        Map<String, Object> shapeMap = new HashMap<>();
        shapeMap.put("type", mapShape.getType());
        movementMap.put("shape", shapeMap);
      }

      return movementMap;
    }
  }

  /**
   * Provides directional capabilities for a map piece.
   */
  public static class MapsDirections {

    private int left;
    private int right;
    private int up;
    private int down;
    private int upLeft;
    private int upRight;
    private int downLeft;
    private int downRight;

    public int getLeft() {
      return left;
    }

    public void setLeft(int left) {
      this.left = left;
    }

    public int getRight() {
      return right;
    }

    public void setRight(int right) {
      this.right = right;
    }

    public int getUp() {
      return up;
    }

    public void setUp(int up) {
      this.up = up;
    }

    public int getDown() {
      return down;
    }

    public void setDown(int down) {
      this.down = down;
    }

    public int getUpLeft() {
      return upLeft;
    }

    public void setUpLeft(int upLeft) {
      this.upLeft = upLeft;
    }

    public int getUpRight() {
      return upRight;
    }

    public void setUpRight(int upRight) {
      this.upRight = upRight;
    }

    public int getDownLeft() {
      return downLeft;
    }

    public void setDownLeft(int downLeft) {
      this.downLeft = downLeft;
    }

    public int getDownRight() {
      return downRight;
    }

    public void setDownRight(int downRight) {
      this.downRight = downRight;
    }

    /**
     * Converts this MapsDirections into a map format for Firebase storage.
     *
     * @return A map representing this direction's data.
     */
    public Map<String, Object> toMap() {
      Map<String, Object> directionsMap = new HashMap<>();
      directionsMap.put("left", left);
      directionsMap.put("right", right);
      directionsMap.put("up", up);
      directionsMap.put("down", down);
      directionsMap.put("upLeft", upLeft);
      directionsMap.put("upRight", upRight);
      directionsMap.put("downLeft", downLeft);
      directionsMap.put("downRight", downRight);
      return directionsMap;
    }

  }

  /**
   * Describes the shape of the movement path for a map piece.
   */
  public static class MapShape {

    private String type;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }


  public String getMapId() {
    return mapId;
  }

  public void setMapId(String mapId) {
    this.mapId = mapId;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public MapPieceDescription[] getPieces() {
    return pieces;
  }

  public void setPieces(MapPieceDescription[] pieces) {
    this.pieces = pieces;
  }

  public String getPlacement() {
    return placement;
  }

  public void setPlacement(String placement) {
    this.placement = placement;
  }

  public int[] getGridSize() {
    return gridSize;
  }

  public void setGridSize(int[] gridSize) {
    this.gridSize = gridSize;
  }

  public int getFlags() {
    return flags;
  }

  public void setFlags(int flags) {
    this.flags = flags;
  }

  public int getBlocks() {
    return blocks;
  }

  public void setBlocks(int blocks) {
    this.blocks = blocks;
  }

  public int getTeams() {
    return teams;
  }

  public void setTeams(int teams) {
    this.teams = teams;
  }

  public int getTotalTimeLimitInSeconds() {
    return totalTimeLimitInSeconds;
  }

  public void setTotalTimeLimitInSeconds(int totalTimeLimitInSeconds) {
    this.totalTimeLimitInSeconds = totalTimeLimitInSeconds;
  }

  public int getMoveTimeLimitInSeconds() {
    return moveTimeLimitInSeconds;
  }

  public void setMoveTimeLimitInSeconds(int moveTimeLimitInSeconds) {
    this.moveTimeLimitInSeconds = moveTimeLimitInSeconds;
  }

  /**
   * Converts this MapModel into a map format for Firebase storage.
   *
   * @return A map representing this map's data.
   */
  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("gridSize", Arrays.stream(gridSize).boxed().collect(Collectors.toList()));
    map.put("teams", teams);
    map.put("flags", flags);
    map.put("blocks", blocks);
    map.put("placement", placement);
    map.put("totalTimeLimitInSeconds", totalTimeLimitInSeconds);
    map.put("moveTimeLimitInSeconds", moveTimeLimitInSeconds);
    map.put("createdBy", createdBy);
    map.put("MapId", mapId);

    if (pieces != null) {
      // Convert each PieceDescription to a Map and collect to a List
      map.put("pieces", Arrays.stream(pieces)
          .map(MapPieceDescription::toMap)
          .collect(Collectors.toList()));
    }

    return map;
  }
}

