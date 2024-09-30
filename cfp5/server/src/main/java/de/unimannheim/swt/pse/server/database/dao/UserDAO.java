package de.unimannheim.swt.pse.server.database.dao;


import com.google.firebase.database.*;
import de.unimannheim.swt.pse.server.database.model.UserModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.google.firebase.database.Query;
import java.util.Optional;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;

/**
 * Data Access Object (DAO) for user data stored in Firebase. This class handles direct interactions
 * with the Firebase database to perform CRUD operations on user data. It is intended to be used
 * through its corresponding controller. Direct usage of this class outside its corresponding
 * controller is discouraged to maintain design consistency and data integrity.
 */
public class UserDAO {

  /**
   * Reference to the Firebase database node
   */
  private final DatabaseReference databaseReference;

  /**
   * Constructs a new instance of the {@link UserDAO} class with a reference to the 'users' node in
   * the Firebase database.
   */
  public UserDAO() {
    this.databaseReference = FirebaseDatabase.getInstance().getReference("users");
  }

  /**
   * Adds or updates a user in the Firebase database.
   *
   * @param user the UserModel instance containing the user's data.
   * @return CompletableFuture that completes when the user is successfully added or updated in
   * Firebase.
   * @throws RuntimeException if there is a Firebase database error.
   * @author ohandsch
   */
  public CompletableFuture<Void> addUser(UserModel user) {
    System.out.println("Adding user to database: " + user);
    CompletableFuture<Void> future = new CompletableFuture<>();
    String userId = user.getUid();

    // Create a Map to store the user data
    Map<String, Object> userData = new HashMap<>();
    userData.put("uid", user.getUid());
    userData.put("username", user.getUsername());
    userData.put("email", user.getEmail());
    userData.put("gender", user.getGender());

    //add the default maps to the user
    user.addMap("sampleMap1");
    user.addMap("sampleMap2");
    user.addMap("sampleMap3");
    List<String> mapsArray = user.getMaps();
    userData.put("maps", mapsArray);

    databaseReference.child(userId).setValue(userData, (databaseError, databaseReference) -> {
      if (databaseError != null) {
        System.err.println("Firebase error: " + databaseError.getMessage());
        future.completeExceptionally(databaseError.toException());
      } else {
        System.out.println("User successfully added/updated");
        future.complete(null);
      }
    });

    return future;
  }


  /**
   * Retrieves a user by their unique ID from Firebase.
   *
   * @param userId the unique identifier of the user to retrieve.
   * @return CompletableFuture containing the UserModel if found.
   * @throws RuntimeException         if there is a Firebase database error during retrieval.
   * @throws IllegalArgumentException if the userId is not found or is null.
   * @author ohandsch
   */
  public CompletableFuture<UserModel> getUserById(String userId) {
    if (userId == null) {
      throw new IllegalArgumentException("User ID cannot be null");

    }
    CompletableFuture<UserModel> future = new CompletableFuture<>();
    databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        System.out.println("DataSnapshot key: " + dataSnapshot.getKey());
        System.out.println("DataSnapshot exists: " + dataSnapshot.exists());

        if (dataSnapshot.exists()) {
          //create UserModel from database data
          DataSnapshot uid = dataSnapshot.child("uid");
          DataSnapshot username = dataSnapshot.child("username");
          DataSnapshot email = dataSnapshot.child("email");
          DataSnapshot gender = dataSnapshot.child("gender");
          UserModel user = new UserModel();
          user.setUid(uid.getValue().toString());
          user.setUsername(username.getValue().toString());
          user.setEmail(email.getValue().toString());
          user.setGender(gender.getValue(Integer.class));
          if (dataSnapshot.hasChild("maps")) {
            DataSnapshot mapsSnapshot = dataSnapshot.child("maps");
            List<String> maps = (List<String>) mapsSnapshot.getValue();
            user.setMaps(maps);
          }
          future.complete(user);
        } else {
          System.out.println("User not found with ID: " + userId);
          future.completeExceptionally(new Exception("User not found"));
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        System.err.println("Error retrieving user by ID: " + databaseError.getMessage());
        future.completeExceptionally(databaseError.toException());
      }
    });
    return future;
  }


  /**
   * Updates a user's information in Firebase.
   *
   * @param user the UserModel instance containing the updated data.
   * @return CompletableFuture that completes when the user's data is updated in Firebase.
   * @throws RuntimeException if there is a Firebase database error during update.
   * @author ohandsch
   */
  public CompletableFuture<Void> updateUser(UserModel user) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    DatabaseReference userRef = databaseReference.child(user.getUid());

    // First check if the child exists
    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          // Proceed with the update if the child exists
          userRef.updateChildren(user.toMap(), (databaseError, dbReference) -> {
            if (databaseError != null) {
              future.completeExceptionally(databaseError.toException());
            } else {
              future.complete(null);
            }
          });
        } else {
          future.completeExceptionally(new Exception("No user found with ID: " + user.getUid()));
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
   * Deletes a user from Firebase based on their unique ID.
   *
   * @param userId the unique identifier of the user to delete.
   * @return CompletableFuture that completes when the user is successfully deleted from Firebase.
   * @throws RuntimeException if there is a Firebase database error during deletion.
   * @author ohandsch
   */
  public CompletableFuture<Void> deleteUser(String userId) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    databaseReference.child(userId).removeValue((databaseError, databaseReference) -> {
      if (databaseError != null) {
        future.completeExceptionally(databaseError.toException());
      } else {
        future.complete(null);
      }
    });
    return future;
  }

  /**
   * Adds a map to a user's list of maps in Firebase.
   *
   * @param userId the unique identifier of the user to add the map to.
   * @param mapId  the unique identifier of the map to add to the user's list.
   * @return CompletableFuture that completes when the map is successfully added to the user's list.
   * @throws RuntimeException         if there is a Firebase database error during the operation.
   * @throws IllegalArgumentException if the userId or mapId is null or empty.
   * @throws NullPointerException     if the user or map is not found in the database.
   * @author ohandsch
   */
  public CompletableFuture<Void> addMapToUser(String userId, String mapId) {
    CompletableFuture<Void> future = new CompletableFuture<>();

    try {
      CompletableFuture<UserModel> future1 = this.getUserById(userId);
      future1.get();
    } catch (Exception e) {
      System.err.println("Error retrieving user: " + e.getMessage());
      future.completeExceptionally(e);
      return future;
    }

    DatabaseReference mapsRef = databaseReference.child(userId).child("maps");

    mapsRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        // Check if the maps node exists
        if (dataSnapshot.exists()) {
          // If the maps list already exists, retrieve it, add the new map, and update
          List<String> currentMaps = (List<String>) dataSnapshot.getValue();
          if (!currentMaps.contains(mapId)) {
            currentMaps.add(mapId);
            mapsRef.setValue(currentMaps, (databaseError, dbReference) -> {
              if (databaseError != null) {
                System.err.println("Firebase error: " + databaseError.getMessage());
                future.completeExceptionally(databaseError.toException());
              } else {
                System.out.println("Map successfully added to user " + mapId);
                future.complete(null);
              }
            });
          } else {
            System.out.println("Map already exists for user");
            future.complete(null);
          }
        } else {
          // If the maps node does not exist, create it with the new map as the only entry
          List<String> newMaps = new ArrayList<>();
          newMaps.add(mapId);
          mapsRef.setValue(newMaps, (databaseError, dbReference) -> {
            if (databaseError != null) {
              System.err.println("Firebase error: " + databaseError.getMessage());
              future.completeExceptionally(databaseError.toException());
            } else {
              System.out.println("Map successfully added to user and maps node created " + mapId);
              future.complete(null);
            }
          });
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        System.err.println("Error retrieving user maps: " + databaseError.getMessage());
        future.completeExceptionally(databaseError.toException());
      }
    });

    return future;
  }

  /**
   * Retrieves a user by their username from Firebase.
   *
   * @param username the username of the user to retrieve.
   * @return CompletableFuture containing the UserModel if found.
   * @throws RuntimeException         if there is a Firebase database error during retrieval.
   * @throws IllegalArgumentException if the userId is not found or is null.
   * @author ohandsch
   */
  public CompletableFuture<Optional<UserModel>> getUserByUsername(String username) {
    CompletableFuture<Optional<UserModel>> future = new CompletableFuture<>();
    Query query = databaseReference.orderByChild("username").equalTo(username);

    query.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            UserModel user = childSnapshot.getValue(UserModel.class);
            future.complete(Optional.ofNullable(user));
            return;
          }
        } else {
          future.complete(Optional.empty());
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
   * Retrieves a user by their email from Firebase.
   *
   * @param email the email of the user to retrieve.
   * @return CompletableFuture containing the UserModel if found.
   * @throws RuntimeException         if there is a Firebase database error during retrieval.
   * @throws IllegalArgumentException if the userId is not found or is null.
   * @author ohandsch
   */
  public CompletableFuture<Optional<UserModel>> getUserByEmail(String email) {
    CompletableFuture<Optional<UserModel>> future = new CompletableFuture<>();
    Query query = databaseReference.orderByChild("email").equalTo(email);

    query.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
            UserModel user = childSnapshot.getValue(UserModel.class);
            future.complete(Optional.ofNullable(user));
            return;
          }
        } else {
          future.complete(Optional.empty());
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

