package com.ninety8point6.droptoken.concepts;

import com.google.common.base.Optional;

/**
 * A {@link GameStore} persists and fetches the current {@link GameState}.
 */
public interface GameStore {

    /**
     * Get a {@link GameState} from the {@link GameStore}.
     *
     * @param key the identifier for the {@link GameState}
     * @param callback a {@link ResponseCallback} which will indicate success/failure for the operation
     */
    void get(String key, ResponseCallback<GameState, Throwable> callback);

    /**
     * Put {@link GameState} into the {@link GameStore}.
     *
     * @param state the {@link GameState} to be saved
     * @param callback a {@link ResponseCallback} which will indicate success/failure for the operation
     */
    void put(GameState state, ResponseCallback<Boolean, Throwable> callback);

}
