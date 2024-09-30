package de.unimannheim.swt.pse.server.database;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.IOException;
import java.io.InputStream;

/**
 * Initializes the connection to the Firebase database. This class is responsible for setting up the
 * Firebase application instance. It ensures that the Firebase connection is established only once
 * throughout the application. It should be used during the application startup to prepare the
 * Firebase environment for subsequent operations.
 */
public class DatabaseInitializer {

  /**
   * Initializes the Firebase application with credentials and database URL from a JSON
   * configuration file. This method will set up the Firebase connection if it has not already been
   * initialized.
   *
   * @throws IOException    If there is an error reading from the configuration file or if the file
   *                        cannot be found.
   * @throws AssertionError If the configuration file is missing (assertion error).
   * @author ohandsch
   */
  public void initialize() throws IOException {
    if (FirebaseApp.getApps().isEmpty()) {

      InputStream serviceAccount = getClass().getClassLoader()
          .getResourceAsStream("database_key.json");

      assert serviceAccount != null;
      FirebaseOptions options = new FirebaseOptions.Builder()
          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
          .setDatabaseUrl(
              "https://new-horizon-games-default-rtdb.europe-west1.firebasedatabase.app")
          .build();
      FirebaseApp.initializeApp(options);
    }
  }
}
