package de.unimannheim.swt.pse.server.database.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionsModelTest {

  @Test
  void testGameSessionsModelConstructor() {
    GameSessionsModel session = new GameSessionsModel("session1", "192.168.1.1", true);
    assertEquals("session1", session.getSessionId());
    assertEquals("192.168.1.1", session.getIp());
    assertTrue(session.isFull());
  }

  @Test
  void testSettersAndGetters() {
    GameSessionsModel session = new GameSessionsModel("session1", "192.168.1.1", true);

    session.setSessionId("session2");
    assertEquals("session2", session.getSessionId());

    session.setIp("10.0.0.1");
    assertEquals("10.0.0.1", session.getIp());

    session.setFull(false);
    assertFalse(session.isFull());
  }

  @Test
  void testToMap() {
    GameSessionsModel session = new GameSessionsModel("session1", "192.168.1.1", true);
    Map<String, Object> mapRepresentation = session.toMap();

    assertEquals("session1", mapRepresentation.get("sessionId"));
    assertEquals("192.168.1.1", mapRepresentation.get("ip"));
    assertEquals(true, mapRepresentation.get("isFull"));
  }
}

