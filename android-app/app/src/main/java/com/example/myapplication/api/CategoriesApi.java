package com.example.myapplication.api;

import android.app.Application;
import android.net.TrafficStats;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.model.Category;
import com.example.myapplication.model.CategoryDao;
import com.example.myapplication.model.Movie;
import com.example.myapplication.model.MovieDao;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesApi {
    private final ApiService apiService;
    private final ExecutorService executor;

    public CategoriesApi(Application application) {
        this.executor = Executors.newSingleThreadExecutor();
        this.apiService = ApiService.getInstance(application.getApplicationContext());
        TrafficStats.setThreadStatsTag(124);
    }

    /**
     * Fetch all categories from API and update the local database
     *
     * @param categoryDao the category dao for category operations
     */
    public void getAllCategories(CategoryDao categoryDao) {
        apiService.getAllCategories().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                executor.execute(() -> {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful() || responseBody == null) {
                            Log.e("getAllCategories", "Error fetching categories");
                            return;
                        }
                        String jsonString = responseBody.string();
                        JsonArray categoriesArray = JsonParser.parseString(jsonString).getAsJsonArray();
                        List<String> categoryIds = new LinkedList<>();

                        for (int i = 0; i < categoriesArray.size(); i++) {
                            JsonObject categoryJson = categoriesArray.get(i).getAsJsonObject();
                            Category category = new Category(
                                    categoryJson.get("id").getAsString(),
                                    categoryJson.get("title").getAsString(),
                                    categoryJson.get("promoted").getAsBoolean()
                            );
                            categoryIds.add(category.getCategoryId());

                            if (!categoryDao.categoryExistsById(category.getCategoryId())) {
                                categoryDao.insertAll(category);
                            } else {
                                categoryDao.update(category);
                            }
                        }
                        categoryDao.removeOldData(categoryIds);
                    } catch (Exception e) {
                        Log.e("getAllCategories", "Error processing response", e);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("getAllCategories", "API call failed", t);
            }
        });
    }

    /**
     * Fetch categories and their movies
     *
     * @param categories the categories with movies
     */
    public void getCategoriesAndMovies(MutableLiveData<Map<String, List<Movie>>> categories) {
        apiService.getCategoriesAndMovies().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, List<Movie>>> call, @NonNull Response<Map<String, List<Movie>>> response) {
                if (response.isSuccessful()) {
                    categories.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, List<Movie>>> call, @NonNull Throwable t) {
                Log.e("getCategoriesAndMovies", "API call failed", t);
            }
        });
    }


    /**
     * Create a new category
     *
     * @param code        to listen to api response code
     * @param title       category title
     * @param promoted    is category promoted
     * @param categoryDao DAO for category operations
     */
    public void createCategory(MutableLiveData<Integer> code, String title, boolean promoted, CategoryDao categoryDao) {
        try {
            JSONObject json = new JSONObject();
            json.put("title", title);
            json.put("promoted", promoted);
            RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), json.toString());

            apiService.createNewCategory(requestBody).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.code() == 400) {
                        code.postValue(response.code());
                    }
                    if (response.isSuccessful()) {
                        getAllCategories(categoryDao);
                        code.postValue(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    code.postValue(500);
                }
            });
        } catch (Exception e) {
            Log.e("createCategory", "Error creating JSON object", e);
        }
    }


    /**
     * Remove a movie from a category
     *
     * @param movieId    movie id
     * @param categoryId category id
     * @param code       observer for api response code
     * @param movieDao   DAO for movie operations
     */
    public void removeMovieFromCategory(long movieId, String categoryId, MutableLiveData<Integer> code, MovieDao movieDao) {
        apiService.deleteMovieFromCategory(movieId, categoryId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> movieDao.deleteRef(movieId, categoryId));
                }
                code.postValue(response.code());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                code.postValue(500);
            }
        });
    }

    /**
     * Edit a category
     *
     * @param title       category title
     * @param promotion   category promoted
     * @param categoryId  category id
     * @param categoryDao DAO for category operations
     * @param code        response code from api
     */
    public void editCategory(String title, boolean promotion, String categoryId, CategoryDao categoryDao, MutableLiveData<Integer> code) {
        JSONObject json = new JSONObject();
        try {
            json = new JSONObject();
            json.put("title", title);
            json.put("promoted", promotion);
        } catch (Exception e) {
            Log.e("createCategory: ", "error getting json obj");
        }
        RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), json.toString());


        apiService.editCategory(categoryId, requestBody).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> categoryDao.update(new Category(categoryId, title, promotion)));
                }
                code.postValue(response.code());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                code.postValue(500);
            }
        });
    }


    /**
     * Delete a category
     *
     * @param categoryId  category id to delete
     * @param categoryDao DAO for category operations
     * @param code        response code from api
     */
    public void deleteCategory(String categoryId, CategoryDao categoryDao, MutableLiveData<Integer> code) {
        apiService.deleteCategory(categoryId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> {
                        Category category = categoryDao.getCategory(categoryId);
                        categoryDao.delete(category);
                    });
                }
                code.postValue(response.code());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                code.postValue(500);
            }
        });
    }
}
