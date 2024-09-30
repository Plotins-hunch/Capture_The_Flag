package de.unimannheim.swt.pse.server.database;

import de.unimannheim.swt.pse.server.database.model.MapModel;
import de.unimannheim.swt.pse.server.game.map.Directions;
import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import de.unimannheim.swt.pse.server.game.map.Movement;
import de.unimannheim.swt.pse.server.game.map.PieceDescription;
import de.unimannheim.swt.pse.server.game.map.PlacementType;
import de.unimannheim.swt.pse.server.game.map.Shape;
import de.unimannheim.swt.pse.server.game.map.ShapeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapModelMapperTest {

  @Test
  void testToMapsModel() {
    // Create a test map
    MapTemplate mapTemplate = new MapTemplate();
    int[] size = {10, 10};
    PieceDescription[] pd = new PieceDescription[3];
    PieceDescription p1 = new PieceDescription();
    PieceDescription p2 = new PieceDescription();
    PieceDescription p3 = new PieceDescription();
    p1.setType("infantry");
    p1.setAttackPower(5);
    p1.setCount(2);
    Movement mvmt1 = new Movement();
    Directions d1 = new Directions();
    d1.setDown(2);
    d1.setUpLeft(2);
    d1.setUpRight(2);
    mvmt1.setDirections(d1);
    p1.setMovement(mvmt1);
    pd[0] = p1;
    p2.setType("grenadier");
    p2.setAttackPower(10);
    p2.setCount(2);
    Movement mvmt2 = new Movement();
    Shape s1 = new Shape();
    s1.setType(ShapeType.lshape);
    mvmt2.setShape(s1);
    p2.setMovement(mvmt2);
    pd[1] = p2;
    p3.setType("general");
    p3.setAttackPower(20);
    p3.setCount(2);
    Movement mvmt3 = new Movement();
    Directions d2 = new Directions();
    d2.setDown(2);
    d2.setLeft(2);
    d2.setRight(2);
    d2.setUp(2);
    mvmt3.setDirections(d2);
    p3.setMovement(mvmt3);
    pd[2] = p3;
    mapTemplate.setPieces(pd);
    mapTemplate.setGridSize(size);
    mapTemplate.setTeams(2);
    mapTemplate.setFlags(2);
    mapTemplate.setBlocks(3);
    mapTemplate.setPlacement(PlacementType.spaced_out);
    mapTemplate.setTotalTimeLimitInSeconds(1200);
    mapTemplate.setMoveTimeLimitInSeconds(300);
    MapModel mapModel = MapModelMapper.toMapsModel(mapTemplate, "1");

    // Assertions to verify conversion is correct
    assertEquals("1", mapModel.getCreatedBy());
    assertEquals(mapTemplate.getBlocks(), mapModel.getBlocks());
    assertEquals(mapTemplate.getFlags(), mapModel.getFlags());
    assertArrayEquals(mapTemplate.getGridSize(), mapModel.getGridSize());
    assertEquals(mapTemplate.getPlacement().toString(), mapModel.getPlacement());
    assertEquals(mapTemplate.getMoveTimeLimitInSeconds(), mapModel.getMoveTimeLimitInSeconds());
    assertEquals(mapTemplate.getTeams(), mapModel.getTeams());
    assertEquals(mapTemplate.getTotalTimeLimitInSeconds(), mapModel.getTotalTimeLimitInSeconds());
    assertEquals(3, mapModel.getPieces().length);
  }
}

