package de.unimannheim.swt.pse.server.database.controller;

import de.unimannheim.swt.pse.server.database.model.UserModel;
import de.unimannheim.swt.pse.server.database.service.UserService;
import java.util.concurrent.CompletableFuture;

/**
 * Controller class for user management within the application. This class serves as the primary
 * interface for operations related to users, such as creating, updating, retrieving, and deleting
 * user records. All user-related data is stored and managed in a Firebase database. Other
 * components should interact with user functionalities exclusively through this controller.
 */
public class UserController {

  /**
   * Service object for handling user-related operations.
   */
  private final UserService userService;

  /**
   * Constructs a new instance of the {@link UserController} class with the specified
   * {@link UserService} object.
   *
   * @param userService the service object for handling user-related operations
   * @author ohandsch
   */
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Creates a new user in the Firebase database.
   *
   * @param user the UserModel to create.
   * @return a CompletableFuture that completes when the user is successfully created.
   * @throws RuntimeException if there is an issue creating the user in the database.
   * @author ohandsch
   */
  public CompletableFuture<Void> createUser(UserModel user) {
    System.out.println("Creating user in controller: " + user);
    return userService.createUser(user)
        .thenRun(() -> System.out.println("User created successfully"));
  }


  /**
   * Retrieves a user by their ID from the Firebase database.
   *
   * @param userId the ID of the user to retrieve.
   * @return a CompletableFuture containing the UserModel of the retrieved user.
   * @throws IllegalArgumentException if the userId is not found or is null.
   * @throws RuntimeException         if there is an issue retrieving the user from the database.
   * @author ohandsch
   */
  public CompletableFuture<UserModel> getUserById(String userId) {
    return userService.getUserById(userId)
        .thenApply(user -> {
          System.out.println("User fetched successfully: " + user);
          return user;
        });
  }


  /**
   * Updates an existing user's information in the Firebase database.
   *
   * @param user the updated UserModel information.
   * @return a CompletableFuture that completes when the user is updated.
   * @throws IllegalArgumentException if the userId does not match any existing user or if the user
   *                                  data is invalid.
   * @throws RuntimeException         if there is an issue retrieving the user from the database.
   * @author ohandsch
   */
  public CompletableFuture<Void> updateUser(UserModel user) {
    return userService.updateUser(user)
        .thenRun(() -> System.out.println("User updated successfully"));
  }


  /**
   * Deletes a user from the Firebase database based on their ID.
   *
   * @param userId the ID of the user to delete.
   * @return a CompletableFuture that completes when the user is deleted.
   * @throws IllegalArgumentException if the userId is not found or is null.
   * @throws RuntimeException         if there is an issue deleting the user from the database.
   * @author ohandsch
   */
  public CompletableFuture<Void> deleteUser(String userId) {
    return userService.deleteUser(userId)
        .thenRun(() -> System.out.println("User deleted successfully"));
  }


  /**
   * Associates a map with a user in the Firebase database.
   *
   * @param userId the ID of the user to whom the map will be added.
   * @param mapId  the ID of the map to add to the user's account.
   * @return a CompletableFuture that completes when the map is added to the user.
   * @throws IllegalArgumentException if either the userId or mapId are not found or are null.
   * @throws RuntimeException         if there is an issue adding the map to the user in the
   *                                  database.
   * @author ohandsch
   */
  public CompletableFuture<Void> addMapToUser(String userId, String mapId) {
    return userService.addMapToUser(userId, mapId)
        .thenRun(() -> System.out.println("Map added to user successfully"));
  }
  
}
