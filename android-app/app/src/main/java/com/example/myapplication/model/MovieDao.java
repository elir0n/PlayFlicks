package com.example.myapplication.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MovieDao {

    /**
     * Retrieves all movies associated with a specific category.
     *
     * @param categoryId The ID of the category.
     * @return LiveData containing a list of movies.
     */
    @Transaction
    @Query("SELECT * FROM movies INNER JOIN ref ON movies.movieId = ref.movieId WHERE ref.categoryId = :categoryId")
    LiveData<List<Movie>> getMoviesOfCategory(String categoryId);

    /**
     * Inserts multiple movies into the database, ignoring conflicts.
     *
     * @param movies The movies to insert.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Movie... movies);

    /**
     * Inserts a movie-category relationship into the cross-reference table, ignoring conflicts.
     *
     * @param crossRef The movie-category cross-reference.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMovieCategoryCrossRef(MovieCategoryCrossRef crossRef);

    /**
     * Retrieves a movie by its unique ID.
     *
     * @param movieId The ID of the movie.
     * @return The corresponding movie.
     */
    @Query("SELECT * FROM movies WHERE movieId = :movieId")
    Movie getMovieById(long movieId);

    /**
     * Deletes a specific movie-category relationship.
     *
     * @param movieId    The movie's ID.
     * @param categoryId The category's ID.
     */
    @Query("DELETE FROM ref WHERE movieId = :movieId AND categoryId = :categoryId")
    void deleteRef(long movieId, String categoryId);

    /**
     * Checks if a movie exists by its ID.
     *
     * @param movieId The ID of the movie.
     * @return True if the movie exists, otherwise false.
     */
    @Query("SELECT EXISTS (SELECT 1 FROM movies WHERE movieId = :movieId)")
    boolean movieExistsById(long movieId);

    /**
     * Retrieves a movie by its title.
     *
     * @param title The title of the movie.
     * @return The corresponding movie.
     */
    @Query("SELECT * FROM movies WHERE title = :title")
    Movie getMovieByTitle(String title);

    /**
     * Removes movies from the database that are not in the provided list of movie IDs.
     *
     * @param moviesIds The list of movie IDs to retain.
     */
    @Query("DELETE FROM movies WHERE movieId NOT IN (:moviesIds)")
    void removeOldData(List<Long> moviesIds);

    /**
     * Retrieves a movie by its unique ID as LiveData.
     *
     * @param movieId The ID of the movie.
     * @return LiveData containing the movie.
     */
    @Query("SELECT * FROM movies WHERE movieId = :movieId")
    LiveData<Movie> getMovieByMovieId(long movieId);

    /**
     * Updates multiple movie entries in the database.
     *
     * @param movies The movies to update.
     */
    @Update
    void updateAll(Movie... movies);

    /**
     * Deletes multiple movies from the database.
     *
     * @param movies The movies to delete.
     */
    @Delete
    void delete(Movie... movies);
}
