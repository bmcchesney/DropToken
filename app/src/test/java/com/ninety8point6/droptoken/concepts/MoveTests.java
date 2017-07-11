package com.ninety8point6.droptoken.concepts;

import com.ninety8point6.droptoken.BuildConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

/**
 * A suite of tests to verify the {@link Move} has the expected behavior.
 *
 * @see Move
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class MoveTests {

    @Test(expected = NullPointerException.class)
    public void testMoveChecksMoves() {
        new Move(null);
    }

    @Test
    public void testMoveHasTheExpectedValues() {
        final List<Integer> moves = Arrays.asList(0, 1, 2);
        Assert.assertArrayEquals(moves.toArray(), new Move(moves).moves().toArray());
    }
}
