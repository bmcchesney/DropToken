package com.ninety8point6.droptoken.store;

import com.ninety8point6.droptoken.BuildConfig;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * A suite of tests to verify the {@link SharedPreferencesGameStore} has the expected behavior.
 *
 * TODO: Implement tests!!!
 *
 * @see SharedPreferencesGameStore
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
@Ignore
public class SharedPreferencesGameStoreTests {

    @Test(expected = NullPointerException.class)
    public void testStoreChecksSharedPreferences() {
        new SharedPreferencesGameStore(null);
    }

    @Test(expected = NullPointerException.class)
    public void testStoreGetChecksNullKey() {

    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreGetChecksEmptyKey() {

    }

    @Test(expected = NullPointerException.class)
    public void testStoreGetChecksCallback() {

    }

    @Test
    public void testStoreGetHandlesException() {

    }

    @Test
    public void testStoreGetsState() {

    }

    @Test(expected = NullPointerException.class)
    public void testStorePutChecksState() {

    }

    @Test(expected = NullPointerException.class)
    public void testStorePutChecksCallback() {

    }

    @Test
    public void testStorePutHandlesException() {

    }

    @Test
    public void testStorePutsState() {

    }
}
