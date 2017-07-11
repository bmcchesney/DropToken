package com.ninety8point6.droptoken;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ninety8point6.droptoken.concepts.GameManager;
import com.ninety8point6.droptoken.concepts.GameService;
import com.ninety8point6.droptoken.concepts.GameStore;
import com.ninety8point6.droptoken.concepts.TokenLocation;
import com.ninety8point6.droptoken.game.SinglePlayerGameManager;
import com.ninety8point6.droptoken.service.SimpleGameService;
import com.ninety8point6.droptoken.store.SharedPreferencesGameStore;
import com.ninety8point6.droptoken.view.GameView;

import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;

/**
 * The main {@link AppCompatActivity} for DropToken, which initializes all of the required dependencies
 * and starts game play. The {@link com.ninety8point6.droptoken.concepts.GameState} is loaded from the
 * {@link GameStore} (if one exists), or a new game is created by prompting the user.
 *
 * TODO: Investigate non-deterministic double-prompting of player selection
 */
public class GameActivity extends AppCompatActivity implements GameView.Delegate {

    private static final String TAG = "GameActivity";

    private static final String ENDPOINT = "https://w0ayb2ph1k.execute-api.us-west-2.amazonaws.com/production";
    private static final String SHARED_PREFERENCES_NAME = "GameStore";

    private GameManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        final URL serviceUrl;
        try {
            serviceUrl = new URL(ENDPOINT);
        } catch (final MalformedURLException ex) {
            Log.e(TAG, "[onCreate] Unable create service endpoint... closing Activity.", ex);
            finish();
            return;
        }

        final GameService service = new SimpleGameService(new OkHttpClient(), serviceUrl);
        final GameStore store = new SharedPreferencesGameStore(getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE));
        final GameView view = new GameView(getApplicationContext(),
                    getSupportFragmentManager(),
                    findViewById(R.id.board),
                    (ImageButton) findViewById(R.id.fab),
                    (ContentLoadingProgressBar) findViewById(R.id.progress_bar),
                    (TextView) findViewById(R.id.message),
                    this);

        mManager = new SinglePlayerGameManager(getResources(),
                service,
                store,
                view,
                new Handler(Looper.getMainLooper()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mManager.loadGame();
    }

    @Override
    public void onNewGame() {
        mManager.newGame();
    }

    @Override
    public void onTokenPlayed(TokenLocation location) {
        mManager.play(location);
    }
}
