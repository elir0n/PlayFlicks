package com.example.myapplication.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.api.MovieApi;
import com.example.myapplication.model.AppDB;
import com.example.myapplication.model.Category;
import com.example.myapplication.model.CategoryDao;
import com.example.myapplication.model.Movie;
import com.example.myapplication.model.MovieDao;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository class responsible for managing movie-related data operations.
 * It interacts with the local database (via DAOs) and remote API.
 */
public class MoviesRepository {

    // DAO and API references
    private final MovieDao movieDao;
    private final CategoryDao categoryDao;
    private final MovieApi api;

    // Executor for background tasks
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MovieListData recommendMovies;

    // Singleton instance
    private static volatile MoviesRepository INSTANCE;

    /**
     * Constructor for MoviesRepository.
     * Initializes DAOs and API, sets up background task executor, and starts data refresh.
     *
     * @param application the application context used to initialize the repository
     */
    public MoviesRepository(Application application) {
        AppDB db = AppDB.getInstance(application.getApplicationContext());
        movieDao = db.movieDao();
        categoryDao = db.categoryDao();
        api = new MovieApi(application);
        // LiveData for random and recommended movies
        recommendMovies = new MovieListData();
        refreshData();
    }

    /**
     * Returns the singleton instance of MoviesRepository.
     * Ensures only one instance of the repository exists.
     *
     * @param application the application context
     * @return the singleton MoviesRepository instance
     */
    public static MoviesRepository getInstance(Application application) {


        if (INSTANCE != null) {
            return INSTANCE;
        }

        INSTANCE = new MoviesRepository(application);
        INSTANCE.refreshData();
        return INSTANCE;
    }

    /**
     * Fetches a movie by its unique ID.
     *
     * @param movieId the ID of the movie to fetch
     * @return LiveData representing the movie with the given ID
     */
    public LiveData<Movie> getMovieById(long movieId) {
        return movieDao.getMovieByMovieId(movieId);
    }

    /**
     * MovieListData class representing a mutable LiveData list of movies.
     * Extends MutableLiveData to manage the list of movie objects.
     */
    static class MovieListData extends MutableLiveData<List<Movie>> {
        public MovieListData() {
            super();
        }

        @Override
        protected void onActive() {
            super.onActive();
        }
    }

    /**
     * Refreshes movie data by fetching all movies from the API and updating the local database.
     */
    public void refreshData() {
        api.getAllMovies(movieDao, categoryDao);
    }

    // Movie Operations

    /**
     * Creates a new movie by interacting with the API and updating the local database.
     *
     * @param movieTitle       the title of the new movie
     * @param movieDescription the description of the new movie
     * @param image            the image file for the movie
     * @param video            the video file for the movie
     * @param categories       the categories of the new movie
     * @return LiveData containing an exit code representing the success/failure of the operation
     */
    public LiveData<Integer> createMovie(String movieTitle, String movieDescription, File image, File video, List<String> categories) {
        MutableLiveData<Integer> exitCode = new MutableLiveData<>(0);
        executor.execute(() -> {
            List<Category> categoriesAddition = categoryDao.getCategoriesByTitle(categories);
            if (categoriesAddition == null) {
                exitCode.postValue(400);
                return;
            }
            api.createMovie(movieTitle, movieDescription, image, video, categoriesAddition, exitCode, categoryDao, movieDao);
        });
        return exitCode;
    }

    /**
     * Fetches a movie by its title.
     *
     * @param title the title of the movie to fetch
     * @return LiveData representing the movie with the given title
     */
    public LiveData<Movie> getMovieByTitle(String title) {
        MutableLiveData<Movie> movie = new MutableLiveData<>();
        executor.execute(() -> movie.postValue(movieDao.getMovieByTitle(title)));
        return movie;
    }

    /**
     * Edits an existing movie by updating its details in the local database and through the API.
     *
     * @param movieTitle       the new title of the movie
     * @param movieDescription the new description of the movie
     * @param image            the new image file for the movie
     * @param video            the new video file for the movie
     * @param categories       the new categories for the movie
     * @param oldMovieTitle    the title of the movie to be edited
     * @return LiveData containing an exit code representing the success/failure of the operation
     */
    public LiveData<Integer> editMovie(String movieTitle, String movieDescription, File image, File video, List<String> categories, String oldMovieTitle) {
        MutableLiveData<Integer> exitCode = new MutableLiveData<>(0);
        executor.execute(() -> {
            List<Category> currentCategories = categoryDao.getCategoriesByTitle(categories);
            if (currentCategories == null) {
                exitCode.postValue(400);
                return;
            }

            Movie movie = movieDao.getMovieByTitle(oldMovieTitle);
            if (movie == null) {
                Log.e("editMovie: ", "movieNull: " + oldMovieTitle);
                return;
            }
            api.editMovie(movie.getMovieId(), movieTitle, movieDescription, image, video, currentCategories, exitCode, categoryDao, movieDao);
        });
        return exitCode;
    }

    /**
     * Fetches a list of movies belonging to a specific category ID.
     *
     * @param categoryId the ID of the category to filter by
     * @return LiveData representing a list of movies within the specified category
     */
    public LiveData<List<Movie>> getMoviesByCategoryId(String categoryId) {
        return movieDao.getMoviesOfCategory(categoryId);
    }

    /**
     * Fetches recommended movies based on a specific movie ID.
     *
     * @param movieId the ID of the movie for which recommendations are generated
     * @return LiveData representing a list of recommended movies
     */
    public LiveData<List<Movie>> getRecommendedMovies(long movieId) {
        recommendMovies.setValue(new ArrayList<>());
        api.getMoviesRecommendations(movieDao, movieId, recommendMovies);
        return recommendMovies;
    }

    /**
     * Searches for movies matching a query string.
     *
     * @param query the query string to search for
     * @return LiveData representing a list of movies matching the query
     */
    public LiveData<List<Movie>> getQueryMovies(String query) {
        MutableLiveData<List<Movie>> queriedMovies = new MutableLiveData<>();
        queriedMovies.setValue(new LinkedList<>());
        api.getQueryMovies(query, queriedMovies, movieDao);
        return queriedMovies;
    }

    /**
     * Adds a movie to the user's watched list.
     *
     * @param movieId the ID of the movie to be added to the watched list
     */
    public void addMovieToUserWatchedList(long movieId) {
        api.addMovieToUserWatchedList(movieId);
    }
}
