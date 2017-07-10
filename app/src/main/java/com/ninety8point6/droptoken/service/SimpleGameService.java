package com.ninety8point6.droptoken.service;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.ninety8point6.droptoken.concepts.GameService;
import com.ninety8point6.droptoken.concepts.Move;
import com.ninety8point6.droptoken.concepts.ResponseCallback;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * An implementation of the {@link GameService} that leverages the {@link OkHttpClient} for
 * out-of-the-box http functionality.
 */
public class SimpleGameService implements GameService {

    /**
     * A query parameter key for adding the array of moves to the service endpoint.
     */
    private static final String MOVES_KEY = "moves";

    private final Gson mGson;
    private final OkHttpClient mClient;
    private final HttpUrl mEndpoint;

    /**
     * Builds the {@link SimpleGameService} with the provided dependencies.
     *
     * @param client a {@link OkHttpClient} for making network calls
     * @param endpoint the base {@link URL} for accessing the 9dt service
     */
    public SimpleGameService(final OkHttpClient client, final URL endpoint) {
        mGson = new Gson();
        mClient = Preconditions.checkNotNull(client);

        // Two point checked required as HttpUrl#get is nullable
        final HttpUrl url = HttpUrl.get(Preconditions.checkNotNull(endpoint));
        mEndpoint = Preconditions.checkNotNull(url);
    }

    @Override
    public void play(final Move move, final ResponseCallback<List<Integer>, Throwable> callback) {

        Preconditions.checkArgument(move != null);
        Preconditions.checkArgument(callback != null);

        try {

            // Finalize the url using the endpoint and the moves parameter
            final HttpUrl url = mEndpoint
                    .newBuilder()
                    .addQueryParameter(MOVES_KEY, mGson.toJson(move.moves()))
                    .build();

            // Build the request and enqueue the call
            final Request request = new Request.Builder()
                    .url(url)
                    .build();

            mClient.newCall(request).enqueue(new PlayMoveCallback(callback));
        } catch (final Exception ex) {
            callback.onError(ex);
        }
    }

    /**
     * A {@link Callback} to handle the response from the 9dt service. This wraps a
     * {@link ResponseCallback} to conform to our {@link GameService} contract.
     */
    private final class PlayMoveCallback implements Callback {

        private final ResponseCallback<List<Integer>, Throwable> mCallback;

        /**
         * Builds the {@link PlayMoveCallback} with a {@link ResponseCallback}.
         * @param callback the {@link ResponseCallback} by which to deliver the response
         */
        PlayMoveCallback(final ResponseCallback<List<Integer>, Throwable> callback) {
            mCallback = callback;
        }

        @Override
        public void onFailure(final Call call, final IOException e) {
            mCallback.onError(e);
        }

        /**
         * API documentation specifies that a code of 200 represents a successful move, and a 400
         * represents an invalid move. All other response codes are treated as exceptions.
         * <p/>
         * {@inheritDoc}
         */
        @Override
        public void onResponse(final Call call, final Response response) throws IOException {
            switch (response.code()) {
                case 200:
                    final Integer[] moves;
                    try (final ResponseBody responseBody = response.body()) {
                        moves = mGson.fromJson(responseBody.string(), Integer[].class); // #body() only null in Callback#onFailure
                    } catch (final IOException exception) {
                        mCallback.onError(exception);
                        return;
                    }
                    mCallback.onSuccess(Arrays.asList(moves));
                    break;
                case 400:
                    mCallback.onSuccess(Collections.emptyList());
                    break;
                default:
                    mCallback.onError(new Exception("Invalid response code."));
            }
        }
    }
}
