package com.ninety8point6.droptoken.concepts;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

/**
 * The {@link GameState} tracks all necessary values for the game. This is simply a container for
 * storing state. No game logic should be implemented here.
 *
 * TODO: Consider versioning for future iterations (as we might adopt a different key strategy).
 */
@Immutable
public final class GameState {

    public static final int PLAYER_1 = 0;
    public static final int PLAYER_2 = 1;

    private static final Set<Integer> VALID_PLAYERS = ImmutableSet.<Integer>builder()
            .add(PLAYER_1, PLAYER_2)
            .build();

    @SerializedName("key")
    private final String mKey;

    @SerializedName("moves")
    private final List<Integer> mMoves;

    @SerializedName("player")
    private int mPlayer;

    /**
     * Builds the {@link GameState} with default parameters. This should be used when initializing
     * new games.
     *
     * @param key an identifier for this {@link GameState}
     * @param player the inital player for this {@link GameState}
     */
    public GameState(final String key, final int player) {
        this(key, Collections.emptyList(), player);
    }

    /**
     * Builds the {@link GameState} with the provided inputs.
     *
     * @param key an identifier for this {@link GameState}
     * @param moves the current list of moves that have been played
     * @param player the inital player for this {@link GameState}
     */
    public GameState(final String key, final List<Integer> moves, final int player) {

        Preconditions.checkArgument(key != null);
        Preconditions.checkArgument(!key.isEmpty());
        Preconditions.checkArgument(VALID_PLAYERS.contains(player));

        mKey = key;
        mMoves = ImmutableList.copyOf(Preconditions.checkNotNull(moves));
        mPlayer = Preconditions.checkNotNull(player);
    }

    /**
     * @return an identifier for this {@link GameState}
     */
    public String key() {
        return mKey;
    }

    /**
     * @return the current list of moves that have been applied
     */
    public List<Integer> moves() {
        return mMoves;
    }

    /**
     * @return the current player based on the list of moves and the initial palter
     */
    public int currentPlayer() {
        return moves().size() % 2 == 0
                ? mPlayer
                : getNextPlayer(mPlayer);
    }

    /**
     * @return the identifier for the first player in this {@link GameState}; used to construct state
     *         after de-serialization
     */
    public int initialPlayer() {
        return mPlayer;
    }

    /**
     * @return get the last player who successfully added a move, which is useful for determining
     *         the winning player on a completed game
     */
    public int lastPlayer() {
        return moves().size() % 2 == 0
                ? getNextPlayer(mPlayer)
                : mPlayer;
    }

    /**
     * Get the next player based on the provided player.
     *
     * @param player the provided player who has just placed a move
     *
     * @return he next player
     */
    public static int getNextPlayer(final int player) {
        if (player < VALID_PLAYERS.size() - 1) {
            return player + 1;
        }

        return PLAYER_1;
    }
}
