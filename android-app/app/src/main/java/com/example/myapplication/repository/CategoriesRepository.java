package com.example.myapplication.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.api.CategoriesApi;
import com.example.myapplication.model.AppDB;
import com.example.myapplication.model.Category;
import com.example.myapplication.model.CategoryDao;
import com.example.myapplication.model.Movie;
import com.example.myapplication.model.MovieDao;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository class for managing categories and associated movies.
 * Provides methods to interact with the database and API to create, update, delete, and fetch categories and movies.
 */
public class CategoriesRepository {

    // Instance variables
    private final MutableLiveData<Map<String, List<Movie>>> promotedCategoriesWithMovies;
    private final CategoryDao categoryDao;
    private final MovieDao movieDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final CategoriesApi api;

    private static CategoriesRepository INSTANCE;

    /**
     * Private constructor for initializing the repository with application context.
     *
     * @param application The application context
     */
    private CategoriesRepository(Application application) {
        AppDB db = AppDB.getInstance(application.getApplicationContext());
        categoryDao = db.categoryDao();
        movieDao = db.movieDao();
        application.getApplicationContext();
        this.api = new CategoriesApi(application);
        promotedCategoriesWithMovies = new MutableLiveData<>();
    }

    /**
     * Gets the singleton instance of the repository.
     *
     * @param application The application context
     * @return The singleton instance of CategoriesRepository
     */
    public static CategoriesRepository getInstance(Application application) {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new CategoriesRepository(application);
        INSTANCE.refreshData();
        return INSTANCE;
    }

    /**
     * Creates a new category in the database.
     *
     * @param title     The title of the new category
     * @param promotion The promotion flag for the category
     * @return A LiveData object containing an integer exit code indicating success or failure
     */
    public LiveData<Integer> createCategory(String title, boolean promotion) {
        MutableLiveData<Integer> error = new MutableLiveData<>(0);
        executor.execute(() -> {
            if (categoryDao.categoryExists(title)) {
                error.postValue(400);
                return;
            }
            api.createCategory(error, title, promotion, categoryDao);
        });
        return error;
    }

    /**
     * Edits an existing category in the database.
     *
     * @param title      The new title of the category
     * @param promotion  The new promotion flag for the category
     * @param categoryId The ID of the category to be edited
     * @return A LiveData object containing an integer exit code indicating success or failure
     */
    public LiveData<Integer> editCategory(String title, boolean promotion, String categoryId) {
        MutableLiveData<Integer> exitCode = new MutableLiveData<>(0);
        executor.execute(() -> {
            if (!categoryDao.categoryExists(title)) {
                exitCode.postValue(400);
                return;
            }
            api.editCategory(title, promotion, categoryId, categoryDao, exitCode);
        });
        return exitCode;
    }

    /**
     * Deletes a category by its title.
     *
     * @param title The title of the category to be deleted
     * @return A LiveData object containing an integer exit code indicating success or failure
     */
    public LiveData<Integer> deleteCategoryByTitle(String title) {
        MutableLiveData<Integer> exitCode = new MutableLiveData<>(0);
        if (title == null) {
            exitCode.postValue(400);
        }
        executor.execute(() -> {
            Category category = categoryDao.getCategoryByTitle(title);
            api.deleteCategory(category.getCategoryId(), categoryDao, exitCode);
        });
        return exitCode;
    }

    /**
     * Retrieves a category by its title.
     *
     * @param categoryTitle The title of the category
     * @return A LiveData object containing the requested Category
     */
    public LiveData<Category> getCategoryByTitle(String categoryTitle) {
        MutableLiveData<Category> categoryData = new MutableLiveData<>();
        executor.execute(() -> {
            Category category = categoryDao.getCategoryByTitle(categoryTitle);
            categoryData.postValue(category);
        });
        return categoryData;
    }

    // Category Query Operations

    /**
     * Retrieves the names of all categories.
     *
     * @return A LiveData object containing a list of category names
     */
    public LiveData<List<String>> getNames() {
        return categoryDao.getNames();
    }

    /**
     * Retrieves categories along with their associated movies.
     *
     * @return A LiveData object containing a map of categories and their associated movies
     */
    public LiveData<Map<String, List<Movie>>> getCategoriesWithMovies() {
        api.getCategoriesAndMovies(promotedCategoriesWithMovies);
        return promotedCategoriesWithMovies;
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return A LiveData object containing a list of all categories
     */
    public LiveData<List<Category>> getAll() {
        return categoryDao.getAll();
    }

    /**
     * Retrieves movies of a specific category.
     *
     * @param categoryTitle The title of the category
     * @return A LiveData object containing a list of movies in the specified category
     */
    public LiveData<List<Movie>> getMoviesByCategoryTitle(String categoryTitle) {
        return categoryDao.getMoviesByCategoryTitle(categoryTitle);
    }

    /**
     * Retrieves categories associated with a specific movie.
     *
     * @param movieId The ID of the movie
     * @return A LiveData object containing a list of categories associated with the movie
     */
    public LiveData<List<Category>> getMovieCategories(long movieId) {
        return categoryDao.getMovieCategories(movieId);
    }

    /**
     * Removes a movie from a specific category.
     *
     * @param movieId       The ID of the movie to be removed
     * @param categoryTitle The title of the category
     * @return A LiveData object containing an integer exit code indicating success or failure
     */
    public LiveData<Integer> removeMovieFromCategory(long movieId, String categoryTitle) {
        MutableLiveData<Integer> exitCode = new MutableLiveData<>(0);
        executor.execute(() -> {
            Category categoriesAddition = categoryDao.getCategoryByTitle(categoryTitle);
            if (categoriesAddition == null) {
                exitCode.postValue(400);
                return;
            }
            api.removeMovieFromCategory(movieId, categoriesAddition.getCategoryId(), exitCode, movieDao);
        });
        return exitCode;
    }

    /**
     * Refreshes the category data by fetching the latest data from the API.
     */
    public void refreshData() {
        api.getAllCategories(categoryDao);
    }
}
