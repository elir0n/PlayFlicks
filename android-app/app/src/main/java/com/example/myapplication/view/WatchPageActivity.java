package com.example.myapplication.view;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.myapplication.R;
import com.example.myapplication.viewmodel.MoviesViewModel;

public class WatchPageActivity extends AppCompatActivity {

    // ExoPlayer instance for video playback
    private ExoPlayer exoPlayer;
    // Variables for tracking playback position, state, and movie watch status
    private long playerTime = 0;
    private boolean playing = true;
    private boolean movieWatched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_page);

        // Enable full-screen immersive mode
        EdgeToEdge.enable(this);
        configureSystemUI();

        // Retrieve movie ID from the intent
        long movieId = getIntent().getLongExtra("id", 0);

        // Initialize ExoPlayer and set it to PlayerView
        PlayerView playerView = findViewById(R.id.pvVideo);
        exoPlayer = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);

        // Use ViewModel to fetch movie data based on the movie ID
        MoviesViewModel moviesViewModel = new ViewModelProvider(this).get(MoviesViewModel.class);
        moviesViewModel.getMovieById(movieId).observe(this, movie -> {
            if (movie == null) {
                // Show an error dialog if the movie does not load within 30 seconds
                new Handler(Looper.getMainLooper()).postDelayed(this::showErrorDialog, 30000);
                return;
            }

            // Construct video URI and set up the player
            Uri videoUri = Uri.parse(getString(R.string.BASE_URL) + movie.getVideo());
            setupPlayer(videoUri, moviesViewModel, movieId);
        });
    }

    /**
     * Configures the system UI for full-screen playback by hiding system bars.
     */
    private void configureSystemUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * Sets up the ExoPlayer with the provided video URI and adds event listeners.
     *
     * @param videoUri        the URI of the video to play
     * @param moviesViewModel the ViewModel used to manage movie data
     * @param movieId         the ID of the movie being played
     */
    private void setupPlayer(Uri videoUri, MoviesViewModel moviesViewModel, long movieId) {
        Log.e("onCreate: time", playerTime + "");

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    finish(); // Close activity when playback ends
                }
                if (playbackState == Player.STATE_READY && !movieWatched) {
                    movieWatched = true;
                    moviesViewModel.addMovieToUser(movieId); // Mark the movie as watched
                }
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                showErrorDialog(); // Show error dialog if playback fails
            }
        });

        // Prepare the player with the media item
        exoPlayer.setMediaItem(MediaItem.fromUri(videoUri));
        exoPlayer.seekTo(playerTime); // Seek to the saved position if available
        exoPlayer.prepare();

        Log.e("onCreate: playing", playing + "");
        // Start or pause playback based on the saved state
        if (playing) {
            exoPlayer.play();
        } else {
            exoPlayer.pause();
        }
    }

    /**
     * Displays an error dialog if the video cannot be loaded.
     */
    private void showErrorDialog() {
        new AlertDialog.Builder(WatchPageActivity.this)
                .setTitle("Error")
                .setMessage("Couldn't load video, Please try again later")
                .setNeutralButton("OK", (dialog, which) -> finish()) // Close the activity on 'OK'
                .show();
    }

    /**
     * Releases the resources used by ExoPlayer.
     */
    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release(); // Release the ExoPlayer instance
            exoPlayer = null;
        }
    }

    /**
     * Called when the activity is paused.
     * Saves the current playback position and state, then stops the player to release resources.
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Save current playback position and state when the activity is paused
        playerTime = exoPlayer.getCurrentPosition();
        playing = exoPlayer.isPlaying();
        exoPlayer.stop(); // Stop playback to release resources
    }

    /**
     * Called to save the current instance state before the activity is potentially destroyed.
     * Stores playback-related information such as watched status, playback time, and playing state.
     *
     * @param outState The Bundle in which to place the saved state.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save important state data to restore playback later
        outState.putBoolean("watched", movieWatched);
        outState.putLong("time", playerTime);
        outState.putBoolean("playing", playing);
    }

    /**
     * Called to restore the activity's previously saved state.
     * Retrieves playback-related data to maintain continuity after recreation.
     *
     * @param savedInstanceState The Bundle containing the saved state data.
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the saved state when the activity is recreated
        movieWatched = savedInstanceState.getBoolean("watched");
        playerTime = savedInstanceState.getLong("time");
        playing = savedInstanceState.getBoolean("playing");
    }

    /**
     * Called when the activity is no longer visible to the user.
     * Releases player resources to free up memory.
     */
    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer(); // Release player resources when stopping the activity
    }

    /**
     * Called when the activity is resumed after being paused or stopped.
     * Resumes playback if the player is available.
     */

    @Override
    protected void onResume() {
        super.onResume();
        if (exoPlayer != null) {
            exoPlayer.play(); // Resume playback when the activity is resumed
        }
    }

    /**
     * Called when the activity is about to be destroyed.
     * Ensures that the player is released to prevent memory leaks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer(); // Ensure that the player is released when the activity is destroyed
    }
}
