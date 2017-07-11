package com.ninety8point6.droptoken.concepts;

import com.ninety8point6.droptoken.BuildConfig;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import static com.ninety8point6.droptoken.concepts.GameState.PLAYER_1;
import static com.ninety8point6.droptoken.concepts.GameState.PLAYER_2;

/**
 * A suite of tests to verify the {@link GameState} has the expected behavior.
 *
 * @see GameState
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class GameStateTests {

    @Test(expected = IllegalArgumentException.class)
    public void testGameStateChecksNullKey() {
        new GameState(null, PLAYER_1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGameStateChecksEmptyKey() {
        new GameState("", PLAYER_1);
    }

    @Test(expected = NullPointerException.class)
    public void testGameStateChecksMoves() {
        new GameState("key", null, PLAYER_1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGameStateChecksPlayer() {
        new GameState("key", 2);
    }

    @Test
    public void testGameStateHasTheExpectedValues() {

        final String key = "key";
        final List<Integer> moves = Arrays.asList(0, 1, 2, 3);
        final int player = PLAYER_1;

        final GameState board = new GameState(key, moves, player);

        Assert.assertEquals(key, board.key());
        Assert.assertEquals(moves, board.moves());
        Assert.assertEquals(player, board.initialPlayer());
    }

    @Test
    public void testGameStateCurrentPlayer() {
        final GameState board = new GameState("key", Arrays.asList(0, 1, 2, 3), PLAYER_1);
        Assert.assertEquals(PLAYER_1, board.currentPlayer());
    }

    @Test
    public void testGameStateLastPlayer() {
        final GameState board = new GameState("key", Arrays.asList(0, 1, 2, 3), PLAYER_1);
        Assert.assertEquals(PLAYER_2, board.lastPlayer());
    }

    @Test
    public void testGameStateNextPlayer() {
        Assert.assertEquals(PLAYER_2, GameState.nextPlayer(PLAYER_1));
        Assert.assertEquals(PLAYER_1, GameState.nextPlayer(PLAYER_2));
    }
}
