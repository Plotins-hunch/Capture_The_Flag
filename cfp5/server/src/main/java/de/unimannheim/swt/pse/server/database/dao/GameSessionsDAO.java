package de.unimannheim.swt.pse.server.database.dao;

import de.unimannheim.swt.pse.server.database.model.GameSessionsModel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.google.firebase.database.*;

/**
 * Data Access Object (DAO) for managing game session data interactions with Firebase. This class
 * performs CRUD operations directly on the "gameSessions" Firebase node. It is designed to be used
 * through a controller to ensure proper handling and encapsulation within the application's
 * architecture. Direct usage of this DAO outside of its controller is strongly discouraged to
 * maintain consistency and integrity in data handling.
 */
public class GameSessionsDAO {

  /**
   * Reference to the Firebase database node for game sessions.
   */
  private final DatabaseReference databaseReference;

  /**
   * Initializes a new instance of GameSessionsDAO with a reference to the "gameSessions" node in
   * Firebase.
   */
  public GameSessionsDAO() {
    this.databaseReference = FirebaseDatabase.getInstance().getReference("gameSessions");
  }

  /**
   * Adds a new game session to Firebase and returns the unique session ID.
   *
   * @param session The GameSessionsModel object containing the session data.
   * @return A CompletableFuture that, when completed, will return the unique ID of the newly
   * created game session.
   * @throws RuntimeException if there is an error during the Firebase operation.
   * @author ohandsch
   */
  public CompletableFuture<String> addGameSession(GameSessionsModel session) {
    CompletableFuture<String> future = new CompletableFuture<>();
    databaseReference.child(session.getSessionId())
        .setValue(session.toMap(), (databaseError, databaseReference) -> {
          if (databaseError != null) {
            future.completeExceptionally(databaseError.toException());
          } else {
            future.complete(session.getSessionId());
          }
        });

    return future;
  }

  /**
   * Deletes a game session from Firebase based on the session ID.
   *
   * @param sessionId The unique identifier for the game session to be deleted.
   * @return A CompletableFuture that completes when the game session is successfully deleted from
   * Firebase.
   * @throws RuntimeException if there is an error during the Firebase operation.
   * @author ohandsch
   */
  public CompletableFuture<Void> deleteGameSession(String sessionId) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    databaseReference.child(sessionId).removeValue((databaseError, databaseReference) -> {
      if (databaseError != null) {
        future.completeExceptionally(databaseError.toException());
      } else {
        future.complete(null);
      }
    });

    return future;
  }

  /**
   * Retrieves all current game sessions from Firebase.
   *
   * @return A CompletableFuture containing a list of all current GameSessionsModel objects.
   * @throws RuntimeException if there is an error during the Firebase operation.
   * @author ohandsch
   */
  public CompletableFuture<List<GameSessionsModel>> getAllCurrentGameSessions() {
    CompletableFuture<List<GameSessionsModel>> future = new CompletableFuture<>();
    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        List<GameSessionsModel> gameSessions = new ArrayList<>();
        for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
          String ip = sessionSnapshot.child("ip").getValue(String.class);
          boolean isFull = sessionSnapshot.child("isFull").getValue(Boolean.class);
          String sessionId = sessionSnapshot.getKey();
          GameSessionsModel session = new GameSessionsModel(sessionId, ip, isFull);
          gameSessions.add(session);
        }
        future.complete(gameSessions);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        future.completeExceptionally(databaseError.toException());
      }
    });

    return future;
  }
}
