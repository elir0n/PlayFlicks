package com.example.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.viewmodel.CategoriesViewModel;

public class AddEditCategoryActivity extends AppCompatActivity {
    // UI components
    private SwitchCompat scPromotion;  // Toggle switch for category promotion
    private EditText etCategoryTitle;  // Input field for category title
    private ProgressBar progressBar;   // Progress bar for loading indication
    private TextView tvProgressBar;    // Text associated with the progress bar

    // ViewModel for handling category data
    private CategoriesViewModel categoriesViewModel;

    // State variables
    private boolean isAdd;    // Determines if the activity is in Add or Edit mode
    private String categoryId; // Stores the category ID in Edit mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enables edge-to-edge UI
        setContentView(R.layout.activity_category_add_edit);

        // Adjust layout for system UI insets (status and navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Determine whether adding or editing a category
        isAdd = getIntent().getBooleanExtra("add", true);

        // Initialize UI components
        scPromotion = findViewById(R.id.scPromotion);
        etCategoryTitle = findViewById(R.id.etCategoryTitle);
        progressBar = findViewById(R.id.progressBar);
        tvProgressBar = findViewById(R.id.tvProgressBar);

        Button btnSave = findViewById(R.id.btnSaveCategory);
        Button btnCancel = findViewById(R.id.btnCancelCategory);

        categoriesViewModel = new CategoriesViewModel(getApplication());

        // If editing, load the existing category data
        if (!isAdd) {
            loadData();
        }

        // Set event listeners
        btnCancel.setOnClickListener(v -> exitActivity());
        btnSave.setOnClickListener(v -> handleSave());

        // Handle back press to exit
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exitActivity();
            }
        });
    }

    /**
     * Exits the activity and navigates back to AdminActivity.
     */
    private void exitActivity() {
        startActivity(new Intent(this, AdminActivity.class));
        finish();
    }

    /**
     * Displays an alert dialog with a given message.
     */
    private void alertDialogDisplay(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setNeutralButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Handles the save button click. Validates input and determines whether to create or edit a category.
     */
    private void handleSave() {
        enableProgressBarDisplay(getString(R.string.updating_data_please_wait));
        String title = etCategoryTitle.getText().toString().trim();
        if (title.isEmpty()) {
            disableProgressBarDisplay();
            alertDialogDisplay("Title Required");
            return;
        }

        boolean promoted = scPromotion.isChecked();

        if (isAdd) {
            createCategory(title, promoted);
        } else {
            editCategory(title, promoted);
        }
    }

    /**
     * Loads category data from ViewModel when in Edit mode.
     */
    private void loadData() {
        enableProgressBarDisplay(getString(R.string.loading_data_please_wait));
        String categoryName = getIntent().getStringExtra("category");

        categoriesViewModel.getCategoryByName(categoryName).observe(this, category -> {
            if (category == null) return;

            scPromotion.setChecked(category.isPromoted());
            etCategoryTitle.setText(category.getTitle());
            categoryId = category.getCategoryId();
            disableProgressBarDisplay();
        });
    }

    /**
     * Edits an existing category.
     */
    private void editCategory(String title, boolean promoted) {

        categoriesViewModel.editCategory(title, promoted, categoryId).observe(this, resultCode -> {
            disableProgressBarDisplay();

            switch (resultCode) {
                case 400:
                    disableProgressBarDisplay();
                    alertDialogDisplay(getString(R.string.category_already_exists));
                    break;
                case 204:
                    disableProgressBarDisplay();
                    Toast.makeText(this, getString(R.string.category_modified_successfully), Toast.LENGTH_LONG).show();
                    exitActivity();
                    break;
                case 500:
                    disableProgressBarDisplay();
                    alertDialogDisplay(getString(R.string.api_server_error_please_try_again_later));
                    break;
            }
        });
    }

    /**
     * Creates a new category.
     */
    private void createCategory(String title, boolean promoted) {

        categoriesViewModel.createCategory(title, promoted).observe(this, resultCode -> {
            switch (resultCode) {
                case 400:
                    disableProgressBarDisplay();
                    alertDialogDisplay(getString(R.string.category_already_exists));
                    break;
                case 201:
                    disableProgressBarDisplay();
                    Toast.makeText(this, getString(R.string.category_added_successfully), Toast.LENGTH_LONG).show();
                    exitActivity();
                    break;
                case 500:
                    disableProgressBarDisplay();
                    alertDialogDisplay(getString(R.string.api_server_error_please_try_again_later));
                    break;
            }
        });
    }

    /**
     * Displays the progress bar with a given message and prevents user interaction.
     */
    private void enableProgressBarDisplay(String text) {
        progressBar.setVisibility(View.VISIBLE);
        tvProgressBar.setVisibility(View.VISIBLE);
        tvProgressBar.setText(text);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     * Hides the progress bar and restores user interaction.
     */
    private void disableProgressBarDisplay() {
        progressBar.setVisibility(View.GONE);
        tvProgressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
