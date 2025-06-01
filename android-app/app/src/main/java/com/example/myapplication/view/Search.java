package com.example.myapplication.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.VerticalMovieGridAdapter;
import com.example.myapplication.model.Movie;
import com.example.myapplication.viewmodel.MoviesViewModel;

import java.util.LinkedList;

public class Search extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        getWindow().setStatusBarColor(ContextCompat.getColor(this, uiMode == Configuration.UI_MODE_NIGHT_YES ? R.color.black : R.color.lightGray));


        SearchView searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        searchView.setOnClickListener(v -> {
            searchView.setIconified(false);
            searchView.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            clearData();
            return false;
        });

        ImageButton back = findViewById(R.id.ivBack);

        back.setOnClickListener(v -> finish());
    }

    private void clearData() {
        recyclerView.setAdapter(null);
        recyclerView.stopScroll();
        recyclerView.clearAnimation();

    }

    private void handleQuery(String query) {
        MoviesViewModel moviesViewModel = new ViewModelProvider(this).get(MoviesViewModel.class);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        VerticalMovieGridAdapter adapter = new VerticalMovieGridAdapter(new LinkedList<>(), this);
        adapter.setOnItemClickListener(movie -> {
            Intent movieDesc = new Intent(this, MovieDetails.class);
            movieDesc.putExtra("id", movie.getMovieId());
            startActivity(movieDesc);
        });
        recyclerView.setAdapter(adapter);
        moviesViewModel.getQueryMovies(query).observe(this, adapter::updateMovies);
    }
}