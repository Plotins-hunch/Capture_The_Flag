package de.unimannheim.swt.pse.server.database;

import de.unimannheim.swt.pse.server.database.model.MapModel;
import de.unimannheim.swt.pse.server.database.model.MapModel.MapMovement;
import de.unimannheim.swt.pse.server.database.model.MapModel.MapPieceDescription;
import de.unimannheim.swt.pse.server.database.model.MapModel.MapShape;
import de.unimannheim.swt.pse.server.database.model.MapModel.MapsDirections;
import de.unimannheim.swt.pse.server.game.map.Directions;
import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import de.unimannheim.swt.pse.server.game.map.Movement;
import de.unimannheim.swt.pse.server.game.map.PieceDescription;
import de.unimannheim.swt.pse.server.game.map.PlacementType;
import de.unimannheim.swt.pse.server.game.map.Shape;
import de.unimannheim.swt.pse.server.game.map.ShapeType;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Utility class for converting between MapModel and MapTemplate data structures. This class
 * provides static methods to transform data from one format to another, facilitating easy data
 * manipulation and interaction with Firebase database operations.
 */
public class MapModelMapper {

  /**
   * Converts a MapModel object into a MapTemplate object. This method maps all relevant fields from
   * a MapModel to a new MapTemplate, including nested structures.
   *
   * @param mapModel the MapModel to convert.
   * @return the converted MapTemplate object.
   * @throws RuntimeException if any errors occur during the conversion process (e.g., null values
   *                          in expected fields).
   * @author ohandsch
   */
  public static MapTemplate toMapTemplate(MapModel mapModel) {
    MapTemplate mapTemplate = new MapTemplate();
    mapTemplate.setBlocks(mapModel.getBlocks());
    mapTemplate.setFlags(mapModel.getFlags());
    mapTemplate.setGridSize(mapModel.getGridSize());
    ArrayList<PieceDescription> pieces = new ArrayList<>();
    for (MapPieceDescription piece : mapModel.getPieces()) {
      PieceDescription pieceDescription = new PieceDescription();
      pieceDescription.setAttackPower(piece.getAttackPower());
      pieceDescription.setCount(piece.getCount());
      pieceDescription.setType(piece.getType());
      Movement movement = new Movement();
      Directions directions = getDirections(piece);
      movement.setDirections(directions);

      if (!Objects.equals(piece.getMovement().getShape().getType(), "default")) {
        Shape shape = new Shape();
        shape.setType(ShapeType.lshape);
        movement.setShape(shape);
      }
      pieceDescription.setMovement(movement);
      pieces.add(pieceDescription);
      mapTemplate.setPieces(pieces.toArray(new PieceDescription[0]));
    }
    switch (mapModel.getPlacement()) {
      case "symmetrical":
        mapTemplate.setPlacement(PlacementType.symmetrical);
        break;
      case "spaced_out":
        mapTemplate.setPlacement(PlacementType.spaced_out);
        break;
      case "defensive":
        mapTemplate.setPlacement(PlacementType.defensive);
        break;
      default:
        throw new RuntimeException("Invalid placement type: " + mapModel.getPlacement());
    }
    mapTemplate.setMoveTimeLimitInSeconds(mapModel.getMoveTimeLimitInSeconds());
    mapTemplate.setTeams(mapModel.getTeams());
    mapTemplate.setTotalTimeLimitInSeconds(mapModel.getTotalTimeLimitInSeconds());
    return mapTemplate;
  }

  /**
   * Helper method to extract direction data from a MapPieceDescription object.
   *
   * @param piece the MapPieceDescription containing the direction data.
   * @return a Directions object populated with direction data from the piece.
   * @author ohandsch
   */
  private static Directions getDirections(MapPieceDescription piece) {
    Directions directions = new Directions();
    directions.setDown(piece.getMovement().getDirections().getDown());
    directions.setLeft(piece.getMovement().getDirections().getLeft());
    directions.setDownLeft(piece.getMovement().getDirections().getDownLeft());
    directions.setUp(piece.getMovement().getDirections().getUp());
    directions.setRight(piece.getMovement().getDirections().getRight());
    directions.setDownRight(piece.getMovement().getDirections().getDownRight());
    directions.setUpLeft(piece.getMovement().getDirections().getUpLeft());
    directions.setUpRight(piece.getMovement().getDirections().getUpRight());
    return directions;
  }

  /**
   * Converts a MapTemplate object into a MapModel object, assigning it to a specified user by
   * userId. This method maps all relevant fields from a MapTemplate to a new MapModel, including
   * complex nested structures.
   *
   * @param mapTemplate the MapTemplate to convert.
   * @param userId      the user ID to set as the creator of the resulting MapModel.
   * @return the converted MapModel object.
   * @throws RuntimeException if any errors occur during the conversion process (e.g., null values
   *                          in expected fields).
   * @author ohandsch
   */
  public static MapModel toMapsModel(MapTemplate mapTemplate, String userId) {

    MapModel mapModel = new MapModel();
    mapModel.setCreatedBy(userId);
    mapModel.setBlocks(mapTemplate.getBlocks());
    mapModel.setFlags(mapTemplate.getFlags());
    mapModel.setGridSize(mapTemplate.getGridSize());
    ArrayList<MapPieceDescription> pieces = new ArrayList<>();
    for (PieceDescription piece : mapTemplate.getPieces()) {
      MapPieceDescription pieceDescription = new MapPieceDescription();
      pieceDescription.setAttackPower(piece.getAttackPower());
      pieceDescription.setCount(piece.getCount());
      pieceDescription.setType(piece.getType());
      MapMovement mapMovement = new MapMovement();
      MapsDirections directions = getMapsDirections(
          piece);
      mapMovement.setDirections(directions);
      MapShape shape = new MapShape();
      if (piece.getMovement().getShape() != null) {
        if (piece.getMovement().getShape().getType() != null) {
          shape.setType(piece.getMovement().getShape().getType().toString());
        } else {
          shape.setType("default");
        }
      } else {
        shape.setType("default");
      }
      mapMovement.setShape(shape);
      pieceDescription.setMovement(mapMovement);
      pieces.add(pieceDescription);
      mapModel.setPieces(pieces.toArray(new MapPieceDescription[0]));
    }
    mapModel.setPlacement(mapTemplate.getPlacement().toString());
    mapModel.setMoveTimeLimitInSeconds(mapTemplate.getMoveTimeLimitInSeconds());
    mapModel.setTeams(mapTemplate.getTeams());
    mapModel.setTotalTimeLimitInSeconds(mapTemplate.getTotalTimeLimitInSeconds());
    return mapModel;
  }

  /**
   * Helper method to extract direction data from a PieceDescription object.
   *
   * @param piece the PieceDescription containing the direction data.
   * @return a MapsDirections object populated with direction data from the piece.
   */
  private static MapsDirections getMapsDirections(PieceDescription piece) {
    MapsDirections directions = new MapsDirections();
    if (piece.getMovement().getDirections() == null) {
      directions.setDown(0);
      directions.setLeft(0);
      directions.setDownLeft(0);
      directions.setUp(0);
      directions.setRight(0);
      directions.setDownRight(0);
      directions.setUpLeft(0);
      directions.setUpRight(0);
      return directions;
    }
    directions.setDown(piece.getMovement().getDirections().getDown());
    directions.setLeft(piece.getMovement().getDirections().getLeft());
    directions.setDownLeft(piece.getMovement().getDirections().getDownLeft());
    directions.setUp(piece.getMovement().getDirections().getUp());
    directions.setRight(piece.getMovement().getDirections().getRight());
    directions.setDownRight(piece.getMovement().getDirections().getDownRight());
    directions.setUpLeft(piece.getMovement().getDirections().getUpLeft());
    directions.setUpRight(piece.getMovement().getDirections().getUpRight());
    return directions;
  }
}
