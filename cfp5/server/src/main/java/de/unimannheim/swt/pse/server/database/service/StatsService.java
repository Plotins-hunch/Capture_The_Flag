package de.unimannheim.swt.pse.server.database.service;

import de.unimannheim.swt.pse.server.database.dao.StatsDAO;
import de.unimannheim.swt.pse.server.database.model.StatsModel;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.StringUtils;

/**
 * Service class for managing statistics-related operations. This class serves as a mediator between
 * the controller and the DAO ({@link StatsDAO}), handling business logic and data transformations
 * needed before interacting with the database. It ensures that all statistics operations are
 * executed through centralized logic. This class should not be used directly by other parts of the
 * application except through its corresponding controller to maintain clean architecture and proper
 * data flow.
 */
public class StatsService {

  /**
   * Data Access Object for statistics, responsible for database interactions.
   */
  private final StatsDAO statsDAO;

  /**
   * Constructs a new instance of the {@link StatsService} class with the specified
   * {@link StatsDAO}.
   *
   * @param statsDAO the data access object for statistics
   */
  public StatsService(StatsDAO statsDAO) {
    this.statsDAO = statsDAO;
  }

  /**
   * Retrieves statistics for a specified user by user ID.
   *
   * @param userId the ID of the user whose statistics are to be retrieved.
   * @return A CompletableFuture that, when completed, returns the StatsModel of the specified user.
   * @throws RuntimeException         if there is an error retrieving the stats from the database.
   * @throws IllegalArgumentException if the user ID is null or empty.
   * @author ohandsch
   */
  public CompletableFuture<StatsModel> getStats(String userId) {
    if (userId == null || StringUtils.isBlank(userId)) {
      throw new IllegalArgumentException("User ID cannot be null or empty");
    }
    return statsDAO.getStats(userId)
        .exceptionally(ex -> {
          System.err.println("Error fetching stats for user " + userId + ": " + ex.getMessage());
          // Optionally, return a default StatsModel or rethrow a custom exception
          throw new RuntimeException("Failed to fetch stats for user " + userId, ex);
        });
  }

  /**
   * Updates the statistics for a user in the database.
   *
   * @param stats the StatsModel object containing the updated statistics data.
   * @return A CompletableFuture that completes when the statistics are successfully updated.
   * @throws RuntimeException         if there is an error updating the stats in the database.
   * @throws IllegalArgumentException if the StatsModel object is null.
   * @author ohandsch
   */
  public CompletableFuture<Void> updateStats(StatsModel stats) {
    if (stats == null) {
      throw new IllegalArgumentException("Stats object cannot be null");
    }
    return statsDAO.updateStats(stats)
        .exceptionally(ex -> {
          System.err.println("Error updating stats: " + ex.getMessage());
          throw new RuntimeException("Failed to update stats", ex);
        });
  }

  /**
   * Retrieves the leaderboard with the top N users based on their game wins.
   *
   * @param topN the number of top users to retrieve.
   * @return A CompletableFuture that, when completed, returns a list of StatsModels representing
   * the top users.
   * @throws RuntimeException         if there is an error fetching the leaderboard from the
   *                                  database.
   * @throws IllegalArgumentException if the topN value is less than or equal to 0.
   * @author ohandsch
   */
  public CompletableFuture<List<StatsModel>> getLeaderboard(int topN) {
    if (topN <= 0) {
      throw new IllegalArgumentException("TopN value must be greater than 0");
    }
    return statsDAO.getLeaderboard(topN)
        .exceptionally(ex -> {
          System.err.println("Error fetching leaderboard: " + ex.getMessage());
          throw new RuntimeException("Failed to fetch leaderboard", ex);
        });
  }

  /**
   * Updates or initializes a user's position on the leaderboard, based on whether they won a game.
   *
   * @param userId the ID of the user whose leaderboard status needs updating.
   * @param isWin  true if the user won the game, false otherwise.
   * @return A CompletableFuture that completes when the leaderboard is updated.
   * @throws RuntimeException         if there is an error updating the leaderboard in the
   *                                  database.
   * @throws IllegalArgumentException if the user ID is null or empty.
   * @author ohandsch
   */
  public CompletableFuture<Void> updateLeaderboard(String userId, boolean isWin) {
    if (userId == null || StringUtils.isBlank(userId)) {
      throw new IllegalArgumentException("User ID cannot be null or empty");
    }
    int scoreIncrement = isWin ? 1 : 0;

    return statsDAO.updateLeaderboard(userId, scoreIncrement)
        .exceptionally(ex -> {
          System.err.println(
              "Error updating leaderboard for user " + userId + ": " + ex.getMessage());
          throw new RuntimeException("Failed to update leaderboard for user " + userId, ex);
        });
  }

}

