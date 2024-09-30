package de.unimannheim.swt.pse.server.controller;

import de.unimannheim.swt.pse.server.game.GameEngineGame;
import de.unimannheim.swt.pse.server.game.map.Directions;
import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import de.unimannheim.swt.pse.server.game.map.Movement;
import de.unimannheim.swt.pse.server.game.map.PieceDescription;
import de.unimannheim.swt.pse.server.game.map.PlacementType;
import de.unimannheim.swt.pse.server.game.map.Shape;
import de.unimannheim.swt.pse.server.game.map.ShapeType;
import de.unimannheim.swt.pse.server.game.state.Team;
import org.junit.jupiter.api.Test;

public class GameEngineGameIntegrationTest {

  //Integration test for Sample Map 1
  @Test
  void testSampleMap1() {
    GameEngineGame sample1Game = new GameEngineGame();

    MapTemplate sampleMap = new MapTemplate();
    int[] size = {10, 10};
    PieceDescription[] pd = new PieceDescription[5];
    PieceDescription p1 = new PieceDescription();
    PieceDescription p2 = new PieceDescription();
    PieceDescription p3 = new PieceDescription();
    PieceDescription pM4 = new PieceDescription();
    PieceDescription pM5 = new PieceDescription();
    p1.setType("water_frigate");
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
    p2.setType("water_jesus");
    p2.setAttackPower(10);
    p2.setCount(2);
    Movement mvmt2 = new Movement();
    Shape s1 = new Shape();
    s1.setType(ShapeType.lshape);
    mvmt2.setShape(s1);
    p2.setMovement(mvmt2);
    pd[1] = p2;
    p3.setType("water_mermaid");
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
    pM4.setType("water_flyboat");
    pM4.setAttackPower(8);
    pM4.setCount(2);
    Movement movmt4 = new Movement();
    Directions di3 = new Directions();
    di3.setDown(2);
    di3.setUpRight(1);
    di3.setUpLeft(3);
    movmt4.setDirections(di3);
    pM4.setMovement(movmt4);
    pd[3] = pM4;
    pM5.setType("water_octopus");
    pM5.setAttackPower(8);
    pM5.setCount(1);
    pM5.setMovement(movmt4);
    pd[4] = pM5;

    sampleMap.setPieces(pd);
    sampleMap.setGridSize(size);
    sampleMap.setTeams(2);
    sampleMap.setFlags(2);
    sampleMap.setBlocks(3);
    sampleMap.setPlacement(PlacementType.spaced_out);
    sampleMap.setTotalTimeLimitInSeconds(1200);
    sampleMap.setMoveTimeLimitInSeconds(300);

    sample1Game.create(sampleMap);
    Team startingTeam = sample1Game.joinGame(1 + "");
    startingTeam = sample1Game.joinGame(2 + "");
    TestClient testClient = new TestClient();
    testClient.printGameState(sample1Game.getCurrentGameState());

  }

  //Integration test for Sample Map 2
  @Test
  void testSampleMap2() {
    GameEngineGame sample2Game = new GameEngineGame();

    MapTemplate sampleMap2 = new MapTemplate();
    int[] size2 = {10, 12};
    PieceDescription[] pd2 = new PieceDescription[9];
    PieceDescription p4 = new PieceDescription();
    PieceDescription p5 = new PieceDescription();
    PieceDescription p6 = new PieceDescription();
    PieceDescription p7 = new PieceDescription();
    PieceDescription p8 = new PieceDescription();
    PieceDescription p9 = new PieceDescription();
    PieceDescription p10 = new PieceDescription();
    PieceDescription p11 = new PieceDescription();
    PieceDescription p12 = new PieceDescription();
    p4.setType("infantry");
    p4.setAttackPower(5);
    p4.setCount(1);
    Movement mvmt4 = new Movement();
    Directions d3 = new Directions();
    d3.setDown(2);
    d3.setUpLeft(2);
    d3.setUpRight(2);
    mvmt4.setDirections(d3);
    p4.setMovement(mvmt4);
    pd2[0] = p4;
    p5.setType("grenadier");
    p5.setAttackPower(10);
    p5.setCount(1);
    Movement mvmt5 = new Movement();
    Shape s2 = new Shape();
    s2.setType(ShapeType.lshape);
    mvmt5.setShape(s2);
    p5.setMovement(mvmt5);
    pd2[1] = p5;
    p6.setType("general");
    p6.setAttackPower(20);
    p6.setCount(1);
    Movement mvmt6 = new Movement();
    Directions d4 = new Directions();
    d4.setDown(2);
    d4.setLeft(2);
    d4.setRight(2);
    d4.setUp(2);
    mvmt6.setDirections(d4);
    p6.setMovement(mvmt6);
    pd2[2] = p6;
    p7.setType("cavalry");
    p7.setAttackPower(15);
    p7.setCount(1);
    Movement mvmt7 = new Movement();
    Shape s3 = new Shape();
    s3.setType(ShapeType.lshape);
    mvmt7.setShape(s3);
    p7.setMovement(mvmt7);
    pd2[3] = p7;
    p8.setType("dragonfighter");
    p8.setAttackPower(25);
    p8.setCount(1);
    Movement mvmt8 = new Movement();
    Directions d5 = new Directions();
    d5.setDown(2);
    d5.setLeft(2);
    d5.setRight(2);
    d5.setUp(2);
    d5.setUpLeft(2);
    d5.setUpRight(2);
    d5.setDownLeft(2);
    d5.setDownRight(2);
    mvmt8.setDirections(d5);
    p8.setMovement(mvmt8);
    pd2[4] = p8;
    p9.setType("musketeer");
    p9.setAttackPower(10);
    p9.setCount(1);
    Movement mvmt9 = new Movement();
    Shape s4 = new Shape();
    s4.setType(ShapeType.lshape);
    mvmt9.setShape(s4);
    p9.setMovement(mvmt9);
    pd2[5] = p9;
    p10.setType("pikenier");
    p10.setAttackPower(15);
    p10.setCount(1);
    Movement mvmt10 = new Movement();
    Directions d6 = new Directions();
    d6.setDown(2);
    d6.setUp(2);
    mvmt10.setDirections(d6);
    p10.setMovement(mvmt10);
    pd2[6] = p10;
    p11.setType("priest");
    p11.setAttackPower(5);
    p11.setCount(1);
    Movement mvmt11 = new Movement();
    Directions d7 = new Directions();
    d7.setDown(2);
    d7.setUp(2);
    mvmt11.setDirections(d7);
    p11.setMovement(mvmt11);
    pd2[7] = p11;
    p12.setType("scout");
    p12.setAttackPower(5);
    p12.setCount(1);
    Movement mvmt12 = new Movement();
    Directions d8 = new Directions();
    d8.setDown(2);
    d8.setUp(2);
    mvmt12.setDirections(d8);
    p12.setMovement(mvmt12);
    pd2[8] = p12;

    sampleMap2.setPieces(pd2);
    sampleMap2.setGridSize(size2);
    sampleMap2.setTeams(2);
    sampleMap2.setFlags(1);
    sampleMap2.setBlocks(0);
    sampleMap2.setPlacement(PlacementType.symmetrical);
    sampleMap2.setTotalTimeLimitInSeconds(1000);
    sampleMap2.setMoveTimeLimitInSeconds(30);

    sample2Game.create(sampleMap2);
    Team startingTeam = sample2Game.joinGame(1 + "");
    startingTeam = sample2Game.joinGame(2 + "");
    TestClient testClient = new TestClient();
    testClient.printGameState(sample2Game.getCurrentGameState());
  }


  //Integration test for Sample Map 3
  @Test
  void testSampleMap3() {
    GameEngineGame sample3Game = new GameEngineGame();

    MapTemplate sampleMap3 = new MapTemplate();
    int[] size3 = {10, 10};
    PieceDescription[] pd3 = new PieceDescription[3];
    PieceDescription p13 = new PieceDescription();
    PieceDescription p14 = new PieceDescription();
    PieceDescription p15 = new PieceDescription();
    p13.setType("general");
    p13.setAttackPower(20);
    p13.setCount(1);
    Movement mvmt13 = new Movement();
    Directions d9 = new Directions();
    d9.setDown(2);
    d9.setLeft(2);
    d9.setRight(2);
    d9.setUp(2);
    mvmt13.setDirections(d9);
    p13.setMovement(mvmt13);
    pd3[0] = p13;
    p14.setType("scout");
    p14.setAttackPower(5);
    p14.setCount(3);
    Movement mvmt14 = new Movement();
    Directions d10 = new Directions();
    d10.setDown(2);
    d10.setUp(5);
    mvmt14.setDirections(d10);
    p14.setMovement(mvmt14);
    pd3[1] = p14;
    p15.setType("artillery");
    p15.setAttackPower(20);
    p15.setCount(2);
    Movement mvmt15 = new Movement();
    Shape s5 = new Shape();
    s5.setType(ShapeType.lshape);
    mvmt15.setShape(s5);
    p15.setMovement(mvmt15);
    pd3[2] = p15;
    sampleMap3.setPieces(pd3);
    sampleMap3.setGridSize(size3);
    sampleMap3.setTeams(4);
    sampleMap3.setFlags(2);
    sampleMap3.setBlocks(4);
    sampleMap3.setPlacement(PlacementType.defensive);
    sampleMap3.setTotalTimeLimitInSeconds(1200);
    sampleMap3.setMoveTimeLimitInSeconds(30);

    sample3Game.create(sampleMap3);
    Team startingTeam = sample3Game.joinGame(1 + "");
    startingTeam = sample3Game.joinGame(2 + "");
    startingTeam = sample3Game.joinGame(3 + "");
    startingTeam = sample3Game.joinGame(4 + "");
    TestClient testClient = new TestClient();
    testClient.printGameState(sample3Game.getCurrentGameState());
  }
  

}
