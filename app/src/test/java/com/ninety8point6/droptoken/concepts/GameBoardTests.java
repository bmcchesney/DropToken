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
 * @see GameBoard
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class GameBoardTests {

    // test rows

    // test columns

    @Test
    public void testIsGameOverFirstDiagonal() {
        final List<Integer> moves = Arrays.asList(0, 1, 1, 2, 0, 2, 2, 3, 3, 3, 3);
        final GameBoard board = new GameBoard(new GameState("Testing", moves, 0));
        Assert.assertTrue(GameBoard.GameOverType.WIN.equals(board.isGameOver(0)));
    }

    // test first diagonal, out of order

    @Test
    public void testIsGameOverSecondDiagonal() {
        final List<Integer> moves = Arrays.asList(3, 2, 2, 1, 3, 1, 1, 0, 0, 0, 0);
        final GameBoard board = new GameBoard(new GameState("Testing", moves, 0));
        Assert.assertTrue(GameBoard.GameOverType.WIN.equals(board.isGameOver(0)));
    }

    // test second diagonal, out of order

    // test draws (player + computer)

    // test continue
}
