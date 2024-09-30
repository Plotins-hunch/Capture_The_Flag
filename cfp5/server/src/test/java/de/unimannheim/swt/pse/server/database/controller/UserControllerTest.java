package de.unimannheim.swt.pse.server.database.controller;


import static org.junit.jupiter.api.Assertions.*;

import de.unimannheim.swt.pse.server.database.DatabaseInitializer;
import de.unimannheim.swt.pse.server.database.dao.UserDAO;
import de.unimannheim.swt.pse.server.database.model.UserModel;
import de.unimannheim.swt.pse.server.database.service.UserService;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class UserControllerTest {

  private UserController userController;

  @BeforeAll
  public static void setupClass() {
    try {
      new DatabaseInitializer().initialize();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @BeforeEach
  public void setup() {
    UserDAO userDAO = new UserDAO();
    UserService userService = new UserService(userDAO);
    userController = new UserController(userService);

  }

  @Test
  public void testCreateUser_ValidUser_Success() throws ExecutionException, InterruptedException {
    UserModel user = new UserModel("testUser1", "testUser1", "test1@example.com", 1);

    CompletableFuture<Void> future = userController.createUser(user);
    future.get();

    // Verify that the user is created in the database
    CompletableFuture<UserModel> retrievedUserFuture = userController.getUserById(user.getUid());
    UserModel retrievedUser = retrievedUserFuture.get();
    assertEquals(user.getUid(), retrievedUser.getUid());
    assertEquals(user.getUsername(), retrievedUser.getUsername());
    assertEquals(user.getEmail(), retrievedUser.getEmail());
  }

  @Test
  public void testCreateUser_NullUser_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> userController.createUser(null));
  }

  @Test
  public void testGetUserById_ExistingUser_ReturnsUser()
      throws ExecutionException, InterruptedException {
    UserModel user = new UserModel("testUser2", "testUser2", "test2@example.com", 1);

    CompletableFuture<Void> createFuture = userController.createUser(user);
    createFuture.get();

    CompletableFuture<UserModel> retrievedUserFuture = userController.getUserById(user.getUid());
    UserModel retrievedUser = retrievedUserFuture.get();
    assertEquals(user.getUid(), retrievedUser.getUid());
    assertEquals(user.getUsername(), retrievedUser.getUsername());
    assertEquals(user.getEmail(), retrievedUser.getEmail());
  }

  @Test
  public void testGetUserById_NonExistingUser_ThrowsException() {
    CompletableFuture<UserModel> future = userController.getUserById("nonExistingUserId");
    assertThrows(ExecutionException.class, future::get);
  }

  @Test
  public void testUpdateUser_ExistingUser_Success()
      throws ExecutionException, InterruptedException {
    UserModel user = new UserModel("testUser3", "testUser3", "test3@example.com", 1);

    CompletableFuture<Void> createFuture = userController.createUser(user);
    createFuture.get();

    user.setUsername("updatedTestUser3");
    user.setEmail("updatedTest3@example.com");

    CompletableFuture<Void> updateFuture = userController.updateUser(user);
    updateFuture.get();

    CompletableFuture<UserModel> retrievedUserFuture = userController.getUserById(user.getUid());
    UserModel retrievedUser = retrievedUserFuture.get();
    assertEquals(user.getUid(), retrievedUser.getUid());
    assertEquals("updatedTestUser3", retrievedUser.getUsername());
    assertEquals("updatedTest3@example.com", retrievedUser.getEmail());
  }

  @Test
  public void testUpdateUser_NonExistingUser_ThrowsException() {
    UserModel user = new UserModel("nonExistingUser", "nonExistingUser", "nonExisting@example.com",
        1);

    CompletableFuture<Void> future = userController.updateUser(user);
    assertThrows(ExecutionException.class, future::get);
  }

  @Test
  public void testDeleteUser_ExistingUser_Success()
      throws ExecutionException, InterruptedException {
    UserModel user = new UserModel("testUser4", "testUser4", "test4@example.com", 1);

    CompletableFuture<Void> createFuture = userController.createUser(user);
    createFuture.get();

    CompletableFuture<Void> deleteFuture = userController.deleteUser(user.getUid());
    deleteFuture.get();

    CompletableFuture<UserModel> retrievedUserFuture = userController.getUserById(user.getUid());
    assertThrows(ExecutionException.class, retrievedUserFuture::get);
  }

  @Test
  public void testAddMapToUser_ExistingUserAndMap_Success()
      throws ExecutionException, InterruptedException {
    UserModel user = new UserModel("testUser6", "testUser6", "test5@example.com", 1);

    CompletableFuture<Void> createFuture = userController.createUser(user);
    createFuture.get();

    String mapId = "testMap1";

    CompletableFuture<Void> addMapFuture = userController.addMapToUser(user.getUid(), mapId);
    addMapFuture.get();

    CompletableFuture<UserModel> retrievedUserFuture = userController.getUserById(user.getUid());
    UserModel retrievedUser = retrievedUserFuture.get();
    assertTrue(retrievedUser.getMaps().contains(mapId));
  }

  @Test
  public void testAddMapToUser_NonExistingUser_ThrowsException() {
    String mapId = "testMap2";

    CompletableFuture<Void> future = userController.addMapToUser("nonExistingUserId", mapId);
    assertThrows(ExecutionException.class, future::get);
  }

  @Test
  public void testCreateUser_BlankUsername_ThrowsException() {
    UserModel user = new UserModel("testUser7", "", "test7@example.com", 1);

    assertThrows(IllegalArgumentException.class, () -> userController.createUser(user));
  }

  @Test
  public void testCreateUser_BlankEmail_ThrowsException() {
    UserModel user = new UserModel("testUser8", "testUser8", "", 1);

    assertThrows(IllegalArgumentException.class, () -> userController.createUser(user));
  }

  @Test
  public void testGetUserById_NullUserId_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> userController.getUserById(null));
  }

  @Test
  public void testUpdateUser_NullUser_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> userController.updateUser(null));
  }

  @Test
  public void testDeleteUser_NullUserId_ThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> userController.deleteUser(null));
  }

  @Test
  public void testAddMapToUser_NullUserId_ThrowsException() {
    String mapId = "testMap3";

    assertThrows(IllegalArgumentException.class, () -> userController.addMapToUser(null, mapId));
  }

  @Test
  public void testAddMapToUser_NullMapId_ThrowsException()
      throws ExecutionException, InterruptedException {
    UserModel user = new UserModel("testUser9", "testUser9", "test9@example.com", 1);

    CompletableFuture<Void> createFuture = userController.createUser(user);
    createFuture.get();

    assertThrows(IllegalArgumentException.class,
        () -> userController.addMapToUser(user.getUid(), null));
  }

  @AfterAll
  public static void tearDownClass() throws ExecutionException, InterruptedException {
    UserDAO userDAO = new UserDAO();
    UserService userService = new UserService(userDAO);
    UserController userController = new UserController(userService);

    CompletableFuture<Void> deleteFuture1 = userController.deleteUser("testUser1");
    deleteFuture1.get();
    CompletableFuture<Void> deleteFuture2 = userController.deleteUser("testUser");
    deleteFuture2.get();
    CompletableFuture<Void> deleteFuture3 = userController.deleteUser("testUser2");
    deleteFuture3.get();
    CompletableFuture<Void> deleteFuture4 = userController.deleteUser("testUser3");
    deleteFuture4.get();
    CompletableFuture<Void> deleteFuture5 = userController.deleteUser("testUser4");
    deleteFuture5.get();
    CompletableFuture<Void> deleteFuture6 = userController.deleteUser("testUser6");
    deleteFuture6.get();
    CompletableFuture<Void> deleteFuture7 = userController.deleteUser("testUser7");
    deleteFuture7.get();
    CompletableFuture<Void> deleteFuture8 = userController.deleteUser("testUser8");
    deleteFuture8.get();
    CompletableFuture<Void> deleteFuture9 = userController.deleteUser("testUser9");
    deleteFuture9.get();
  }
}

