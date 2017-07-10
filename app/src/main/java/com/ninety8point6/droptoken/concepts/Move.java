package com.ninety8point6.droptoken.concepts;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.concurrent.Immutable;

/**
 * An immutable container for representing a {@link Move} within the game. Currently this is not
 * very different from the game state, but creating this abstraction allows for future iterations to
 * use the same contracts, without extensive refactoring.
 */
@Immutable
public final class Move {

    private final List<Integer> mMoves;

    /**
     * Builds the {@link Move} with the provided inputs.
     *
     * @param moves the current {@link List} of moves
     */
    public Move(final List<Integer> moves) {
        mMoves = ImmutableList.copyOf(Preconditions.checkNotNull(moves));
    }

    /**
     * @return the {@link List} of moves
     */
    public List<Integer> moves() {
        return mMoves;
    }
}
