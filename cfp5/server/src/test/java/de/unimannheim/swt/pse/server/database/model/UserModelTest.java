package de.unimannheim.swt.pse.server.database.model;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserModelTest {

  private UserModel userModel;

  @BeforeEach
  public void setUp() {
    userModel = new UserModel();
  }

  @Test
  public void testUid() {
    userModel.setUid("testUid");
    assertEquals("testUid", userModel.getUid());
  }

  @Test
  public void testUsername() {
    userModel.setUsername("testUser");
    assertEquals("testUser", userModel.getUsername());
  }

  @Test
  public void testEmail() {
    userModel.setEmail("test@example.com");
    assertEquals("test@example.com", userModel.getEmail());
  }

  @Test
  public void testMaps() {
    List<String> testMaps = new ArrayList<>();
    testMaps.add("map1");
    testMaps.add("map2");
    userModel.setMaps(testMaps);
    assertEquals(testMaps, userModel.getMaps());
  }

  @Test
  public void testAddMap() {
    userModel.addMap("map1");
    assertTrue(userModel.getMaps().contains("map1"),
        "Map should be added to the user's map collection");
  }

  @Test
  public void testRemoveMap() {
    userModel.addMap("map1");
    userModel.removeMap("map1");
    assertFalse(userModel.getMaps().contains("map1"),
        "Map should be removed from the user's map collection");
  }

  @Test
  public void testToMap() {
    UserModel user = new UserModel();
    user.setEmail("test@test");
    user.setUid("123");
    user.setUsername("testUser");
    user.setGender(0);
    Map<String, Object> userMap = user.toMap();
    assertEquals(userMap.get("uid"), "123", "UID should match");
    assertEquals(userMap.get("username"), "testUser", "Username should match");
    assertEquals(userMap.get("email"), "test@test", "Email should match");
    assertNotNull(userMap.get("maps"), "Maps should not be null");
  }

  @Test
  public void testMapModification() {
    String mapId1 = "map1";
    String mapId2 = "map2";
    userModel.addMap(mapId1);
    userModel.addMap(mapId2);
    userModel.removeMap(mapId1);

    Map<String, Object> userMap = userModel.toMap();
    List<String> maps = (List<String>) userMap.get("maps");
    assertFalse(maps.contains(mapId1), "mapId1 should not be in the maps collection");
    assertTrue(maps.contains(mapId2), "mapId2 should be in the maps collection");
  }
}
