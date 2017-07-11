package com.ninety8point6.droptoken.service;

import com.ninety8point6.droptoken.BuildConfig;
import com.ninety8point6.droptoken.concepts.Move;
import com.ninety8point6.droptoken.concepts.ResponseCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

import static org.mockito.Mockito.*;

/**
 * A suite of tests to verify the {@link SimpleGameService} has the expected behavior.
 *
 * @see SimpleGameService
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class SimpleGameServiceTests {

    private static final String TEST_ENDPOINT_URL = "https://www.foo.bar/bash";

    @Mock
    private OkHttpClient mockHttpClient;

    @Mock
    private ResponseCallback<List<Integer>, Throwable> mockCallback;

    private final Move mMove = new Move(Collections.emptyList());

    private SimpleGameService mService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mService = new SimpleGameService(mockHttpClient, new URL(TEST_ENDPOINT_URL));
    }

    @Test(expected = NullPointerException.class)
    public void testServiceChecksClient() throws Exception {
        new SimpleGameService(null, new URL(TEST_ENDPOINT_URL));
    }

    @Test(expected = NullPointerException.class)
    public void testServiceChecksEndpoint() {
        new SimpleGameService(mockHttpClient, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testServicePlayChecksMove() {
        mService.play(null, mockCallback);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testServicePlayChecksCallback() {
        mService.play(mMove, null);
    }

    @Test
    public void testServiceHandlesException() {
        final Exception expected = new RuntimeException("Boom!");
        doThrow(expected).when(mockHttpClient).newCall(any(Request.class));
        mService.play(mMove, mockCallback);
        verify(mockCallback).onError(expected);
    }

    @Test
    public void testServiceHandlesOnFailure() {

        final Call call = mock(Call.class);
        doReturn(call).when(mockHttpClient).newCall(any(Request.class));

        final IOException expected = new IOException("IO!");
        doAnswer(invocation -> {
            final Callback callback = invocation.getArgument(0);
            callback.onFailure(call, expected);
            return null;
        }).when(call).enqueue(any(Callback.class));

        mService.play(mMove, mockCallback);

        verify(mockCallback).onError(expected);
    }

    @Test
    public void testServiceHandlesOnResponse_400() {

        final Call call = mock(Call.class);
        doReturn(call).when(mockHttpClient).newCall(any(Request.class));

        // Required as Response cannot be mocked
        final Response response = new Response.Builder()
                .request(new Request.Builder().url(TEST_ENDPOINT_URL).build())
                .protocol(Protocol.HTTP_1_1)
                .message("")
                .code(400)
                .build();

        doAnswer(invocation -> {
            final Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any(Callback.class));

        mService.play(mMove, mockCallback);

        verify(mockCallback).onSuccess(argThat(List::isEmpty));
    }

    @Test
    public void testServiceHandlesOnResponse() throws Exception {

        final Call call = mock(Call.class);
        doReturn(call).when(mockHttpClient).newCall(any(Request.class));

        final BufferedSource source = mock(BufferedSource.class);
        final ResponseBody body = mock(ResponseBody.class);
        doReturn(source).when(body).source();
        doReturn("[1]").when(source).readString(any(Charset.class));

        // Required as Response cannot be mocked
        final Response response = new Response.Builder()
                .request(new Request.Builder().url(TEST_ENDPOINT_URL).build())
                .protocol(Protocol.HTTP_1_1)
                .message("")
                .code(200)
                .body(body)
                .build();

        doAnswer(invocation -> {
            final Callback callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any(Callback.class));

        mService.play(mMove, mockCallback);

        verify(mockCallback).onSuccess(argThat(l -> l.size() == 1));
    }
}
