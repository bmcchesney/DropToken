package com.ninety8point6.droptoken.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.util.Pair;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.ninety8point6.droptoken.R;
import com.ninety8point6.droptoken.concepts.GameState;
import com.ninety8point6.droptoken.concepts.ResponseCallback;
import com.ninety8point6.droptoken.concepts.TokenLocation;

import java.util.Set;

import static com.ninety8point6.droptoken.concepts.GameState.PLAYER_1;

/**
 * A view layer abstraction for encapsulating any view manipulation. All operations should be called
 * from the main thread as UI elements are updated internally.
 * <p/>
 * Note that this layer does not provide state validation. A {@link GameState} cannot get into an
 * invalid state, and the {@link com.ninety8point6.droptoken.concepts.GameManager} is responsible
 * for validation.
 *
 * TODO: Create a more robust 3-state button than can handle current player and state changes
 */
public class GameView implements View.OnClickListener {

    private static final String TAG = "GameView";

    private static final String PLAYER_SELECTION_TAG = "PlayerSelection";

    private static final Set<Integer> TOKEN_BUTTON_RES_IDS = ImmutableSet.<Integer>builder()
            .add(R.id.one_one, R.id.one_two, R.id.one_three, R.id.one_four)
            .add(R.id.two_one, R.id.two_two, R.id.two_three, R.id.two_four)
            .add(R.id.three_one, R.id.three_two, R.id.three_three, R.id.three_four)
            .add(R.id.four_one, R.id.four_two, R.id.four_three, R.id.four_four)
            .build();

    private static final int[][] TOKEN_BUTTON_MAPPING = new int[][] {
            { R.id.four_one, R.id.four_two, R.id.four_three, R.id.four_four },
            { R.id.three_one, R.id.three_two, R.id.three_three, R.id.three_four },
            { R.id.two_one, R.id.two_two, R.id.two_three, R.id.two_four },
            { R.id.one_one, R.id.one_two, R.id.one_three, R.id.one_four }
    };

    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private final View mBoard;
    private final ImageButton mNewGame;
    private final ContentLoadingProgressBar mProgressBar;
    private final TextView mMessage;
    private final Delegate mDelegate;

    /**
     * Builds the {@link GameView} with the provided view components and dependencies.
     *
     * @param context the Android application {@link Context}
     * @param fragmentManager the {@link FragmentManager} used to show dialogs
     * @param board the {@link View} holding the game board
     * @param newGame the {@link ImageButton} for creating a new game
     * @param progressBar the {@link ContentLoadingProgressBar} for showing a busy state
     * @param message the {@link TextView} for showing messages to the user
     * @param delegate the {@link Delegate} for handling UI interactions
     */
    public GameView(final Context context,
                    final FragmentManager fragmentManager,
                    final View board,
                    final ImageButton newGame,
                    final ContentLoadingProgressBar progressBar,
                    final TextView message,
                    final Delegate delegate) {

        mContext = Preconditions.checkNotNull(context);
        mFragmentManager = Preconditions.checkNotNull(fragmentManager);
        mBoard = Preconditions.checkNotNull(board);
        mNewGame = Preconditions.checkNotNull(newGame);
        mProgressBar = Preconditions.checkNotNull(progressBar);
        mMessage = Preconditions.checkNotNull(message);
        mDelegate = Preconditions.checkNotNull(delegate);

        // Use ourselves as a single click listener; internally we'll route operations based on
        // resource ids.
        mNewGame.setOnClickListener(this);
        for (final Integer resId : TOKEN_BUTTON_RES_IDS) {
            mBoard.findViewById(resId).setOnClickListener(this);
        }
    }

    /**
     * Prompt for player selection. This shows a {@link android.app.DialogFragment} for selecting the
     * player that should go first.
     *
     * @param callback a {@link ResponseCallback} for handling the result of the player selection
     */
    public void promptPlayerSelection(final ResponseCallback<Integer, Throwable> callback) {
        PlayerSelectionDialogFragment.create(callback).show(mFragmentManager, PLAYER_SELECTION_TAG);
    }

    /**
     * Set the busy state for the {@link GameView}.
     *
     * TODO: Actually use when CX is added to delay between turns...
     *
     * @param isBusy {@code} true if the busy state should be shown; otherwise {@code false}
     */
    public void setBusyState(final boolean isBusy) {
        if (isBusy) {
            mProgressBar.show();
            mNewGame.setEnabled(false);
            setEnabled(false);
        } else {
            mNewGame.setEnabled(true);
            setEnabled(true);
            mProgressBar.hide();
        }
    }

    /**
     * Set a message for the user. Used to notify about current turn and the state of the game.
     *
     * @param message the message to show
     */
    public void setMessage(final String message) {
        mMessage.setText(Preconditions.checkNotNull(message));
    }

    /**
     * Update the view contents based on {@link GameState} changes.
     *
     * @param state the state to reflect in the view
     * @param isGameOver {@code true} if the game is over; otherwise {@code false}
     */
    public void updateGameView(final GameState state, final boolean isGameOver) {

        if (isGameOver) {
            setEnabled(false);
        } else {
            setMessage(state.currentPlayer() == 0
                    ? mContext.getString(R.string.player_turn_message)
                    : mContext.getString(R.string.computer_turn_message));
        }

        handleStateChange(state);
    }

    /**
     * A catch-all {@link android.view.View.OnClickListener#onClick(View)} for handling interactions.
     *
     * {@inheritDoc}
     */
    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.fab:
                mDelegate.onNewGame();
                break;
            case R.id.one_one:
            case R.id.two_one:
            case R.id.three_one:
            case R.id.four_one:
                handleTokenClick(new TokenLocation(0));
                break;
            case R.id.one_two:
            case R.id.two_two:
            case R.id.three_two:
            case R.id.four_two:
                handleTokenClick(new TokenLocation(1));
                break;
            case R.id.one_three:
            case R.id.two_three:
            case R.id.three_three:
            case R.id.four_three:
                handleTokenClick(new TokenLocation(2));
                break;
            case R.id.one_four:
            case R.id.two_four:
            case R.id.three_four:
            case R.id.four_four:
                handleTokenClick(new TokenLocation(3));
                break;
            default:
                Log.e(TAG, "Unknown resource id.");
                break;
        }
    }

    /**
     * Route a token click event to the correct location. Each column resolves to the same
     * {@link TokenLocation} as you can only "drop" tokens in from the top.
     *
     * @param location the {@link TokenLocation} for the click event
     */
    private void handleTokenClick(final TokenLocation location) {
        mDelegate.onTokenPlayed(location);
    }

    /**
     * Handles the necessary changes when the {@link GameState} is updated. The parses the currently
     * committed moves and determines which tokens are selected, and who owns them.
     *
     * @param state the state to reflect in the view
     */
    private void handleStateChange(final GameState state) {

        // An empty moves list signals a new game
        if (state.moves().size() <= 1) {
            clearBoard();
        }

        // We can use a sparse array to count up tokens in each column and update ownership by
        // alternating the current player (since we know the initial one).
        final SparseIntArray tokenCounts = new SparseIntArray();
        int currentPlayer = state.initialPlayer();

        for (final int move : state.moves()) {
            int count = tokenCounts.get(move, 0);
            setTokenState(currentPlayer, new Pair<>(count, move));
            tokenCounts.put(move, count + 1);
            currentPlayer = GameState.nextPlayer(currentPlayer);
        }
    }

    /**
     * Reset all tokens on the game board.
     */
    private void clearBoard() {
        for (final Integer resId : TOKEN_BUTTON_RES_IDS) {
            mBoard.findViewById(resId).setEnabled(true);
            ViewCompat.setBackgroundTintList(mBoard.findViewById(resId), ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.gray)));
        }
    }

    /**
     * Set the state for a single token. Currently this just updates the background color based on the
     * player who has selected it.
     *
     * @param player the player who has selected the token
     * @param location the location of the token
     */
    private void setTokenState(final int player, final Pair<Integer, Integer> location) {
        final View view = mBoard.findViewById(TOKEN_BUTTON_MAPPING[location.first][location.second]);
        final int colorResId = player == PLAYER_1 ? R.color.player1 : R.color.player2;
        ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(ContextCompat.getColor(mContext, colorResId)));
    }

    /**
     * Set the enabled state for all tokens.
     *
     * @param isEnabled {@code true} to enable; or {@code false} to disable
     */
    private void setEnabled(final boolean isEnabled) {
        for (final Integer resId : TOKEN_BUTTON_RES_IDS) {
            mBoard.findViewById(resId).setEnabled(isEnabled);
        }
    }

    // ---------------------------------
    //          NESTED CLASSES
    // ---------------------------------

    /**
     * A delegate which handles UI interactions from the {@link GameView}.
     */
    public interface Delegate {

        /**
         * Should be called when a new game is requested.
         */
        void onNewGame();

        /**
         * Should be called when a new token has been played.
         *
         * @param location the location of the token
         */
        void onTokenPlayed(TokenLocation location);

    }
}
