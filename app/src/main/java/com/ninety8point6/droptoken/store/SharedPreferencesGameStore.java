package com.ninety8point6.droptoken.store;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.ninety8point6.droptoken.concepts.GameState;
import com.ninety8point6.droptoken.concepts.GameStore;
import com.ninety8point6.droptoken.concepts.ResponseCallback;

/**
 * An implementation of the {@link GameStore} that is backed by Android's {@link SharedPreferences},
 * which supports get/put operations on simple objects. We'll use this to fetch and save the
 * {@link GameState}.
 * <p/>
 * This implementation uses JSON serialization to store the state in a single location.
 */
public class SharedPreferencesGameStore implements GameStore {

    private final Gson mGson;
    private final SharedPreferences mSharedPreferences;

    /**
     * Builds the {@link SharedPreferencesGameStore} with the provided dependencies.
     *
     * @param sharedPreferences a {@link SharedPreferences} instance already initialized for use
     */
    public SharedPreferencesGameStore(final SharedPreferences sharedPreferences) {
        mGson = new Gson();
        mSharedPreferences = Preconditions.checkNotNull(sharedPreferences);
    }

    @Override
    public void get(final String key, final ResponseCallback<GameState, Throwable> callback) {

        Preconditions.checkArgument(!TextUtils.isEmpty(key));
        Preconditions.checkArgument(callback != null);

        try {
            final String serialized = mSharedPreferences.getString(key, null);
            if (serialized == null) {
                callback.onSuccess(null);
            } else {
                callback.onSuccess(mGson.fromJson(serialized, GameState.class));
            }
        } catch (final Exception ex) {
            callback.onError(ex);
        }
    }

    /**
     * Currently using a {@link ResponseCallback} does not provide much value, but we stick with
     * this API contract for when the app needs to support more complex persistence scenarios, such
     * as network or IO. A response of {@code true} simply means that the
     * {@link SharedPreferences.Editor#apply} call was made, but does not guarantee the value was
     * saved.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public void put(final GameState state, final ResponseCallback<Boolean, Throwable> callback) {

        Preconditions.checkArgument(state != null);
        Preconditions.checkArgument(callback != null);

        try {
            mSharedPreferences.edit()
                              .putString(state.key(), mGson.toJson(state))
                              .apply();
            callback.onSuccess(true);
        } catch (final Exception ex) {
            callback.onError(ex);
        }
    }
}
