package com.ninety8point6.droptoken.game;

import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.ninety8point6.droptoken.R;
import com.ninety8point6.droptoken.concepts.GameBoard;
import com.ninety8point6.droptoken.concepts.GameManager;
import com.ninety8point6.droptoken.concepts.GameService;
import com.ninety8point6.droptoken.concepts.GameState;
import com.ninety8point6.droptoken.concepts.GameStore;
import com.ninety8point6.droptoken.concepts.Move;
import com.ninety8point6.droptoken.concepts.ResponseCallback;
import com.ninety8point6.droptoken.concepts.TokenLocation;
import com.ninety8point6.droptoken.view.GameView;

import java.util.LinkedList;
import java.util.List;

import static com.ninety8point6.droptoken.concepts.GameState.PLAYER_1;

/**
 * A {@link GameManager} implementation for a single player version of Drop Token. The user swaps
 * moves with the service until a win occurs or if the board is full, resulting in a draw.
 * <p/>
 * All public APIs are assumed to have been invoked from the main UI thread.
 *
 * TODO: Add synchronization once more async implementations are available
 */
public class SinglePlayerGameManager implements GameManager {

    private static final String TAG = "SinglePlayerGameManager";

    private static final String GAME_KEY = "SinglePlayerGameManager";

    private final Resources mResources;
    private final GameStore mStore;
    private final GameService mService;

    /**
     * All method invocations should be executed using the #mMainThreadHandler to ensure View layer
     * updates occur on the main UI thread.
     */
    private final GameView mView;

    /**
     * A main thread {@link Handler} for posting UI updates and ensuring consistency on any internal
     * state changes.
     */
    private final Handler mMainThreadHandler;

    /**
     * The current {@link GameState} for which the {@link GameManager} acts upon. This implementation
     * is immutable and so we create new instances each time it needs to be modified
     * (using the existing values).
     * <p/>
     * Access is not synchronized as this should only be accessed/modified from the main thread.
     */
    private GameState mState;

    /**
     * The current {@link GameBoard} for which the {@link GameManager} applies validation. This
     * implementation is immutable and so we create new instances each time it needs to be modified
     * (using the {@link GameState}). This could eventually be updated with mutable operations, but
     * opting to keep things simple since it is just used for validating moves and it is lightweight.
     * <p/>
     * Access is not synchronized as this should only be accessed/modified from the main thread.
     */
    private GameBoard mBoard;

    /**
     * Builds the {@link GameManager} with the provided dependencies.
     *
     * @param resources the {@link Resources} for accessing strings
     * @param service the {@link GameService} for communication with the 9dt service
     * @param store the {@link GameStore} for persisting {@link GameState}
     * @param view the {@link GameView} for pushing state changes to the view layer
     * @param mainThreadHandler the main thread {@link Handler} for ensuring mutable field consistency
     *                          and for updating the {@link GameView}
     */
    public SinglePlayerGameManager(final Resources resources,
                                   final GameService service,
                                   final GameStore store,
                                   final GameView view,
                                   final Handler mainThreadHandler) {
        mResources = Preconditions.checkNotNull(resources);
        mService = Preconditions.checkNotNull(service);
        mStore = Preconditions.checkNotNull(store);
        mView = Preconditions.checkNotNull(view);
        mMainThreadHandler = Preconditions.checkNotNull(mainThreadHandler);
    }

    @Override
    public void loadGame() {
        mStore.get(GAME_KEY, new OnGameLoaded());
    }

    @Override
    public void newGame() {
        mView.promptPlayerSelection(new OnPlayerSelected());
    }

    @Override
    public void play(final TokenLocation location) {

        if (!mBoard.isLocationValid(location)) {
            mView.setMessage(mResources.getString(R.string.invalid_move_message));
            return;
        }

        final List<Integer> currentMoves = new LinkedList<>(mState.moves());
        currentMoves.add(location.column());
        final GameState newState = new GameState(mState.key(),
                currentMoves,
                mState.initialPlayer());

        // If we've reached a game over, save the state and notify the view; otherwise we can safely
        // play the token and ping the service for the computer's turn.
        switch (new GameBoard(newState).isGameOver(PLAYER_1)) {
            case DRAW:
            case WIN:
                mStore.put(newState, new OnGameSaved(newState));
                break;
            case NONE:
                mService.play(new Move(currentMoves), new OnMovePlayed(newState));
                break;
            default:
                throw new RuntimeException("Unknown game over type");
        }
    }

    /**
     * Handle when the {@link GameState} becomes available. We need to check for whether it is in a
     * "game over" state before proceeding.
     *
     * @param state the game state
     */
    private void handleGameStateAvailable(final GameState state) {
        final int player = state.lastPlayer();
        switch (new GameBoard(state).isGameOver(player)) {
            case DRAW:
                handleGameOver(state, mResources.getString(R.string.draw_message));
                break;
            case NONE:
                handleGameLoaded(state);
                break;
            case WIN:
                handleGameOver(state, player == 0
                        ? mResources.getString(R.string.player_win_message)
                        : mResources.getString(R.string.computer_win_message));
                break;
            default:
                throw new RuntimeException("Unknown game over type");
        }
    }

    /**
     * Handle when the {@link GameState} has been loaded and verified. This modifies the manager's
     * mutable state holders, as well as notifies the UI with the latest {@link GameState} for
     * rendering.
     *
     * @param state the game state
     */
    private void handleGameLoaded(final GameState state) {
        mMainThreadHandler.post(() -> {
            mState = state;
            mBoard = new GameBoard(mState);
            mView.updateGameView(mState, false);
        });
    }

    /**
     * Handle state changes and UI updates when the game is over. We save to the store so that if the
     * app is launched before starting a new game we end up in a consistent state.
     *
     * @param state the game state
     * @param message the message to be displayed
     */
    private void handleGameOver(final GameState state, final String message) {
        mMainThreadHandler.post(() -> {
            mState = state;
            mBoard = new GameBoard(mState);
            mView.updateGameView(mState, true);
            mView.setMessage(message);
        });
    }

    /**
     * A convenience method for handling an error without a {@link Throwable}.
     *
     * @param logMsg the message to log
     * @param viewMsg the message to display on the view
     *
     * @see #handleError(String, String, Throwable)
     */
    private void handleError(final String logMsg, final String viewMsg) {
        handleError(logMsg, viewMsg, null);
    }

    /**
     * Handle any unexpected error that may occur. This publishes a log message and displays a message
     * on the {@link GameView}.
     *
     * @param logMsg the message to log
     * @param viewMsg the message to display on the view
     * @param reason the {@link Throwable} (may be null) that is associated with this error
     */
    private void handleError(final String logMsg, final String viewMsg, final Throwable reason) {

        if (reason == null) {
            Log.e(TAG, "[handleError] " + logMsg);
        } else {
            Log.e(TAG, "[handleError] " + logMsg, reason);
        }

        mMainThreadHandler.post(() -> {
            mView.setMessage(viewMsg);
        });
    }

    // ---------------------------------
    //          NESTED CLASSES
    // ---------------------------------

    /**
     * A {@link ResponseCallback} for handling when the {@link GameState} has been loaded from
     * the {@link GameStore}.
     */
    private class OnGameLoaded implements ResponseCallback<GameState, Throwable> {

        @Override
        public void onSuccess(final GameState response) {

            if (response != null) {
                handleGameStateAvailable(response);
                return;
            }

            mView.promptPlayerSelection(new OnPlayerSelected());
        }

        @Override
        public void onError(final Throwable reason) {
            handleError("Unable to load a game from the store.",
                    mResources.getString(R.string.unexpected_error_message),
                    reason);
        }
    }

    /**
     * A {@link ResponseCallback} for handling when the {@link GameState} has been saved by the
     * {@link GameStore}.
     */
    private class OnGameSaved implements ResponseCallback<Boolean, Throwable> {

        private final GameState mState;

        /**
         * Builds the {@link OnGameSaved} callback.
         *
         * @param state the state that has been saved
         */
        OnGameSaved(final GameState state) {
            mState = state;
        }

        @Override
        public void onSuccess(final Boolean response) {

            if (!response) {
                handleError("Unable to save a game in the store.", mResources.getString(R.string.unexpected_error_message));
                return;
            }

            handleGameStateAvailable(mState);
        }

        @Override
        public void onError(final Throwable reason) {
            handleError("Unable to save a game in the store.",
                    mResources.getString(R.string.unexpected_error_message),
                    reason);
        }
    }

    /**
     * A {@link ResponseCallback} for handling when a move has been played against the
     * {@link GameService}. The response includes the latest move from the opponent.
     */
    private class OnMovePlayed implements ResponseCallback<List<Integer>, Throwable> {

        private final GameState mState;

        /**
         * Builds the {@link OnMovePlayed} callback.
         *
         * @param state the current state to be updated
         */
        OnMovePlayed(final GameState state) {
            mState = state;
        }

        @Override
        public void onSuccess(final List<Integer> response) {

            if (response.isEmpty()) {
                handleError("Attempted to play an invalid move.", mResources.getString(R.string.unexpected_error_message));
                return;
            }

            final GameState newState = new GameState(mState.key(),
                    response,
                    mState.initialPlayer());
            mStore.put(newState, new OnGameSaved(newState));
        }

        @Override
        public void onError(final Throwable reason) {
            handleError("Unable to play a move.",
                    mResources.getString(R.string.unexpected_error_message),
                    reason);
        }
    }

    /**
     * A {@link ResponseCallback} for handling when an initial player has been selected.
     */
    private class OnPlayerSelected implements ResponseCallback<Integer, Throwable> {

        @Override
        public void onSuccess(final Integer response) {

            final GameState state = new GameState(GAME_KEY, response);
            if (response != PLAYER_1) {
                mService.play(new Move(state.moves()), new OnMovePlayed(state));
                return;
            }

            mStore.put(state, new OnGameSaved(state));
        }

        @Override
        public void onError(final Throwable reason) {
            handleError("Unable to select the initial player.",
                    mResources.getString(R.string.unexpected_error_message),
                    reason);
        }
    }
}
