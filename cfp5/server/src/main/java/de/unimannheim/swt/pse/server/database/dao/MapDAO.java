package de.unimannheim.swt.pse.server.database.dao;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import de.unimannheim.swt.pse.server.database.controller.UserController;
import de.unimannheim.swt.pse.server.database.model.MapModel;
import de.unimannheim.swt.pse.server.database.model.MapModel.MapPieceDescription;
import de.unimannheim.swt.pse.server.database.model.UserModel;
import de.unimannheim.swt.pse.server.database.service.UserService;
import java.util.concurrent.CompletableFuture;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Data Access Object (DAO) for handling all map-related data interactions with Firebase. This class
 * is responsible for performing CRUD operations on maps but should not be used directly. Instead,
 * all interactions should be handled through its corresponding controller to ensure data integrity
 * and proper encapsulation within the application's architecture. Direct usage of this DAO outside
 * of its controller is strongly discouraged.
 */
public class MapDAO {

  /**
   * Reference to the Firebase Realtime Database node
   */
  private final DatabaseReference databaseReference;

  /**
   * Constructor for MapDAO that initializes the DatabaseReference to the "maps" node in Firebase.
   */
  public MapDAO() {
    this.databaseReference = FirebaseDatabase.getInstance().getReference("maps");
  }

  /**
   * Adds a new map to Firebase under a specific user ID.
   *
   * @param map    The MapModel object to add.
   * @param userId The user ID of the map's creator.
   * @return CompletableFuture that completes when the map is successfully added to Firebase.
   * @throws RuntimeException if there is a Firebase error during the map addition.
   * @author ohandsch
   */
  public CompletableFuture<Void> addMap(MapModel map, String userId)
      throws ExecutionException, InterruptedException {
    CompletableFuture<Void> future = new CompletableFuture<>();
    // Generate a unique key for the map
    String mapId = databaseReference.push().getKey();

    // Set the 'createdBy' field in the map to the userId
    map.setCreatedBy(userId);
    map.setMapId(mapId);
    UserDAO userDAO = new UserDAO();
    UserService userService = new UserService(userDAO);
    UserController userController = new UserController(userService);
    try {
      System.out.println("Fetching user by ID: " + userId);
      CompletableFuture<UserModel> userFuture = userController.getUserById(userId);
      System.out.println("User future created");
      UserModel user = userFuture.get(); // Blocking call to wait for user retrieval
      System.out.println("User fetched successfully: " + user);
      System.out.println("User ID: " + user.getUid());
      user.addMap(mapId);
      userController.updateUser(user);

    } catch (InterruptedException | ExecutionException e) {
      System.err.println("Error during user retrieval: " + e.getMessage());

    }

    // Save the map to the database with the 'createdBy' field
    databaseReference.child(mapId).setValue(map.toMap(), (databaseError, databaseReference) -> {
      if (databaseError != null) {
        future.completeExceptionally(databaseError.toException());
      } else {
        future.complete(null);
      }
    });

    return future;
  }

  /**
   * Deletes a map from Firebase based on the map ID.
   *
   * @param mapId The unique identifier of the map to delete.
   * @return CompletableFuture that completes when the map is successfully deleted from Firebase.
   * @throws RuntimeException if there is a Firebase error during the map deletion.
   * @author ohandsch
   */
  public CompletableFuture<Void> deleteMap(String mapId) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    databaseReference.child(mapId).removeValue((databaseError, databaseReference) -> {
      if (databaseError != null) {
        future.completeExceptionally(databaseError.toException());
      } else {
        future.complete(null);
      }
    });
    return future;
  }

  /**
   * Retrieves all maps created by a specific user from Firebase.
   *
   * @param userId The user ID whose maps are to be retrieved.
   * @return CompletableFuture containing a list of MapModels.
   * @throws RuntimeException if there is a Firebase error during the retrieval.
   * @author ohandsch
   */
  public CompletableFuture<List<MapModel>> getAllMapsByUser(String userId) {

    CompletableFuture<List<MapModel>> future = new CompletableFuture<>();
    try {
      UserDAO dao = new UserDAO();
      CompletableFuture<UserModel> future1 = dao.getUserById(userId);
      future1.get();
    } catch (Exception e) {
      System.err.println("Error retrieving user: " + e.getMessage());
      future.completeExceptionally(e);
      return future;
    }

    List<CompletableFuture<MapModel>> mapFutures = new ArrayList<>();
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

    userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          if (dataSnapshot.hasChild("maps")) {
            for (DataSnapshot mapSnapshot : dataSnapshot.child("maps").getChildren()) {
              String mapId = mapSnapshot.getValue().toString();
              CompletableFuture<MapModel> mapFuture = getMapById(mapId);
              mapFutures.add(mapFuture);
            }

            CompletableFuture.allOf(mapFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> mapFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()))
                .thenAccept(future::complete)
                .exceptionally(ex -> {
                  future.completeExceptionally(ex);
                  return null;
                });
          } else {
            future.complete(new ArrayList<>());
          }
        } else {
          future.completeExceptionally(new Exception("User not found"));
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        future.completeExceptionally(databaseError.toException());
      }
    });

    return future;
  }


  /**
   * Retrieves a single map from Firebase based on its ID.
   *
   * @param mapId The unique identifier of the map to retrieve.
   * @return CompletableFuture containing the MapModel if found.
   * @throws RuntimeException if there is a Firebase error during the retrieval.
   * @author ohandsch
   */
  public CompletableFuture<MapModel> getMapById(String mapId) {
    System.out.println("Fetching map by ID: " + mapId);
    CompletableFuture<MapModel> future = new CompletableFuture<>();
    DatabaseReference mapRef = databaseReference.child(mapId);
    mapRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        System.out.println("DataSnapshot exists: " + dataSnapshot.exists());
        if (dataSnapshot.exists()) {

          //create MapModel from database data
          MapModel map = new MapModel();
          map.setMapId(dataSnapshot.getKey());
          map.setCreatedBy(dataSnapshot.child("createdBy").getValue().toString());
          map.setBlocks(Integer.parseInt(dataSnapshot.child("blocks").getValue().toString()));
          map.setFlags(Integer.parseInt(dataSnapshot.child("flags").getValue().toString()));
          List<Object> gridSize = (List<Object>) (dataSnapshot.child("gridSize").getValue());
          int[] size = new int[2];
          size[0] = Integer.parseInt(gridSize.get(0).toString());
          size[1] = Integer.parseInt(gridSize.get(1).toString());
          map.setGridSize(size);
          map.setMoveTimeLimitInSeconds(
              Integer.parseInt(dataSnapshot.child("moveTimeLimitInSeconds").getValue().toString()));
          map.setTotalTimeLimitInSeconds(
              Integer.parseInt(
                  dataSnapshot.child("totalTimeLimitInSeconds").getValue().toString()));
          map.setPlacement(dataSnapshot.child("placement").getValue().toString());
          map.setTeams(Integer.parseInt(dataSnapshot.child("teams").getValue().toString()));
          List<MapPieceDescription> pieces = new ArrayList<>();
          for (DataSnapshot pieceSnapshot : dataSnapshot.child("pieces").getChildren()) {
            MapPieceDescription piece = new MapPieceDescription();
            piece.setAttackPower(
                Integer.parseInt(pieceSnapshot.child("attackPower").getValue().toString()));
            piece.setCount(Integer.parseInt(pieceSnapshot.child("count").getValue().toString()));
            piece.setType(pieceSnapshot.child("type").getValue().toString());
            MapModel.MapMovement movement = new MapModel.MapMovement();
            MapModel.MapsDirections directions = new MapModel.MapsDirections();
            DataSnapshot directionSnapshot = pieceSnapshot.child("movement").child("directions");
            directions.setDown(
                Integer.parseInt(directionSnapshot.child("down").getValue().toString()));
            directions.setDownLeft(
                Integer.parseInt(directionSnapshot.child("downLeft").getValue().toString()));
            directions.setLeft(
                Integer.parseInt(directionSnapshot.child("left").getValue().toString()));
            directions.setRight(
                Integer.parseInt(directionSnapshot.child("right").getValue().toString()));
            directions.setUp(Integer.parseInt(directionSnapshot.child("up").getValue().toString()));
            directions.setUpLeft(
                Integer.parseInt(directionSnapshot.child("upLeft").getValue().toString()));
            directions.setUpRight(
                Integer.parseInt(directionSnapshot.child("upRight").getValue().toString()));
            directions.setDownRight(
                Integer.parseInt(directionSnapshot.child("downRight").getValue().toString()));
            movement.setDirections(directions);
            MapModel.MapShape shape = new MapModel.MapShape();
            shape.setType(
                pieceSnapshot.child("movement").child("shape").child("type").getValue().toString());
            movement.setShape(shape);
            piece.setMovement(movement);
            pieces.add(piece);
          }
          map.setPieces(pieces.toArray(new MapPieceDescription[0]));
          System.out.println("Map retrieved: " + map);

          future.complete(map);
        } else {
          future.completeExceptionally(new Exception("Map not found with ID: " + mapId));
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        future.completeExceptionally(databaseError.toException());
      }
    });

    return future;
  }
}
