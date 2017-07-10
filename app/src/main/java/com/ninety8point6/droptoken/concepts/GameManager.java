package com.ninety8point6.droptoken.concepts;

/**
 *
 */
public interface GameManager {

    /**
     *
     */
    void loadGame();

    /**
     *
     */
    void newGame();

    /**
     *
     * @param drop
     */
    void play(TokenLocation location);
}
