package de.unimannheim.swt.pse.server.database.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a user model in the application with methods for managing user's data. This class is
 * used to encapsulate user information and associated operations.
 */
public class UserModel {

  /**
   * Unique identifier for the user. Typically, the UID provided by Firebase authentication.
   */
  private String Uid;
  /**
   * The user's chosen username.
   */
  private String username;
  /**
   * The user's email address.
   */
  private String email;
  /**
   * A map of the user's map ids
   */
  private List<String> maps = new ArrayList<>();

  /**
   * The user's gender (0 for diverse, 1 for female, 2 for male)
   */
  private int gender;

  /**
   * Constructs a new UserModel with specified UID, username, and email.
   *
   * @param Uid      The unique identifier for the user.
   * @param username The user's chosen username.
   * @param email    The user's email address.
   * @param gender   The user's gender.
   * @author ohandsch
   */
  public UserModel(String Uid, String username, String email, int gender) {
    this.Uid = Uid;
    this.username = username;
    this.email = email;
    this.maps = new ArrayList<>();
    this.gender = gender;
  }

  /**
   * An empty constructor for Firebase deserialization. This is typically used by Firebase itself to
   * create instances of UserModel based on data retrieved from Firebase.
   */
  public UserModel() {
  }

  // Getters and Setters

  public String getUid() {
    return Uid;
  }

  public void setUid(String uid) {
    this.Uid = uid;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<String> getMaps() {
    return maps != null ? maps : new ArrayList<>();
  }

  public int getGender() {
    return this.gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public void setMaps(List<String> maps) {
    this.maps = maps;
  }

  /**
   * Adds a map to the user's collection based on the given map ID.
   *
   * @param mapId The ID of the map to add to the collection.
   */
  public void addMap(String mapId) {
    this.maps.add(mapId);
  }

  /**
   * Removes a map from the user's collection based on the given map ID.
   *
   * @param mapId The ID of the map to remove from the collection.
   */
  public void removeMap(String mapId) {
    this.maps.remove(mapId);
  }

  /**
   * Converts the UserModel to a Map object for serialization to Firebase.
   *
   * @return A Map object representing the UserModel.
   * @author ohandsch
   */
  public Map<String, Object> toMap() {
    HashMap<String, Object> result = new HashMap<>();
    result.put("uid", Uid);
    result.put("username", username);
    result.put("email", email);
    result.put("maps", maps != null ? maps : null);
    result.put("gender", gender);
    return result;
  }
}
