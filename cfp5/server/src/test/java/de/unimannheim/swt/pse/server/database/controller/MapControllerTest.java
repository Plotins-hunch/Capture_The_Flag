package de.unimannheim.swt.pse.server.database.controller;

import static org.junit.jupiter.api.Assertions.*;

import de.unimannheim.swt.pse.server.database.DatabaseInitializer;
import de.unimannheim.swt.pse.server.database.dao.MapDAO;
import de.unimannheim.swt.pse.server.database.dao.UserDAO;
import de.unimannheim.swt.pse.server.database.model.UserModel;
import de.unimannheim.swt.pse.server.database.service.MapService;
import de.unimannheim.swt.pse.server.database.service.UserService;
import de.unimannheim.swt.pse.server.game.map.Directions;
import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import de.unimannheim.swt.pse.server.game.map.Movement;
import de.unimannheim.swt.pse.server.game.map.PieceDescription;
import de.unimannheim.swt.pse.server.game.map.PlacementType;
import de.unimannheim.swt.pse.server.game.map.Shape;
import de.unimannheim.swt.pse.server.game.map.ShapeType;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MapControllerTest {

  private MapController mapController;
  private static MapTemplate mapTemplate1;
  private static MapTemplate mapTemplate2;
  private static MapTemplate mapTemplate3;

  @BeforeAll
  public static void setupClass() {
    try {
      new DatabaseInitializer().initialize(); // Initialize Firebase
      mapTemplate1 = new MapTemplate();
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

      mapTemplate1.setPieces(pd);
      mapTemplate1.setGridSize(size);
      mapTemplate1.setTeams(2);
      mapTemplate1.setFlags(2);
      mapTemplate1.setBlocks(3);
      mapTemplate1.setPlacement(PlacementType.spaced_out);
      mapTemplate1.setTotalTimeLimitInSeconds(1200);
      mapTemplate1.setMoveTimeLimitInSeconds(300);

      mapTemplate2 = new MapTemplate();
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

      mapTemplate2.setPieces(pd2);
      mapTemplate2.setGridSize(size2);
      mapTemplate2.setTeams(2);
      mapTemplate2.setFlags(1);
      mapTemplate2.setBlocks(0);
      mapTemplate2.setPlacement(PlacementType.symmetrical);
      mapTemplate2.setTotalTimeLimitInSeconds(1000);
      mapTemplate2.setMoveTimeLimitInSeconds(30);

      mapTemplate3 = new MapTemplate();
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
      mapTemplate3.setPieces(pd3);
      mapTemplate3.setGridSize(size3);
      mapTemplate3.setTeams(4);
      mapTemplate3.setFlags(2);
      mapTemplate3.setBlocks(4);
      mapTemplate3.setPlacement(PlacementType.defensive);
      mapTemplate3.setTotalTimeLimitInSeconds(1200);
      mapTemplate3.setMoveTimeLimitInSeconds(30);

      UserDAO userDAO = new UserDAO();
      UserService userService = new UserService(userDAO);
      UserController userController = new UserController(userService);

      UserModel user1 = new UserModel("testUser1", "testUser1", "test1@example.com", 0);
      CompletableFuture<Void> future1 = userController.createUser(user1);
      future1.get();

      UserModel user2 = new UserModel("testUser2", "testUser2", "test2@example.com", 0);
      CompletableFuture<Void> future2 = userController.createUser(user2);
      future2.get();

      UserModel user3 = new UserModel("testUser3", "testUser3", "test3@example.com", 0);
      CompletableFuture<Void> future3 = userController.createUser(user3);
      future3.get();

      UserModel user4 = new UserModel("testUser4", "testUser4", "test4@example.com", 0);
      CompletableFuture<Void> future4 = userController.createUser(user4);
      future4.get();

    } catch (IOException e) {
      System.out.println("Error initializing Firebase");
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @BeforeEach
  public void setup() {
    MapDAO mapDAO = new MapDAO();
    MapService mapService = new MapService(mapDAO);
    mapController = new MapController(mapService);
  }

  @Test
  public void testCreateMap_ValidMapTemplate_Success()
      throws ExecutionException, InterruptedException {
    CompletableFuture<Void> future = mapController.createMap(mapTemplate1, "testUser1");
    future.get();

    // Verify that the map is created successfully
    CompletableFuture<List<MapTemplate>> getAllFuture = mapController.getAllMapsByUser("testUser1");
    List<MapTemplate> maps = getAllFuture.get();
    assertEquals(4, maps.size());
  }

  @Test
  public void testCreateMap_NullMapTemplate_ThrowsException() {
    assertThrows(NullPointerException.class, () -> mapController.createMap(null, "testUser2"));
  }

  @Test
  public void testCreateMap_NullUserId_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> mapController.createMap(mapTemplate2, null));
  }

  @Test
  public void testCreateMap_EmptyUserId_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> mapController.createMap(mapTemplate3, ""));
  }

  @Test
  public void testDeleteMap_NullMapId_ThrowsException() {
    assertThrows(NullPointerException.class, () -> mapController.deleteMap(null));
  }

  @Test
  public void testDeleteMap_EmptyMapId_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> mapController.deleteMap(""));
  }

  @Test
  public void testGetAllMapsByUser_ExistingUser_ReturnsListOfMaps()
      throws ExecutionException, InterruptedException {

    CompletableFuture<Void> createFuture1 = mapController.createMap(mapTemplate1, "testUser4");
    createFuture1.get();
    System.out.println("here");

    CompletableFuture<List<MapTemplate>> getAllFuture = mapController.getAllMapsByUser("testUser4");
    System.out.println("here2");
    List<MapTemplate> maps = getAllFuture.get();

    assertEquals(4, maps.size());
  }

  @Test
  public void testGetAllMapsByUser_NonExistingUser_ThrowsException() {
    assertThrows(ExecutionException.class,
        () -> mapController.getAllMapsByUser("nonExistingUser").get());
  }

  @Test
  public void testGetAllMapsByUser_NullUserId_ThrowsException() {
    assertThrows(NullPointerException.class, () -> mapController.getAllMapsByUser(null));
  }

  @Test
  public void testGetAllMapsByUser_EmptyUserId_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> mapController.getAllMapsByUser(""));
  }

  @AfterAll
  public static void tearDownClass() throws ExecutionException, InterruptedException {
    // Initialize the UserController with the necessary dependencies
    UserDAO userDAO = new UserDAO();
    UserService userService = new UserService(userDAO);
    UserController userController = new UserController(userService);

    CompletableFuture<Void> deleteFuture1 = userController.deleteUser("testUser1");
    deleteFuture1.get();
    CompletableFuture<Void> deleteFuture2 = userController.deleteUser("testUser");
    deleteFuture2.get();
    CompletableFuture<Void> deleteFuture3 = userController.deleteUser("testUser2");
    deleteFuture3.get();
    CompletableFuture<Void> deleteFuture4 = userController.deleteUser("testUser3");
    deleteFuture4.get();
    CompletableFuture<Void> deleteFuture5 = userController.deleteUser("testUser4");
    deleteFuture5.get();
  }
}