package com.example.myapplication.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) for the Category entity.
 * Provides methods to interact with the categories table in the database.
 */
@Dao
public interface CategoryDao {

    /**
     * Retrieves all categories.
     * @return LiveData list of all categories.
     */
    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAll();

    /**
     * Inserts multiple categories. If a conflict occurs, the insertion is ignored.
     * @param categories The categories to insert.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Category... categories);

    /**
     * Deletes the specified categories.
     * @param categories The categories to delete.
     */
    @Delete
    void delete(Category... categories);

    /**
     * Updates the specified categories.
     * @param categories The categories to update.
     */
    @Update
    void update(Category... categories);

    /**
     * Retrieves categories associated with a given movie ID.
     * @param movieId The ID of the movie.
     * @return LiveData list of categories linked to the movie.
     */
    @Query("SELECT * FROM categories INNER JOIN ref ON categories.categoryId = ref.categoryId WHERE ref.movieId = :movieId")
    LiveData<List<Category>> getMovieCategories(long movieId);

    /**
     * Retrieves the titles of all categories.
     * @return LiveData list of category titles.
     */
    @Query("SELECT title FROM categories")
    LiveData<List<String>> getNames();

    /**
     * Checks if a category with the given title exists.
     * @param title The category title.
     * @return True if the category exists, false otherwise.
     */
    @Query("SELECT EXISTS (SELECT 1 FROM categories WHERE title = :title)")
    boolean categoryExists(String title);

    /**
     * Checks if a category with the given ID exists.
     * @param categoryId The category ID.
     * @return True if the category exists, false otherwise.
     */
    @Query("SELECT EXISTS (SELECT 1 FROM categories WHERE categoryId = :categoryId)")
    boolean categoryExistsById(String categoryId);

    /**
     * Retrieves categories by a list of titles.
     * @param titles The list of category titles.
     * @return List of matching Category objects.
     */
    @Query("SELECT * FROM categories WHERE title IN (:titles)")
    List<Category> getCategoriesByTitle(List<String> titles);

    /**
     * Retrieves a single category by title.
     * @param title The category title.
     * @return The matching Category object.
     */
    @Query("SELECT * FROM categories WHERE title = :title")
    Category getCategoryByTitle(String title);

    /**
     * Retrieves movies belonging to a category with the given title.
     * @param title The category title.
     * @return LiveData list of movies in the category.
     */
    @Query("SELECT movies.* FROM movies " +
            "INNER JOIN ref ON movies.movieId = ref.movieId " +
            "INNER JOIN categories ON ref.categoryId = categories.categoryId " +
            "WHERE categories.title = :title")
    LiveData<List<Movie>> getMoviesByCategoryTitle(String title);

    /**
     * Removes categories that are not in the provided list of IDs.
     * @param categoriesIds List of category IDs to keep.
     */
    @Query("DELETE FROM categories WHERE categoryId NOT IN (:categoriesIds)")
    void removeOldData(List<String> categoriesIds);

    /**
     * Retrieves a category by its ID.
     * @param categoryId The category ID.
     * @return The matching Category object.
     */
    @Query("SELECT * FROM categories WHERE categoryId = :categoryId")
    Category getCategory(String categoryId);
}
