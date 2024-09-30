package de.unimannheim.swt.pse.server.database.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;


class MapModelTest {

  @Test
  void testMapModelGettersAndSetters() {
    MapModel mapModel = new MapModel();
    mapModel.setMapId("map1");
    assertEquals("map1", mapModel.getMapId());

    mapModel.setCreatedBy("user1");
    assertEquals("user1", mapModel.getCreatedBy());

    mapModel.setGridSize(new int[]{10, 10});
    assertArrayEquals(new int[]{10, 10}, mapModel.getGridSize());

    mapModel.setTeams(2);
    assertEquals(2, mapModel.getTeams());

    mapModel.setFlags(1);
    assertEquals(1, mapModel.getFlags());

    mapModel.setBlocks(5);
    assertEquals(5, mapModel.getBlocks());

    mapModel.setPlacement("symmetrical");
    assertEquals("symmetrical", mapModel.getPlacement());

    mapModel.setTotalTimeLimitInSeconds(300);
    assertEquals(300, mapModel.getTotalTimeLimitInSeconds());

    mapModel.setMoveTimeLimitInSeconds(30);
    assertEquals(30, mapModel.getMoveTimeLimitInSeconds());
  }

  @Test
  void testMapPieceDescription() {
    MapModel.MapPieceDescription piece = new MapModel.MapPieceDescription();
    piece.setType("knight");
    assertEquals("knight", piece.getType());

    piece.setAttackPower(5);
    assertEquals(5, piece.getAttackPower());

    piece.setCount(3);
    assertEquals(3, piece.getCount());

    MapModel.MapMovement movement = new MapModel.MapMovement();
    piece.setMovement(movement);
    assertEquals(movement, piece.getMovement());
  }

  @Test
  void testMapMovement() {
    MapModel.MapMovement movement = new MapModel.MapMovement();
    MapModel.MapsDirections directions = new MapModel.MapsDirections();
    movement.setDirections(directions);
    assertEquals(directions, movement.getDirections());

    MapModel.MapShape shape = new MapModel.MapShape();
    movement.setShape(shape);
    assertEquals(shape, movement.getShape());
  }

  @Test
  void testMapsDirections() {
    MapModel.MapsDirections directions = new MapModel.MapsDirections();
    directions.setLeft(1);
    assertEquals(1, directions.getLeft());

    directions.setRight(2);
    assertEquals(2, directions.getRight());

    directions.setUp(3);
    assertEquals(3, directions.getUp());

    directions.setDown(4);
    assertEquals(4, directions.getDown());

    directions.setUpLeft(5);
    assertEquals(5, directions.getUpLeft());

    directions.setUpRight(6);
    assertEquals(6, directions.getUpRight());

    directions.setDownLeft(7);
    assertEquals(7, directions.getDownLeft());

    directions.setDownRight(8);
    assertEquals(8, directions.getDownRight());
  }

  @Test
  void testMapShape() {
    MapModel.MapShape shape = new MapModel.MapShape();
    shape.setType("lshape");
    assertEquals("lshape", shape.getType());
  }

  @Test
  void testMapModelToMap() {
    MapModel mapModel = new MapModel();
    mapModel.setMapId("map1");
    mapModel.setCreatedBy("user1");
    mapModel.setGridSize(new int[]{10, 10});
    mapModel.setTeams(2);
    mapModel.setFlags(1);
    mapModel.setBlocks(5);
    mapModel.setPlacement("symmetrical");
    mapModel.setTotalTimeLimitInSeconds(300);
    mapModel.setMoveTimeLimitInSeconds(30);

    MapModel.MapPieceDescription piece = new MapModel.MapPieceDescription();
    piece.setType("knight");
    piece.setAttackPower(5);
    piece.setCount(3);

    MapModel.MapMovement movement = new MapModel.MapMovement();
    MapModel.MapsDirections directions = new MapModel.MapsDirections();
    directions.setLeft(1);
    movement.setDirections(directions);

    piece.setMovement(movement);
    mapModel.setPieces(new MapModel.MapPieceDescription[]{piece});

    Map<String, Object> mapRepresentation = mapModel.toMap();

    assertEquals("map1", mapRepresentation.get("MapId"));
    assertEquals("user1", mapRepresentation.get("createdBy"));
    assertEquals(2, mapRepresentation.get("teams"));
    assertEquals(1, mapRepresentation.get("flags"));
    assertEquals(5, mapRepresentation.get("blocks"));
    assertEquals("symmetrical", mapRepresentation.get("placement"));
    assertEquals(300, mapRepresentation.get("totalTimeLimitInSeconds"));
    assertEquals(30, mapRepresentation.get("moveTimeLimitInSeconds"));

    List<Map<String, Object>> piecesList = (List<Map<String, Object>>) mapRepresentation.get(
        "pieces");
    assertNotNull(piecesList);
    assertFalse(piecesList.isEmpty());
    Map<String, Object> pieceMap = piecesList.get(0);
    assertEquals("knight", pieceMap.get("type"));
    assertEquals(5, pieceMap.get("attackPower"));
    assertEquals(3, pieceMap.get("count"));

    Map<String, Object> movementMap = (Map<String, Object>) pieceMap.get("movement");
    assertNotNull(movementMap);
    Map<String, Object> directionsMap = (Map<String, Object>) movementMap.get("directions");
    assertNotNull(directionsMap);
    assertEquals(1, directionsMap.get("left"));
  }
}

