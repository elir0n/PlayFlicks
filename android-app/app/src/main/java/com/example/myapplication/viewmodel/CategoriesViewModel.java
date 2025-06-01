package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.model.Category;
import com.example.myapplication.model.Movie;
import com.example.myapplication.repository.CategoriesRepository;

import java.util.List;
import java.util.Map;

/**
 * ViewModel class for managing categories and their associated operations.
 * This ViewModel interacts with the repository to fetch, create, update, and delete categories,
 * as well as associate movies with categories.
 */
public class CategoriesViewModel extends AndroidViewModel {

    private final CategoriesRepository categoriesRepository;

    /**
     * Constructor for CategoriesViewModel.
     * Initializes the repository and fetches the initial list of categories.
     *
     * @param application The application context.
     */
    public CategoriesViewModel(Application application) {
        super(application);
        categoriesRepository = CategoriesRepository.getInstance(application);
        categoriesRepository.getAll();
    }

    // Category Operations

    /**
     * Gets all categories.
     *
     * @return A LiveData object containing a list of all categories.
     */
    public LiveData<List<Category>> getCategories() {
        return categoriesRepository.getAll();
    }

    /**
     * Gets a specific category by its name.
     *
     * @param category The name of the category.
     * @return A LiveData object containing the category, or null if not found.
     */
    public LiveData<Category> getCategoryByName(String category) {
        return categoriesRepository.getCategoryByTitle(category);
    }

    /**
     * Creates a new category.
     *
     * @param title     The title of the new category.
     * @param promotion A boolean indicating whether the category is a promotion.
     * @return A LiveData object containing the result of the operation (success or failure).
     */
    public LiveData<Integer> createCategory(String title, boolean promotion) {
        return categoriesRepository.createCategory(title, promotion);
    }

    /**
     * Edits an existing category.
     *
     * @param title      The new title for the category.
     * @param promotion  A boolean indicating whether the category is a promotion.
     * @param categoryId The ID of the category to edit.
     * @return A LiveData object containing the result of the operation (success or failure).
     */
    public LiveData<Integer> editCategory(String title, boolean promotion, String categoryId) {
        return categoriesRepository.editCategory(title, promotion, categoryId);
    }

    /**
     * Deletes a category by its title.
     *
     * @param title The title of the category to delete.
     * @return A LiveData object containing the result of the operation (success or failure).
     */
    public LiveData<Integer> deleteCategoryByTitle(String title) {
        return categoriesRepository.deleteCategoryByTitle(title);
    }

    // Category with Movies Operations

    /**
     * Gets all promoted categories along with their associated movies.
     *
     * @return A LiveData object containing a map where the key is the category name
     * and the value is a list of movies in that category.
     */
    public LiveData<Map<String, List<Movie>>> getAllPromotedCategories() {
        return categoriesRepository.getCategoriesWithMovies();
    }

    /**
     * Gets categories associated with a specific movie.
     *
     * @param movieId The ID of the movie.
     * @return A LiveData object containing a list of categories associated with the movie.
     */
    public LiveData<List<Category>> getMovieCategories(long movieId) {
        return categoriesRepository.getMovieCategories(movieId);
    }

    /**
     * Gets movies in a specific category.
     *
     * @param categoryTitle The title of the category.
     * @return A LiveData object containing a list of movies in the specified category.
     */
    public LiveData<List<Movie>> getMoviesByCategoryTitle(String categoryTitle) {
        return categoriesRepository.getMoviesByCategoryTitle(categoryTitle);
    }

    /**
     * Gets all category names.
     *
     * @return A LiveData object containing a list of all category names.
     */
    public LiveData<List<String>> getCategoriesNames() {
        return categoriesRepository.getNames();
    }

    /**
     * Deletes a movie from a specific category.
     *
     * @param movieId       The ID of the movie to delete.
     * @param categoryTitle The title of the category from which the movie should be removed.
     * @return A LiveData object containing the result of the operation (success or failure).
     */
    public LiveData<Integer> deleteMovieFromCategory(long movieId, String categoryTitle) {
        return categoriesRepository.removeMovieFromCategory(movieId, categoryTitle);
    }
}
