package com.example.myapplication.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.adapter.CategorySelectionAdapter;
import com.example.myapplication.model.Category;
import com.example.myapplication.model.Movie;
import com.example.myapplication.utils.FileManager;
import com.example.myapplication.viewmodel.CategoriesViewModel;
import com.example.myapplication.viewmodel.MoviesViewModel;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class AddEditMovieActivity extends AppCompatActivity {

    // UI Elements
    private EditText etMovieTitle;
    private EditText etMovieDescription;
    private ImageView photo;
    private PlayerView video;
    private ProgressBar progressBar;
    private TextView tvProgressBar;
    private Button saveButton;
    private Button cancelButton;

    private ExoPlayer player;

    // URI Variables for Image and Video
    private Uri videoUri = null;
    private Uri imageUri = null;

    // ViewModels
    private MoviesViewModel movieViewModel;
    private CategoriesViewModel categoriesViewModel;

    // Movie Data
    private List<String> categories;
    private boolean isAdd;
    private String oldMovieTitle;
    private boolean enteredNewImage = false;
    private boolean enteredNewVideo = false;

    // pickers
    private ActivityResultLauncher<PickVisualMediaRequest> pickImage;
    private ActivityResultLauncher<PickVisualMediaRequest> pickVideo;

    // Adapter for Categories
    private CategorySelectionAdapter adapter;

    /**
     * Called when the activity is first created.
     * Initializes UI elements, sets up event listeners, and loads data if editing an existing movie.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_add_edit);

        // Set up padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        initUI();

        // Initialize ViewModels
        movieViewModel = new ViewModelProvider(this).get(MoviesViewModel.class);
        categoriesViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);

        // Determine if adding or editing movie
        isAdd = getIntent().getBooleanExtra("add", true);

        if (!isAdd) {
            oldMovieTitle = getIntent().getStringExtra("title");
            loadMovieData(oldMovieTitle);
        }

        // Set up back button callback
        setupBackButton();

        // Register for media picking
        registerForMediaPickers();

        // Set up RecyclerView for categories
        setupCategoryRecyclerView();

        // Set up button click listeners
        setupButtonListeners();
    }

    /**
     * Initializes UI components and player.
     */
    private void initUI() {
        player = new ExoPlayer.Builder(this)
                .build();
        etMovieTitle = findViewById(R.id.etMovieTitle);
        etMovieDescription = findViewById(R.id.etMovieDescription);
        photo = findViewById(R.id.imgMoviePoster);
        video = findViewById(R.id.video);
        progressBar = findViewById(R.id.progressBar);
        tvProgressBar = findViewById(R.id.tvProgressBar);
        saveButton = findViewById(R.id.btnSaveMovie);
        cancelButton = findViewById(R.id.btnCancel);

    }

    /**
     * Sets up back button handling to confirm before exiting.
     */
    // Setup back button navigation
    private void setupBackButton() {
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exitActivity();
            }
        });
    }

    /**
     * Releases the ExoPlayer instance to free resources.
     */
    private void releasePlayer() {
        if (player != null) {
            player.release();  // Free up resources
            player = null;
        }
    }

    /**
     * Initializes the video player with a given URI.
     *
     * @param videoUri The URI of the video to be played.
     */
    private void initializePlayer(Uri videoUri) {
        releasePlayer();  // Ensure old instance is destroyed

        player = new ExoPlayer.Builder(this).build();

        video.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(videoUri);

        player.setMediaItem(mediaItem);
        player.setVolume(0);
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_ENDED) {
                    player.seekTo(0);  // Restart video
                    player.play();      // Play again
                }
            }
        });
        video.setUseController(false);
        player.prepare();
        player.play();
    }


    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    /**
     * Registers media pickers for selecting images and videos.
     */
    private void registerForMediaPickers() {
        pickImage = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                imageUri = uri;
                photo.setImageURI(imageUri);
                Toast.makeText(this, getString(R.string.image_added_successfully), Toast.LENGTH_LONG).show();
                enteredNewImage = true;
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        pickVideo = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                videoUri = uri;
                enteredNewVideo = true;
                initializePlayer(videoUri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        photo.setOnClickListener(v -> pickImage());
        video.setOnClickListener(v -> pickVideo());
    }

    /**
     * Sets up the RecyclerView for selecting movie categories.
     * Initializes the adapter and observes category data from ViewModel.
     */
    private void setupCategoryRecyclerView() {
        RecyclerView categoriesRecyclerView = findViewById(R.id.rvGroupCategories);
        categories = new LinkedList<>();

        adapter = new CategorySelectionAdapter(categories, item -> {
            if (item.isChecked()) {
                categories.add(item.getText().toString());
            } else {
                categories.remove(item.getText().toString());
            }
        });

        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoriesRecyclerView.setAdapter(adapter);

        categoriesViewModel.getCategoriesNames().observe(this, adapter::updateData);
    }

    /**
     * Sets up click listeners for the save and cancel buttons.
     */
    private void setupButtonListeners() {
        saveButton.setOnClickListener(v -> onSaveClick());
        cancelButton.setOnClickListener(v -> exitActivity());
    }

    /**
     * Loads movie data for editing based on the provided title.
     * Retrieves movie details and updates the UI accordingly.
     *
     * @param title The title of the movie to be loaded.
     */
    private void loadMovieData(String title) {
        enableProgressBar(getString(R.string.loading_data_please_wait));

        LiveData<Movie> movieLiveData = movieViewModel.getMovieByTitle(title);
        movieLiveData.observe(this, movie -> {
            if (movie == null) return;


            imageUri = Uri.parse(getString(R.string.BASE_URL) + movie.getImage());
            videoUri = Uri.parse(getString(R.string.BASE_URL) + movie.getVideo());

            Glide.with(getApplicationContext()).load(getString(R.string.BASE_URL) + movie.getImage())
                    .error(R.drawable.no_image_found)
                    .placeholder(R.drawable.no_image_found)
                    .override(250, 250)
                    .into(photo);


            initializePlayer(videoUri);

            etMovieTitle.setText(title);
            etMovieDescription.setText(movie.getDescription());

            LiveData<List<Category>> categoriesLiveData = categoriesViewModel.getMovieCategories(movie.getMovieId());
            categoriesLiveData.observe(AddEditMovieActivity.this, categories1 -> {
                for (Category c : categories1) {
                    categories.add(c.getTitle());
                }
                adapter.setSelectedCategories(categories);
            });
            disableProgressBar();
        });
    }

    /**
     * Handles the save button click event.
     * Validates input and either creates or edits a movie.
     */
    private void onSaveClick() {
        enableProgressBar(getString(R.string.updating_data_please_wait));
        String movieTitle = etMovieTitle.getText().toString().trim();

        // Validate input
        if (movieTitle.isEmpty()) {
            disableProgressBar();
            alertMessage(getString(R.string.movie_title_must_be_provided));
            return;
        }
        if (imageUri == null) {
            disableProgressBar();
            alertMessage(getString(R.string.image_must_be_provided));
            return;
        }
        if (videoUri == null) {
            disableProgressBar();
            alertMessage(getString(R.string.video_must_be_provided));
            return;
        }
        if (categories.isEmpty()) {
            disableProgressBar();
            alertMessage(getString(R.string.no_category_provided));
            return;
        }

        String movieDescription = etMovieDescription.getText().toString().trim();

        try {
            File image = enteredNewImage ? FileManager.convertUriToFile(this, imageUri) : null;
            File video = enteredNewVideo ? FileManager.convertUriToFile(this, videoUri) : null;

            if (isAdd) {
                createMovie(movieTitle, movieDescription, image, video);
            } else {
                createEditMovie(movieTitle, movieDescription, image, video, oldMovieTitle);
            }
        } catch (Exception e) {
            disableProgressBar();
            alertMessage(getString(R.string.error_converting_image_to_correct_format_please_try_again));
        }
    }

    /**
     * Creates a new movie with the provided details.
     *
     * @param movieTitle       The title of the movie.
     * @param movieDescription The description of the movie.
     * @param image            The movie image file.
     * @param video            The movie video file.
     */
    private void createMovie(String movieTitle, String movieDescription, File image, File video) {
        movieViewModel.createMovie(movieTitle, movieDescription, image, video, categories).observe(this, this::handleResponse);
    }

    /**
     * Handling the user response from server
     *
     * @param exitCode exit code to base response on
     */
    private void handleResponse(int exitCode) {
        if (exitCode == 0) return;
        if (exitCode == 400) {
            disableProgressBar();
            alertMessage(getString(R.string.movie_already_exists));
            return;
        }
        if (exitCode == 500) {
            disableProgressBar();
            alertMessage(getString(R.string.error_connecting_to_server_please_try_again_later));
            return;
        }

        disableProgressBar();
        Toast.makeText(this, getString(R.string.movie_added_successfully), Toast.LENGTH_SHORT).show();
        exitActivity();
    }

    /**
     * Edits an existing movie with new details.
     *
     * @param movieTitle       The updated title of the movie.
     * @param movieDescription The updated description of the movie.
     * @param image            The new movie image file.
     * @param video            The new movie video file.
     * @param oldMovieTitle    The original title of the movie before editing.
     */
    private void createEditMovie(String movieTitle, String movieDescription, File image, File video, String oldMovieTitle) {
        movieViewModel.editMovie(movieTitle, movieDescription, image, video, categories, oldMovieTitle).observe(this, this::handleResponse);
    }

    /**
     * Display progress bar
     */
    private void enableProgressBar(String text) {
        progressBar.setVisibility(View.VISIBLE);
        tvProgressBar.setVisibility(View.VISIBLE);
        tvProgressBar.setText(text);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     * Hide progress bar
     */
    private void disableProgressBar() {
        progressBar.setVisibility(View.GONE);
        tvProgressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    /**
     * Exit Activity
     */
    private void exitActivity() {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Display error message
     *
     * @param message message to display
     */
    private void alertMessage(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setNeutralButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Launch media picker for image
     */
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        pickImage.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    /**
     * Launch media picker for video
     */
    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        pickVideo.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                .build());

    }
}
