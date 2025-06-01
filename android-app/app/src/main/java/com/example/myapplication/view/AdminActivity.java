package com.example.myapplication.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.VerticalMovieGridAdapter;
import com.example.myapplication.viewmodel.CategoriesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.LinkedList;

public class AdminActivity extends AppCompatActivity {
    private static final String CATEGORY = "category"; // Key for passing category data between activities

    private Spinner categoriesSpinner;
    private int numOfCategories;
    private CategoriesViewModel categoriesViewModel;

    /**
     * Called when the activity is first created.
     * Initializes UI components and sets up ViewModels, RecyclerView, and category spinner.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        // define the upper color to match tool bar
        int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        getWindow().setStatusBarColor(ContextCompat.getColor(this, uiMode == Configuration.UI_MODE_NIGHT_YES ? R.color.black : R.color.lightGray));

        setupUI(); // Initialize UI elements
        setupBackButton(); // Adjust manual back
        setupViewModels(); // Set up ViewModels for data management
        setupRecyclerView(); // Configure RecyclerView
        setupCategorySpinner(); // Populate category spinner
    }

    /**
     * Initializes UI elements and sets up click listeners.
     */
    private void setupUI() {
        categoriesSpinner = findViewById(R.id.spinnerCategories);
        Button btnEditCategory = findViewById(R.id.btnEditCategory);
        Button btnDeleteCategory = findViewById(R.id.btnDeleteCategory);
        ImageButton back = findViewById(R.id.ivBack);
        FloatingActionButton btnAddMovie = findViewById(R.id.btnAddMovie);

        // Set click listeners for buttons
        btnEditCategory.setOnClickListener(v -> editCategory());
        btnDeleteCategory.setOnClickListener(v -> deleteCategory());
        back.setOnClickListener(v -> exitActivity());
        btnAddMovie.setOnClickListener(v -> handleMovieAddition());

        // Adjust UI padding based on system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Sets up the back button to handle navigation manually.
     */
    private void setupBackButton() {
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exitActivity();
            }
        });
    }

    /**
     * Initializes the ViewModel for managing category data.
     */
    private void setupViewModels() {
        categoriesViewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
    }

    /**
     * Configures the RecyclerView to display movies in a grid layout.
     */
    private void setupRecyclerView() {
        RecyclerView recyclerMovies = findViewById(R.id.recyclerMovies);
        recyclerMovies.setLayoutManager(new GridLayoutManager(this, 2)); // 2-column grid layout
    }

    /**
     * Sets up the category spinner with available categories and default options.
     */
    private void setupCategorySpinner() {
        ArrayAdapter<String> categoriesAdapter = getStringArrayAdapter();
        categoriesSpinner.setAdapter(categoriesAdapter);

        categoriesAdapter.add(getString(R.string.select_category));
        categoriesAdapter.add(getString(R.string.add_category));
        categoriesSpinner.setSelection(0);

        categoriesViewModel.getCategoriesNames().observe(this, categories -> {
            categoriesAdapter.clear();
            categoriesAdapter.add(getString(R.string.select_category));
            categoriesAdapter.addAll(categories);
            numOfCategories = categories.size();
            categoriesAdapter.add(getString(R.string.add_category));
            categoriesSpinner.setSelection(0);
        });
    }

    /**
     * Exits the current activity and navigates back to the main activity.
     */
    private void exitActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish(); // Close current activity
    }

    /**
     * Handles editing a selected category.
     */
    private void editCategory() {
        if (categoriesSpinner.getSelectedItem().toString().equals(getString(R.string.add_category))
                || categoriesSpinner.getSelectedItem().toString().equals(getString(R.string.select_category))) {
            Toast.makeText(this, getString(R.string.please_be_on_a_valid_category), Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, AddEditCategoryActivity.class);
        intent.putExtra("add", false);
        intent.putExtra(CATEGORY, categoriesSpinner.getSelectedItem().toString());
        startActivity(intent);
        finish();
    }

    /**
     * Handles deleting a selected category.
     */
    private void deleteCategory() {
        if (categoriesSpinner.getSelectedItem() == null
                || categoriesSpinner.getSelectedItem().toString().equals(getString(R.string.select_category))
                || categoriesSpinner.getSelectedItem().toString().equals(getString(R.string.add_category))) {
            Toast.makeText(this, getString(R.string.please_be_on_a_valid_category), Toast.LENGTH_LONG).show();
            return;
        }
        categoriesViewModel.deleteCategoryByTitle(categoriesSpinner.getSelectedItem().toString()
        ).observe(this, integer -> {
            if (integer == 204) { // If delete is successful
                recreate(); // Refresh the activity
            }
            if (integer == 500) {
                alertDialog(getString(R.string.api_server_error_please_try_again_later));
                return;
            }
            if (integer == 400) {
                alertDialog(getString(R.string.invalid_category_selected));
            }
        });
    }

    /**
     * Called when the activity is starting.
     * Initializes the categories spinner and sets up its item selection listener.
     */
    @Override
    protected void onStart() {
        super.onStart();
        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleCategorySelection(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Handles the selected category from the spinner.
     *
     * @param categoryTitle The title of the selected category.
     */
    private void handleCategorySelection(String categoryTitle) {
        if (categoryTitle.equals(getString(R.string.add_category))) {
            startActivity(new Intent(getApplicationContext(), AddEditCategoryActivity.class));
            finish();
            return;
        }

        if (categoryTitle.equals(getString(R.string.select_category))) {
            return;
        }

        setupMoviesRecyclerView(categoryTitle);
    }

    /**
     * Sets up the RecyclerView to display movies based on the selected category.
     *
     * @param categoryTitle The title of the selected category.
     */
    private void setupMoviesRecyclerView(String categoryTitle) {
        VerticalMovieGridAdapter adapter = new VerticalMovieGridAdapter(new ArrayList<>(), this);

        adapter.setOnItemLongClickListener(movieId -> {
            CategoriesViewModel viewModel = new ViewModelProvider(this).get(CategoriesViewModel.class);
            viewModel.deleteMovieFromCategory(movieId, categoryTitle).observe(this, integer -> {
                if (integer == 0) return;

                if (integer == 404) {
                    Toast.makeText(this, getString(R.string.category_or_movie_doesn_t_exist), Toast.LENGTH_LONG).show();
                    return;
                }
                if (integer == 204) {
                    Toast.makeText(this, getString(R.string.movie_deleted_successfully), Toast.LENGTH_LONG).show();
                }
            });
        });

        adapter.setOnItemClickListener(movie -> {
            Intent intent = new Intent(getApplicationContext(), AddEditMovieActivity.class);
            intent.putExtra("add", false);
            intent.putExtra("category", categoryTitle);
            intent.putExtra("title", movie.getTitle());
            startActivity(intent);
            finish();
        });

        RecyclerView recyclerMovies = findViewById(R.id.recyclerMovies);
        recyclerMovies.setAdapter(adapter);

        categoriesViewModel.getMoviesByCategoryTitle(categoryTitle).observe(this, adapter::updateMovies);
    }

    /**
     * Initializes the categories spinner adapter.
     *
     * @return An ArrayAdapter for the spinner with default items.
     */
    @NonNull
    private ArrayAdapter<String> getStringArrayAdapter() {
        LinkedList<String> defaultItems = new LinkedList<>();
        return new ArrayAdapter<>(this, R.layout.text_view_spinner, defaultItems) {
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View v;
                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setVisibility(View.GONE);
                    tv.setHeight(0);
                    v = tv;
                    v.setVisibility(View.GONE);
                } else {
                    v = super.getDropDownView(position, null, parent);
                }
                return v;
            }
        };
    }


    /**
     * Handles the addition of a new movie.
     * If there are no categories, prompts the user to create one first.
     */
    private void handleMovieAddition() {
        if (numOfCategories == 0) {
            alertDialog(getString(R.string.you_need_to_create_a_category_first));
            return;
        }

        Intent intent = new Intent(this, AddEditMovieActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Displays an alert dialog with a specified message.
     *
     * @param message The message to display in the alert dialog.
     */
    private void alertDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error))
                .setMessage(message)
                .setNeutralButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                .show();
    }
}
