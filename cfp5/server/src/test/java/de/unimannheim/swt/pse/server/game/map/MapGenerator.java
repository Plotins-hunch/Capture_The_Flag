package de.unimannheim.swt.pse.server.game.map;

import java.util.Arrays;

public class MapGenerator {

  public Directions generateDirection(boolean allZero) {
    Directions direction = new Directions();
    if (allZero) {
      direction.setDown(0);
      direction.setDownLeft(0);
      direction.setDownRight(0);
      direction.setLeft(0);
      direction.setRight(0);
      direction.setUp(0);
      direction.setUpLeft(0);
      direction.setUpRight(0);
    } else {
      direction.setDown((int) (Math.random() * 4));
      direction.setDownLeft((int) (Math.random() * 4));
      direction.setDownRight((int) (Math.random() * 4));
      direction.setLeft((int) (Math.random() * 4));
      direction.setRight((int) (Math.random() * 4));
      direction.setUp((int) (/*Math.random() * */4));
      direction.setUpLeft((int) (Math.random() * 4));
      direction.setUpRight((int) (Math.random() * 4));
    }
    return direction;
  }

  public Movement generateMovement() {
    Movement movement = new Movement();
    if (Math.random() > 0.1 || true) {
      movement.setDirections(generateDirection(false));
      movement.setShape(new Shape());
    } else {
      Shape tempShape = new Shape();
      tempShape.setType(ShapeType.lshape);
      movement.setShape(tempShape);
      movement.setDirections(generateDirection(true));
    }
    return movement;
  }

  public PieceDescription[] generatePieceDescriptions(int pieceCount) {
    String[] types = {"Pawn", "Rook", "Knight", "Bishop", "Queen", "King"};
    PieceDescription[] pieces = new PieceDescription[pieceCount];
    for (int i = 0; i < pieceCount; i++) {
      pieces[i] = new PieceDescription();
      pieces[i].setType(types[(int) (Math.random() * types.length)]);
      pieces[i].setAttackPower((int) (Math.random() * 10) + 1);
      pieces[i].setCount(1);
      pieces[i].setMovement(generateMovement());
    }
    return pieces;
  }

  public MapTemplate generateMap(int[] size, int teamCount, int flagCount, int pieceCount,
      int blockCount, PlacementType placement, int totalTimeLimitInSeconds,
      int moveTimeLimitInSeconds) {
    MapTemplate mapTemplate = new MapTemplate();
    mapTemplate.setGridSize(size);
    mapTemplate.setTeams(teamCount);
    mapTemplate.setFlags(flagCount);
    mapTemplate.setBlocks(blockCount);
    mapTemplate.setPlacement(placement);
    mapTemplate.setTotalTimeLimitInSeconds(totalTimeLimitInSeconds);
    mapTemplate.setMoveTimeLimitInSeconds(moveTimeLimitInSeconds);
    PieceDescription[] pieces = new PieceDescription[pieceCount];
    for (int i = 0; i < pieceCount; i++) {
      pieces[i] = new PieceDescription();
    }
    mapTemplate.setPieces(pieces);
    return mapTemplate;

  }

  public String[][] generateEmptyGrid(int[] size) {
    String[][] grid = new String[size[0]][size[1]];
    for (String[] strings : grid) {
      Arrays.fill(strings, "");
    }
    return grid;
  }

  public PieceDescription getCustomPieceDescription(int up, int down, int left, int right, int upLeft, int upRight, int downLeft, int downRight, ShapeType shapeType, int attackPower) {
    PieceDescription customPiece = new PieceDescription();
    customPiece.setAttackPower(attackPower);
    customPiece.setType("CustomPiece");
    customPiece.setCount(1);
    Movement customMovement = new Movement();
    Shape shape = new Shape();
    shape.setType(shapeType);
    customMovement.setShape(shape);
    Directions customDirections = new Directions();
    customDirections.setUp(up);
    customDirections.setDown(down);
    customDirections.setLeft(left);
    customDirections.setRight(right);
    customDirections.setUpLeft(upLeft);
    customDirections.setUpRight(upRight);
    customDirections.setDownLeft(downLeft);
    customDirections.setDownRight(downRight);
    customMovement.setDirections(customDirections);
    customPiece.setMovement(customMovement);
    return customPiece;
  }
}
