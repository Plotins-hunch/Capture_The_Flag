package de.unimannheim.swt.pse.server.database.controller;

import de.unimannheim.swt.pse.server.database.service.MapService;
import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller class for managing map operations within the application. This class serves as the
 * primary interface for map-related actions such as creating, deleting, and retrieving maps. Other
 * parts of the application should interact with map functionalities exclusively through this
 * controller.
 */
public class MapController {

  /**
   * Service object for handling map-related business logic.
   */
  private final MapService mapService;

  /**
   * Constructs a new instance of the {@link MapController} class with the specified
   * {@link MapService} object.
   *
   * @param mapService the service object for handling map-related business logic
   * @author ohandsch
   */
  public MapController(MapService mapService) {
    this.mapService = mapService;
  }

  /**
   * Creates a map based on the provided template and associates it with a user.
   *
   * @param mapTemplate the template for creating the map.
   * @param userId      the user ID to associate with the new map.
   * @return a CompletableFuture that completes when the map is created.
   * @throws NullPointerException     if the provided map template is null
   * @throws IllegalArgumentException if the provided user ID is null or empty
   * @throws RuntimeException         if an error occurs while creating the map
   * @author ohandsch
   */
  public CompletableFuture<Void> createMap(MapTemplate mapTemplate, String userId) {
    return mapService.createMap(mapTemplate, userId)
        .thenRun(() -> System.out.println("Map created successfully"));
  }


  /**
   * Deletes a map identified by its unique map ID.
   *
   * @param mapId the ID of the map to delete.
   * @throws IllegalArgumentException if the provided map ID is null or empty
   * @throws RuntimeException         if an error occurs while retrieving the map
   * @throws NullPointerException     if the provided map ID is null
   * @author ohandsch
   */
  public void deleteMap(String mapId) {
    mapService.deleteMap(mapId)
        .thenRun(() -> System.out.println("Map deleted successfully"));
  }


  /**
   * Retrieves all maps created by a specific user.
   *
   * @param userId the user ID whose maps are to be retrieved.
   * @return a CompletableFuture that completes with a list of MapTemplates.
   * @throws IllegalArgumentException if the provided map ID is null or empty
   * @throws RuntimeException         if an error occurs while retrieving the map
   * @throws NullPointerException     if the provided map ID is null
   * @author ohandsch
   */
  public CompletableFuture<List<MapTemplate>> getAllMapsByUser(String userId) {
    return mapService.getAllMapsByUser(userId)
        .thenApply(maps -> {
          System.out.println("Maps fetched successfully: " + maps);
          return maps;
        });
  }


}


