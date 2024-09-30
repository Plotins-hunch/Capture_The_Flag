package de.unimannheim.swt.pse.ai.minimax;

import static java.lang.Integer.valueOf;

import de.unimannheim.swt.pse.ai.minimax.MinimaxAlgorithm.Pair;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Piece;
import de.unimannheim.swt.pse.server.game.state.Team;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that evaluates the current state of the game based on different evaluation methods
 */
public class HeuristicEvaluator {

  private static int distance;

  /**
   * Evaluates the current state of the game
   *
   * @param state the current state of the game
   * @return the evaluation of the current state
   * @author rkonradt
   */
  public static int evaluate(GameState state, boolean isMaximizing) {

    int evaluateCounter = 0;

    evaluateCounter += countPieces(state, isMaximizing);
    if (evaluateCounter >= Math.abs(100)) {
      return evaluateCounter;
    }

    evaluateCounter += countFlags(state, isMaximizing);

    if (evaluateCounter >= Math.abs(100)) {
      return evaluateCounter;
    }

    evaluateCounter += attackabilityOfOpponentsFlag(state, isMaximizing);

    if (evaluateCounter >= Math.abs(100)) {
      return evaluateCounter;
    }

    evaluateCounter += protectionOfMyFlag(state, isMaximizing);

    return evaluateCounter;
  }


  /**
   * Counts the pieces of each team
   *
   * @param state        the current state of the game
   * @param isMaximizing true if the current team is the maximizing team
   * @return the difference between the current team and the other teams
   * @author rkonradt
   */
  public static int countPieces(GameState state, boolean isMaximizing) {
    List<Piece[]> pieces = new ArrayList<>();
    int count = 0;
    for (Team t : state.getTeams()) {
      pieces.add(t.getPieces());
    }

    int[] countPieces = new int[state.getTeams().length];
    int a = 0;
    for (Piece[] p : pieces) {
      countPieces[a] = p.length;
      a++;
    }

    int currentTeamPositionInCountPieces = 0;
    int j = 0;
    for (Team t : state.getTeams()) {
      if (t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
        currentTeamPositionInCountPieces = j;
      }
      j++;
    }

    //checks if we need to maximize or minimize the evaluation
    if (!isMaximizing) {
      //if the current team has no pieces left, the game is over
      for (Team t : state.getTeams()) {
        if (t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
          if (t.getPieces().length == 0) {
            return 100;
          }
        } else {
          if (t.getPieces().length == 0) {
            count -= 100 / state.getTeams().length - 1;
            if (Math.abs(count) >= 100) {
              return count;
            }
          }
        }
      }

      for (int i = 0; i < state.getTeams().length; i++) {
        if (currentTeamPositionInCountPieces == i) {
          continue;
        }
        if (countPieces[currentTeamPositionInCountPieces] > countPieces[i]) {
          count += (-15 - (countPieces[currentTeamPositionInCountPieces] - countPieces[i]));
          if (Math.abs(count) >= 100) {
            return count;
          }
        } else if (countPieces[currentTeamPositionInCountPieces] < countPieces[i]) {
          count += (15 + (countPieces[i] - countPieces[currentTeamPositionInCountPieces]));
          if (Math.abs(count) >= 100) {
            return count;
          }
        }
      }
    } else {
      //if the current team has no pieces left, the game is over
      for (Team t : state.getTeams()) {
        if (t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
          if (t.getPieces().length == 0) {
            return -100;
          }
        } else {
          if (t.getPieces().length == 0) {
            count += 100 / state.getTeams().length - 1;
            if (Math.abs(count) >= 100) {
              return count;
            }
          }
        }
      }

      for (int i = 0; i < state.getTeams().length; i++) {
        if (currentTeamPositionInCountPieces == i) {
          continue;
        }
        if (countPieces[currentTeamPositionInCountPieces] > countPieces[i]) {
          count += (15 + (countPieces[currentTeamPositionInCountPieces] - countPieces[i]));
          if (Math.abs(count) >= 100) {
            return count;
          }
        } else if (countPieces[currentTeamPositionInCountPieces] < countPieces[i]) {
          count += (-15 - (countPieces[i] - countPieces[currentTeamPositionInCountPieces]));
          if (Math.abs(count) >= 100) {
            return count;
          }
        }
      }
    }
    return count;
  }

  /**
   * Counts the flags of each team
   *
   * @param state        the current state of the game
   * @param isMaximizing true if the current team is the maximizing team
   * @return the difference between the current team and the other teams
   * @author rkonradt
   */
  public static int countFlags(GameState state, boolean isMaximizing) {
    int count = 0;

    int[] flags = new int[state.getTeams().length];
    int i = 0;
    int currentTeamPositionInFlags = 0;
    for (Team t : state.getTeams()) {
      flags[i] = t.getFlags();
      if (t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
        currentTeamPositionInFlags = i;
      }
      i++;
    }

    //checks if we need to maximize or minimize the evaluation
    if (!isMaximizing) {
      for (Team t : state.getTeams()) {
        if (t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
          if (t.getFlags() == 0) {
            return 100;
          }
        } else {
          if (t.getFlags() == 0) {
            count -= (100 / (state.getTeams().length - 1));
          }
          if (Math.abs(count) >= 100) {
            return count;
          }
        }
      }

      for (int j = 0; j < flags.length; j++) {
        if (currentTeamPositionInFlags == j) {
          continue;
        }
        if (flags[currentTeamPositionInFlags] > flags[j]) {
          count += -40 - ((flags[currentTeamPositionInFlags] - flags[j]) * 5);
          if (Math.abs(count) >= 100) {
            return count;
          }
        } else if (flags[currentTeamPositionInFlags] < flags[j]) {
          count += 40 + ((flags[j] - flags[currentTeamPositionInFlags]) * 5);
          if (Math.abs(count) >= 100) {
            return count;
          }
        }
      }
    } else {
      for (Team t : state.getTeams()) {
        if (t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
          if (t.getFlags() == 0) {
            return -100;
          }
        } else {
          if (t.getFlags() == 0) {
            count += (100 / state.getTeams().length - 1);
          }
          if (Math.abs(count) >= 100) {
            return count;
          }
        }
      }

      for (int j = 0; j < flags.length; j++) {
        if (currentTeamPositionInFlags == j) {
          continue;
        }
        if (flags[currentTeamPositionInFlags] > flags[j]) {
          count += 40 + ((flags[currentTeamPositionInFlags] - flags[j]) * 5);
          if (Math.abs(count) >= 100) {
            return count;
          }
        } else if (flags[currentTeamPositionInFlags] < flags[j]) {
          count += -40 - ((flags[j] - flags[currentTeamPositionInFlags]) * 5);
          if (Math.abs(count) >= 100) {
            return count;
          }
        }
      }
    }
    return count;
  }

  /**
   * Evaluates the attackability of the opponents flag by the current team
   *
   * @param state        the current state of the game
   * @param isMaximizing true if the current team is the maximizing team
   * @return the evaluation of the attackability of the opponents flag
   * @author rkonradt
   */
  public static int attackabilityOfOpponentsFlag(GameState state, boolean isMaximizing) {
    int count = 0;

    //checks if we need to maximize or minimize the evaluation
    if (!isMaximizing) {
      if (myPieceNearestDistanceToOpponentsFlag(state).first
          < opponentsPieceNearestDistanceToOpponentsFlag(state).first) {
        count -= 10;
      } else if (myPieceNearestDistanceToOpponentsFlag(state).first
          > opponentsPieceNearestDistanceToOpponentsFlag(state).first) {
        count += 5;
      }

      if (myPiecesNearOpponentsFlag(state).size() > opponentsPiecesNearOpponentsFlag(
          state).size()) {
        count -= 1;
      } else if (myPiecesNearOpponentsFlag(state).size() < opponentsPiecesNearOpponentsFlag(
          state).size()) {
        count += 1;
      }

      if (myPieceNearestDistanceToOpponentsFlag(state).first < (state.getGrid().length / 3)) {
        if (myPieceNearestDistanceToOpponentsFlag(state).second.getDescription().getAttackPower()
            > opponentsPieceNearestDistanceToOpponentsFlag(state).second.getDescription()
            .getAttackPower()) {
          count -= 10;
        } else if (
            myPieceNearestDistanceToOpponentsFlag(state).second.getDescription().getAttackPower()
                < opponentsPieceNearestDistanceToOpponentsFlag(state).second.getDescription()
                .getAttackPower()) {
          count += 5;
        }
      }
    } else {
      if (myPieceNearestDistanceToOpponentsFlag(state).first
          < opponentsPieceNearestDistanceToOpponentsFlag(state).first) {
        count += 10;
      } else if (myPieceNearestDistanceToOpponentsFlag(state).first
          > opponentsPieceNearestDistanceToOpponentsFlag(state).first) {
        count -= 5;
      }

      if (myPiecesNearOpponentsFlag(state).size() > opponentsPiecesNearOpponentsFlag(
          state).size()) {
        count += 1;
      } else if (myPiecesNearOpponentsFlag(state).size() < opponentsPiecesNearOpponentsFlag(
          state).size()) {
        count -= 1;
      }

      if (myPieceNearestDistanceToOpponentsFlag(state).first < (state.getGrid().length / 3)) {
        if (myPieceNearestDistanceToOpponentsFlag(state).second.getDescription().getAttackPower()
            > opponentsPieceNearestDistanceToOpponentsFlag(state).second.getDescription()
            .getAttackPower()) {
          count += 10;
        } else if (
            myPieceNearestDistanceToOpponentsFlag(state).second.getDescription().getAttackPower()
                < opponentsPieceNearestDistanceToOpponentsFlag(state).second.getDescription()
                .getAttackPower()) {
          count -= 5;
        }
      }
    }
    return count;
  }

  /**
   * Evaluates the protection of the current teams flag by the current team
   *
   * @param state        the current state of the game
   * @param isMaximizing true if the current team is the maximizing team
   * @return the evaluation of the protection of the current teams flag
   * @author rkonradt
   */
  public static int protectionOfMyFlag(GameState state, boolean isMaximizing) {
    int count = 0;

    //checks if we need to maximize or minimize the evaluation
    if (!isMaximizing) {
      if (myPieceNearestDistanceToMyFlag(state).first < opponentsPieceNearestDistanceToMyFlag(
          state).first) {
        count -= 10;
      } else if (myPieceNearestDistanceToMyFlag(state).first
          > opponentsPieceNearestDistanceToMyFlag(state).first) {
        count += 5;
      }

      if (myPiecesNearMyFlag(state).size() > opponentsPiecesNearMyFlag(state).size()) {
        count -= 1;
      } else if (myPiecesNearMyFlag(state).size() < opponentsPiecesNearMyFlag(state).size()) {
        count += 1;
      }

      if (opponentsPieceNearestDistanceToMyFlag(state).first < (state.getGrid().length / 3)) {
        if (myPieceNearestDistanceToMyFlag(state).second.getDescription().getAttackPower()
            > opponentsPieceNearestDistanceToMyFlag(state).second.getDescription()
            .getAttackPower()) {
          count -= 10;
        } else if (myPieceNearestDistanceToMyFlag(state).second.getDescription().getAttackPower()
            < opponentsPieceNearestDistanceToMyFlag(state).second.getDescription()
            .getAttackPower()) {
          count += 5;
        }
      }
      if (myFlagBetweenNearestPieces(state)) {
        count += 20;
      }

    } else {
      if (myPieceNearestDistanceToMyFlag(state).first < opponentsPieceNearestDistanceToMyFlag(
          state).first) {
        count += 10;
      } else if (myPieceNearestDistanceToMyFlag(state).first
          > opponentsPieceNearestDistanceToMyFlag(state).first) {
        count -= 5;
      }

      if (myPiecesNearMyFlag(state).size() > opponentsPiecesNearMyFlag(state).size()) {
        count += 1;
      } else if (myPiecesNearMyFlag(state).size() < opponentsPiecesNearMyFlag(state).size()) {
        count -= 1;
      }

      if (opponentsPieceNearestDistanceToMyFlag(state).first < (state.getGrid().length / 3)) {
        if (myPieceNearestDistanceToMyFlag(state).second.getDescription().getAttackPower()
            > opponentsPieceNearestDistanceToMyFlag(state).second.getDescription()
            .getAttackPower()) {
          count += 10;
        } else if (myPieceNearestDistanceToMyFlag(state).second.getDescription().getAttackPower()
            < opponentsPieceNearestDistanceToMyFlag(state).second.getDescription()
            .getAttackPower()) {
          count -= 5;
        }
      }

      if (myFlagBetweenNearestPieces(state)) {
        count -= 20;
      }
    }
    return count;
  }


  /**
   * Calculates the distance of the nearest piece of the current team to the opponents flag
   *
   * @param state the current state of the game
   * @return the distance of the nearest piece of the current team to the opponents flag
   * @author rkonradt
   */
  public static Pair<Integer, Piece> myPieceNearestDistanceToOpponentsFlag(GameState state) {
    int distance = 0;
    Piece piece = null;
    Team[] myTeam = new Team[state.getTeams().length - 1];

    int i = 0;
    for (Team t : state.getTeams()) {
      if (!t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
        myTeam[i] = t;
        i++;
      }
    }

    for (Team t : state.getTeams()) {
      if (t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
        for (Team t2 : myTeam) {
          for (Piece p : t.getPieces()) {
            if (Math.sqrt(Math.pow(p.getPosition()[0] - t2.getBase()[0], 2) +
                Math.pow(p.getPosition()[1] - t2.getBase()[1], 2)) < distance || distance == 0) {
              distance = (int) Math.sqrt(Math.pow(p.getPosition()[0] - t2.getBase()[0], 2) +
                  Math.pow(p.getPosition()[1] - t2.getBase()[1], 2));
              piece = p;
            }
          }
        }
      }
    }

    return new Pair<>(distance, piece);
  }


  /**
   * Calculates the distance of the nearest piece of the current team to the current teams flag
   *
   * @param state the current state of the game
   * @return the distance of the nearest piece of the current team to the current teams flag
   * @author rkonradt
   */
  public static Pair<Integer, Piece> myPieceNearestDistanceToMyFlag(GameState state) {
    Piece piece = null;
    int distance = 0;

    for (Team t : state.getTeams()) {
      if (t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
        for (Piece p : t.getPieces()) {
          if (Math.sqrt(Math.pow(p.getPosition()[0] - t.getBase()[0], 2) +
              Math.pow(p.getPosition()[1] - t.getBase()[1], 2)) < distance || distance == 0) {
            distance = (int) Math.sqrt(Math.pow(p.getPosition()[0] - t.getBase()[0], 2) +
                Math.pow(p.getPosition()[1] - t.getBase()[1], 2));
            piece = p;
          }
        }
      }
    }
    return new Pair<>(distance, piece);
  }

  /**
   * Calculates the distance of the nearest piece of the opponents team to the opponents flag
   *
   * @param state the current state of the game
   * @return the distance of the nearest piece of the opponents team to the opponents flag
   * @author rkonradt
   */
  public static Pair<Integer, Piece> opponentsPieceNearestDistanceToOpponentsFlag(GameState state) {
    Piece piece = null;
    int distance = 0;

    Team[] opponnentTeams = new Team[state.getTeams().length - 1];

    int i = 0;
    for (Team t : state.getTeams()) {
      if (!t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
        opponnentTeams[i] = t;
        i++;
      }
    }

    for (Team t : state.getTeams()) {
      if (!t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
        for (Team t2 : opponnentTeams) {
          for (Piece p : t.getPieces()) {
            if (Math.sqrt(Math.pow(p.getPosition()[0] - t2.getBase()[0], 2) +
                Math.pow(p.getPosition()[1] - t2.getBase()[1], 2)) < distance || distance == 0) {
              distance = (int) Math.sqrt(Math.pow(p.getPosition()[0] - t2.getBase()[0], 2) +
                  Math.pow(p.getPosition()[1] - t2.getBase()[1], 2));
              piece = p;
            }
          }
        }
      }
    }
    return new Pair<>(distance, piece);
  }

  /**
   * Calculates the distance of the nearest piece of the opponents team to the current teams flag
   *
   * @param state the current state of the game
   * @return the distance of the nearest piece of the opponents team to the current teams flag
   * @author rkonradt
   */
  public static Pair<Integer, Piece> opponentsPieceNearestDistanceToMyFlag(GameState state) {
    Piece piece = null;
    int distance = 0;
    Team myTeam = null;

    for (Team t : state.getTeams()) {
      if (t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
        myTeam = t;
      }
    }

    for (Team t : state.getTeams()) {
      if (!t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
        for (Piece p : t.getPieces()) {
          assert myTeam != null;
          if (Math.sqrt(Math.pow(p.getPosition()[0] - myTeam.getBase()[0], 2) +
              Math.pow(p.getPosition()[1] - myTeam.getBase()[1], 2)) < distance || distance == 0) {
            distance = (int) Math.sqrt(Math.pow(p.getPosition()[0] - myTeam.getBase()[0], 2) +
                Math.pow(p.getPosition()[1] - myTeam.getBase()[1], 2));
            piece = p;
          }
        }
      }
    }
    return new Pair<>(distance, piece);
  }

  /**
   * Counts the pieces of the current team that are near the opponents flag
   *
   * @param state the current state of the game
   * @return the pieces of the current team that are near the opponents flag
   * @author rkonradt
   */
  public static List<Piece> myPiecesNearOpponentsFlag(GameState state) {
    List<Piece> myPieces = new ArrayList<>();
    distance = 0;

    for (Team t : state.getTeams()) {
      if (t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
        for (Piece p : t.getPieces()) {
          for (Team t2 : state.getTeams()) {
            if (!t2.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
              distance = (int) Math.sqrt(Math.pow(p.getPosition()[0] - t2.getBase()[0], 2) + Math.pow(
                  p.getPosition()[1] - t2.getBase()[1], 2));
              if (distance <= (double) (state.getGrid().length / 3)) {
                myPieces.add(p);
              }
            }
          }
        }
      }
    }
    return myPieces;
  }

  /**
   * Counts the pieces of the currents team that are near the own flag
   *
   * @param state the current state of the game
   * @return the pieces of the opponents team that are near the opponents flag
   * @author rkonradt
   */
  public static List<Piece> myPiecesNearMyFlag(GameState state) {
    List<Piece> myPieces = new ArrayList<>();
    distance = 0;

    for (Team t : state.getTeams()) {
      if (t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
        for (Piece p : t.getPieces()) {
          distance = (int) Math.sqrt(Math.pow(p.getPosition()[0] - t.getBase()[0], 2) + Math.pow(
              p.getPosition()[1] - t.getBase()[1], 2));
          if (distance <= (double) (state.getGrid().length / 3)) {
            myPieces.add(p);
          }
        }
      }
    }
    return myPieces;
  }

  /**
   * Counts the pieces of the opponents team that are near the opponents flag
   *
   * @param state the current state of the game
   * @return the pieces of the opponents team that are near the opponents flag
   * @author rkonradt
   */
  public static List<Piece> opponentsPiecesNearOpponentsFlag(GameState state) {
    List<Piece> opponentsPieces = new ArrayList<>();
    distance = 0;

    for (Team t : state.getTeams()) {
      if (!t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
        for (Piece p : t.getPieces()) {
          for (Team t2 : state.getTeams()) {
            if (!t2.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
              distance = (int) Math.sqrt(Math.pow(p.getPosition()[0] - t2.getBase()[0], 2) +
                  Math.pow(p.getPosition()[1] - t2.getBase()[1], 2));
              if (distance <= (double) (state.getGrid().length / 3)) {
                opponentsPieces.add(p);
              }
            }
          }
        }
      }
    }
    return opponentsPieces;
  }

  /**
   * Counts the pieces of the opponents team that are near the current teams flag
   *
   * @param state the current state of the game
   * @return the pieces of the opponents team that are near the current teams flag
   * @author rkonradt
   */
  public static List<Piece> opponentsPiecesNearMyFlag(GameState state) {
    List<Piece> opponentsPieces = new ArrayList<>();
    distance = 0;

    for (Team t : state.getTeams()) {
      if (!t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
        for (Piece p : t.getPieces()) {
          for (Team t2 : state.getTeams()) {
            if (t2.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
              distance = (int) Math.sqrt(Math.pow(p.getPosition()[0] - t2.getBase()[0],2) +
                  Math.pow(p.getPosition()[1] - t2.getBase()[1],2));
              if (distance <= (double) (state.getGrid().length / 3)) {
                opponentsPieces.add(p);
              }
            }
          }
        }
      }
    }
    return opponentsPieces;
  }

  /**
   * This method first identifies the nearest pieces from the current team and the opponent's team relative to
   * the flag. It then determines the positions of these pieces in relation to the flag and checks if the flag
   * lies between these pieces.
   *
   * @param state the current state of the game
   * @return true if the flag is between the nearest pieces of the current team and the opponents
   * team
   * @author rkonradt
   */
  public static boolean myFlagBetweenNearestPieces(GameState state) {
    Piece myPiece = myPieceNearestDistanceToMyFlag(state).second;
    Piece opponentsPiece = opponentsPieceNearestDistanceToMyFlag(state).second;
    Team currentTeam = null;

    for (Team t : state.getTeams()) {
      if (t.getId().equals(valueOf(state.getCurrentTeam()).toString())) {
        currentTeam = t;
      }
    }

    //variables to save the x position of the piece depending on the flag
    assert currentTeam != null;
    int myX = myPiece.getPosition()[0] - currentTeam.getBase()[0];
    int opponentsX = opponentsPiece.getPosition()[0] - currentTeam.getBase()[0];

    //variables to save the y position of the piece depending on the flag
    int myY = myPiece.getPosition()[1] - currentTeam.getBase()[1];
    int opponentsY = opponentsPiece.getPosition()[1] - currentTeam.getBase()[1];

    //check if the flag is between the nearest pieces
    if (myX < 0 && opponentsX > 0 && myY < 0 && opponentsY > 0) {
      return true;
    } else if (myX > 0 && opponentsX < 0 && myY > 0 && opponentsY < 0) {
      return true;
    } else if (myX < 0 && opponentsX > 0 && myY > 0 && opponentsY < 0) {
      return true;
    } else if (myX > 0 && opponentsX < 0 && myY < 0 && opponentsY > 0) {
      return true;
    } else if (myX == 0 && opponentsX == 0 && myY < 0 && opponentsY > 0) {
      return true;
    } else if (myX == 0 && opponentsX == 0 && myY > 0 && opponentsY < 0) {
      return true;
    } else if (myX < 0 && opponentsX > 0 && myY == 0 && opponentsY == 0) {
      return true;
    } else {
      return myX > 0 && opponentsX < 0 && myY == 0 && opponentsY == 0;
    }
  }

}
