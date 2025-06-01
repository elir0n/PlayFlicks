package com.example.myapplication.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.api.UsersApi;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText, confirmPasswordEditText, displayNameEditText;
    private ImageView profileImageView;
    private Button loginButton, browseButton;

    private TextView signIn;

    private Uri imageUri;

    /**
     * Pick image for user
     */
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;
                    profileImageView.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        initUI();

        // Set up image picker for profile image and browse button
        profileImageView.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        browseButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        signIn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Register user on login button click
        loginButton.setOnClickListener(v -> registerUser());
    }

    /**
     * Init UI elements
     */
    private void initUI() {
        signIn = findViewById(R.id.signInText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.validatePasswordEditText);
        displayNameEditText = findViewById(R.id.displayNameEditText);
        profileImageView = findViewById(R.id.profileImageView);
        loginButton = findViewById(R.id.loginButton);
        browseButton = findViewById(R.id.browseButton);
    }

    /**
     * Register user, display corresponding message on failure
     */

    private void registerUser() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String displayName = displayNameEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty() || displayName.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 8) {
            Toast.makeText(this, getString(R.string.the_password_needs_to_be_at_least_8_characters_long), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null || imageUri.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.please_select_a_profile_image), Toast.LENGTH_SHORT).show();
            return;
        }

        UsersApi usersApi = new UsersApi(this.getApplication());
        usersApi.register(this, imageUri, username, password, displayName);
    }
}