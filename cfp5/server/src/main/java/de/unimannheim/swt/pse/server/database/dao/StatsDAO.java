package de.unimannheim.swt.pse.server.database.dao;

import com.google.firebase.database.*;
import de.unimannheim.swt.pse.server.database.model.StatsModel;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Data Access Object (DAO) for handling all statistics-related data interactions with Firebase.
 * This class should not be used directly; instead, interactions should be handled through its
 * corresponding controller to ensure proper data management and encapsulation within the
 * application's architecture. Direct usage of this DAO outside of its controller is strongly
 * discouraged.
 */
public class StatsDAO {

  /**
   * Reference to the Firebase Realtime Database node
   */
  private final DatabaseReference statsRef;

  /**
   * Constructor for StatsDAO that initializes the DatabaseReference to the "userStats" node in
   * Firebase.
   */
  public StatsDAO() {
    this.statsRef = FirebaseDatabase.getInstance().getReference("userStats");
  }

  /**
   * Retrieves the statistics for a specific user by their user ID.
   *
   * @param userId the unique identifier of the user whose statistics are being retrieved.
   * @return a CompletableFuture that, when completed, will return the StatsModel containing the
   * user's statistics.
   * @author ohandsch
   */
  public CompletableFuture<StatsModel> getStats(String userId) {
    System.out.println("Fetching stats for user: " + userId);
    CompletableFuture<StatsModel> future = new CompletableFuture<>();
    DatabaseReference userStatsRef = statsRef.child(userId);

    userStatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        StatsModel stats = new StatsModel(userId);
        if (dataSnapshot.exists()) {
          stats.setGamesWon(dataSnapshot.child("gamesWon").getValue(Integer.class));
          stats.setGamesLost(dataSnapshot.child("gamesLost").getValue(Integer.class));
        }
        future.complete(stats);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        future.completeExceptionally(databaseError.toException());
      }
    });

    return future;
  }


  /**
   * Updates the statistics for a specific user in Firebase.
   *
   * @param stats the StatsModel object containing the updated statistics to be stored.
   * @return CompletableFuture that completes when the statistics update is successful.
   * @throws RuntimeException if a Firebase error occurs during the update process.
   * @author ohandsch
   */
  public CompletableFuture<Void> updateStats(StatsModel stats) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    String userId = stats.getUserId();

    statsRef.child(userId)
        .updateChildren(stats.toMap(), (databaseError, databaseReference) -> {
          if (databaseError != null) {
            future.completeExceptionally(databaseError.toException());
          } else {
            // Update the leaderboard after successfully updating the stats
            updateLeaderboard(userId, stats.getGamesWon())
                .thenRun(() -> future.complete(null))
                .exceptionally(ex -> {
                  future.completeExceptionally(ex);
                  return null;
                });
          }
        });

    return future;
  }

  /**
   * Retrieves the top N users based on their game wins from the leaderboard in Firebase.
   *
   * @param topN the number of top entries to retrieve from the leaderboard.
   * @return CompletableFuture that completes with a list of StatsModels representing the top users.
   * @throws RuntimeException if a Firebase error occurs while retrieving the data.
   * @author ohandsch
   */
  public CompletableFuture<List<StatsModel>> getLeaderboard(int topN) {
    CompletableFuture<List<StatsModel>> future = new CompletableFuture<>();

    Query query = statsRef.getParent().child("leaderboard").orderByValue().limitToLast(topN);

    query.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        List<StatsModel> leaderboard = new ArrayList<>();
        dataSnapshot.getChildren().forEach(childSnapshot -> {
          String userId = childSnapshot.getKey();
          Integer score = childSnapshot.getValue(Integer.class);

          StatsModel statsModel = new StatsModel(userId);
          statsModel.setGamesWon(score);

          leaderboard.add(statsModel);
        });

        // Since Firebase returns in ascending order, reverse to get the top entries
        Collections.reverse(leaderboard);
        future.complete(leaderboard);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        future.completeExceptionally(databaseError.toException());
      }
    });

    return future;
  }

  /**
   * Updates or creates a leaderboard entry for a user based on their new score.
   *
   * @param userId   the user's unique identifier.
   * @param newScore the score to add to the user's existing score on the leaderboard.
   * @return CompletableFuture that completes when the leaderboard update is successful.
   * @throws RuntimeException if a Firebase error occurs during the update process.
   * @author ohandsch
   */
  public CompletableFuture<Void> updateLeaderboard(String userId, int newScore) {
    CompletableFuture<Void> future = new CompletableFuture<>();

    DatabaseReference leaderboardRef = statsRef.getParent().child("leaderboard");

    // Check if the leaderboard entry for the user exists
    leaderboardRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        // Update the score if the leaderboard entry exists, or create a new one with the initial score
        leaderboardRef.child(userId).setValue(
            newScore,
            (databaseError, databaseReference) -> {
              if (databaseError != null) {
                future.completeExceptionally(databaseError.toException());
              } else {
                future.complete(null);
              }
            });
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        future.completeExceptionally(databaseError.toException());
      }
    });

    return future;
  }

}

