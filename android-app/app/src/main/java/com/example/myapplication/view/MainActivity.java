package com.example.myapplication.view;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ListItemsAdapter;
import com.example.myapplication.adapter.MoviesAdapter;
import com.example.myapplication.model.Movie;
import com.example.myapplication.viewmodel.CategoriesViewModel;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ListItemsAdapter categoriesAdapter;

    private RecyclerView recyclerView;

    private Button filterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initUI();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        CategoriesViewModel categoriesViewModelViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);

        int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        getWindow().setStatusBarColor(ContextCompat.getColor(this, uiMode == Configuration.UI_MODE_NIGHT_YES ? R.color.black : R.color.lightGray));


        filterButton.setOnClickListener(v -> getFilter());


        categoriesAdapter = new ListItemsAdapter(this, new HashMap<>());

        categoriesAdapter.setOnClickCardListener(movie -> {
            Intent intent = new Intent(this, MovieDetails.class);
            intent.putExtra("id", movie.getMovieId());
            startActivity(intent);
            // Apply transition animation
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        });

        recyclerView.setAdapter(categoriesAdapter);

        categoriesViewModelViewModel.getAllPromotedCategories().observe(this, categoriesWitMovies -> {
            if (categoriesWitMovies != null) {
                categoriesAdapter.updateData(categoriesWitMovies);
            }
        });


    }

    /**
     * init UI components
     */
    private void initUI() {
        recyclerView = findViewById(R.id.recycle);
        filterButton = findViewById(R.id.seeAllCategoriesButton);

    }

    /**
     * going to see movies by categories
     */
    private void getFilter() {
        recyclerView.animate()
                .alpha(0)
                .setDuration(500)
                .withEndAction(() -> {
                    Intent intent = new Intent(this, MoviesByCategoriesActivity.class);
                    startActivity(intent);
                    finish();
                }).start();

    }
}
