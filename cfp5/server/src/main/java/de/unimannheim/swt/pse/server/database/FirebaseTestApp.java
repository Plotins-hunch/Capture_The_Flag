package de.unimannheim.swt.pse.server.database;

import de.unimannheim.swt.pse.server.authentication.AuthController;
import de.unimannheim.swt.pse.server.database.controller.GameSessionsController;
import de.unimannheim.swt.pse.server.database.controller.MapController;
import de.unimannheim.swt.pse.server.database.controller.StatsController;
import de.unimannheim.swt.pse.server.database.controller.UserController;
import de.unimannheim.swt.pse.server.database.dao.GameSessionsDAO;
import de.unimannheim.swt.pse.server.database.dao.MapDAO;
import de.unimannheim.swt.pse.server.database.dao.StatsDAO;
import de.unimannheim.swt.pse.server.database.dao.UserDAO;
import de.unimannheim.swt.pse.server.database.model.GameRecord;
import de.unimannheim.swt.pse.server.database.model.GameSessionsModel;
import de.unimannheim.swt.pse.server.database.model.MapModel;
import de.unimannheim.swt.pse.server.database.model.StatsModel;
import de.unimannheim.swt.pse.server.database.model.UserModel;
import de.unimannheim.swt.pse.server.database.service.GameSessionsService;
import de.unimannheim.swt.pse.server.database.service.MapService;
import de.unimannheim.swt.pse.server.database.service.StatsService;
import de.unimannheim.swt.pse.server.database.service.UserService;
import de.unimannheim.swt.pse.server.game.map.Directions;
import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import de.unimannheim.swt.pse.server.game.map.Movement;
import de.unimannheim.swt.pse.server.game.map.PieceDescription;
import de.unimannheim.swt.pse.server.game.map.PlacementType;
import de.unimannheim.swt.pse.server.game.map.Shape;
import de.unimannheim.swt.pse.server.game.map.ShapeType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 * This class is used to manually set values in the database
 */
public class FirebaseTestApp {

  public static void signUpSynchronously(String username, String email, String password,
      String gender) {
    UserDAO userDAO = new UserDAO();
    AuthController authController = new AuthController(userDAO);

    // Perform sign-up synchronously
    try {
      CompletableFuture<UserModel> signUpFuture = authController.signUp(email, password, username,
          gender);
      UserModel newUser = signUpFuture.get(); // Blocking call to wait for sign-up completion
      System.out.println("User signed up successfully with UID: " + newUser.getUid());
    } catch (InterruptedException | ExecutionException e) {
      System.err.println("Error during user sign-up: " + e.getMessage());
    }
  }

  public static boolean signInSynchronously(String email, String password) {
    // Instantiate AuthService and AuthController
    UserDAO userDAO = new UserDAO();
    AuthController authController = new AuthController(userDAO);

    // Perform sign-in synchronously
    try {
      CompletableFuture<UserModel> signInFuture = authController.signIn(email, password);
      UserModel user = signInFuture.get(); // Blocking call to wait for sign-in completion
      System.out.println("User signed in successfully with UID: " + user.getUid());
      return true;
    } catch (InterruptedException | ExecutionException e) {
      System.err.println("Error during user sign-in: " + e.getMessage());
      return false;
    }
  }

  public static void updateStatsSynchronously(String userId) {
    // Instantiate StatsService and StatsController
    StatsDAO statsDAO = new StatsDAO();
    StatsService statsService = new StatsService(statsDAO);
    StatsController statsController = new StatsController(statsService);

    StatsModel stats = new StatsModel(userId);
    stats.setGamesWon(20); // Set number of games won
    stats.setGamesLost(10); // Set number of games lost
    // Create game records and add them to the stats model
    List<GameRecord> gameRecords = new ArrayList<>();
    GameRecord gameRecord1 = new GameRecord("gameId1", "opponentId1", true);
    GameRecord gameRecord2 = new GameRecord("gameId2", "opponentId2", false);
    gameRecords.add(gameRecord1);
    gameRecords.add(gameRecord2);
    stats.setGameRecords(gameRecords);// Perform statistics update synchronously
    try {
      CompletableFuture<Void> updateFuture = statsController.updateStats(stats);
      updateFuture.get(); // Blocking call to wait for update completion
      System.out.println("Statistics updated successfully");
    } catch (InterruptedException | ExecutionException e) {
      System.err.println("Error during statistics update: " + e.getMessage());
    }
  }

  public static void createUserSynchronously(String userId, String username, String email) {
    // Instantiate UserDAO and UserService
    UserDAO userDAO = new UserDAO();
    System.out.println("UserDAO instantiated");
    UserService userService = new UserService(userDAO);
    System.out.println("UserService instantiated");
    UserController userController = new UserController(userService);
    System.out.println("UserController instantiated");

    // Create a test user
    UserModel testUser = new UserModel(userId, username, email, 1);
    ArrayList<String> maps = new ArrayList<>();
    testUser.setMaps(maps);
    System.out.println("Test user created");
    CompletableFuture<Void> future = userController.createUser(testUser);
    try {
      future.join(); // Wait for completion
      System.out.println("User added to database");
    } catch (CompletionException e) {
      System.err.println("Exception during user creation: " + e.getCause().getMessage());
    }
  }

  public static void getUserByIdSynchronously(String userId) {
    // Instantiate UserDAO and UserService
    UserDAO userDAO = new UserDAO();
    UserService userService = new UserService(userDAO);
    UserController userController = new UserController(userService);

    // Retrieve user by ID
    try {
      System.out.println("Fetching user by ID: " + userId);
      CompletableFuture<UserModel> userFuture = userController.getUserById(userId);
      System.out.println("User future created");
      UserModel user = userFuture.get(); // Blocking call to wait for user retrieval
      System.out.println("User fetched successfully: " + user);
      System.out.println("User ID: " + user.getUid());

    } catch (InterruptedException | ExecutionException e) {
      System.err.println("Error during user retrieval: " + e.getMessage());

    }
  }

  public static void updateUserSynchronously(String userId, String username, String email) {
    // Instantiate UserDAO and UserService
    UserDAO userDAO = new UserDAO();
    UserService userService = new UserService(userDAO);
    UserController userController = new UserController(userService);

    // Create a test user
    UserModel testUser = new UserModel(userId, username, email, 1);
    ArrayList<String> maps = new ArrayList<>();
    maps.add("map1");
    testUser.setMaps(maps);

    // Update user information
    CompletableFuture<Void> future = userController.updateUser(testUser);
    try {
      future.join(); // Wait for completion
      System.out.println("User information updated");
    } catch (CompletionException e) {
      System.err.println("Exception during user update: " + e.getCause().getMessage());
    }
  }

  public static void deleteUserSynchronously(String userId) {
    // Instantiate UserDAO and UserService
    UserDAO userDAO = new UserDAO();
    UserService userService = new UserService(userDAO);
    UserController userController = new UserController(userService);

    // Delete user by ID
    CompletableFuture<Void> future = userController.deleteUser(userId);
    try {
      future.join(); // Wait for completion
      System.out.println("User deleted successfully");
    } catch (CompletionException e) {
      System.err.println("Exception during user deletion: " + e.getCause().getMessage());
    }
  }

  public static void updateLeaderboardSynchronously(String userId, boolean gameResult) {
    // Instantiate StatsService and StatsController
    StatsDAO statsDAO = new StatsDAO();
    StatsService statsService = new StatsService(statsDAO);
    StatsController statsController = new StatsController(statsService);

    // Update leaderboard for a specific user
    CompletableFuture<Void> future = statsController.updateLeaderboard(userId, gameResult);
    try {
      future.join(); // Wait for completion
      System.out.println("Leaderboard updated successfully for user " + userId);
    } catch (CompletionException e) {
      System.err.println("Exception during leaderboard update: " + e.getCause().getMessage());
    }
  }

  public static void getLeaderboardSynchronously(int topN) {
    // Instantiate StatsService and StatsController
    StatsDAO statsDAO = new StatsDAO();
    StatsService statsService = new StatsService(statsDAO);
    StatsController statsController = new StatsController(statsService);

    // Retrieve top N users from the leaderboard
    try {
      CompletableFuture<List<StatsModel>> leaderboardFuture = statsController.getLeaderboard(topN);
      List<StatsModel> leaderboard = leaderboardFuture.get(); // Blocking call to wait for leaderboard retrieval
      System.out.println("Leaderboard fetched successfully: " + leaderboard);
    } catch (InterruptedException | ExecutionException e) {
      System.err.println("Error during leaderboard retrieval: " + e.getMessage());
    }
  }

  public static void createMapSynchronously(String userId) {
    // Instantiate MapService and MapController
    MapDAO mapDAO = new MapDAO();
    MapService mapService = new MapService(mapDAO);
    MapController mapController = new MapController(mapService);

    // Create a test map
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

    // Create the map
    CompletableFuture<Void> future = mapController.createMap(sampleMap3, userId);
    try {
      future.join(); // Wait for completion
      System.out.println("Map created successfully");
    } catch (CompletionException e) {
      System.err.println("Exception during map creation: " + e.getCause().getMessage());
    }
  }

  public static void getAllMapsByUserSynchronously(String userId) {
    // Instantiate MapService and MapController
    MapDAO mapDAO = new MapDAO();
    MapService mapService = new MapService(mapDAO);
    MapController mapController = new MapController(mapService);

    // Retrieve all maps created by a specific user
    try {
      CompletableFuture<List<MapTemplate>> mapsFuture = mapController.getAllMapsByUser(userId);
      List<MapTemplate> maps = mapsFuture.get(); // Blocking call to wait for map retrieval
      System.out.println("Maps fetched successfully: " + maps);
    } catch (InterruptedException | ExecutionException e) {
      System.err.println("Error during map retrieval: " + e.getMessage());
    }
  }

  public static void getMapByIdSynchronously(String mapId) {
    // Instantiate MapDAO and MapService
    MapDAO mapDAO = new MapDAO();
    MapService mapService = new MapService(mapDAO);
    MapController mapController = new MapController(mapService);

    // Retrieve map by ID
    try {
      CompletableFuture<MapModel> mapFuture = mapDAO.getMapById(mapId);
      MapModel map = mapFuture.get(); // Blocking call to wait for map retrieval
      System.out.println(map.getMapId());
      System.out.println("Map fetched successfully: " + map);
    } catch (InterruptedException | ExecutionException e) {
      System.err.println("Error during map retrieval: " + e.getMessage());
    }
  }

  public static void addGameSessionSynchronously(String sessionId, String ip, boolean isFull) {
    // Instantiate GameSessionsModel and GameSessionsController
    GameSessionsModel session = new GameSessionsModel(sessionId, ip, isFull);
    GameSessionsDAO gameSessionsDAO = new GameSessionsDAO();
    GameSessionsService gameSessionsService = new GameSessionsService(gameSessionsDAO);
    GameSessionsController gameSessionsController = new GameSessionsController(gameSessionsService);

    // Add a new game session
    CompletableFuture<String> future = gameSessionsController.addGameSession(session);
    try {
      String newSessionId = future.get(); // Blocking call to wait for session addition
      System.out.println("Game session added successfully with ID: " + newSessionId);
    } catch (InterruptedException | ExecutionException e) {
      System.err.println("Error during game session addition: " + e.getMessage());
    }
  }

  public static void deleteGameSessionSynchronously(String sessionId) {
    // Instantiate GameSessionsController
    GameSessionsDAO gameSessionsDAO = new GameSessionsDAO();
    GameSessionsService gameSessionsService = new GameSessionsService(gameSessionsDAO);
    GameSessionsController gameSessionsController = new GameSessionsController(gameSessionsService);

    // Delete a game session
    CompletableFuture<Void> future = gameSessionsController.deleteGameSession(sessionId);
    try {
      future.get(); // Blocking call to wait for session deletion
      System.out.println("Game session deleted successfully with ID: " + sessionId);
    } catch (InterruptedException | ExecutionException e) {
      System.err.println("Error during game session deletion: " + e.getMessage());
    }
  }

  public static void getAllGameSessionsSynchronously() {
    // Instantiate GameSessionsController
    GameSessionsDAO gameSessionsDAO = new GameSessionsDAO();
    GameSessionsService gameSessionsService = new GameSessionsService(gameSessionsDAO);
    GameSessionsController gameSessionsController = new GameSessionsController(gameSessionsService);

    // Retrieve all game sessions
    try {
      CompletableFuture<List<GameSessionsModel>> sessionsFuture = gameSessionsController.getAllCurrentGameSessions();
      List<GameSessionsModel> sessions = sessionsFuture.get(); // Blocking call to wait for session retrieval
      for (GameSessionsModel session : sessions) {
        System.out.println("Session ID: " + session.getSessionId());
        System.out.println("IP: " + session.getIp());
        System.out.println("Is full: " + session.isFull());
      }
      System.out.println("Game sessions fetched successfully: " + sessions);
    } catch (InterruptedException | ExecutionException e) {
      System.err.println("Error during game session retrieval: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    try {
      new DatabaseInitializer().initialize(); // Initialize Firebase
    } catch (IOException e) {
      System.err.println("Error initializing Firebase: " + e.getMessage());
    }
    // Add new user to the database
    //createUserSynchronously("AnitaMaxWynn", "AnitaMaxWynn", "drake@drake.com");
    System.out.println("finished");
  }
}

