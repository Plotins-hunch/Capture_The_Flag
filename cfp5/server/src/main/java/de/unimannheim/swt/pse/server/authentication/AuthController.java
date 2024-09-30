package de.unimannheim.swt.pse.server.authentication;

import de.unimannheim.swt.pse.server.database.dao.UserDAO;
import de.unimannheim.swt.pse.server.database.model.UserModel;
import java.util.concurrent.CompletableFuture;


/**
 * AuthController is the primary interface for authentication operations in the application. It uses
 * {@link AuthService} to handle the detailed authentication logic. Other parts of the application
 * should interact with authentication functionalities exclusively through this controller to ensure
 * that all authentication logic is centralized and consistent.
 *
 * @see AuthService for the service used by this controller exclusively.
 */
public class AuthController {

  /**
   * The service responsible for authentication tasks.
   */
  private final AuthService authService;

  /**
   * Creates a new instance of the {@link AuthController} class.
   *
   * @param userDAO The DAO for user data operations.
   */
  public AuthController(UserDAO userDAO) {
    this.authService = new AuthService(userDAO);
  }

  /**
   * Signs up a new user with the given email, password and username.
   *
   * @param email    The email of the user to sign up.
   * @param password The password of the user to sign up.
   * @param username The username of the user to sign up.
   * @param gender   The gender of the user to sign up.
   * @return A {@link CompletableFuture} that will be completed with the user model after successful
   * registration user.
   * @author ohandsch
   */
  public CompletableFuture<UserModel> signUp(String email, String password, String username,
      String gender) {
    return authService.signUp(email, password, username, gender)
        .thenApply(user -> {
          System.out.println("User successfully signed up: " + user);
          return user;
        });
  }

  /**
   * Authenticates an existing user with the given email and password.
   *
   * @param email    The email of the user to sign in.
   * @param password The password of the user to sign in.
   * @return A {@link CompletableFuture} that , when completed, provides a {@link UserModel}
   * representing the signed-in user.
   * @author ohandsch
   */
  public CompletableFuture<UserModel> signIn(String email, String password) {
    return authService.signIn(email, password)
        .thenApply(user -> {
          System.out.println("User successfully signed in: " + user);
          return user;
        });
  }
}
