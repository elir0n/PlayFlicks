package com.example.myapplication.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.adapter.VerticalMovieGridAdapter;
import com.example.myapplication.model.Category;
import com.example.myapplication.viewmodel.CategoriesViewModel;
import com.example.myapplication.viewmodel.MoviesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;

public class MovieDetails extends AppCompatActivity {

    // UI Components
    private RecyclerView recyclerView;
    private TextView categories;
    private long movieId;
    private ImageView image;
    private TextView title;
    private TextView description;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_details);

        // Handle window insets for better UI experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUI();

        // Handle back button click
        back.setOnClickListener(v -> exitActivity());

        // Adjust status bar color based on light/dark mode
        int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        getWindow().setStatusBarColor(ContextCompat.getColor(this, uiMode == Configuration.UI_MODE_NIGHT_YES ? R.color.black : R.color.lightGray));

        // Retrieve movie ID from intent
        movieId = getIntent().getLongExtra("id", 0);

        // Configure RecyclerView
        configureRecycleView();

        // ViewModel setup
        MoviesViewModel viewModel = new ViewModelProvider(this).get(MoviesViewModel.class);

        // Handle system back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exitActivity();
            }
        });

        displayMovieData(viewModel);
    }

    /**
     * Displaying movie data
     *
     * @param viewModel the view model for data getting
     */

    private void displayMovieData(@NonNull MoviesViewModel viewModel) {
        // Observe movie data and update UI accordingly
        viewModel.getMovieById(movieId).observe(this, movie -> {
            if (movie == null) return;

            // Load movie poster using Glide
            Glide.with(this).load(getString(R.string.BASE_URL) + movie.getImage())
                    .error(R.drawable.no_image_found)
                    .placeholder(R.drawable.no_image_found)
                    .into(image);

            // Set movie details
            title.setText(movie.getTitle());
            description.setText(movie.getDescription());

            // Handle play button click to start WatchPageActivity
            FloatingActionButton fab = findViewById(R.id.playButton);
            fab.setOnClickListener(l -> startActivity(new Intent(this, WatchPageActivity.class)
                    .putExtra("id", movie.getMovieId())));

            // Set movie categories
            setCategories(movieId);
        });
    }


    /**
     * Initialize UI elements
     */
    private void initUI() {
        image = findViewById(R.id.ivMoviePoster);
        title = findViewById(R.id.movieTitle);
        categories = findViewById(R.id.movieCategories);
        description = findViewById(R.id.movieDescription);
        recyclerView = findViewById(R.id.recommendedMoviesRecycler);
        back = findViewById(R.id.ibBack);
    }

    /**
     * Configures RecyclerView to display recommended movies.
     */
    private void configureRecycleView() {
        MoviesViewModel moviesViewModel = new ViewModelProvider(this).get(MoviesViewModel.class);
        VerticalMovieGridAdapter adapter = new VerticalMovieGridAdapter(new LinkedList<>(), this);

        // Handle item click to open movie details
        adapter.setOnItemClickListener(movie -> {
            Intent intent = new Intent(this, MovieDetails.class);
            intent.putExtra("id", movie.getMovieId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        });

        // Set up layout manager and adapter
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        // Observe recommended movies and update adapter
        moviesViewModel.getRecommendedMovies(movieId).observe(this, movies -> {
            Log.d( "configureRecycleView: ", movies.toString());
            if (movies == null) return;
            adapter.updateMovies(movies);
        });
    }

    /**
     * Fetches and sets categories for the current movie.
     *
     * @param movieId The ID of the movie
     */
    private void setCategories(long movieId) {
        CategoriesViewModel viewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);

        // Observe movie categories and update UI
        viewModel.getMovieCategories(movieId).observe(this, categories -> {
            if (categories == null || categories.isEmpty()) return;

            StringBuilder s = new StringBuilder();
            Log.e("setCategories: ", categories.toString());

            for (Category c : categories) {
                s.append(c.getTitle()).append(", ");
            }

            // Remove trailing comma and space
            s.delete(s.length() - 2, s.length());
            this.categories.setText(s);
        });
    }

    /**
     * Handles exit behavior with a smooth transition.
     */
    private void exitActivity() {
        finish();
        overridePendingTransition(R.anim.nothing, R.anim.slide_out_right); // Custom transition
    }
}