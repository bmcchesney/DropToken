package com.ninety8point6.droptoken.game;

import com.ninety8point6.droptoken.BuildConfig;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * A suite of tests to verify the {@link SinglePlayerGameManager} has the expected behavior.
 *
 * TODO: Implement tests!!! Handling error states for operations + various outcomes upon game loaded
 * TODO: and after moves are played by the service
 *
 * @see SinglePlayerGameManager
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
@Ignore
public class SinglePlayerGameManagerTests {

    @Test(expected = NullPointerException.class)
    public void testManagerChecksResources() {

    }

    @Test(expected = NullPointerException.class)
    public void testManagerChecksService() {

    }

    @Test(expected = NullPointerException.class)
    public void testManagerChecksStore() {

    }

    @Test(expected = NullPointerException.class)
    public void testManagerChecksView() {

    }

    @Test(expected = IllegalArgumentException.class)
    public void testManagerChecksHandlerIsMainHandler() {

    }

    @Test(expected = NullPointerException.class)
    public void testManagerChecksHandler() {

    }

    @Test
    public void testManagerLoadsGame() {

    }

    @Test
    public void testManagerNewGame() {

    }

    @Test(expected = NullPointerException.class)
    public void testManagerPlayChecksLocation() {

    }

    @Test
    public void testManagerPlayChecksValidLocation() {

    }

    @Test
    public void testManagerPlayChecksIsGameOver() {

    }

    @Test
    public void testManagerPlays() {

    }
}
