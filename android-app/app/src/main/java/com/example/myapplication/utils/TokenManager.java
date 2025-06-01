package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;


// Utility class for managing JWT tokens using SharedPreferences.

public class TokenManager {
    private static final String PREFS_NAME = "myapp_prefs";
    private static final String KEY_TOKEN = "jwt_token";

    /**
     * Saves the JWT token in SharedPreferences.
     *
     * @param context The application context.
     * @param token   The JWT token to be saved.
     */
    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    /**
     * Retrieves the stored JWT token from SharedPreferences.
     *
     * @param context The application context.
     * @return The JWT token, or null if not found.
     */
    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    /**
     * Deletes the stored JWT token from SharedPreferences.
     *
     * @param context The application context.
     */
    public static void deleteToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(KEY_TOKEN).apply();
    }

    /**
     * Extracts the user's role from the JWT token payload.
     *
     * @param context The application context.
     * @return The role as a string, or an empty string if an error occurs.
     */
    public static String getRole(Context context) {
        return getPartFromToken(context, "role");
    }

    /**
     * Extracts the user's image URL from the JWT token payload.
     *
     * @param context The application context.
     * @return The image URL as a string, or an empty string if an error occurs.
     */
    public static String getImage(Context context) {
        return getPartFromToken(context, "image");
    }

    /**
     * Extracts a specific part from the JWT token payload.
     *
     * @param context The application context.
     * @param part    The part key to retrieve from the token.
     * @return The part value as a string, or an empty string if an error occurs.
     */
    private static String getPartFromToken(Context context, String part) {
        try {
            String token = getToken(context);
            if (token == null) {
                return "";
            }

            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return "";
            }

            String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE), StandardCharsets.UTF_8);
            JSONObject payload = new JSONObject(payloadJson);

            return payload.optString(part, "");
        } catch (Exception e) {
            Log.e("TokenManager", "Error parsing token payload", e);
            return "";
        }
    }
}
