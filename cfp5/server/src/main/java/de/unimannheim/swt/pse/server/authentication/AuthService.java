package de.unimannheim.swt.pse.server.authentication;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import de.unimannheim.swt.pse.server.database.dao.UserDAO;
import de.unimannheim.swt.pse.server.database.model.UserModel;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.regex.Pattern;
import java.util.Properties;


/**
 * AuthService handles authentication-related operations and is intended to be accessed through
 * {@link AuthController}. This class provides functionalities for signing up and signing in users
 * using Firebase Authentication API. It is not intended to be used directly by other parts of the
 * application to maintain proper encapsulation.
 *
 * @see AuthController
 */
public class AuthService {

  /**
   * DAO (Data Access Object) for user-related database operations.
   */
  private final UserDAO userDAO;
  /**
   * API key for Firebase used to authenticate requests to Firebase services.
   */
  private final String firebaseApiKey;


  /**
   * Creates a new instance of the {@link AuthService} class, initializes the {@code UserDAO} and
   * loads the Firebase API key.
   *
   * @param userDAO the DAO used for operations on user data in the database.
   */
  public AuthService(UserDAO userDAO) {
    this.userDAO = userDAO;
    this.firebaseApiKey = loadFirebaseApiKey();
  }

  /**
   * Loads the Firebase API key from the application properties file.
   *
   * @return the Firebase API key as a string.
   * @author ohandsch
   */
  private String loadFirebaseApiKey() {
    Properties prop = new Properties();
    String apiKey = "";

    try (InputStream inputStream = getClass().getClassLoader()
        .getResourceAsStream("application.properties")) {
      if (inputStream != null) {
        prop.load(inputStream);
        apiKey = prop.getProperty("firebase.api.key");
      } else {
        throw new FileNotFoundException("application.properties file not found in the classpath");
      }
    } catch (IOException e) {
      System.err.println("Failed to load Firebase API key: " + e.getMessage());
    }

    return apiKey;
  }

  /**
   * Signs up a new user with the given email, password and username. This method performs
   * validation checks on the email and password, checks if the email is already in use, and creates
   * a new Firebase Authentication user. If the user is successfully created, a new
   * {@link UserModel} is created and saved in the database.
   *
   * @param email    the email of the user to sign up.
   * @param password the password of the user to sign up.
   * @param username the username of the user to sign up.
   * @param gender   the gender of the user to sign up.
   * @return a {@link CompletableFuture} , when completed, provides a {@link UserModel} representing
   * the newly registered user.
   * @throws IllegalArgumentException if the email or password is invalid or if the email is already
   *                                  in use.
   * @author ohandsch
   */
  public CompletableFuture<UserModel> signUp(String email, String password, String username,
      String gender) {
    CompletableFuture<UserModel> future = new CompletableFuture<>();

    // Email validation
    if (!isValidEmail(email)) {
      future.completeExceptionally(new IllegalArgumentException("Invalid email format"));
      return future;
    }

    // Password validation
    if (!isValidPassword(password)) {
      future.completeExceptionally(new IllegalArgumentException("Invalid password format"));
      return future;
    }

    int numGender = 0;
    switch (gender) {
      case "diverse":
        break;
      case "female":
        numGender = 1;
        break;
      case "male":
        numGender = 2;
        break;
      default:
        future.completeExceptionally(new IllegalArgumentException("Invalid gender format"));
    }

    // Check if the email is already in use
    CompletableFuture<Optional<UserModel>> emailCheck = userDAO.getUserByEmail(email);
    CompletableFuture<Optional<UserModel>> usernameCheck = userDAO.getUserByUsername(username);

    // Combine both checks
    CompletableFuture<Void> checks = CompletableFuture.allOf(emailCheck, usernameCheck);

    int finalNumGender = numGender;
    checks.thenRun(() -> {
      try {
        if (emailCheck.get().isPresent()) {
          throw new IllegalArgumentException("Email is already in use");
        }
        if (usernameCheck.get().isPresent()) {
          throw new IllegalArgumentException("Username is already in use");
        }

        // Create a new Firebase Authentication user
        CreateRequest request = new CreateRequest()
            .setEmail(email)
            .setPassword(password)
            .setDisplayName(username);

        ApiFuture<UserRecord> createUserTask = FirebaseAuth.getInstance().createUserAsync(request);

        ApiFutures.addCallback(createUserTask, new ApiFutureCallback<>() {
          @Override
          public void onSuccess(UserRecord userRecord) {
            UserModel userModel = new UserModel(userRecord.getUid(), email, username,
                finalNumGender);
            userDAO.addUser(userModel)
                .thenRun(() -> future.complete(userModel))
                .exceptionally(ex -> {
                  System.err.println(
                      "Failed to save user model for " + email + ": " + ex.getMessage());
                  future.completeExceptionally(ex);
                  return null;
                });
          }

          @Override
          public void onFailure(Throwable throwable) {
            System.err.println(
                "Failed to create Firebase user for " + email + ": " + throwable.getMessage());
            future.completeExceptionally(throwable);
          }
        }, MoreExecutors.directExecutor());
      } catch (Exception e) {
        future.completeExceptionally(e);
      }
    }).exceptionally(ex -> {
      future.completeExceptionally(ex);
      return null;
    });

    return future;
  }


  /**
   * Validates the password format. The password must be at least 8 characters long and contain at
   * least one number and one special character.
   *
   * @param password the password to validate.
   * @return {@code true} if the password is valid, {@code false} otherwise.
   * @author ohandsch
   */
  private boolean isValidPassword(String password) {
    // Password should be at least 8 characters long and contain a number and special character
    return password != null && password.length() >= 8 &&
        password.matches(".*\\d.*") && password.matches(
        ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
  }

  /**
   * Validates the format of the given email using a regular expression to ensure it adheres to a
   * standard email format.
   *
   * @param email the email to validate.
   * @return {@code true} if the email is valid, {@code false} otherwise.
   * @author ohandsch
   */
  private boolean isValidEmail(String email) {
    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    Pattern pattern = Pattern.compile(emailRegex);
    if (email == null) {
      return false;
    }
    return pattern.matcher(email).matches();
  }

  /**
   * Authenticates an existing user with the given email and password. This method sends a sign-in
   * request to the Firebase Authentication API and retrieves the user's UID. The UID is then used
   * to fetch the corresponding {@link UserModel} from the database.
   *
   * @param email    the email of the user to sign in.
   * @param password the password of the user to sign in.
   * @return a {@link CompletableFuture} that , when completed, provides a {@link UserModel}
   * representing the signed-in user.
   * @throws IllegalArgumentException if the email or password is invalid or if the user is not
   *                                  found.
   * @author ohandsch
   */
  public CompletableFuture<UserModel> signIn(String email, String password) {
    CompletableFuture<UserModel> future = new CompletableFuture<>();
    System.out.println("Signing in user with email: " + email);
    // Firebase REST API endpoint for sign-in
    String signInUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="
        + firebaseApiKey;

    // JSON payload containing email and password
    String jsonPayload = String.format(
        "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email, password);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(signInUrl))
        .header("Content-Type", "application/json")
        .POST(BodyPublishers.ofString(jsonPayload))
        .build();
    HttpClient client = HttpClient.newHttpClient();
    try {

      client.sendAsync(request, BodyHandlers.ofString())
          .thenApply(HttpResponse::body)
          .thenAccept(responseBody -> {
            JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();

            // Check if the response contains an error
            if (responseJson.has("error")) {
              JsonObject errorObject = responseJson.getAsJsonObject("error");
              String errorMessage =
                  errorObject.has("message") ? errorObject.get("message").getAsString()
                      : "Unknown error";
              // Handle the error based on the error message or code
              future.completeExceptionally(new IllegalArgumentException(errorMessage));
            } else {
              String uid = responseJson.get("localId").getAsString(); // Extract UID from response

              // Use UID to get or create UserModel in your database
              userDAO.getUserById(uid).thenAccept(userModel -> {
                if (userModel != null) {
                  future.complete(userModel);
                } else {
                  future.completeExceptionally(new Exception("User not found"));
                }
              }).exceptionally(ex -> {
                future.completeExceptionally(ex);
                return null;
              });
            }
          }).exceptionally(ex -> {
            future.completeExceptionally(ex);
            return null;
          });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return future;
  }
}

