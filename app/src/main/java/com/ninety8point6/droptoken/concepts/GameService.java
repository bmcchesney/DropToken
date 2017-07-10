package com.ninety8point6.droptoken.concepts;

import java.util.List;

/**
 * A {@link GameService} interacts with the remote 9dt service to validate moves.
 */
public interface GameService {

    /**
     * Sends a {@link Move} to the 9dt service for validation.
     *
     * @param move the {@link Move} to validate
     * @param callback a {@link ResponseCallback} which will indicate success/failure for the operation
     */
    void play(Move move, ResponseCallback<List<Integer>, Throwable> callback);

}
