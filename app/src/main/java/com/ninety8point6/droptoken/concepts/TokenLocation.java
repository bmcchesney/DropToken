package com.ninety8point6.droptoken.concepts;

import javax.annotation.concurrent.Immutable;

/**
 * An immutable container for representing a token drop within the game.
 */
@Immutable
public final class TokenLocation {

    private final int mColumn;

    /**
     * Builds the {@link TokenLocation}.
     *
     * @param column the column to drop the token
     */
    public TokenLocation(final int column) {
        mColumn = column;
    }

    /**
     * @return the column to drop the token
     */
    public int column() {
        return mColumn;
    }
}
