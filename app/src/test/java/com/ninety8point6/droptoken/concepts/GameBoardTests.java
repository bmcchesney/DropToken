package com.ninety8point6.droptoken.concepts;

import com.ninety8point6.droptoken.BuildConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

/**
 * A suite of tests to verify the {@link GameBoard} has the expected behavior.
 *
 *  TODO: Add comprehensive tests for isLocationValid, row, col, and diagonal game over scenarios
 *
 * @see GameBoard
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class GameBoardTests {

    @Test(expected = IllegalArgumentException.class)
    public void testGameBoardChecksState() {
        new GameBoard(null);
    }

    @Test
    public void testIsLocationValid() {
        final List<Integer> moves = Arrays.asList(0, 0, 0);
        final GameBoard board = new GameBoard(new GameState("key", moves, GameState.PLAYER_1));
        Assert.assertTrue(board.isLocationValid(new TokenLocation(0)));
    }

    @Test
    public void testIsLocationInValid() {
        final List<Integer> moves = Arrays.asList(0, 0, 0, 0);
        final GameBoard board = new GameBoard(new GameState("key", moves, GameState.PLAYER_1));
        Assert.assertFalse(board.isLocationValid(new TokenLocation(0)));
    }

    @Test
    public void testIsGameOverRows() {
        final List<Integer> moves = Arrays.asList(0, 0, 1, 0, 2, 0, 3);
        final GameBoard board = new GameBoard(new GameState("key", moves, 0));
        Assert.assertTrue(GameBoard.GameOverType.WIN.equals(board.isGameOver(0)));
    }

    @Test
    public void testIsGameOverColumns() {
        final List<Integer> moves = Arrays.asList(0, 1, 0, 2, 0, 3, 0);
        final GameBoard board = new GameBoard(new GameState("key", moves, 0));
        Assert.assertTrue(GameBoard.GameOverType.WIN.equals(board.isGameOver(0)));
    }

    @Test
    public void testIsGameOverFirstDiagonal() {
        final List<Integer> moves = Arrays.asList(0, 1, 1, 2, 0, 2, 2, 3, 3, 3, 3);
        final GameBoard board = new GameBoard(new GameState("key", moves, 0));
        Assert.assertTrue(GameBoard.GameOverType.WIN.equals(board.isGameOver(0)));
    }

    @Test
    public void testIsGameOverSecondDiagonal() {
        final List<Integer> moves = Arrays.asList(3, 2, 2, 1, 3, 1, 1, 0, 0, 0, 0);
        final GameBoard board = new GameBoard(new GameState("key", moves, 0));
        Assert.assertTrue(GameBoard.GameOverType.WIN.equals(board.isGameOver(0)));
    }

    @Test
    public void testIsGameOverDraw() {
        final List<Integer> moves = Arrays.asList(0, 0, 0, 0, 1, 2, 3, 1, 1, 1, 2, 2, 2, 3, 3, 3);
        final GameBoard board = new GameBoard(new GameState("key", moves, 0));
        Assert.assertTrue(GameBoard.GameOverType.DRAW.equals(board.isGameOver(0)));
    }

    @Test
    public void testIsNotGameOver() {
        final List<Integer> moves = Arrays.asList(0, 1, 2);
        final GameBoard board = new GameBoard(new GameState("key", moves, 0));
        Assert.assertFalse(GameBoard.GameOverType.WIN.equals(board.isGameOver(0)));
    }
}
