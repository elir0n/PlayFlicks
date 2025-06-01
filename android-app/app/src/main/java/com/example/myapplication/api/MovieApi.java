package com.example.myapplication.api;

import android.app.Application;
import android.net.TrafficStats;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieApi {
    private final ApiService apiService;
    private final ExecutorService executor;
    private static final String TAG = "MovieApi";

    public MovieApi(Application myApplication) {
        this.executor = Executors.newSingleThreadExecutor();
        this.apiService = ApiService.getInstance(myApplication.getApplicationContext());
        TrafficStats.setThreadStatsTag(1234);
    }


    /**
     * Fetches all movies from the API and updates the local database
     *
     * @param movieDao    DAO for movie operations
     * @param categoryDao DAO for category operations
     */
    public void getAllMovies(MovieDao movieDao, CategoryDao categoryDao) {
        apiService.getAllMovies().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error fetching movies");
                    return;
                }
                try (ResponseBody body = response.body()) {
                    String jsonString;
                    if (body != null) {
                        jsonString = body.string();
                    } else {
                        jsonString = null;
                    }
                    executor.execute(() -> processMoviesResponse(jsonString, movieDao, categoryDao));
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing movie response", e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Error fetching movies", throwable);
            }
        });
    }

    /**
     * Presses the movie data
     *
     * @param jsonString  the Json string from server
     * @param movieDao    DAO for movie operations
     * @param categoryDao DAO for category operations
     */
    private void processMoviesResponse(String jsonString, MovieDao movieDao, CategoryDao categoryDao) {
        JsonArray movies = JsonParser.parseString(jsonString).getAsJsonArray();
        List<Long> movieIds = new LinkedList<>();

        for (int i = 0; i < movies.size(); i++) {
            JsonObject m = movies.get(i).getAsJsonObject();
            if (!m.has("title") || !m.has("image") || !m.has("video")) continue;

            String description = m.has("description") ? m.get("description").getAsString() : "";
            Movie movie = new Movie(m.get("id").getAsLong(), m.get("title").getAsString(),
                    m.get("image").getAsString(), m.get("video").getAsString(), description);

            movieIds.add(movie.getMovieId());
            if (movieDao.movieExistsById(movie.getMovieId())) {
                movieDao.updateAll(movie);
            } else {
                movieDao.insertAll(movie);
            }

            // Process categories
            if (m.has("categories")) {
                JsonArray categories = m.get("categories").getAsJsonArray();
                for (int j = 0; j < categories.size(); j++) {
                    String categoryId = categories.get(j).getAsString();
                    if (categoryDao.categoryExistsById(categoryId)) {
                        movieDao.insertMovieCategoryCrossRef(new MovieCategoryCrossRef(movie.getMovieId(), categoryId));
                    }
                }
            }
        }
        movieDao.removeOldData(movieIds);
    }

    /**
     * Fetches movie recommendations and updates LiveData
     *
     * @param movieDao        DAO for movie data
     * @param movieId         movie id for recommendation
     * @param recommendMovies live data of the recommended movies
     */
    public void getMoviesRecommendations(MovieDao movieDao, long movieId, MutableLiveData<List<Movie>> recommendMovies) {
        apiService.getRecommendedMovies(movieId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (!response.isSuccessful()) return;
                try (ResponseBody body = response.body()) {
                    List<Movie> recommendedMovies = new LinkedList<>();
                    String[] movies;
                    if (body != null) {
                        movies = body.string().replace("\"", "").split(" ");
                    } else {
                        Log.e(TAG, "Error fetching recommendations");
                        return;
                    }

                    executor.execute(() -> {
                        for (String movie : movies) {
                            if (!movie.isEmpty()) {
                                recommendedMovies.add(movieDao.getMovieById(Long.parseLong(movie)));
                            }
                        }
                        recommendMovies.postValue(recommendedMovies);
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error fetching recommendations", e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Error fetching recommendations", throwable);
            }
        });
    }


    /**
     * Edit or create movie
     *
     * @param title       movie title
     * @param description movie description
     * @param image       movie image
     * @param video       movie video
     * @param categories  movie categories
     * @param code        api server code
     * @param categoryDao DAO for category operations
     * @param movieDao    DAO for movie operations
     * @param movieId     movie id to edit
     */

    private void uploadMovie(String title, String description, File image, File video,
                             List<Category> categories, MutableLiveData<Integer> code,
                             CategoryDao categoryDao, MovieDao movieDao,
                             boolean isEdit, long movieId) {

        // Prepare multipart request bodies for image and video (if provided)
        MultipartBody.Part imagePart = image != null ? MultipartBody.Part.createFormData(
                "image-video", image.getName(), RequestBody.create(MediaType.parse("image/*"), image)) : null;

        MultipartBody.Part videoPart = video != null ? MultipartBody.Part.createFormData(
                "image-video", video.getName(), RequestBody.create(MediaType.parse("video/*"), video)) : null;

        // Prepare request bodies for title and description
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);

        // Convert category list to JSON
        List<String> categoryIds = new LinkedList<>();
        for (Category category : categories) {
            categoryIds.add(category.getCategoryId());
        }
        String jsonCategories = new Gson().toJson(categoryIds);
        RequestBody categoriesBody = RequestBody.create(MediaType.parse("application/json"), jsonCategories);

        // Make API call (create or edit movie based on isEdit flag)
        Call<ResponseBody> call = isEdit ?
                apiService.editMovie(movieId, imagePart, videoPart, titleBody, descriptionBody, categoriesBody) :
                apiService.createNewMovie(imagePart, videoPart, titleBody, descriptionBody, categoriesBody);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    getAllMovies(movieDao, categoryDao);  // Refresh local movie list
                }
                code.postValue(response.code());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e("uploadMovie", "Error uploading movie", throwable);
                code.postValue(500);
            }
        });
    }


    /**
     * Edit  movie
     *
     * @param title       movie title
     * @param description movie description
     * @param image       movie image
     * @param video       movie video
     * @param categories  movie categories
     * @param code        api server code
     * @param categoryDao DAO for category operations
     * @param movieDao    DAO for movie operations
     */
    public void createMovie(String title, String description, File image, File video, List<Category> categories, MutableLiveData<Integer> code, CategoryDao categoryDao, MovieDao movieDao) {
        uploadMovie(title, description, image, video, categories, code, categoryDao, movieDao, false, -1);
    }

    /**
     * Edit  movie
     *
     * @param title       movie title
     * @param description movie description
     * @param image       movie image
     * @param video       movie video
     * @param categories  movie categories
     * @param code        api server code
     * @param categoryDao DAO for category operations
     * @param movieDao    DAO for movie operations
     * @param movieId     movie id to edit
     */
    public void editMovie(long movieId, String title, String description, File image, File video, List<Category> categories, MutableLiveData<Integer> code, CategoryDao categoryDao, MovieDao movieDao) {
        uploadMovie(title, description, image, video, categories, code, categoryDao, movieDao, true, movieId);
    }

    /**
     * Adds a movie to the user's watched list
     *
     * @param movieId movie id to add to user watched
     */
    public void addMovieToUserWatchedList(long movieId) {
        apiService.addMovieToUserWatchedList(movieId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Log.d(TAG, response.isSuccessful() ? "Movie added to watched list" : "Failed to add movie");
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Error adding movie to watched list", throwable);
            }
        });
    }

    /**
     * Getting all queried movies
     *
     * @param query         query filter
     * @param queriedMovies queried movies live data
     * @param movieDao      DAO for movie operations
     */
    public void getQueryMovies(String query, MutableLiveData<List<Movie>> queriedMovies, MovieDao movieDao) {
        apiService.getQueriedMovie(query).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                queriedMovies.setValue(new LinkedList<>());
                if (!response.isSuccessful()) {
                    return;
                }
                executor.execute(() -> {
                    List<Movie> queriedMoviesList = new LinkedList<>();
                    JsonArray movies;
                    try (ResponseBody responseBody = response.body()) {
                        movies = JsonParser.parseString(Objects.requireNonNull(responseBody).
                                string()).getAsJsonArray();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.e("onResponse: ", movies.toString());
                    for (JsonElement movieLine : movies) {
                        JsonObject object = movieLine.getAsJsonObject();

                        Movie movie = movieDao.getMovieById(object.get("id").getAsLong());

                        queriedMoviesList.add(movie);
                    }
                    queriedMovies.postValue(queriedMoviesList);
                });
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e(TAG, "onFailure: Couldn't connect to api");
            }
        });
    }
}
