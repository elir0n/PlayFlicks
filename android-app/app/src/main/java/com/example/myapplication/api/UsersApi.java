package com.example.myapplication.api;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.example.myapplication.utils.FileManager;
import com.example.myapplication.utils.TokenManager;
import com.example.myapplication.view.LoginActivity;
import com.example.myapplication.view.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsersApi {

    private final ApiService apiService;

    public UsersApi(Application application) {
        // Initialize Retrofit with base URL and GSON converter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(application.getString(R.string.BASE_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Tag network requests for debugging
        TrafficStats.setThreadStatsTag(1234);

        apiService = retrofit.create(ApiService.class);
    }

    /**
     * Attempts to log in the user with the provided credentials.
     *
     * @param context  the application context
     * @param name     the username
     * @param password the password
     */
    public void login(Context context, String name, String password) {
        try {
            JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("password", password);

            RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), json.toString());

            apiService.getToken(requestBody).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    try (ResponseBody responseBody = response.body()) {
                        if (response.isSuccessful() && responseBody != null) {

                            String token = responseBody.string();
                            TokenManager.saveToken(context, token);
                            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
                            navigateToMainActivity(context);

                        } else {
                            showToast(context, "Login failed");
                        }
                    } catch (Exception e) {
                        Log.e("LOGIN_ERROR", "Error reading token", e);
                        showToast(context, "Error reading token");
                    }

                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                    Log.e("LOGIN_ERROR", "Connection failed", throwable);
                    showToast(context, "Connection failed");
                }
            });
        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Invalid JSON format", e);
        }
    }

    /**
     * Registers a new user with an image, username, password, and display name.
     *
     * @param context     the application context
     * @param imageUri    the URI of the profile image
     * @param username    the username
     * @param password    the password
     * @param displayName the display name
     */
    public void register(Context context, Uri imageUri, String username, String password, String displayName) {
        File imageFile;
        try {
            imageFile = FileManager.convertUriToFile(context, imageUri);
        } catch (Exception e) {
            Log.e("REGISTER_ERROR", "Image conversion failed");
            showToast(context, "Registration failed");
            return;
        }


        MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                "image", imageFile.getName(), RequestBody.create(MediaType.parse("image/*"), imageFile));

        RequestBody usernameBody = createRequestBody(username);
        RequestBody passwordBody = createRequestBody(password);
        RequestBody displayNameBody = createRequestBody(displayName);

        apiService.registerUser(imagePart, usernameBody, passwordBody, displayNameBody)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            showToast(context, "Registration successful");
                            navigateToLoginActivity(context);
                        } else {
                            showToast(context, "Registration failed");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Log.e("REGISTER_ERROR", "Registration request failed", t);
                        showToast(context, "Registration failed");
                    }
                });
    }

    /**
     * Helper method to create a RequestBody from a String.
     *
     * @param value the string value
     * @return the RequestBody instance
     */
    private RequestBody createRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    /**
     * Navigates to the MainActivity.
     *
     * @param context the application context
     */
    private void navigateToMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    /**
     * Navigates to the LoginActivity.
     *
     * @param context the application context
     */
    private void navigateToLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    /**
     * Displays a toast message.
     *
     * @param context the application context
     * @param message the message to display
     */
    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
