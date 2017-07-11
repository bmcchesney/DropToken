package com.ninety8point6.droptoken.concepts;

import android.util.SparseIntArray;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import java.util.List;

import javax.annotation.concurrent.Immutable;

/**
 * An immutable container for storing the current state of the Game's board, which is represented by
 * a 4 x 4 matrix.
 */
@Immutable
public final class GameBoard {

    /**
     * An enumeration to track the possible outcomes of a "game over" check. This allows us to scan
     * the board's contents once and determine whether the game is over.
     */
    public enum GameOverType {
        NONE,
        DRAW,
        WIN
    }

    private static final int ROWS = 4;
    private static final int COLS = 4;

    private final int[][] mBoard;
    private final List<Integer> mMoves;

    /**
     * Builds a new {@link GameBoard}.
     *
     * @param state the current {@link GameState}
     */
    public GameBoard(final GameState state) {
        Preconditions.checkArgument(state != null);
        mBoard = buildBoard(state);
        mMoves = state.moves();
    }

    /**
     * Determine whether the provided {@link TokenLocation} is a valid move. This checks whether the
     * current column is full.
     *
     * @param location the {@link TokenLocation} to check
     *
     * @return {@code true} if the location is valid; otherwise {@code false}
     */
    public boolean isLocationValid(final TokenLocation location) {

        Preconditions.checkArgument(location != null);

        final int col = location.column();

        int tokens = 0;
        for (int i = 0; i < ROWS; i++) {
            if (mBoard[i][col] >= 0) {
                tokens++;
            }
        }

        return tokens < ROWS;
    }

    /**
     * Determine whether the provided {@link TokenLocation} constitutes a "game over" scenario.
     *
     * TODO: Break this up and consider simplifying
     *
     * @param player the last player to take a turn
     *
     * @return a {@link GameOverType} which indicates the outcome of the check
     */
    public GameOverType isGameOver(final int player) {

        if (mMoves.isEmpty()) {
            return GameOverType.NONE;
        }

        final int col = Iterables.getLast(mMoves);

        /*
         * Columns - Iterate through the column of the previous move to check for ownership.
         */

        int tokens = 0;
        for (int i = 0; i < ROWS; i++) {
            if (mBoard[i][col] == player) {
                tokens++;
            }
        }

        if (tokens == ROWS) {
            return GameOverType.WIN;
        }

        /*
         * Rows - We have to calculate the row of the previous move before beginning. Once we have that
         * we can iterate across the row checking for ownership. Similar logic as #buildBoard applies
         * here for correctly mapping row values based on our 0,0 top left location.
         */

        int row = ROWS;
        for (final int move : mMoves) {
            if (move == col) {
                --row;
            }
        }

        tokens = 0;
        for (int i = 0; i < COLS; i++) {
            if (mBoard[row][i] == player) {
                tokens++;
            }
        }

        if (tokens == COLS) {
            return GameOverType.WIN;
        }

        /*
         * Diagonals - Check this each time as it is possible to fill in any of the tokens along the
         * diagonals. Use a single loop to cover both at the same time using inc/dec counters.
         */

        int diagonal1 = 0;
        int diagonal2 = 0;
        for (int i = 0, j = COLS - 1; i < COLS; i++, j--) {
            if (mBoard[i][i] == player) {
                diagonal1++;
            }

            if (mBoard[j][i] == player) {
                diagonal2++;
            }
        }

        if (diagonal1 == COLS || diagonal2 == COLS) {
            return GameOverType.WIN;
        }

        /*
         * If we haven't encountered a winning scenario and the board is full then its has to result
         * in a draw; otherwise the game is still in play and may continue.
         */

        if (mMoves.size() > (ROWS * COLS) - 1) {
            return GameOverType.DRAW;
        }

        return GameOverType.NONE;
    }

    /**
     * Build a new game board based off the provided {@link GameState}.
     *
     * @param state the current {@link GameState}
     *
     * @return a newly initialized game board (with token ownership applied)
     */
    private static int[][] buildBoard(final GameState state) {

        final int[][] board = initializeBoard();
        final SparseIntArray tokenCounts = new SparseIntArray();

        int currentPlayer = state.initialPlayer();
        for (final int move : state.moves()) {

            // Use an initial value of total rows and decrement to correctly set token ownership.
            // [0][0] is top left, but we need to fill tokens from the bottom up.
            int count = tokenCounts.get(move, ROWS);
            board[--count][move] = currentPlayer;
            tokenCounts.put(move, count);

            currentPlayer = GameState.nextPlayer(currentPlayer);
        }

        return board;
    }

    /**
     * @return a newly initialized game board (-1 signals no ownership)
     */
    private static int[][] initializeBoard() {
        final int[][] board = new int[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = -1;
            }
        }
        return board;
    }
}
