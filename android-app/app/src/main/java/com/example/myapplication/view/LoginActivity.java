package com.example.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.api.UsersApi;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        initUI();

        Button loginButton = findViewById(R.id.loginButton);
        signUp.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });


        loginButton.setOnClickListener(v -> loginUser());
    }

    /**
     * init UI components
     */

    private void initUI() {
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUp = findViewById(R.id.signUpText);
    }

    /**
     * login the user if authenticated, else displays corresponding error messages
     */
    private void loginUser() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_fill_in_both_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        UsersApi usersApi = new UsersApi(this.getApplication());
        usersApi.login(this, username, password);
    }
}
