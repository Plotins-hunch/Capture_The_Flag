package de.unimannheim.swt.pse.server.game;

import de.unimannheim.swt.pse.server.game.map.MapTemplate;
import de.unimannheim.swt.pse.server.game.state.GameState;
import de.unimannheim.swt.pse.server.game.state.Move;
import de.unimannheim.swt.pse.server.game.state.Team;
import java.util.Date;

/**
 * This is a dummy game that does nothing. Remove this class and provide your own implementation of {@link Game}.
 */
public class DummyGame implements Game {


    /**
     * Initializes a new game based on given {@link MapTemplate} configuration.
     *
     * @param template {@link MapTemplate}
     * @return GameState
     */
    @Override
    public GameState create(MapTemplate template) {
        return null;
    }

    /**
     * Get current state of the game
     *
     * @return GameState
     */
    @Override
    public GameState getCurrentGameState() {
        return null;
    }

    /**
     * Updates a game and its state based on team join request (add team).
     *
     * <ul>
     *     <li>adds team if a slot is free (array element is null)</li>
     *     <li>if all team slots are finally assigned, implicitly starts the game by picking a starting team at random</li>
     * </ul>
     *
     * @param teamId Team ID
     * @return Team
     * @throws NoMoreTeamSlots No more team slots available
     */
    @Override
    public Team joinGame(String teamId) {
        return null;
    }

    /**
     * @return number of remaining team slots
     */
    @Override
    public int getRemainingTeamSlots() {
        return 0;
    }

    /**
     * @return -1 if no total game time limit set, 0 if over, > 0 if seconds remain
     */
    @Override
    public int getRemainingGameTimeInSeconds() {
        return 0;
    }

    /**
     * @return -1 if no move time limit set, 0 if over, > 0 if seconds remain
     */
    @Override
    public int getRemainingMoveTimeInSeconds() {
        return 0;
    }

    /**
     * A team has to option to give up a game (i.e., losing the game as a result).
     * <p>
     * Assume that a team can only give up if it is its move (turn).
     *
     * @param teamId Team ID
     */
    @Override
    public void giveUp(String teamId) {

    }

    /**
     * Checks whether the game is started based on the current {@link GameState}.
     *
     * <ul>
     *     <li>{@link Game#isGameOver()} == false</li>
     *     <li>{@link Game#getCurrentGameState()} != null</li>
     * </ul>
     *
     * @return
     */
    @Override
    public boolean isStarted() {
        return false;
    }

    /**
     * Checks whether the game is over based on the current {@link GameState}.
     *
     * @return true if game is over, false if game is still running.
     */
    @Override
    public boolean isGameOver() {
        return false;
    }

    /**
     * Get winner(s) (if any)
     *
     * @return {@link Team#getId()} if there is a winner
     */
    @Override
    public String[] getWinner() {
        return new String[0];
    }

    /**
     * @return Start {@link Date} of game
     */
    @Override
    public Date getStartedDate() {
        return null;
    }

    /**
     * @return End date of game
     */
    @Override
    public Date getEndDate() {
        return null;
    }

    /**
     * Checks whether a move is valid based on the current game state.
     *
     * @param move {@link Move}
     * @return true if move is valid based on current game state, false otherwise
     */
    @Override
    public boolean isValidMove(Move move) {
        return false;
    }

    /**
     * Make a move
     *
     * @param move {@link Move}
     * @throws InvalidMove Requested move is invalid
     * @throws GameOver    Game is over
     */
    @Override
    public void makeMove(Move move) {

    }
}
