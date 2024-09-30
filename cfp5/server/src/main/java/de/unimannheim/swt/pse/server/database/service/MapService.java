package de.unimannheim.swt.pse.server.database.service;

import de.unimannheim.swt.pse.server.database.MapModelMapper;
import de.unimannheim.swt.pse.server.database.dao.MapDAO;
import de.unimannheim.swt.pse.server.database.model.MapModel;
import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Service class for managing map-related operations. This class provides a high-level API over the
 * {@link MapDAO} for performing CRUD operations on map data. It is designed to be accessed through
 * a controller to ensure that all map-related operations are centralized and consistent throughout
 * the application. Direct usage of this service class outside of its controller is discouraged to
 * maintain a clean architecture and proper separation of concerns.
 */
public class MapService {

  /**
   * The DAO responsible for direct database interactions for map data.
   */
  private final MapDAO mapDAO;

  /**
   * Constructs a new instance of the {@link MapService} with the provided {@link MapDAO}.
   *
   * @param mapDAO the DAO responsible for direct database interactions for map data
   */
  public MapService(MapDAO mapDAO) {
    this.mapDAO = mapDAO;
  }

  /**
   * Creates a new map in the database using the provided map template and associates it with a
   * user.
   *
   * @param mapTemplate The template from which to create the map.
   * @param userId      The user ID to associate with the map (typically the creator).
   * @return A CompletableFuture that completes when the map has been created in the database.
   * @throws RuntimeException         If an error occurs during the map creation process.
   * @throws NullPointerException     If the map template is null.
   * @throws IllegalArgumentException If the user ID is null or empty.
   * @author ohandsch
   */
  public CompletableFuture<Void> createMap(MapTemplate mapTemplate, String userId) {
    if (mapTemplate == null) {
      throw new NullPointerException("Map template cannot be null");
    }
    if (userId == null || StringUtils.isBlank(userId)) {
      throw new IllegalArgumentException("User ID cannot be null or empty");
    }
    MapModel mapModel = MapModelMapper.toMapsModel(mapTemplate, userId);
    mapModel.setCreatedBy(userId);
    try {
      return mapDAO.addMap(mapModel, userId)
          .exceptionally(ex -> {
            System.err.println("Failed to create map: " + ex.getMessage());
            throw new RuntimeException("Failed to create map", ex);
          });
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Deletes a map from the database based on its ID.
   *
   * @param mapId The unique identifier of the map to delete.
   * @return A CompletableFuture that completes when the map has been deleted from the database.
   * @throws RuntimeException         If an error occurs during the map deletion process.
   * @throws NullPointerException     If the map ID is null.
   * @throws IllegalArgumentException If the map ID is empty.
   * @author ohandsch
   */
  public CompletableFuture<Void> deleteMap(String mapId) {
    if (mapId == null) {
      throw new NullPointerException("Map ID cannot be null");
    }
    if (StringUtils.isBlank(mapId)) {
      throw new IllegalArgumentException("Map ID cannot be empty");
    }
    return mapDAO.deleteMap(mapId)
        .exceptionally(ex -> {
          System.err.println("Failed to delete map: " + ex.getMessage());
          throw new RuntimeException("Failed to delete map", ex);
        });
  }

  /**
   * Retrieves all maps created by a specific user.
   *
   * @param userId The user ID whose maps are to be retrieved.
   * @return A CompletableFuture that, when completed, returns a list of MapTemplates for all maps
   * created by the user.
   * @throws RuntimeException         If an error occurs during the retrieval process.
   * @throws NullPointerException     If the user ID is null.
   * @throws IllegalArgumentException If the user ID is empty.
   * @author ohandsch
   */
  public CompletableFuture<List<MapTemplate>> getAllMapsByUser(String userId) {
    if (userId == null) {
      throw new NullPointerException("User ID cannot be null");
    }
    if (StringUtils.isBlank(userId)) {
      throw new IllegalArgumentException("User ID cannot be empty");
    }
    return mapDAO.getAllMapsByUser(userId)
        .thenApply(mapsModels -> mapsModels.stream()
            .map(MapModelMapper::toMapTemplate)
            .collect(Collectors.toList()))
        .exceptionally(ex -> {
          System.err.println("Failed to retrieve maps for user " + userId + ": " + ex.getMessage());
          throw new RuntimeException("Failed to retrieve maps for user", ex);
        });
  }


}

