package de.unimannheim.swt.pse.server.database.service;

import de.unimannheim.swt.pse.server.database.dao.UserDAO;
import de.unimannheim.swt.pse.server.database.model.UserModel;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.StringUtils;

/**
 * Service class for managing user operations. This class acts as a bridge between the application
 * logic and the database access layer, handling all business logic related to user operations. It
 * is responsible for interacting with the {@link UserDAO} to perform CRUD operations on users. This
 * class should not be used directly by external classes and should instead be accessed through its
 * corresponding controller to maintain proper encapsulation
 */
public class UserService {

  /**
   * The data access object for user operations
   */
  private final UserDAO userDAO;

  /**
   * Constructs a UserService with the necessary DAO dependency.
   *
   * @param userDAO the DAO object for user operations
   */
  public UserService(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  /**
   * Creates a new user in the database.
   *
   * @param user The {@link UserModel} representing the user to be created.
   * @return A CompletableFuture that completes when the user is successfully added to the database.
   * @throws IllegalArgumentException If the user information is invalid.
   * @throws RuntimeException         If there is a failure in creating the user in the database.
   * @author ohandsch
   */
  public CompletableFuture<Void> createUser(UserModel user) {
    if (user == null || StringUtils.isBlank(user.getEmail()) || StringUtils.isBlank(
        user.getUsername())) {
      System.out.println("Invalid user data");
      CompletableFuture<Void> failedFuture = new CompletableFuture<>();
      failedFuture.completeExceptionally(new IllegalArgumentException("Invalid user data"));
      throw new IllegalArgumentException("Invalid user data");
    }
    return userDAO.addUser(user)
        .thenRun(() -> {
          // Maybe welcome mail etc
        })
        .exceptionally(e -> {
          System.err.println("Error creating user: " + e.getMessage());
          throw new RuntimeException("Failed to create user", e);
        });
  }

  /**
   * Retrieves a user by their unique identifier.
   *
   * @param userId The ID of the user to retrieve.
   * @return A CompletableFuture containing the UserModel of the requested user if found.
   * @throws RuntimeException         If there is a failure in fetching the user from the database.
   * @throws IllegalArgumentException If the user ID is invalid.
   * @author ohandsch
   */
  public CompletableFuture<UserModel> getUserById(String userId) {
    if (userId == null || StringUtils.isBlank(userId)) {
      CompletableFuture<Void> failedFuture = new CompletableFuture<>();
      failedFuture.completeExceptionally(new IllegalArgumentException("Invalid user id"));
      throw new IllegalArgumentException("User ID cannot be null");
    }
    return userDAO.getUserById(userId)
        .exceptionally(e -> {
          System.err.println("Error fetching user: " + e.getMessage());
          throw new RuntimeException("Failed to fetch user", e);
        });
  }

  /**
   * Updates an existing user's information in the database.
   *
   * @param user The UserModel with updated information.
   * @return A CompletableFuture that completes when the user update is processed.
   * @throws RuntimeException         If there is a failure in updating the user information.
   * @throws IllegalArgumentException If the user data is invalid.
   * @author ohandsch
   */
  public CompletableFuture<Void> updateUser(UserModel user) {
    if (user == null || StringUtils.isBlank(user.getUid()) || StringUtils.isBlank(
        user.getEmail()) || StringUtils.isBlank(user.getUsername())) {
      CompletableFuture<Void> failedFuture = new CompletableFuture<>();
      failedFuture.completeExceptionally(new IllegalArgumentException("Invalid user data"));
      throw new IllegalArgumentException("Invalid user data");
    }
    return userDAO.updateUser(user)
        .exceptionally(e -> {
          System.err.println("Error updating user: " + e.getMessage());
          throw new RuntimeException("Failed to update user", e);
        });
  }

  /**
   * Deletes a user from the database.
   *
   * @param userId The unique identifier of the user to delete.
   * @return A CompletableFuture that completes when the user is deleted from the database.
   * @throws RuntimeException         If there is a failure in deleting the user.
   * @throws IllegalArgumentException If the user ID is invalid.
   * @author ohandsch
   */
  public CompletableFuture<Void> deleteUser(String userId) {
    if (StringUtils.isBlank(userId)) {
      CompletableFuture<Void> failedFuture = new CompletableFuture<>();
      failedFuture.completeExceptionally(new IllegalArgumentException("Invalid user ID"));
      throw new IllegalArgumentException("Invalid user ID");
    }
    return userDAO.deleteUser(userId)
        .exceptionally(e -> {
          System.err.println("Error deleting user: " + e.getMessage());
          throw new RuntimeException("Failed to delete user", e);
        });
  }

  /**
   * Adds a map to a user's profile.
   *
   * @param userId The user ID to which the map will be added.
   * @param mapId  The map ID that will be added to the user's profile.
   * @return A CompletableFuture that completes when the map is added to the user's profile.
   * @throws RuntimeException         If there is an error during the operation.
   * @throws IllegalArgumentException If the user or map ID is invalid.
   * @author ohandsch
   */
  public CompletableFuture<Void> addMapToUser(String userId, String mapId) {
    if (StringUtils.isBlank(userId) || StringUtils.isBlank(mapId)) {
      CompletableFuture<Void> failedFuture = new CompletableFuture<>();
      failedFuture.completeExceptionally(new IllegalArgumentException("Invalid user or map ID"));
      throw new IllegalArgumentException("Invalid user or map ID");
    }
    return userDAO.addMapToUser(userId, mapId)
        .thenRun(() -> System.out.println("Map " + mapId + " added to user " + userId))
        .exceptionally(e -> {
          System.err.println("Error adding map to user: " + e.getMessage());
          throw new RuntimeException(e.getMessage());
        });
  }

}

