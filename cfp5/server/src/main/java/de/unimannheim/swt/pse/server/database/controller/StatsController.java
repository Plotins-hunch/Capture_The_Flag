package de.unimannheim.swt.pse.server.database.controller;

import de.unimannheim.swt.pse.server.database.model.StatsModel;
import de.unimannheim.swt.pse.server.database.service.StatsService;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller class for managing statistics within the application. This class serves as the primary
 * interface for operations related to user statistics and leaderboards, such as retrieving stats,
 * updating them, and managing leaderboard entries. Other components should interact with statistics
 * functionalities exclusively through this controller.
 */
public class StatsController {

  /**
   * Service object for handling operations related to statistics.
   */
  private final StatsService statsService;

  /**
   * Constructs a new instance of the {@link StatsController} class with the specified service.
   *
   * @param statsService the service object to use for handling statistics operations
   * @author ohandsch
   */
  public StatsController(StatsService statsService) {
    this.statsService = statsService;
  }

  /**
   * Retrieves the statistics for a given user ID.
   *
   * @param userId the ID of the user whose statistics are being retrieved.
   * @return a CompletableFuture that, when completed, will return the StatsModel of the requested
   * user.
   * @throws IllegalArgumentException if the userId is null or not found.
   * @author ohandsch
   */
  public CompletableFuture<StatsModel> getStats(String userId) {
    return statsService.getStats(userId)
        .thenApply(stats -> {
          System.out.println("Stats fetched successfully: " + stats);
          return stats;
        });
  }


  /**
   * Updates the statistics for a user using the provided StatsModel.
   *
   * @param stats the StatsModel containing the updated values.
   * @return a CompletableFuture that, when completed, signifies that the stats have been updated.
   * @throws IllegalArgumentException if the stats object is null or contains invalid data.
   * @throws RuntimeException         if the update operation fails for any reason.
   * @author ohandsch
   */
  public CompletableFuture<Void> updateStats(StatsModel stats) {
    return statsService.updateStats(stats)
        .thenRun(() -> System.out.println("Stats updated successfully"));
  }


  /**
   * Retrieves the top N users from the leaderboard.
   *
   * @param topN the number of top users to retrieve from the leaderboard.
   * @return a CompletableFuture that, when completed, will return a list of StatsModels
   * representing the top users.
   * @throws IllegalArgumentException if the topN parameter is less than 1.
   * @throws RuntimeException         if the leaderboard retrieval operation fails for any reason.
   * @author ohandsch
   */
  public CompletableFuture<List<StatsModel>> getLeaderboard(int topN) {
    return statsService.getLeaderboard(topN)
        .thenApply(leaderboard -> {
          System.out.println("Leaderboard fetched successfully: " + leaderboard);
          return leaderboard;
        });
  }

  /**
   * Updates the leaderboard for a specific user based on the game result.
   *
   * @param userId     the ID of the user for whom the leaderboard is to be updated.
   * @param gameResult the result of the user's game, true if won, false otherwise.
   * @return a CompletableFuture that completes when the leaderboard is updated.
   * @throws RuntimeException if there is an error during the update process.
   * @author ohandsch
   */
  public CompletableFuture<Void> updateLeaderboard(String userId, boolean gameResult) {
    return statsService.updateLeaderboard(userId, gameResult)
        .thenRun(() -> System.out.println("Leaderboard updated successfully for user " + userId))
        .exceptionally(ex -> {
          System.err.println(
              "Error updating leaderboard for user " + userId + ": " + ex.getMessage());
          throw new RuntimeException("Failed to update leaderboard for user " + userId, ex);
        });
  }

}
