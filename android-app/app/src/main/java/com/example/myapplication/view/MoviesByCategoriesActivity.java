package com.example.myapplication.view;


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
import com.example.myapplication.viewmodel.CategoriesViewModel;

import java.util.LinkedList;

public class MoviesByCategoriesActivity extends AppCompatActivity {
    private ListItemsAdapter categoriesAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // change ui mode based on current settings.
        int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        getWindow().setStatusBarColor(ContextCompat.getColor(this, uiMode == Configuration.UI_MODE_NIGHT_YES ? R.color.black : R.color.lightGray));

        Button filterButton = findViewById(R.id.seeAllCategoriesButton);

        filterButton.setText(R.string.see_random_movies);
        filterButton.setOnClickListener(v -> getFilter());

        CategoriesViewModel categoriesViewModelViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);

        categoriesAdapter = new ListItemsAdapter(this, new LinkedList<>());

        categoriesAdapter.setOnClickCardListener(movie -> {
            Intent intent = new Intent(this, MovieDetails.class);
            intent.putExtra("id", movie.getMovieId());
            startActivity(intent);
            // Apply transition animation
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        });

        recyclerView.setAdapter(categoriesAdapter);

        // getting movies by their categories
        categoriesViewModelViewModel.getCategories().observe(this, categories -> {
            if (categories != null) {
                categoriesAdapter.updateData(categories);
            }
        });
    }

    private void getFilter() {
        recyclerView.animate()
                .alpha(0)
                .setDuration(500)
                .withEndAction(() -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }).start();
    }
}
