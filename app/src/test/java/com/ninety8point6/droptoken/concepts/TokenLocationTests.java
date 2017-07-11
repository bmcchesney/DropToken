package com.ninety8point6.droptoken.concepts;

import com.ninety8point6.droptoken.BuildConfig;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * A suite of tests to verify the {@link TokenLocation} has the expected behavior.
 *
 * @see TokenLocation
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class TokenLocationTests {

    @Test(expected = IllegalArgumentException.class)
    public void testTokenLocationChecksColumn() {
        new TokenLocation(-1);
    }

    @Test
    public void testTokenLocationHasTheExpectedValues() {
        final int expected = 99;
        Assert.assertEquals(expected, new TokenLocation(expected).column());
    }
}
